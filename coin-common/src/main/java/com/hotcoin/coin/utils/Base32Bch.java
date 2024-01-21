package com.hotcoin.coin.utils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2018 Tobias Brandt
 * 
 * Distributed under the MIT software license, see the accompanying file LICENSE
 * or http://www.opensource.org/licenses/mit-license.php.
 */
public class Base32Bch {

	public static final String CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

	private static final char[] CHARS = CHARSET.toCharArray();

	private static final BigInteger POLYMOD_AND_CONSTANT = new BigInteger("07ffffffff", 16);

	private static final BigInteger[] POLYMOD_GENERATORS = new BigInteger[] { new BigInteger("98f2bc8e61", 16),
			new BigInteger("79b76d99e2", 16), new BigInteger("f33e5fb3c4", 16), new BigInteger("ae2eabe2a8", 16),
			new BigInteger("1e4f43e470", 16) };


	private static Map<Character, Integer> charPositionMap;
	static {
		charPositionMap = new HashMap<>();
		for (int i = 0; i < CHARS.length; i++) {
			charPositionMap.put(CHARS[i], i);
		}
		if (charPositionMap.size() != 32) {
			throw new RuntimeException("The charset must contain 32 unique characters.");
		}
	}

	/**
	 * Encode a byte array as base32 string. This method assumes that all bytes
	 * are only from 0-31
	 * 
	 * @param byteArray
	 * @return
	 */
	public static String encode(byte[] byteArray) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			int val = (int) byteArray[i];

			if (val < 0 || val > 31) {
				throw new RuntimeException("This method assumes that all bytes are only from 0-31. Was: " + val);
			}

			sb.append(CHARS[val]);
		}

		return sb.toString();
	}

	/**
	 * Decode a base32 string back to the byte array representation
	 * 
	 * @param base32String
	 * @return
	 */
	public static byte[] decode(String base32String) {
		byte[] bytes = new byte[base32String.length()];

		char[] charArray = base32String.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			Integer position = charPositionMap.get(charArray[i]);
			if (position == null) {
				throw new RuntimeException("There seems to be an invalid char: " + charArray[i]);
			}
			bytes[i] = (byte) ((int) position);
		}

		return bytes;
	}
	public static byte[] getPrefixBytes(String prefixString) {
		byte[] prefixBytes = new byte[prefixString.length()];

		char[] charArray = prefixString.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			prefixBytes[i] = (byte) (charArray[i] & 0x1f);
		}
		return prefixBytes;
	}

	public static byte[] convertBits(byte[] bytes8Bits, int from, int to, boolean strictMode) {
		int length = (int) (strictMode ? Math.floor((double) bytes8Bits.length * from / to)
				: Math.ceil((double) bytes8Bits.length * from / to));
		int mask = ((1 << to) - 1) & 0xff;
		byte[] result = new byte[length];
		int index = 0;
		int accumulator = 0;
		int bits = 0;
		for (int i = 0; i < bytes8Bits.length; i++) {
			byte value = bytes8Bits[i];
			accumulator = (((accumulator & 0xff) << from) | (value & 0xff));
			bits += from;
			while (bits >= to) {
				bits -= to;
				result[index] = (byte) ((accumulator >> bits) & mask);
				++index;
			}
		}
		if (!strictMode) {
			if (bits > 0) {
				result[index] = (byte) ((accumulator << (to - bits)) & mask);
				++index;
			}
		} else {
			if (!(bits < from && ((accumulator << (to - bits)) & mask) == 0)) {
				throw new RuntimeException("Strict mode was used but input couldn't be converted without padding");
			}
		}

		return result;
	}

	public static byte[] calculateChecksumBytesPolymod(byte[] checksumInput) {
		BigInteger c = BigInteger.ONE;

		for (int i = 0; i < checksumInput.length; i++) {
			byte c0 = c.shiftRight(35).byteValue();
			c = c.and(POLYMOD_AND_CONSTANT).shiftLeft(5)
					.xor(new BigInteger(String.format("%02x", checksumInput[i]), 16));

			if ((c0 & 0x01) != 0)
				c = c.xor(POLYMOD_GENERATORS[0]);
			if ((c0 & 0x02) != 0)
				c = c.xor(POLYMOD_GENERATORS[1]);
			if ((c0 & 0x04) != 0)
				c = c.xor(POLYMOD_GENERATORS[2]);
			if ((c0 & 0x08) != 0)
				c = c.xor(POLYMOD_GENERATORS[3]);
			if ((c0 & 0x10) != 0)
				c = c.xor(POLYMOD_GENERATORS[4]);
		}

		byte[] checksum = c.xor(BigInteger.ONE).toByteArray();
		if (checksum.length == 5) {
			return checksum;
		} else {
			byte[] newChecksumArray = new byte[5];

			System.arraycopy(checksum, Math.max(0, checksum.length - 5), newChecksumArray,
					Math.max(0, 5 - checksum.length), Math.min(5, checksum.length));

			return newChecksumArray;
		}

	}
}
