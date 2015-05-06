package semant;

import semant.amsyntax.Code;
import semant.amsyntax.Inst;
import semant.whilesyntax.Stm;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    private static boolean DEBUG = false;
    private static boolean STEP  = false;

    public static void main(String[] args) throws Exception {
        if (args.length > 1 && args[1].equals("-d"))
            DEBUG = true;
        if (args.length > 2 && args[2].equals("-s"))
            STEP = true;

        // Generate While AST
        Stm s = WhileParser.parse(args[0]);

        // Print While AST
        System.out.println("============= Source =============");
        s.accept(new PrettyPrinter());
        System.out.println();
        System.out.println();

        // Compile s into AM Code AST
        Code am = s.accept(new CompileVisitor());

        if (DEBUG) {
            System.out.println("============== Code ==============");
            for (Inst inst : am) System.out.println(inst);
            System.out.println();
            System.out.println(">>> START");
        }

        VM vm = new VM(am, DEBUG);

        // Execute resulting AM Code using a step-function
        if (STEP) {
            while (vm.executeStep())
                new BufferedReader(new InputStreamReader(System.in)).readLine();
        } else {
            while (vm.executeStep()) {};
        }
    }
}
