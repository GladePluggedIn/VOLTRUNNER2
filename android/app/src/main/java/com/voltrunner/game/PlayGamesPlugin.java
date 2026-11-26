package com.voltrunner.game;

import android.content.Intent;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.google.android.gms.games.GamesSignInClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.tasks.Task;

/** Small bridge between the game (JavaScript) and Google Play Games leaderboards. */
@CapacitorPlugin(name = "PlayGames")
public class PlayGamesPlugin extends Plugin {
    private static final int RC_LEADERBOARD = 9004;

    private void done(PluginCall call, boolean signedIn) {
        JSObject r = new JSObject();
        r.put("signedIn", signedIn);
        call.resolve(r);
    }

    /** Checks the automatic sign-in; if it didn't happen, asks the player to sign in. */
    @PluginMethod
    public void signIn(PluginCall call) {
        GamesSignInClient client = PlayGames.getGamesSignInClient(getActivity());
        client.isAuthenticated().addOnCompleteListener(t -> {
            boolean ok = t.isSuccessful() && t.getResult().isAuthenticated();
            if (ok || !call.getBoolean("interactive", false)) { done(call, ok); return; }
            client.signIn().addOnCompleteListener(t2 ->
                done(call, t2.isSuccessful() && t2.getResult().isAuthenticated()));
        });
    }

    /** Submits a score (for time boards: milliseconds). */
    @PluginMethod
    public void submitScore(PluginCall call) {
        String id = call.getString("leaderboardId");
        Double score = call.getDouble("score");
        if (id == null || id.isEmpty() || score == null) { call.reject("leaderboardId and score are required"); return; }
        PlayGames.getLeaderboardsClient(getActivity()).submitScore(id, Math.round(score));
        call.resolve();
    }

    /** Opens one leaderboard, or all of them when no id is given. */
    @PluginMethod
    public void showLeaderboard(PluginCall call) {
        String id = call.getString("leaderboardId");
        LeaderboardsClient lc = PlayGames.getLeaderboardsClient(getActivity());
        Task<Intent> task = (id == null || id.isEmpty()) ? lc.getAllLeaderboardsIntent() : lc.getLeaderboardIntent(id);
        task.addOnSuccessListener(intent -> {
            getActivity().startActivityForResult(intent, RC_LEADERBOARD);
            call.resolve();
        }).addOnFailureListener(e -> call.reject(e.getMessage() == null ? "Could not open leaderboard" : e.getMessage()));
    }
}
