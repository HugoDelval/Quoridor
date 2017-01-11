package tcd.hdelval.softwareengineering.Utils;

import tcd.hdelval.softwareengineering.Modele.Gameboard;
import tcd.hdelval.softwareengineering.Modele.Square;

/**
 * Created by hdelval on 13/11/16.
 */
public class Move {

    public static final int MOVE_PAWN = 0;
    public static final int PLACE_BARRIER = 1;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private final int type;
    private final boolean isPlayer1;
    private int orientation;
    private int colOrLine;
    private int colOrLine1stSquare;
    private int colOrLine2ndSquare;
    private Square direction;
    private double score;
    private Square oldPosition;

    public Move(Square square, boolean isPlayer1) {
        this.type = MOVE_PAWN;
        this.isPlayer1 = isPlayer1;
        this.direction = square;
        this.score = 0;
    }

    public Move(int orientation, int colOrLine, int colOrLine1stSquare, int colOrLine2ndSquare, boolean isPlayer1) {
        this.type = PLACE_BARRIER;
        this.orientation = orientation;
        this.colOrLine = colOrLine;
        this.colOrLine1stSquare = colOrLine1stSquare;
        this.colOrLine2ndSquare = colOrLine2ndSquare;
        this.isPlayer1 = isPlayer1;
        this.score = 0;
    }

    public int getType() {
        return type;
    }

    public Square getDirection() {
        return direction;
    }

    public int getColOrLine2ndSquare() {
        return colOrLine2ndSquare;
    }

    public int getColOrLine1stSquare() {
        return colOrLine1stSquare;
    }

    public int getColOrLine() {
        return colOrLine;
    }

    public int getOrientation() {
        return orientation;
    }

    private void addHorizontalBarrier(int col, int lineFirstSquare, int lineSecondSquare, Gameboard theGameBoard) {
        Square squareFromCoord1 = theGameBoard.getSquareFromCoord(col, lineFirstSquare);
        Square squareFromCoord2 = theGameBoard.getSquareFromCoord(col, lineSecondSquare);
        squareFromCoord1.removeAccessible(squareFromCoord2);
        squareFromCoord2.removeAccessible(squareFromCoord1);
    }

    private void addVerticalBarrier(int line, int colFirstSquare, int colSecondSquare, Gameboard theGameBoard) {
        Square squareFromCoord1 = theGameBoard.getSquareFromCoord(colFirstSquare, line);
        Square squareFromCoord2 = theGameBoard.getSquareFromCoord(colSecondSquare, line);
        squareFromCoord1.removeAccessible(squareFromCoord2);
        squareFromCoord2.removeAccessible(squareFromCoord1);
    }

    private void removeHorizontalBarrier(int col, int lineFirstSquare, int lineSecondSquare, Gameboard theGameBoard) {
        Square squareFromCoord1 = theGameBoard.getSquareFromCoord(col, lineFirstSquare);
        Square squareFromCoord2 = theGameBoard.getSquareFromCoord(col, lineSecondSquare);
        squareFromCoord1.addAccessible(squareFromCoord2);
        squareFromCoord2.addAccessible(squareFromCoord1);
    }

    private void removeVerticalBarrier(int line, int colFirstSquare, int colSecondSquare, Gameboard theGameBoard) {
        Square squareFromCoord1 = theGameBoard.getSquareFromCoord(colFirstSquare, line);
        Square squareFromCoord2 = theGameBoard.getSquareFromCoord(colSecondSquare, line);
        squareFromCoord1.addAccessible(squareFromCoord2);
        squareFromCoord2.addAccessible(squareFromCoord1);
    }


