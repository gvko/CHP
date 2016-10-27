import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Solver {
	public static void main(String[] args) {
        System.out.print("Which input file to check? Number: ");
        Scanner scanner = new Scanner(System.in);
        String file_path = "gitcode/CHP/input_files/test" + scanner.nextLine() + ".SWE";

        ArrayList<String> lines = new ArrayList<>();

        try {
            Scanner in = new Scanner(new FileReader(file_path));

            while (in.hasNext()) {
                lines.add(in.next());
            }

            if (Decoder.checkValid(lines)) {
                Problem problem = Decoder.parse(lines);
                
                Map<Character, String> solution = solve(problem);
                if (solution != null) {
                	System.out.println("Problem was successfully solved!");
                	System.out.println("The correct combination is " + solution);
                } else {
                	System.out.println("No solution was found for this problem.");
                }
            } else {
                System.out.println("The content of the file is not in SWE format.");
            }
        } catch (FileNotFoundException up) {
            System.out.print("File not found: " + file_path);
        }
	}
	
	// Solves the problem and saves the solution in a .SOL file.
	public static Map<Character, String> solve(Problem p) {
		// Apply heuristic techniques on this instance of the problem and simplify it
		simplify(p);
		
		Map<Character, Integer> curIndices = new HashMap<Character, Integer>();
		
		List<Integer> lengths = new ArrayList<Integer>();
		for (Map.Entry<Character, List<String>> entry : p.R.entrySet()) {
			curIndices.put(entry.getKey(), 0);
			lengths.add(entry.getValue().size());
		}
		
		boolean stop = false;
		while (true) {
			Map<Character, String> selection = convertToStringCombination(p, curIndices);
			if (verifyCombination(p, selection)) {
				return selection;
			}
			int i = 0;
			for (Map.Entry<Character, Integer> entry : curIndices.entrySet()) {
				curIndices.put(entry.getKey(), entry.getValue() + 1);
				
				if (entry.getValue() >= lengths.get(i) && i != curIndices.size() - 1) {
					curIndices.put(entry.getKey(), 0);
				} else if (i == curIndices.size() - 1 && entry.getValue() == lengths.get(i)) {
					stop = true;
					break;
				} else {
					break;
				}
				
				i++;
			}
			
			if (stop) {
				break;
			}
		}
		
		return null;
	}
	
	public static Map<Character, String> convertToStringCombination(Problem p, Map<Character, Integer> indices) {
		Map<Character, String> strings = new HashMap<Character, String>();
		
		// Get a string for each R for this combination
		int i = 0;
		for (Map.Entry<Character, Integer> entry : indices.entrySet()) {
			if (i >= indices.size() - 1)
				break;
			
			strings.put(entry.getKey(), p.R.get(entry.getKey()).get(entry.getValue()));
			i++;
		}
		
		return strings;
	}
	
	public static boolean verifyCombination(Problem p, Map<Character, String> combinations) {
		boolean result = true;
		for (String t : p.T) {
			char[] tChars = t.toCharArray();
			
			String expansion = "";
			for (char letter : tChars) {
				expansion += combinations.get(letter);
			}
			
			if (!p.s.contains(expansion)) {
				result = false;
				break;
			}
		}
		
		return result;
	}
	
	// Removes elements of R that are not substrings of s
	public static void simplify(Problem p) {
		for (Map.Entry<Character, List<String>> entry : p.R.entrySet()) {
			Iterator<String> iterator = entry.getValue().iterator();
			
			while (iterator.hasNext()) {
				if (!p.s.contains(iterator.next())) {
					iterator.remove();
				}
			}
		}
	}
}
