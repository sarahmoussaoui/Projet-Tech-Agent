package part2_interplatforms;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.AID;
import jade.core.Location;
import jade.core.ContainerID;

public class BuyerAgent extends Agent {
    private AID[] sellerAgents;

    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            sellerAgents = new AID[args.length];
            for (int i = 0; i < args.length; ++i) {
                sellerAgents[i] = new AID((String) args[i], AID.ISLOCALNAME);
            }

            addBehaviour(new OneShotBehaviour() {
                public void action() {
                    System.out.println("BuyerAgent is trying to move to the SellerPlatform...");
                    ContainerID destination = new ContainerID();
                    destination.setName("Main-Container-seller");
                    destination.setAddress("localhost");
                    destination.setPort("8888");

                    try {
                        doMove(destination);  // Try to move the agent
                        System.out.println("Migration initiated successfully.");
                        Location loc = here();
                        System.out.println("BuyerAgent current location: " + loc.getName());
                    } catch (Exception e) {
                        System.out.println("Migration failed: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });


            addBehaviour(new CyclicBehaviour() {
                public void action() {
                    Location loc = here();
                    System.out.println("BuyerAgent current location: " + loc.getName());
                    if (loc.getName().equals("Main-Container-seller")) {
                        System.out.println("BuyerAgent has arrived at the SellerPlatform.");
                        addBehaviour(new RequestOffers());
                        removeBehaviour(this); // Remove this behavior as it's no longer needed
                    } else {
                        block(1000); // Wait for some time before checking location again
                    }
                }
            });
        } else {
            System.out.println("No sellers specified");
            doDelete();
        }
    }

    private class RequestOffers extends OneShotBehaviour {
        public void action() {
            System.out.println("Requesting offers from sellers...");
            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
            for (AID sellerAgent : sellerAgents) {
                cfp.addReceiver(sellerAgent);
            }
            cfp.setContent("request-offer");
            send(cfp);

            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
            ACLMessage[] responses = new ACLMessage[sellerAgents.length];
            int responsesCnt = 0;

            while (responsesCnt < sellerAgents.length) {
                ACLMessage reply = blockingReceive(mt);
                if (reply != null) {
                    responses[responsesCnt] = reply;
                    responsesCnt++;
                }
            }

            ACLMessage bestOffer = null;
            double bestPrice = Double.MAX_VALUE;

            for (ACLMessage response : responses) {
                String[] content = response.getContent().split(",");
                double price = Double.parseDouble(content[0]);
                double deliveryPrice = Double.parseDouble(content[1]);

                double totalCost = price + deliveryPrice;

                if (totalCost < bestPrice) {
                    bestPrice = totalCost;
                    bestOffer = response;
                }
            }

            if (bestOffer != null) {
                ACLMessage order = bestOffer.createReply();
                order.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                order.setContent("buy");
                send(order);
                System.out.println("Accepted offer from: " + bestOffer.getSender().getLocalName() + " with total cost: " + bestPrice);
            }
        }
    }


    
}

