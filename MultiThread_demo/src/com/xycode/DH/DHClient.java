package com.xycode.DH;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DHClient implements Runnable{
	private static ExecutorService es=Executors.newCachedThreadPool();
	private int private_key;
	private int server_DH_KEY;//中间结果
	private int final_DH_KEY;//最终的密钥
	
	public DHClient(int private_key) {
		this.private_key = private_key;
	}

	@Override
	public void run() {
		Socket client=null;
		PrintWriter writer=null;
		BufferedReader reader=null;
		try {
			client=new Socket();
			//client.bind(new InetSocketAddress("localhost",12345));//静态绑定,固定端口
			client.connect(new InetSocketAddress("localhost",8888));
			//连接到Server监听的ip与port,client所用的port是随机分配的,除非之前bind(port)
			writer=new PrintWriter(client.getOutputStream(),true);
			writer.println("Client_DH_KEY : "+Constant.get(private_key));
			reader=new BufferedReader(new InputStreamReader(client.getInputStream()));
			String inputLine=null;
			while((inputLine=reader.readLine())!=null) {
				String[] s=inputLine.split(" : ");
				if(s[0].equals(new String("Server_DH_KEY"))) {	
					server_DH_KEY=Integer.parseInt(s[1]);
					final_DH_KEY=Constant.get(Constant.get(private_key),server_DH_KEY);
				}
				break;
			}
			int info=Constant.r.nextInt(1000);
			writer.println("Client_INFO : "+(info^final_DH_KEY));
			System.out.println("Client send : "+info);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(writer!=null) writer.close();
				if(reader!=null) reader.close();
				if(client!=null) client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		for(int i=0;i<10;++i) {//10个ClientSocket连接
			Thread.sleep(500);
			es.execute(new DHClient(Constant.r.nextInt(1000)));
		}
	}

}
