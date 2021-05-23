package agent;

import java.lang.instrument.Instrumentation;

public class Premainer {
    public static void premain(String args, Instrumentation instrumentation) {
        instrumentation.addTransformer(new MainTransformer());
    }
}
