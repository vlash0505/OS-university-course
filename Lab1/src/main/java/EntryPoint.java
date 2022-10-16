import java.io.IOException;

public class EntryPoint {

    public static void main(String[] args) {
        //as the function takes only one parameter,
        //we should handle that case accordingly
        if (args.length != 1) {
            return;
        }

        try {
            new Manager().initManager(Integer.parseInt(args[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
