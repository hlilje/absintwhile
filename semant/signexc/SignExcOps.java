package semant.signexc;

import semant.Operations;
import static semant.signexc.SignExc.*;
import static semant.signexc.TTExc.*;

public class SignExcOps implements Operations<SignExc, TTExc> {

    private final static TTExc[][] AND_MAP = {
        /*             NONE_B  TT      FF      ERR_B   T       B_ANY */
        /* NONE_B */ { NONE_B, NONE_B, NONE_B, NONE_B, NONE_B, NONE_B },
        /*     TT */ { NONE_B, TT,     FF,     ERR_B,  T,      ANY_B  },
        /*     FF */ { NONE_B, FF,     FF,     ERR_B,  T,      ANY_B  },
        /*  ERR_B */ { NONE_B, ERR_B,  ERR_B,  ERR_B,  ERR_B,  ERR_B  },
        /*      T */ { NONE_B, T,      T,      ERR_B,  T,      ANY_B  },
        /*  B_ANY */ { NONE_B, ANY_B,  ANY_B,  ERR_B,  ANY_B,  ANY_B  }
    };
    
    private final static TTExc[] NEG_MAP = {
        /* NONE_B  TT  FF  ERR_B  T  B_ANY */
           NONE_B, FF, TT, ERR_B, T, ANY_B
    };
    
    private final static SignExc[][] MULT_MAP = {
        /*                 NONE_A   NEG       ZERO     POS       ERR_A   NON_POS  NON_ZERO   NON_NEG   Z        ANY_A */
        /*    NONE_A */  { NONE_A,  NONE_A,   NONE_A,  NONE_A,   NONE_A, NONE_A,  NONE_A,    NONE_A,   NONE_A,  NONE_A },
        /*       NEG */  { NONE_A,  POS,      ZERO,    NEG,      ERR_A,  NON_NEG, NON_ZERO,  NON_POS,  Z,       ANY_A  },
        /*      ZERO */  { NONE_A,  ZERO,     ZERO,    ZERO,     ERR_A,  ZERO,    ZERO,      ZERO,     ZERO,    ANY_A  },
        /*       POS */  { NONE_A,  NEG,      ZERO,    POS,      ERR_A,  NON_POS, NON_ZERO,  NON_NEG,  Z,       ANY_A  },
        /*     ERR_A */  { NONE_A,  ERR_A,    ERR_A,   ERR_A,    ERR_A,  ERR_A,   ERR_A,     ERR_A,    ERR_A,   ERR_A  },
        /*   NON_POS */  { NONE_A,  NON_NEG,  ZERO,    NON_POS,  ERR_A,  NON_NEG, Z,         NON_POS,  Z,       ANY_A  },
        /*  NON_ZERO */  { NONE_A,  NON_ZERO, ZERO,    NON_ZERO, ERR_A,  Z,       NON_ZERO,  Z,        Z,       ANY_A  },
        /*   NON_NEG */  { NONE_A,  NON_POS,  ZERO,    NON_NEG,  ERR_A,  NON_POS, Z,         NON_NEG,  Z,       ANY_A  },
        /*         Z */  { NONE_A,  Z,        ZERO,    Z,        ERR_A,  Z,       Z,         Z,        Z,       ANY_A  },
        /*     ANY_A */  { NONE_A,  ANY_A,    ANY_A,   ANY_A,    ERR_A,  ANY_A,   ANY_A,     ANY_A,    ANY_A,   ANY_A  }
    };
    
    private final static SignExc[][] DIV_MAP = {
        /*                 NONE_A   NEG       ZERO     POS       ERR_A   NON_POS  NON_ZERO   NON_NEG   Z        ANY_A */
        /*    NONE_A */  { NONE_A,  NONE_A,   NONE_A,  NONE_A,   NONE_A, NONE_A,  NONE_A,    NONE_A,   NONE_A,  NONE_A },
        /*       NEG */  { NONE_A,  NON_NEG,  ERR_A,   NON_POS,  ERR_A,  ANY_A,   Z,         ANY_A,    ANY_A,   ANY_A  },
        /*      ZERO */  { NONE_A,  ZERO,     ERR_A,   ZERO,     ERR_A,  ANY_A,   ZERO,      ANY_A,    ANY_A,   ANY_A  },
        /*       POS */  { NONE_A,  NON_POS,  ERR_A,   NON_NEG,  ERR_A,  ANY_A,   Z,         ANY_A,    ANY_A,   ANY_A  },
        /*     ERR_A */  { NONE_A,  ERR_A,    ERR_A,   ERR_A,    ERR_A,  ERR_A,   ERR_A,     ERR_A,    ERR_A,   ERR_A  },
        /*   NON_POS */  { NONE_A,  NON_NEG,  ERR_A,   NON_POS,  ERR_A,  ANY_A,   Z,         ANY_A,    ANY_A,   ANY_A  },
        /*  NON_ZERO */  { NONE_A,  Z,        ERR_A,   Z,        ERR_A,  ANY_A,   Z,         ANY_A,    ANY_A,   ANY_A  },
        /*   NON_NEG */  { NONE_A,  NON_POS,  ERR_A,   NON_NEG,  ERR_A,  ANY_A,   Z,         ANY_A,    ANY_A,   ANY_A  },
        /*         Z */  { NONE_A,  Z,        ERR_A,   Z,        ERR_A,  ANY_A,   Z,         ANY_A,    ANY_A,   ANY_A  },
        /*     ANY_A */  { NONE_A,  ANY_A,    ERR_A,   ANY_A,    ERR_A,  ANY_A,   ANY_A,     ANY_A,    ANY_A,   ANY_A  }
    };
    
