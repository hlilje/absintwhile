package semant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import semant.amsyntax.*;
import semant.signexc.*;

public class VM {

    private static boolean DEBUG;
    private static boolean STEP;

    private SignExcOps op;                   // Type of operations to use
    private HashSet<Configuration> visited;  // Visited configurations
    private LinkedList<Configuration> queue; // BFS queue
    private int controlPoint;

    public VM(Code code, boolean debug, boolean step) {
        DEBUG = debug;
        STEP = step;
        op = new SignExcOps();
        visited = new HashSet<Configuration>();
        queue = new LinkedList<Configuration>();
        controlPoint = 0;

        Configuration conf = new Configuration();
        conf.setCode(code);
        queue.add(conf);
    }

    /**
     * Execute one step of the code with the given Configuration `conf`.
     * Return the set of resulting configurations.
     */
    private HashSet<Configuration> step(Configuration conf) {
        if (DEBUG) System.out.println(conf);

        Inst inst = conf.getCode().get(0);
        if (DEBUG) System.out.println("> " + inst.opcode);

        HashSet<Configuration> configs = new HashSet<Configuration>();

        Configuration confNew;
        Code c1, c2, c1_2, c2_2;
        SignExc a, a1, a2;
        TTExc b, b1, b2;
        conf.getCode().remove(0);
        switch (inst.opcode) {
            case ADD:
                confNew = conf.clone();
                a1 = (SignExc) confNew.popStack();
                a2 = (SignExc) confNew.popStack();
                confNew.pushStack(op.add(a1, a2));
                configs.add(confNew);
                break;
            case AND:
                confNew = conf.clone();
                b1 = (TTExc) confNew.popStack();
                b2 = (TTExc) confNew.popStack();
                confNew.pushStack(op.and(b1, b2));
                configs.add(confNew);
                break;
            case BRANCH:
                b = (TTExc) conf.popStack();
                if (op.possiblyBErr(b)) {
                    confNew = conf.clone();
                    configs.add(confNew);
                }
                if (op.possiblyTrue(b)) {
                    confNew = conf.clone();
                    confNew.getCode().addAll(0, ((Branch) inst).c1);
                    configs.add(confNew);
                }
                if (op.possiblyFalse(b)) {
                    confNew = conf.clone();
                    confNew.getCode().addAll(0, ((Branch) inst).c2);
                    configs.add(confNew);
                }
                break;
            case EQ:
                confNew = conf.clone();
                a1 = (SignExc) confNew.popStack();
                a2 = (SignExc) confNew.popStack();
                confNew.pushStack(op.eq(a1, a2));
                configs.add(confNew);
                break;
            case FALSE:
                confNew = conf.clone();
                confNew.pushStack(op.abs(false));
                configs.add(confNew);
                break;
            case FETCH:
                confNew = conf.clone();
                a = confNew.getVar(((Fetch) inst).x);
                confNew.pushStack(a);
                configs.add(confNew);
                break;
            case LE:
                confNew = conf.clone();
                a1 = (SignExc) confNew.popStack();
                a2 = (SignExc) confNew.popStack();
                confNew.pushStack(op.leq(a1, a2));
                configs.add(confNew);
                break;
            case LOOP:
                confNew = conf.clone();
                c1 = ((Loop) inst).c1;
                c2 = ((Loop) inst).c2;
                c1_2 = new Code();
                c2_2 = new Code();
                c1_2.addAll(c2);
                c1_2.add(new Loop(c1, c2));
                c2_2.add(new Noop());
                // Insert new code at current position in code
                confNew.getCode().addAll(0, c1);
                confNew.getCode().add(c1.size(), new Branch(c1_2, c2_2));
                configs.add(confNew);
                break;
            case MULT:
                confNew = conf.clone();
                a1 = (SignExc) confNew.popStack();
                a2 = (SignExc) confNew.popStack();
                confNew.pushStack(op.multiply(a1, a2));
                configs.add(confNew);
                break;
            case NEG:
                confNew = conf.clone();
                b = (TTExc) confNew.popStack();
                confNew.pushStack(op.neg(b));
                configs.add(confNew);
                break;
            case NOOP:
                confNew = conf.clone();
                configs.add(confNew);
                break;
            case PUSH:
                confNew = conf.clone();
                confNew.pushStack(op.abs(((Push) inst).getValue()));
                configs.add(confNew);
                break;
            case STORE:
                confNew = conf.clone();
                a = (SignExc) confNew.popStack();
                if (op.possiblyAErr(a)) {
                    confNew = conf.clone();
                    configs.add(confNew);
                }
                if (op.possiblyInt(a)) {
                    confNew = conf.clone();
                    confNew.setVar(((Store) inst).x, a);
                    configs.add(confNew);
                }
                break;
            case SUB:
                confNew = conf.clone();
                a1 = (SignExc) confNew.popStack();
                a2 = (SignExc) confNew.popStack();
                confNew.pushStack(op.subtract(a1, a2));
                configs.add(confNew);
                break;
            case TRUE:
                confNew = conf.clone();
                confNew.pushStack(op.abs(true));
                configs.add(confNew);
                break;
            case DIV:
                confNew = conf.clone();
                a1 = (SignExc) confNew.popStack();
                a2 = (SignExc) confNew.popStack();
                a = op.divide(a1, a2);
                if (a == SignExc.ERR_A) confNew.setExceptional(true);
                confNew.pushStack(a);
                configs.add(confNew);
                break;
            case TRY:
                confNew = conf.clone();
                c1 = ((Try) inst).c1;
                c2 = ((Try) inst).c2;
                // dont catch outer exceptions
                if (c1 != null && confNew.isExceptional())
                    break;
                if (c1 == null) {
                    if (confNew.isExceptional()) {
                        if (DEBUG) System.out.println("CATCH EXCEPTION");
                        confNew.setExceptional(false);
                        confNew.getCode().addAll(0, c2);
                    }
                } else {
                    confNew.getCode().addAll(0, c1);
                    confNew.getCode().add(c1.size(), new Try(null, c2));
                }
                configs.add(confNew);
                break;
            default:
                System.err.println("Invalid opcode");
                System.exit(1);
        }

        return configs;
    }

    /**
     * Perform one execute step of the VM by doing a BFS search
     * of the configuration space, return `false` if no more
     * code can be executed.
     */
    private boolean executeStep() {
        Configuration conf = queue.removeFirst();
        int controlPoint = conf.getCode().get(0).stmControlPoint;
        HashSet<Configuration> configs = step(conf);

        // If the new control point is higher, consider the config visited
        for (Configuration c : configs) {
            if (c.getCode().isEmpty())
                visited.add(c.clone());
            else if (c.getCode().get(0).stmControlPoint > controlPoint)
                visited.add(c.clone());
        }

        // Add all non-visited Configurations to the queue
        for (Configuration c : configs) {
            if (!visited.contains(c))
                queue.add(c.clone());
        }

        return queue.size() != 0;
    }

    /**
     * Execute the entire program.
     */
    public void execute() throws IOException {
        // The first Configuration is always visited
        visited.add(queue.getFirst().clone());

        // Execute resulting AM Code using a step-function
        if (STEP) {
            while (executeStep())
                new BufferedReader(new InputStreamReader(System.in)).readLine();
        } else {
            while (executeStep()) {};
        }

        if (DEBUG) System.out.println(">>> END");
    }
}
