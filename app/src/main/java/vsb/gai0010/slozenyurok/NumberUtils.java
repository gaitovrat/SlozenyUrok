package vsb.gai0010.slozenyurok;

public class NumberUtils {
    static int parseInt(String s) {
        if (s.isEmpty()) {
            return 0;
        }

        return (int) Float.parseFloat(s);
    }
}
