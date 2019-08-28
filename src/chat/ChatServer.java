package chat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	private static final int PORT = 5051;

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		List<User> users=new ArrayList<User>();
		
		try {
			// 1. 서버 소겟 생성
			serverSocket = new ServerSocket();

			// 2. 바인딩
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			System.out.println(hostAddress);
			serverSocket.bind(new InetSocketAddress(hostAddress, PORT));
			log("연결 기다림 " + hostAddress + ":" + PORT);

			// 3. 요청 대기
			while (true) {
				Socket socket = serverSocket.accept();
				new ChatServerThread(socket, users).start();
			}
		} catch (IOException e) { // 이 catch문은 server socket의 Exception을 위해 존재함.
			e.printStackTrace();
		} finally {
			// 8. Server Socket 자원정리
			try {
				if (serverSocket != null && serverSocket.isClosed() == false) {
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void log(String log) {
		System.out.println("[Chat Server#" + Thread.currentThread().getId() + "] " + log);
	}
}
