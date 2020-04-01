import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Main {
    public static void main(String[] args) {
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        gameRunner.addAgent(Agent2.class);
        gameRunner.addAgent(AgentAI.class);

        /*gameRunner.addAgent("python3 /Users/giant/Desktop/agente.py");
        gameRunner.addAgent("python3 /Users/giant/Desktop/agente.py");*/

        gameRunner.setLeagueLevel(3);

        gameRunner.start();
    }
}