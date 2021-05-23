package agent;

import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class MainTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {

        try {
           if (className.equals("main/tasks/TaskRunner")){

               System.out.println(className);
               System.out.println("this");
           }
            if ("main/tasks/TaskRunner".equals(className)) {
                System.out.println("***********");
                ClassPool pool = ClassPool.getDefault();

                CtClass clazz = pool.get("main.tasks.TaskRunner");

                CtField startTiming = CtField.make("private static long timeStart = 0L;", clazz);
                clazz.addField(startTiming);

                CtField stopTiming = CtField.make("private static long timeStop = 0L;", clazz);
                clazz.addField(stopTiming);

//                CtField test = CtField.make("private static int test = 2;", clazz);
//                clazz.addField(test);

                CtField timeElapsed = CtField.make("private static long elapsedTime = 0L;", clazz);
                clazz.addField(timeElapsed);
                CtMethod startTask = CtMethod.make("""
                        private static void startTask(){
                            System.out.println("before start");                           
                            timeStart = System.currentTimeMillis();
                        }
                        """, clazz);
                CtMethod stopTask = CtMethod.make("""
                        private static void stopTask(){
                            timeStop = System.currentTimeMillis();
                            elapsedTime = (timeStop - timeStart);
                            System.out.println("[profile] task time elapse: " + elapsedTime + " ms");
//                            System.out.println(test);
                        }
                        """, clazz);

                clazz.addMethod(startTask);
                clazz.addMethod(stopTask);

                clazz.getDeclaredMethod("run").insertBefore("startTask();");
//                clazz.getDeclaredMethod("run").insertAfter("stopTask();");
                clazz.getDeclaredMethod("run").insertAt(23, "stopTask();");


//                CtMethod newMethod = CtMethod.make("""
//                        private static void newMethod() {
//                            System.out.println("x + y + z = " + (z + x + y));
//                            x += 10;
//                            y += 20;
//                            z += 10;
//                            System.out.println("after update: x + y + z  = " + (z + x + y));
//                        }
//                        """, clazz);
//                clazz.addMethod(newMethod);
//                clazz.getDeclaredMethod("run").insertAfter("newMethod();");
//.insertAt(20, "newMethod();");//
                return clazz.toBytecode();
            }
            else {
                return classfileBuffer;
            }

        }
        catch (Throwable e) {
            System.out.println("After if and else in catch block");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
