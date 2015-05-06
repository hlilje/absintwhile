package semant.signexc;

import static semant.signexc.TTExc.*;
import semant.Lattice;

public class TTExcLattice extends Lattice<TTExc> {
    
    private final static TTExc[][] LUB_MAP = {
        /*             NONE_B  TT      FF      ERR_B   T    ANY_B */
        /* NONE_B */ { NONE_B, TT,     FF,     ERR_B,  T,      ANY_B  },
        /* TT     */ { TT,     TT,     T,      ANY_B,  T,      ANY_B  },
        /* FF     */ { FF,     T,      FF,     ANY_B,  T,      ANY_B  },
        /* ERR_B  */ { ERR_B,  ANY_B,  ANY_B,  ERR_B,  ANY_B,  ANY_B  },
        /* T      */ { T,      T,      T,      ANY_B,  T,      ANY_B  },
        /* ANY_B  */ { ANY_B,  ANY_B,  ANY_B,  ANY_B,  ANY_B,  ANY_B  }
    };
    
    
    private final static TTExc[][] GLB_MAP = {
        /*             NONE_B  TT      FF      ERR_B   T       ANY_B */
        /* NONE_B */ { NONE_B, NONE_B, NONE_B, NONE_B, NONE_B, NONE_B },
        /* TT     */ { NONE_B, TT,     NONE_B, NONE_B, TT,     TT,    },
        /* FF     */ { NONE_B, NONE_B, FF,     NONE_B, FF,     FF,    },
        /* ERR_B  */ { NONE_B, NONE_B, NONE_B, ERR_B,  NONE_B, ERR_B, },
        /* T      */ { NONE_B, TT,     FF,     NONE_B, T,      T,     },
        /* ANY_B  */ { NONE_B, TT,     FF,     ERR_B,  T,      ANY_B, }
    };
    
    
    public TTExcLattice() {
        super(ANY_B, NONE_B);
    }
    
    public TTExc glb(TTExc b1, TTExc b2) {
        return GLB_MAP[b1.ordinal()][b2.ordinal()];
    }
    
    
    public TTExc lub(TTExc b1, TTExc b2) {
        return LUB_MAP[b1.ordinal()][b2.ordinal()];
    }
    
}
