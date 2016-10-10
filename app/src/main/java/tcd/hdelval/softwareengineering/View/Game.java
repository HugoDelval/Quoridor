package tcd.hdelval.softwareengineering.View;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.LinkedList;
import tcd.hdelval.softwareengineering.Controller.Controller;
import tcd.hdelval.softwareengineering.Modele.Gameboard;
import tcd.hdelval.softwareengineering.Modele.Square;
import tcd.hdelval.softwareengineering.R;
import tcd.hdelval.softwareengineering.Utils.Listeners;

public class Game extends AppCompatActivity {

    private ArrayList<FrameLayout> squares = new ArrayList<>();
    private ArrayList<FrameLayout> barriers = new ArrayList<>();
    private ArrayList<FrameLayout> interBarriers = new ArrayList<>();
    private static int OFFSET_SQUARE = 1000;
    private static int OFFSET_BARRIER = 2000;
    private static int OFFSET_INTER_BARRIER = 3000;
    private int squareSize = 0;
    private GridLayout board;
    private int widthBarrier = 22;
    private ImageView firstImagePawn;
    private ImageView secondImagePawn;
    private FrameLayout firstPawnSquare;
    private FrameLayout secondPawnSquare;
    private LinearLayout boardLayoutWrapper;
    private boolean firstPlayerToPlay = true;
    private boolean firstImagePawnSelected = false;
    private boolean secondImagePawnSelected = false;
    private Controller gameController;

    /**
     * Get the square at the coordinate (x,y)
     * (0,0) is top left
     * @param x The horizontal coordinate
     * @param y The vertical coordinate
     * @return the square at the position (x, y)
     */
    private FrameLayout getSquare(int x, int y){
        int targetPosition = y*9+x;
        return squares.get(targetPosition);
    }

    /**
     * Move the pawn with the ID `idPawn` to the coordinate x, y
     * (0,0) is top:left
     * Pawn `1` is the purple pawn, Pawn `2` is the beige one
     * @param x The horizontal coordinate
     * @param y The vertical coordinate
     * @param idPawn the pawn identifier (1 or 2)
     */
    private void movePawn(int x, int y, int idPawn){
        FrameLayout pawnTargetPosition = getSquare(x, y);
        int androidPawnId = idPawn==1? R.id.pion1:R.id.pion2;
        ImageView imagePawn = (ImageView) findViewById(androidPawnId);
        imagePawn.setX(pawnTargetPosition.getX() + boardLayoutWrapper.getX() + board.getX());
        imagePawn.setY(pawnTargetPosition.getY()+ boardLayoutWrapper.getY() + board.getY());
    }

