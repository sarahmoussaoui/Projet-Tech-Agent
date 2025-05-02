package part2_intercontainers;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Main {
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        
        // Create main container
        Profile mainProfile = new ProfileImpl();
        mainProfile.setParameter(Profile.GUI, "true");
        AgentContainer mainContainer = rt.createMainContainer(mainProfile);

        try {
            // Create seller container (different port)
            Profile sellerProfile = new ProfileImpl();
            sellerProfile.setParameter(Profile.CONTAINER_NAME, "Seller-Container");
            sellerProfile.setParameter(Profile.MAIN_PORT, "1099");
            AgentContainer sellerContainer = rt.createAgentContainer(sellerProfile);

            // Create buyer container
            Profile buyerProfile = new ProfileImpl();
            buyerProfile.setParameter(Profile.CONTAINER_NAME, "Buyer-Container");
            buyerProfile.setParameter(Profile.MAIN_PORT, "1098");
            AgentContainer buyerContainer = rt.createAgentContainer(buyerProfile);

            // Start seller agents
            for (int i = 1; i <= 4; i++) {
                AgentController seller = sellerContainer.createNewAgent(
                    "Seller" + i, SellerAgent.class.getName(), null);
                seller.start();
            }

            // Start buyer agent with seller names as arguments
            String[] sellerNames = {"Seller1", "Seller2", "Seller3", "Seller4"};
            AgentController buyer = buyerContainer.createNewAgent(
                "Buyer", BuyerAgent.class.getName(), sellerNames);
            buyer.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}