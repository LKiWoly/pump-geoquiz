package com.bignerdranch.android.geoquiz;

import java.io.Serializable;

public class Question implements Serializable {
    private int mTextResId;
    private boolean mAnswerTrue;
    private boolean mUserInput = false;
    private boolean mIsCheat = false;

    public Question(int textResId, boolean answerTrue) {
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }

    public void setAlreadyDone() {
        mUserInput = true;
    }

    public boolean isAlreadyDone() {
        return mUserInput;
    }

    public void cheatQuestion() {
        mIsCheat = true;
    }

    public boolean isCheat() {
        return mIsCheat;
    }
}
