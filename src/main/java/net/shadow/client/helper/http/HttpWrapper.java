package net.shadow.client.helper.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpClient.*;
import java.time.Duration;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class HttpWrapper {
    
    final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    final ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    public HttpWrapper(){

    }

    public HttpResponse<String> get(String uri, String... headers) throws IOException, InterruptedException{
        HttpRequest.Builder builder = HttpRequest.newBuilder().GET().setHeader("User-Agent", "MoleHttp/1.0");
        builder.uri(URI.create(uri));
        for(String header : headers){
            String[] parsedheader = header.split(":");
            builder.setHeader(parsedheader[0], parsedheader[1]);
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public void getAsync(String uri, String... headers){
        pool.execute(() -> {
            HttpRequest.Builder builder = HttpRequest.newBuilder().GET().setHeader("User-Agent", "MoleHttp/1.0");
            builder.uri(URI.create(uri));
            for(String header : headers){
                String[] parsedheader = header.split(":");
                builder.setHeader(parsedheader[0], parsedheader[1]);
            }

            HttpRequest request = builder.build();

            try {
                client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }); 
    }

    public HttpResponse<String> post(String uri, String data, String... headers) throws IOException, InterruptedException{
        HttpRequest.Builder builder = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(data)).setHeader("User-Agent", "MoleHttp/1.0").setHeader("Content-Type", "application/json");
        builder.uri(URI.create(uri));
        for(String header : headers){
            String[] parsedheader = header.split(":");
            builder.setHeader(parsedheader[0], parsedheader[1]);
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public void postAsync(String uri, String data, String... headers){
        pool.execute(() -> {
            HttpRequest.Builder builder = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(data)).setHeader("User-Agent", "MoleHttp/1.0").setHeader("Content-Type", "application/json");
            builder.uri(URI.create(uri));
            for(String header : headers){
                String[] parsedheader = header.split(":");
                builder.setHeader(parsedheader[0], parsedheader[1]);
            }

            HttpRequest request = builder.build();

            try {
                client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }); 
    } 

    public HttpResponse<String> delete(String uri, String... headers) throws IOException, InterruptedException{
        HttpRequest.Builder builder = HttpRequest.newBuilder().DELETE().setHeader("User-Agent", "MoleHttp/1.0");
        builder.uri(URI.create(uri));
        for(String header : headers){
            String[] parsedheader = header.split(":");
            builder.setHeader(parsedheader[0], parsedheader[1]);
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public void deleteAsync(String uri, String... headers){
        pool.execute(() -> {
            HttpRequest.Builder builder = HttpRequest.newBuilder().DELETE().setHeader("User-Agent", "MoleHttp/1.0");
            builder.uri(URI.create(uri));
            for(String header : headers){
                String[] parsedheader = header.split(":");
                builder.setHeader(parsedheader[0], parsedheader[1]);
            }
    
            HttpRequest request = builder.build();
            try {
                client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public HttpResponse<String> put(String uri, String data, String... headers) throws IOException, InterruptedException{
        HttpRequest.Builder builder = HttpRequest.newBuilder().PUT(HttpRequest.BodyPublishers.ofString(data)).setHeader("User-Agent", "MoleHttp/1.0").setHeader("Content-Type", "application/json");
        builder.uri(URI.create(uri));
        for(String header : headers){
            String[] parsedheader = header.split(":");
            builder.setHeader(parsedheader[0], parsedheader[1]);
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public void putAsync(String uri, String data, String... headers){
        pool.execute(() -> {
            HttpRequest.Builder builder = HttpRequest.newBuilder().PUT(HttpRequest.BodyPublishers.ofString(data)).setHeader("User-Agent", "MoleHttp/1.0").setHeader("Content-Type", "application/json");
            builder.uri(URI.create(uri));
            for(String header : headers){
                String[] parsedheader = header.split(":");
                builder.setHeader(parsedheader[0], parsedheader[1]);
            }
    
            HttpRequest request = builder.build();
            try {
                client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public HttpResponse<String> patch(String uri, String data, String... headers) throws IOException, InterruptedException{
        HttpRequest.Builder builder = HttpRequest.newBuilder().method("PATCH", HttpRequest.BodyPublishers.ofString(data)).setHeader("User-Agent", "MoleHttp/1.0").setHeader("Content-Type", "application/json");
        builder.uri(URI.create(uri));
        for(String header : headers){
            String[] parsedheader = header.split(":");
            builder.setHeader(parsedheader[0], parsedheader[1]);
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public void patchAsync(String uri, String data, String... headers){
        pool.execute(() -> {
            HttpRequest.Builder builder = HttpRequest.newBuilder().method("PATCH", HttpRequest.BodyPublishers.ofString(data)).setHeader("User-Agent", "MoleHttp/1.0").setHeader("Content-Type", "application/json");
            builder.uri(URI.create(uri));
            for(String header : headers){
                String[] parsedheader = header.split(":");
                builder.setHeader(parsedheader[0], parsedheader[1]);
            }
    
            HttpRequest request = builder.build();
            try {
                client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
