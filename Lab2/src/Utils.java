import java.util.StringTokenizer;

public class Utils {

    public static int parseInteger(String line) {
        StringTokenizer st = new StringTokenizer(line);
        st.nextToken();
        return Common.s2i(st.nextToken());
    }

}