    public void doMove(Gameboard theGameBoard){
        if(this.type == MOVE_PAWN){
            boolean weHaveToRecalculateNeighbours = theGameBoard.positionPlayer1.getAccessibles().contains(theGameBoard.positionPlayer2);
            if(isPlayer1) {
                Square tmp = theGameBoard.positionPlayer1;
                theGameBoard.positionPlayer1 = direction;
                oldPosition = tmp;
            } else {
                Square tmp = theGameBoard.positionPlayer2;
                theGameBoard.positionPlayer2 = direction;
                oldPosition = tmp;
            }
            if(weHaveToRecalculateNeighbours || theGameBoard.positionPlayer1.getNeighbours().contains(theGameBoard.positionPlayer2)){
                // the players are/were closed to each other -> we have to recalculate the neighbours
                oldPosition.calculateNeighbours();
                theGameBoard.positionPlayer2.calculateNeighbours();
                theGameBoard.positionPlayer1.calculateNeighbours();
            }
        }else if(type == PLACE_BARRIER) {
            if (orientation == HORIZONTAL) {
                addHorizontalBarrier(colOrLine1stSquare, colOrLine, colOrLine+1, theGameBoard);
                addHorizontalBarrier(colOrLine2ndSquare, colOrLine, colOrLine+1, theGameBoard);
                theGameBoard.addTakenMiniSquare(theGameBoard.getSquareFromCoord(colOrLine1stSquare>colOrLine2ndSquare?colOrLine2ndSquare:colOrLine1stSquare, colOrLine));
            } else if (orientation == VERTICAL) {
                addVerticalBarrier(colOrLine1stSquare, colOrLine, colOrLine+1, theGameBoard);
                addVerticalBarrier(colOrLine2ndSquare, colOrLine, colOrLine+1, theGameBoard);
                theGameBoard.addTakenMiniSquare(theGameBoard.getSquareFromCoord(colOrLine, colOrLine1stSquare>colOrLine2ndSquare?colOrLine2ndSquare:colOrLine1stSquare));
            }
            if (isPlayer1)
                theGameBoard.removeBarrierP1();
            else
                theGameBoard.removeBarrierP2();
        }
    }

    public void undoMove(Gameboard theGameBoard){
        if(this.type == MOVE_PAWN){
            boolean weHaveToRecalculateNeighbours = theGameBoard.positionPlayer1.getAccessibles().contains(theGameBoard.positionPlayer2);
            if(isPlayer1) {
                Square tmp = theGameBoard.positionPlayer1;
                theGameBoard.positionPlayer1 = oldPosition;
                oldPosition = tmp;
            } else {
                Square tmp = theGameBoard.positionPlayer2;
                theGameBoard.positionPlayer2 = oldPosition;
                oldPosition = tmp;
            }
            if(weHaveToRecalculateNeighbours || theGameBoard.positionPlayer1.getNeighbours().contains(theGameBoard.positionPlayer2)){
                // the players are/were closed to each other -> we have to recalculate the neighbours
                oldPosition.calculateNeighbours();
                theGameBoard.positionPlayer2.calculateNeighbours();
                theGameBoard.positionPlayer1.calculateNeighbours();
            }
        }else if(type == PLACE_BARRIER) {
            if (orientation == HORIZONTAL) {
                removeHorizontalBarrier(colOrLine1stSquare, colOrLine, colOrLine+1, theGameBoard);
                removeHorizontalBarrier(colOrLine2ndSquare, colOrLine, colOrLine+1, theGameBoard);
                theGameBoard.removeTakenMiniSquare(theGameBoard.getSquareFromCoord(colOrLine1stSquare>colOrLine2ndSquare?colOrLine2ndSquare:colOrLine1stSquare, colOrLine));
            } else if (orientation == VERTICAL) {
                removeVerticalBarrier(colOrLine1stSquare, colOrLine, colOrLine+1, theGameBoard);
                removeVerticalBarrier(colOrLine2ndSquare, colOrLine, colOrLine+1, theGameBoard);
                theGameBoard.removeTakenMiniSquare(theGameBoard.getSquareFromCoord(colOrLine, colOrLine1stSquare>colOrLine2ndSquare?colOrLine2ndSquare:colOrLine1stSquare));
            }
            if (isPlayer1)
                theGameBoard.addBarrierP1();
            else
                theGameBoard.addBarrierP2();
        }
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
