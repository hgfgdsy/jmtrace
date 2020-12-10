import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class APPtrans implements ClassFileTransformer {

    public static byte[] getBytesFromFile(byte[] is) throws IOException {

        ClassReader classReader = new ClassReader(is);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor visitor = new MyVisitor(writer);
        classReader.accept(visitor, ClassReader.EXPAND_FRAMES);

        return  writer.toByteArray();

    }

    public byte[] transform(ClassLoader l, String className, Class<?> c,
                            ProtectionDomain pd, byte[] b) throws IllegalClassFormatException {
        try {
            if(className.length() >= 4){
                if(className.substring(0,4).equals("jdk/")){
                    return b;
                }
                if(className.substring(0,4).equals("sun/")){
                    return b;
                }
            }
            if(className.length() >= 5){
                if(className.substring(0,5).equals("java/")){
                    return b;
                }
            }
            return getBytesFromFile(b);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}

