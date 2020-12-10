import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.PrintStream;

public class MyMethodVisitor extends MethodVisitor{
    MyMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if(owner.length() >= 4){
            if(owner.substring(0,4).equals("jdk/")){
                super.visitFieldInsn(opcode, owner, name, descriptor);
                return;
            }
            if(owner.substring(0,4).equals("sun/")){
                super.visitFieldInsn(opcode, owner, name, descriptor);
                return;
            }
        }
        if(owner.length() >= 5){
            if(owner.substring(0,5).equals("java/")){
                super.visitFieldInsn(opcode, owner, name, descriptor);
                return;
            }
        }
        if (opcode == Opcodes.PUTFIELD){
            if(descriptor.equals("D") || descriptor.equals("J")){
                mv.visitInsn(Opcodes.DUP2_X1);
                mv.visitInsn(Opcodes.POP2);
                mv.visitInsn(Opcodes.DUP);
                mv.visitLdcInsn(owner +"."+ name);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mtrace", "traceWritefield", "(Ljava/lang/Object;Ljava/lang/String;)V", false);
                mv.visitInsn(Opcodes.DUP_X2);
                mv.visitInsn(Opcodes.POP);
            }
            else {
                mv.visitInsn(Opcodes.DUP2);
                mv.visitInsn(Opcodes.POP);
                mv.visitLdcInsn(owner + "." + name);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mtrace", "traceWritefield", "(Ljava/lang/Object;Ljava/lang/String;)V", false);
            }
        }
        if (opcode == Opcodes.GETFIELD){
            mv.visitInsn(Opcodes.DUP);
            mv.visitLdcInsn(owner +"."+ name);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mtrace", "traceReadfield", "(Ljava/lang/Object;Ljava/lang/String;)V", false);
        }
        super.visitFieldInsn(opcode, owner, name, descriptor);
        if (opcode == Opcodes.GETSTATIC){
            mv.visitFieldInsn(Opcodes.GETSTATIC, owner, name, descriptor);
            mdds(mv, descriptor);
            mv.visitLdcInsn(owner +"."+ name);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mtrace", "traceReadstatic", "(Ljava/lang/Object;Ljava/lang/String;)V", false);
        }
        if (opcode == Opcodes.PUTSTATIC){
            mv.visitFieldInsn(Opcodes.GETSTATIC, owner, name, descriptor);
            mdds(mv, descriptor);
            mv.visitLdcInsn(owner +"."+ name);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mtrace", "traceWritestatic", "(Ljava/lang/Object;Ljava/lang/String;)V", false);
        }
    }

    @Override
    public void visitInsn(int opcode) {
        if(calo(opcode)){
            mv.visitInsn(Opcodes.DUP2);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mtrace", "traceaload", "(Ljava/lang/Object;I)V", false);
        }
        if(calot(opcode)){
            if(opcode == Opcodes.DASTORE || opcode == Opcodes.LASTORE)
            {
                mv.visitInsn(Opcodes.DUP2_X2);
                mv.visitInsn(Opcodes.POP2);
                mv.visitInsn(Opcodes.DUP2);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mtrace", "traceastore", "(Ljava/lang/Object;I)V", false);
                mv.visitInsn(Opcodes.DUP2_X2);
                mv.visitInsn(Opcodes.POP2);
            }
            else {
                mv.visitInsn(Opcodes.DUP_X2);
                mv.visitInsn(Opcodes.POP);
                mv.visitInsn(Opcodes.DUP2_X1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mtrace", "traceastore", "(Ljava/lang/Object;I)V", false);
            }
        }
        super.visitInsn(opcode);
    }


    //    @Override
//    public void visitCode() {
//        super.visitCode();
//        System.out.println("start hack before");
//        hack(mv, "asm insert before");
//    }
//
//
//    @Override
//    public void visitInsn(int opcode) {
//        if (opcode == Opcodes.RETURN) {
//            System.out.println("start hack after");
//            hack(mv, "asm insert after");
//        }
//        if (opcode == Opcodes.IADD) {
//            System.out.println("Woops, won't it");
//        }
//        super.visitInsn(opcode);
//    }

//    @Override
//    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
////        super.visitFieldInsn(opcode, owner, name, descriptor);
//        if (opcode == Opcodes.GETFIELD) {
//            System.out.println(owner + name + "+" + descriptor);
//            if(owner.length() >= 4){
//                if(owner.substring(0,4).equals("jdk/")){
//                    return;
//                }
//                if(owner.substring(0,4).equals("sun/")){
//                    return;
//                }
//            }
//            if(owner.length() >= 5){
//                if(owner.substring(0,5).equals("java/")){
//                    return;
//                }
//            }
//            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J", false);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);
//
//
////            mv.visitInsn(Opcodes.POP);
////            mv.visitInsn(Opcodes.DUP);
//
//            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            mv.visitInsn(Opcodes.DUP2);
//            mv.visitInsn(Opcodes.POP);
//            mv.visitFieldInsn(Opcodes.GETFIELD, owner, name, descriptor);
//            if(descriptor.length() == 1){
//                switch (descriptor){
//                    case "I": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
//                        break;
//                    }
//                    case "S": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
//                        break;
//                    }
//                    case "J": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
//                        break;
//                    }
//                    case "Z": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
//                        break;
//                    }
//                    case "F": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
//                        break;
//                    }
//                    case "D": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
//                        break;
//                    }
//                    case "C": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
//                        break;
//                    }
//                    default: {
//                    }
//                }
//            }
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "identityHashCode", "(Ljava/lang/Object;)I", false);
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "toHexString", "(I)Ljava/lang/String;", false);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
////            mv.visitFieldInsn(Opcodes.GETFIELD, owner, name, descriptor);
//        }
//        else if(opcode == Opcodes.PUTFIELD) {
//            System.out.println(owner + name + "+" + descriptor);
//            if(owner.length() >= 4){
//                if(owner.substring(0,4).equals("jdk/")){
//                    return;
//                }
//                if(owner.substring(0,4).equals("sun/")){
//                    return;
//                }
//            }
//
//            if(owner.length() >= 5){
//                if(owner.substring(0,5).equals("java/")){
//                    return;
//                }
//            }
//            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J", false);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);
//
//
//
//            mv.visitInsn(Opcodes.DUP2);
//            mv.visitInsn(Opcodes.POP);
//            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            mv.visitInsn(Opcodes.DUP2);
//            mv.visitInsn(Opcodes.POP);
//            mv.visitFieldInsn(Opcodes.GETFIELD, owner, name, descriptor);
//            if(descriptor.length() == 1){
//                switch (descriptor){
//                    case "I": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
//                        break;
//                    }
//                    case "S": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
//                        break;
//                    }
//                    case "J": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
//                        break;
//                    }
//                    case "Z": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
//                        break;
//                    }
//                    case "F": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
//                        break;
//                    }
//                    case "D": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
//                        break;
//                    }
//                    case "C": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
//                        break;
//                    }
//                    default: {
//                    }
//                }
//            }
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "identityHashCode", "(Ljava/lang/Object;)I", false);
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "toHexString", "(I)Ljava/lang/String;", false);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//            mv.visitInsn(Opcodes.POP);
//        }
//        super.visitFieldInsn(opcode, owner, name, descriptor);
//        if(opcode == Opcodes.PUTSTATIC) {
//            System.out.println(owner + name + "+" + descriptor);
//            if(owner.length() >= 4){
//                if(owner.substring(0,4).equals("jdk/")){
//                    return;
//                }
//                if(owner.substring(0,4).equals("sun/")){
//                    return;
//                }
//            }
//
//            if(owner.length() >= 5){
//                if(owner.substring(0,5).equals("java/")){
//                    return;
//                }
//            }
//            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J", false);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);
//
//
//            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            mv.visitFieldInsn(Opcodes.GETSTATIC, owner, name, descriptor);
//            if(descriptor.length() == 1){
//                switch (descriptor){
//                    case "I": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
//                        break;
//                    }
//                    case "S": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
//                        break;
//                    }
//                    case "J": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
//                        break;
//                    }
//                    case "Z": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
//                        break;
//                    }
//                    case "F": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
//                        break;
//                    }
//                    case "D": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
//                        break;
//                    }
//                    case "C": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
//                        break;
//                    }
//                    default: {
//                    }
//                }
//            }
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "identityHashCode", "(Ljava/lang/Object;)I", false);
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "toHexString", "(I)Ljava/lang/String;", false);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//
//
//        }
//        else if(opcode == Opcodes.GETSTATIC) {
//            System.out.println(owner + name + "+" + descriptor);
//            if(owner.length() >= 4){
//                if(owner.substring(0,4).equals("jdk/")){
//                    return;
//                }
//                if(owner.substring(0,4).equals("sun/")){
//                    return;
//                }
//            }
//            if(owner.length() >= 5){
//                if(owner.substring(0,5).equals("java/")){
//                    return;
//                }
//            }
//            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J", false);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);
//
//
//            mv.visitInsn(Opcodes.POP);
//            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            mv.visitFieldInsn(Opcodes.GETSTATIC, owner, name, descriptor);
//            if(descriptor.length() == 1){
//                switch (descriptor){
//                    case "I": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
//                        break;
//                    }
//                    case "S": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
//                        break;
//                    }
//                    case "J": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
//                        break;
//                    }
//                    case "Z": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
//                        break;
//                    }
//                    case "F": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
//                        break;
//                    }
//                    case "D": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
//                        break;
//                    }
//                    case "C": {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
//                        break;
//                    }
//                    default: {
//                    }
//                }
//            }
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "identityHashCode", "(Ljava/lang/Object;)I", false);
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "toHexString", "(I)Ljava/lang/String;", false);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//            mv.visitFieldInsn(Opcodes.GETSTATIC, owner, name, descriptor);
//        }
//    }

