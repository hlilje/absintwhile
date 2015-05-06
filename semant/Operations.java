package semant;

public interface Operations<A, B> {
    
    public A abs(Integer z);
    public B abs(boolean b);
    
    public A add(A a1, A a2);
    public A subtract(A a1, A a2);
    public A multiply(A a1, A a2);
    public A divide(A a1, A a2);
    public B eq(A a1, A a2);
    public B leq(A a1, A a2);
    public B and(B b1, B b2);
    public B neg(B b);
    
    public boolean possiblyAErr(A a);
    public boolean possiblyBErr(B b);
    public boolean possiblyTrue(B b);
    public boolean possiblyFalse(B b);
    public boolean possiblyInt(A a);
    
    public boolean isInt(A a);
}
