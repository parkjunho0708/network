package thread;

public class MultithreadEx02 {
	// 메인 자체도 하나의 쓰레드
	// Java는 메인이 먼저 끝났다고 해서 프로그램이 종료되는 것은 아님.
	public static void main(String[] args) {
		Thread thread1 = new DigitThread();
		Thread thread2 = new AlphabetThread();

		thread1.start();
		thread2.start();
	}
}
