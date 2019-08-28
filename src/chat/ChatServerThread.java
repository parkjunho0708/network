package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class ChatServerThread extends Thread {
	private Socket socket; // Blocking <- 찌르면 실행됨
	private User user;
	List<User> users = null;

	public ChatServerThread(Socket socket, List<User> users) {
		this.socket = socket;
		this.users = users;
	}

	@Override
	public void run() {
		InetSocketAddress inetRemoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
		ChatServer.log(" connected from client[" + inetRemoteSocketAddress.getAddress().getHostAddress() + " : "
				+ inetRemoteSocketAddress.getPort() + "]");

		try {
			// 4. I/OStream 생성
			// socket.close() 를 하면 그 안에 있는 input & output stream은 자동으로 닫힘.
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

			user = new User(socket, printWriter, bufferedReader);

			while (true) {
				// 5. 데이터 읽기(수신)
				String request = user.getBufferedReader().readLine();

				if (request == null) {
					ChatServer.log(" 클라이언트로 부터 연결 끊김");
					doQuit(user);
					break;
				}

				String[] tokens = request.split(":");

				if ("join".equals(tokens[0])) {
					doJoin(tokens[1], user.getPrintWriter());
				} else if ("message".equals(tokens[0])) {
					doMessage(tokens[1]);
				} else if ("quit".equals(tokens[0])) {
					doQuit(user);
				} else if ("w".equals(tokens[0])) { // 귓속말
					// whisper:귓말할 사람 이름
					doWhisper(tokens[1], tokens[2], user.getPrintWriter());
				} else {
					ChatServer.log("에러:알수 없는 요청(" + tokens[0] + ")");
				}
			}
		} catch (SocketException e) { // 통신과 관련된 socket에 대한 처리
			// 비정상적으로 종료됨. (TCP의 4way hand shake 방법이 아닌 방법으로 종료됨.)
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

	public void doJoin(String username, PrintWriter writer) {
		this.user.setUsername(username);

		String data = user.getUsername() + "님이 참여하였습니다.";

		/* writer pool에 저장 */
		addUser(user);
		broadcast(data);

		// ack
		writer.println("join:ok");
		writer.flush();

	}

	// 접속한 사용자를 리스트 형식으로 차례대로 추가
	private void addUser(User user) {
		synchronized (users) {
			users.add(user);
		}
	}

	// 특정사용자의 메시지를 전체 채팅창에 출력
	private void broadcast(String data) {
		synchronized (users) {
			for (User user : users) {
				PrintWriter printWriter = (PrintWriter) user.getPrintWriter();
				printWriter.println(data);
				System.out.println(data);
				printWriter.flush();
			}
		}
	}

	// 사용자 이름과 메시지를 받아서 전체 채팅 메소드로 전달
	private void doMessage(String message) {
		/* 잘 구현 해 보기 */
		broadcast(user.getUsername() + " : " + message);
	}
	
	// 귓속말 구현
	private void doWhisper(String whipName, String message, PrintWriter printWriter) {
			String sendUser = user.getUsername(); // 보낸 사람의 이름
			User receiveuser = null;

			for (int i = 0; i < users.size(); i++) {
				if (whipName.equals(users.get(i).getUsername())) {
					receiveuser = users.get(i);
					break;
				}
			}
			
			if(receiveuser == null) {
				user.getPrintWriter().println("귓속말 사용자가 존재하지 않습니다.");
				return;
			}

			receiveuser.getPrintWriter().println("(귓속말)" + sendUser + " : " + message);
			receiveuser.getPrintWriter().flush();
	}

	// User 퇴장
	private void doQuit(User user) {
		removeWriter(user);
		String data = user.getUsername() + "님이 퇴장 하였습니다.";
		broadcast(data);
	}

	// User List에서 User 삭제
	private void removeWriter(User user) {
		/* 잘 구현 해보기 */
		synchronized (users) {
			users.remove(user);
		}
	}

}
