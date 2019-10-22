package bigint;

/**
 * This class encapsulates a BigInteger, i.e. a positive or negative integer
 * with any number of digits, which overcomes the computer storage length
 * limitation of an integer.
 * 
 */
public class BigInteger {

	/**
	 * True if this is a negative integer
	 */
	boolean negative;

	/**
	 * Number of digits in this integer
	 */
	int numDigits;

	/**
	 * Reference to the first node of this integer's linked list representation
	 * NOTE: The linked list stores the Least Significant Digit in the FIRST node.
	 * For instance, the integer 235 would be stored as: 5 --> 3 --> 2
	 * 
	 * Insignificant digits are not stored. So the integer 00235 will be stored as:
	 * 5 --> 3 --> 2 (No zeros after the last 2)
	 */
	DigitNode front;

	/**
	 * Initializes this integer to a positive number with zero digits, in other
	 * words this is the 0 (zero) valued integer.
	 */
	public BigInteger() {
		negative = false;
		numDigits = 0;
		front = null;
	}

	/**
	 * Parses an input integer string into a corresponding BigInteger instance. A
	 * correctly formatted integer would have an optional sign as the first
	 * character (no sign means positive), and at least one digit character
	 * (including zero). Examples of correct format, with corresponding values
	 * Format Value +0 0 -0 0 +123 123 1023 1023 0012 12 0 0 -123 -123 -001 -1 +000
	 * 0
	 * 
	 * Leading and trailing spaces are ignored. So " +123 " will still parse
	 * correctly, as +123, after ignoring leading and trailing spaces in the input
	 * string.
	 * 
	 * Spaces between digits are not ignored. So "12 345" will not parse as an
	 * integer - the input is incorrectly formatted.
	 * 
	 * An integer with value 0 will correspond to a null (empty) list - see the
	 * BigInteger constructor
	 * 
	 * @param integer Integer string that is to be parsed
	 * @return BigInteger instance that stores the input integer.
	 * @throws IllegalArgumentException If input is incorrectly formatted
	 */
	public static BigInteger parse(String integer) throws IllegalArgumentException {
		boolean firstDigit = false;
		BigInteger bigNum = new BigInteger();
		int i;
		integer = integer.trim();

		if (integer.charAt(0) == '-') {
			bigNum.negative = true;
			i = 1;
		} else if (integer.charAt(0) == '+') {
			bigNum.negative = false;
			i = 1;
		} else {
			i = 0;
		}
		while (i < integer.length()) {

			char cdigit = integer.charAt(i);

			if (Character.isDigit(cdigit)) {
				if (cdigit == '0' && firstDigit == false) {
					i++;
				} else {
					int digit = Character.getNumericValue(cdigit);
					firstDigit = true;
					bigNum.front = new DigitNode(digit, bigNum.front);
					bigNum.numDigits++;
					i++;
				}
			} else {
				throw new IllegalArgumentException();
			}

		}
		System.out.println(bigNum.numDigits);
		System.out.println(bigNum.negative);

		return bigNum;

	}

