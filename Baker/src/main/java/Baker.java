import java.util.LinkedList;
import java.util.List;

public class Baker {


    public static void main(String[] args) {

        List<BakerThread> bakerThreadList = new LinkedList<>();
        int timeMakingPie;

        //port
        int p = Integer.parseInt(args[0]);

        //count of bakers
        int n = Integer.parseInt(args[1]);

        for (int i = 0; i < n; i++) {
            //seconds to make a pie
            timeMakingPie = Integer.parseInt(args[i + 2]);
            System.out.println(" +++++++ SECONDS FOR BAKE+++++++ " + timeMakingPie);
            bakerThreadList.add(new BakerThread(p, timeMakingPie));
        }

        for (BakerThread bt : bakerThreadList) {
            new Thread(bt).start();
        }
    }

}
