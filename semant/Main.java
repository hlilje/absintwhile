package semant;

import semant.amsyntax.Code;
import semant.amsyntax.Inst;
import semant.whilesyntax.Stm;

public class Main {

    private static boolean DEBUG = false;
    private static boolean STEP  = false;

    public static void main(String[] args) throws Exception {
        if (args.length > 1 && args[1].equals("-d")) DEBUG = true;
        if (args.length > 2 && args[2].equals("-s")) STEP = true;

        // Generate While AST
        Stm s = WhileParser.parse(args[0]);

        // Compile s into AM Code AST
        Code am = s.accept(new CompileVisitor());

        if (DEBUG) {
            System.out.println("============== Code ==============");
            for (Inst inst : am) System.out.println(inst);
            System.out.println();
            System.out.println(">>> START");
        }

        // Run the program and compute least upper bounds
        VM vm = new VM(am, DEBUG, STEP);
        vm.execute();
        vm.computeLubs();

        // Pretty print the program with annotations
        PrettyPrinter p = new PrettyPrinter(vm);
        s.accept(p);
        p.printTermination();
        System.out.println();
    }
}
