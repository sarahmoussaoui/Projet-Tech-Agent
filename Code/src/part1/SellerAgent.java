package part1;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

public class SellerAgent extends Agent {
    private int startingPrice = 100;
    private int reservePrice = 800;
    private int currentPrice;
    private int auctionTime = 30000; // 30 seconds
    private long startTime;
    private long lastBidTime;
    private String winningBuyer;

    protected void setup() {
        System.out.println("Seller agent " + getAID().getName() + " is ready.");
        addBehaviour(new SendStartingPriceBehaviour());
        addBehaviour(new ReceiveBidsBehaviour());
    }

    private class SendStartingPriceBehaviour extends OneShotBehaviour {
        public void action() {
            currentPrice = startingPrice;
            System.out.println("Seller: Starting auction with price " + currentPrice);
            
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent(Integer.toString(currentPrice));
            
            // Add all buyer agents as receivers
            for (int i = 1; i <= 4; i++) {
                msg.addReceiver(new AID("BuyerAgent" + i, AID.ISLOCALNAME));
            }
            
            send(msg);
            startTime = System.currentTimeMillis();
            lastBidTime = startTime;
        }
    }

    private class ReceiveBidsBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                lastBidTime = System.currentTimeMillis();
                String[] content = msg.getContent().split(" ");
                String buyerName = content[0];
                int bidAmount = Integer.parseInt(content[1]);
                
                if (bidAmount > currentPrice) {
                    currentPrice = bidAmount;
                    winningBuyer = buyerName;
                    System.out.println("Seller: New highest bid " + currentPrice + " from " + buyerName);
                    
                    // Notify all buyers
                    ACLMessage update = new ACLMessage(ACLMessage.INFORM);
                    update.setContent(Integer.toString(currentPrice));
                    for (int i = 1; i <= 4; i++) {
                        update.addReceiver(new AID("BuyerAgent" + i, AID.ISLOCALNAME));
                    }
                    send(update);
                }
            } else {
                // Check if auction time has elapsed
                if (System.currentTimeMillis() - lastBidTime > auctionTime) {
                    System.out.println("Seller: Auction ended. Winning bid: " + currentPrice + " from " + winningBuyer);
                    doDelete();
                } else {
                    block();
                }
            }
        }
    }
}