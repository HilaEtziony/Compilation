/***********/
/* PACKAGE */
/***********/
package ir;

import mips.MipsGenerator;
import java.util.*;
import temp.*;

public class IrCommandMalloc extends IrCommand
{
    public Temp dst;   // Get the address of the allocated memory in this temp
    public Temp size;  // The size of the memory to allocate (in bytes)

    public IrCommandMalloc(Temp dst, Temp size)
    {
        this.dst = dst;
        this.size = size;
    }

    @Override
    public Set<Temp> use() { 
        return Collections.singleton(size); 
    }

    @Override
    public Set<Temp> def() { 
        return Collections.singleton(dst); 
    }

    @Override
    public String toString()
    {
        return String.format("%s = Malloc(%s)", dst, size);
    }

    /***************/
    /* MIPS me !!! */
    /***************/
    public void mipsMe()
    {
        MipsGenerator.getInstance().malloc(dst, size);
    }
}