//    MyMethodVisitor(int api, MethodVisitor mv) {
//        super(api, mv);
//    }

    private static void hack(MethodVisitor mv, String msg) {
        mv.visitFieldInsn(
                Opcodes.GETSTATIC,
                Type.getInternalName(System.class),
                "out",
                Type.getDescriptor(PrintStream.class)
        );
        mv.visitLdcInsn(msg);
        mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(PrintStream.class),
                "println",
                "(Ljava/lang/String;)V",
                false
        );
    }
    private static void mdds(MethodVisitor mv, String descriptor) {
        switch (descriptor){
                case "I": {
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                    break;
                }
                case "S": {
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
                    break;
                }
                case "J": {
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                    break;
                }
                case "Z": {
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                    break;
                }
                case "F": {
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                    break;
                }
                case "D": {
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                    break;
                }
                case "C": {
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                    break;
                }
                default: {
                }
            }
    }
    private static boolean calo(int opcode) {
        if(opcode == Opcodes.IALOAD){
            return true;
        }
        if(opcode == Opcodes.LALOAD){
            return true;
        }
        if(opcode == Opcodes.FALOAD){
            return true;
        }
        if(opcode == Opcodes.DALOAD){
            return true;
        }
        if(opcode == Opcodes.AALOAD){
            return true;
        }
        if(opcode == Opcodes.BALOAD){
            return true;
        }
        if(opcode == Opcodes.CALOAD){
            return true;
        }
        if(opcode == Opcodes.SALOAD){
            return true;
        }
        return false;

    }
    private static boolean calot(int opcode) {
        if(opcode == Opcodes.IASTORE){
            return true;
        }
        if(opcode == Opcodes.LASTORE){
            return true;
        }
        if(opcode == Opcodes.FASTORE){
            return true;
        }
        if(opcode == Opcodes.DASTORE){
            return true;
        }
        if(opcode == Opcodes.AASTORE){
            return true;
        }
        if(opcode == Opcodes.BASTORE){
            return true;
        }
        if(opcode == Opcodes.CASTORE){
            return true;
        }
        if(opcode == Opcodes.SASTORE){
            return true;
        }
        return false;

    }
}

