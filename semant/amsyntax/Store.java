package semant.amsyntax;

public class Store extends Inst {
    
    public final String x;
    
    public Store(String x) {
        super(Opcode.STORE);
        this.x = x;
    }
    
    public String toString() {
        return super.toString() + "-" + x;
    }
}
