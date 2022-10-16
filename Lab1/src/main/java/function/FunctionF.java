package function;

import java.io.IOException;

public class FunctionF {

    public static void main(String[] args) {
        try {
            System.out.println(Function.compute(Integer.parseInt(args[0]), FunctionName.F));
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
