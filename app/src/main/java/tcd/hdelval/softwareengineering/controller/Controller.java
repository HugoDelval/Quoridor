package tcd.hdelval.softwareengineering.controller;

import android.widget.FrameLayout;

import java.util.LinkedList;
import java.util.List;

import tcd.hdelval.softwareengineering.Modele.Gameboard;
import tcd.hdelval.softwareengineering.Modele.Square;
import tcd.hdelval.softwareengineering.R;
import tcd.hdelval.softwareengineering.Utils.Listeners;
import tcd.hdelval.softwareengineering.Utils.Move;
import tcd.hdelval.softwareengineering.View.Game;
import tcd.hdelval.softwareengineering.View.UndoRedo.UndoRedoBarriers;

import static tcd.hdelval.softwareengineering.View.Game.BARRIER_PLAYED;
import static tcd.hdelval.softwareengineering.View.Game.boardObjectType.HORITONTAL_BARRIER;
import static tcd.hdelval.softwareengineering.View.Game.boardObjectType.INTER_BARRIER;
import static tcd.hdelval.softwareengineering.View.Game.boardObjectType.VERTICAL_BARRIER;

/**
 * Created by hdelval on 04/10/16.
 */

public class Controller {
    Game theGame;
    Gameboard theGameBoard;
    AI aiPlayer;

    public Controller(Game theGame){
        this.theGame = theGame;
        this.theGameBoard = new Gameboard();
        this.aiPlayer = new AI(theGameBoard);
    }

    public void movePawn(int x, int y, int pawnId){
        Square oldPosition;
        Square newPosition = theGameBoard.getSquareFromCoord(x, y);
        boolean weHaveToRecalculateNeighbours = theGameBoard.positionPlayer1.getAccessibles().contains(theGameBoard.positionPlayer2);
        if(pawnId == 1) {
            oldPosition = theGameBoard.positionPlayer1;
            theGameBoard.positionPlayer1 = newPosition;
        }else {
            oldPosition = theGameBoard.positionPlayer2;
            theGameBoard.positionPlayer2 = newPosition;
        }
        if (weHaveToRecalculateNeighbours || theGameBoard.positionPlayer1.getNeighbours().contains(theGameBoard.positionPlayer2)){
            // the players are/were closed to each other -> we have to recalculate the neighbours
            oldPosition.calculateNeighbours();
            theGameBoard.positionPlayer2.calculateNeighbours();
            theGameBoard.positionPlayer1.calculateNeighbours();
        }
        theGameBoard.addBeenThere(false, newPosition);
    }

    private void addBarriersFromUndoRedo(UndoRedoBarriers undoRedoBarriers){
        int lineMiniSquare = 99, colMiniSquare = 99;
        for (FrameLayout frameLayout : undoRedoBarriers.getSubjectsOfBackgroundChanges()) {
            int id = frameLayout.getId();
            if(id >= Game.OFFSET_VERTICAL_BARRIER && id < Game.OFFSET_HORIZONTAL_BARRIER){
                id -= Game.OFFSET_VERTICAL_BARRIER;
                int line = id / 8;
                int colFirstSquare = id % 8;
                int colSecondSquare = id % 8 + 1;
                addVerticalBarrier(line, colFirstSquare, colSecondSquare);
                if(lineMiniSquare > line)
                    lineMiniSquare = line;
                if(colMiniSquare > colFirstSquare)
                    colMiniSquare = colFirstSquare;
            }else if(id >= Game.OFFSET_HORIZONTAL_BARRIER && id < Game.OFFSET_INTER_BARRIER){
                id -= Game.OFFSET_HORIZONTAL_BARRIER;
                int col = id % 9;
                int lineFirstSquare = id / 9;
                int lineSecondSquare = 1 + id / 9;
                addHorizontalBarrier(col, lineFirstSquare, lineSecondSquare);
                if(lineMiniSquare > lineFirstSquare)
                    lineMiniSquare = lineFirstSquare;
                if(colMiniSquare > col)
                    colMiniSquare = col;
            }
        }
        theGameBoard.addTakenMiniSquare(theGameBoard.getSquareFromCoord(colMiniSquare, lineMiniSquare));
    }

    public void addBarriersFromStackUndoRedo(List<UndoRedoBarriers> stackUndo) {
        for (UndoRedoBarriers undoRedoBarriers : stackUndo) {
            addBarriersFromUndoRedo(undoRedoBarriers);
        }
    }

    private void addHorizontalBarrier(int col, int lineFirstSquare, int lineSecondSquare) {
        Square squareFromCoord1 = theGameBoard.getSquareFromCoord(col, lineFirstSquare);
        Square squareFromCoord2 = theGameBoard.getSquareFromCoord(col, lineSecondSquare);
        squareFromCoord1.removeAccessible(squareFromCoord2);
        squareFromCoord2.removeAccessible(squareFromCoord1);
    }

    private void addVerticalBarrier(int line, int colFirstSquare, int colSecondSquare) {
        Square squareFromCoord1 = theGameBoard.getSquareFromCoord(colFirstSquare, line);
        Square squareFromCoord2 = theGameBoard.getSquareFromCoord(colSecondSquare, line);
        squareFromCoord1.removeAccessible(squareFromCoord2);
        squareFromCoord2.removeAccessible(squareFromCoord1);
    }

