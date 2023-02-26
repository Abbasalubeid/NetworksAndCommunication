import java.net.*;
import java.io.*;
import java.util.Scanner;

public class HTTPAsk {
    private static int BUFFERSIZE = 1024;
    private static String status = "HTTP/1.1 200 OK \r\n";
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
        try {
            byte[] responseBytes = getResponse(url);
            // write the response to the output stream
            OutputStream outputStream = clientSocket.getOutputStream(); 
            outputStream.write(responseBytes); 
            outputStream.close();
            clientSocket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static String getURL(Socket clientSocket) throws IOException {
        // read the client request and decode it
        byte[] buffer = new byte[BUFFERSIZE];
        clientSocket.getInputStream().read(buffer);
        String request = new String(buffer, "UTF-8");
        System.out.println("Request: " + request); // print the client request to the console
    
        // extract the URL and query string
        String[] parts = request.split(" "); // GET... 
        if (!parts[0].equals("GET") || !request.contains("HTTP/1.1")) 
            status = "HTTP/1.1 400 Bad Request \r\n";
        else
            status ="HTTP/1.1 200 OK \r\n";
        return parts[1]; // extract the URL from the second part of the request string
     }

     private static byte[] getResponse(String url) throws Exception{
        String hostname = null;
        Integer port = null;
        String stringToServer = "";
        boolean shutdown = false;
        Integer limit = null;
        Integer timeout = null;

        String[] urlParts = url.split("\\?"); // split the URL into parts using ? as the separator
        String path = urlParts[0]; // extract the path from the first part of the URL string
        if(!path.equals("/ask")){
            status = "HTTP/1.1 400 Bad Request \r\n";
        }
        else 
            status = "HTTP/1.1 200 OK \r\n"; 
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
                    stringToServer = param[1];
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

        if (hostname == null || port == null)
            status = "HTTP/1.1 400 Bad Request \r\n";
        else
            status ="HTTP/1.1 200 OK \r\n";

        byte[] serverBytes = new byte[0];
        String serverOutput = "";
        try {
            TCPClient client = new TCPClient(shutdown, timeout, limit);
            serverBytes = client.askServer(hostname, port, stringToServer.getBytes());
            serverOutput = new String(serverBytes);
        } catch (Exception e) {
            System.out.println(e);
        }
        String response = "";
        // create a response
        if(status.equals("HTTP/1.1 400 Bad Request \r\n")){
            response = status // create the HTTP response status line
            + "Content-Type: text/html\r\n" // add the HTTP response headers
            + "\r\n" // add an empty line to separate the headers from the body
            + "<h1>400 Bad request</h1>\n"; // add the HTML response body
        }
        else{
            response = status // create the HTTP response status line
            + "Content-Type: text/html\r\n" // add the HTTP response headers
            + "\r\n" // add an empty line to separate the headers from the body
            + "<h1>HTTP Ask Server</h1>\n" // add the HTML response body
            + "<p>Path: " + path + "</p>\n"
            + "<h2>The query you sent:</h2>\n"
            + "<p>Query string: " + queryString + "</p>\n"
            + "<p>hostname: " + hostname + "</p>\n"
            + "<p>port: " + port + "</p>\n"
            + "<p>stringToServer: " + stringToServer + "</p>\n"
            + "<p>shutdown: " + shutdown + "</p>\n"
            + "<p>limit: " + limit + "</p>\n"
            + "<p>timeout: " + timeout + "</p>\n"
            + "<h1>Response:</h1>\n"
            + "<p>" + serverOutput + "</p>\n";
        }   
        return response.getBytes("UTF-8"); // convert the response string to bytes 
     }

}
