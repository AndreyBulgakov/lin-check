package com.devexperts.dxlab.lincheck.transformers;

import com.devexperts.dxlab.lincheck.strategy.Strategy;
import com.devexperts.dxlab.lincheck.strategy.StrategyHolder;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import static org.objectweb.asm.Opcodes.ALOAD;

// TODO rename class
// TODO public?
public class BeforeSharedVariableMethodTransformer extends GeneratorAdapter {

    private static final Type STRATEGYHOLDER_TYPE = Type.getType(StrategyHolder.class);
    private static final Method STRATEGYHOLDER_GET = new Method("getCurrentStrategy", Type.getType(Strategy.class), new Type[]{});
    private static final Type STRATEGY_ITF_TYPE = Type.getType(Strategy.class);
    private static final Method STRATEGY_ITF_METHOD = new Method("onSharedVariableAccess", Type.VOID_TYPE, new Type[]{Type.INT_TYPE});
    // TODO remove commented code
//    private static final Type STRATEGYHOLDER_TYPE = Type.getType(Utils.class);
//    private static final Method STRATEGYHOLDER_GET = new Method("consumeCPU", Type.VOID_TYPE, new Type[]{Type.INT_TYPE});


    private final String className;
    private final String methodName;
    private final ClassLoader loader;

    private final LocationManager lm = LocationManager.getInstance();

    private int line;

    // TODO remove unused constructors
    public BeforeSharedVariableMethodTransformer(MethodVisitor mv, int access, String name, String desc, String className, ClassLoader loader) {
        super(mv, access, name, desc);
        this.className = className;
        this.methodName = name;
        this.loader = loader;
    }

    // TODO is it really needful to pass all these parameters?
    public BeforeSharedVariableMethodTransformer(int api, MethodVisitor mv, int access, String name, String desc, String className, ClassLoader loader) {
        super(api, mv, access, name, desc);
        this.className = className;
        this.methodName = name;
        this.loader = loader;
    }

    public BeforeSharedVariableMethodTransformer(int access, Method method, MethodVisitor mv, String className, ClassLoader loader) {
        super(access, method, mv);
        this.className = className;
        this.methodName = method.getName();
        this.loader = loader;
    }

    public BeforeSharedVariableMethodTransformer(int access, Method method, String signature, Type[] exceptions, ClassVisitor cv, String className, ClassLoader loader) {
        super(access, method, signature, exceptions, cv);
        this.className = className;
        this.methodName = method.getName();
        this.loader = loader;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        this.line = line;
        mv.visitLineNumber(line, start);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (!(opcode == ALOAD && var == 0)) { // TODO hard to read
            int id = lm.getLocationId(loader, className, methodName, line);
            super.invokeStatic(STRATEGYHOLDER_TYPE, STRATEGYHOLDER_GET);
            super.push(id);
            super.invokeInterface(STRATEGY_ITF_TYPE, STRATEGY_ITF_METHOD);
        }
        mv.visitVarInsn(opcode, var);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        int id = lm.getLocationId(loader, className, methodName, line);
        // TODO use mv instead of super (it's simplier to read)
        super.invokeStatic(STRATEGYHOLDER_TYPE, STRATEGYHOLDER_GET);
        super.push(id);
        super.invokeInterface(STRATEGY_ITF_TYPE, STRATEGY_ITF_METHOD);
        mv.visitFieldInsn(opcode, owner, name, desc);
    }
}
