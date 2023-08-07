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

public class ChallengeCmd implements Cmd {

    private JSONObject quotes;

    public ChallengeCmd() {
        this.quotes = readFrom("/challenges.json");

    }
    private JSONObject readFrom(String fileName) {
        try {
            URL resource = ChallengeCmd.class.getResource(fileName);
            File file = Paths.get(resource.toURI()).toFile();
            try (var reader = new BufferedReader(new FileReader(file));) {
                var tokener = new JSONTokener(reader);
                return new JSONObject(tokener);
            }
        } catch (Exception e) {
            return new JSONObject("{\"challenges\":[{\"name\":\"Murphy hit the ground. Error while loading challenges.json file.\",\"url\":\"https://codingchallenges.fyi\"}]}");
        }

    }
    @Override
    public String commandPrefix() {
        return "!challenge";
    }

    @Override
    public void onMessage(Message message, BotResponse response) {
        var challenge = this.randomChallenge();
        response.sendTextMessage(String.format("%s: %s", challenge.name, challenge.url));
    }

    private Challenge randomChallenge() {
        var arr = this.quotes.getJSONArray("challenges");
        var challenge = arr.getJSONObject((int)(Math.random()*arr.length()));
        return new Challenge(challenge.getString("name"), challenge.getString("url"));
    }

    public static class Challenge {
        public String name;
        public String url;

        public Challenge(String name, String url){
            this.name = name;
            this.url
 = url
;}
    }

}
