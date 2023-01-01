package frog;

class frog_procs {
  
  static public int numIter = 8;

  /* Values defined from RAND Corporation's "A Million Random Digits" */

  private static int[] randomSeed = {
	  113, 21,232, 18,113, 92, 63,157,124,193,166,197,126, 56,229,229,
	  156,162, 54, 17,230, 89,189, 87,169,  0, 81,204,  8, 70,203,225,
	  160, 59,167,189,100,157, 84, 11,  7,130, 29, 51, 32, 45,135,237,
	  139, 33, 17,221, 24, 50, 89, 74, 21,205,191,242, 84, 53,  3,230,
	  231,118, 15, 15,107,  4, 21, 34,  3,156, 57, 66, 93,255,191,  3,
	   85,135,205,200,185,204, 52, 37, 35, 24, 68,185,201, 10,224,234,
		7,120,201,115,216,103, 57,255, 93,110, 42,249, 68, 14, 29, 55,
	  128, 84, 37,152,221,137, 39, 11,252, 50,144, 35,178,190, 43,162,
	  103,249,109,  8,235, 33,158,111,252,205,169, 54, 10, 20,221,201,
	  178,224, 89,184,182, 65,201, 10, 60,  6,191,174, 79, 98, 26,160,
	  252, 51, 63, 79,  6,102,123,173, 49,  3,110,233, 90,158,228,210,
	  209,237, 30, 95, 28,179,204,220, 72,163, 77,166,192, 98,165, 25,
	  145,162, 91,212, 41,230,110,  6,107,187,127, 38, 82, 98, 30, 67,
	  225, 80,208,134, 60,250,153, 87,148, 60, 66,165, 72, 29,165, 82,
	  211,207,  0,177,206, 13,  6, 14, 92,248, 60,201,132, 95, 35,215,
	  118,177,121,180, 27, 83,131, 26, 39, 46, 12};
  
  static public int[] makePermutation( int[] permu )
	/*	Receives an arbitrary byte array of (lastElem -1) elements and
		returns a permutation with values between 0 and lastElem.
		Reference Text: section B.1.3   */
  {
    int use[] = new int[256];
	int i, j, k, count, last;
    int lastElem = permu.length - 1;

	/* Initialize use array */
	for (i=0;i<=lastElem;use[i]=i,i++);

	last = lastElem;
    j = 0;
	/* Fill permutation with non-sequencial, unique values */
    for (i=0; i<lastElem; i++ )
    {
      j = (j+permu[i]) % (last+1);
	  permu[i] = use[j];
      /* Remove use[index] value from use array */
	  if ( j < last )
	    for ( k=j; k <= last-1; use[k] = use[k+1], k++ );
      last--;
	  if ( j > last ) j = 0;
    }
    permu[lastElem] = use[0];
	return permu;
  }

  static public int[] invertPermutation( int[] orig )
  /* Inverts a permutation */
  {
    int invert[] = new int[256];
    int i, lastElem = orig.length-1;
    for ( i=0; i<=lastElem; i++ ) invert[orig[i]] = i;
    return invert;
  }
  
  public static byte[] encryptFrog( byte[] plainText, frog_IterKey[] key )
  /* Encrypt plainText using internalKey - (internal cycle) See B.1.1 */
  {
    byte ite, ib;
    for ( ite=0; ite<numIter; ite++ )
    {
      for ( ib=0; ib < frog_Algorithm.BLOCK_SIZE; ib++ )
      {
        plainText[ib] ^= key[ite].xorBu[ib];
	if ( plainText[ib] < 0 )
	  plainText[ib] = (byte) key[ite].SubstPermu[plainText[ib]+256];
	else
	  plainText[ib] = (byte) key[ite].SubstPermu[plainText[ib]];
	if ( ib < frog_Algorithm.BLOCK_SIZE-1 ) 
    	  plainText[ib+1] ^= plainText[ib];
	else
	  plainText[0] ^= plainText[frog_Algorithm.BLOCK_SIZE-1];
	plainText[key[ite].BombPermu[ib]] ^= plainText[ib];
      }
    }
    return plainText;
  }

  public static byte[] decryptFrog( byte[] cipherText, frog_IterKey[] key )
  /* Decrypt cipherText using internalKey - (internal cycle) See B.1.1 */
  {
    int ib, ite;

    for ( ite = numIter-1; ite >= 0; ite-- )
    {
      for ( ib = frog_Algorithm.BLOCK_SIZE-1; ib >= 0; ib-- )
	  {
	    cipherText[key[ite].BombPermu[ib]] ^= cipherText[ib];
	    if ( ib < frog_Algorithm.BLOCK_SIZE-1 )
	      cipherText[ib+1] ^= cipherText[ib];
	    else
		  cipherText[0] ^= cipherText[frog_Algorithm.BLOCK_SIZE-1];
	    if ( cipherText[ib]<0 )
		  cipherText[ib] = (byte)key[ite].SubstPermu[cipherText[ib]+256];
		else
		  cipherText[ib] = (byte)key[ite].SubstPermu[cipherText[ib]];
	    cipherText[ib] ^= key[ite].xorBu[ib];
	  }
    }
	return cipherText;
  }
  
