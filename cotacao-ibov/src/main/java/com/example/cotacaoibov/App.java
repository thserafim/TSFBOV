package com.example.cotacaoibov;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;

public class App {

    //Listas de simbolos de açõe da B3
    private static final List<String> SYMBOLS = Arrays.asList(
            "PETR4.SA",
            "VALE3.SA",
            "ITUB4.SA",
            "BBDC4.SA"
    );

    //Intervalo de atualizações em segundos
    private static final int UPDATE_INTERVAL = 60;

    //chave da alpha api
    private static final String API_KEY = "OLEVHZ9OVMZ6VBJD";

    public static void main(String[] args) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable fetchTak = () -> {
            System.out.println("Atualizando cotações em: " + java.time.LocalTime.now());
            for (String symbol : SYMBOLS){
                try{
                    //requisicao a api
                    String jsonResponse = fetchStockData(symbol);
                    if (jsonResponse != null){
                        parseAndDisplayStockData(symbol, jsonResponse);
                    } else {
                        System.err.println("Erro ao obter dados da ação: " + symbol);
                    }
                }catch (Exception e){
                    System.err.println("Erro ao processar dados da acao: " + symbol);
                    e.printStackTrace();
                }
            }
            System.out.println("___________________________________________________");

        };

        //Agendar a tarefa para executar a cada update segundos
        scheduler.scheduleAtFixedRate(fetchTak, 0, UPDATE_INTERVAL, TimeUnit.SECONDS);

        //adicionar o shutdown hook para finalizar o scheduler ao encerrar o app
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Encerrar o tracker de cotações...");
            scheduler.shutdown();
            try{
                if(!scheduler.awaitTermination(5,TimeUnit.SECONDS)){
                    System.err.println("Scheduler não encerrou no tempo esperado.");
                }
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }));
    }

    // funcao para buscar dados das açoes da na api
    private static String fetchStockData(String symbol) throws IOException, InterruptedException{
        String url = String.format("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", symbol, API_KEY);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200){
            return response.body();
        }else {
            System.err.println("Erro de requisição HTTP. Codigo: " + response.statusCode());
            return null;
        }
    }

    public static List<String> obterCotacoes(){
        List<String> cotacoes = new ArrayList<>();
        for(String symbol : SYMBOLS){
            BigDecimal preco = BigDecimal.valueOf(Math.random()*100); //simulação de preco
            BigDecimal variacao = BigDecimal.valueOf(Math.random()*10 - 5); //simulacao de variacao
            cotacoes.add(String.format("Acao: %s | Preco: %.2f | Variacao: %.2f%%", symbol, preco, variacao));
        }
        return cotacoes;
    }

    // funcao para exibir dados das açoes
    private static void parseAndDisplayStockData(String symbol, String jsonResponse){
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonObject globalQuote = jsonObject.getAsJsonObject("Global Quote");

        if(globalQuote != null){
            BigDecimal price = new BigDecimal(globalQuote.get("05. price").getAsString());
            BigDecimal changePercent = new BigDecimal(globalQuote.get("10. change percent").getAsString().replace("%",
                    ""));
            System.out.printf("Acao: %s | Preco: %.2f | Variacao: %.2f%%\n", symbol, price, changePercent);

        } else {
            System.err.println("Nenhum dado disponível para ação: " + symbol);
        }


    }

    public void start(Stage primaryStage) {

    }
}