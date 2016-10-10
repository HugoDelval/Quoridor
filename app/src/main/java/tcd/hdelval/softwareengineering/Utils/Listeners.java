package tcd.hdelval.softwareengineering.Utils;

import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

import java.util.ArrayList;

import tcd.hdelval.softwareengineering.R;
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

    public static class BarrierTouchListener implements View.OnTouchListener
    {

        private final ArrayList<FrameLayout> interBarriers;
        private final ArrayList<FrameLayout> barriers;
        private final int barrierWidth;
        private final int barrierHeight;
        private final GridLayout board;

        public BarrierTouchListener(ArrayList<FrameLayout> barriers, ArrayList<FrameLayout> interBarriers, GridLayout board, int barrierWidth, int squareSize){
            this.barriers = barriers;
            this.interBarriers = interBarriers;
            this.board = board;
            this.barrierWidth = barrierWidth;
            this.barrierHeight = squareSize * 2 + barrierWidth;

        }

        @Override
        public boolean onTouch(View barrier, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                CustomViews.ImageDragShadowBuilder shadowBuilder = (CustomViews.ImageDragShadowBuilder) CustomViews.ImageDragShadowBuilder.fromResource(
                        board.getContext(), barrier.getBackground(), barrierWidth, barrierHeight
                );
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    barrier.startDragAndDrop(data, shadowBuilder, barrier, 0);
                else
                    barrier.startDrag(data, shadowBuilder, barrier, 0);
                return true;
            } else {
                return false;
            }
        }
    }

    public static class BarrierDragListener implements View.OnDragListener {

        private final ImageView shadow;

        public BarrierDragListener(ImageView shadow){
            this.shadow = shadow;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundResource(R.drawable.light_wood);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundResource(0);
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
//                    View view = (View) event.getLocalState();
//                    ViewGroup owner = (ViewGroup) view.getParent();
//                    owner.removeView(view);
//                    LinearLayout container = (LinearLayout) v;
//                    container.addView(view);
//                    view.setVisibility(View.VISIBLE);
                    v.setBackgroundResource(R.drawable.light_wood);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
//                    v.setBackgroundResource(0);
                default:
                    break;
            }
            return true;
        }
    }

}