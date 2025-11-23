package ast;

public class AstDecFunc extends AstDec
{
    public AstVarType return_type;
    public String identifier;
    public AstTypeIdList func_input; // might be null - don't forget to check whenever using
    public AstStmtList stmnts_of_funs;

    public AstDecFunc(AstVarType return_type, String identifier, AstTypeIdList func_input, AstStmtList stmnts_of_funs){
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();

        this.return_type = return_type;
        this.identifier = identifier;
        this.func_input = func_input;
        this.stmnts_of_funs = stmnts_of_funs;
    }
}

/*
accepts:

RESULT = new AstDecFunc(t,i,l,s);
RESULT = new AstDecFunc(t,i,null,s);
*/