    private final static SignExc[][] ADD_MAP = {
        /*                 NONE_A   NEG       ZERO      POS       ERR_A   NON_POS  NON_ZERO   NON_NEG   Z         ANY_A */
        /*    NONE_A */  { NONE_A,  NONE_A,   NONE_A,   NONE_A,   NONE_A, NONE_A,  NONE_A,    NONE_A,   NONE_A,    NONE_A },
        /*       NEG */  { NONE_A,  NEG,      NEG,      Z,        ERR_A,  NEG,     Z,         Z,        Z,         ANY_A  },
        /*      ZERO */  { NONE_A,  NEG,      ZERO,     POS,      ERR_A,  NON_POS, NON_ZERO,  NON_NEG,  Z,         ANY_A  },
        /*       POS */  { NONE_A,  Z,        POS,      POS,      ERR_A,  Z,       Z,         POS,      Z,         ANY_A  },
        /*     ERR_A */  { NONE_A,  ERR_A,    ERR_A,    ERR_A,    ERR_A,  ERR_A,   ERR_A,     ERR_A,    ERR_A,     ERR_A  },
        /*   NON_POS */  { NONE_A,  NEG,      NON_POS,  Z,        ERR_A,  NON_POS, Z,         Z,        Z,         ANY_A  },
        /*  NON_ZERO */  { NONE_A,  Z,        NON_ZERO, Z,        ERR_A,  Z,       Z,         Z,        Z,         ANY_A  },
        /*   NON_NEG */  { NONE_A,  Z,        NON_NEG,  POS,      ERR_A,  Z,       Z,         NON_NEG,  Z,         ANY_A  },
        /*         Z */  { NONE_A,  Z,        Z,        Z,        ERR_A,  Z,       Z,         Z,        Z,         ANY_A  },
        /*     ANY_A */  { NONE_A,  ANY_A,    ANY_A,    ANY_A,    ERR_A,  ANY_A,   ANY_A,     ANY_A,    ANY_A,     ANY_A  }
    };
    
