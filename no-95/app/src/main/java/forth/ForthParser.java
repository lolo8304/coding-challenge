package forth;

import forth.memory.Constant;
import forth.memory.Variable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class ForthParser {
    public ForthParser() {
    }

    public List<ForthInterpreter.Instruction> parse(String line) {
        var instructions = new ArrayList<ForthInterpreter.Instruction>();
        var controlStack = new ArrayDeque<Integer>();
        var scanner = new ForthScanner(line);
        // Parse the input using the scanner
        var token = scanner.nextToken();
        while (token != null) {
            var tokenLower = token.toLowerCase();
            var shallReadTokenAtTheEnd = true; // flag to read token at the end of the loop
            if (token.equals("\\")) {
                token = scanner.nextTokenAfterNewLine();
                shallReadTokenAtTheEnd = false;
            } else if (token.equals("(")) {
                // scan many ( comments
                while (token != null && token.equals("(")) {
                    token = scanner.nextToken();
                }
                while (token != null && !token.equals(")")) {
                    token = scanner.nextToken();
                }
                if (token == null) {
                    throw new RuntimeException("parsing comment - no end ) found");
                }
                // scan many ) comments
                while (token != null && token.equals(")")) {
                    token = scanner.nextToken();
                }
                shallReadTokenAtTheEnd = false;
            } else if (token.matches("-?\\d+")) {
                var i = Long.parseLong(token);
                instructions.add(
                        context -> context.push(i)
                );
            } else if (tokenLower.equals("s\"")) {
                token = scanner.nextTokenUntil('\"');
                var builderString = "%s %s\"".formatted(tokenLower, token);
                var constantString = token;
                instructions.add(
                        context -> {
                            Variable variable;
                            if (context.hasVariable(builderString)) {
                                variable = context.getVariable(builderString);
                            } else {
                                variable = context.defineVariable(builderString, (long)constantString.length());
                                context.setStringMemory(variable.getAddress(), constantString);
                            }
                            context.push(variable.getAddress());
                            context.push(variable.getLength());
                        }
                );
            } else if (tokenLower.equals("z\"")) {
                token = scanner.nextTokenUntil('\"');
                var zString = token;
                instructions.add(
                        context -> {
                            var address = context.pop(); // pop the address of the z-string
                            context.cellAllot((long)zString.length()+1); // +1 for the zero terminator
                            context.setStringMemory(address, zString);
                            context.setCell(address + zString.length(), 0L); // zero terminator
                            context.push(address + zString.length() + 1); // push the address of the zero terminator
                        }
                );
            } else if (tokenLower.equals(".\"")) {
                token = scanner.nextTokenUntil('\"');
                var toPrint = token;
                instructions.add(
                        context -> context.executePrint(toPrint)
                );
            } else if (tokenLower.equals(",\"")) {
                token = scanner.nextTokenUntil('\"');
                var hereString = token;
                instructions.add(
                        context -> {
                            context.pop(); // pop the address of here
                            var address = context.cellAllot((long)hereString.length());
                            context.setStringMemory(address, hereString);
                            context.push(address+hereString.length());
                        }
                );
            } else if (tokenLower.equals("if")) {
                var ifIndex = instructions.size();
                instructions.add(null); // fill later
                controlStack.push(ifIndex);
            } else if (tokenLower.equals("else")) {
                var ifIndex = controlStack.pop(); // from if
                var elseIndex = instructions.size();
                instructions.add(null); // fill later
                controlStack.push(-elseIndex); // replace with else
                instructions.set(ifIndex, ctx -> {
                    if (ctx.pop().equals(0L)) {
                        ctx.jumpTo(elseIndex + 1);
                    }
                });
            } else if (tokenLower.equals("then")) {
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
            } else if (tokenLower.equals("create")) {
                token = scanner.nextToken();
                if (token == null) {
                    throw new RuntimeException("parsing definition create - word not found");
                }
                var word = token;
                instructions.add(
                        context -> {
                            context.push(context.defineVariable(word, 0L).getAddress());
                        }
                );
            } else if (tokenLower.equals("char")) {
                token = scanner.nextToken();
                if (token == null) {
                    throw new RuntimeException("parsing definition char expects a character");
                }
                var character = token;
                if (character.length() != 1) {
                    throw new RuntimeException("parsing definition char - expected a single character, got: " + character);
                }
                instructions.add(
                        context -> {
                            var charValue = (long) character.charAt(0);
                            context.push(charValue);
                        }
                );
            } else if (tokenLower.equals("variable")) {
                token = scanner.nextToken();
                if (token == null) {
                    throw new RuntimeException("parsing definition variable - word not found");
                }
                var word = token;
                instructions.add(
                    context -> context.defineVariable(word)
                );
            } else if (tokenLower.equals("constant")) {
                token = scanner.nextToken();
                if (token == null) {
                    throw new RuntimeException("parsing definition constant - word not found");
                }
                var word = token;
                instructions.add(
                        context -> {
                            var value = context.pop();
                            var constant = context.defineConstant(word, 1L).makeReadOnly();
                            context.setCell(constant.getAddress(), value);
                        }
                );
            } else if (tokenLower.equals("value")) {
                token = scanner.nextToken();
                if (token == null) {
                    throw new RuntimeException("parsing definition value - word not found");
                }
                var word = token;
                instructions.add(
                        context -> {
                            var value = context.pop();
                            var constant = context.defineConstant(word, 1L).makeReadWrite();
                            context.setCell(constant.getAddress(), value);
                        }
                );
            } else if (tokenLower.equals("to")) {
                token = scanner.nextToken();
                if (token == null) {
                    throw new RuntimeException("parsing definition value - word not found");
                }
                var word = token;
                instructions.add(
                        context -> {
                            var value = context.pop();
                            Constant constant;
                            if (context.hasConstant(word)) {
                                constant = context.getConstant(word);
                                if (!constant.isWritable()) {
                                    throw new RuntimeException("Value " + word + " is read-only and cannot be modified");
                                }
                            } else {
                                constant = context.defineConstant(word, 1L).makeReadWrite();
                            }
                            context.setCell(constant.getAddress(), value);
                        }
                );
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
            } else if (tokenLower.equals("do")) {
                int doStart = instructions.size();
                instructions.add(ctx -> {
                    var start = ctx.pop();
                    var limit = ctx.pop();
                    ctx.pushLoop(start, limit);
                });
                controlStack.push(doStart);
            } else if (tokenLower.equals("loop")) {
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
            if (shallReadTokenAtTheEnd) {
                token = scanner.nextToken();
            }
        }
        return instructions;
    }

}
