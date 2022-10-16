import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Manager {

    private final static int TIMEOUT = 15;

    private final List<Process> processes = new ArrayList<>();

    private Integer f = null;
    private Integer g = null;

    private Prompt prompt;

    public void initManager(int arg) throws IOException {
        initPrompt();
        try {
            int result = performProcesses(arg);
            System.out.println("Binary operation result: " + result);
        } catch (TimeoutException e) {
            System.out.println("Timeout");
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        endSession();
    }

    private void initPrompt() {
        List<Integer> values = new ArrayList<>();
        values.add(f);
        values.add(g);
        prompt = new Prompt(values, this);
    }

    private int performProcesses(int arg) throws IOException, InterruptedException, TimeoutException {
        initProcesses(arg);
        try {
            f = Integer.parseInt(Files.readAllLines(Paths.get("function.FunctionF" + ".log")).get(0));
            g = Integer.parseInt(Files.readAllLines(Paths.get("function.FunctionG" + ".log")).get(0));
        } catch (NumberFormatException e) {
            while (prompt.isActive()) {
                Thread.sleep(1000);
            }
            throw new IllegalArgumentException(e.getMessage().split("For the next input: ")[1]);
        }
        while (prompt.isActive()) {
            Thread.sleep(1000);
        }
        //sum of two results is used as a binary operation
        return binaryOperation();
    }

    private Integer binaryOperation() {
        return f + g;
    }

    private void initProcesses(int arg) throws IOException, InterruptedException, TimeoutException {
        List<String> args = List.of(String.valueOf(arg));

        ProcessBuilder processBuilderF = buildProcess("function.FunctionF", new ArrayList<>(), args);
        ProcessBuilder processBuilderG = buildProcess("function.FunctionG", new ArrayList<>(), args);

        Process processF = processBuilderF.start();
        Process processG = processBuilderG.start();

        processes.add(processF);
        processes.add(processG);

        if (!processF.waitFor(TIMEOUT, TimeUnit.SECONDS) || !processG.waitFor(TIMEOUT, TimeUnit.SECONDS)) {
            while (prompt.isActive()) {
                Thread.sleep(1000);
            }
            throw new TimeoutException();
        }
    }

    private ProcessBuilder buildProcess(String className, List<String> jvmArgs, List<String> args) {
        ProcessBuilder processBuilder = new ProcessBuilder(buildCommand(className, jvmArgs, args));

        File log = new File(className + ".log");
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(log);

        return processBuilder;
    }

    private List<String> buildCommand(String className, List<String> jvmArgs, List<String> args) {
        String javaBin =  System.getProperty("java.home") + "\\bin\\java";
        String classpath = System.getProperty("java.class.path");

        List<String> command = new ArrayList<>();
        command.add(javaBin);
        command.addAll(jvmArgs);
        command.add("-cp");
        command.add(classpath);
        command.add(className);
        command.addAll(args);

        return command;
    }

    public void endSession() {
        System.out.println("Session ended.");
        processes.forEach(Process::destroyForcibly);
        Runtime.getRuntime().halt(0);
    }
}
