import java.io.PrintStream;

public class ProcessLogger {

    public static void logProcess(PrintStream out, String processState, int currentProcess, sProcess process) {
        out.println("Process: " + currentProcess + " " + processState + "... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.ionext + ")");
    }

}
