package tcd.hdelval.softwareengineering.Modele;

import java.util.LinkedList;

/**
 * Created by hdelval on 04/10/16.
 */

/**
 * Represent a square on the gameboard where a pawn can go
 */
public class Square {
    private final Gameboard gameBoard;

    /**
     * Horizontal coordinate of the square in the Gameboard
     */
    private int x;

    /**
     * Vertical coordinate of the square in the Gameboard
     */
    private int y;

    /**
     * The id of the square in the gameboard (x+y*9)
     */
    private int idSquare;

    /**
     * Which squares can a pawn on this square go to ?
     */
    private LinkedList<Square> neighbours = new LinkedList<>();

    /**
     * Which squares are accessible from this square ?
     * /!\ different from neighbours ! We ignore pawns for this list
     * (it exists just to facilitate calculus)
     */
    private LinkedList<Square> accessibles = new LinkedList<>();

    public Square(int x, int y, Gameboard gameBoard){
        this.x = x;
        this.y = y;
        this.idSquare = x+y*9;
        this.gameBoard = gameBoard;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getIdSquare() {
        return idSquare;
    }

    public LinkedList<Square> getNeighbours() {
        return neighbours;
    }

    public void addNeighbour(Square neighbour) {
        if(neighbour != null)
            this.neighbours.add(neighbour);
    }

    public LinkedList<Square> getAccessibles() {
        return accessibles;
    }

    public void setAccessibles(LinkedList<Square> accessibles) {
        this.accessibles = accessibles;
    }

    public void removeAccessible(Square accessible) {
        try {
            this.accessibles.remove(accessible);
        }catch (Exception ignored){}
        calculateNeighbours();
    }

    public void addAccessible(Square accessible) {
        if(accessible != null)
            this.accessibles.add(accessible);
        calculateNeighbours();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Square && ((Square) obj).idSquare == this.idSquare;
    }

    public void calculateNeighbours() {
        Square posP1 = gameBoard.positionPlayer1;
        Square posP2 = gameBoard.positionPlayer2;
        Square posOtherPlayer = posP1.equals(this)?posP2:(posP2.equals(this)?posP1:null);
        neighbours = new LinkedList<>();
        for (Square accessibleSquare : accessibles) {
            if(posOtherPlayer != null && (accessibleSquare.equals(posP2) || accessibleSquare.equals(posP1))) {
                Square above = gameBoard.getSquareFromCoord(2*posOtherPlayer.getX()-x, 2*posOtherPlayer.getY()-y);
                if(posOtherPlayer.getAccessibles().contains(above)){
                    neighbours.add(above);
                }else{
                    for (Square s : posOtherPlayer.getAccessibles()) {
                        if(!s.equals(this))
                            neighbours.add(s);
                    }
                }
            }else{
                neighbours.add(accessibleSquare);
            }
        }
    }

    public boolean isAccessible(Square squareToGo) {
        return neighbours.contains(squareToGo);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
