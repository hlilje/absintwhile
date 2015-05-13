package semant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import semant.amsyntax.*;
import semant.signexc.*;

public class VM {

    private static boolean DEBUG;
    private static boolean STEP;

    private SignExcOps op;                   // Type of operations to use
    private SignExcLattice zLattice;         // Type lattice for Z
    private TTExcLattice ttLattice;          // Type lattice for TT
    private HashSet<Configuration> visited;  // Visited configurations
    private LinkedList<Configuration> queue; // BFS queue
    private SignExc[] zVals;                 // Lubs of Z
    private TTExc[] ttVals;                  // Lubs of TT
    private HashMap<String, SignExc>[] lubs; // Lubs of vars
    private int maxControlPoint;             // Highest control point
    private int tryDepth, exceptionDepth;    // For handling nested Try Catch
    private boolean possiblyNormalTermination;
    private boolean possiblyExceptionalTermination;

    public VM(Code code, boolean debug, boolean step) {
        DEBUG                          = debug;
        STEP                           = step;
        op                             = new SignExcOps();
        zLattice                       = new SignExcLattice();
        ttLattice                      = new TTExcLattice();
        visited                        = new HashSet<Configuration>();
        queue                          = new LinkedList<Configuration>();
        maxControlPoint                = 0;
        tryDepth                       = 0;
        exceptionDepth                 = 0;
        possiblyNormalTermination      = false;
        possiblyExceptionalTermination = false;

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
        if (DEBUG) {
            System.out.println("> " + inst.opcode + " (ctrl: " +
                    inst.stmControlPoint + ")");
            System.out.println("Exceptional state: " + conf.isExceptional());
        }

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
                    if (!conf.isExceptional()) exceptionDepth = tryDepth;
                    confNew = conf.clone();
                    confNew.setExceptional(true);
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

                Loop loop = new Loop(c1, c2);
                loop.stmControlPoint = inst.stmControlPoint;
                c1_2.add(loop);

                Noop noop = new Noop();
                noop.stmControlPoint = inst.stmControlPoint;
                c2_2.add(noop);
                // Insert new code at current position in code
                confNew.getCode().addAll(0, c1);
                Branch branch = new Branch(c1_2, c2_2);
                branch.stmControlPoint = inst.stmControlPoint;
                confNew.getCode().add(c1.size(), branch);
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
                a = (SignExc) conf.popStack();
                if (op.possiblyAErr(a)) {
                    if (!conf.isExceptional()) exceptionDepth = tryDepth;
                    confNew = conf.clone();
                    confNew.setExceptional(true);
                    // to initialize x
                    confNew.getVar(((Store) inst).x);
                    configs.add(confNew);
                }
                if (op.possiblyInt(a)) {
                    confNew = conf.clone();
                    if (!conf.isExceptional())
                        confNew.setVar(((Store) inst).x, a);
                    else // to initialize x
                        confNew.getVar(((Store) inst).x);
                    configs.add(confNew);
                }
                if (a == SignExc.NONE_A) {
                    confNew = conf.clone();
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
                confNew.pushStack(a);
                configs.add(confNew);
                break;
            case TRY:
                confNew = conf.clone();
                c1 = ((Try) inst).c1;
                c2 = ((Try) inst).c2;
                // Catch
                if (c1 == null) {
                    if (confNew.isExceptional()) {
                        if (DEBUG) System.out.println("CATCH EXCEPTION");
                        // Handle nested Try Catch
                        if (tryDepth == exceptionDepth)
                            confNew.setExceptional(false);
                    }
                    confNew.getCode().addAll(0, c2);
                    --tryDepth;
                // Try
                } else {
                    ++tryDepth;
                    confNew.getCode().addAll(0, c1);
                    Try tr = new Try(null, c2);
                    tr.stmControlPoint = c2.get(0).stmControlPoint;
                    confNew.getCode().add(c1.size(), tr);
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

        // Add all non-visited Configurations to the queue
        for (Configuration c : configs) {
            if (!visited.contains(c) && !c.getCode().isEmpty()) {
                queue.add(c.clone());
            }
        }

        // If the new control point is higher, consider the config visited
        for (Configuration c : configs) {
            // Last control point
            if (c.getCode().isEmpty()) {
                visited.add(c.clone());
            } else {
                // Keep track of the highest control point
                if (c.getCode().get(0).stmControlPoint > maxControlPoint)
                    maxControlPoint = c.getCode().get(0).stmControlPoint;
                visited.add(c.clone());
            }
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

        ++maxControlPoint; // Since last control point will have no instructions left

        if (DEBUG) System.out.println("Max control point: " + maxControlPoint);
        if (DEBUG) System.out.println(">>> END");
    }

    /**
     * Compute the least upper bounds.
     */
    @SuppressWarnings("unchecked")
    public void computeLubs() {
        lubs = new HashMap[maxControlPoint];
        zVals  = new SignExc[maxControlPoint];
        ttVals = new TTExc[maxControlPoint];

        for (int i = 0; i < lubs.length; ++i)
            lubs[i] = new HashMap<String, SignExc>();

        // Pick out the relevant configurations for the lubs
        for (Configuration c : visited) {
            Inst inst = c.getCode().size() > 0 ? c.getCode().get(0) : null;
            int cp = inst == null ? maxControlPoint - 1 :
                inst.stmControlPoint - 1;

            // Check if the program exited normally
            if (cp == maxControlPoint - 1) {
                if (!possiblyNormalTermination)
                    possiblyNormalTermination = !c.isExceptional();
                if (!possiblyExceptionalTermination)
                    possiblyExceptionalTermination = c.isExceptional();
            }

            if (inst instanceof Store) {
                zVals[cp] = zVals[cp] != null ? zLattice.lub(zVals[cp],
                        (SignExc) c.getStackTop()) : (SignExc) c.getStackTop();
            }
            if (inst instanceof Branch) {
                ttVals[cp] = ttVals[cp] != null ? ttLattice.lub(ttVals[cp],
                        (TTExc) c.getStackTop()) : (TTExc) c.getStackTop();
            }

            for (Map.Entry<String, SignExc> e : c.getSymTable().entrySet()) {
                if (lubs[cp].containsKey(e.getKey())) {
                    lubs[cp].put(e.getKey(),
                                   zLattice.lub(lubs[cp].get(e.getKey()),
                                                      e.getValue()));
                } else {
                    lubs[cp].put(e.getKey(), e.getValue());
                }
            }
        }
        // Make sure all variables exist at all control points
        for (Map.Entry<String, SignExc> e : lubs[lubs.length-1].entrySet()) {
            for (int i = 0; i < lubs.length-1; i++) {
                if (!lubs[i].containsKey(e.getKey()))
                    lubs[i].put(e.getKey(), SignExc.Z);
            }
        }
    }

    /**
     * Return the number of last control point
     */
    public int lastControlPoint() {
        return maxControlPoint;
    }

    /**
     * Return the least upper bounds of variables.
     */
    public HashMap<String, SignExc>[] getVarLubs() {
        return lubs;
    }

    /**
     * Return the least upper bouds of the Z values.
     */
    public SignExc[] getZLubs() {
        return zVals;
    }

    /**
     * Return the least upper bouds of the TT values.
     */
    public TTExc[] getTTLubs() {
        return ttVals;
    }

    /**
     * Return whether the program exited normally (possibly).
     */
    public boolean possiblyNormalTermination() {
        return possiblyNormalTermination;
    }

    /**
     * Return whether the program exited exceptionally (possibly).
     */
    public boolean possiblyExceptionalTermination() {
        return possiblyExceptionalTermination;
    }
}
