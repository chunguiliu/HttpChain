package com.gcl.webimpl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.gcl.dao.AfterRequestHandler;
import com.gcl.dao.BeforeRequestHandler;
import com.gcl.exception.ReRequestException;
import com.gcl.exception.RollBackException;
import com.gcl.exception.StopChainException;
import com.gcl.http.BasicHttpRequest;
import com.gcl.http.HttpRequestChain;

public class ExportCSV {

	public static void write(FileChannel channel, ByteBuffer buf, byte[] bytes){
		buf.clear();
		buf.put(bytes);
		buf.flip();
		while (buf.hasRemaining()) {
			try {
				channel.write(buf);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private static byte[] arraycopy(byte[] src, int from, int offset){
		byte[] tmp = new byte[offset];
		System.arraycopy(src, from, tmp, 0, offset);
		return tmp;
	}
	public static void write(FileChannel channel, String data) {
		int len = 12;
		byte[] bytes = data.getBytes();
		int total = bytes.length;
		ByteBuffer buf = ByteBuffer.allocate(len);
		int from = 0;
		do{
//			byte[] tmp = new byte[offset];
//			System.arraycopy(bytes, from, tmp, 0, offset);
			int offset = Math.min(len, total - from);
			byte[] tmp = arraycopy(bytes, from, offset);
			from += offset;
			write(channel, buf, tmp);
		}while(from < total);
	}
	public static void main(String[] args) throws ParseException, IOException {
		ExportCSV exporter = new ExportCSV();
		HttpRequestChain chain = new HttpRequestChain();
		chain.addRequest(exporter.getLoginReq());
		chain.addRequest(exporter.getAdminphp());
		chain.addRequest(exporter.getQuestion_listphp());
		
		String filename = "D:\\export\\"+System.currentTimeMillis()+".csv";
		File file = new File(filename);
		file.createNewFile();
		RandomAccessFile aFile = new RandomAccessFile(file, "rw");
		final FileChannel inChannel = aFile.getChannel();
		write(inChannel, "手机号,设备号,提问时间,提问,是否处理,处理人,处理时间\r\n");
//			inChannel.close();
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		final Date endDate = sdf.parse("2015-11-01 00:00:00");
//		final Date endDate = sdf.parse("2015-11-28 16:31:09");
		AfterRequestHandler handler = new AfterRequestHandler(){
			@Override
			public void afterHandle(Object response,
					BasicHttpRequest httpRequest) throws RollBackException,
					StopChainException, ReRequestException {
				JSONObject jsonRes = JSONObject.fromObject(response);
				System.out.println(jsonRes);
				JSONArray list = JSONArray.fromObject(jsonRes.get("rows"));
				Date minDate = null;
				for(Object obj : list){
					JSONObject json = (JSONObject) obj;
					write(inChannel, json.getString("cellphone")
							.concat(",").concat(json.getString("device_number"))
							.concat(",").concat(json.getString("create_date"))
							.concat(",").concat(json.getString("question"))
							.concat(",").concat("".equals(json.getString("answer_date"))?"否":"是")
							.concat(",").concat(json.getString("answer_user"))
							.concat(",").concat(json.getString("answer_date"))
							.concat("\r\n"));
						try {
						if(minDate == null || minDate.getTime() > sdf.parse(json.getString("create_date")).getTime())
							minDate = sdf.parse(json.getString("create_date"));
						} catch (ParseException e) {
							e.printStackTrace();
						}
				}
				if(endDate.getTime() > minDate.getTime()){
					throw new StopChainException("下载完成");
				}else{
					int nextPage = Integer.valueOf(httpRequest.getAttribute("page").toString())+1;
					httpRequest.addAttribute("page", String.valueOf(nextPage));
					throw new ReRequestException("开始第"+nextPage+"页数据");
				}
			}
			
		};
		BasicHttpRequest exportRequest = exporter.getQuestionlist();
		exportRequest.setAfterHandler(handler);

		chain.addRequest(exportRequest);
		try {
			chain.doRequest();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		inChannel.close();
	}

	
	public BasicHttpRequest getLoginReq(){
		BasicHttpRequest request = new BasicHttpRequest(new AfterRequestHandler() {
			@Override
			public void afterHandle(Object response, BasicHttpRequest httpRequest)
					throws RollBackException, StopChainException, ReRequestException {
				System.out.println(response);
				if(response.toString().equals("1")){
					System.out.println("登陆成功！");
				}else{
					System.out.println("登陆失败");
				}
			}
		});
		request.setMethod("POST");
		request.setRequestUrl("http://kb.e-dog.cn/operate.php");
		request.addParameter("op", "login");
		request.addParameter("password", "123456");
		request.addParameter("timeStamp", String.valueOf(System.currentTimeMillis()));
		request.addParameter("username", "李爽玉");
		return request;
		
	}
	
	public BasicHttpRequest getAdminphp(){
		BasicHttpRequest request = new BasicHttpRequest(new AfterRequestHandler() {
			@Override
			public void afterHandle(Object response, BasicHttpRequest httpRequest)
					throws RollBackException, StopChainException, ReRequestException {
				System.out.println(response);
			}
		});
		request.setRequestUrl("http://kb.e-dog.cn/admin.php");
		return request;
	}
	
	public BasicHttpRequest getQuestion_listphp(){
		BasicHttpRequest request = new BasicHttpRequest(new AfterRequestHandler() {
			@Override
			public void afterHandle(Object response, BasicHttpRequest httpRequest)
					throws RollBackException, StopChainException, ReRequestException {
				System.out.println(response);
			}
		});
		request.setRequestUrl("http://kb.e-dog.cn/question_list.php");
		return request;
	}
	
	public BasicHttpRequest getQuestionlist(){
		BasicHttpRequest request = new BasicHttpRequest(new BeforeRequestHandler() {
			@Override
			public void beforeHandle(Object lastResponse, BasicHttpRequest httpRequest)
					throws RollBackException, StopChainException, ReRequestException {
				httpRequest.refresh();
				String page = (String) (httpRequest.getAttribute("page")==null?"1":httpRequest.getAttribute("page"));
				httpRequest.addAttribute("page", page);
				httpRequest.addParameter("page", page);
				httpRequest.addParameter("rows", "200");
				httpRequest.addParameter("sort", "id");
				httpRequest.addParameter("order", "desc");
			}
		});
		request.setMethod("POST");
		request.setRequestUrl("http://kb.e-dog.cn/operate.php?op=question_list");
		return request;
	}
}
