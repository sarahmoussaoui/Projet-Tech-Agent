package part2_interplatforms;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class SellerPlatformMain {
    public static void main(String[] args) {
        // Create and set up the seller platform container
        Runtime rt = Runtime.instance();
        Profile pSeller = new ProfileImpl();
        pSeller.setParameter(Profile.PLATFORM_ID, "SellerPlatform");
        pSeller.setParameter(Profile.CONTAINER_NAME, "Main-Container-seller");
        pSeller.setParameter(Profile.MAIN_HOST, "localhost");
        pSeller.setParameter(Profile.MAIN_PORT, "8888");
        pSeller.setParameter(Profile.SERVICES, "jade.core.mobility.AgentMobilityService;jade.core.event.NotificationService");

        pSeller.setParameter(Profile.GUI, "true");

        AgentContainer sellerContainer = rt.createMainContainer(pSeller);

        try {
            // Start 4 seller agents
            for (int i = 1; i <= 4; i++) {
                AgentController seller = sellerContainer.createNewAgent("Seller" + i, "part2_interplatforms.SellerAgent", null);
                seller.start();
            }
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
