package lisp.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
    while (token.isPresent()) {
      expressions.add(token.get());
      token = this.tokenizer.nextToken();
    }
    return expressions;
  }
}
