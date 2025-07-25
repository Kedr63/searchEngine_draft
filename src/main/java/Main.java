public class Main {

    public static void main(String[] args) {
//        Test4 test4 = new Test4(1, 0, 1, 1);
//        test4.print();

        int x;
        int y;
        int z;
        int w;
        for (int i = 0; i < 2; i++) {
            x = i;
            for (int j = 0; j < 2; j++) {
                y = j;
                for (int k = 0; k < 2; k++) {
                    z = k;
                    for (int l = 0; l < 2; l++) {
                        w = l;
                        Test4 test41 = new Test4(x, y, z, w);
                        test41.print();
                    }
                }
            }

        }

    }
}
