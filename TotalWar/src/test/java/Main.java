import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Main {
    public static void main(String[] args) {
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        gameRunner.addAgent(Agent.class);
        gameRunner.addAgent(Agent2.class);

        /*gameRunner.addAgent("python3 /Users/giant/Desktop/agente.py");
        gameRunner.addAgent("python3 /Users/giant/Desktop/agente.py");*/

        gameRunner.setLeagueLevel(2);

        gameRunner.start();
    }
}