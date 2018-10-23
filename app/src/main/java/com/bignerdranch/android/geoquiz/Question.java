package com.bignerdranch.android.geoquiz;

import java.io.Serializable;

public class Question implements Serializable {
    private int textResId;

    private boolean answerTrue;
    private boolean userInput = false;
    private boolean isCheat = false;

    public Question(int textResId, boolean answerTrue) {
        this.textResId = textResId;
        this.answerTrue = answerTrue;
    }

    public int getTextResId() {
        return textResId;
    }

    public boolean isAnswerTrue() {
        return answerTrue;
    }

    public void setAlreadyDone() {
        userInput = true;
    }

    public boolean isAlreadyDone() {
        return userInput;
    }

    public void cheatQuestion() {
        isCheat = true;
    }

    public boolean isCheat() {
        return isCheat;
    }
}
