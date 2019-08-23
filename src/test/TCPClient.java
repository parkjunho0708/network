package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPClient {
	private static String SERVER_IP = "192.168.246.1";
	private static int SERVER_PORT = 6000;

	public static void main(String[] args) {
		Socket socket = null;

		try {
			// 1. 소켓생성
			socket = new Socket();

			// 2. 서버연결
			InetSocketAddress inetSocketAddress = new InetSocketAddress(SERVER_IP, SERVER_PORT);
			socket.connect(inetSocketAddress); // Blocking

			System.out.println("[TCP Client] connected");
			
			// 3. IOStream 받아오기
			InputStream is = socket.getInputStream(); // socket.close() 를 하면 그 안에 있는 input & output stream은 자동으로 닫힘.
			OutputStream os = socket.getOutputStream();
			
			// 4. 쓰기
			//String data = "Hello World\n";
			String data = "안녕하세요\n";
			os.write(data.getBytes("UTF-8"));
			
			// 5. 읽기
			byte[] buffer = new byte[256];
			// 읽어내는 buffer의 양이 항상 일정하지 않을 수 있기 때문에 buffer의 양을 count해야 함.
			int readByteCount = is.read(buffer); // Blocking
			if (readByteCount == -1) { // -1이면 server가 연결을 끊은 것이기 때문에 break로 연결을 빠져나가야 함.
				// 정상종료 : remote socket이 close()
				// 메소드를 통해서 정상적으로 소켓을 닫은 경우
				System.out.println("[TCP Client] closed by server");
				return;
			}
			
			data = new String(buffer, 0 ,readByteCount, "UTF-8");
			System.out.println("[TCP Client] received : " + data);
			
		} catch (IOException e) {

		} finally {
			try {
				if (socket != null && socket.isClosed() == false) {
					socket.close();
				}
			} catch (IOException e) {
			}
		}
	}
}
