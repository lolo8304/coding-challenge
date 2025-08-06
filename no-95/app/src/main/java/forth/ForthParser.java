package forth;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class ForthParser {
    public ForthParser() {
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public List<ForthInterpreter.Instruction> parse(String line) {
        var instructions = new ArrayList<ForthInterpreter.Instruction>();
        var controlStack = new ArrayDeque<Integer>();
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
                var toPrint = builderString.substring(3, builderString.length() - 1);
                instructions.add(
                        context -> context.executePrint(toPrint)
                );
            } else if (token.equals("if")) {
                var ifIndex = instructions.size();
                instructions.add(null); // fill later
                controlStack.push(ifIndex);
            } else if (token.equals("else")) {
                var ifIndex = controlStack.pop(); // from if
                var elseIndex = instructions.size();
                instructions.add(null); // fill later
                controlStack.push(-elseIndex); // replace with else
                instructions.set(ifIndex, ctx -> {
                    if (ctx.pop().equals(0)) {
                        ctx.jumpTo(elseIndex + 1);
                    }
                });
            } else if (token.equals("then")) {
                int patchIndex = controlStack.pop(); // from if or else
                var elseAlreadyChecked = patchIndex < 0;
                patchIndex = Math.abs(patchIndex);
                int thenIndex = instructions.size();
                if (!elseAlreadyChecked) {
                    instructions.set(patchIndex, ctx -> {
                        if (ctx.pop() == 0) ctx.jumpTo(thenIndex);
                    });
                } else {
                    instructions.set(patchIndex, ctx -> ctx.jumpTo(thenIndex));
                }
            } else if (token.equals(":")) {
                var expression = new StringBuilder();
                token = scanner.nextToken();
                var word = token;
                token = scanner.nextToken();
                while (token != null && !token.equals(";")) {
                    expression.append(" ").append(token);
                    token = scanner.nextToken();
                }
                if (token == null) {
                    throw new RuntimeException("parsing definition : __ ; - token ; not found");
                }
                var expressionSource = expression.toString().trim();
                if (expressionSource.isEmpty()) {
                    throw new RuntimeException("parsing definition : __ ; - expression is empty");
                }
                var expressionInstructions = this.parse(expression.toString());
                instructions.add(
                    context -> context.define(word, expressionInstructions)
                );
            } else if (token.equals("do")) {
                int doStart = instructions.size();
                instructions.add(ctx -> {
                    int start = ctx.pop();
                    int limit = ctx.pop();
                    ctx.pushLoop(start, limit);
                });
                controlStack.push(doStart);
            } else if (token.equals("loop")) {
                int loopStart = controlStack.pop();
                instructions.add(ctx -> {
                    if (ctx.incrementLoop()) {
                        ctx.jumpTo(loopStart+1);
                    } else {
                        ctx.popLoop();
                    }
                });
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
