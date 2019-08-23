package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

// localhost : 127.0.0.1 (loopback) 자기 자신을 가리키는 IP Address
// Server 실행시킨 후, Ctrl + F11 (재실행) 금지!!!

// XShell5를 이용해서 통신!!
/*
 * <telnet에서 eclipse의 socket server close 하는 방법>
 * 
 * Connecting to 192.168.246.1:6000... Connection established. To escape to
 * local shell, press 'Ctrl+Alt+]'.
 * 
 * Escape to local shell...... To return to remote host, enter "exit". To close
 * connection, enter "disconnect".
 * 
 * Type `help' to learn how to use Xshell prompt. [c:\~]$ disconnect
 */

public class TCPServer {
	private static final int PORT = 6000;

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		// 1. 서버소켓 생성
		try {
			serverSocket = new ServerSocket();

			// 2. Binding : Socket에 SocketAddress(IPAddress + Port) 바인딩한다.
			InetAddress inetAddress = InetAddress.getLocalHost();
			String localhostAddress = inetAddress.getHostAddress();

			// InetSocketAddress.InetSocketAddress(String hostname, int port)
			InetSocketAddress inetSocketAddress = new InetSocketAddress(localhostAddress, PORT);
			serverSocket.bind(inetSocketAddress);
			System.out.println("[TCP Server] binding " + inetAddress.getHostAddress() + " : " + PORT);

			// 3. accept : 클라이언트로 부터 연결요청(Connect)을 기다린다.
			Socket socket = serverSocket.accept(); // Blocking <- 찌르면 실행됨.
			InetSocketAddress inetRemoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();

			String remoteHostAddress = inetRemoteSocketAddress.getAddress().getHostAddress();
			int remoteHostPort = inetRemoteSocketAddress.getPort();
			System.out.println("[TCP Server] connected from client[" + remoteHostAddress + " : " + remoteHostPort + "]");

			try {
				// 4. IOStream 받아오기
				InputStream is = socket.getInputStream(); // socket.close() 를 하면 그 안에 있는 input & output stream은 자동으로 닫힘.
				OutputStream os = socket.getOutputStream();

				while (true) {
					
					// 5. 데이터 읽기
					byte[] buffer = new byte[256];
					// 읽어내는 buffer의 양이 항상 일정하지 않을 수 있기 때문에 buffer의 양을 count해야 함.
					int readByteCount = is.read(buffer); // Blocking
					if (readByteCount == -1) { // -1이면 client가 연결을 끊은 것이기 때문에 break로 연결을 빠져나가야 함.
						// 정상종료 : remote socket이 close()
						// 메소드를 통해서 정상적으로 소켓을 닫은 경우
						System.out.println("[TCP Server] closed by client");
						break;
					}
					
					String data = new String(buffer, 0, readByteCount, "UTF-8");
					System.out.println("[TCP Server] received : " + data);
					
					// 6. 데이터 쓰기
					os.write(data.getBytes("UTF-8"));
				}
			} catch (SocketException e) { // 통신과 관련된 socket에 대한 처리
				// 비정상적으로 종료됨. (TCP의 4way hand shake 방법이 아닌 방법으로 종료됨.)
				System.out.println("[TCP Server] abnormal closed by client");
			} catch (IOException e) { // 통신과 관련된 socket에 대한 처리
				e.printStackTrace();
			} finally {
				// 7. Socket 자원정리
				if (serverSocket != null && serverSocket.isClosed() == false) {
					socket.close();
				}
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
}


