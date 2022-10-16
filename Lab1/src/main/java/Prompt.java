import java.util.*;

public class Prompt {

    private final static int DELAY = 1000;
    private final static String PROMPT_TEXT = "1 - continue\n2 - continue without prompt\n3 - stop";

    private Timer promptTimer;
    private boolean isActive;
    private final List<Integer> values;
    private final Manager manager;

    public Prompt(List<Integer> values, Manager manager) {
        this.values = values;
        this.manager = manager;
        buildPrompt();
    }

    private void buildPrompt() {
        promptTimer = new Timer();
        promptTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                initPrompt();
            }
        }, DELAY);
    }

    private void initPrompt() {
        promptTimer.cancel();
        if (areValuesCalculated()) {
            isActive = false;
            endSession();
        }
        isActive = true;

        //show user the prompt
        System.out.println(PROMPT_TEXT);
        //handle user's input
        Scanner in = new Scanner(System.in);
        int i = in.nextInt();
        if (i == 1) {
            buildPrompt();
        } else if (i == 3) {
            endSession();
        }
        isActive = false;
    }

    private boolean areValuesCalculated() {
        return values.stream().noneMatch(Objects::isNull);
    }

    public boolean isActive() {
        return isActive;
    }

    private void endSession() {
        manager.endSession();
    }
}
