package frog;

import java.io.PrintWriter;
import java.security.InvalidKeyException;

/* Internal FROG classes */

class frog_IterKey {
  public int xorBu[];
  public int SubstPermu[];
  public int BombPermu[];

  frog_IterKey()
  {
    xorBu = new int[frog_Algorithm.BLOCK_SIZE];
    SubstPermu = new int[256];
    BombPermu = new int[frog_Algorithm.BLOCK_SIZE];
  }

  public static int size()
  {
    return frog_Algorithm.BLOCK_SIZE*2+256;
  }

  public void setValue( int index, int value )
  {
    if ( value < 0 ) value = 256 + value;
    if ( index < frog_Algorithm.BLOCK_SIZE ) 
	  xorBu[index] = value;
	else if ( index < frog_Algorithm.BLOCK_SIZE + 256 ) 
	  SubstPermu[index-frog_Algorithm.BLOCK_SIZE] = value;
	else
	  BombPermu[index-frog_Algorithm.BLOCK_SIZE-256] = value;  
  }

  public int getValue( int index )
  {
    if ( index < frog_Algorithm.BLOCK_SIZE ) 
	  return xorBu[index];
	else if ( index < frog_Algorithm.BLOCK_SIZE + 256 ) 
	  return SubstPermu[index-frog_Algorithm.BLOCK_SIZE];
	else
	  return BombPermu[index-frog_Algorithm.BLOCK_SIZE-256];  
  }
  public void CopyFrom( frog_IterKey ori )
  {
    int i;
	for (i=0;i<ori.xorBu.length;i++)
	  xorBu[i] = ori.xorBu[i];
	for (i=0;i<ori.SubstPermu.length;i++)
	  SubstPermu[i] = ori.SubstPermu[i];
	for (i=0;i<ori.BombPermu.length;i++)
      BombPermu[i] = ori.BombPermu[i];
  }
}
