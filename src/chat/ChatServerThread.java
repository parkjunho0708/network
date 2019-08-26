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

public class ChatServerThread extends Thread {
	private Socket socket; // Blocking <- 찌르면 실행됨
	private String nickname;
	List<Writer> listWriters = null;

	public ChatServerThread(Socket socket, List<Writer> listWriters) {
		this.socket = socket;
		this.listWriters = listWriters;
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

			while (true) {
				// 5. 데이터 읽기(수신)
				// String data = br.readLine();
				String request = bufferedReader.readLine();

				if (request == null) {
					ChatServer.log(" 클라이언트로 부터 연결 끊김");
					doQuit(printWriter);
					break;
				}

				String[] tokens = request.split(":");
				
				if ("join".equals(tokens[0])) {
					doJoin(tokens[1], printWriter);
				} else if ("message".equals(tokens[0])) {
					doMessage(tokens[1]);
				} else if ("quit".equals(tokens[0])) {
					doQuit(printWriter);
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

	public void doJoin(String nickName, PrintWriter writer) {
		this.nickname = nickName;

		String data = nickName + "님이 참여하였습니다.";
		
		/* writer pool에 저장 */
		addWriter(writer);
		broadcast(data);

		// ack
		writer.println("join:ok");
		writer.flush();

	}

	private void addWriter(PrintWriter writer) {
		synchronized (listWriters) {
			listWriters.add(writer);
		}
	}

	private void broadcast(String data) {
		synchronized (listWriters) {
			for (Writer writer : listWriters) {
				PrintWriter printWriter = (PrintWriter) writer;
				printWriter.println(data);
				System.out.println(data);
				printWriter.flush();
			}
		}
	}

	private void doMessage(String message) {
		/* 잘 구현 해 보기 */
		broadcast(nickname + " : " + message);
	}

	private void doQuit(PrintWriter writer) {
		removeWriter(writer);
		String data = nickname + "님이 퇴장 하였습니다.";
		broadcast(data);
	}

	private void removeWriter(PrintWriter writer) {
		/* 잘 구현 해보기 */
		synchronized (listWriters) {
			listWriters.remove(writer);
		}
	}

}
