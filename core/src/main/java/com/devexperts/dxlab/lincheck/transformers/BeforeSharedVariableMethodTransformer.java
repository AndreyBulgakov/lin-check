package com.devexperts.dxlab.lincheck.transformers;

import co.paralleluniverse.fibers.instrument.Retransform;
import com.devexperts.dxlab.lincheck.strategy.Strategy;
import com.devexperts.dxlab.lincheck.strategy.StrategyHolder;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 * MethodTransformer to insert StrategyHolder.currentStrategy.onSharedVariable method call
 *
 * @see Strategy
 * @see StrategyHolder
 */
class BeforeSharedVariableMethodTransformer extends GeneratorAdapter {

    private static final Type STRATEGYHOLDER_TYPE = Type.getType(StrategyHolder.class);
    private static final Method STRATEGYHOLDER_GET = new Method("getCurrentStrategy", Type.getType(Strategy.class), new Type[]{});
    private static final Type STRATEGY_ITF_TYPE = Type.getType(Strategy.class);
    private static final Method STRATEGY_ITF_METHOD_READ = new Method("onSharedVariableRead", Type.VOID_TYPE, new Type[]{Type.INT_TYPE});
    private static final Method STRATEGY_ITF_METHOD_WRITE = new Method("onSharedVariableWrite", Type.VOID_TYPE, new Type[]{Type.INT_TYPE});

    private final String className;
    private final String methodName;
    private final String methodDesc;

    private final LocationManager lm = LocationManager.getInstance();

    private int instructionNumber;

    BeforeSharedVariableMethodTransformer(int api, MethodVisitor mv, int access, String name, String desc, String className) {
        super(api, mv, access, name, desc);
        this.className = className;
        this.methodName = name;
        this.methodDesc = desc;
        Retransform.addWaiver(className.replace("/", "."), name);
    }


    @Override
    public void visitVarInsn(int opcode, int var) {
        this.instructionNumber++;
        switch (opcode) {
            case Opcodes.AALOAD:
            case Opcodes.LALOAD:
            case Opcodes.FALOAD:
            case Opcodes.DALOAD:
            case Opcodes.IALOAD:
                insertMethod(STRATEGY_ITF_METHOD_READ);
                break;
            case Opcodes.AASTORE:
            case Opcodes.IASTORE:
            case Opcodes.LASTORE:
            case Opcodes.FASTORE:
            case Opcodes.DASTORE:
                insertMethod(STRATEGY_ITF_METHOD_WRITE);
                break;
        }
        super.visitVarInsn(opcode, var);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        this.instructionNumber++;
        switch (opcode) {
            case Opcodes.GETSTATIC:
            case Opcodes.GETFIELD: { // TODO do not use brackets in switch statement
                insertMethod(STRATEGY_ITF_METHOD_READ);
                break;
            }

            case Opcodes.PUTSTATIC:
            case Opcodes.PUTFIELD: {
                insertMethod(STRATEGY_ITF_METHOD_WRITE);
                break;
            }
            default:
                break;
        }
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    private void insertMethod(Method method) {
        // Get or create current locationId
        int id = lm.getLocationId(className, methodName, methodDesc, instructionNumber);
        // Get current strategy
        invokeStatic(STRATEGYHOLDER_TYPE, STRATEGYHOLDER_GET);
        // Insert Strategy.onSharedVariableWrite interface method call
        push(id);
        invokeInterface(STRATEGY_ITF_TYPE, method);
    }
}
