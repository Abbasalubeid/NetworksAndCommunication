import java.net.*;
import java.io.*;

public class TCPClient {
    private static int BUFFERSIZE = 1024;
    boolean shutdown = false;
    Integer timeout;
    Integer limit;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {

        //Byte array that grows dynamically since we do not know how much data we receive
        ByteArrayOutputStream fromServerBuffer = new ByteArrayOutputStream();
        //Temporarily storage for each "read" iteration of data
        byte[] intermediateStorage = new byte[BUFFERSIZE];
        //This constructor calls connect to the given hostname at the given port 
        Socket clientSocket = new Socket(hostname, port);
        clientSocket.getOutputStream().write(toServerBytes); //send(...)

        //Get the input stream from the socket to perform recv(...) later
        InputStream input = clientSocket.getInputStream(); 
        
        receive(intermediateStorage, fromServerBuffer, input, clientSocket);

        clientSocket.close();

        return fromServerBuffer.toByteArray();
    }

    public byte[] askServer(String hostname, int port) throws IOException {
        //Byte array that grows dynamically since we do not know how much data we receive
        ByteArrayOutputStream fromServerBuffer = new ByteArrayOutputStream();
        //Temporarily storage for each "read" iteration of data
        byte[] intermediateStorage = new byte[BUFFERSIZE];

        //This constructor calls connect to the given hostname at the given port 
        Socket clientSocket = new Socket(hostname, port);
        //Get the input stream from the socket to perform recv(...) later
        InputStream input = clientSocket.getInputStream(); 
        
        receive(intermediateStorage, fromServerBuffer, input, clientSocket);

        clientSocket.close();

        return fromServerBuffer.toByteArray();
    }

    private void receive(byte[] intermediate, ByteArrayOutputStream buffer, InputStream input, Socket client)throws IOException{
                
        //Close the outgoing direction after sending data if shutdown is true
        if (shutdown)   
            client.shutdownOutput();

        //Set a timer for the read call on the input
        if(timeout != null){
            if(timeout >= 0)
                try {
                    client.setSoTimeout(timeout);
                } catch (SocketException e) {
                    System.out.println(e);
                }
            else{
                System.out.println("Timeout cannot be a negative number");
                return;
            }
        }

        //Temporary saves the returned "read amount" for each read iteration
        int currentLength;
        //Counter for the data to check if the limit is reached
        int counter = 0;

        while(true){
            try {    
                currentLength = input.read(intermediate); //recv(...)

                //-1 indicates end of the stream, no more data to collect
                if(currentLength == -1)
                    break;

                buffer.write(intermediate, 0, currentLength);
                counter += currentLength;

                if((limit != null && counter >= limit))
                    break;
                
            } catch (SocketTimeoutException e) {
                return;
            }
        }
    }
}