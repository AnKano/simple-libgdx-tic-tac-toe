package io.ash.simpletoe.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import io.ash.simpletoe.AppContext;
import io.ash.simpletoe.R;
import io.socket.client.Socket;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        // Set navigation controller with graph
        NavigationUI.setupWithNavController(
                (BottomNavigationView) findViewById(R.id.bottom_navigation),
                navHostFragment.getNavController()
        );
    }

    @Override
    protected void onDestroy() {
        // Clear up socket clearly way
        ((AppContext) getApplication()).getSocket().close();
        super.onDestroy();
    }

    /**
     * It's necessary, cos' instance destroying brings to session closing.
     * MainActivity can be destroyed in some cases.
     * @return public provider of socket instance from main context
     */
    public Socket getSocket() {
        return ((AppContext) Objects.requireNonNull(getApplication())).getSocket();
    }
}
