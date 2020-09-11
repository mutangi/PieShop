import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Customer {


    public static void main(String[] args) {

        List<CustomerThread> customerThreadList = new LinkedList<CustomerThread>();
        AtomicInteger countOfCustomers = new AtomicInteger();

        //port
        int p = Integer.parseInt(args[0]);
        //max pies
        int x = Integer.parseInt(args[1]);
        //count of customers
        int m = Integer.parseInt(args[2]);
        countOfCustomers.set(m);

        for (int i = 0; i < m; i++) {
            //seconds to eat a pie
            int c = Integer.parseInt(args[i + 3]);
            System.out.println(c + " seconds to eat a pie");
            customerThreadList.add(new CustomerThread(p, x, c, countOfCustomers));
        }

        for (CustomerThread ct : customerThreadList) {
            new Thread(ct).start();
        }
    }
}
