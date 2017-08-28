import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Juggle {
	
	/*	i 	 	: get input byte and assign right
	 * 	o 	  	: output var on left
	 * 	p		: output var on left + '\n'
	 * 	a,b,c,d 	: data locations
	 * 	|		: bitwise OR
	 * 	&		: bitwise AND
	 * 	^		: bitwise XOR
	 * 	~		: bitwise NOT
	 * 	>		: shift right zero fill
	 * 	<		: shift left zero fill
	 * 	=		: assignment
	 *  	[	 	: repeat enclosed until a == 0
	 * 	]	  	: close loop
	 *  	"		: num to char and implicit o
	 *  	'		: num to byte
	 */
	
	//Arraylist of loop blocks to process
	static ArrayList<String> blocks = new ArrayList<String>();
	
	//Map of characters to values, 0-9 are literals
	static HashMap<Character, BigInteger> h = new HashMap<Character, BigInteger>();
	
	static {
		
		h.put('a', new BigInteger("0"));
		h.put('b', new BigInteger("0"));
		h.put('c', new BigInteger("0"));
		h.put('d', new BigInteger("0"));
		h.put('n', new BigInteger("0"));
		h.put('0', new BigInteger("0"));
		h.put('1', new BigInteger("1"));
		h.put('2', new BigInteger("2"));
		h.put('3', new BigInteger("3"));
		h.put('4', new BigInteger("4"));
		h.put('5', new BigInteger("5"));
		h.put('6', new BigInteger("6"));
		h.put('7', new BigInteger("7"));
		h.put('8', new BigInteger("8"));
		h.put('9', new BigInteger("9"));
		
	}
	
	public static void main(String[] args) throws IOException {
		
		System.out.println();
		
		//If no file is input, don't execute program
		if(args.length < 1) return;
		
		//
		String code = getSource(args[0]);
		
		//Uses RegEx removes out all non-special characters
		code = reduce(code);
		
		//Splits blocks in the code, replacing them with ';'
		code = parse(code);
		
		//Reverses blocks to permit proper nesting
		Collections.reverse(blocks);
		
		//Interpret the remaining code
		process(code);
		
		System.out.println();
	}

	private static void process(String code) {
		
		for(int i = 0; i < code.length(); ++i) {
			
			switch(code.charAt(i)) {
				
				case 'i':
					
					if(h.get(code.charAt(i+1)) == null) {
						
						h.put('n', new BigInteger(new java.util.Scanner(System.in).nextLine()));
						
						code = code.substring(0,i) + 'n' + code.substring(i+1,code.length());
						
						--i;
						
						break;
					}
					
					h.put(code.charAt(i+1),new BigInteger(new java.util.Scanner(System.in).nextLine()));
					
					break;
					
				case 'o':
					
					System.out.print(h.get(code.charAt(i-1)));
					
					break;
					
				case 'p':
					
					if(i-1 >= 0 && h.get(code.charAt(i-1)) != null) {
						System.out.println(h.get(code.charAt(i-1)));
					}else System.out.println();
					
					break;
					
				case '\"':
					
					System.out.print((char)(h.get(code.charAt(i-1)).intValue()&0xff));
					
					code = code.substring(0,i) + 'n' + code.substring(i+1,code.length());
					h.put('n', new BigInteger(""+h.get('n').byteValue()));
					
					break;
					
				case '\'':
					
					code = code.substring(0,i) + 'n' + code.substring(i+1,code.length());
					h.put('n', new BigInteger(""+h.get('n').byteValue()));
					
					i -= 2;
					break;
				case ';':
					
					if(blocks.size() < 1) break;
					
					String temp = blocks.remove(0);
					
					while(h.get('a').intValue() > 0){
						process(temp);
						h.put('a', h.get('a').subtract(BigInteger.ONE));
						
					}
					break;
					
				case '=':
					h.put(code.charAt(i+1), h.get(code.charAt(i-1)));
					break;
				case '|':
					h.put('n', h.get(code.charAt(i-1)).or(h.get(code.charAt(i+1))));
					
					code = code.substring(0,i) + 'n' + code.substring(i+2,code.length());
					i -= 2;
					break;
				case '&':
					h.put('n', h.get(code.charAt(i-1)).and(h.get(code.charAt(i+1))));
					
					code = code.substring(0,i) + 'n' + code.substring(i+2,code.length());
					i -= 2;
					break;
				case '^':
					h.put('n', h.get(code.charAt(i-1)).xor(h.get(code.charAt(i+1))));
					
					code = code.substring(0,i) + 'n' + code.substring(i+2,code.length());
					i -= 2;
					break;
				case '<':
					h.put('n', h.get(code.charAt(i-1)).shiftLeft(h.get(code.charAt(i+1)).intValue()));
					
					code = code.substring(0,i) + 'n' + code.substring(i+2,code.length());
					i -= 2;
					break;
				case '>':
					h.put('n', h.get(code.charAt(i-1)).shiftRight(h.get(code.charAt(i+1)).intValue()));
					
					code = code.substring(0,i) + 'n' + code.substring(i+2,code.length());
					i -= 2;
					break;
				case '~':
					
					h.put('n', h.get(code.charAt(i-1)).negate());
					
					code = code.substring(0,i) + 'n' + code.substring(i+1,code.length());
					break;
					
			}
			
		}
	}

	private static String parse(String code) {
		
		while(true) {
			
			int iopen  = code.indexOf('[') + 1;
			int iclose = code.indexOf(']');
			
			if(iopen-1 < 0 || iclose < 0) {
				break;
			}
			int oldiclose = iopen;
			while(code.substring(oldiclose,iclose).contains("[")) {
				oldiclose = iclose;
				iclose = code.indexOf(']',iclose+1);
			}
			String sub = code.substring(iopen, iclose);
			
			sub = parse(sub);
			
			code = code.substring(0,iopen-1) + ";" + code.substring(iclose,code.length());
			code = code.replaceFirst("\\]", "");
			
			
			blocks.add(sub);
		}
		return code;
	}

	private static String reduce(String code) {
		
		return code.replaceAll("[^&0-9<=>\\[\\]\\^a-diop|~\"]", "");
		
	}

	private static String getSource(String str) throws IOException {
		
		String result = "";
		
		BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(str)));
		String in = b.readLine();
		
		while(in != null) {
			result += in;
			in = b.readLine();
		}
		
		return result;
		
	}

}
