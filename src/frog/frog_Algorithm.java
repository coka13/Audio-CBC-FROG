package frog;

import java.util.Arrays;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;

public final class frog_Algorithm // implicit no-argument constructor
{
// Debugging methods and variables
//...........................................................................

	static final String NAME = "frog_Algorithm";
	static final boolean IN = true, OUT = false;

	static final boolean DEBUG = frog_Properties.GLOBAL_DEBUG;
	static final int debuglevel = DEBUG ? frog_Properties.getLevel(NAME) : 0;
	static final PrintWriter err = DEBUG ? frog_Properties.getOutput() : null;

	static final boolean TRACE = frog_Properties.isTraceable(NAME);

	static void debug(String s) {
		err.println(">>> " + NAME + ": " + s);
	}

	static void trace(boolean in, String s) {
		if (TRACE)
			err.println((in ? "==> " : "<== ") + NAME + "." + s);
	}

	static void trace(String s) {
		if (TRACE)
			err.println("<=> " + NAME + "." + s);
	}

// Constants and variables
//...........................................................................

	static final int BLOCK_SIZE = 16; // bytes in a data-block
	static final byte DIR_ENCRYPT = 0;
	static final byte DIR_DECRYPT = 1;

	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

// Static code - to intialise S-box and permutation tables if any
//...........................................................................

	static {
		long time = System.currentTimeMillis();

		if (DEBUG && debuglevel > 6) {
			System.out.println("Algorithm Name: " + frog_Properties.FULL_NAME);

		}
		//
		// precompute eventual S-box tables
		//
		time = System.currentTimeMillis() - time;

		if (DEBUG && debuglevel > 8) {
			System.out.println("==========");
			System.out.println();
			System.out.println("Static Data");
			System.out.println();
//
// any other println() statements
//
			System.out.println();
			System.out.println("Total initialization time: " + time + " ms.");
			System.out.println();
		}
	}

// Basic API methods
//...........................................................................

	/**
	 * Expand a user-supplied key material into a session key.
	 *
	 * @param key The 128/192/256-bit user-key to use.
	 * @return This cipher's round keys.
	 * @exception InvalidKeyException If the key is invalid.
	 */
	public static synchronized Object makeKey(byte[] k) throws InvalidKeyException {
		if (DEBUG)
			trace(IN, "makeKey(" + k + ")");
		if (DEBUG && debuglevel > 7) {
			System.out.println("Intermediate Session Key Values");
			System.out.println();
			System.out.println("Raw=" + toString(k));
		}
		//
		// ...
		//
		Object sessionKey = null;

		frog_InternalKey intkey = new frog_InternalKey();
		/* Fill internal key with hashed keyMaterial */
		intkey.internalKey = frog_procs.hashKey(k);
		/*
		 * Convert internalKey into a valid format for encrypt and decrypt (see B.1.2.e)
		 */
		intkey.keyE = frog_procs.makeInternalKey(frog_Algorithm.DIR_ENCRYPT, intkey.internalKey);
		intkey.keyD = frog_procs.makeInternalKey(frog_Algorithm.DIR_DECRYPT, intkey.internalKey);

		sessionKey = intkey;
		//
		// ...
		//
		if (DEBUG && debuglevel > 7) {
			System.out.println("...any intermediate values");
			System.out.println();
		}
		if (DEBUG)
			trace(OUT, "makeKey()");
		return sessionKey;
	}

	/**
	 * Encrypt exactly one block of plaintext.
	 *
	 * @param in         The plaintext.
	 * @param inOffset   Index of in from which to start considering data.
	 * @param sessionKey The session key to use for encryption.
	 * @return The ciphertext generated from a plaintext using the session key.
	 */
	public static byte[] blockEncrypt(byte[] in, int inOffset, Object sessionKey) {
		if (DEBUG)
			trace(IN, "blockEncrypt(" + in + ", " + inOffset + ", " + sessionKey + ")");
		if (DEBUG && debuglevel > 6)
			System.out.println("PT=" + toString(in, inOffset, BLOCK_SIZE));
		//
		// ....
		//
		byte[] result = new byte[BLOCK_SIZE];
		int i, j;
		//
		// Null - just copy it straight across.
		// Guarunteed to work, guarunteed to be insecure!
		//
		for (i = inOffset, j = 0; i < frog_Algorithm.BLOCK_SIZE; i++, j++)
			result[j] = in[i];

		result = frog_procs.encryptFrog(result, ((frog_InternalKey) sessionKey).keyE);

		if (DEBUG && debuglevel > 6) {
			System.out.println("CT=" + toString(result));
			System.out.println();
		}
		if (DEBUG)
			trace(OUT, "blockEncrypt()");
		return result;
	}

