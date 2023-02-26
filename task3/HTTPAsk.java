import java.net.*;
import java.io.*;
import java.util.Scanner;

public class HTTPAsk {

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
            System.out.println(e);
        }
    }

    private static void handleRequest(Socket clientSocket) throws IOException {
        String url = getURL(clientSocket);
        String[] urlParts = url.split("\\?"); // split the URL into parts using ? as the separator
        String path = urlParts[0]; // extract the path from the first part of the URL string
        String queryString = urlParts.length > 1 ? urlParts[1] : ""; // extract the query string from the second part of the URL string, if it exists
        System.out.println("Path: " + path); // print the path to the console
        System.out.println("Query string: " + queryString); // print the query string to the console

        //Pare the query string
        Scanner scanner = new Scanner(queryString);
        scanner.useDelimiter("&");
        while (scanner.hasNext()) {
            // get the parameter and value
            String[] param = scanner.next().split("=");
            String paramName = param[0];
            // If there is a value, otherwise ""
            String paramValue = param.length > 1 ? param[1] : "";
    
            // print the parameter and value
            System.out.println(paramName + " = " + paramValue);
        }

        // create a response
        String response = "HTTP/1.1 200 OK\r\n" // create the HTTP response status line
                + "Content-Type: text/html\r\n" // add the HTTP response headers
                + "\r\n" // add an empty line to separate the headers from the body
                + "<h1>HTTP Ask Server</h1>\n" // add the HTML response body
                + "<p>Path: " + path + "</p>\n"
                + "<p>Query string: " + queryString + "</p>\n";
        byte[] responseBytes = response.getBytes("UTF-8"); // convert the response string to bytes using UTF-8 encoding
    
        // write the response to the output stream
        OutputStream outputStream = clientSocket.getOutputStream(); 
        outputStream.write(responseBytes); 
    
        outputStream.close();
        clientSocket.close(); 
    }

    private static String getURL(Socket clientSocket) throws IOException {
        // read the client request
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String request = reader.readLine(); // read the first line
        System.out.println("Request: " + request); // print the client request to the console
    
        // extract the URL and query string
        String[] parts = request.split(" "); // first part is just "GET"
        return parts[1]; // extract the URL from the second part of the request string
     }

}
