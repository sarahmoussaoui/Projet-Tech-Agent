package part2_interplatforms;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Arrays;

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

            addBehaviour(new CyclicBehaviour() {
                private int attemptCount = 0;
                private final int MAX_ATTEMPTS = 3;

                public void action() {
                    if (attemptCount >= MAX_ATTEMPTS) {
                        System.out.println("Max migration attempts reached. Giving up.");
                        removeBehaviour(this);
                        return;
                    }

                    attemptCount++;
                    System.out.println("Migration attempt #" + attemptCount);

                    ContainerID destination = new ContainerID("Main-Container-seller", null);

                    try {
                        System.out.println("Trying to move to: " + destination.getName());
                        doMove(destination);

                        // If we get here, migration likely failed
                        System.out.println("Move attempt completed but agent didn't migrate");
                        block(3000); // Wait 3 seconds before retrying

                    } catch (Exception e) {
                        System.out.println("Migration failed: " + e.getMessage());
                        block(3000); // Wait 3 seconds before retrying
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
                System.out.println("Accepted offer from: " + bestOffer.getSender().getLocalName() + " with total cost: "
                        + bestPrice);
            }
        }
    }

}
