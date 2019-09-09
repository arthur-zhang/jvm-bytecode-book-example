import com.sun.tools.attach.VirtualMachine;

/**
 * Created By Arthur Zhang at 2019/9/4
 */
public class MyAttachMain {
    public static void main(String[] args) throws Exception {
        VirtualMachine vm = VirtualMachine.attach(args[0]);
        try {
            vm.loadAgent("/Users/arthur/cvt_dev/java/care/my-attach-demo/target/my-attach-agent.jar");
//            vm.loadAgent("/Users/arthur/cvt_dev/java/care/my-attach-demo-test/src/main/java/my-attach-agent.jar");
            System.in.read();
        } finally {
            vm.detach();
        }
    }
}
