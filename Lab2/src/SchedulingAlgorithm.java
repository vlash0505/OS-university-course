import java.util.*;
import java.io.*;

public class SchedulingAlgorithm {
    private static final String STATE_REGISTERED = "registered";
    private static final String STATE_COMPLETED  = "completed";
    private static final String STATE_IO_BLOCKED = "I/O blocked";

    public static void Run(int runtime, Vector<sProcess> processVector, List<List<Integer>> queueList, Results result) {
        int comptime = 0;
        int currentProcess;
        int size = processVector.size();
        int completed = 0;
        String resultsFile = "Summary-Processes";

        result.schedulingType = "Interactive";
        result.schedulingName = "Multiple Queues";

        try {
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
            int currentQueueLevel = 0;

            while (comptime < runtime) {
                List<Integer> currentQueue = queueList.get(currentQueueLevel);
                for (int i = 0; i < currentQueue.size(); i++) {
                    currentProcess = currentQueue.get(i);
                    sProcess process = processVector.elementAt(currentProcess);
                    ProcessLogger.logProcess(out, STATE_REGISTERED, currentProcess, process);

                    while (comptime < runtime) {
                        if (process.cpudone >= process.cputime) {
                            i--;
                            completed++;
                            currentQueue.remove((Object) currentProcess);
                            ProcessLogger.logProcess(out, STATE_COMPLETED, currentProcess, process);

                            if (completed == size) {
                                result.compuTime = comptime;
                                out.close();
                                return;
                            } else {
                                break;
                            }
                        }

                        if (process.ioblocking == process.ionext) {
                            ProcessLogger.logProcess(out, STATE_IO_BLOCKED, currentProcess, process);

                            process.numblocked++;
                            process.ionext = 0;
                            process.ioblocking *= 2;
                            if (currentQueueLevel != queueList.size() - 1) {
                                queueList.get(currentQueueLevel + 1).add(currentProcess);
                                currentQueue.remove((Object) currentProcess);
                                i--;
                                if (i < 0) {
                                    i = 0;
                                }
                            }

                            break;
                        }
                        process = processVector.elementAt(currentProcess);
                        process.cpudone++;
                        if (process.ioblocking > 0) {
                            process.ionext++;
                        }
                        comptime++;
                    }
                }
                if (comptime >= runtime) {
                    break;
                }
                currentQueueLevel = (currentQueueLevel + 1) % queueList.size();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.compuTime = comptime;
    }
}
