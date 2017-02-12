package com.devexperts.dxlab.lincheck.transformers;

import com.devexperts.dxlab.lincheck.Utils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import static org.objectweb.asm.Opcodes.*;

/**
 * Compute LocationId and inserts Utils.consumeCPU method call
 * before each xALOAD, xASTORE, GETFIELD, GETSTATIC, PUTFIELD, PUTSTATIC
 */
public class ConsumeCPUMethodTransformer extends MethodVisitor {


    private static final Type M_OWNER = Type.getType(Utils.class);
    private static final Method M_NAME = new Method("consumeCPU", Type.VOID_TYPE, new Type[]{Type.INT_TYPE});

    private final String className;
    private final String methodName;


    private final LocationManager lm = LocationManager.getInstance();
    private final GeneratorAdapter mv;

    private int line;


    public ConsumeCPUMethodTransformer(int api, MethodVisitor mv, String className,
                                       String methodName, int acces, String desc) {
        super(api, mv);
        this.className = className;
        this.methodName = methodName;
        this.mv = new GeneratorAdapter(mv, acces, methodName, desc);

    }

    @Override
    public void visitLineNumber(int line, Label start) {
        this.line = line;
        mv.visitLineNumber(line, start);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (!(opcode == ALOAD && var == 0)) {
            int id = lm.getLocationId(className, methodName, line);
            mv.push(id);
            mv.invokeStatic(M_OWNER, M_NAME);
        }
        mv.visitVarInsn(opcode, var);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        int id = lm.getLocationId(className, methodName, line);
        mv.push(id);
        mv.invokeStatic(M_OWNER, M_NAME);
        mv.visitFieldInsn(opcode, owner, name, desc);
    }
}
