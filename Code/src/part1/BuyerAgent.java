package part1;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class BuyerAgent extends Agent {
    private int maxBidAmount;

    protected void setup() {
        System.out.println("Buyer started");
        maxBidAmount = 1000 + (int) (Math.random() * 501); // Generate random max bid amount between 1000 and 1500
        addBehaviour(new ReceiveOfferBehaviour());
    }

    private class ReceiveOfferBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                int currentPrice = Integer.parseInt(msg.getContent());
                int newBid = currentPrice + (int) (Math.random() * 11); // Generate random bid between 0 and 10
                if (newBid > maxBidAmount) {
                    System.out.println(getLocalName() + ": Reached max bid amount. Stopping bidding.");
                    addBehaviour(new StopBiddingBehaviour()); // Add the new behaviour
                    removeBehaviour(this); // Remove this behaviour
                } else {
                    System.out.println(getLocalName() + ": Received offer " + currentPrice + ". Making bid: " + newBid);
                    ACLMessage reply = msg.createReply();
                    reply.setContent(getLocalName() + " " + Integer.toString(newBid)); // Include agent's name in the reply
                    send(reply);
                }
            } else {
                block();
            }
        }
    }

    private class StopBiddingBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                System.out.println(getLocalName() + ": Received offer but not bidding.");
            } else {
                block();
            }
        }
    }
}