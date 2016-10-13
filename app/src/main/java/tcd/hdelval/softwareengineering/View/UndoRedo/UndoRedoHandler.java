package tcd.hdelval.softwareengineering.View.UndoRedo;

import java.util.LinkedList;

import tcd.hdelval.softwareengineering.View.Game;
import tcd.hdelval.softwareengineering.controller.Controller;

/**
 * Created by hdelval on 11/10/16.
 */

public class UndoRedoHandler {
    private LinkedList<UndoRedoBarriers> stackUndoPrevious = new LinkedList<>();
    private LinkedList<UndoRedoBarriers> stackUndo = new LinkedList<>();
    private boolean undoAutoOnAdd = false;

    public void undo(){
        if(!stackUndo.isEmpty()) {
            UndoRedoBarriers action = stackUndo.removeLast();
            action.undo();
        }
    }

    public void todo(UndoRedoBarriers action, Controller gameController){
        if(action.isIn(stackUndoPrevious) || !gameController.canAddBarrier(action))
            return;
        if(undoAutoOnAdd)
            undo();
        stackUndo.addLast(action);
        action.todo();
    }

    public void setUndoAutoOnAdd(boolean undoAutoOnAdd) {
        this.undoAutoOnAdd = undoAutoOnAdd;
    }

    public void played(Controller gameController) {
        if(!stackUndo.isEmpty()){
            stackUndoPrevious.addAll(stackUndo);
            gameController.addBarriersFromStackUndoRedo(stackUndo);
            stackUndo.clear();
            gameController.toggleUser(Game.BARRIER_PLAYED);
        }
    }
}
