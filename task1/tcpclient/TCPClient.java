package tcpclient;
import java.net.*;

import javax.sound.midi.Receiver;


import javax.sound.midi.Receiver;

import java.io.*;

public class TCPClient {
    private static int BUFFERSIZE = 1024;
    
    public TCPClient() {
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
        
        receive(intermediateStorage, fromServerBuffer, input);

        clientSocket.close();

        return fromServerBuffer.toByteArray();
    }

    private void receive(byte[] intermediate, ByteArrayOutputStream buffer, InputStream input)throws IOException{
        //Temporary saves the returned "read amount" for each read iteration
        int currentLength;
        while(true){
            //recv(...)
            currentLength = input.read(intermediate);
            //-1 indicates end of the stream, no more data to collect
            if(currentLength == -1)
                break;
            buffer.write(intermediate, 0, currentLength);
        }
    }

    // public byte[] askServer(String hostname, int port) throws IOException {

    // }
}