	/**
	 * Adds the first and second big integers, and returns the result in a NEW
	 * BigInteger object. DOES NOT MODIFY the input big integers.
	 * 
	 * NOTE that either or both of the input big integers could be negative. (Which
	 * means this method can effectively subtract as well.)
	 * 
	 * @param first  First big integer
	 * @param second Second big integer
	 * @return Result big integer
	 */
	public static BigInteger add(BigInteger first, BigInteger second) {
		// this is the code that sets up stuff
		BigInteger ans = new BigInteger();
		BigInteger large = new BigInteger();
		BigInteger small = new BigInteger();
		boolean runone = true;
		boolean same = true;
		boolean neg1 = false;
		int carry = 0;
		if (first.numDigits >= second.numDigits) {
			large = parse(first.toString());
			small = parse(second.toString());
		} else if (second.numDigits > first.numDigits) {
			large = parse(second.toString());
			small = parse(first.toString());
		}
		DigitNode ptr = large.front;
		DigitNode ptr2 = small.front;
		DigitNode ptrAns = null;
		DigitNode prevAns = new DigitNode(0, null);
		int x = first.numDigits;
		if(first.front == null) {
			ans = second;
			ans.numDigits = second.numDigits;
			return ans;
		}
		else if(second.front == null) {
			ans = first;
			ans.numDigits = first.numDigits;
			return ans;
		}
		// this code is for if one of the numbers is positive and one of them is
		// negative
		if (first.negative == true && second.negative == false || first.negative == false && second.negative == true) {
			if (first.numDigits == second.numDigits) { // this is for when they have the same amount of digits
				DigitNode check1 = first.front;
				DigitNode check2 = second.front;
				for (int i = 1; i <= x; i++) {
					if (i == x) {
						if (check1.digit > check2.digit) {
							large = first;
							small = second;
							same = false;
							//System.out.println("hello");
							break;
						} else if (check2.digit > check1.digit) {
							large = second;
							small = first;
							//System.out.println("hello2");
							same = false;
							break;
						} else {
							i = 0;
							check1 = first.front;
							check2 = second.front;
							x = x - 1;
						}
					}
					if (i != 0) {
						check1 = check1.next;
						check2 = check2.next;
					}
				}
			} else if (first.numDigits > second.numDigits) {
				large = first;
				small = second;
				same = false;
			} else {
				large = second;
				small = first;
				same = false;
			}
			ptr = large.front;
			ptr2 = small.front;
			if (large.negative == true) {
				ans.negative = true;
			} else {
				ans.negative = false;
			}

			if (same == false) {
				while (ptr != null) {

					if (ptr2 != null) {
						
						if (ptr.digit < ptr2.digit) {
							ptr.digit = ptr.digit + 10;
								ptr.next.digit = ptr.next.digit - 1;
						}
						if (ptr2.next == null && ptr.next == null && ptr.digit == ptr2.digit) {
							ptrAns = null;
						} else {
							ptrAns = new DigitNode(ptr.digit - ptr2.digit, null);
						}
					} else {
						ptrAns = new DigitNode(ptr.digit, null);
						if(neg1 == true && ptrAns.digit == 0) {
							ptrAns.digit = 9;
						}
						else if (neg1 == true && ptrAns.digit != 0) {
							ptrAns.digit = ptrAns.digit -1;
							neg1 = false;
						}
						if(ptrAns.digit == -1) {
							ptrAns.digit = 9;
							neg1 = true;
						}
					}
					prevAns.next = ptrAns;
					prevAns = ptrAns;
					if (runone == true) {
						ans.front = prevAns;
						runone = false;
					}
					ans.numDigits++;
					ptr = ptr.next;
					if (ptr2 != null) {
						ptr2 = ptr2.next;
					}
				}
			}
			if (same == true) {
				ans.negative = false;
				ans.front = new DigitNode(0, null);
			}
		}

		// this code is for two numbers that have the same sign
		if ((first.negative == false && second.negative == false)
				|| (first.negative == true && second.negative == true)) { // if the numbers are both positive
			while (ptr != null) {
				ptrAns = new DigitNode(carry + ptr.digit + ptr2.digit, null);
				prevAns.next = ptrAns;
				prevAns = ptrAns;
				if (runone == true) {
					ans.front = prevAns;
					runone = false;
				}
				if (ptrAns.digit > 9) {
					carry = 1;
					ptrAns.digit = ptrAns.digit - 10;
					if (ptr.next == null) {
						ptrAns.next = new DigitNode(carry, ptrAns.next);
						ans.numDigits++;
					}

				} else {
					carry = 0;
				}

				ans.numDigits++;
				ptr = ptr.next;
				if (ptr2.next == null) {
					ptr2 = new DigitNode(0, ptr2.next);
				} else {
					ptr2 = ptr2.next;
				}
			}
		}
		if (first.negative == true && second.negative == true) {
			ans.negative = true;
		}
		DigitNode check3 = ans.front;
		DigitNode check3prev = null;
		while (check3 != null && same == false)
			if (check3.next == null) {
				if (check3.digit == 0) {
					check3prev.next = null;
					check3 = ans.front;

				} else {
					break;
				}
			} else {
				check3prev = check3;
				check3 = check3.next;
			}
		if(neg1 ==true) {
			ptrAns = ans.front;
			prevAns = null;
			while(ptrAns.next != null) {
				prevAns = ptrAns;
				ptrAns = ptrAns.next;
			}
			prevAns.next.digit = prevAns.next.digit-1;
			if(prevAns.next.digit == 0) {
				prevAns.next = null;
			}
			
		}
		
		ans.numDigits = 0;
		ptrAns = ans.front;
		while(ptrAns != null) {
			ptrAns = ptrAns.next;
			ans.numDigits++;
		}
		return ans;
	}

