package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ChatClientThread extends Thread {
	private Socket socket;
	private BufferedReader bufferedReader;

	public ChatClientThread(Socket socket, BufferedReader bufferedReader) {
		this.socket = socket;
		this.bufferedReader = bufferedReader;
	}

	@Override
	public void run() {
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

			while (true) {
				// 5. 데이터 읽기(수신)
				// String data = br.readLine();
				String request = bufferedReader.readLine();
				if("join:ok".equals(request)) {
					continue;
				}
				System.out.println(request);
			}
		} catch (SocketException e) { // 통신과 관련된 socket에 대한 처리
			ChatServer.log("abnormal closed by client");
		} catch (IOException e) { // 통신과 관련된 socket에 대한 처리
			e.printStackTrace();
		} finally {
			// 7. Socket 자원정리
			if (socket != null && socket.isClosed() == false) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
