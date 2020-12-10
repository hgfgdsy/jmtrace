import org.objectweb.asm.Opcodes;

public class mtrace {
    public static void traceWritestatic(Object obj, String name){
        System.out.println("W\t" + (Thread.currentThread()).getId() + "\t" + fullhex(name) +"\t"+name);
    }
    public static void traceWritefield(Object obj, String name){
        System.out.println("W\t" + (Thread.currentThread()).getId() + "\t" + fullhex(obj) +"\t"+name);
    }
    public static void traceReadstatic(Object obj, String name){
        System.out.println("R\t" + (Thread.currentThread()).getId() + "\t" + fullhex(name) +"\t"+name);
    }
    public static void traceReadfield(Object obj, String name){
        System.out.println("R\t" + (Thread.currentThread()).getId() + "\t" + fullhex(obj) +"\t"+name);
    }

    public static String fullhex(Object obj){
        String pad = "00000000";
        String hex = Integer.toHexString(System.identityHashCode(obj));

        int space = 8 - hex.length();
        return pad.substring(0,space) + hex;

    }

    public static void traceaload(Object obj, int index){
        System.out.print("R\t" + (Thread.currentThread()).getId() + "\t" + fullhex(obj) +"\t");
        String type = obj.toString();
        int dim = 0;
        while(type.charAt(dim) == '['){
            dim++;
        }
        if (dim == 1){
            choose(type.charAt(1), index);
            return;
        }
        System.out.println("java.lang.Object["+index+"]");
    }
    public static void traceastore(Object obj, int index){
        System.out.print("W\t" + (Thread.currentThread()).getId() + "\t" + fullhex(obj) +"\t");
        String type = obj.toString();
        int dim = 0;
        while(type.charAt(dim) == '['){
            dim++;
        }
        if (dim == 1){
            choose(type.charAt(1), index);
            return;
        }
        System.out.println("java.lang.Object["+index+"]");
    }

    public static void choose(char c, int index){
        switch (c){
            case 'I': {
                System.out.println("int["+index+"]");
                break;
            }
            case 'S': {
                System.out.println("short["+index+"]");
                break;
            }
            case 'J': {
                System.out.println("long["+index+"]");
                break;
            }
            case 'Z': {
                System.out.println("boolean["+index+"]");
                break;
            }
            case 'F': {
                System.out.println("float["+index+"]");
                break;
            }
            case 'D': {
                System.out.println("double["+index+"]");
                break;
            }
            case 'C': {
                System.out.println("char["+index+"]");
                break;
            }
            default: {
                System.out.println("java.lang.Object["+index+"]");
            }
        }
    }
}