  public static frog_IterKey[] makeInternalKey( byte decrypting, frog_IterKey[] keyori )
  /* Processes unstructured internalKey into a valid internalKey.
     Reference Text: section B.1.2 */
  {
    byte[] used = new byte[frog_Algorithm.BLOCK_SIZE];
    int ite, j, i, k, l;
    int posi;
    byte change;
    frog_IterKey[] key = new frog_IterKey[ frog_procs.numIter ];
    for ( i=0; i < frog_procs.numIter; i++ )
	{
      key[i] = new frog_IterKey();
	  key[i].CopyFrom( keyori[i] );
	}
    posi = 1;
    for ( ite = 0; ite < numIter; ite ++ )
    {
      key[ite].SubstPermu = makePermutation( key[ite].SubstPermu );

	  if ( decrypting == 1 ) key[ite].SubstPermu = invertPermutation(key[ite].SubstPermu);

  	  /* See B.1.1a */
	  key[ite].BombPermu = makePermutation(key[ite].BombPermu );

  	  /* Join smaller cycles in BombPermu into one cycle
	     (See B.1.1b for rational and B.1.4 for code) */
      for ( i=0; i<frog_Algorithm.BLOCK_SIZE; used[i]=0, i++ );
      j = 0;
	  for ( i=0; i<frog_Algorithm.BLOCK_SIZE-1; i++ )
	  {
    	if ( key[ite].BombPermu[j] == 0 )
	    {
	      k = j;
          do {
		    k = ( k + 1 ) % frog_Algorithm.BLOCK_SIZE;
		  } while ( used[k]!=0 );
		  key[ite].BombPermu[j] = k;
		  l = k;
		  while ( key[ite].BombPermu[l]!=k ) l = key[ite].BombPermu[l];
		  key[ite].BombPermu[l] = 0;
	    }
	    used[j] = 1;
	    j = key[ite].BombPermu[j];
	  }

      /* Remove references to next element within BombPermu.
         See B.1.1c for rational and B.1.4.b for code. */
      for (i = 0; i < frog_Algorithm.BLOCK_SIZE; i++) {
        j = (i == frog_Algorithm.BLOCK_SIZE-1) ? 0 : i + 1;
        if (key[ite].BombPermu[i] == j) {
	      k = (j == frog_Algorithm.BLOCK_SIZE-1) ? 0 : j + 1;
	      key[ite].BombPermu[i] = k;
        }
      }
    }
    return key;
  }
  
  static public frog_IterKey[] hashKey(byte[] binaryKey)
  {

	/* Hash binaryKey of keyLen bytes into randomKey
	   Reference Text: section B.1.2 */

    byte[] buffer = new byte[frog_Algorithm.BLOCK_SIZE];
    frog_IterKey[] simpleKey = new frog_IterKey[ frog_procs.numIter ];
    frog_IterKey[] internalKey = new frog_IterKey[ frog_procs.numIter ];
    int iSeed, iFrase;
    int sizeKey,i,posi,size;
    int keyLen, last;
    for ( i = 0; i < frog_procs.numIter; i++ ) simpleKey[i] = new frog_IterKey();
    for ( i = 0; i < frog_procs.numIter; i++ ) internalKey[i] = new frog_IterKey();
    keyLen = binaryKey.length;
    sizeKey = frog_IterKey.size() * frog_procs.numIter;

	/* Initialize SimpleKey with user supplied key material and random seed.
       See B.1.2a */

    iSeed = 0; iFrase = 0;
    for ( i = 0; i < sizeKey; i++ )
    {
      simpleKey[i / frog_IterKey.size()].setValue(
	    i%frog_IterKey.size(), 
		randomSeed[iSeed] ^ binaryKey[iFrase]
	  );
	  if ( iSeed<250 ) iSeed++; else iSeed = 0;
	  if ( iFrase<keyLen-1 ) iFrase++; else iFrase = 0;
    }

    /* Convert simpleKey into a valid internal key (see B.1.2b) */

    simpleKey = makeInternalKey( frog_Algorithm.DIR_ENCRYPT, simpleKey );
    for ( i = 0; i < frog_Algorithm.BLOCK_SIZE; buffer[i++] = 0 );

	/* Initialize IV vector (see B.1.2c) */

    last = keyLen - 1;
    if ( last > frog_Algorithm.BLOCK_SIZE ) last = frog_Algorithm.BLOCK_SIZE-1;
    for ( i = 0; i <= last; buffer[i] ^= binaryKey[i], i++ );
    buffer[0] ^= keyLen;

    posi = 0;

    /* Fill randomKey with the cipher texts produced successive
       encryptions (see B.1.2.c) */

    do {
      buffer = encryptFrog( buffer, simpleKey );
	  size = sizeKey - posi;
	  if ( size > frog_Algorithm.BLOCK_SIZE ) size = frog_Algorithm.BLOCK_SIZE;
	  for (i=0;i<frog_Algorithm.BLOCK_SIZE;i++)
	    if ( buffer[i] < 0 )
	      internalKey[(posi+i)/frog_IterKey.size()].setValue((posi+i)%frog_IterKey.size(), buffer[i]+256 ); 
		else
	      internalKey[(posi+i)/frog_IterKey.size()].setValue((posi+i)%frog_IterKey.size(), buffer[i] ); 
	  posi += size;
    } while ( posi != sizeKey );
    return internalKey;
  }

  static public byte[] shif1bitLeft( byte[] buffer )
  
  /* moves an entire block of size bytes 1 bit to the left */
  
  {
    byte[] result = new byte[ frog_Algorithm.BLOCK_SIZE ];
	int i;
	for ( i = frog_Algorithm.BLOCK_SIZE-1; i >= 0 ; i-- ) result[i] = buffer[i];
	for ( i = frog_Algorithm.BLOCK_SIZE-1; i >= 0 ; i-- )
	{
	  result[i] = (byte) (result[i] << 1);
	  if ( i > 0 ) 
	    result[i] |= result[i-1] >> 7;
	}
	return result;
  }
}