package part2_interplatforms;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SellerAgent extends Agent {
    private String productName = "Product A";
    private double price;
    private double deliveryPrice;
    private int deliveryTime;

    protected void setup() {
        // Randomly initialize seller's criteria
        price = 100 + Math.random() * 50; // price between 100 and 150
        deliveryPrice = 10 + Math.random() * 20; // delivery price between 10 and 30
        deliveryTime = 1 + (int)(Math.random() * 5); // delivery time between 1 and 5 days

        System.out.println(getLocalName() + " - Product: " + productName + ", Price: " + price + ", Delivery Price: " + deliveryPrice + ", Delivery Time: " + deliveryTime + " days");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                ACLMessage msg = receive(mt);
                if (msg != null) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent(price + "," + deliveryPrice + "," + deliveryTime);
                    send(reply);
                } else {
                    block();
                }
            }
        });
    }
}
