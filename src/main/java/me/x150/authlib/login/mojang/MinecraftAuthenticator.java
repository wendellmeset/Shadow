package me.x150.authlib.login.mojang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import me.x150.authlib.exception.AuthFailureException;
import me.x150.authlib.login.altening.AlteningAuth;
import me.x150.authlib.login.microsoft.MicrosoftAuthenticator;
import me.x150.authlib.login.microsoft.XboxToken;
import me.x150.authlib.login.mojang.MinecraftToken;
import me.x150.authlib.login.mojang.profile.MinecraftProfile;
import me.x150.authlib.login.mojang.profile.MinecraftProfileCape;
import me.x150.authlib.login.mojang.profile.MinecraftProfileSkin;
import me.x150.authlib.struct.Authenticator;

public class MinecraftAuthenticator extends Authenticator<MinecraftToken> {
    protected final MicrosoftAuthenticator microsoftAuthenticator = new MicrosoftAuthenticator();

    public MinecraftAuthenticator() {
    }

    public MinecraftToken login(String email, String password) {
        try {
            URL url = new URL("https://authserver.mojang.com/authenticate");
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            JsonObject request = new JsonObject();
            JsonObject agent = new JsonObject();
            agent.addProperty("name", "Minecraft");
            agent.addProperty("version", "1");
            request.add("agent", agent);
            request.addProperty("username", email);
            request.addProperty("password", password);
            request.addProperty("clientToken","");
            request.addProperty("requestUser", true);
            String requestBody = request.toString();
            httpURLConnection.setFixedLengthStreamingMode(requestBody.length());
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Host", "authserver.mojang.com");
            httpURLConnection.connect();
            OutputStream outputStream = httpURLConnection.getOutputStream();

            try {
                outputStream.write(requestBody.getBytes(StandardCharsets.US_ASCII));
            } catch (Throwable var13) {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Throwable var12) {
                        var13.addSuppressed(var12);
                    }
                }

                throw var13;
            }

            if (outputStream != null) {
                outputStream.close();
            }

            JsonObject jsonObject = this.parseResponseData(httpURLConnection);
            return new MinecraftToken(jsonObject.get("accessToken").getAsString(), ((JsonObject)jsonObject.get("selectedProfile")).get("name").getAsString());
        } catch (IOException var14) {
            throw new AuthFailureException(String.format("Authentication error. Request could not be made! Cause: '%s'", var14.getMessage()));
        }
    }

    public MinecraftToken loginWithMicrosoft(String email, String password) {
        XboxToken xboxToken = this.microsoftAuthenticator.login(email, password);

        try {
            URL url = new URL("https://api.minecraftservices.com/authentication/login_with_xbox");
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            JsonObject request = new JsonObject();
            request.addProperty("identityToken", "XBL3.0 x=" + xboxToken.getUhs() + ";" + xboxToken.getToken());
            String requestBody = request.toString();
            httpURLConnection.setFixedLengthStreamingMode(requestBody.length());
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Host", "api.minecraftservices.com");
            httpURLConnection.connect();
            OutputStream outputStream = httpURLConnection.getOutputStream();

            try {
                outputStream.write(requestBody.getBytes(StandardCharsets.US_ASCII));
            } catch (Throwable var13) {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Throwable var12) {
                        var13.addSuppressed(var12);
                    }
                }

                throw var13;
            }

            if (outputStream != null) {
                outputStream.close();
            }

            JsonObject jsonObject = this.microsoftAuthenticator.parseResponseData(httpURLConnection);
            return new MinecraftToken(jsonObject.get("access_token").getAsString(), jsonObject.get("username").getAsString());
        } catch (IOException var14) {
            throw new AuthFailureException(String.format("Authentication error. Request could not be made! Cause: '%s'", var14.getMessage()));
        }
    }
    public MinecraftToken loginWithAltening(String token) {
        AlteningAuth alteningAuth = new AlteningAuth(token);
        return alteningAuth.login();
    }

    public MinecraftProfile getGameProfile(MinecraftToken minecraftToken) {
        try {
            URL url = new URL("https://api.minecraftservices.com/minecraft/profile");
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Authorization", "Bearer " + minecraftToken.getAccessToken());
            httpURLConnection.setRequestProperty("Host", "api.minecraftservices.com");
            httpURLConnection.connect();
            JsonObject jsonObject = this.parseResponseData(httpURLConnection);
            UUID uuid = this.generateUUID(jsonObject.get("id").getAsString());
            String name = jsonObject.get("name").getAsString();

            return new MinecraftProfile(uuid, name);
        } catch (IOException var10) {
            throw new AuthFailureException(String.format("Authentication error. Request could not be made! Cause: '%s'", var10.getMessage()));
        }
    }

    public JsonObject parseResponseData(HttpURLConnection httpURLConnection) throws IOException {
        BufferedReader bufferedReader;
        if (httpURLConnection.getResponseCode() != 200) {
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
        } else {
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        }

        String lines = (String)bufferedReader.lines().collect(Collectors.joining());
        JsonObject jsonObject = (JsonObject)this.gson.fromJson(lines, JsonObject.class);
        if (jsonObject.has("error")) {
            throw new AuthFailureException(String.format("Could not find profile!. Error: '%s'", jsonObject.get("errorMessage").getAsString()));
        } else {
            return jsonObject;
        }
    }

    public UUID generateUUID(String trimmedUUID) throws IllegalArgumentException {
        if (trimmedUUID == null) {
            throw new IllegalArgumentException();
        } else {
            StringBuilder builder = new StringBuilder(trimmedUUID.trim());

            try {
                builder.insert(20, "-");
                builder.insert(16, "-");
                builder.insert(12, "-");
                builder.insert(8, "-");
                return UUID.fromString(builder.toString());
            } catch (StringIndexOutOfBoundsException var4) {
                return null;
            }
        }
    }
}
