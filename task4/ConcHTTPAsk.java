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

    private static void handleRequest(Socket clientSocket) throws IOException {
        try {
            byte[] responseBytes = getResponse(clientSocket);
            // write the response to the output stream
            OutputStream outputStream = clientSocket.getOutputStream(); 
            outputStream.write(responseBytes); 
            outputStream.close();
            clientSocket.close();
        } catch (UnsupportedEncodingException e) {
            System.out.println("From handleRequest " + e);
        }

    }

    private static String getURL(Socket clientSocket) throws Exception {
        // read the client request and decode it
        byte[] buffer = new byte[BUFFERSIZE];
        clientSocket.getInputStream().read(buffer);
        String request = new String(buffer, "UTF-8");
    
        // extract the URL and query string
        String[] parts = request.split(" "); // GET request parts

        // It has to be a GET request
        if (!parts[0].equals("GET") || !request.contains("HTTP/1.1")){
            response = "HTTP/1.1 400 Bad Request \r\n"
            + "Content-Type: text/html\r\n" 
            + "\r\n"
           + "<h1>400 Bad Request</h1>\n";  
            throw new Exception("400 Bad Request");
        } 

        return parts[1]; // extract the URL from the second part of the request string
     }


     private static byte[] getResponse(Socket clientSocket) throws UnsupportedEncodingException{
        String hostname = null;
        Integer port = null;
        String stringToServer = "";
        boolean shutdown = false;
        Integer limit = null;
        Integer timeout = null;

        try {
            String url = getURL(clientSocket);
            String[] urlParts = url.split("\\?"); // split the URL into parts using ? as the separator
            String path = urlParts[0]; // extract the path from the first part of the URL string
    
            // request for a resource we dont have (example: favicon)
            if(!path.equals("/ask")){
                response = "HTTP/1.1 404 Not Found \r\n"
                + "Content-Type: text/html\r\n" 
                + "\r\n"
               + "<h1>404 Not Found</h1>\n";  
                throw new Exception("404 Not Found");
            }
            
            // extract the query string from the second part of the URL string, if it exists
            String queryString = urlParts.length > 1 ? urlParts[1] : ""; 
    
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
                        stringToServer = param[1] + "\n";
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
    
            // hostname and port are not optional
            if ((hostname == null || port == null)){
                response = "HTTP/1.1 400 Bad Request \r\n"
                + "Content-Type: text/html\r\n" 
                + "\r\n"
               + "<h1>400 Bad Request</h1>\n";  
                throw new Exception("400 Bad Request");
            }
    
            byte[] serverBytes = new byte[0];
            String serverOutput = "";
            try {
                TCPClient client = new TCPClient(shutdown, timeout, limit);
                serverBytes = client.askServer(hostname, port, stringToServer.getBytes());
                serverOutput = new String(serverBytes);
            } catch (Exception e) {
                if (e instanceof java.net.UnknownHostException){
                    // example: calling time.nift.gov instead of time.nist.gov
                    response = "HTTP/1.1 404 Not Found \r\n"
                    + "Content-Type: text/html\r\n" 
                    + "\r\n"
                   + "<h1>404 Not Found</h1>\n";  
                    throw new Exception("404 Not Found");
                }
                else{
                    // if something we don't know happens in the connection
                    // not part of task3 but so that the program continues
                    response = "HTTP/1.1 503 Service Unavailable \r\n"
                     + "Content-Type: text/html\r\n" 
                     + "\r\n"
                    + "<h1>503 Service Unavailable</h1>\n";  
                    throw new Exception("503 Service Unavailable");
                }
            }
            response = "HTTP/1.1 200 OK\r\n" // create the HTTP response status line
            + "Content-Type: text/html\r\n" // add the HTTP response headers
            + "\r\n" // add an empty line to separate the headers from the body
            + "<h1>" + serverOutput + "</h1>\n";// the HTML response body

            return response.getBytes("UTF-8"); // convert the response string to bytes
            
        } catch (Exception e) {
            return response.getBytes("UTF-8"); // Any error case 
        }
     }
}
