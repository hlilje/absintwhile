package semant;

import semant.amsyntax.*;
import semant.signexc.*;

public class VM<T,E> {

    private static boolean DEBUG;

    private Operations<T, E> op;   // Type of operations to use
    private Code code;             // Code to be excuted
    private Configuration<T, E> conf; // Current state
    private int stepCounter = 0;   // Current code step

    public VM(Operations<T, E> op, Code code, boolean debug) {
        this.op = op;
        this.code = code;
        DEBUG = debug;
        conf = new Configuration<T, E>();
    }

    /**
     * Execute one step of the code with the given Configuration `conf`.
     * Return the resulting configuration.
     */
    private Configuration<T, E> step(Configuration<T, E> conf) {
        if (DEBUG) System.out.println(conf);

        Inst inst = code.get(stepCounter);
        if (DEBUG) System.out.println("> " + inst.opcode);

        Code c1, c2, c1_2, c2_2;
        T a, a1, a2;
        E b, b1, b2;
        switch (inst.opcode) {
            case ADD:
                a1 = conf.popStackInt();
                a2 = conf.popStackInt();
                conf.pushStackInt(op.add(a1, a2));
                break;
            case AND:
                b1 = conf.popStackBool();
                b2 = conf.popStackBool();
                conf.pushStackBool(op.and(b1, b2));
                break;
            case BRANCH:
                b = conf.popStackBool();
                if (!conf.isExceptional())
                    if (b == op.abs(true)) code.addAll(stepCounter + 1, ((Branch) inst).c1);
                    else code.addAll(stepCounter + 1, ((Branch) inst).c2);
                break;
            case EQ:
                a1 = conf.popStackInt();
                a2 = conf.popStackInt();
                conf.pushStackBool(op.eq(a1, a2));
                break;
            case FALSE:
                conf.pushStackBool(op.abs(false));
                break;
            case FETCH:
                a = conf.getVar(((Fetch) inst).x);
                conf.pushStackInt(a);
                break;
            case LE:
                a1 = conf.popStackInt();
                a2 = conf.popStackInt();
                conf.pushStackBool(op.leq(a1, a2));
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
                a1 = conf.popStackInt();
                a2 = conf.popStackInt();
                conf.pushStackInt(op.multiply(a1, a2));
                break;
            case NEG:
                b = conf.popStackBool();
                conf.pushStackBool(op.neg(b));
                break;
            case NOOP:
                break;
            case PUSH:
                conf.pushStackInt(op.abs(((Push) inst).getValue()));
                break;
            case STORE:
                a = conf.popStackInt();
                if (!conf.isExceptional())
                    conf.setVar(((Store) inst).x, a);
                break;
            case SUB:
                a1 = conf.popStackInt();
                a2 = conf.popStackInt();
                conf.pushStackInt(op.subtract(a1, a2));
                break;
            case TRUE:
                conf.pushStackBool(op.abs(true));
                break;
            case DIV:
                a1 = conf.popStackInt();
                a2 = conf.popStackInt();
                conf.pushStackInt(op.divide(a1, a2));
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
