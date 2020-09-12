import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


public class ConnectionThread extends Thread {

    protected static Queue<ConnectionThread> waitingQueue = new ConcurrentLinkedQueue<>();
    protected Socket socket;
    protected AtomicInteger pieCount;
    private CountDownLatch waitingLatch;
    private Date date = new Date();
//    Queue<String> ministryOfFinance = new ConcurrentLinkedQueue<>();
//    private boolean exit;
//    boolean closeTheShop = true;
//    List<String> ministryOfFinance = new ArrayList<>();

    public ConnectionThread(Socket clientSocket, AtomicInteger pieCount) {
        this.socket = clientSocket;
        this.pieCount = pieCount;
//        exit = false;
    }

    public void run() {

        try (DataInputStream inp = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            String line;
            while (true) {

                line = inp.readUTF();

                //Baker
                if (line != null && line.contains("NEW_PIE")) {

                    String bakerId = line.replace("NEW_PIE", "");
                    int count = pieCount.incrementAndGet();

                    System.out.println("New pie received from: " + bakerId + new Timestamp(date.getTime()));
                    System.out.println("The shop has pies left " + count);
//                    ministryOfFinance.add("Pie received from " + bakerId + new Timestamp(date.getTime()) + "\n");

                    if (!waitingQueue.isEmpty()) {
                        waitingQueue.poll().getWaitingLatch().countDown();
                    }
                    //Customer
                } else if (line != null && line.contains("BUY_PIE")) {
                    String customerId = line.replace("BUY_PIE", "");
                    //check if there are pies
                    synchronized (ConnectionThread.class) {
                        int count = pieCount.decrementAndGet(); //get?
                        if (count <= 0) {
                            System.out.println("Not enough pies. Waiting for new pies!");
                            waitingLatch = new CountDownLatch(1);
                            waitingQueue.add(this);
                            waitingLatch.await();
                        }
                        out.writeUTF("HAS_PIE");
                        System.out.println("New pie given to: " + customerId + new Timestamp(date.getTime()));
                        System.out.println("The shop has pies left " + count);
                    }
                } else if (line != null && line.contains("STOP")) {

                    //TODO proper reporting (slf4j)
//                  generateReport(ministryOfFinance);
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@ END OF THE DAY @@@@@@@@@@@@@@@@@@@@@");
                    System.exit(1);
                }
            }

        } catch (EOFException ex) {
            //TODO: Add ID to log which connection is closed
            System.out.println("Connection closing 1");
        } catch (IOException e) {
            System.out.println("IOException occurred, closing connection!" + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("connection closing 2");
            e.printStackTrace();
        }
    }

    public CountDownLatch getWaitingLatch() {
        return waitingLatch;
    }

//    public void generateReport(Queue<String> allRecords) {
//                    System.out.println("-----MINISTRY REPORT-----");
//                    for (String s : ministryOfFinance) {
//                        System.out.println(s);
//                    }
//                    System.out.println("-----END OF REPORT-----");
//    }
}