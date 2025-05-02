package part2_interplatforms;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class Platform1 {
    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.instance();
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            profile.setParameter(Profile.MAIN_PORT, "1099");
            ContainerController mainContainer = rt.createMainContainer(profile);

            // Create and start the migrating agent
            AgentController agent = mainContainer.createNewAgent("migratingAgent", MigratingAgent.class.getName(), null);
            agent.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
