import java.net.*;
import java.io.*;
import java.util.Scanner;

public class HTTPAsk {
    private static int BUFFERSIZE = 1024;


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
        byte[] responseBytes = getResponse(url);

        // write the response to the output stream
        OutputStream outputStream = clientSocket.getOutputStream(); 
        outputStream.write(responseBytes); 
        outputStream.close();
        clientSocket.close(); 
    }

    private static String getURL(Socket clientSocket) throws IOException {
        // read the client request and decode it
        byte[] buffer = new byte[BUFFERSIZE];
        clientSocket.getInputStream().read(buffer);
        String request = new String(buffer, "UTF-8");
        System.out.println("Request: " + request); // print the client request to the console
    
        // extract the URL and query string
        String[] parts = request.split(" "); // first part is just "GET"
        return parts[1]; // extract the URL from the second part of the request string
     }

     private static byte[] getResponse(String url) throws UnsupportedEncodingException{
        String hostname = null;
        Integer port = null;
        String string = "Nothing";
        boolean shutdown = false;
        Integer limit = null;
        Integer timeout = null;

        String[] urlParts = url.split("\\?"); // split the URL into parts using ? as the separator
        String path = urlParts[0]; // extract the path from the first part of the URL string
        String queryString = urlParts.length > 1 ? urlParts[1] : ""; // extract the query string from the second part of the URL string, if it exists
        System.out.println("Path: " + path); // print the path to the console
        System.out.println("Query string: " + queryString); // print the query string to the console
        // create a Scanner object to parse the query string
        Scanner scanner = new Scanner(queryString);
        scanner.useDelimiter("&");

        // loop through the query string parameters
        while (scanner.hasNext()) {
            // get the parameter and value
            String[] param = scanner.next().split("=");
            switch (param[0]) {
                case "hostname":
                    hostname = param[1];
                    break;
                case "port":
                    port = Integer.parseInt(param[1]);
                    break;
                case "string":
                    string = param[1];
                    break;
                case "shutdown":
                    shutdown = Boolean.parseBoolean(param[1]);
                    break;
                case "limit":
                    limit = Integer.parseInt(param[1]);
                    break;
                case "timeout":
                    timeout = Integer.parseInt(param[1]);
                    break;
                default:
                    break;
            } 
        }

        byte[] serverBytes = new byte[0];
        String serverOutput = "";
        try {
            TCPClient client = new TCPClient(shutdown, timeout, limit);
            serverBytes = client.askServer(hostname, port, string.getBytes());
            serverOutput = new String(serverBytes);
        } catch (Exception e) {
            System.out.println(e);
        }
        // create a response
        String response = "HTTP/1.1 200 OK \r\n" // create the HTTP response status line
        + "Content-Type: text/html\r\n" // add the HTTP response headers
        + "\r\n" // add an empty line to separate the headers from the body
        + "<h1>HTTP Ask Server</h1>\n" // add the HTML response body
        + "<p>Path: " + path + "</p>\n"
        + "<h2>The query you sent:</h2>\n"
        + "<p>Query string: " + queryString + "</p>\n"
        + "<p>hostname: " + hostname + "</p>\n"
        + "<p>port: " + port + "</p>\n"
        + "<p>string: " + string + "</p>\n"
        + "<p>shutdown: " + shutdown + "</p>\n"
        + "<p>limit: " + limit + "</p>\n"
        + "<p>timeout: " + timeout + "</p>\n"
        + "<h1>Response:</h1>\n"
        + "<p>" + serverOutput + "</p>\n";
        return response.getBytes("UTF-8"); // convert the response string to bytes 
     }

}
