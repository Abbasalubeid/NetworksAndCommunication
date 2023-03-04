import java.net.Socket;

public class MyRunnable implements Runnable {
    private Socket connectionSocket

    public MyRunnable (Socket connetionSocket){
        this.connectionSocket = connetionSocket;
    }

    @Override
    // this is called when the start method of the thread is called
    public run(){

    }

}
