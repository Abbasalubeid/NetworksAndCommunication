import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ConcHTTPAsk {
    private static int BUFFERSIZE = 1024;
    private static String response = "";
    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(args[0]);
            ServerSocket serverSocket = new ServerSocket(port);
            
            // continuously listen for incoming connections
            while (true) {
                // accept the incoming client connection
                Socket clientSocket = serverSocket.accept();
                handleRequest(clientSocket);
            }
        } catch (Exception e) {
            System.out.println("From main " + e);
        }
    }

}
