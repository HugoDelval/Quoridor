package tcd.hdelval.softwareengineering.View;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import tcd.hdelval.softwareengineering.controller.Controller;
import tcd.hdelval.softwareengineering.Modele.Gameboard;
import tcd.hdelval.softwareengineering.Modele.Square;
import tcd.hdelval.softwareengineering.R;
import tcd.hdelval.softwareengineering.Utils.CustomViews;
import tcd.hdelval.softwareengineering.Utils.Listeners;
import tcd.hdelval.softwareengineering.View.UndoRedo.UndoRedoHandler;

public class Game extends AppCompatActivity {

    public static final int MOVE_PAWN = 2;
    public static final int BARRIER_PLAYED = 1;
    private ArrayList<FrameLayout> squares = new ArrayList<>();
    private ArrayList<FrameLayout> barriers = new ArrayList<>();
    private ArrayList<FrameLayout> interBarriers = new ArrayList<>();
    public static int OFFSET_SQUARE = 1000;
    public static int OFFSET_VERTICAL_BARRIER = 2000;
    public static int OFFSET_HORIZONTAL_BARRIER = 3000;
    public static int OFFSET_INTER_BARRIER = 4000;
    private int squareSize = 0;
    private GridLayout board;
    private int widthBarrier = 28;
    private ImageView firstImagePawn;
    private ImageView secondImagePawn;
    private FrameLayout firstPawnSquare;
    private FrameLayout secondPawnSquare;
    private LinearLayout boardLayoutWrapper;
    private boolean firstImagePawnSelected = false;
    private boolean secondImagePawnSelected = false;
    private Controller gameController;
    private ImageView barriersP1;
    private ImageView barriersP2;
    private CustomViews.ImageDragShadowBuilder shadowBuilder;

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
        if(idPawn==2 && y==0){
            new AlertDialog.Builder(this)
                    .setTitle("Bottom player won!")
                    .setMessage("Bottom player won ;) well played ! Quit ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            firstImagePawn.setOnClickListener(null);
                            secondImagePawn.setOnClickListener(null);
                            barriersP1.setOnTouchListener(null);
                            barriersP2.setOnTouchListener(null);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
        if(idPawn==1 && y==8){
            new AlertDialog.Builder(this)
                    .setTitle("Top player won!")
                    .setMessage("Top player won ;) well played ! Quit ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            firstImagePawn.setOnClickListener(null);
                            secondImagePawn.setOnClickListener(null);
                            barriersP1.setOnTouchListener(null);
                            barriersP2.setOnTouchListener(null);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
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
                                        newObject.setId(OFFSET_SQUARE + (line/2)*9+(col/2));
                                        newObject.setBackgroundResource(R.drawable.dark_wood);

                                        squares.add(newObject);

                                    }else{
                                        // verticals barriers
                                        params.height = squareSize;
                                        params.width = widthBarrier;
                                        newObject.setId(OFFSET_VERTICAL_BARRIER + (line/2)*8+(col/2));
                                        barriers.add(newObject);
                                        barriers.add(newObject);
                                    }
                                }else{
                                    // horizontals barriers + inter-barriers
                                    if(col%2 == 0) {
                                        // horizontals barriers
                                        params.height = widthBarrier;
                                        params.width = squareSize;
                                        newObject.setId(OFFSET_HORIZONTAL_BARRIER + (line/2)*9+(col/2));

                                        barriers.add(newObject);
                                    }else{
                                        // inter-barriers
                                        params.height = widthBarrier;
                                        params.width = widthBarrier;
                                        newObject.setId(OFFSET_INTER_BARRIER + (line/2)*8+(col/2));
                                        newObject.setLayoutParams(params);

                                        interBarriers.add(newObject);
                                    }
                                }
                                newObject.setLayoutParams(params);
                                board.addView(newObject);
                            }
                        }

                        firstImagePawn = (ImageView) findViewById(R.id.pion1);
                        secondImagePawn = (ImageView) findViewById(R.id.pion2);
                        boardLayoutWrapper = (LinearLayout) findViewById(R.id.board_layout_wrapper);

