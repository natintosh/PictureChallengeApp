package org.gdhote.gdhotecodegroup.pixcha.viewmodel;

import androidx.lifecycle.ViewModel;

public class MainActivityNavigationViewModel extends ViewModel {

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
