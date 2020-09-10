import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionThread.class);

    protected static Queue<ConnectionThread> waitingQueue = new ConcurrentLinkedQueue<>();
    protected Socket socket;
    protected AtomicInteger pieCount;
    private CountDownLatch waitingLatch;

    public ConnectionThread(Socket clientSocket, AtomicInteger pieCount) {
        this.socket = clientSocket;
        this.pieCount = pieCount;
    }

    public void run() {

        try (DataInputStream inp = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            String line;
            while (true) {

                line = inp.readUTF();
                if (line != null && line.contains("NEW_PIE")) {

                    String bakerId = line.replace("NEW_PIE", "");
                    int count = pieCount.incrementAndGet();
                    logger.info("New pie received from: {}", bakerId);
                    logger.info("The shop has {} pies left", count);

                    if (!waitingQueue.isEmpty()) {
                        waitingQueue.poll().getWaitingLatch().countDown();
                    }

                } else if (line != null && line.contains("BUY_PIE")) {
                    String customerId = line.replace("BUY_PIE", "");
                    //check if there are pies
                    synchronized (ConnectionThread.class) {
                        int count = pieCount.decrementAndGet();
                        if (count <= 0) {
                            logger.info("Not enough pies. Waiting for new pies!");
                            waitingLatch = new CountDownLatch(1);
                            waitingQueue.add(this);
                            waitingLatch.await();
                        }
                        //count = pieCount.decrementAndGet();
                        out.writeUTF("HAS_PIE");
                        logger.info("New pie given to: {}", customerId);
                        logger.info("The shop has {} pies left", count);
                    }
                }
            }
        } catch (EOFException ex) {
            //TODO: Add ID to log which connection is closed
            logger.warn("Connection closing!");
        } catch (IOException e) {
            logger.error("IOException occurred, closing connection!" + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public CountDownLatch getWaitingLatch() {
        return waitingLatch;
    }
}

