package bot.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import discord4j.core.object.entity.Message;

public class ChallengeCmd implements Cmd {

    private Challenge[] challenges;

    public ChallengeCmd() {
        var quotes = readFrom("/challenges.json");
        this.challenges = Challenge.fromArray(quotes.getJSONArray("challenges"));
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
    public void onMessage(BotRequest request, BotResponse response) {
        var parameters = request.getParameters();
        if (parameters.length == 1 && parameters[0].equalsIgnoreCase("list")){
            var buffer = new StringBuilder();
            for (var challenge : this.challenges) {
                buffer
                    .append(challenge.toString())
                    .append("\n");
            }
            response.sendTextMessage(buffer.toString());
        } else {
            var challenge = this.randomChallenge();
            response.sendTextMessage(challenge.toString());
        }
    }

    private Challenge randomChallenge() {
        return this.challenges[(int)(Math.random()*this.challenges.length)];
    }

    public static class Challenge {
        public String name;
        public String url;


        public static Challenge[] fromArray(JSONArray array) {
            var list = new ArrayList<Challenge>();
            for (int i = 0; i < array.length(); i++) {
                var challenge = array.getJSONObject(i);
                list.add(new Challenge(challenge.getString("name"), challenge.getString("url")));
            }
            return list.toArray(Challenge[]::new);
        }

        public Challenge(String name, String url){
            this.name = name;
            this.url = url;
        }
        public Challenge(JSONObject challenge){
            this.name = challenge.getString("name");
            this.url = challenge.getString("url");
        }

        @Override
        public String toString() {
            return String.format("%s: %s", this.name, this.url);
        }
    }

}
