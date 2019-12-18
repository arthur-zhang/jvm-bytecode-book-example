import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class Encrypt {
    public static void main(String[] args) throws Exception {
        String jarPath = args[0];
        File srcFile = new File(jarPath);
        File dstFile = new File("encrypt.jar");

        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(dstFile));
        JarFile srcJarFile = new JarFile(srcFile);
        Enumeration<JarEntry> entries = srcJarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            addFileToJar(jarOutputStream, srcJarFile, entry);
        }
        jarOutputStream.close();
        srcJarFile.close();
    }

    private static void addFileToJar(JarOutputStream jarOutputStream,
                                     JarFile srcJarFile, JarEntry entry) throws IOException {
        String name = entry.getName();
        jarOutputStream.putNextEntry(new JarEntry(name));
        byte[] bytes = IOUtils.toByteArray(srcJarFile.getInputStream(entry));
        // 只转换以 me/ya/ 包名开头的 class 文件
        if (name.startsWith("me/ya/") && name.endsWith(".class")) {
            System.out.println("encrypting " + name.replaceAll("/", "."));
            bytes = encrypt(bytes);
        }
        jarOutputStream.write(bytes);
        jarOutputStream.closeEntry();
    }

    private static byte[] encrypt(byte[] bytes) {
        byte[] decodedBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            decodedBytes[i] = (byte) (bytes[i] ^ 0xFF);
        }
        return decodedBytes;
    }
}