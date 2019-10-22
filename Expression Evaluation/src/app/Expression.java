package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";

	/**
	 * Populates the vars list with simple variables, and arrays lists with arrays
	 * in the expression. For every variable (simple or array), a SINGLE instance is
	 * created and stored, even if it appears more than once in the expression. At
	 * this time, values for all variables and all array items are set to zero -
	 * they will be loaded from a file in the loadVariableValues method.
	 * 
	 * @param expr   The expression
	 * @param vars   The variables array list - already created by the caller
	 * @param arrays The arrays array list - already created by the caller
	 */
	public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		// Variable variable = new Variable();
		Pattern findVar = Pattern.compile("[a-zA-Z]{1,}"); // gets us only the variables
		Matcher regexVar = findVar.matcher(expr);
		Pattern findArr = Pattern.compile("[a-zA-Z]+[\\[]{1,}"); // gets us the arrays
		Matcher regexArr = findArr.matcher(expr);
		boolean isArray;
		ArrayList<String> temp = new ArrayList<>(1000);
		while (regexArr.find()) {
			if (regexArr.group().length() != 0) {
				Array array = new Array(regexArr.group().trim().substring(0, regexArr.group().length() - 1));
				arrays.add(array);
				temp.add(regexArr.group().trim().substring(0, regexArr.group().length() - 1));
				System.out.println(arrays);
			}
		}
		temp.trimToSize();

		while (regexVar.find()) {
			// if(regexVar.group().length() != 0) {
			isArray = false;

			// System.out.println(temp.get(0));
			for (int i = 0; i < temp.size(); i++) {
				if (regexVar.group().trim().equals(temp.get(i))) {
					isArray = true;
					break;
				}
			}
			if (isArray == false) {
				Variable variable = new Variable(regexVar.group().trim());
				vars.add(variable);
				System.out.println(variable);
			}
		}
		// }
	}

	/**
	 * Loads values for variables and arrays in the expression
	 * 
	 * @param sc Scanner for values input
	 * @throws IOException If there is a problem with the input
	 * @param vars   The variables array list, previously populated by
	 *               makeVariableLists
	 * @param arrays The arrays array list - previously populated by
	 *               makeVariableLists
	 */
	public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays)
			throws IOException {
		while (sc.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String tok = st.nextToken();
			Variable var = new Variable(tok);
			Array arr = new Array(tok);
			int vari = vars.indexOf(var);
			int arri = arrays.indexOf(arr);
			if (vari == -1 && arri == -1) {
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2) { // scalar symbol
				vars.get(vari).value = num;
			} else { // array symbol
				arr = arrays.get(arri);
				arr.values = new int[num];
				// following are (index,val) pairs
				while (st.hasMoreTokens()) {
					tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok, " (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					arr.values[index] = val;
				}
			}
		}
	}

	/**
	 * Evaluates the expression.
	 * 
	 * @param vars   The variables array list, with values for all variables in the
	 *               expression
	 * @param arrays The arrays array list, with values for all array items
	 * @return Result of evaluation
	 */
	public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		String numExpr = expr;
		//boolean isArray = false;
		float ans = 0;
		String inArray;
		float arrayAns;
		int index;
		String putBack;
		String editName;
		
		
		Pattern findArr = Pattern.compile("[a-zA-Z]{1,}"); // gets us the arrays
		Matcher regexArr = findArr.matcher(numExpr);
		while(regexArr.find()) {
			for(int j = 0; j<arrays.size();j++) {
				if(arrays.get(j).name.equals(regexArr.group().trim())) {
					//System.out.println(j);
					inArray = arr(numExpr, vars, arrays);
					//System.out.println(inArray);
					arrayAns = fullEval(inArray, vars, arrays);
					index = (int)arrayAns;
					putBack =  Float.toString((float)arrays.get(j).values[index]);
					editName = arrays.get(j).name;
					numExpr = numExpr.replaceAll(arrays.get(j).name, editName);
					numExpr = numExpr.replaceAll(editName+ "\\[(.*?)\\]",putBack );
					//System.out.println(numExpr);
					//isArray = true;
				}
			}
		}

		for (int i = 0; i < vars.size(); i++) {
			float value = vars.get(i).value;
			String strValue = Float.toString(value);
			// System.out.println(strValue);
			// System.out.println(numExpr);
			numExpr = numExpr.replaceAll(vars.get(i).name, strValue);
		}
		ans = fullEval(numExpr, vars, arrays);
	
		return ans;
	}

	private static String paren(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		boolean firstBr = true;
		int pos = 0;
		int posF = 0;
		int openBr = 0;
		String subExpr = expr;
		for (int i = 0; i < expr.length(); i++) {
			if (expr.charAt(i) == '(') {
				if (firstBr == true) {
					pos = i + 1;
					firstBr = false;
				}
				openBr++;
			}
			if (expr.charAt(i) == ')') {
				openBr--;
				if (openBr == 0) {
					posF = i;
					break;
				}
			}
		}
		if (posF != 0) {
			subExpr = expr.substring(pos, posF);
		}
		// System.out.println(subExpr);
		if (firstBr == false) {
			return paren(subExpr, vars, arrays);
		} else {
			return subExpr;
		}
	}

	private static float math(float num1, float num2, String operator) {
		float x;
		switch (operator) {
		case "+":
			x = (num1 + num2);
			break;
		case "*":
			x = (num1 * num2);
			
			break;
		case "-":
			x = (num1 - num2);
			break;
		case "/":
			x = (num1 / num2);
			break;
		default:
			
			return 0;
		}
		return x;
	}

	private static String simplify(String expr) {
		Stack<String> Op = new Stack<String>();
		Stack<Float> Num = new Stack<Float>();
		Stack<Float> revNum = new Stack<Float>();
		Stack<String> revOp = new Stack<String>();
		Pattern findOp = Pattern.compile("[\\+|\\-|\\*|\\/|]");
		Pattern findNum = Pattern.compile("[0-9\\.?]{1,}");
		Matcher matchOp = findOp.matcher(expr);
		Matcher matchNum = findNum.matcher(expr);
		boolean notOne = true;
		boolean isNeg = false;
		float numTop = 0;
		float numBot = 0;
		float numStack = 0;
		float temp = 0;
		String currOp;
		String tempOp;
		while (matchOp.find()) {
			Op.push(matchOp.group().trim());
			// System.out.println("matchOP: " + matchOp.group().trim());
		}
		while (matchNum.find()) {
			numStack = Float.parseFloat(matchNum.group().trim());
			Num.push(numStack);
			 //System.out.println(Num);
		}
		while (Num.size() != 0) {
			revNum.push(Num.pop());
			if (Op.size() != 0) {
				revOp.push(Op.pop());
			}
		}
		if(revNum.size() == revOp.size()) {
			revOp.pop();
			isNeg = true;
		}
		while (revOp.size() != 0) {
			notOne = false;
			System.out.println(revOp.peek());
			
			numTop = revNum.pop();
			currOp = revOp.pop();
			if (revOp.isEmpty() == false) {
				if ((currOp.equals("+") || currOp.equals("-"))
						&& (revOp.peek().equals("/") || revOp.peek().equals("*"))) {
					temp = numTop;
					tempOp = currOp;
					numTop = revNum.pop();
					//System.out.println(numTop);
					currOp = revOp.pop();
					System.out.println(currOp);
					numBot = revNum.pop();
					numTop = math(numTop, numBot, currOp);
					revNum.push(numTop);
					revNum.push(temp);
					revOp.push(tempOp);
				}

				else {
					numBot = revNum.pop();
					numTop = math(numTop, numBot, currOp);
					// System.out.println(currOp);
					revNum.push(numTop);
				}
			} else {
				numBot = revNum.pop();
				numTop = math(numTop, numBot, currOp);
				// System.out.println(currOp);
				revNum.push(numTop);
			}

		}
		if (notOne == true) {
			return expr;
		}
		if(isNeg == true) {
			numTop = numTop *-1;
		}
		String simple = Float.toString(numTop);
		return simple;

	}

	private static String arr(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		boolean firstBr = true;
		int pos = 0;
		int posF = 0;
		int openBr = 0;
		//int j = 0;
		String subExpr = expr;
		for (int i = 0; i < expr.length(); i++) {
			if (expr.charAt(i) == '[') {
				if (firstBr == true) {
					pos = i + 1;
					firstBr = false;
					
				}
				openBr++;
			}
			if (expr.charAt(i) == ']') {
				openBr--;
				if (openBr == 0) {
					posF = i;
					
					break;
				}
			}
			//j++;
		}
		if (posF != 0) {
			subExpr = expr.substring(pos, posF);
		}
		// System.out.println(subExpr);
		if (firstBr == false) {
			return arr(subExpr, vars, arrays);
		} else {
			return subExpr;
		}
	}

	private static float fullEval(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		String numExpr = expr;
		boolean isOp = true;
		float ans = 0;
		while (isOp == true) {
			String inExpr = paren(numExpr, vars, arrays);
			 //System.out.println(inExpr);
			String strAns = simplify(inExpr);
			// System.out.println(strAns);
			int j = 0;
			while (j < numExpr.length()) {
				if (numExpr.charAt(j) == '(') {
					inExpr = "(" + inExpr + ")";
					break;
				}
				j++;
			}
			// inExpr = "(" + inExpr + ")";
			 //System.out.println(inExpr);
			inExpr = inExpr.replaceAll("[a-zA-Z]+[\\[]{1,}", "");
			inExpr = inExpr.replaceAll("[\\]]{1,}", "");
			inExpr = inExpr.replaceAll("\\+", "\\\\+");
			inExpr = inExpr.replaceAll("\\-", "\\\\-");
			inExpr = inExpr.replaceAll("\\*", "\\\\*");
			inExpr = inExpr.replaceAll("\\(", "\\\\(");
			inExpr = inExpr.replaceAll("\\)", "\\\\)");
			numExpr = numExpr.replaceAll(inExpr, strAns);
			 System.out.println(strAns);
			ans = Float.parseFloat(strAns);
			//System.out.println(inExpr);
			// System.out.println(numExpr);
			// System.out.println("allensukx");
			int i;
			if(numExpr.charAt(0) == '-') {
			 i = 1;
			}
			else {
			 i = 0;
			}
			while (i < numExpr.length()) {
				isOp = true;
				char x = numExpr.charAt(i);
				// System.out.println("allensukx2");
				if (x == '+' || x == '-' || x == '/' || x == '*') {
					break;
				}
				isOp = false;
				i++;
			}

		}
		return ans;
	}
}
