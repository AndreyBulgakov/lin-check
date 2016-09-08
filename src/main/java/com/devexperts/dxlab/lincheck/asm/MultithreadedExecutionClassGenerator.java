package com.devexperts.dxlab.lincheck.asm;

import org.objectweb.asm.*;

import java.lang.reflect.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;

import static org.objectweb.asm.Opcodes.*;

/**
 * Example of generated file
 * public class Generated10 extends Generated {
 * public Object testObject;
 * private Phaser phaser;
 * public Generated10(Object testObject, Phaser phaser) {
 * this.queue = queue;
 * this.phaser = phaser;
 * }
 * public void process(Result[] res, Object[][] args, int[] waits){
 * int a = (int)args[0][0].value;
 * phaser.arriveAndAwaitAdvance();
 * try{
 * busyWait.busyWait(waits[0]);
 * res[0].setValue(testObject.method(a));
 * }catch (Exception e) {
 * res[0].setException(e);
 * }
 * ...
 * phaser.arriveAndAwaitAdvance();
 * }
 * }
 */
public class MultithreadedExecutionClassGenerator {
    /**
     * Method to generate class for multithreaded execution
     *
     * @param test               Test class
     * @param phaser             Barrier synchronizer
     * @param pointedClassName   Name for generated class with dots
     * @param generatedClassName Name for generated class with slashes
     * @param methods            Test methods
     * @param parameters         Parameters for test methods
     * @return Class for multithreaded execution
     * @throws Exception
     */
    public static MultithreadedExecutionClass generate(Object test, Phaser phaser, String pointedClassName, String generatedClassName,
                                                       Method[] methods, Object[][] parameters) throws Exception {
        DynamicClassLoader loader = new DynamicClassLoader();

        Class<?> clz = loader.define(pointedClassName,
                MultithreadedExecutionClassGenerator.generateClass(generatedClassName, test.getClass().getCanonicalName().replace(".", "/"),
                        methods, parameters));

        Constructor<?> constructor = clz.getConstructor(test.getClass(), Phaser.class);
        Object[] constructorParameters = {test, phaser};
        return (MultithreadedExecutionClass) constructor.newInstance(constructorParameters);
    }

    private static class DynamicClassLoader extends ClassLoader {
        private Class<?> define(String className, byte[] bytecode) {
            return super.defineClass(className, bytecode, 0, bytecode.length);
        }
    }

    /**
     * Method generates class for a call from the parallel threads
     *
     * @param generatedClassName Name for generated class
     * @param testClassName      Name of test class
     * @param methods            Array of test methods
     * @param parameters         Array of parameters for test methods
     * @return Class for a call from the parallel threads
     * @throws Exception
     */
    private static byte[] generateClass(String generatedClassName, String testClassName, Method[] methods, Object[][] parameters) throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        FieldVisitor fv;
        MethodVisitor mv;
        Type[] x = methods[0].getGenericParameterTypes();
        cw.visit(52, ACC_PUBLIC + ACC_SUPER, generatedClassName, null, "java/lang/Object", new String[]{"com/devexperts/dxlab/lincheck/asm/MultithreadedExecutionClass"});

        fv = cw.visitField(ACC_PUBLIC, "field", "L" + testClassName + ";", null, null);
        fv.visitEnd();

        fv = cw.visitField(ACC_PRIVATE, "phaser", "Ljava/util/concurrent/Phaser;", null, null);
        fv.visitEnd();

        //Create class constructor
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(L" + testClassName + ";Ljava/util/concurrent/Phaser;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, generatedClassName, "field", "L" + testClassName + ";");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitFieldInsn(PUTFIELD, generatedClassName, "phaser", "Ljava/util/concurrent/Phaser;");
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();


