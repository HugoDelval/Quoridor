package tcd.hdelval.softwareengineering.Utils;

import android.content.ClipData;
import android.os.Build;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.LinkedList;

import tcd.hdelval.softwareengineering.controller.Controller;
import tcd.hdelval.softwareengineering.View.Game;
import tcd.hdelval.softwareengineering.View.UndoRedo.UndoRedoBarriers;
import tcd.hdelval.softwareengineering.View.UndoRedo.UndoRedoHandler;

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
        private final CustomViews.ImageDragShadowBuilder shadowBuilder;

        public BarrierTouchListener(ArrayList<FrameLayout> barriers, ArrayList<FrameLayout> interBarriers, GridLayout board, int barrierWidth, int squareSize, CustomViews.ImageDragShadowBuilder shadowBuilder){
            this.barriers = barriers;
            this.interBarriers = interBarriers;
            this.board = board;
            this.barrierWidth = barrierWidth;
            this.barrierHeight = squareSize * 2 + barrierWidth;
            this.shadowBuilder = shadowBuilder;
        }

        @Override
        public boolean onTouch(View barrier, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
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
        private final GridLayout board;
        private final UndoRedoHandler undoRedoHandler;
        private final Controller gameController;
        private FrameLayout boardObject;
        private int id;
        private enum boardObjectType {VERTICAL_BARRIER, HORITONTAL_BARRIER, INTER_BARRIER, SQUARE};
        private boardObjectType type;
        private int col;
        private int line;

        private FrameLayout getBoardObject(int line, int col, boardObjectType t){
            FrameLayout result = null;
            if(t == boardObjectType.VERTICAL_BARRIER)
                result = (FrameLayout) board.findViewById(Game.OFFSET_VERTICAL_BARRIER + line*8+col);
            else if(t == boardObjectType.HORITONTAL_BARRIER)
                result = (FrameLayout) board.findViewById(Game.OFFSET_HORIZONTAL_BARRIER + line*9+col);
            else if(t == boardObjectType.INTER_BARRIER)
                result = (FrameLayout) board.findViewById(Game.OFFSET_INTER_BARRIER + line*8+col);
            else if(t == boardObjectType.SQUARE)
                result = (FrameLayout) board.findViewById(Game.OFFSET_SQUARE + line*9+col);
            if(result == null) {
                throw new RuntimeException("Cannot find an appropriate type for this board object :/");
            }
            return result;
        }

        public BarrierDragListener(FrameLayout boardObject, GridLayout board, UndoRedoHandler undoRedoHandler, Controller gameController){
            this.board = board;
            this.gameController = gameController;
            this.boardObject = boardObject;
            this.undoRedoHandler = undoRedoHandler;
            id = boardObject.getId();
            if(id >= Game.OFFSET_INTER_BARRIER){
                type = boardObjectType.INTER_BARRIER;
                id = id - Game.OFFSET_INTER_BARRIER;
                col = id%8;
                line = id/8;
            }else if(id >= Game.OFFSET_HORIZONTAL_BARRIER){
                id = id - Game.OFFSET_HORIZONTAL_BARRIER;
                col = id%9;
                line = id/9;
                type = boardObjectType.HORITONTAL_BARRIER;
            }else if(id >= Game.OFFSET_VERTICAL_BARRIER){
                id = id - Game.OFFSET_VERTICAL_BARRIER;
                col = id%8;
                line = id/8;
                type = boardObjectType.VERTICAL_BARRIER;
            }else if(id >= Game.OFFSET_SQUARE){
                type = boardObjectType.SQUARE;
                id = id - Game.OFFSET_SQUARE;
                col = id%9;
                line = id/9;
            }
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            float x = event.getX();
            float y = event.getY();
            float xMiddle = (float)(boardObject.getWidth()/2.0);
            float yMiddle = (float)(boardObject.getHeight()/2.0);
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    LinkedList<FrameLayout> subjectsOfBackgroundChanges = new LinkedList<>();
                    if(type != boardObjectType.SQUARE) {
                        subjectsOfBackgroundChanges.add((FrameLayout) v);
                        if (type == boardObjectType.INTER_BARRIER) {
                            subjectsOfBackgroundChanges.add(getBoardObject(line, col, boardObjectType.VERTICAL_BARRIER));
                            subjectsOfBackgroundChanges.add(getBoardObject(line + 1, col, boardObjectType.VERTICAL_BARRIER));
                        } else if (type == boardObjectType.HORITONTAL_BARRIER) {
                            if ((col == 0 || x > xMiddle) && col != 8) {
                                subjectsOfBackgroundChanges.add(getBoardObject(line, col + 1, boardObjectType.HORITONTAL_BARRIER));
                                subjectsOfBackgroundChanges.add(getBoardObject(line, col, boardObjectType.INTER_BARRIER));
                            } else {
                                subjectsOfBackgroundChanges.add(getBoardObject(line, col - 1, boardObjectType.HORITONTAL_BARRIER));
                                subjectsOfBackgroundChanges.add(getBoardObject(line, col - 1, boardObjectType.INTER_BARRIER));
                            }
                        } else if (type == boardObjectType.VERTICAL_BARRIER) {
                            if ((line == 0 || y > yMiddle) && line != 8) {
                                subjectsOfBackgroundChanges.add(getBoardObject(line + 1, col, boardObjectType.VERTICAL_BARRIER));
                                subjectsOfBackgroundChanges.add(getBoardObject(line, col, boardObjectType.INTER_BARRIER));
                            } else {
                                subjectsOfBackgroundChanges.add(getBoardObject(line-1, col, boardObjectType.VERTICAL_BARRIER));
                                subjectsOfBackgroundChanges.add(getBoardObject(line-1, col, boardObjectType.INTER_BARRIER));
                            }
                        }
                    }else{
                        float angle = (float) Math.toDegrees(Math.atan2(y - yMiddle, x - xMiddle));
                        if(angle <= 0)
                            angle += 360;
                        angle = 360 - angle;
                        boolean canUp = line != 0;
                        boolean canDown = line != 8;
                        boolean canRight = col != 8;
                        boolean canLeft = col != 0;
                        boolean up = false, down = false,right = false,left = false,interUL = false,interUR = false,interDR = false,interDL = false,UUL = false,UUR = false,RRU = false,RRD = false,DDR = false,DDL = false,LLD = false,LLU = false;

                        if(angle > 0 && angle <= 45) {
                            if(canRight && canUp){
                                // | right up
                                right = true;
                                interUR = true;
                                UUR = true;
                            }else if(canRight){
                                // | right down
                                right = true;
                                interDR = true;
                                DDR = true;
                            }else if(canUp){
                                // -- up left
                                up = true;
                                interUL = true;
                                LLU = true;
                            }else{
                                // -- down left
                                down = true;
                                interDL = true;
                                LLD = true;
                            }
                        }else if(angle > 45 && angle <= 90){
                            if(canRight && canUp){
                                // -- up right
                                up = true;
                                interUR = true;
                                RRU = true;
                            }else if(canUp){
                                // -- up left
                                up = true;
                                interUL = true;
                                LLU = true;
                            }else if(canRight){
                                // | right down
                                right = true;
                                interDR = true;
                                DDR = true;
                            }else{
                                // | left down
                                left = true;
                                interDL = true;
                                DDL = true;
                            }
                        }else if(angle > 90 && angle <= 135){
                            if(canUp && canLeft){
                                // -- up left
                                up = true;
                                interUL = true;
                                LLU = true;
                            }else if(canUp){
                                // -- up right
                                up = true;
                                interUR = true;
                                RRU = true;
                            }else if(canLeft){
                                // | left down
                                left = true;
                                interDL = true;
                                DDL = true;
                            }else{
                                // | right down
                                right = true;
                                interDR = true;
                                DDR = true;
                            }
                        }else if(angle > 135 && angle <= 180){
                            if(canUp && canLeft){
                                // | left up
                                left = true;
                                interUL = true;
                                UUL = true;
                            }else if(canLeft){
                                // | left down
                                left = true;
                                interDL = true;
                                DDL = true;
                            }else if(canUp){
                                // -- up right
                                up = true;
                                interUR = true;
                                RRU = true;
                            }else{
                                // -- down right
                                down = true;
                                interDR = true;
                                RRD = true;
                            }
                        }else if(angle > 180 && angle <= 225){
                            if(canDown && canLeft){
                                // | left down
                                left = true;
                                interDL = true;
                                DDL = true;
                            }else if(canLeft){
                                // | left up
                                left = true;
                                interUL = true;
                                UUL = true;
                            }else if(canDown){
                                // -- down right
                                down = true;
                                interDR = true;
                                RRD = true;
                            }else{
                                // | right up
                                right = true;
                                interUR = true;
                                UUR = true;
                            }
                        }else if(angle > 225 && angle <= 270){
                            if(canDown && canLeft){
                                // -- down left
                                down = true;
                                interDL = true;
                                LLD = true;
                            }else if(canDown){
                                // -- down right
                                down = true;
                                interDR = true;
                                RRD = true;
                            }else if(canLeft){
                                // | left up
                                left = true;
                                interUL = true;
                                UUL = true;
                            }else{
                                // | right up
                                right = true;
                                interUR = true;
                                UUR = true;
                            }
                        }else if(angle > 270 && angle <= 315){
                            if(canDown && canRight){
                                // -- down right
                                down = true;
                                interDR = true;
                                RRD = true;
                            }else if(canDown){
                                // -- down left
                                down = true;
                                interDL = true;
                                LLD = true;
                            }else if(canRight){
                                // | right up
                                right = true;
                                interUR = true;
                                UUR = true;
                            }else{
                                // | left up
                                left = true;
                                interUL = true;
                                UUL = true;
                            }
                        }else if(angle > 315 && angle <= 360){
                            if(canDown && canRight){
                                // | right down
                                right = true;
                                interDR = true;
                                DDR = true;
                            }else if(canRight){
                                // | right up
                                right = true;
                                interUR = true;
                                UUR = true;
                            }else if(canDown){
                                // -- down left
                                down = true;
                                interDL = true;
                                LLD = true;
                            }else{
                                // -- up left
                                up = true;
                                interUL = true;
                                LLU = true;
                            }
                        }
                        if(up)
                            subjectsOfBackgroundChanges.add(getBoardObject(line-1, col, boardObjectType.HORITONTAL_BARRIER));
                        if(down)
                            subjectsOfBackgroundChanges.add(getBoardObject(line, col, boardObjectType.HORITONTAL_BARRIER));
                        if(right)
                            subjectsOfBackgroundChanges.add(getBoardObject(line, col, boardObjectType.VERTICAL_BARRIER));
                        if(left)
                            subjectsOfBackgroundChanges.add(getBoardObject(line, col-1, boardObjectType.VERTICAL_BARRIER));
                        if(interUL)
                            subjectsOfBackgroundChanges.add(getBoardObject(line-1, col-1, boardObjectType.INTER_BARRIER));
                        if(interUR)
                            subjectsOfBackgroundChanges.add(getBoardObject(line-1, col, boardObjectType.INTER_BARRIER));
                        if(interDR)
                            subjectsOfBackgroundChanges.add(getBoardObject(line, col, boardObjectType.INTER_BARRIER));
                        if(interDL)
                            subjectsOfBackgroundChanges.add(getBoardObject(line, col-1, boardObjectType.INTER_BARRIER));
                        if(UUL)
                            subjectsOfBackgroundChanges.add(getBoardObject(line-1, col-1, boardObjectType.VERTICAL_BARRIER));
                        if(UUR)
                            subjectsOfBackgroundChanges.add(getBoardObject(line-1, col, boardObjectType.VERTICAL_BARRIER));
                        if(RRU)
                            subjectsOfBackgroundChanges.add(getBoardObject(line-1, col+1, boardObjectType.HORITONTAL_BARRIER));
                        if(RRD)
                            subjectsOfBackgroundChanges.add(getBoardObject(line, col+1, boardObjectType.HORITONTAL_BARRIER));
                        if(DDR)
                            subjectsOfBackgroundChanges.add(getBoardObject(line+1, col, boardObjectType.VERTICAL_BARRIER));
                        if(DDL)
                            subjectsOfBackgroundChanges.add(getBoardObject(line+1, col-1, boardObjectType.VERTICAL_BARRIER));
                        if(LLD)
                            subjectsOfBackgroundChanges.add(getBoardObject(line, col-1, boardObjectType.HORITONTAL_BARRIER));
                        if(LLU)
                            subjectsOfBackgroundChanges.add(getBoardObject(line-1, col-1, boardObjectType.HORITONTAL_BARRIER));
                    }
                    undoRedoHandler.todo(new UndoRedoBarriers(subjectsOfBackgroundChanges), gameController);
                    undoRedoHandler.setUndoAutoOnAdd(true);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    undoRedoHandler.undo();
                    undoRedoHandler.setUndoAutoOnAdd(false);
                    break;
                case DragEvent.ACTION_DROP:
                    undoRedoHandler.setUndoAutoOnAdd(false);
                    undoRedoHandler.played(gameController);
                    break;
            }
            return true;
        }
    }

}