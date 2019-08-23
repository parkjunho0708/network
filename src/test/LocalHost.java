package test;

import java.net.InetAddress;
import java.net.UnknownHostException;

// Windows 시스템을 localhost라고 부름
// InetSocketAddress = [InetAddress(IP Address)] + Port
// InetAddress는 IP Address를 다루는 클래스이다.
public class LocalHost {
	public static void main(String[] args) {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			String hostName = inetAddress.getHostName();
			String hostAddress = inetAddress.getHostAddress(); // inet 주소는 4byte

			System.out.println(hostName);
			System.out.println(hostAddress); // 컴퓨터의 이름

			byte[] ipAddresses = inetAddress.getAddress();
			for (byte ipAddress : ipAddresses) {
				System.out.print(ipAddress & 0x000000ff);
				System.out.print(".");
			}
		} catch (UnknownHostException e) {
			System.out.println("error : " + e);
		}
	}
}
