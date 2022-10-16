package function;

import os.lab1.compfuncs.basic.IntOps;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

public class Function {

    private static final int MAX_ATTEMPTS = 3;

    protected static Integer compute(int x, FunctionName functionName) throws InterruptedException, IOException {
        int countAttempts = 0;
        int arg = x;
        while (countAttempts <= MAX_ATTEMPTS) {
            Optional<Integer> result = (functionName == FunctionName.F) ? IntOps.trialF(arg) : IntOps.trialG(arg);
            if (result.isPresent()) {
                return result.get();
            } else {
                System.out.println("Soft fail occurred with the parameter being " + arg + "\n");
                arg = new Random().nextInt(10) + 1;
            }
            countAttempts++;
        }
        throw new IllegalArgumentException();
    }
}
