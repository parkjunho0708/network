package util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class NSLookup {
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String hostName = null;

		while (true) {
			System.out.print("> ");
			hostName = scanner.nextLine();
			if(hostName.equals("exit")) {
				break;
			}
			try {
				InetAddress[] inetAddresses = InetAddress.getAllByName(hostName); // 이름으로 inetAddress를 가지고 오겠다.

				for (InetAddress inetAddress : inetAddresses) {
					System.out.println(hostName + " : " + inetAddress.getHostAddress());
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}
}
