package edu.indiana.cs.math;

public class HexString {
	public static int toDecimal(String hexString) throws NumberFormatException{
		int result = 0;
		int last = hexString.length() - 1;
		char character;
		int tempValue;
		
		for (int i = 0; i < last; i++) {
			character = hexString.charAt(i);
			
			if (character >= '0' && character <= '9') {
				tempValue = (int)character - 48;
			} else if (character >= 'A' && character <= 'F') {
				tempValue = (int)character - 55;
			} else {
				throw new NumberFormatException();
			}
			
			result = (result + tempValue) * 16;
		}
		
		// Just add the last character's value to the result straight-up
		character = hexString.charAt(last);
		
		if (character >= '0' && character <= '9') {
			tempValue = (int)character - 48;
		} else if (character >= 'A' && character <= 'F') {
			tempValue = (int)character - 55;
		} else {
			throw new NumberFormatException();
		}
		
		result = result + tempValue;
		
		return result;
	}
	
	public static String toHexString(int decimal) {
		int remainder;
		String result = new String();
		Character hexNumber;
		
		while (decimal != 0) {
			remainder = decimal % 16;
			
			if (remainder > 9) {
				hexNumber = (char) (remainder + 55);
			} else {
				hexNumber = (char) (remainder + 48);
			}
			
			result = hexNumber + result;
			decimal /= 16;
		}
		
		while (result.length() < 2)
			result = '0' + result;
		
		return result;
	}

	public static void main (String args[]) {
		System.out.println(toDecimal("7FE"));
		System.out.println(toHexString(22));
	}
}