    private final static SignExc[][] SUB_MAP = {
        /*                 NONE_A   NEG       ZERO      POS      ERR_A   NON_POS  NON_ZERO   NON_NEG   Z          ANY_A */
        /*    NONE_A */  { NONE_A,  NONE_A,   NONE_A,   NONE_A,  NONE_A, NONE_A,  NONE_A,    NONE_A,   NONE_A,    NONE_A },
        /*       NEG */  { NONE_A,  Z,        NEG,      NEG,     ERR_A,  Z,       Z,         Z,        Z,         ANY_A  },
        /*      ZERO */  { NONE_A,  POS,      ZERO,     NEG,     ERR_A,  NON_NEG, NON_ZERO,  NON_POS,  Z,         ANY_A  },
        /*       POS */  { NONE_A,  POS,      POS,      Z,       ERR_A,  POS,     Z,         Z,        Z,         ANY_A  },
        /*     ERR_A */  { NONE_A,  ERR_A,    ERR_A,    ERR_A,   ERR_A,  ERR_A,   ERR_A,     ERR_A,    ERR_A,     ERR_A  },
        /*   NON_POS */  { NONE_A,  POS,      NON_POS,  NEG,     ERR_A,  Z,       Z,         NON_POS,  Z,         ANY_A  },
        /*  NON_ZERO */  { NONE_A,  Z,        NON_ZERO, Z,       ERR_A,  Z,       Z,         Z,        Z,         ANY_A  },
        /*   NON_NEG */  { NONE_A,  POS,      NON_NEG,  Z,       ERR_A,  NON_NEG, Z,         Z,        Z,         ANY_A  },
        /*         Z */  { NONE_A,  Z,        Z,        Z,       ERR_A,  Z,       Z,         Z,        Z,         ANY_A  },
        /*     ANY_A */  { NONE_A,  ANY_A,    ANY_A,    ANY_A,   ERR_A,  ANY_A,   ANY_A,     ANY_A,    ANY_A,     ANY_A  }
    };

    
    private final static TTExc[][] EQ_MAP = {
        /*                 NONE_A  NEG     ZERO    POS     ERR_A   NON_POS NON_ZERO NON_NEG Z       ANY_A */
        /*    NONE_A */  { NONE_B, NONE_B, NONE_B, NONE_B, NONE_B, NONE_B, NONE_B,  NONE_B, NONE_B, NONE_B },
        /*       NEG */  { NONE_B, T,      FF,     FF,     ERR_B,  T,      T,       FF,     T,      ANY_B  },
        /*      ZERO */  { NONE_B, FF,     TT,     FF,     ERR_B,  T,      FF,      T,      T,      ANY_B  },
        /*       POS */  { NONE_B, FF,     FF,     T,      ERR_B,  FF,     T,       T,      T,      ANY_B  },
        /*     ERR_A */  { NONE_B, ERR_B,  ERR_B,  ERR_B,  ERR_B,  ERR_B,  ERR_B,   ERR_B,  ERR_B,  ERR_B  },
        /*   NON_POS */  { NONE_B, T,      T,      FF,     ERR_B,  T,      T,       T,      T,      ANY_B  },
        /*  NON_ZERO */  { NONE_B, T,      FF,     T,      ERR_B,  T,      T,       T,      T,      ANY_B  },
        /*   NON_NEG */  { NONE_B, FF,     T,      T,      ERR_B,  T,      T,       T,      T,      ANY_B  },
        /*         Z */  { NONE_B, T,      T,      T,      ERR_B,  T,      T,       T,      T,      ANY_B  },
        /*     ANY_A */  { NONE_B, ANY_B,  ANY_B,  ANY_B,  ERR_B,  ANY_B,  ANY_B,   ANY_B,  ANY_B,  ANY_B  },
    };    
    private final static TTExc[][] LEQ_MAP = {
        /*                 NONE_A  NEG     ZERO    POS     ERR_A   NON_POS NON_ZERO NON_NEG Z       ANY_A */
        /*    NONE_A */  { NONE_B, NONE_B, NONE_B, NONE_B, NONE_B, NONE_B, NONE_B,  NONE_B, NONE_B, NONE_B },
        /*       NEG */  { NONE_B, T,      TT,     TT,     ERR_B,  T,      T,       TT,     T,      ANY_B  },
        /*      ZERO */  { NONE_B, FF,     TT,     TT,     ERR_B,  T,      T,       T,      T,      ANY_B  },
        /*       POS */  { NONE_B, FF,     FF,     T,      ERR_B,  FF,     T,       T,      T,      ANY_B  },
        /*     ERR_A */  { NONE_B, ERR_B,  ERR_B,  ERR_B,  ERR_B,  ERR_B,  ERR_B,   ERR_B,  ERR_B,  ERR_B  },
        /*   NON_POS */  { NONE_B, T,      T,      TT,     ERR_B,  T,      T,       T,      T,      ANY_B  },
        /*  NON_ZERO */  { NONE_B, T,      T,      T,      ERR_B,  T,      T,       T,      T,      ANY_B  },
        /*   NON_NEG */  { NONE_B, FF,     T,      T,      ERR_B,  T,      T,       T,      T,      ANY_B  },
        /*         Z */  { NONE_B, T,      T,      T,      ERR_B,  T,      T,       T,      T,      ANY_B  },
        /*     ANY_A */  { NONE_B, ANY_B,  ANY_B,  ANY_B,  ERR_B,  ANY_B,  ANY_B,   ANY_B,  ANY_B,  ANY_B  }
    };
    
    public TTExc abs(boolean b) {
        return b ? TT : FF;
    }

    public SignExc abs(Integer z) {
        return z < 0 ? NEG : z > 0 ? POS : ZERO;
    }
    
    public SignExc add(SignExc a1, SignExc a2) {
        return ADD_MAP[a1.ordinal()][a2.ordinal()];
    }

    public TTExc and(TTExc b1, TTExc b2) {
        return AND_MAP[b1.ordinal()][b2.ordinal()];
    }
    
    public SignExc divide(SignExc a1, SignExc a2) {
        return DIV_MAP[a1.ordinal()][a2.ordinal()];
    }
    
    public TTExc eq(SignExc a1, SignExc a2) {
        return EQ_MAP[a1.ordinal()][a2.ordinal()];
    }
    
    public TTExc leq(SignExc a1, SignExc a2) {
        return LEQ_MAP[a1.ordinal()][a2.ordinal()];
    }

    public SignExc multiply(SignExc a1, SignExc a2) {
        return MULT_MAP[a1.ordinal()][a2.ordinal()];
    }

    public TTExc neg(TTExc b) {
        return NEG_MAP[b.ordinal()];
    }

    public boolean possiblyAErr(SignExc a) {
        return a == ERR_A || a == ANY_A;
    }

    public boolean possiblyBErr(TTExc b) {
        return b == ERR_B || b == ANY_B;
    }

    public boolean possiblyFalse(TTExc b) {
        return b == FF || b == T || b == ANY_B;
    }
    
    public boolean possiblyInt(SignExc a) {
        return a == ANY_A || a == NEG || a == ZERO || a == POS || a == NON_POS ||
               a == NON_ZERO || a == NON_NEG || a == Z;
    }

    public boolean possiblyTrue(TTExc b) {
        return b == TT || b == T || b == ANY_B;
    }
    
    public SignExc subtract(SignExc a1, SignExc a2) {
        return SUB_MAP[a1.ordinal()][a2.ordinal()];
    }
    
    public boolean isInt(SignExc a) {
        return a == Z || a == NEG || a == ZERO || a == POS || a == NON_POS ||
               a == NON_ZERO || a == NON_NEG;
    }
}
