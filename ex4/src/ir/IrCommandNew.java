package ir;

import temp.Temp;

public class IrCommandNew extends IrCommand {
    public Temp dst;
    public String className; 
    public Temp arraySize;   

    // Constructor for Class
    public IrCommandNew(Temp dst, String className) {
        this.dst = dst;
        this.className = className;
    }

    // Constructor for Array
    public IrCommandNew(Temp dst, Temp arraySize) {
        this.dst = dst;
        this.arraySize = arraySize;
    }
}