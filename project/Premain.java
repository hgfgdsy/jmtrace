import java.lang.instrument.Instrumentation;
import java.lang.ClassNotFoundException;
import java.lang.instrument.UnmodifiableClassException;
public class Premain {
    public static void premain(String agentArgs, Instrumentation inst)
            throws ClassNotFoundException, UnmodifiableClassException {
        inst.addTransformer(new APPtrans());
    }
}
