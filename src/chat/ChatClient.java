package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatClient {
	private static String SERVER_IP = "192.168.246.1";
	private static int SERVER_PORT = 5000;

	public static void main(String[] args) {
		Scanner scanner = null;
		Socket socket = null;
		
		try {
			// 1. Scanner 생성(표준입력, 키보드 연결)
			scanner = new Scanner(System.in);

			// 2. 소켓생성
			socket = new Socket();

			// 3. 서버연결
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT)); // Blocking
			log("connected");

			// 3. IOStream 생성 reader/writer 생성
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

			// 5. join 프로토콜
			System.out.print("닉네임>>");
			String nickname = scanner.nextLine();
			pw.println("join:" + nickname);
			pw.flush();

			// 6. ChatClientReceiveThread 시작
			new ChatClientThread(socket, br).start();

			while (true) {
				System.out.print(">>");
				String input = scanner.nextLine();
				
				if ("quit".equals(input)) {
					// 8. quit 프로토콜 처리
					pw.println("quit:");
					break;
				} else {
					// 9. 메시지 처리
					pw.println("message:" + input);
				}
			}
		} catch (IOException e) {

		} finally {
			try {
				if (scanner != null) {
					scanner.close();
				}
				if (socket != null && socket.isClosed() == false) {
					socket.close();
				}
			} catch (IOException e) {
			}
		}
	}

	private static void log(String log) {
		System.out.println("[Chat Client] " + log);
	}
}
