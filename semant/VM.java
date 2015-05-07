package semant;

import semant.amsyntax.*;
import semant.signexc.*;

public class VM {

    private static boolean DEBUG;

    private SignExcOps op;       // Type of operations to use
    private Code code;           // Code to be excuted
    private Configuration conf;  // Current state
    private int stepCounter = 0; // Current code step

    public VM(Code code, boolean debug) {
        this.code = code;
        DEBUG = debug;
        op = new SignExcOps();
        conf = new Configuration();
    }

    /**
     * Execute one step of the code with the given Configuration `conf`.
     * Return the resulting configuration.
     */
    private Configuration step(Configuration conf) {
        if (DEBUG) System.out.println(conf);

        Inst inst = code.get(stepCounter);
        if (DEBUG) System.out.println("> " + inst.opcode);

        Code c1, c2, c1_2, c2_2;
        SignExc a, a1, a2;
        TTExc b, b1, b2;
        switch (inst.opcode) {
            case ADD:
                a1 = (SignExc) conf.popStack();
                a2 = (SignExc) conf.popStack();
                conf.pushStack(op.add(a1, a2));
                break;
            case AND:
                b1 = (TTExc) conf.popStack();
                b2 = (TTExc) conf.popStack();
                conf.pushStack(op.and(b1, b2));
                break;
            case BRANCH:
                b = (TTExc) conf.popStack();
                if (!conf.isExceptional())
                    if (b == op.abs(true)) code.addAll(stepCounter + 1, ((Branch) inst).c1);
                    else code.addAll(stepCounter + 1, ((Branch) inst).c2);
                break;
            case EQ:
                a1 = (SignExc) conf.popStack();
                a2 = (SignExc) conf.popStack();
                conf.pushStack(op.eq(a1, a2));
                break;
            case FALSE:
                conf.pushStack(op.abs(false));
                break;
            case FETCH:
                a = conf.getVar(((Fetch) inst).x);
                conf.pushStack(a);
                break;
            case LE:
                a1 = (SignExc) conf.popStack();
                a2 = (SignExc) conf.popStack();
                conf.pushStack(op.leq(a1, a2));
                break;
            case LOOP:
                c1 = ((Loop) inst).c1;
                c2 = ((Loop) inst).c2;
                c1_2 = new Code();
                c2_2 = new Code();
                c1_2.addAll(c2);
                c1_2.add(new Loop(c1, c2));
                c2_2.add(new Noop());
                // Insert new code at current position in code
                code.addAll(stepCounter + 1, c1);
                code.add(stepCounter + c1.size() + 1, new Branch(c1_2, c2_2));
                break;
            case MULT:
                a1 = (SignExc) conf.popStack();
                a2 = (SignExc) conf.popStack();
                conf.pushStack(op.multiply(a1, a2));
                break;
            case NEG:
                b = (TTExc) conf.popStack();
                conf.pushStack(op.neg(b));
                break;
            case NOOP:
                break;
            case PUSH:
                conf.pushStack(op.abs(((Push) inst).getValue()));
                break;
            case STORE:
                a = (SignExc) conf.popStack();
                if (!conf.isExceptional())
                    conf.setVar(((Store) inst).x, a);
                break;
            case SUB:
                a1 = (SignExc) conf.popStack();
                a2 = (SignExc) conf.popStack();
                conf.pushStack(op.subtract(a1, a2));
                break;
            case TRUE:
                conf.pushStack(op.abs(true));
                break;
            case DIV:
                a1 = (SignExc) conf.popStack();
                a2 = (SignExc) conf.popStack();
                conf.pushStack(op.divide(a1, a2));
                break;
            case TRY:
                c1 = ((Try) inst).c1;
                c2 = ((Try) inst).c2;
                // dont catch outer exceptions
                if (c1 != null && conf.isExceptional())
                    break;
                if (c1 == null) {
                    if (conf.isExceptional()) {
                        if (DEBUG) System.out.println("CATCH EXCEPTION");
                        conf.setExceptional(false);
                        code.addAll(stepCounter + 1, c2);
                    }
                } else {
                    code.addAll(stepCounter + 1, c1);
                    code.add(stepCounter + c1.size() + 1, new Try(null, c2));
                }
                break;
            default:
                System.err.println("Invalid opcode");
                System.exit(1);
                break;
        }

        return conf;
    }

    /**
     * Perform one execute step of the VM, return `false`
     * if no more code can be executed.
     */
    public boolean executeStep() {
        // Break execution when no more code is available.
        if (stepCounter == code.size()) {
            if (DEBUG) System.out.println(">>> END");
            System.out.println("======= Final Configuration ======");
            System.out.println();
            System.out.println(conf);
            return false;
        }

        // Execute one step with the current configuration
        conf = step(conf);
        ++stepCounter;

        return true;
    }
}