                        barriersP1 = (ImageView) findViewById(R.id.barriers_player1);
                        ViewGroup.LayoutParams barriersP1LayoutParams = barriersP1.getLayoutParams();
                        barriersP1LayoutParams.height = squareSize*2 + widthBarrier;
                        barriersP1LayoutParams.width = widthBarrier;
                        barriersP1.setLayoutParams(barriersP1LayoutParams);
                        shadowBuilder = (CustomViews.ImageDragShadowBuilder) CustomViews.ImageDragShadowBuilder.fromResource(
                                board.getContext(), barriersP1.getBackground(), widthBarrier, squareSize*2 + widthBarrier
                        );
                        barriersP1.setFocusable(false);

                        barriersP2 = (ImageView) findViewById(R.id.barriers_player2);
                        barriersP2.setLayoutParams(barriersP1LayoutParams);
                        barriersP2.setOnTouchListener(
                                new Listeners.BarrierTouchListener(
                                        barriers, interBarriers, board, widthBarrier, squareSize, shadowBuilder
                                )
                        );
                        barriersP2.setFocusable(true);

                        UndoRedoHandler undoRedoHandler = new UndoRedoHandler();
                        for (FrameLayout square : squares) {
                            square.setOnDragListener(new Listeners.BarrierDragListener(square, board, undoRedoHandler, gameController));
                        }
                        for (FrameLayout interBarrier : interBarriers) {
                            interBarrier.setOnDragListener(new Listeners.BarrierDragListener(interBarrier, board, undoRedoHandler, gameController));
                        }
                        for (FrameLayout barrier : barriers) {
                            barrier.setOnDragListener(new Listeners.BarrierDragListener(barrier, board, undoRedoHandler, gameController));
                        }

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
        toggleUser(idPawn, MOVE_PAWN);
        if(idPawn == 1)
            firstPawnSquare = to;
        else
            secondPawnSquare = to;
    }

    private void unSelectPawn(int idPawn){
        LinkedList<Square> neighbours = idPawn == 1 ? gameController.getNeighboursP1() : gameController.getNeighboursP2();
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
                for (Square s : gameController.getNeighboursP1()) {
                    FrameLayout f = squares.get(s.getIdSquare());
                    f.setPressed(true);
                    Listeners.MovePawnListener movePawnListener = new Listeners.MovePawnListener(firstPawnSquare, f, this, idPawn);
                    f.setOnClickListener(movePawnListener);
                }
            } else {
                // unhightlight neighbours
                for (Square s : gameController.getNeighboursP1()) {
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
                for (Square s : gameController.getNeighboursP2()) {
                    FrameLayout f = squares.get(s.getIdSquare());
                    f.setPressed(true);
                    Listeners.MovePawnListener movePawnListener = new Listeners.MovePawnListener(secondPawnSquare, f, this, idPawn);
                    f.setOnClickListener(movePawnListener);
                }
            } else {
                // unhightlight neighbours
                for (Square s : gameController.getNeighboursP2()) {
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

    public void toggleUser(int idPawn, int action) {
        if(idPawn == 1) {
            if(action == BARRIER_PLAYED) {
                TextView nbBarrierText = (TextView) findViewById(R.id.nb_barriers_p1);
                nbBarrierText.setText("x" + gameController.getNbBarriersP1());
            }
            firstImagePawn.setOnClickListener(null);
            secondImagePawn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pawnOnClick(2);
                }
            });
            if(gameController.player2HasEnoughBarriers()){
                barriersP2.setFocusable(true);
                barriersP2.setOnTouchListener(
                        new Listeners.BarrierTouchListener(
                                barriers, interBarriers, board, widthBarrier, squareSize, shadowBuilder
                        )
                );
            }
            barriersP1.setFocusable(false);
            barriersP1.setOnTouchListener(null);
        }else {
            if(action == BARRIER_PLAYED) {
                TextView nbBarrierText = (TextView) findViewById(R.id.nb_barriers_p2);
                nbBarrierText.setText("x" + gameController.getNbBarriersP2());
            }
            secondImagePawn.setOnClickListener(null);
            firstImagePawn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pawnOnClick(1);
                }
            });
            if(gameController.player1HasEnoughBarriers()){
                barriersP1.setFocusable(true);
                barriersP1.setOnTouchListener(
                        new Listeners.BarrierTouchListener(
                                barriers, interBarriers, board, widthBarrier, squareSize, shadowBuilder
                        )
                );
            }
            barriersP2.setFocusable(false);
            barriersP2.setOnTouchListener(null);
        }
    }

    public int getCurrentPawnId() {
        return barriersP1.isFocusable()?1:2;
    }
}
