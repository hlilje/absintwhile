package semant.amsyntax;

public class Fetch extends Inst {
    
    public final String x;
    
    public Fetch(String x) {
        super(Opcode.FETCH);
        this.x = x;
    }
    
    public String toString() {
        return super.toString() + "-" + x;
    }
}
