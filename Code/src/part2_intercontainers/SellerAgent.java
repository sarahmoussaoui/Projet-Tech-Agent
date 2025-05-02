package part2_intercontainers;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SellerAgent extends Agent {
    private String productName = "ProductX";
    private double price;
    private double deliveryTime;
    private double quality;
    private double userRatings;
    private int stockAvailability;

    protected void setup() {
        // Initialize random product attributes
        price = 100 + Math.random() * 100; // 100-200
        deliveryTime = 1 + Math.random() * 9; // 1-10 days
        quality = 1 + Math.random() * 9; // 1-10
        userRatings = 1 + Math.random() * 4; // 1-5
        stockAvailability = (int)(Math.random() * 100); // 0-100
        
        System.out.println(getLocalName() + " ready with product " + productName + 
                         " (Price: " + price + ", Delivery: " + deliveryTime + 
                         " days, Quality: " + quality + ")");

        addBehaviour(new OfferBehaviour());
    }

    private class OfferBehaviour extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = receive(mt);
            
            if (msg != null) {
                System.out.println(getLocalName() + " received CFP from " + msg.getSender().getLocalName());
                
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.PROPOSE);
                reply.setContent(price + "," + deliveryTime + "," + 
                                quality + "," + userRatings + "," + 
                                stockAvailability);
                send(reply);
            } else {
                block();
            }
        }
    }
}