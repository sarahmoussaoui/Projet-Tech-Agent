package part2_interplatforms;

import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.Location;
import jade.core.behaviours.Behaviour;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.mobility.MobilityOntology;
// import jade.domain.mobility.MobilityOperations;
import jade.lang.acl.ACLMessage;
import jade.core.Location; // Import the Location class

public class MigratingAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("Agent " + getLocalName() + " started.");

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                try {
                    // Set up the target container for migration
                    // Location destination = new Location("localhost", "1098");
                    ContainerID destination = new ContainerID();
                    // destination.setName("Main-Container-seller");
                    destination.setAddress("localhost"); // Replace with the actual IP address of the host
                    destination.setPort("1098");

                    doMove(destination);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void afterMove() {
        System.out.println("Hello, world");
    }
}
