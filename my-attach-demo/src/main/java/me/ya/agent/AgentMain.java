package me.ya.agent;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

import static org.objectweb.asm.Opcodes.ASM7;


/**
 * Created By Arthur Zhang at 2019/9/4
 */
public class AgentMain {
    public static class MyMethodVisitor extends AdviceAdapter {
        protected MyMethodVisitor(MethodVisitor mv, int access, String name, String desc) {
            super(ASM7, mv, access, name, desc);
        }

        @Override
        protected void onMethodEnter() {
            // 在方法开始插入 return 50;
            mv.visitIntInsn(BIPUSH, 50);
            mv.visitInsn(IRETURN);
        }
    }

    public static class MyClassVisitor extends ClassVisitor {

        public MyClassVisitor(ClassVisitor classVisitor) {
            super(ASM7, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            // 只转换 foo 方法
            if ("foo".equals(name)) {
                return new MyMethodVisitor(mv, access, name, descriptor);
            }
            return mv;
        }
    }

    public static class MyClassFileTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
            if (!"MyTestMain".equals(className)) return bytes;
            ClassReader cr = new ClassReader(bytes);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
            ClassVisitor cv = new MyClassVisitor(cw);
            cr.accept(cv, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
            return cw.toByteArray();
        }
    }

    public static void agentmain(String agentArgs, Instrumentation inst) throws ClassNotFoundException, UnmodifiableClassException {
        System.out.println("agentmain called");
        inst.addTransformer(new MyClassFileTransformer(), true);
        Class classes[] = inst.getAllLoadedClasses();
        for (int i = 0; i < classes.length; i++) {
            if (classes[i].getName().equals("MyTestMain")) {
                System.out.println("Reloading: " + classes[i].getName());
                inst.retransformClasses(classes[i]);
                break;
            }
        }
    }
}
