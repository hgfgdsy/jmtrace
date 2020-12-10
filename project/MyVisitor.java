import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MyVisitor extends ClassVisitor {

    MyVisitor(ClassVisitor cv) {
        super(Opcodes.ASM6, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {


        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        mv = new MyMethodVisitor(Opcodes.ASM6, mv);

        return mv;
    }
}
