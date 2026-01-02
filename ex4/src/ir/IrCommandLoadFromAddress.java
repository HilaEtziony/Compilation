package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import temp.*;

public class IrCommandLoadFromAddress extends IrCommand {
    public Temp dst;
    public Temp address; // This holds the dynamic address we calculated

    public IrCommandLoadFromAddress(Temp dst, Temp address) {
        this.dst = dst;
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("%s := *%s", dst, address);
    }
}