package net.shadow.client.helper.discord;

import java.io.IOException;
import java.net.http.HttpResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.shadow.client.helper.http.HttpWrapper;

public class DiscordClient {
    String token = "";
    HttpWrapper requests = new HttpWrapper();

    public DiscordClient(String token, boolean isBot){
        if(isBot){
            this.token = "Bot " + token;
        }else{
            this.token = token;
        }
    }

    public int[] getGuilds() throws IOException, InterruptedException{
        HttpResponse<String> resp = requests.get("https://discord.com/api/v8/users/@me/guilds", "Authorization:" + token);
        JsonArray guilds = new JsonParser().parse(resp.body()).getAsJsonArray();
        int[] guildr = new int[guilds.size()];
        int iter = 0;
        for(JsonElement guild : guilds){
            guildr[iter] = Integer.valueOf(guild.getAsJsonObject().get("id").getAsString());
            iter++;
        }
        return guildr;
    }

    public int[] getChannels(int guildId) throws IOException, InterruptedException{
        HttpResponse<String> resp = requests.get("https://discord.com/api/v8/guilds/" + String.valueOf(guildId) + "/channels", "Authorization:" + token);
        JsonArray guilds = new JsonParser().parse(resp.body()).getAsJsonArray();
        int[] guildr = new int[guilds.size()];
        int iter = 0;
        for(JsonElement guild : guilds){
            guildr[iter] = Integer.valueOf(guild.getAsJsonObject().get("id").getAsString());
            iter++;
        }
        return guildr;
    }

    public int[] getDmChannels() throws IOException, InterruptedException{
        HttpResponse<String> resp = requests.get("https://discord.com/api/v8/users/@me/channels", "Authorization:" + token);
        JsonArray guilds = new JsonParser().parse(resp.body()).getAsJsonArray();
        int[] guildr = new int[guilds.size()];
        int iter = 0;
        for(JsonElement guild : guilds){
            guildr[iter] = Integer.valueOf(guild.getAsJsonObject().get("id").getAsString());
            iter++;
        }
        return guildr;
    }

    public int[] getRoles(int guildId) throws IOException, InterruptedException{
        HttpResponse<String> resp = requests.get("https://discord.com/api/v8/guilds/" + guildId + "/roles", "Authorization:" + token);
        JsonArray guilds = new JsonParser().parse(resp.body()).getAsJsonArray();
        int[] guildr = new int[guilds.size()];
        int iter = 0;
        for(JsonElement guild : guilds){
            guildr[iter] = Integer.valueOf(guild.getAsJsonObject().get("id").getAsString());
            iter++;
        }
        return guildr;
    }

    public int[] getMembers(int guildId) throws IOException, InterruptedException{
        HttpResponse<String> resp = requests.get("https://discord.com/api/v8/guilds/" + guildId + "/members", "Authorization:" + token);
        JsonArray guilds = new JsonParser().parse(resp.body()).getAsJsonArray();
        int[] guildr = new int[guilds.size()];
        int iter = 0;
        for(JsonElement guild : guilds){
            guildr[iter] = Integer.valueOf(guild.getAsJsonObject().get("id").getAsString());
            iter++;
        }
        return guildr;
    }


    public int deleteChannel(int channelId){ 
        HttpResponse<String> req;
        try {
            req = requests.delete("https://discord.com/api/v9/channels/" + channelId, "Authorization:" + token);
            return req.statusCode();
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    public int createChannel(int guildId, int type, String name){
        HttpResponse<String> req;
        try {
            req = requests.post("https://discord.com/api/v9/guilds/" + guildId + "/channels", "{\"name\":\""+name+"\", \"permission_overwrites\":[], \"type\":\""+type+"\"}", "Authorization:" + token);
            return req.statusCode();
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    public int sendMessage(int channelid, String content, String embed){
        HttpResponse<String> req;
        try {
            req = requests.post("https://discord.com/api/v9/channels/" + channelid + "/messages", "{'content':'"+content+"'', 'embeds':["+embed+"]}", "Authorization:" + token);
            return req.statusCode();
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    public int banMember(int guildId, int userId){
        HttpResponse<String> req;
        try {
            req = requests.put("https://discord.com/api/v9/guilds/"+guildId+"/bans/"+userId+"", "{'delete_message_days':0}", "Authorization:" + token);
            return req.statusCode();
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    public int createRole(int guildId, String name){
        HttpResponse<String> req;
        try {
            req = requests.post("https://discord.com/api/v9/guilds/"+guildId+"/roles", "{'name':'"+name+"'}", "Authorization:" + token);
            return req.statusCode();
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    public int deleteRole(int guildId, int roleId){
        try {
            HttpResponse<String> req;
            req = requests.delete("https://discord.com/api/v9/guilds/"+roleId+"/roles", "Authorization:" + token);
            return req.statusCode();
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

}