    /**
     * Generate the game board ex: the squares + the barriers + the images of the pawns
     * @param boardId the Android ID representing the board to fill up. ex: R.id.board
     */
    private void generateGameBoard(final int boardId){
        board = (GridLayout) findViewById(boardId);
        board.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            board.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }else{
                            board.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        int boardWidth = board.getWidth();
                        int boardHeight = board.getHeight();
                        int paddingLR=0, paddingBU=0;
                        if(boardWidth > boardHeight) {
                            squareSize = (boardHeight - widthBarrier * 8)/9;
                            paddingLR = (boardWidth - (squareSize * 9 + widthBarrier * 8))/2;
                        }else{
                            squareSize = (boardWidth - widthBarrier * 8)/9 ;
                            paddingBU = (boardHeight - (squareSize * 9 + widthBarrier * 8))/2;
                        }
                        board.setPadding(paddingLR,paddingBU,paddingLR,paddingBU);

                        for (int line = 0; line < 17; line++) {
                            for (int col = 0; col < 17; col++) {
                                FrameLayout newObject = new FrameLayout(board.getContext());
                                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                                params.columnSpec = GridLayout.spec(col);
                                params.rowSpec = GridLayout.spec(line);
                                params.setGravity(Gravity.CENTER);

                                if(line%2 == 0) {
                                    // squares + verticals barriers
                                    if(col%2 == 0) {
                                        // squares
                                        params.height = squareSize;
                                        params.width = squareSize;
                                        newObject.setId(OFFSET_SQUARE + line*9+col);
                                        newObject.setBackgroundResource(R.drawable.dark_wood);

                                        squares.add(newObject);

                                    }else{
                                        // verticals barriers
                                        params.height = squareSize;
                                        params.width = widthBarrier;
                                        newObject.setId(OFFSET_BARRIER + line*9+col);

                                        barriers.add(newObject);
                                    }
                                }else{
                                    // horizontals barriers + inter-barriers
                                    if(col%2 == 0) {
                                        // horizontals barriers
                                        params.height = widthBarrier;
                                        params.width = squareSize;
                                        newObject.setId(OFFSET_BARRIER + line*9+col);

                                        barriers.add(newObject);
                                    }else{
                                        // inter-barriers
                                        params.height = widthBarrier;
                                        params.width = widthBarrier;
                                        newObject.setId(OFFSET_INTER_BARRIER + line*9+col);
                                        newObject.setLayoutParams(params);

                                        interBarriers.add(newObject);
                                    }
                                }
                                newObject.setLayoutParams(params);
                                board.addView(newObject);
                            }
                        }
                        barriers.get(0).setBackgroundResource(R.drawable.light_wood);
                        barriers.get(17).setBackgroundResource(R.drawable.light_wood);
                        interBarriers.get(0).setBackgroundResource(R.drawable.light_wood);

                        firstImagePawn = (ImageView) findViewById(R.id.pion1);
                        secondImagePawn = (ImageView) findViewById(R.id.pion2);
                        boardLayoutWrapper = (LinearLayout) findViewById(R.id.board_layout_wrapper);

                        ImageView barriersP1 = (ImageView) findViewById(R.id.barriers_player1);
                        ViewGroup.LayoutParams barriersP1LayoutParams = barriersP1.getLayoutParams();
                        barriersP1LayoutParams.height = squareSize*2 + widthBarrier;
                        barriersP1LayoutParams.width = widthBarrier;
                        barriersP1.setLayoutParams(barriersP1LayoutParams);
                        barriersP1.setOnTouchListener(
                                new Listeners.BarrierTouchListener(barriers, interBarriers, board, widthBarrier, squareSize)
                        );
                        barriers.get(1).setOnDragListener(new Listeners.BarrierDragListener(barriersP1));

                        secondImagePawn.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                pawnOnClick(2);
                            }
                        });

                        firstPawnSquare = getSquare(4, 0);
                        firstImagePawn.getLayoutParams().width = squareSize;
                        firstImagePawn.getLayoutParams().height = squareSize;

                        firstPawnSquare.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                    firstPawnSquare.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                }else{
                                    firstPawnSquare.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                                ImageView firstImPawn = (ImageView) findViewById(R.id.pion1);
                                firstImPawn.setX(firstPawnSquare.getX() + boardLayoutWrapper.getX() + board.getX());
                                firstImPawn.setY(firstPawnSquare.getY()+ boardLayoutWrapper.getY() + board.getY());
                            }
                        });
                        firstImagePawn.bringToFront();

                        secondPawnSquare = getSquare(4, 8);
                        secondImagePawn.getLayoutParams().width = squareSize;
                        secondImagePawn.getLayoutParams().height = squareSize;

                        secondPawnSquare.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                    secondPawnSquare.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                }else{
                                    secondPawnSquare.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                                ImageView secondImPawn = (ImageView) findViewById(R.id.pion2);
                                secondImPawn.setX(secondPawnSquare.getX() + boardLayoutWrapper.getX() + board.getX());
                                secondImPawn.setY(secondPawnSquare.getY()+ boardLayoutWrapper.getY() + board.getY());
                            }
                        });
                        secondImagePawn.bringToFront();
                    }
                }
        );

    }

    public void movePawn(FrameLayout from, FrameLayout to, int idPawn){
        int x = squares.indexOf(to) % 9;
        int y = squares.indexOf(to) / 9;
        unSelectPawn(idPawn);
        movePawn(x, y, idPawn);
        gameController.movePawn(x, y, idPawn);
        firstImagePawnSelected = false;
        secondImagePawnSelected = false;
        if(idPawn == 1) {
            firstPawnSquare = to;
            firstImagePawn.setOnClickListener(null);
            secondImagePawn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pawnOnClick(2);
                }
            });
        }else {
            secondPawnSquare = to;
            secondImagePawn.setOnClickListener(null);
            firstImagePawn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pawnOnClick(1);
                }
            });
        }
    }

    private void unSelectPawn(int idPawn){
        LinkedList<Square> neighbours = idPawn == 1 ? Gameboard.THE_GAME_BOARD.positionPlayer1.getNeighbours() : Gameboard.THE_GAME_BOARD.positionPlayer2.getNeighbours();
        for(Square s:neighbours){
            FrameLayout f = squares.get(s.getIdSquare());
            f.setPressed(false);
            f.setOnClickListener(null);
        }
    }

    private void pawnOnClick(int idPawn) {
        if(idPawn == 1) {
            firstImagePawnSelected = !firstImagePawnSelected;
            if (firstImagePawnSelected) {
                // hightlight neighbours
                for (Square s : Gameboard.THE_GAME_BOARD.positionPlayer1.getNeighbours()) {
                    FrameLayout f = squares.get(s.getIdSquare());
                    f.setPressed(true);
                    Listeners.MovePawnListener movePawnListener = new Listeners.MovePawnListener(firstPawnSquare, f, this, idPawn);
                    f.setOnClickListener(movePawnListener);
                }
            } else {
                // unhightlight neighbours
                for (Square s : Gameboard.THE_GAME_BOARD.positionPlayer1.getNeighbours()) {
                    FrameLayout f = squares.get(s.getIdSquare());
                    f.setPressed(false);
                    f.setOnClickListener(null);
                }
            }
            secondImagePawn.bringToFront();
        }else {
            secondImagePawnSelected = !secondImagePawnSelected;
            if (secondImagePawnSelected) {
                // hightlight neighbours
                for (Square s : Gameboard.THE_GAME_BOARD.positionPlayer2.getNeighbours()) {
                    FrameLayout f = squares.get(s.getIdSquare());
                    f.setPressed(true);
                    Listeners.MovePawnListener movePawnListener = new Listeners.MovePawnListener(secondPawnSquare, f, this, idPawn);
                    f.setOnClickListener(movePawnListener);
                }
            } else {
                // unhightlight neighbours
                for (Square s : Gameboard.THE_GAME_BOARD.positionPlayer2.getNeighbours()) {
                    FrameLayout f = squares.get(s.getIdSquare());
                    f.setPressed(false);
                    f.setOnClickListener(null);
                }
            }
            secondImagePawn.bringToFront();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.gameController = new Controller(this);
        setContentView(R.layout.activity_game);
        generateGameBoard(R.id.board);
    }
}
