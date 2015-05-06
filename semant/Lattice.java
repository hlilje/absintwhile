package semant;

public abstract class Lattice<A> {
    
    public final A join;
    public final A meet;
    
    protected Lattice(A join, A meet) {
        this.join = join;
        this.meet = meet;
    }
    
    public abstract A lub(A a1, A a2);
    public abstract A glb(A a1, A a2);
}
