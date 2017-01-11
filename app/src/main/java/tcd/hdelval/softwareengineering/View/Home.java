package tcd.hdelval.softwareengineering.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import tcd.hdelval.softwareengineering.R;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        /* PLAY HUMAN */
        Button playHumanButton = (Button) findViewById(R.id.play_human);
        playHumanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent wantToPlayAGame = new Intent(getBaseContext(), Game.class);
                wantToPlayAGame.putExtra("Against", "HUMAN");
                startActivity(wantToPlayAGame);
            }
        });

        /* PLAY AI */
        Button playAIButton = (Button) findViewById(R.id.play_ai);
        playAIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent wantToPlayAGame = new Intent(getBaseContext(), Game.class);
                wantToPlayAGame.putExtra("Against", "AI");
                startActivity(wantToPlayAGame);
            }
        });
    }
}
