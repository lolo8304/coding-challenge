package bot.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import discord4j.core.object.entity.Message;
import io.netty.handler.codec.json.JsonObjectDecoder;

public class QuotesCmd implements Cmd {

    private JSONObject quotes;

    public QuotesCmd() {
        this.quotes = readFrom("/quotes.json");

    }
    private JSONObject readFrom(String fileName) {
        try {
            URL resource = QuotesCmd.class.getResource(fileName);
            File file = Paths.get(resource.toURI()).toFile();
            try (var reader = new BufferedReader(new FileReader(file));) {
                var tokener = new JSONTokener(reader);
                return new JSONObject(tokener);
            }
        } catch (Exception e) {
            return new JSONObject("{\"quotes\":[{\"id\":1,\"quote\":\"Murphy hit the ground. Error while loading qoutes.json file.\",\"author\":\"Lolo\"}]}");
        }

    }
    @Override
    public String commandPrefix() {
        return "!quote";
    }

    @Override
    public void onMessage(BotRequest request, BotResponse response) {
        var quote = this.randomQuote();
        response.sendTextMessage(String.format("%s says: '%s'", quote.author, quote.quote));
    }

    private Quote randomQuote() {
        var arr = this.quotes.getJSONArray("quotes");
        var quoteObject = arr.getJSONObject((int)(Math.random()*arr.length()));
        return new Quote(quoteObject.getString("quote"), quoteObject.getString("author"));
    }

    public static class Quote {
        public String quote;
        public String author;

        public Quote(String quote, String author){
            this.quote = quote;
            this.author = author;}
    }

}
