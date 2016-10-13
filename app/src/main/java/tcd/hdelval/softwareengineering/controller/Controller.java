package tcd.hdelval.softwareengineering.controller;

import android.widget.FrameLayout;

import java.util.LinkedList;
import java.util.List;

import tcd.hdelval.softwareengineering.Modele.Gameboard;
import tcd.hdelval.softwareengineering.Modele.Square;
import tcd.hdelval.softwareengineering.View.Game;
import tcd.hdelval.softwareengineering.View.UndoRedo.UndoRedoBarriers;

/**
 * Created by hdelval on 04/10/16.
 */

public class Controller {
    Game theGame;
    Gameboard theGameBoard;

    public Controller(Game theGame){
        this.theGame = theGame;
        this.theGameBoard = new Gameboard();
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
    }

    private void addBarriersFromUndoRedo(UndoRedoBarriers undoRedoBarriers){
        for (FrameLayout frameLayout : undoRedoBarriers.getSubjectsOfBackgroundChanges()) {
            int id = frameLayout.getId();
            if(id >= Game.OFFSET_VERTICAL_BARRIER && id < Game.OFFSET_HORIZONTAL_BARRIER){
                id -= Game.OFFSET_VERTICAL_BARRIER;
                int line = id / 8;
                int colFirstSquare = id % 8;
                int colSecondSquare = id % 8 + 1;
                addVerticalBarrier(line, colFirstSquare, colSecondSquare);
            }else if(id >= Game.OFFSET_HORIZONTAL_BARRIER && id < Game.OFFSET_INTER_BARRIER){
                id -= Game.OFFSET_HORIZONTAL_BARRIER;
                int col = id % 9;
                int lineFirstSquare = id / 9;
                int lineSecondSquare = 1 + id / 9;
                addHorizontalBarrier(col, lineFirstSquare, lineSecondSquare);
            }
        }
    }

    private void removeBarriersFromUndoRedo(UndoRedoBarriers undoRedoBarriers){
        for (FrameLayout frameLayout : undoRedoBarriers.getSubjectsOfBackgroundChanges()) {
            int id = frameLayout.getId();
            if(id >= Game.OFFSET_VERTICAL_BARRIER && id < Game.OFFSET_HORIZONTAL_BARRIER){
                id -= Game.OFFSET_VERTICAL_BARRIER;
                int line = id / 8;
                int colFirstSquare = id % 8;
                int colSecondSquare = id % 8 + 1;
                removeVerticalBarrier(line, colFirstSquare, colSecondSquare);
            }else if(id >= Game.OFFSET_HORIZONTAL_BARRIER && id < Game.OFFSET_INTER_BARRIER){
                id -= Game.OFFSET_HORIZONTAL_BARRIER;
                int col = id % 9;
                int lineFirstSquare = id / 9;
                int lineSecondSquare = 1 + id / 9;
                removeHorizontalBarrier(col, lineFirstSquare, lineSecondSquare);
            }
        }
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

    private void removeHorizontalBarrier(int col, int lineFirstSquare, int lineSecondSquare) {
        Square squareFromCoord1 = theGameBoard.getSquareFromCoord(col, lineFirstSquare);
        Square squareFromCoord2 = theGameBoard.getSquareFromCoord(col, lineSecondSquare);
        squareFromCoord1.addAccessible(squareFromCoord2);
        squareFromCoord2.addAccessible(squareFromCoord1);
    }

    private void removeVerticalBarrier(int line, int colFirstSquare, int colSecondSquare) {
        Square squareFromCoord1 = theGameBoard.getSquareFromCoord(colFirstSquare, line);
        Square squareFromCoord2 = theGameBoard.getSquareFromCoord(colSecondSquare, line);
        squareFromCoord1.addAccessible(squareFromCoord2);
        squareFromCoord2.addAccessible(squareFromCoord1);
    }

    public void toggleUser(int action) {
        int idPlayer = theGame.getCurrentPawnId();
        if(action==Game.BARRIER_PLAYED){
            if(idPlayer == 1)
                theGameBoard.removeBarrierP1();
            else
                theGameBoard.removeBarrierP2();
        }
        theGame.toggleUser(idPlayer, action);
    }

    public boolean canAddBarrier(UndoRedoBarriers action) {
        addBarriersFromUndoRedo(action);
        boolean gameboardPlayable = bothPlayerCanAccessTheGoal();
        removeBarriersFromUndoRedo(action);
        return gameboardPlayable;
    }

    private boolean bothPlayerCanAccessTheGoal() {
        return theGameBoard.floodFillGamePlayable();
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
}
