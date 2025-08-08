


import forth.ForthInterpreter;
import org.junit.jupiter.api.Test;

class ForthInterpreterBuiltInTest {

    @Test void runBuiltIn_plus() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "10 20 + ";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == 30 : "Expected 30, but got " + result;
    }

    @Test void runBuiltIn_minus() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "20 10 - ";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == 10 : "Expected 10, but got " + result;
    }

    @Test void runBuiltIn_times() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "10 20 * ";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == 200 : "Expected 200, but got " + result;
    }

    @Test void runBuiltIn_divide() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "20 10 / ";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == 2 : "Expected 2, but got " + result;
    }

    @Test void runBuiltIn_mod() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "20 3 mod";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == 2 : "Expected 2, but got " + result;
    }

    @Test void runBuiltIn_swap() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "10 20 swap";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == 10 : "Expected 10, but got " + result;
    }

    @Test void runBuiltIn_dup() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "10 dup";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == 10 : "Expected 10, but got " + result;
    }

    @Test void runBuiltIn_2dup() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "10 20 2dup";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == 20 : "Expected 20, but got " + result;
    }

    @Test void runBuiltIn_over() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "10 20 over";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == 10 : "Expected 10, but got " + result;
    }

    @Test void runBuiltIn_rot() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "10 20 30 rot";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == 20 : "Expected 20, but got " + result;
    }

    @Test void runBuiltIn_drop() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "10 20 drop";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == 10 : "Expected 10, but got " + result;
    }

    @Test void runBuiltIn_dot() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "10 .";

        // Action
        interpreter.run(code);
        var result = interpreter.outputToPrint();

        // Assert
        assert result.equals("10") : "Expected '10', but got '" + result + "'";
    }

    @Test void runBuiltIn_emit() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "65 emit";

        // Action
        interpreter.run(code);
        var result = interpreter.outputToPrint();

        // Assert
        assert result.equals("A") : "Expected 'A', but got '" + result + "'";
    }

    @Test void runBuiltIn_cr() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "cr";

        // Action
        interpreter.run(code);
        var result = interpreter.outputToPrint();

        // Assert
        assert result.equals("\n") : "Expected '\\n', but got '" + result + "'";
    }

    @Test void runBuiltIn_lessThan() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "10 20 <";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == -1 : "Expected -1, but got " + result;
    }

    @Test void runBuiltIn_lessThanOrEqual() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "10 20 <=";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == -1 : "Expected -1, but got " + result;
    }

    @Test void runBuiltIn_greaterThan() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "20 10 >";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == -1 : "Expected -1, but got " + result;
    }

    @Test void runBuiltIn_greaterThanOrEqual() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "20 10 >=";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == -1 : "Expected -1, but got " + result;
    }

    @Test void runBuiltIn_equals() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "10 10 =";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == -1 : "Expected -1, but got " + result;
    }

    @Test void runBuiltIn_notEquals() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "10 20 <>";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == -1 : "Expected -1, but got " + result;
    }

    @Test void runBuiltIn_and() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "-1 -1 and";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == -1 : "Expected -1, but got " + result;
    }

    @Test void runBuiltIn_or() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "-1 0 or";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == -1 : "Expected -1, but got " + result;
    }

    @Test void runBuiltIn_invert() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "-1 invert";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == 0 : "Expected 0, but got " + result;
    }






    @Test void runBuiltIn_depth() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "10 20 depth";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == 2 : "Expected 2, but got " + result;
    }

    @Test void runBuiltIn_clear() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "10 20 clear depth";

        // Action
        interpreter.run(code);
        var result = interpreter.peek();

        // Assert
        assert result == 0 : "Expected 0, but got " + result;
    }

    @Test void runBuiltIn_bye() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "bye";

        // Action
        try {
            interpreter.run(code);
            assert false : "Expected System.exit(0) to be called, but it was not.";
        } catch (SecurityException e) {
            // Expected exception due to System.exit(0)
        }
    }

    @Test void runBuiltIn_output() {
        // Arrange
        var interpreter = new ForthInterpreter();
        var code = "10 20 .s";

        // Action
        interpreter.run(code);
        var result = interpreter.outputToPrint();

        // Assert
        assert result.equals("10 20 ") : "Expected '10 20 ', but got '" + result + "'";
    }



}
