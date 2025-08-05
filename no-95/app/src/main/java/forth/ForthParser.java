package forth;

import java.util.ArrayList;
import java.util.List;

public class ForthParser {
    public ForthParser() {
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public List<ForthInterpreter.Instruction> parse(String line) {
        var instructions = new ArrayList<ForthInterpreter.Instruction>();
        var scanner = new ForthScanner(line);
        // Parse the input using the scanner
        var token = scanner.nextToken();
        var inComment = false;
        while (token != null) {
            if (token.equals("(") && !inComment) {
                inComment = true;
            } else if (inComment && !token.equals(")")) {
                // skip
            } else if (inComment) {
                inComment = false;
            } else if (token.matches("-?\\d+")) {
                var i = Integer.parseInt(token);
                instructions.add(
                        context -> context.push(i)
                );
            } else if (token.equals(".\"")) {
                var strBuilder = new StringBuilder();
                strBuilder.append(token);
                token = scanner.nextToken();
                while (token != null && !token.endsWith("\"")) {
                    strBuilder.append(' ').append(token);
                    token = scanner.nextToken();
                }
                if (token == null) {
                    throw new RuntimeException("No end quote \" found");
                }
                strBuilder.append(' ').append(token);
                var builderString = strBuilder.toString(); // format ." _______ _____" (3 --> -1)
                var toPrint = builderString.substring(3, builderString.length()-1);
                instructions.add(
                        context -> context.executePrint(toPrint)
                );
            } else {
                String t = token;
                instructions.add(
                        context -> context.executeWord(t)
                );
            }
            token = scanner.nextToken();
        }
        return instructions;
    }

}
