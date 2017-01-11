package tcd.hdelval.softwareengineering.controller;

import java.util.LinkedList;
import java.util.List;

import tcd.hdelval.softwareengineering.Modele.Gameboard;
import tcd.hdelval.softwareengineering.Modele.Square;
import tcd.hdelval.softwareengineering.Utils.Move;

/**
 * Created by hdelval on 13/11/16.
 */
public class AI {
    private final Gameboard theGameBoard;

    public AI(Gameboard theGameBoard) {
        this.theGameBoard = theGameBoard;
    }

    public Move getNextMove(boolean isPlayer1, int depth){
        LinkedList<Move> moves = generateAllPossibleMoves(isPlayer1);
        Move choosenMove = null;
        if(depth == 1){
            for (Move move : moves) {
                move.doMove(theGameBoard);
                double evaluation = getEvaluation(isPlayer1);
                if(isPlayer1) {
                    // max player
                    if (choosenMove == null || evaluation > choosenMove.getScore()) {
                        choosenMove = move;
                        choosenMove.setScore(evaluation);
                    }
                }else {
                    // min player
                    if (choosenMove == null || evaluation < choosenMove.getScore()) {
                        choosenMove = move;
                        choosenMove.setScore(evaluation);
                    }
                }
                move.undoMove(theGameBoard);
            }
        }else{
            for (Move move : moves) {
                move.doMove(theGameBoard);
                move.setScore(getNextMove(!isPlayer1, depth-1).getScore());
                if(isPlayer1) {
                    // max player
                    if (choosenMove == null || move.getScore() > choosenMove.getScore())
                        choosenMove = move;
                }else {
                    // min player
                    if (choosenMove == null || move.getScore() < choosenMove.getScore())
                        choosenMove = move;
                }
                move.undoMove(theGameBoard);
            }
        }
        return choosenMove;
    }

    private double getEvaluation(boolean isPlayer1) {
        double positionFeature   = theGameBoard.positionPlayer1.getY() + theGameBoard.positionPlayer2.getY();
        double barriersFeature   = theGameBoard.getNbBarriersP1() - theGameBoard.getNbBarriersP2();
        double barriersBeforeYou = theGameBoard.getBarriersBeforeFeature();
        double beenThereFeature  = theGameBoard.calculateBeenThereFeatture();
        return positionFeature*0.6 + barriersFeature*0.2 + barriersBeforeYou*0.3 + beenThereFeature;
    }

    private LinkedList<Move> generateAllPossibleMoves(boolean isPlayer1){
        LinkedList<Move> result = new LinkedList<>();
        if(theGameBoard.playerCanAddBarrier(isPlayer1)){
            for(int col=0 ; col<7 ; ++col){
                for(int line=0 ; line<7 ; ++line){
                    Square square1 = theGameBoard.getSquareFromCoord(col, line);
                    Square square1Bis = theGameBoard.getSquareFromCoord(col, line+1);
                    Square square2 = theGameBoard.getSquareFromCoord(col+1, line);
                    Square square2Bis = theGameBoard.getSquareFromCoord(col+1, line+1);
                    if(theGameBoard.canAddBarrier(square1, square1Bis, square2, square2Bis) && (theGameBoard.distFromClosestBarrier(col, line, 1) || theGameBoard.distFromClosestPawn(col, line, 1))){
                        result.add(new Move(Move.HORIZONTAL, line, col, col+1, isPlayer1));
                    }
                    if(theGameBoard.canAddBarrier(square1, square2, square1Bis, square2Bis) && (theGameBoard.distFromClosestBarrier(col, line, 1) || theGameBoard.distFromClosestPawn(col, line, 1))){
                        result.add(new Move(Move.VERTICAL, col, line, line+1, isPlayer1));
                    }
                }
            }
        }
        if(isPlayer1){
            for (Square square : theGameBoard.positionPlayer1.getNeighbours()) {
                result.add(new Move(square, true));
            }
        }else{
            for (Square square : theGameBoard.positionPlayer2.getNeighbours()) {
                result.add(new Move(square, false));
            }
        }
        return result;
    }
}
