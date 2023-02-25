import java.net.*;
import java.io.*;

public class HTTPAsk {

    public static void main(String[] args) {
        try {
            // create a ServerSocket on port 8080
            ServerSocket serverSocket = new ServerSocket(8080);
            
            // continuously listen for incoming connections
            while (true) {
                // accept the incoming client connection
                Socket clientSocket = serverSocket.accept();
                
                // handle the incoming client request
                handleRequest(clientSocket);
            }
        } catch (IOException e) {
            // print the error message in case of exception
            System.err.println("IOException: " + e.getMessage());
        }
    }

    private static void handleRequest(Socket clientSocket) throws IOException {
        // construct the HTTP response header
        String response = "HTTP/1.1 200 OK\r\n" +
                          "Content-Type: text/plain\r\n" +
                          "\r\n" +
                          "This is from HTTPAsk";
        byte[] responseBytes = response.getBytes("UTF-8");
    
        // get the output stream of the client socket
        OutputStream outputStream = clientSocket.getOutputStream();
        
        // write the response bytes to the output stream and flush the stream
        outputStream.write(responseBytes);
        outputStream.flush();
    
        // close the client socket
        clientSocket.close();
    }
}
