import java.util.concurrent.TimeUnit;

/**
 * Created By Arthur Zhang at 2019/9/4
 */
public class MyTestMain {
    public static void main(String[] args) throws InterruptedException {
        MyTestMain main = new MyTestMain();
        while (true) {
            System.out.println(main.foo());
            TimeUnit.SECONDS.sleep(3);
        }
    }

    public int foo() {
        return 100;
    }
}
