package org.example;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;

public class ExchangeRateBot extends TelegramLongPollingBot {

    private final String API_TOKEN = "YOUR_BOT_TOKEN";
    private final String EXCHANGE_API_URL = "https://api.exchangeratesapi.io/latest?base=USD";

    @Override
    public void onUpdateReceived(Update update) {
        // Do nothing for incoming messages
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        for (Update update : updates) {
            try {
                sendExchangeRate(update.getMessage().getChatId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendExchangeRate(Long chatId) throws IOException, JSONException, TelegramApiException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(EXCHANGE_API_URL)
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject json = new JSONObject(responseBody);
        JSONObject rates = json.getJSONObject("rates");
        double usdExchangeRate = rates.getDouble("USD");

        SendMessage message;
        message = new SendMessage()
                .setChatId(chatId)
                .setText("Current USD exchange rate: " + usdExchangeRate);

        execute(message);
    }

    @Override
    public String getBotUsername() {
        return "YourBotUsername";
    }

    @Override
    public String getBotToken() {
        return API_TOKEN;
    }

