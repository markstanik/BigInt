package bigint;

public class Tester {
	public static void test(){
	boolean success = true;
	for(int i = -1000; i<=1000; i++) {
	for(int j = -1000; j<=1000; j++) {
	int sum = i+j;
	int x = i;
	int y = j;
	BigInteger a = BigInteger.parse(Integer.toString(i));
	BigInteger b = BigInteger.parse(j + "");
	BigInteger result = BigInteger.add(a,b);
	if(result.toString().compareTo(sum+"") != 0) {
	success = false;
	System.out.println("MY compare: " + result.toString().compareTo(sum+""));
	System.out.println("MY bigint: " + result);
	System.out.println("MY sum: " + sum);
	System.out.println(x + " " + y);
	}
	}
	}
	if(success) {
	System.out.println("All test cases passed");
	}
	else {
	System.out.println("Failed");
	}
}
}