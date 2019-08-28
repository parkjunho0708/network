package chat;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class User {
	private Socket socket;
	private String username;
	private PrintWriter printWriter;
	private BufferedReader bufferedReader;

	public User(Socket socket, PrintWriter printWriter, BufferedReader bufferedReader) {
		this.socket = socket;
		this.printWriter = printWriter;
		this.bufferedReader = bufferedReader;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public PrintWriter getPrintWriter() {
		return printWriter;
	}

	public void setPrintWriter(PrintWriter printWriter) {
		this.printWriter = printWriter;
	}

	public BufferedReader getBufferedReader() {
		return bufferedReader;
	}

	public void setBufferedReader(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
	}

}
