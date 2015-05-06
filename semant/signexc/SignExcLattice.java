package semant.signexc;

import static semant.signexc.SignExc.*;
import semant.Lattice;

public class SignExcLattice extends Lattice<SignExc> {
    
    private final static SignExc[][] LUB_MAP = {
        /*                 NONE_A    NEG       ZERO     POS       A_ERR  NON_POS  NON_ZERO  NON_NEG  Z       ANY_A */
        /*    NONE_A */  { NONE_A,   NEG,      ZERO,    POS,      ERR_A, NON_POS, NON_ZERO, NON_NEG, Z,      ANY_A },
        /*       NEG */  { NEG,      NEG,      NON_POS, NON_ZERO, ANY_A, NON_POS, NON_ZERO, Z,       Z,      ANY_A },
        /*      ZERO */  { ZERO,     NON_POS,  ZERO,    NON_NEG,  ANY_A, NON_POS, Z,        NON_NEG, Z,      ANY_A },
        /*       POS */  { POS,      NON_ZERO, NON_NEG, POS,      ANY_A, Z,       NON_ZERO, NON_NEG, Z,      ANY_A },
        /*     A_ERR */  { ERR_A,    ANY_A,    ANY_A,   ANY_A,    ERR_A, ANY_A,   ANY_A,    ANY_A,   ANY_A,  ANY_A },
        /*   NON_POS */  { NON_POS,  NON_POS,  NON_POS, Z,        ANY_A, NON_POS, Z,        Z,       Z,      ANY_A },
        /*  NON_ZERO */  { NON_ZERO, NON_ZERO, Z,       NON_ZERO, ANY_A, Z,       NON_ZERO, Z,       Z,      ANY_A },
        /*   NON_NEG */  { NON_NEG,  Z,        NON_NEG, NON_NEG,  ANY_A, Z,       Z,        NON_NEG, Z,      ANY_A },
        /*         Z */  { Z,        Z,        Z,       Z,        ANY_A, Z,       Z,        Z,       Z,      ANY_A },
        /*     ANY_A */  { ANY_A,    ANY_A,    ANY_A,   ANY_A,    ANY_A, ANY_A,   ANY_A,    ANY_A,   ANY_A,  ANY_A }
    };
    
    private final static SignExc[][] GLB_MAP = {
        /*                NONE_A   NEG     ZERO    POS     A_ERR    NON_POS  NON_ZERO  NON_NEG  Z         A_ANY   */
        /*    NONE_A */ { NONE_A,  NONE_A, NONE_A, NONE_A, NONE_A,  NONE_A,  NONE_A,   NONE_A,  NONE_A,   NONE_A   },
        /*       NEG */ { NONE_A,  NEG,    NONE_A, NONE_A, NONE_A,  NEG,     NEG,      NONE_A,  NEG,      NEG      },
        /*      ZERO */ { NONE_A,  NONE_A, ZERO,   NONE_A, NONE_A,  ZERO,    NONE_A,   ZERO,    ZERO,     ZERO     },
        /*       POS */ { NONE_A,  NONE_A, NONE_A, POS,    NONE_A,  NONE_A,  POS,      POS,     POS,      POS      },
        /*     A_ERR */ { NONE_A,  NONE_A, NONE_A, NONE_A, ERR_A,   NONE_A,  NONE_A,   NONE_A,  NONE_A,   ERR_A    },
        /*   NON_POS */ { NONE_A,  NEG,    ZERO,   NONE_A, NONE_A,  NON_POS, NEG,      ZERO,    NON_POS,  NON_POS  },
        /*  NON_ZERO */ { NONE_A,  NEG,    NONE_A, POS,    NONE_A,  NEG,     NON_ZERO, POS,     NON_ZERO, NON_ZERO },
        /*   NON_NEG */ { NONE_A,  NONE_A, ZERO,   POS,    NONE_A,  ZERO,    POS,      NON_NEG, NON_NEG,  NON_NEG  },
        /*         Z */ { NONE_A,  NEG,    ZERO,   POS,    NONE_A,  NON_POS, NON_ZERO, NON_NEG, Z,        Z        },
        /*     A_ANY */ { NONE_A,  NEG,    ZERO,   POS,    ERR_A,   NON_POS, NON_ZERO, NON_NEG, Z,        ANY_A    }
    };
    
    public SignExcLattice() {
        super(ANY_A, NONE_A);
    }
    
    public SignExc lub(SignExc a1, SignExc a2) {
        return LUB_MAP[a1.ordinal()][a2.ordinal()];
    }
    
    public SignExc glb(SignExc a1, SignExc a2) {
        return GLB_MAP[a1.ordinal()][a2.ordinal()];
    }
    
}
