


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class PieShop {

    private static final Logger logger = LoggerFactory.getLogger(PieShop.class);

    private ServerSocket server = null;
    private Socket socket = null;
    private DataInputStream in = null;
    private AtomicInteger pieCount = new AtomicInteger();
    private ExecutorService executorService;

    public PieShop(int port, int stockOfPies) {

        pieCount.set(stockOfPies);
        executorService = Executors.newCachedThreadPool();

        try {
            server = new ServerSocket(port);
            logger.info("server started");
            logger.info("waiting for a client");

            while (true) {
                try {
                    socket = server.accept();
                    logger.info("client accepted");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                executorService.execute(new ConnectionThread(socket, pieCount));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("closing connection");
        try {
            socket.close();
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int p = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);

        new PieShop(p, y);
    }

    //1. Similar approach for customer threads - DONE
    //2. Segregate threads in the server based on type - baker/customer - DONE
    //3. Configure all modules to take arguments - DONE?
    //4. Solve the customer waiting problem !!!
    //n. Clean up code
}
