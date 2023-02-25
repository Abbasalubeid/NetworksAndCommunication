import java.net.*;
import java.io.*;

public class HTTPAsk {

    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(args[0]);
            ServerSocket serverSocket = new ServerSocket(port);
            
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
            System.out.println(e);
        }
    }

    private static void handleRequest(Socket clientSocket) throws IOException {
        // construct the HTTP response header
        // first \r\n ends the headers, and the second double \r\n is an empty line 
        // that separates the headers from the message body.
        String response = "HTTP/1.1 200 OK\r\n" //status 
                          + "Content-Type: text/html\r\n" //Header 
                          + "\r\n" 
                          + "<div>This is a test from HTTPAsk</div>"; //Body
        byte[] responseBytes = response.getBytes("UTF-8");
    
        // get the output stream of the client socket
        OutputStream outputStream = clientSocket.getOutputStream();
        
        // write the response bytes to the output stream and flush the stream
        outputStream.write(responseBytes);
    
        // close the client socket
        clientSocket.close();
    }
}
