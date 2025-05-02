package part1;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.Runtime;

public class Main {
    public static void main(String[] args) {
        // Start JADE container
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.GUI, "true");
        ContainerController cc = rt.createMainContainer(p);

        try {
            // Create and start agent
            AgentController ac = cc.createNewAgent("SellerAgent", "part1.SellerAgent", null);
            ac.start();

            for (int i = 1; i <= 4; i++) {
                ac = cc.createNewAgent("BuyerAgent" + i, "part1.BuyerAgent", null);
                ac.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}