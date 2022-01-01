import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Server{
	
	private static Path curDir;
	
	public static void main(String[] args) throws Exception{
		ServerSocket ss = new ServerSocket(12345);
		while(true){
			Socket s = ss.accept();
			curDir = Paths.get(".");
			
			BufferedReader sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			String cmd = "";
			
			while(!cmd.equals("exit")){
				printDir(s);
				cmd = sin.readLine();
				
				printString(s, handleCommand(cmd));
			}
			
			s.close();
		}
	}
	
	private static void printDir(Socket s) throws Exception{
		String dir = System.getProperty("user.name") + "@";
		dir += curDir.toFile().getCanonicalPath();
		printString(s, dir);
	}
	
	private static void printString(Socket s, String str) throws Exception{
		s.getOutputStream().write((str + "\n").getBytes());
	}
	
	private static String handleCommand(String command) throws Exception{
		String[] cmd = command.split(" ");
		
		if(cmd.length == 0){
			return "";
		}
		
		if(cmd[0].equals("prop") && cmd.length == 2){
			return System.getProperty(cmd[1]);
		}
		
		if(cmd[0].equals("cd") && cmd.length == 2){
			Path temp = curDir.resolve(Paths.get(cmd[1])).normalize();
			if(!temp.toFile().exists()){
				return "Unknown directory!";
			}
			
			curDir = temp;
			
			return "";
		}
		
		if(cmd[0].equals("ls") && cmd.length == 1){
			return ls();
		}
		
		if(cmd[0].equals("mkdir") && cmd.length == 2){
			mkdir(cmd[1]);
			return "";
		}
		
		if(cmd[0].equals("rm") && cmd.length == 2){
			rm(cmd[1]);
			return "";
		}
		
		if(cmd[0].equals("echo") && cmd.length >= 3){
			String[] content = new String[cmd.length - 2];
			System.arraycopy(cmd, 2, content, 0, content.length);
			echo(cmd[1], content);
			return "";
		}
		
		if(cmd[0].equals("echoa") && cmd.length >= 3){
			String[] content = new String[cmd.length - 2];
			System.arraycopy(cmd, 2, content, 0, content.length);
			echoa(cmd[1], content);
			return "";
		}
		
		if(cmd[0].equals("exit") && cmd.length == 1){
			return "";
		}
		
		if((cmd[0].equals("help") || cmd[0].equals("?")) && cmd.length == 1){
			return "Commands:\0\tprop <property-name>\0\tcd <directory>\0\tls\0\tmkdir <name>" + 
					"\0\trm <file or directory>\0\techo <file> <word1> [word2] ..." + 
					"\0\techoa <file> <word1> [word2] ...\0\thelp or ?\0\texit";
		}
		
		return "Unknown command: " + command + "\0Use help or ? to get help!";
	}
	
	private static String ls(){
		File[] files = curDir.toFile().listFiles();
		StringBuilder list = new StringBuilder();
		for(File f: files){
			char type = f.isDirectory() ? 'D' : 'F';
			list.append(type + ": ");
			list.append(f.getName());
			list.append("\0");
		}
		return list.toString();
	}
	
	private static void mkdir(String name) throws Exception{
		File f = curDir.resolve(name).toFile();
		f.mkdir();
	}
	
	private static void rm(String name) throws Exception{
		File f = curDir.resolve(name).toFile();
		f.delete();
	}
	
	private static void echo(String name, String[] content) throws Exception{
		File f = curDir.resolve(name).toFile();
		if(!f.exists()){
			f.createNewFile();
		}
		PrintWriter pw = new PrintWriter(f);
		
		for(int i = 0; i < content.length; i++){
			if(i > 0){
				pw.print(" ");
			}
			String s = content[i];
			pw.print(s.replace("\\t", "\t").replace("\\n", "\n"));
		}
		
		pw.close();
	}
	
	private static void echoa(String name, String[] content) throws Exception{
		File f = curDir.resolve(name).toFile();
		if(!f.exists()){
			f.createNewFile();
		}
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
		
		for(int i = 0; i < content.length; i++){
			if(i > 0){
				pw.print(" ");
			}
			String s = content[i];
			pw.print(s.replace("\\t", "\t").replace("\\n", "\n"));
		}
		
		pw.close();
	}
	
}