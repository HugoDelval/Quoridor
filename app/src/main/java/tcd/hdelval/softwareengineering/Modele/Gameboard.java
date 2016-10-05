package tcd.hdelval.softwareengineering.Modele;

import java.util.HashMap;

/**
 * Created by hdelval on 04/10/16.
 */

public enum Gameboard {
    THE_GAME_BOARD;
    private HashMap<Integer, Square> theGameBoard;
    public Square positionPlayer1;
    public Square positionPlayer2;

    Gameboard(){
        theGameBoard = new HashMap<>();
        for(int x=0; x<9; x++)
            for(int y=0; y<9; y++){
                Square squareToAdd = new Square(x, y);
                theGameBoard.put(getIdFromCoord(x,y), squareToAdd);
            }
        for(int x=0; x<9; x++)
            for(int y=0; y<9; y++){
                Square squareToComplete = getSquareFromCoord(x,y);
                squareToComplete.addNeighbour(getSquareFromCoord(x-1,y));
                squareToComplete.addNeighbour(getSquareFromCoord(x+1,y));
                squareToComplete.addNeighbour(getSquareFromCoord(x,y-1));
                squareToComplete.addNeighbour(getSquareFromCoord(x,y+1));
                squareToComplete.setAccessibles(squareToComplete.getNeighbours());
            }
        positionPlayer1 = theGameBoard.get(getIdFromCoord(4,0));
        positionPlayer2 = theGameBoard.get(getIdFromCoord(4,8));
    }

    /**
     * Return the id of a square based on his coordinate
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return the id of the square
     */
    private static int getIdFromCoord(int x, int y){
        if(x>8 || x<0 || y>8 || y<0)
            return -1;
        return x+y*9;
    }

    public Square getSquareFromCoord(int x, int y){
        return getSquareFromId(getIdFromCoord(x, y));
    }

    public Square getSquareFromId(int squareId){
        return theGameBoard.get(squareId);
    }
}
