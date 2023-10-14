package lisp.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Parser {
  private Tokenizer tokenizer;

  public Parser(String content) {
    var reader = new StringReader(content);
    this.tokenizer = new Tokenizer(reader);
  }

  public Parser(Tokenizer tokenizer) {
    this.tokenizer = tokenizer;
  }

  public Parser(Reader reader) {
    this.tokenizer = new Tokenizer(reader);
  }

  public List<TokenValue> parse() throws IOException {
    var token = this.tokenizer.nextToken();
    var expressions = new ArrayList<TokenValue>();
    Optional<TokenValue> lastExpression = Optional.empty();
    while (token.isPresent()) {
      if (token.get().getToken() == Token.RPAREN) {
        if (lastExpression.isPresent()) {
          throw new IllegalArgumentException(") too much in the last expression \n" + lastExpression.get().toString());
        } else {
          throw new IllegalArgumentException("cannot start with a )");
        }
      }
      expressions.add(token.get());
      lastExpression = token;
      token = this.tokenizer.nextToken();
    }
    return expressions;
  }
}
