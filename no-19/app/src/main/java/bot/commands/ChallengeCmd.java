package bot.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import discord4j.core.object.entity.Message;

public class ChallengeCmd implements Cmd {

    private List<Challenge> challenges;

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
            processList(response);
        } else if (parameters.length == 1 && parameters[0].equalsIgnoreCase("add")) {
            processAdd(request, response);
        } else {
            processRandom(response);
        }
    }


    private Optional<Challenge> getChallengeFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            InputStream is = url.openStream();
            try (var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));) {
                final String regex = "<title.*>(.*)<\\/title>";        
                final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                String line = reader.readLine();
                while (line != null) {
                    var matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        return Optional.of(new Challenge(matcher.group(1), urlString)) ;
                    }
                    line = reader.readLine();
                }
            }
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.empty();
    } 


    private void processAdd(BotRequest request, BotResponse response) {
        var args = request.getParameters();
        if (args.length > 1) {
            var url = args[1].toLowerCase();
            if (url.startsWith("https://codingchallenges.fyi")) {
                var challenge = this.getChallengeFromUrl(url);
                if (challenge.isPresent()) {
                    this.challenges.add(challenge.get());
                    response.sendTextMessage(challenge.get().toString());
                    return;
                }
            }
            response.sendTextMessage("Unable to add: "+url+" check if it is a valid coding challenge");
        } else {
            response.sendTextMessage("Unable to add: no url passed");
        }
    }
    private void processRandom(BotResponse response) {
        var challenge = this.randomChallenge();
        response.sendTextMessage(challenge.toString());
    }
    private void processList(BotResponse response) {
        var buffer = new StringBuilder();
        for (var challenge : this.challenges) {
            buffer
                .append(challenge.toString())
                .append("\n");
        }
        response.sendTextMessage(buffer.toString());
    }

    private Challenge randomChallenge() {
        return this.challenges.get((int)(Math.random()*this.challenges.size()));
    }

    public static class Challenge {
        public String name;
        public String url;


        public static List<Challenge> fromArray(JSONArray array) {
            var list = new ArrayList<Challenge>();
            for (int i = 0; i < array.length(); i++) {
                var challenge = array.getJSONObject(i);
                list.add(new Challenge(challenge.getString("name"), challenge.getString("url")));
            }
            return list;
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
