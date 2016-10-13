package tcd.hdelval.softwareengineering.View.UndoRedo;

import android.widget.FrameLayout;

import java.util.LinkedList;

import tcd.hdelval.softwareengineering.R;

/**
 * Created by hdelval on 11/10/16.
 */

public class UndoRedoBarriers {

    private final LinkedList<FrameLayout> subjectsOfBackgroundChanges;

    public UndoRedoBarriers(LinkedList<FrameLayout> subjectsOfBackgroundChanges){
        this.subjectsOfBackgroundChanges = subjectsOfBackgroundChanges;
    }

    public void todo() {
        for (FrameLayout subjectOfBackgroundChange : subjectsOfBackgroundChanges) {
            subjectOfBackgroundChange.setBackgroundResource(R.drawable.light_wood);
        }
    }

    public void undo() {
        for (FrameLayout subjectOfBackgroundChange : subjectsOfBackgroundChanges) {
            subjectOfBackgroundChange.setBackgroundResource(0);
        }
    }

    public boolean isIn(LinkedList<UndoRedoBarriers> stackUndo) {
        for (UndoRedoBarriers undoRedoBarriers : stackUndo) {
            for (FrameLayout frameLayout : undoRedoBarriers.getSubjectsOfBackgroundChanges()) {
                for (FrameLayout subjectsOfBackgroundChange : subjectsOfBackgroundChanges) {
                    if(subjectsOfBackgroundChange.equals(frameLayout))
                        return true;
                }
            }
        }
        return false;
    }

    public LinkedList<FrameLayout> getSubjectsOfBackgroundChanges() {
        return subjectsOfBackgroundChanges;
    }
}
