public class Parser {
  private Tokenizer tokenizer;
  public Parser(Tokenizer tokenizer) {
    this.tokenizer = tokenizer:
  }
  public Parser(BufferedReader reader) {
    this.tokenizer = new Tokenizer(reader);
  }

  public List<TokenValue> parse() {
    var token = this.tokenizer.nextToken();
    var expressions = new ArrayList<TokenValue>();
    while (token.isPresent()) {
        expressions.add(token.get());
        token = this.tokenizer.nextToken();
    }
    
  }
}
