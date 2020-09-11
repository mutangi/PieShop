import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class PieShop {

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
            System.out.println("server started");
            System.out.println("waiting for a client");

            while (true) {
                try {
                    socket = server.accept();
                    System.out.println("client accepted");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                executorService.execute(new ConnectionThread(socket, pieCount));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("closing connection");
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
}
