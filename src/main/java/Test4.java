public class Test4 {
    boolean x;
    boolean y;
    boolean z;
    boolean w;

    public Test4(int x, int y, int z, int w) {
        if (x == 1) this.x = true;
        else this.x = false;

        if (y == 1) {
            this.y = true;
        } else {
            this.y = false;
        }

        if (z == 1) {
            this.z = true;
        } else {
            this.z = false;
        }

        if (w == 1) {
            this.w = true;
        } else {
            this.w = false;
        }
    }

    public void print() {
        if (y && !x) {
            if (!z || w){
                System.out.println(this);
            }
        }
    }
    public static int convertBooleanToInt(boolean a) {
        int i;
        if (a) {
            i = 1;
        } else {
            i = 0;
        }
        return i;
    }

    @Override
    public String toString() {
        return "Результат {" + convertBooleanToInt(x)+ ", "
                + convertBooleanToInt(y) + ", "
                + convertBooleanToInt(z) + ", "
                + convertBooleanToInt(w) + "}";

    }
}

