package tcd.hdelval.softwareengineering.Utils;

import android.view.View;
import android.widget.FrameLayout;

import tcd.hdelval.softwareengineering.View.Game;

/**
 * Created by hdelval on 04/10/16.
 */
public class Listeners{
    public static class MovePawnListener implements View.OnClickListener
    {
        FrameLayout to;
        FrameLayout from;
        int idPawn;
        Game game;
        public MovePawnListener(FrameLayout from, FrameLayout to, Game game, int idPawn) {
            this.to = to;
            this.from = from;
            this.game = game;
            this.idPawn = idPawn;
        }

        @Override
        public void onClick(View v) {
            game.movePawn(from , to, idPawn);
        }

    }

}