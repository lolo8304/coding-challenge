


import forth.ForthInterpreter;
import forth.ForthParser;
import org.junit.jupiter.api.Test;

class ForthInterpreterTest {

    @Test void run_skip() {
        // Arrange
        var interpreter = new ForthInterpreter();
        interpreter.addBuiltInWord("sqrt", () -> {
            //skip
        });
        var code = "10 skip 10 + ";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == 20 : "Expected 20, but got " + result;
    }



    @Test void run_newWord() {
        // Arrange
        var interpreter = new ForthInterpreter();
        interpreter.addBuiltInWord("sqrt", (forth) -> {
            var n = forth.pop();
            forth.push((long) Math.sqrt(n));
        });
        var code = "16 sqrt";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == 4 : "Expected 4, but got " + result;
    }

    @Test void execNewWord_simple() {
        // Arrange
        var interpreter = new ForthInterpreter();
        interpreter.addBuiltInWord("square", (forth) -> {
            var n = forth.pop();
            forth.push(n * n);
        });
        interpreter.push(10L);

        // Action
        interpreter.executeWord("square");
        var result = interpreter.peek();

        // Assert
        assert result == 100 : "Expected 100, but got " + result;

    }

    @Test void execNewWord_SIMPLE() {
        // Arrange
        var interpreter = new ForthInterpreter();
        interpreter.addBuiltInWord("SQUARE", (forth) -> {
            var n = forth.pop();
            forth.push(n * n);
        });
        interpreter.push(10L);

        // Action
        interpreter.executeWord("square");
        var result = interpreter.peek();

        // Assert
        assert result == 100 : "Expected 100, but got " + result;

    }

    @Test void execNewWord_null() {
        // Arrange
        var interpreter = new ForthInterpreter();

        // Action
        interpreter.executeWord(null);
        var printOutput = interpreter.outputToPrint();

        // Assert
        // No exception should be thrown but printOutput shou
        assert printOutput.equals("null ?") : "Expected 'null ?', but got '" + printOutput + "'";
    }

    @Test void execNewWord_plus() {
        // Arrange
        var interpreter = new ForthInterpreter();
        interpreter.push(10L);
        interpreter.push(20L);

        // Action
        interpreter.executeWord("+");
        var result = interpreter.peek();

        // Assert
        assert result == 30 : "Expected 30, but got " + result;
    }

    @Test void addDynamicWord_simple() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var parser = new ForthParser();
        var code = parser.parse("42");

        // Action
        interpreter.addDynamicWord("mock", code);
        interpreter.executeWord("mock");
        var result = interpreter.peek();

        // Assert
        assert result == 42 : "Expected 42, but got " + result;
    }

    @Test void stackToString_empty() {
        // Arrange
        var interpreter = new ForthInterpreter();

        // Action
        var result = interpreter.stackToString();

        // Assert
        assert result.isEmpty() : "Expected empty string, but got '" + result + "'";
    }

    @Test void stackToString_nonEmpty() {
        // Arrange
        var interpreter = new ForthInterpreter();
        interpreter.push(10L);
        interpreter.push(20L);
        interpreter.push(30L);

        // Action
        var result = interpreter.stackToString();

        // Assert
        assert result.equals("<3> 10 20 30 ") : "Expected '10 20 30 ', but got '" + result + "'";
    }


    @Test void outputToPrint_empty() {
        // Arrange
        var interpreter = new ForthInterpreter();

        // Action
        var result = interpreter.outputToPrint();

        // Assert
        assert result.isEmpty() : "Expected empty string, but got '" + result + "'";
    }

    @Test void outputToPrint_nonEmpty() {
        // Arrange
        var interpreter = new ForthInterpreter();
        interpreter.executeWord("don'tKnow"); // this will print "dontKnow ?"

        // Action
        var result = interpreter.outputToPrint();

        // Assert
        assert result.equals("don'tKnow ?") : "Expected 'don'tKnow ?', but got '" + result + "'";
    }

}
