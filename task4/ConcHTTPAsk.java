import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ConcHTTPAsk {
    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(args[0]);
            ServerSocket serverSocket = new ServerSocket(port);
            
            // continuously listen for incoming connections
            while (true) {
                // accept the incoming client connection
                Socket clientSocket = serverSocket.accept();
                MyRunnable runnable = new MyRunnable(clientSocket);
                Thread thread = new Thread(runnable);
                // calls run from runnable
                thread.start();
            }
        } catch (Exception e) {
            System.out.println("From main " + e);
        }
    }

}
