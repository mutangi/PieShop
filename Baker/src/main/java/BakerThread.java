import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class BakerThread implements Runnable {


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
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream input = new DataInputStream(socket.getInputStream())) {

            System.out.println("Baker Thread Connected");
            //Introduce the new baker
            output.writeUTF("I Am baker " + id);

            while (true) {

                output.writeUTF("BAKER with id: " + id + " NEW_PIE");
                System.out.println(id + " baker: baking pie for seconds " + bakingTime);
                Thread.sleep(bakingTime * 1000);

//                while (input.available() > 0) {
//                    String response = "";
//                    response = input.readUTF();
//                    if (response.contains("CUSTOMERS_DONE_EATING")) {
//                        bakersCounter--;
//                        break;
//                    }
//                }
//
//                if (bakersCounter == 0) {
//                    System.out.println("NO MORE CUSTOMERS, CLOSE THE SHOP");
//                }

            }
        } catch (EOFException e) {
            System.out.println("Closing connection!");
        } catch (IOException | InterruptedException e) {
            System.out.println("Closing connection");
        }
    }
}
