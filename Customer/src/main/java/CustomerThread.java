import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class CustomerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(CustomerThread.class);

    private int id;
    private static int idCounter = 0;

    private String address = "127.0.0.1";
    private int port;
    private int eatingTime;
    private int maximumPiesPerDay;

    public CustomerThread(int port, int maxPies, int eatingTime) {
        this.maximumPiesPerDay = maxPies;
        this.id = idCounter++;
        this.port = port;
        this.eatingTime = eatingTime;
    }

    public void run() {
        try (
                Socket socket = new Socket(address, port);
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        ) {
            logger.info("Customer {} Thread Connected", id);

            output.writeUTF("I Am customer " + id);
            while (maximumPiesPerDay > 0) {
                logger.info("Customer {} wants new pie.", id);
                output.writeUTF("CUSTOMER: " + id + " BUY_PIE");
                logger.info("Customer {} waiting new pie.", id);
                String response = in.readUTF();

                if ("HAS_PIE".equalsIgnoreCase(response)) {
                    logger.info("Customer: {} eating pie for {} seconds", id, eatingTime);
                    Thread.sleep(eatingTime * 1000);
                    maximumPiesPerDay--;
                    logger.info("Customer {} has {} PIES LEFT FOR TODAY ========================", id, maximumPiesPerDay);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            logger.info("Customer connection closing!");
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Customer connection closing!");
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            logger.info("Customer connection closing!");
        }
    }
}
