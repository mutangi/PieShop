import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class BakerThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(BakerThread.class);

    private int id;
    private static int idCounter = 0;

    private String address = "127.0.0.1";
    private int port;
    private int bakingTime;

    public BakerThread(int port, int bakingTime) {
        this.port = port;
        this.bakingTime = bakingTime;
        this.id = idCounter++;
    }

    public void run() {
        try (Socket socket = new Socket(address, port);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {

            logger.info("Baker Thread Connected");
            //Introduce the new baker
            output.writeUTF("I Am baker " + id);

            while (true) {
                output.writeUTF("BAKER: " + id + " NEW_PIE");
                logger.info("baker: {} baking pie for {} seconds", id, bakingTime);
                Thread.sleep(bakingTime * 1000);
            }
        } catch (EOFException e) {
            logger.info("Closing connection!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
