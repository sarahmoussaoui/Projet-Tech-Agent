package part2_interplatforms;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
// import jade.core.Runtime;






public class Main {

    public static void main(String[] args) {
        // Get a hold on JADE runtime
        jade.core.Runtime rt = jade.core.Runtime.instance();

        // Create a profile, where the launch arguments are stored
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost"); // Change to match your host
        profile.setParameter(Profile.GUI, "true");

        // Create a main container
        AgentContainer mainContainer = rt.createMainContainer(profile);

        try {

             // Create the seller container
             Profile pSeller = new ProfileImpl(null, 8888, null);
             pSeller.setParameter(Profile.CONTAINER_NAME, "SellerContainer");
             AgentContainer sellerContainer = rt.createAgentContainer(pSeller);
 
             // Start 4 seller agents
             for (int i = 1; i <= 4; i++) {
                 AgentController seller = sellerContainer.createNewAgent("Seller" + i, "part2_interplatforms.SellerAgent", null);
                 seller.start();
             }

              // Create the buyer container
            Profile pBuyer = new ProfileImpl(null, 8888, null);
            pBuyer.setParameter(Profile.CONTAINER_NAME, "BuyerContainer");
            AgentContainer buyerContainer = rt.createAgentContainer(pBuyer);

            String[] sellerNames = {"Seller1", "Seller2", "Seller3", "Seller4"};
            AgentController buyer = buyerContainer.createNewAgent("Buyer", "part2_interplatforms.BuyerAgent", sellerNames);
            buyer.start();


            // // Start Agent1
            // AgentController agentController1 = mainContainer.createNewAgent("Agent1", Agent1.class.getName(), null);
            // agentController1.start();

            // // Start Agent2
            // AgentController agentController2 = mainContainer.createNewAgent("Agent2", Agent2.class.getName(), null);
            // agentController2.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}









