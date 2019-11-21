package com.xycode.DH;
/* 非对称密钥交换
 * Diffie-Hellman算法
 * 将其应用在Socket编程中，安全地生成密钥后，应用简单的异或加密来验证一下
 * 
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DHServer {
	private static ExecutorService es=Executors.newCachedThreadPool();
	//ServerSocket可能会管理多个Client连接，故采用Map存储密钥信息
	private static Map<Socket,Integer> client_DH_KEY=new HashMap<>();//中间结果
	private static Map<Socket,Integer> final_DH_KEY=new HashMap<>();//最终的密钥
	private static int private_key;
	static class handle_msg implements Runnable{
		Socket client_socket;
		public handle_msg(Socket client_socket) {
			this.client_socket = client_socket;
		}
		
		@Override
		public void run() {
			BufferedReader is=null;
			PrintWriter os=null;
			try {
				is=new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
				os=new PrintWriter(client_socket.getOutputStream(),true);//autoFlush=true
				String inputLine=null;
				while((inputLine=is.readLine())!=null) {
					String[] s=inputLine.split(" : ");
					if(s[0].equals(new String("Client_DH_KEY"))) {
						client_DH_KEY.put(client_socket, Integer.parseInt(s[1]));
						final_DH_KEY.put(client_socket,Constant.get(Constant.get(private_key),Integer.parseInt(s[1])));
						os.println("Server_DH_KEY : "+Constant.get(private_key));
					}else if(s[0].equals(new String("Client_INFO"))){
						int info=Integer.parseInt(s[1])^final_DH_KEY.get(client_socket);
						System.out.println("DHServer recv : "+info);
					}else {
						System.out.println("Format Error!");
						break;
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				try{
					if(is!=null) is.close();
					if(os!=null) os.close();
					if(client_socket!=null) client_socket.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}

	public static void main(String[] args) {
		ServerSocket echoServer=null;
		Socket clientSocket=null;
		try {
			echoServer=new ServerSocket(8888);
		}catch (Exception e) {
			e.printStackTrace();
		}
		private_key=123;
		while(true) {
			try {
				clientSocket=echoServer.accept();//监听echoServer的8888端口
				System.out.println(clientSocket.getRemoteSocketAddress()+" connected.");
				es.execute(new handle_msg(clientSocket));
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
