import java.util.LinkedList;
import java.util.List;

public class Customer {

    private static int idCounter = 0;

    public static void main(String[] args) {

        List<CustomerThread> customerThreadList = new LinkedList<CustomerThread>();

        //port
        int p = Integer.parseInt(args[0]);
        //max pies
        int x = Integer.parseInt(args[1]);
        //count of customers
        int m = Integer.parseInt(args[2]);

        for (int i = 0; i < m; i++) {
            //seconds to eat a pie
            int c = Integer.parseInt(args[i + 3]);
            customerThreadList.add(new CustomerThread(p, x, c));
        }

        for (CustomerThread ct : customerThreadList) {
            new Thread(ct).start();
        }
    }
}
