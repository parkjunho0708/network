package thread;

public class MultithreadEx01 {
	// 메인 자체도 하나의 쓰레드
	// Java는 메인이 먼저 끝났다고 해서 프로그램이 종료되는 것은 아님.
	// 
	public static void main(String[] args) {
//		for(int i = 0; i < 10; i++) {
//			System.out.print(i);
//		}
		
		Thread digitThread = new DigitThread();
		digitThread.start();
		
		for(char c = 'a'; c <= 'z'; c++) {
			System.out.print(c);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
