package frog;

import frog.frog_InternalKey;
import frog.frog_IterKey;

class frog_InternalKey {
  public frog_IterKey[] internalKey;
  
  public frog_IterKey[] keyE;
  
  public frog_IterKey[] keyD;
  
  public void setValue(int paramInt1, int paramInt2) {
    this.internalKey[paramInt1 / 288].setValue(paramInt1 % 288, paramInt2);
  }
  
  public int getValue(int paramInt) {
    return this.internalKey[paramInt / 288].getValue(paramInt % 288);
  }
}
