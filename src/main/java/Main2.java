import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main2 {

    public static int compute(int x) {
        String binary = Integer.toBinaryString(x);
        int sum = 0;
        for (String str : List.of(binary.split(""))) {
            int n = Integer.parseInt(str);
            sum = sum + n;
        }

        StringBuilder builderNewBinary = new StringBuilder();
        if (binary.length() > 2) {
            if (sum % 2 == 0) {
                builderNewBinary
                        .append("10")
                        .append(binary.substring(2))
                        .append("0");
            } else {
                builderNewBinary
                        .append("11")
                        .append(binary.substring(2))
                        .append("11");
            }
        } else {
            builderNewBinary.append(binary);
        }

        int numberTen = Integer.parseInt(builderNewBinary.toString(), 2);

        return numberTen;
    }

    public static void main(String[] args) {
        //   int Res = compute(2);

        Set<Integer> integerSet = new HashSet<>();

        for (int i = 0; i <= 1000; i++) {
            int R = compute(i);
            if (R < 99) {
                integerSet.add(i);
            }

        }
        int maxValue = 0;
        for (Integer integer : integerSet) {
            if (integer > maxValue) {
                maxValue = integer;
            }
        }
        System.out.println(maxValue);
    }
}
