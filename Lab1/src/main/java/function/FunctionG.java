package function;

import java.io.IOException;

public class FunctionG {

    public static void main(String[] args) {
        try {
            System.out.println(Function.compute(Integer.parseInt(args[0]), FunctionName.G));
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