        mv = cw.visitMethod(ACC_PUBLIC, "process", "([Lcom/devexperts/dxlab/lincheck/util/Result;[[Ljava/lang/Object;[I)V", null, null);
        mv.visitCode();
        Label[][] labelsForMethod = new Label[methods.length][3];
        for (int i = 0; i < labelsForMethod.length; i++) {
            for (int j = 0; j < labelsForMethod[i].length; j++) {
                labelsForMethod[i][j] = new Label();
            }
            mv.visitTryCatchBlock(labelsForMethod[i][0], labelsForMethod[i][1], labelsForMethod[i][2], "java/lang/Exception");
        }
        Label l12 = new Label();
        int border = 4;
        int[][] borders = new int[methods.length][];
        String[] methodnames = new String[methods.length];
        List<Object> parList = new ArrayList<>();
        parList.add(generatedClassName);
        parList.add("[Lcom/devexperts/dxlab/lincheck/util/Result;");
        parList.add("[[Ljava/lang/Object;");
        parList.add("[I");
        Type[][] types = new Type[methods.length][];
        Type[] returnTypes = new Type[methods.length];
        for (int i = 0; i < methods.length; i++) {
            borders[i] = new int[parameters[i].length];
            StringBuilder type = new StringBuilder();
            type.append("(");
            types[i] = methods[i].getGenericParameterTypes();
            for (int j = 0; j < parameters[i].length; j++) {
                mv.visitVarInsn(ALOAD, 2);
                mv.visitIntInsn(SIPUSH, i);
                mv.visitInsn(AALOAD);
                mv.visitIntInsn(SIPUSH, j);
                mv.visitInsn(AALOAD);
                if (types[i][j].equals(Boolean.TYPE)) {
                    mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
                    mv.visitVarInsn(ISTORE, border);
                    parList.add(Opcodes.INTEGER);
                    borders[i][j] = border;
                    border += 1;
                    type.append("Z");
                } else if (types[i][j].equals(Integer.TYPE)) {
                    mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
                    mv.visitVarInsn(ISTORE, border);
                    parList.add(Opcodes.INTEGER);
                    borders[i][j] = border;
                    border += 1;
                    type.append("I");
                } else if (types[i][j].equals(Short.TYPE)) {
                    mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
                    mv.visitVarInsn(ISTORE, border);
                    parList.add(Opcodes.INTEGER);
                    borders[i][j] = border;
                    border += 1;
                    type.append("S");
                } else if (types[i][j].equals(Long.TYPE)) {
                    mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
                    mv.visitVarInsn(LSTORE, border);
                    borders[i][j] = border;
                    border += 2;
                    parList.add(Opcodes.LONG);
                    type.append("J");
                } else if (types[i][j].equals(Byte.TYPE)) {
                    mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
                    mv.visitVarInsn(ISTORE, border);
                    parList.add(Opcodes.INTEGER);
                    borders[i][j] = border;
                    border += 1;
                    type.append("B");
                } else if (types[i][j].equals(Float.TYPE)) {
                    mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
                    mv.visitVarInsn(FSTORE, border);
                    parList.add(Opcodes.FLOAT);
                    borders[i][j] = border;
                    border += 1;
                    type.append("F");
                } else if (types[i][j].equals(Double.TYPE)) {
                    mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
                    mv.visitVarInsn(DSTORE, border);
                    borders[i][j] = border;
                    border += 2;
                    parList.add(Opcodes.DOUBLE);
                    type.append("D");
                } else if (types[i][j].equals(Character.TYPE)) {
                    mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
                    mv.visitVarInsn(ISTORE, border);
                    parList.add(Opcodes.INTEGER);
                    borders[i][j] = border;
                    border += 1;
                    type.append("C");
                } else {
                    String parameterTypeName = methods[i].getParameterTypes()[j].getName().replace(".", "/");
                    if (parameterTypeName.contains("[")) {
                        mv.visitTypeInsn(CHECKCAST, parameterTypeName);
                        mv.visitTypeInsn(CHECKCAST, parameterTypeName);
                        parList.add(parameterTypeName);
                        type.append(parameterTypeName);
                    } else {
                        mv.visitTypeInsn(CHECKCAST, parameterTypeName);
                        parList.add(parameterTypeName);
                        type.append("L").append(parameterTypeName).append(";");
                    }
                    mv.visitVarInsn(ASTORE, border);
                    borders[i][j] = border;
                    border += 1;
                }
            }
            type.append(")");

            returnTypes[i] = methods[i].getGenericReturnType();
            if (returnTypes[i] == Void.TYPE)
                type.append("V");
            else if (returnTypes[i].equals(Boolean.TYPE))
                type.append("Z");
            else if (returnTypes[i].equals(Integer.TYPE))
                type.append("I");
            else if (returnTypes[i].equals(Short.TYPE))
                type.append("S");
            else if (returnTypes[i].equals(Long.TYPE))
                type.append("J");
            else if (returnTypes[i].equals(Byte.TYPE))
                type.append("B");
            else if (returnTypes[i].equals(Float.TYPE))
                type.append("F");
            else if (returnTypes[i].equals(Double.TYPE))
                type.append("D");
            else if (returnTypes[i].equals(Character.TYPE))
                type.append("C");
            else {
                String parameterTypeName = methods[i].getReturnType().getName().replace(".", "/");
                if (parameterTypeName.contains("["))
                    type.append(parameterTypeName);
                else
                    type.append("L").append(parameterTypeName).append(";");
            }
            methodnames[i] = type.toString();
        }
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, generatedClassName, "phaser", "Ljava/util/concurrent/Phaser;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/concurrent/Phaser", "arriveAndAwaitAdvance", "()I", false);
        mv.visitInsn(POP);


        for (int i = 0; i < methods.length; i++) {

            mv.visitLabel(labelsForMethod[i][0]);
            if (i > 0)
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

            mv.visitVarInsn(ALOAD, 3);
            mv.visitIntInsn(SIPUSH, i);
            mv.visitInsn(IALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "com/devexperts/dxlab/lincheck/util/MyRandom", "busyWait", "(I)V", false);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, i);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, generatedClassName, "field", "L" + testClassName + ";");
            for (int j = 0; j < parameters[i].length; j++) {
                if (types[i][j].equals(Boolean.TYPE) || types[i][j].equals(Integer.TYPE) || types[i][j].equals(Short.TYPE) ||
                        types[i][j].equals(Long.TYPE) || types[i][j].equals(Byte.TYPE) || types[i][j].equals(Character.TYPE))
                    mv.visitVarInsn(ILOAD, borders[i][j]);
                else if (types[i][j].equals(Float.TYPE))
                    mv.visitVarInsn(FLOAD, borders[i][j]);
                else if (types[i][j].equals(Double.TYPE))
                    mv.visitVarInsn(DLOAD, borders[i][j]);
                else
                    mv.visitVarInsn(ALOAD, borders[i][j]);
            }
            String s = methods[i].getReturnType().getName().replace(".", "/");
            mv.visitMethodInsn(INVOKEVIRTUAL, testClassName, methods[i].getName(), methodnames[i], false);
            if (returnTypes[i].equals(Boolean.TYPE))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
            else if (returnTypes[i].equals(Integer.TYPE))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            else if (returnTypes[i].equals(Short.TYPE))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
            else if (returnTypes[i].equals(Long.TYPE))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
            else if (returnTypes[i].equals(Byte.TYPE))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
            else if (returnTypes[i].equals(Float.TYPE))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
            else if (returnTypes[i].equals(Double.TYPE))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
            else if (returnTypes[i].equals(Character.TYPE))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
            if (s.equals("void")) {
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setVoid", "()V", false);
            } else {
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setValue", "(Ljava/lang/Object;)V", false);
            }
            mv.visitLabel(labelsForMethod[i][1]);
            if (i < labelsForMethod.length - 1) {
                mv.visitJumpInsn(GOTO, labelsForMethod[i + 1][0]);
            } else {
                mv.visitJumpInsn(GOTO, l12);
            }
            mv.visitLabel(labelsForMethod[i][2]);
            if (i == 0)
                mv.visitFrame(Opcodes.F_FULL, parList.size(), parList.toArray(), 1, new Object[]{"java/lang/Exception"});
            else
                mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, border);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(BIPUSH, i);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, border);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setException", "(Ljava/lang/Exception;)V", false);
        }
        mv.visitLabel(l12);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, generatedClassName, "phaser", "Ljava/util/concurrent/Phaser;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/concurrent/Phaser", "arriveAndAwaitAdvance", "()I", false);
        mv.visitInsn(POP);
        mv.visitInsn(RETURN);
        mv.visitMaxs(4, 5);
        mv.visitEnd();


        cw.visitEnd();

        return cw.toByteArray();
    }
}
