package part2_interplatforms;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class BuyerPlatformMain {
    public static void main(String[] args) {
        // Create and set up the buyer platform container
        Runtime rt = Runtime.instance();
        Profile pBuyer = new ProfileImpl();
        pBuyer.setParameter(Profile.PLATFORM_ID, "BuyerPlatform");
        pBuyer.setParameter(Profile.CONTAINER_NAME, "Main-Container-buyer");
        pBuyer.setParameter(Profile.MAIN_HOST, "localhost");
        pBuyer.setParameter(Profile.MAIN_PORT, "8889");
        pBuyer.setParameter(Profile.SERVICES, "jade.core.mobility.AgentMobilityService;jade.core.event.NotificationService");
        pBuyer.setParameter(Profile.GUI, "true");
        AgentContainer buyerContainer = rt.createMainContainer(pBuyer);

        try {
            // Start the buyer agent
            String[] sellerNames = {"Seller1", "Seller2", "Seller3", "Seller4"};
            AgentController buyer = buyerContainer.createNewAgent("Buyer", "part2_interplatforms.BuyerAgent", sellerNames);
            buyer.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}

