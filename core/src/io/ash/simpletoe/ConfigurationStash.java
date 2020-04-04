package io.ash.simpletoe;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import org.json.JSONException;
import org.json.JSONObject;

import io.ash.simpletoe.enums.Figures;
import io.ash.simpletoe.enums.GameState;
import io.socket.client.Socket;

public class ConfigurationStash {
    public static Socket network;
    public static String uId;
    public static String name;
    public static String lobbyId;
    public static String password;
    public static String winner;

    public static Figures figure = Figures.EMPTY; // player symbol
    public static Figures currentTurn = Figures.EMPTY; // current turn for this symbol

    public static GameState gameState = null;

    public static boolean readyPreScreenState = false;

    public static BitmapFont bakedFont;

    ConfigurationStash(String lobbyId, String uId, String name, Socket socket, String password) {
        ConfigurationStash.network = socket;
        ConfigurationStash.uId = (uId != null) ? uId : "";
        ConfigurationStash.name = (name != null) ? name : "";
        ConfigurationStash.lobbyId = (lobbyId != null) ? lobbyId : "";
        ConfigurationStash.password = (password != null) ? password : "";
        ConfigurationStash.gameState = GameState.IN_PROGRESS;
    }

    public static JSONObject createDataBundle() {
        JSONObject object = new JSONObject();
        try {
            // collect all useful on back end info
            object.put("lobbyID", ConfigurationStash.lobbyId);
            object.put("password", ConfigurationStash.password);
            object.put("clientID", ConfigurationStash.uId);
            object.put("clientName", ConfigurationStash.name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static Figures setCurentTurnFromInt(int symbol) {
        switch (symbol) {
            case 0: return Figures.CROSS;
            case 1: return Figures.NOUGHT;
            case 2: return Figures.EMPTY;
        }
        return Figures.EMPTY;
    }

    public static GameState setGameState(int state) {
        GameState gameState;
        switch (state) {
            case 0: gameState = GameState.WIN; break;
            case 1: gameState = GameState.DRAW; break;
            default: gameState = GameState.IN_PROGRESS;
        }
        return gameState;
    }

    static void setFigure(String idX, String idO) {
        if (idX.equals(ConfigurationStash.uId))
            ConfigurationStash.figure = Figures.CROSS;
        else if (idO.equals(ConfigurationStash.uId))
            ConfigurationStash.figure = Figures.NOUGHT;
    }

    public static boolean isYourTurn() {
        return ConfigurationStash.currentTurn == ConfigurationStash.figure;
    }

    public static void resetStash() {
        uId = null;
        name = null;
        lobbyId = null;
        password = null;
        winner = null;

        figure = Figures.EMPTY; // player symbol
        currentTurn = Figures.EMPTY; // current turn for this symbol

        readyPreScreenState = false;
        gameState = null;

        ConfigurationStash.network.off("successJoin");
        ConfigurationStash.network.off("failureJoin");
        ConfigurationStash.network.off("moveIsCorrect");

        ConfigurationStash.network.off("gameEnded");

        ConfigurationStash.network.off("lobbyPaused");
        ConfigurationStash.network.off("lobbyResumed");
        ConfigurationStash.network.off("lobbyDeleted");

        network = null;
    }
}
