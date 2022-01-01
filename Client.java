import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client{
	
	public static void main(String[] args) throws Exception {
		Socket s = new Socket(args[0], 12345);
		
		BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
		String cmd = "";
		
		while(!cmd.equals("exit\n")){
			System.out.print(sin.readLine() + "> ");
			cmd = sysin.readLine() + "\n";
			s.getOutputStream().write(cmd.getBytes());
			s.getOutputStream().flush();
			
			System.out.println(sin.readLine().replace("\0", "\n"));
			
		}
		
		s.close();
	}
	
}