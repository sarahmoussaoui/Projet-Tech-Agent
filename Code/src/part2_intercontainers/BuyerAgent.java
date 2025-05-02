package part2_intercontainers;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ControllerException;
import jade.core.AID;

public class BuyerAgent extends Agent {
    private AID[] sellerAgents;

    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            sellerAgents = new AID[args.length];
            for (int i = 0; i < args.length; i++) {
                sellerAgents[i] = new AID((String) args[i], AID.ISLOCALNAME);
            }
            
            System.out.println("Buyer agent ready. Sellers: " + java.util.Arrays.toString(args));
            addBehaviour(new RequestOffersBehaviour());
        } else {
            System.out.println("No seller agents specified");
            doDelete();
        }
        
        try {
			System.out.println(
				    "Buyer running in: " + 
				    this.getContainerController().getContainerName()
				);
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private class RequestOffersBehaviour extends OneShotBehaviour {
        public void action() {
            // Send CFP to all sellers
            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
            for (AID seller : sellerAgents) {
                cfp.addReceiver(seller);
            }
            cfp.setContent("product-request");
            send(cfp);
            System.out.println("Sent CFP to all sellers");

            // Collect proposals
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
            ACLMessage bestProposal = null;
            double bestScore = -1;
            int repliesCnt = 0;
            
            // Wait for all proposals or timeout
            long startTime = System.currentTimeMillis();
            while (repliesCnt < sellerAgents.length && 
                  (System.currentTimeMillis() - startTime) < 10000) {
                
                ACLMessage reply = receive(mt);
                if (reply != null) {
                    repliesCnt++;
                    System.out.println("Received proposal from " + reply.getSender().getLocalName());
                    
                    // Parse proposal
                    String[] content = reply.getContent().split(",");
                    double price = Double.parseDouble(content[0]);
                    double deliveryTime = Double.parseDouble(content[1]);
                    double quality = Double.parseDouble(content[2]);
                    double userRatings = Double.parseDouble(content[3]);
                    int stock = Integer.parseInt(content[4]);
                    
                    // Evaluate offer
                    double score = ProductEvaluator.evaluateProduct(
                        price, deliveryTime, quality, userRatings, stock);
                    
                    if (score > bestScore) {
                        bestScore = score;
                        bestProposal = reply;
                    }
                } else {
                    block(1000);
                }
            }

            // Accept best proposal
            if (bestProposal != null) {
                ACLMessage accept = bestProposal.createReply();
                accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                accept.setContent("accept-offer");
                send(accept);
                System.out.println("Accepted offer from " + bestProposal.getSender().getLocalName() + 
                                 " with score: " + bestScore);
            } else {
                System.out.println("No acceptable offers received");
            }
            
            doDelete();
        }
    }
}