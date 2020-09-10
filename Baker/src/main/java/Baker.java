import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Baker {
    private static final Logger logger = LoggerFactory.getLogger(Baker.class);


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
            logger.info(" +++++++ {} SECONDS FOR BAKE+++++++", timeMakingPie);
            bakerThreadList.add(new BakerThread(p, timeMakingPie));
        }

        for (BakerThread bt : bakerThreadList) {
            new Thread(bt).start();
        }
    }

}
