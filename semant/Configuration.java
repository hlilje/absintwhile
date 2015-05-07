package semant;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;
import semant.amsyntax.*;

public class Configuration<T> {

    private HashMap<String, T> symTable;
    private Stack<T> stack;
    private Code code;
    private boolean exceptional;

    public Configuration() {
        symTable = new HashMap<String, T>();
        stack = new Stack<T>();
        exceptional = false;
    }

    /**
     * Return a clone of this configuration,
     * i.e. a configuration with the same
     * state, stack, and machine code.
     */
    public Configuration clone() {
        Configuration clone = new Configuration<T>();
        // clone state
        clone.symTable = (HashMap<String, T>) symTable.clone();
        // clone stack
        clone.stack = (Stack<T>) stack.clone();
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
    public boolean equals(Object o) {
        if(!(o instanceof Configuration))
            return false;

        Configuration oc = (Configuration) o;
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
    public void pushStack(T val) {
        stack.push(val);
    }

    /**
     * Pop one value from the stack.
     */
    public T popStack() {
        return stack.pop();
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
        for (ListIterator<T> it = stack.listIterator(stack.size());
                it.hasPrevious();) {
            sb.append(it.previous() + "\n");
        }

        return sb.toString();
    }
}
