


import forth.ForthInterpreterOperationsAll;
import forth.ForthParser;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ForthParserTest {

    @Test void parse_integer() {
        // Arrange
        var code = "10";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);

        // Action
        var instructions = parser.parse(code);
        instructions.getFirst().execute(mock);

        // Assert
        assert instructions.size() == 1;
        verify(mock, times(1)).push(10L);
    }

    @Test void parse_addition() {
        // Arrange
        var code = "10 20 +";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);

        // Action
        var instructions = parser.parse(code);
        instructions.forEach(x -> x.execute(mock));

        // Assert
        assert instructions.size() == 3; // 2 pushes and 1 add
        verify(mock, times(1)).push(10L);
        verify(mock, times(1)).push(20L);
        verify(mock, times(1)).executeWord("+");
    }

    @Test void parse_subtraction() {
        // Arrange
        var code = "30 10 -";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);

        // Action
        var instructions = parser.parse(code);
        instructions.forEach(x -> x.execute(mock));

        // Assert
        assert instructions.size() == 3; // 2 pushes and 1 subtract
        verify(mock, times(1)).push(30L);
        verify(mock, times(1)).push(10L);
        verify(mock, times(1)).executeWord("-");
    }
    @Test void parse_comment() {
        // Arrange
        var code = "10 20 ( n1 n2 - sum ) +";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);

        // Action
        var instructions = parser.parse(code);
        instructions.forEach(x -> x.execute(mock));

        // Assert
        assert instructions.size() == 3; // 2 pushes and 1 add
        verify(mock, times(1)).push(10L);
        verify(mock, times(1)).push(20L);
        verify(mock, times(1)).executeWord("+");
    }

    @Test void parse_commentmultiLeft() {
        // Arrange
        var code = "10 20 ( ( n1 n2 - sum ) +";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);

        // Action
        var instructions = parser.parse(code);
        instructions.forEach(x -> x.execute(mock));

        // Assert
        assert instructions.size() == 3; // 2 pushes and 1 add
        verify(mock, times(1)).push(10L);
        verify(mock, times(1)).push(20L);
        verify(mock, times(1)).executeWord("+");
    }

    @Test void parse_commentmultiRight() {
        // Arrange
        var code = "10 20 (  n1 n2 - sum ) ) ) +";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);

        // Action
        var instructions = parser.parse(code);
        instructions.forEach(x -> x.execute(mock));

        // Assert
        assert instructions.size() == 3; // 2 pushes and 1 add
        verify(mock, times(1)).push(10L);
        verify(mock, times(1)).push(20L);
        verify(mock, times(1)).executeWord("+");
    }

    @Test void parse_comment_empty() {
        // Arrange
        var code = "10 20 ( ) +";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);

        // Action
        var instructions = parser.parse(code);
        instructions.forEach(x -> x.execute(mock));

        // Assert
        assert instructions.size() == 3; // 2 pushes and 1 add
        verify(mock, times(1)).push(10L);
        verify(mock, times(1)).push(20L);
        verify(mock, times(1)).executeWord("+");
    }

    @Test void parse_comment_still_open() {
        // Arrange
        var code = "10 20 ( n1 n2 - sum +";
        var parser = new ForthParser();

        // Action & Assert
        try {
            parser.parse(code);

            assert false; // Should not reach here
        } catch (RuntimeException ex) {
            assert ex.getMessage().equals("parsing comment - no end ) found");
        }
    }

    @Test void parse_comment_backslash() {
        // Arrange
        var code = "10 20 \\ this is a comment \n +";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);

        // Action
        var instructions = parser.parse(code);
        instructions.forEach(x -> x.execute(mock));

        // Assert
        assert instructions.size() == 3; // 2 pushes and 1 add
        verify(mock, times(1)).push(10L);
        verify(mock, times(1)).push(20L);
        verify(mock, times(1)).executeWord("+");
    }


    @Test void parse_comment_backslashUntilEndOfLine() {
        // Arrange
        var code = "\\ this is a comment";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);

        // Action
        var instructions = parser.parse(code);
        instructions.forEach(x -> x.execute(mock));

        // Assert
        assert instructions.isEmpty();
    }

    @Test void parse_comment_on_multiple_lines() {
        // Arrange
        var code = "\\ define and test a variable\n" +
                "VARIABLE counter          \\ creates a cell initialized to 0\n" +
                "counter @ . CR            \\ expect: 0\n";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);

        // Action
        var instructions = parser.parse(code);
        instructions.forEach(x -> x.execute(mock));

        // Assert
        assert instructions.size() == 5; // 2 pushes and 1 add
    }


    @Test void parse_string() {
        // Arrange
        var code = ".\" Hello, World!\"";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);

        // Action
        var instructions = parser.parse(code);
        instructions.getFirst().execute(mock);

        // Assert
        assert instructions.size() == 1;
        verify(mock, times(1)).executePrint("Hello, World!");
    }

    @Test void parse_stringWithBackslashes() {
        // Arrange
        var code = ".\" \\Hello\\, World!\"";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);

        // Action
        var instructions = parser.parse(code);
        instructions.getFirst().execute(mock);

        // Assert
        assert instructions.size() == 1;
        verify(mock, times(1)).executePrint("\\Hello\\, World!");
    }

    @Test void parse_stringWithBackslashAtEnd() {
        // Arrange
        var code = ".\" Hello, World!\\\"";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);

        // Action
        var instructions = parser.parse(code);
        instructions.getFirst().execute(mock);

        // Assert
        assert instructions.size() == 1;
        verify(mock, times(1)).executePrint("Hello, World!\\");
    }

    @Test void parse_stringNotClosed() {
        // Arrange
        var code = ".\" Hello, World!";
        var parser = new ForthParser();

        // Action & Assert
        try {
            parser.parse(code);
            assert false; // Should not reach here
        } catch (RuntimeException ex) {
            assert ex.getMessage().equals("No end quote \" found");
        }
    }

    @Test void parse_ifthen() {
        // Arrange
        var code = "10 10 = if .\"eq\" then";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);

        // Action
        var instructions = parser.parse(code);
        instructions.forEach(x -> x.execute(mock));

        // Assert
        assert instructions.size() == 5; // 2 pushes, 1 equals, 1 if, 1 print, 1 then
        verify(mock, times(2)).push(10L);
        verify(mock, times(1)).executeWord("=");
        verify(mock, times(1)).pop();
        verify(mock, times(1)).jumpTo(5);
        verify(mock, times(1)).executeWord(".\"eq\"");
    }

    @Test void parse_ifelsethen_0() {
        // Arrange
        var code = "10 20 = if .\"eq\" else .\"ne\" then";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);
        when(mock.pop()).thenReturn(0L);

        // Action
        var instructions = parser.parse(code);
        instructions.forEach(x -> x.execute(mock));

        // Assert
        assert instructions.size() == 7; // 2 pushes, 1 equals, 1 if, 1 print eq, 1 else, 1 print ne, 1 then
        verify(mock, times(1)).push(10L);
        verify(mock, times(1)).push(20L);
        verify(mock, times(1)).executeWord("=");
        verify(mock, times(1)).pop();
        verify(mock, times(1)).jumpTo(7);
        verify(mock, times(1)).executeWord(".\"ne\"");
    }


    @Test void parse_ifelsethen_minus1() {
        // Arrange
        var code = "10 10 = if .\"eq\" else .\"ne\" then";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);
        when(mock.pop()).thenReturn(-1L);

        // Action
        var instructions = parser.parse(code);
        instructions.forEach(x -> x.execute(mock));

        // Assert
        assert instructions.size() == 7; // 2 pushes, 1 equals, 1 if, 1 print eq, 1 else, 1 print ne, 1 then
        verify(mock, times(2)).push(10L);
        verify(mock, times(1)).executeWord("=");
        verify(mock, times(1)).pop();
        verify(mock, times(1)).jumpTo(7);
        verify(mock, times(1)).executeWord(".\"ne\"");
    }

    @Test void parse_defineWord() {
        // Arrange
        var code = ": square dup * ; 10 square";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);

        // Action
        var instructions = parser.parse(code);
        instructions.forEach(x -> x.execute(mock));

        // Assert
        assert instructions.size() == 3; // 1 define, 1 dup, 1 multiply
        verify(mock, times(1)).define(eq("square"), any());
        verify(mock, times(1)).push(10L);
        verify(mock, times(1)).executeWord("square");
    }

    @Test void parse_defineWordInComplete() {
        // Arrange
        var code = ": square dup *";
        var parser = new ForthParser();

        // Action & Assert
        try {
            parser.parse(code);
            assert false; // Should not reach here
        } catch (RuntimeException ex) {
            assert ex.getMessage().equals("parsing definition : __ ; - token ; not found");
        }
    }
    @Test void parse_defineWordInCompleteExpressionEmpty() {
        // Arrange
        var code = ": square ;";
        var parser = new ForthParser();

        // Action & Assert
        try {
            parser.parse(code);
            assert false; // Should not reach here
        } catch (RuntimeException ex) {
            assert ex.getMessage().equals("parsing definition : __ ; - expression is empty");
        }
    }

    @Test void parse_doLoop() {
        // Arrange
        var code = "5 0 do .\" Test \" i . cr loop";
        var parser = new ForthParser();
        var mock = mock(ForthInterpreterOperationsAll.class);

        // Action
        var instructions = parser.parse(code);
        instructions.forEach(x -> x.execute(mock));

        // Assert
        assert instructions.size() == 8; // 2 pushes, 1 do, 1 print, 1 i, 1 cr, 1 loop
        verify(mock, times(1)).push(5L);
        verify(mock, times(1)).push(0L);
        verify(mock, times(1)).pushLoop(0, 0L);
        verify(mock, times(1)).executeWord(".");
        verify(mock, times(1)).executeWord("i");
        verify(mock, times(1)).executeWord("cr");
        verify(mock, times(1)).incrementLoop();
        verify(mock, times(1)).popLoop();
    }

}
