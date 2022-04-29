/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.helper;

import com.google.gson.Gson;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class ShadowAPIWrapper {
    public static final String BASE_DOMAIN = "api.shadowclient.cf";
    public static final String BASE_URL = "https://" + BASE_DOMAIN;
    public static final String BASE_WS = "wss://" + BASE_DOMAIN;
    static String authKey = "";
    static HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
    static Gson gson = new Gson();
    static boolean currentUserIsAdmin = false;

    public static String getAuthKey() {
        if (authKey.isEmpty()) return null;
        return authKey;
    }

    public static boolean isCurrentUserAdmin() {
        return currentUserIsAdmin;
    }

    static HttpResponse<String> get(String path) {
        return request(path, "GET", HttpRequest.BodyPublishers.noBody());
    }

    static HttpResponse<String> request(String path, String method, HttpRequest.BodyPublisher publisher) {
        URI u = URI.create(BASE_URL + path);
        HttpRequest request = HttpRequest.newBuilder().method(method, publisher).uri(u).header("Authorization", authKey).build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static HttpResponse<String> post(String path, String data) {
        return request(path, "POST", HttpRequest.BodyPublishers.ofString(data));
    }

    public static void logout() {
        authKey = "";
    }

    public static boolean loginWithKey(String session) {
        String prevAuthKey = authKey;
        authKey = session;
        HttpResponse<String> resp = get("/users/current");
        if (resp.statusCode() != 200) {
            authKey = prevAuthKey;
            return false;
        }
        Map<String, Object> info = gson.fromJson(resp.body(), Map.class);
        currentUserIsAdmin = (boolean) info.getOrDefault("isAdmin", false);
        return true;
    }

    public static boolean attemptLogin(String username, String password) {
        String d = gson.toJson(Map.of(
                "username", username,
                "password", password
        ));
        HttpResponse<String> uResp = post("/users/apiKeyForCreds", d);
        if (uResp == null) return false;
        System.out.println(uResp.body() + ": " + d);
        if (uResp.statusCode() != 200) return false;
        return loginWithKey(uResp.body());
    }

    public static List<AccountEntry> getAccounts() {
        HttpResponse<String> a = get("/users/admin/list");
        if (a.statusCode() != 200) return List.of();
        return new ArrayList<>(List.of(new Gson().fromJson(a.body(), AccountEntry[].class)));
    }

    public static boolean deleteAccount(String user, String pass) {
        HttpResponse<String> s = request("/users/admin/delete", "DELETE", HttpRequest.BodyPublishers.ofString(new Gson().toJson(
                Map.of("username", user, "password", pass)
        )));
        return s != null && s.statusCode() == 200;
    }

    public static boolean registerAccount(String user, String pass) {
        HttpResponse<String> s = post("/users/admin/register", new Gson().toJson(
                Map.of("username", user, "password", pass)
        ));
        if (s != null) System.out.println(s.body());
        return s != null && s.statusCode() == 200;
    }

    public static boolean putItem(ItemStack stack) {
        HttpResponse<String> a = request("/items", "PUT", HttpRequest.BodyPublishers.ofString(gson.toJson(Map.of(
                "itemName", Registry.ITEM.getId(stack.getItem()).getPath(),
                "itemNbt", new String(Base64.getEncoder().encode(stack.getOrCreateNbt().toString().getBytes(StandardCharsets.UTF_8)))
        ))));
        if (a == null) return false;
        System.out.println(a.body());
        return a.statusCode() == 200;
    }

    public static class AccountEntry {
        public String username, password, apiKey;

        @Override
        public String toString() {
            return "AccountEntry{" +
                    "username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", apiKey='" + apiKey + '\'' +
                    '}';
        }
    }
}