    public void toggleUser(int action) {
        int idPlayer = theGame.getCurrentPawnId();
        if(action== BARRIER_PLAYED){
            if(idPlayer == 1)
                theGameBoard.removeBarrierP1();
            else
                theGameBoard.removeBarrierP2();
        }
        theGame.toggleUser(idPlayer, action);
    }

    public boolean canAddBarrier(UndoRedoBarriers action) {
        Square square1 = null, square1Bis = null, square2 = null, square2Bis = null;
        boolean done = false;
        for (FrameLayout frameLayout : action.getSubjectsOfBackgroundChanges()) {
            int id = frameLayout.getId();
            if(id >= Game.OFFSET_VERTICAL_BARRIER && id < Game.OFFSET_HORIZONTAL_BARRIER){
                id -= Game.OFFSET_VERTICAL_BARRIER;
                int line = id / 8;
                int colFirstSquare = id % 8;
                int colSecondSquare = id % 8 + 1;
                if(!done) {
                    square1 = theGameBoard.getSquareFromCoord(colFirstSquare, line);
                    square1Bis = theGameBoard.getSquareFromCoord(colSecondSquare, line);
                    done = true;
                }else{
                    square2 = theGameBoard.getSquareFromCoord(colFirstSquare, line);
                    square2Bis = theGameBoard.getSquareFromCoord(colSecondSquare, line);
                }
            }else if(id >= Game.OFFSET_HORIZONTAL_BARRIER && id < Game.OFFSET_INTER_BARRIER){
                id -= Game.OFFSET_HORIZONTAL_BARRIER;
                int col = id % 9;
                int lineFirstSquare = id / 9;
                int lineSecondSquare = 1 + id / 9;
                if(!done) {
                    square1 = theGameBoard.getSquareFromCoord(col, lineFirstSquare);
                    square1Bis = theGameBoard.getSquareFromCoord(col, lineSecondSquare);
                    done = true;
                }else{
                    square2 = theGameBoard.getSquareFromCoord(col, lineFirstSquare);
                    square2Bis = theGameBoard.getSquareFromCoord(col, lineSecondSquare);
                }
            }
        }
        return theGameBoard.canAddBarrier(square1, square1Bis, square2, square2Bis);
    }

    public int getNbBarriersP1() {
        return theGameBoard.getNbBarriersP1();
    }

    public int getNbBarriersP2() {
        return theGameBoard.getNbBarriersP2();
    }

    public boolean player2HasEnoughBarriers() {
        return theGameBoard.getNbBarriersP2()>0;
    }

    public boolean player1HasEnoughBarriers() {
        return theGameBoard.getNbBarriersP1()>0;
    }

    public LinkedList<Square> getNeighboursP2() {
        return theGameBoard.positionPlayer2.getNeighbours();
    }

    public LinkedList<Square> getNeighboursP1() {
        return theGameBoard.positionPlayer1.getNeighbours();
    }

    public void aiMove(){
        Move aiMove = aiPlayer.getNextMove(true, 3);
        int moveType = aiMove.getType();
        if(moveType == Move.MOVE_PAWN){
            Square direction = aiMove.getDirection();
            theGame.movePawn(theGame.getSquare(direction.getX(), direction.getY()), 1);
            theGameBoard.addBeenThere(true, direction);
        }else if(moveType == Move.PLACE_BARRIER){
            int orientation = aiMove.getOrientation();
            if(orientation == Move.HORIZONTAL){
                int line = aiMove.getColOrLine();
                int col1 = aiMove.getColOrLine1stSquare();
                int col2 = aiMove.getColOrLine2ndSquare();
                FrameLayout left = theGame.getBoardObject(line, col1, HORITONTAL_BARRIER);
                FrameLayout middle = theGame.getBoardObject(line, col1>col2?col2:col1, INTER_BARRIER);
                FrameLayout right = theGame.getBoardObject(line, col2, HORITONTAL_BARRIER);
                left.setBackgroundResource(R.drawable.light_wood);
                middle.setBackgroundResource(R.drawable.light_wood);
                right.setBackgroundResource(R.drawable.light_wood);
                addHorizontalBarrier(col1, line, line+1);
                addHorizontalBarrier(col2, line, line+1);
                theGameBoard.addTakenMiniSquare(theGameBoard.getSquareFromCoord(col1>col2?col2:col1, line));
            }else if(orientation == Move.VERTICAL){
                int col = aiMove.getColOrLine();
                int line1 = aiMove.getColOrLine1stSquare();
                int line2 = aiMove.getColOrLine2ndSquare();
                FrameLayout top = theGame.getBoardObject(line1, col, VERTICAL_BARRIER);
                FrameLayout middle = theGame.getBoardObject(line1>line2?line2:line1, col, INTER_BARRIER);
                FrameLayout bottom = theGame.getBoardObject(line2, col, VERTICAL_BARRIER);
                top.setBackgroundResource(R.drawable.light_wood);
                middle.setBackgroundResource(R.drawable.light_wood);
                bottom.setBackgroundResource(R.drawable.light_wood);
                addVerticalBarrier(line1, col, col+1);
                addVerticalBarrier(line2, col, col+1);
                theGameBoard.addTakenMiniSquare(theGameBoard.getSquareFromCoord(col, line1>line2?line2:line1));
            }

            toggleUser(BARRIER_PLAYED);
        }
    }
}
