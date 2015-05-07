package semant;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;
import semant.amsyntax.*;

public class Configuration<T, E> {

    private HashMap<String, T> symTable;
    private Stack<Integer> stack;
    private Code code;
    private boolean exceptional;

    public Configuration() {
        symTable = new HashMap<String, T>();
        stack = new Stack<Integer>();
        exceptional = false;
    }

    /**
     * Return a clone of this configuration,
     * i.e. a configuration with the same
     * state, stack, and machine code.
     */
    @SuppressWarnings("unchecked")
    public Configuration<T, E> clone() {
        Configuration<T, E> clone = new Configuration<T, E>();
        // clone state
        clone.symTable = (HashMap<String, T>) symTable.clone();
        // clone stack
        clone.stack = (Stack<Integer>) stack.clone();
        // clone code
        clone.code = (Code) code.clone();
        return clone;
    }

    /**
     * Return the hashCode for this configuration.
     */
    public int hashCode() {
        return symTable.hashCode() ^ stack.hashCode() ^ code.hashCode();
    }

    /**
     * Check if two configurations are the same.
     */
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if(!(o instanceof Configuration))
            return false;

        Configuration<T, E> oc = (Configuration<T, E>) o;
        return oc.symTable.equals(symTable) &&
               oc.stack.equals(stack) && oc.code.equals(code);
    }

    /**
     * Set the code of this configuration.
     */
    public void setCode(Code c) {
        code = c;
    }

    /**
     * Get the code of this configuration.
     */
    public Code getCode() {
        return code;
    }

    /**
     * Set the value of the given variable.
     */
    public void setVar(String var, T val) {
        symTable.put(var, val);
    }

    /**
     * Get the value of the given variable.
     */
    public T getVar(String var) {
        return symTable.get(var);
    }

    /**
     * Push the given value on the stack.
     */
    public void pushStackInt(T val) {
        stack.push((Integer) val);
    }

    /**
     * Push the given value on the stack.
     */
    public void pushStackBool(E val) {
        stack.push((Integer) val);
    }

    /**
     * Pop one value from the stack.
     */
    @SuppressWarnings("unchecked")
    public T popStackInt() {
        return (T) stack.pop();
    }

    /**
     * Pop one value from the stack.
     */
    @SuppressWarnings("unchecked")
    public E popStackBool() {
        return (E) stack.pop();
    }

    /**
     * Return whether the configuration is in an exceptional state.
     */
    public boolean isExceptional() {
        return exceptional;
    }

    /**
     * Set the exceptional status of this configuration.
     */
    public void setExceptional(boolean exceptional) {
        this.exceptional = exceptional;
    }

    /**
     * Return a string representation of this configuration.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("========== Symbol Table ==========\n");
        for (Map.Entry<String, T> entry : symTable.entrySet()) {
            String var = entry.getKey();
            T val = entry.getValue();
            sb.append(var + ": " + val + "\n");
        }

        sb.append("\n");

        sb.append("======== Stack (Top-Down) ========\n");
        // Java's stack iterator is reversed
        for (ListIterator<Integer> it = stack.listIterator(stack.size());
                it.hasPrevious();) {
            sb.append(it.previous() + "\n");
        }

        return sb.toString();
    }
}
