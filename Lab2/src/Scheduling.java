import java.io.*;
import java.util.*;

import static java.lang.Math.min;

public class Scheduling {
    private static int processnum = 5;
    private static int meanDev = 1000;
    private static int standardDev = 100;
    private static int runtime = 1000;
    private static final Vector<sProcess> processVector = new Vector<>();
    private static final Results result = new Results("null", "null", 0);
    private static List<List<Integer>> queueList = new ArrayList<>();
    private static int queuesLimit = 0;


    private static void Init(File f) {
        String line;
        int cputime;
        int ioblocking;
        double X;

        try {
            DataInputStream in = new DataInputStream(new FileInputStream(f));
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            int processId = 0;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("maxqueues")) {
                    queuesLimit = Utils.parseInteger(line);

                    queueList = new ArrayList<>();
                    for (int i = 0; i < queuesLimit; i++) {
                        queueList.add(new ArrayList<>());
                    }
                }
                if (line.startsWith("numprocess")) {
                    processnum = Utils.parseInteger(line);
                }
                if (line.startsWith("meandev")) {
                    meanDev = Utils.parseInteger(line);
                }
                if (line.startsWith("standdev")) {
                    standardDev = Utils.parseInteger(line);
                }
                if (line.startsWith("process")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    ioblocking = Common.s2i(st.nextToken());
                    int level = min(queuesLimit - 1, Common.s2i(st.nextToken()));
                    if (level < 0) {
                        level = 0;
                    }
                    X = Common.R1();
                    while (X == -1.0) {
                        X = Common.R1();
                    }
                    X = X * standardDev;
                    cputime = (int) X + meanDev;
                    processVector.addElement(new sProcess(cputime, ioblocking, 0, 0, 0));
                    queueList.get(level).add(processId++);
                }
                if (line.startsWith("runtime")) {
                    runtime = Utils.parseInteger(line);
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void debug() {
        System.out.println("processnum " + processnum);
        System.out.println("meandevm " + meanDev);
        System.out.println("standdev " + standardDev);
        int size = processVector.size();
        for (int i = 0; i < size; i++) {
            sProcess process = processVector.elementAt(i);
            System.out.println("process " + i + " " + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.numblocked);
        }
        System.out.println("runtime " + runtime);
    }

    public static void main(String[] args) {
        int i = 0;

        if (args.length != 1) {
            System.out.println("Usage: 'java Scheduling <INIT FILE>'");
            System.exit(-1);
        }
        File f = new File(args[0]);
        if (!(f.exists())) {
            System.out.println("Scheduling: error, file '" + f.getName() + "' does not exist.");
            System.exit(-1);
        }
        if (!(f.canRead())) {
            System.out.println("Scheduling: error, read of " + f.getName() + " failed.");
            System.exit(-1);
        }
        System.out.println("Working...");
        Init(f);
        if (processVector.size() < processnum) {
            int processId = processVector.size();
            Random random = new Random();
            while (processVector.size() < processnum) {
                int cputime = (int) (random.nextDouble() * standardDev + meanDev);
                int level = random.nextInt(0, queuesLimit);
                processVector.addElement(new sProcess(cputime, (i + 1) * 100, 0, 0, 0));
                queueList.get(level).add(processId++);
                i++;
            }
        }
        SchedulingAlgorithm.Run(runtime, processVector, queueList, result);
        try {
            String resultsFile = "Summary-Results";
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
            out.println("Scheduling Type: " + result.schedulingType);
            out.println("Scheduling Name: " + result.schedulingName);
            out.println("Simulation Run Time: " + result.compuTime);
            out.println("Mean: " + meanDev);
            out.println("Standard Deviation: " + standardDev);
            out.println("Process #\tCPU Time\tIO Blocking\tCPU Completed\tCPU Blocked");
            for (i = 0; i < processVector.size(); i++) {
                sProcess process = processVector.elementAt(i);

                out.print(i);
                out.print(i < 100 ? "\t\t" : "\t");

                out.print(process.cputime);
                out.print(process.cputime < 100 ? " (ms)\t\t" : " (ms)\t");

                out.print(process.ioblocking);
                out.print(process.ioblocking < 100 ? " (ms)\t\t" : " (ms)\t");

                out.print(process.cpudone);
                out.print(process.cpudone < 100 ? " (ms)\t\t" : " (ms)\t");

                out.println(process.numblocked + " times");
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Completed.");
    }
}

