package com.devexperts.dxlab.lincheck.transformers;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import static org.objectweb.asm.Opcodes.ALOAD;

/**
 * Compute LocationId and inserts Thread.yield method call
 * before each xALOAD, xASTORE, GETFIELD, GETSTATIC, PUTFIELD, PUTSTATIC
 */
public class ThreadYieldMethodTransformer extends MethodVisitor{

    private final GeneratorAdapter mv;
    private static final Type M_OWNER = Type.getType(Thread.class);
    private static final Method M_NAME = new Method("yield", Type.VOID_TYPE, new Type[]{});



    public ThreadYieldMethodTransformer(int api, MethodVisitor mv, String methodName, int acces, String desc) {
        super(api, mv);
        this.mv = new GeneratorAdapter(mv, acces, methodName, desc);

    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (!(opcode == ALOAD && var == 0)) {
            mv.invokeStatic(M_OWNER, M_NAME);
        }
        mv.visitVarInsn(opcode, var);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        mv.invokeStatic(M_OWNER, M_NAME);
        mv.visitFieldInsn(opcode, owner, name, desc);
    }
}
