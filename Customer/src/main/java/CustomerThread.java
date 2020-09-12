import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomerThread implements Runnable {

    private int id;
    private static int idCounter = 0;
    private String address = "127.0.0.1";
    private int port;
    private int eatingTime;
    private int maximumPiesPerDay;
    private static AtomicInteger customersCounter;

    public CustomerThread(int port, int maxPies, int eatingTime, AtomicInteger countOfCustomers) {
        this.maximumPiesPerDay = maxPies;
        this.id = idCounter++;
        this.port = port;
        this.eatingTime = eatingTime;
        this.customersCounter = countOfCustomers;
    }

    public void run() {
        try (Socket socket = new Socket(address, port);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()))) {

            System.out.println(id + "Customer Thread Connected" + id);

            output.writeUTF("I Am customer " + id);
            while (maximumPiesPerDay > 0) {
                System.out.println(id + " Customer wants new pie.");
                output.writeUTF("CUSTOMER with id: " + id + " BUY_PIE");
                System.out.println(id + " Customer waiting new pie.");
                String response = in.readUTF();

                if ("HAS_PIE".equalsIgnoreCase(response)) {
                    System.out.println(id + " Customer: eating pie for seconds: " + eatingTime);
                    Thread.sleep(eatingTime * 1000);
                    maximumPiesPerDay--;
                    System.out.println(id + " Customer has PIES LEFT FOR TODAY ======================== " + maximumPiesPerDay);
                }
            }
            System.out.println("CUSTOMER_DONE_EATING, id: " + id);
            customersCounter.decrementAndGet();
            if (customersCounter.get() <= 0) {
                output.writeUTF("STOP");
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            System.out.println("Customer connection closing!");
        }
    }
}
