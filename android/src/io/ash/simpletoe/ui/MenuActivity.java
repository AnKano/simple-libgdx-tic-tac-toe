package io.ash.simpletoe.ui;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;

import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import io.ash.simpletoe.AppContext;
import io.ash.simpletoe.R;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);

        NavigationUI.setupWithNavController(
                (BottomNavigationView) findViewById(R.id.bottom_navigation),
                Navigation.findNavController(this, R.id.nav_host_fragment)
        );
    }

    @Override
    protected void onDestroy() {
        ((AppContext) getApplication()).getSocket().close();
        super.onDestroy();
    }
}