	/**
	 * Decrypt exactly one block of ciphertext.
	 *
	 * @param in         The ciphertext.
	 * @param inOffset   Index of in from which to start considering data.
	 * @param sessionKey The session key to use for decryption.
	 * @return The plaintext generated from a ciphertext using the session key.
	 */
	public static byte[] blockDecrypt(byte[] in, int inOffset, Object sessionKey) {
		if (DEBUG)
			trace(IN, "blockDecrypt(" + in + ", " + inOffset + ", " + sessionKey + ")");
		if (DEBUG && debuglevel > 6)
			System.out.println("CT=" + toString(in, inOffset, BLOCK_SIZE));
		//
		// ....
		//
		byte[] result = new byte[BLOCK_SIZE];
		int i;
		for (i = 0; i < frog_Algorithm.BLOCK_SIZE; i++)
			result[i] = in[i + inOffset];
		result = frog_procs.decryptFrog(result, ((frog_InternalKey) sessionKey).keyD);

		if (DEBUG && debuglevel > 6) {
			System.out.println("PT=" + toString(result));
			System.out.println();
		}
		if (DEBUG)
			trace(OUT, "blockDecrypt()");
		return result;
	}

	/** A basic symmetric encryption/decryption test. */
	public static boolean self_test() {
		return self_test(BLOCK_SIZE);
	}

// own methods
//...........................................................................

	/** @return The length in bytes of the Algorithm input block. */
	public static int blockSize() {
		return BLOCK_SIZE;
	}

	static boolean self_test(int keysize) {
		if (DEBUG)
			trace(IN, "self_test(" + keysize + ")");
		boolean ok = false;
		try {
			byte[] kb = new byte[keysize];
			byte[] pt = new byte[BLOCK_SIZE];
			int i;

			for (i = 0; i < keysize; i++)
				kb[i] = (byte) i;
			for (i = 0; i < BLOCK_SIZE; i++)
				pt[i] = (byte) i;

			if (DEBUG && debuglevel > 6) {
				System.out.println("==========");
				System.out.println();
				System.out.println("KEYSIZE=" + (8 * keysize));
				System.out.println("KEY=" + toString(kb));
				System.out.println();
			}
			Object key = makeKey(kb);

			if (DEBUG && debuglevel > 6) {
				System.out.println("Intermediate Ciphertext Values (Encryption)");
				System.out.println();
			}
			byte[] ct = blockEncrypt(pt, 0, key);

			if (DEBUG && debuglevel > 6) {
				System.out.println("Intermediate Plaintext Values (Decryption)");
				System.out.println();
			}
			byte[] cpt = blockDecrypt(ct, 0, key);

			ok = areEqual(pt, cpt);
			if (!ok)
				throw new RuntimeException("Symmetric operation failed");
		} catch (Exception x) {
			if (DEBUG && debuglevel > 0) {
				debug("Exception encountered during self-test: " + x.getMessage());
				x.printStackTrace();
			}
		}
		if (DEBUG && debuglevel > 0)
			debug("Self-test OK? " + ok);
		if (DEBUG)
			trace(OUT, "self_test()");
		return ok;
	}

	static boolean prueba(int keysize) {
		boolean ok = false;
		try {
			byte[] kb = new byte[keysize];
			byte[] pt = new byte[BLOCK_SIZE];
			int i;

			for (i = 0; i < keysize; i++)
				kb[i] = (byte) 0;
			for (i = 0; i < BLOCK_SIZE; i++)
				pt[i] = (byte) 0;
//            pt[15] = -128;

			Object key = makeKey(kb);

			byte[] ct = blockEncrypt(pt, 0, key);
			System.out.println("keysize=" + keysize);
			for (i = 0; i < 16; i++) {
				if (ct[i] < 0)
					System.out.print((ct[i] + 256) + ",");
				else
					System.out.print(ct[i] + ",");
			}
			System.out.println("");

			byte[] cpt = blockDecrypt(ct, 0, key);

			ok = areEqual(pt, cpt);
			if (!ok)
				System.out.println("Decryption failed");
		} catch (Exception x) {
		}
		return ok;
	}
// utility static methods (from cryptix.util.core ArrayUtil and Hex classes)
//...........................................................................

	/** @return True iff the arrays have identical contents. */
	private static boolean areEqual(byte[] a, byte[] b) {
		int aLength = a.length;
		if (aLength != b.length)
			return false;
		for (int i = 0; i < aLength; i++)
			if (a[i] != b[i])
				return false;
		return true;
	}

	/**
	 * Returns a string of hexadecimal digits from a byte array. Each byte is
	 * converted to 2 hex symbols.
	 */
	 static String toString(byte[] ba) {
		return toString(ba, 0, ba.length);
	}

	static String toString(byte[] ba, int offset, int length) {

		char[] buf = new char[length * 2];
		for (int i = offset, j = 0, k = 0; i < offset + length;) {
			k = ba[i++];
			buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
			buf[j++] = HEX_DIGITS[k & 0x0F];
		}
		return new String(buf);
	}

	static String byte2String(byte[] ba) {
		String l = new String(ba, StandardCharsets.UTF_8);
		System.out.println(l);
		return l;
	}

}
