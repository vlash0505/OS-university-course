// This file contains the main() function for the Scheduling
// simulation.  Init() initializes most of the variables by
// reading from a provided file.  SchedulingAlgorithm.Run() is
// called from main() to run the simulation.  Summary-Results
// is where the summary results are written, and Summary-Processes
// is where the process scheduling summary is written.

// Created by Alexander Reeder, 2001 January 06

import java.io.*;
import java.util.*;

import static java.lang.Math.min;

public class Scheduling {
    private static int processnum = 5;
    private static int meanDev = 1000;
    private static int standardDev = 100;
    private static int runtime = 1000;
    private static Vector processVector = new Vector();
    private static Results result = new Results("null", "null", 0);
    private static String resultsFile = "Summary-Results";
    private static List<List<Integer>> queueList = new ArrayList<>();
    private static int queuesLimit = 0;


    private static void Init(File f) {
        String line;
        int cputime = 0;
        int ioblocking = 0;
        double X = 0.0;

        try {
            DataInputStream in = new DataInputStream(new FileInputStream(f));
            int processId = 0;

            while ((line = in.readLine()) != null) {
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
        int i = 0;

        System.out.println("processnum " + processnum);
        System.out.println("meandevm " + meanDev);
        System.out.println("standdev " + standardDev);
        int size = processVector.size();
        for (i = 0; i < size; i++) {
            sProcess process = (sProcess) processVector.elementAt(i);
            System.out.println("process " + i + " " + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.numblocked);
        }
        System.out.println("runtime " + runtime);
    }

    public static void main(String[] args) {
        int i = 0;

//        if (args.length != 1) {
//            System.out.println("Usage: 'java Scheduling <INIT FILE>'");
//            System.exit(-1);
//        }
        File f = new File("E:\\Dev\\University\\OS-university-course\\Lab2\\src\\config.cfg");
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
            i = 0;
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
        result = SchedulingAlgorithm.run(runtime, processVector, queueList, result);
        try {
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
            out.println("Scheduling Type: " + result.schedulingType);
            out.println("Scheduling Name: " + result.schedulingName);
            out.println("Simulation Run Time: " + result.compuTime);
            out.println("Mean: " + meanDev);
            out.println("Standard Deviation: " + standardDev);
            out.println("Process #\tCPU Time\tIO Blocking\tCPU Completed\tCPU Blocked");
            for (i = 0; i < processVector.size(); i++) {
                sProcess process = (sProcess) processVector.elementAt(i);
                out.print(Integer.toString(i));
                if (i < 100) {
                    out.print("\t\t");
                } else {
                    out.print("\t");
                }
                out.print(Integer.toString(process.cputime));
                if (process.cputime < 100) {
                    out.print(" (ms)\t\t");
                } else {
                    out.print(" (ms)\t");
                }
                out.print(Integer.toString(process.ioblocking));
                if (process.ioblocking < 100) {
                    out.print(" (ms)\t\t");
                } else {
                    out.print(" (ms)\t");
                }
                out.print(Integer.toString(process.cpudone));
                if (process.cpudone < 100) {
                    out.print(" (ms)\t\t");
                } else {
                    out.print(" (ms)\t");
                }
                out.println(process.numblocked + " times");
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Completed.");
    }
}

