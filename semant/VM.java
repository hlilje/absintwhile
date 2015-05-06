package semant;

import semant.amsyntax.*;

public class VM {

    // Value to represent an error
    private static final int BOT = 0;
    private static boolean DEBUG;

    private Code code;           // Code to be excuted
    private Configuration conf;  // Current state
    private int stepCounter = 0; // Current code step

    public VM(Code code, boolean debug) {
        this.code = code;
        DEBUG = debug;
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
        int a, a1, a2;
        switch (inst.opcode) {
            case ADD:
                a1 = conf.popStack();
                a2 = conf.popStack();
                if (conf.isExceptional())
                    conf.pushStack(BOT);
                else
                    conf.pushStack(a1 + a2);
                break;
            case AND:
                a1 = conf.popStack();
                a2 = conf.popStack();
                if (conf.isExceptional())
                    conf.pushStack(BOT);
                else
                    conf.pushStack((a1 == 1) && (a2 == 1) ? 1 : 0);
                break;
            case BRANCH:
                a = conf.popStack();
                if (!conf.isExceptional())
                    if (a == 1) code.addAll(stepCounter + 1, ((Branch) inst).c1);
                    else code.addAll(stepCounter + 1, ((Branch) inst).c2);
                break;
            case EQ:
                a1 = conf.popStack();
                a2 = conf.popStack();
                if (conf.isExceptional())
                    conf.pushStack(BOT);
                else
                    conf.pushStack(a1 == a2 ? 1 : 0);
                break;
            case FALSE:
                conf.pushStack(0); // False
                break;
            case FETCH:
                a = conf.getVar(((Fetch) inst).x);
                conf.pushStack(a);
                break;
            case LE:
                a1 = conf.popStack();
                a2 = conf.popStack();
                if (conf.isExceptional())
                    conf.pushStack(BOT);
                else
                    conf.pushStack(a1 <= a2 ? 1 : 0);
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
                a1 = conf.popStack();
                a2 = conf.popStack();
                if (conf.isExceptional())
                    conf.pushStack(BOT);
                else
                    conf.pushStack(a1 * a2);
                break;
            case NEG:
                a = conf.popStack();
                if (conf.isExceptional())
                    conf.pushStack(BOT);
                else
                    conf.pushStack((a == 1) ? 0 : 1);
                break;
            case NOOP:
                break;
            case PUSH:
                conf.pushStack(((Push) inst).getValue());
                break;
            case STORE:
                a = conf.popStack();
                if (!conf.isExceptional())
                    conf.setVar(((Store) inst).x, a);
                break;
            case SUB:
                a1 = conf.popStack();
                a2 = conf.popStack();
                if (conf.isExceptional())
                    conf.pushStack(BOT);
                else
                    conf.pushStack(a1 - a2);
                break;
            case TRUE:
                conf.pushStack(1); // True
                break;
            case DIV:
                a1 = conf.popStack();
                a2 = conf.popStack();
                if (a2 == 0) {
                    if (DEBUG) System.out.println("THROW EXCEPTION");
                    conf.setExceptional(true);
                    conf.pushStack(BOT);
                } else
                    conf.pushStack(a1 / a2);
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
