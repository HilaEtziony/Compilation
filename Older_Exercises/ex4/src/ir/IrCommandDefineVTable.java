package ir;

import types.TypeList;
import types.TypeFunction;

public class IrCommandDefineVTable extends IrCommand {
    public String className;
    public TypeList methods;

    public IrCommandDefineVTable(String className, TypeList methods) {
        this.className = className;
        this.methods = methods;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("VTable_%s:", className));
        
        TypeList it = methods;
        while (it != null) {
            if (it.head instanceof TypeFunction) {
                TypeFunction func = (TypeFunction) it.head;

                sb.append(String.format("\n\t.word %s", func.name));
            }
            it = it.tail;
        }
        return sb.toString();
    }
}