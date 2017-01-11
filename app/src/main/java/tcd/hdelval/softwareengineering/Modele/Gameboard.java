package tcd.hdelval.softwareengineering.Modele;

import android.util.SparseArray;
import android.util.SparseBooleanArray;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by hdelval on 04/10/16.
 */

public class Gameboard {
    private SparseArray<Square> theGameBoard;
    private SparseBooleanArray takenMiniSquares;
    private SparseArray<Integer> beenThereP1;
    private SparseArray<Integer> beenThereP2;
    public Square positionPlayer1;
    public Square positionPlayer2;
    private int nbBarriersP1 = 10;
    private int nbBarriersP2 = 10;


    public Gameboard(){
        theGameBoard = new SparseArray<>();
        takenMiniSquares = new SparseBooleanArray();
        beenThereP1 = new SparseArray<>();
        beenThereP2 = new SparseArray<>();
        for(int x=0; x<9; x++)
            for(int y=0; y<9; y++){
                Square squareToAdd = new Square(x, y, this);
                theGameBoard.put(getIdFromCoord(x,y), squareToAdd);
                beenThereP1.put(getIdFromCoord(x,y), 0);
                beenThereP2.put(getIdFromCoord(x,y), 0);
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

    public void removeBarrierP1() {
        nbBarriersP1--;
    }

    public void removeBarrierP2() {
        nbBarriersP2--;
    }

    public void addBarrierP1() {
        nbBarriersP1++;
    }

    public void addBarrierP2() {
        nbBarriersP2++;
    }

    public int getNbBarriersP1() {
        return nbBarriersP1;
    }

    public int getNbBarriersP2() {
        return nbBarriersP2;
    }

    public boolean floodFillGamePlayable() {
        return floodFill(positionPlayer1, 8, new LinkedList<Square>()) && floodFill(positionPlayer2, 0, new LinkedList<Square>());
    }

    private boolean floodFill(Square positionSeed, int goal, LinkedList<Square> visited) {
        if(positionSeed.getY() == goal)
            return true;
        visited.add(positionSeed);
        for (Square neighbour : positionSeed.getNeighbours()) {
            if(!visited.contains(neighbour)){
                if(floodFill(neighbour, goal, visited))
                    return true;
            }
        }
        return false;
    }

    public boolean canAddBarrier(Square square1, Square square1Bis, Square square2, Square square2Bis){
        int minX = square1.getX()>square2Bis.getX()?square2Bis.getX():square1.getX();
        int minY = square1.getY()>square2Bis.getY()?square2Bis.getY():square1.getY();
        boolean canAdd = square1.isAccessible(square1Bis) && square2.isAccessible(square2Bis) && (!takenMiniSquares.get(minX + minY*8));
        if(!canAdd)
            return false;
        square1.removeAccessible(square1Bis);
        square1Bis.removeAccessible(square1);
        square2.removeAccessible(square2Bis);
        square2Bis.removeAccessible(square2);
        boolean playable = floodFillGamePlayable();
        square1.addAccessible(square1Bis);
        square1Bis.addAccessible(square1);
        square2.addAccessible(square2Bis);
        square2Bis.addAccessible(square2);
        return playable;
    }

    public boolean playerCanAddBarrier(boolean isPlayer1){
        if(isPlayer1)
            return nbBarriersP1>0;
        else
            return nbBarriersP2>0;
    }

    public void addTakenMiniSquare(Square s){
        takenMiniSquares.put(s.getX()+s.getY()*8, true);
    }

    public void removeTakenMiniSquare(Square s){
        takenMiniSquares.put(s.getX()+s.getY()*8, false);
    }

    public boolean distFromClosestBarrier(int col, int line, int minDist) {
        for(int i = 0; i < takenMiniSquares.size(); i++) {
            int key = takenMiniSquares.keyAt(i);
            if(takenMiniSquares.get(key, false)){
                int col2 = key%8;
                int line2 = key/8;
                int dist = Math.abs(col-col2) + Math.abs(line-line2);
                if(dist <= minDist){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean distFromClosestPawn(int col, int line, int minDist) {
        double refCol1 = positionPlayer1.getX() - 0.5;
        double refLine1 = positionPlayer1.getY() - 0.5;
        double refCol2 = positionPlayer2.getX() - 0.5;
        double refLine2 = positionPlayer2.getY() - 0.5;
        double dist1 = Math.abs(col-refCol1) + Math.abs(line-refLine1);
        double dist2 = Math.abs(col-refCol2) + Math.abs(line-refLine2);
        return dist1 <= minDist || dist2 <= minDist;
    }

    public double getBarriersBeforeFeature() {
        double res = 0;
        for(int i = 0; i < takenMiniSquares.size(); i++) {
            int key = takenMiniSquares.keyAt(i);
            if(takenMiniSquares.get(key, false)){
                int line = key/8;
                int col = key%8;
                if(line >= positionPlayer1.getY()){
                    res = res - (1 + Math.abs(col - positionPlayer1.getX())/16 ) ;
                }
                if(line < positionPlayer2.getY()){
                    res = res + (1 - Math.abs(col - positionPlayer2.getX())/16 );
                }
            }
        }
        return res;
    }

    public double calculateBeenThereFeatture() {
        int idP1 = getIdFromCoord(positionPlayer1.getX(), positionPlayer1.getY());
        int idP2 = getIdFromCoord(positionPlayer2.getX(), positionPlayer2.getY());
        return beenThereP1.get(idP1, 0) + beenThereP2.get(idP2, 0);
    }

    public void addBeenThere(boolean isPlayer1, Square direction) {
        int id = getIdFromCoord(direction.getX(), direction.getY());
        if(isPlayer1){
            beenThereP1.put(id, beenThereP1.get(id, 0) - 1);
        }else{
            beenThereP2.put(id, beenThereP2.get(id, 0) + 1);
        }
    }
}
