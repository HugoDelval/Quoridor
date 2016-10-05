package tcd.hdelval.softwareengineering.Controller;

import tcd.hdelval.softwareengineering.Modele.Gameboard;
import tcd.hdelval.softwareengineering.Modele.Square;
import tcd.hdelval.softwareengineering.View.Game;

/**
 * Created by hdelval on 04/10/16.
 */

public class Controller {
    Game theGame;

    public Controller(Game theGame){
        this.theGame = theGame;
    }

    public void movePawn(int x, int y, int pawnId){
        Square oldPosition;
        Square newPosition = Gameboard.THE_GAME_BOARD.getSquareFromCoord(x, y);
        boolean weHaveToRecalculateNeighbours = Gameboard.THE_GAME_BOARD.positionPlayer1.getAccessibles().contains(Gameboard.THE_GAME_BOARD.positionPlayer2);
        if(pawnId == 1) {
            oldPosition = Gameboard.THE_GAME_BOARD.positionPlayer1;
            Gameboard.THE_GAME_BOARD.positionPlayer1 = newPosition;
        }else {
            oldPosition = Gameboard.THE_GAME_BOARD.positionPlayer2;
            Gameboard.THE_GAME_BOARD.positionPlayer2 = newPosition;
        }
        if (weHaveToRecalculateNeighbours || Gameboard.THE_GAME_BOARD.positionPlayer1.getNeighbours().contains(Gameboard.THE_GAME_BOARD.positionPlayer2)){
            // the players are/were closed to each other -> we have to recalculate the neighbours
            oldPosition.calculateNeighbours();
            Gameboard.THE_GAME_BOARD.positionPlayer2.calculateNeighbours();
            Gameboard.THE_GAME_BOARD.positionPlayer1.calculateNeighbours();
        }
    }
}