	/**
	 * Returns the BigInteger obtained by multiplying the first big integer with the
	 * second big integer
	 * 
	 * This method DOES NOT MODIFY either of the input big integers
	 * 
	 * @param first  First big integer
	 * @param second Second big integer
	 * @return A new BigInteger which is the product of the first and second big
	 *         integers
	 */
	public static BigInteger multiply(BigInteger first, BigInteger second) {

		BigInteger ans = new BigInteger();
		BigInteger large = new BigInteger();
		BigInteger small = new BigInteger();
		int carry = 0;
		int mult;
		int mult2;
		if (first.numDigits > second.numDigits) {
			large = parse(first.toString());
			small = parse(second.toString());

		} else {
			large = parse(second.toString());
			small = parse(first.toString());
		}
		DigitNode firstptr = large.front;
		DigitNode secondptr = small.front;
		
		
		if(second.front == null || first.front == null) {
			ans.front = null;
		}
		else {
			mult = firstptr.digit * secondptr.digit;
		ans.front = new DigitNode(mult%10, null);
			
		DigitNode ansptr = ans.front;
		DigitNode ansptr2 = ans.front;
		
		carry = mult / 10;
		firstptr = firstptr.next;
		while(secondptr != null) {
			while(firstptr != null) {
				if(ansptr.next == null) {			
					mult2 = firstptr.digit * secondptr.digit;
					ansptr.next = new DigitNode((mult2 + carry) % 10, null);
					ansptr = ansptr.next;
					carry = (mult2 + carry)/10;
					ans.numDigits++;
					if (carry != 0) {
						ansptr.next = new DigitNode(carry,null);
						ans.numDigits++;
						carry = 0;
					}
					firstptr = firstptr.next;
				}
				else {
					mult2 = firstptr.digit * secondptr.digit + carry;
					carry = (ansptr.next.digit + mult2)/10;
					ansptr.next.digit = (ansptr.next.digit + mult2)%10;
					ansptr = ansptr.next;
					firstptr = firstptr.next;
				}
				
			}
			if (carry != 0) {
				ansptr.next = new DigitNode(carry,null);
				ans.numDigits++;
				carry = 0;
			}
			firstptr = large.front;
			secondptr = secondptr.next;
			ansptr = ansptr2;
			ansptr2 = ansptr2.next;
		}
		if((first.negative && second.negative) || (first.negative == false && second.negative ==false)) {
			ans.negative = false;
		}
		else {
			ans.negative = true;
		}
		}
		ans.numDigits = 0;
		firstptr = ans.front;
		while(firstptr != null) {
			firstptr = firstptr.next;
			ans.numDigits++;
		}
		return ans;
	
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (front == null) {
			return "0";
		}
		String retval = front.digit + "";
		for (DigitNode curr = front.next; curr != null; curr = curr.next) {
			retval = curr.digit + retval;
		}

		if (negative) {
			retval = '-' + retval;
		}
		return retval;
	}
}

