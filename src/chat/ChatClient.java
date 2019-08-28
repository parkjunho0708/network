package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
	private static String SERVER_IP = "127.0.0.1";
	private static int SERVER_PORT = 5051;

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
				} else if ("whisper".equals(input)) {
					// 귓속말을 하기 위해서는 "whisper"를 먼저 입력한다
					System.out.println("귓속말을 하기 위해 사용자 아이디 및 메시지를 입력해주세요.");
					System.out.println("w: UserID: message");
					System.out.print(">>");
					input = scanner.nextLine();
					// w: 사용자 아이디 : 메시지 입력
					// 하게되면 지정된 사용자에게 메시지가 전달되게 된다.
					
					// 사용자에게 다시 귓말을 보내고 싶으면, "whisper"를 입력하고,
					// w: 사용자 아이디 : 메시지 입력 을 다시 입력하면 된다.
					pw.println(input);
				} else {
					// 9. 메시지 처리
					pw.println("message:" + input);
				}
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
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
