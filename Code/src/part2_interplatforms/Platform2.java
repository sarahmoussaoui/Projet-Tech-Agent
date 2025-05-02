package part2_interplatforms;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.ContainerController;

public class Platform2 {
    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.instance();
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            profile.setParameter(Profile.MAIN_PORT, "1098");
            profile.setParameter(Profile.MAIN, "false");
            ContainerController secondaryContainer = rt.createAgentContainer(profile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
