package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";

    private int mCurrentIndex = 0;
    private int mScore = 0;
    private int mNumAnswers = 0;
    private int cheatsLeft = CHEAT_NUMBER;


    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;

    private ImageButton mNextButton;
    private ImageButton mPrevButton;

    private TextView mQuestionTextView;
    private TextView mCheatNumbersView;


    private static final String KEY_INDEX = "index";
    private static final String USER_INPUT_INDEX = "input";
    private static final String SCORE_INDEX = "score";
    private static final String NUMBER_ANSWERS_INDEX = "answers_number";
    private static final String CHEAT_NUMBERS_INDEX = "number_cheat";

    private static final int REQUEST_CODE_CHEAT = 0;
    private static final int CHEAT_NUMBER = 3;


    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };
    //test

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);


        if (savedInstanceState != null) {
            restorePreviousState(savedInstanceState);
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mQuestionBank = (Question[]) savedInstanceState.getSerializable(USER_INPUT_INDEX);
            mScore = savedInstanceState.getInt(SCORE_INDEX, 0);
            mNumAnswers = savedInstanceState.getInt(NUMBER_ANSWERS_INDEX, 0);
            mCheatNumbers = savedInstanceState.getInt(CHEAT_NUMBERS_INDEX, 3);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.previous_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex -= 1;
                if (mCurrentIndex < 0) {
                    mCurrentIndex = mQuestionBank.length + mCurrentIndex;
                }
                updateQuestion();
            }
        });


        mCheatButton = (Button) findViewById(R.id.cheat_button);

        mCheatNumbersView = (TextView) findViewById(R.id.cheat_numbers_view);
        setTextCheatNumberView();
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheatNumbers--;
                setTextCheatNumberView();
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);

            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });
        updateQuestion();
    }

    private void setTextCheatNumberView() {
        mCheatNumbersView.setText("You have " + mCheatNumbers + " helps.");
        if (mCheatNumbers == 0) {
            mCheatButton.setEnabled(false);
        }
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        mTrueButton.setEnabled(!mQuestionBank[mCurrentIndex].isAlreadyDone());
        mFalseButton.setEnabled(!mQuestionBank[mCurrentIndex].isAlreadyDone());
    }

    private void checkAnswer(boolean userPressedTrue) {
        mNumAnswers++;
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (mQuestionBank[mCurrentIndex].isCheat()) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                mScore++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        mQuestionBank[mCurrentIndex].setAlreadyDone();
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
        if (mNumAnswers == mQuestionBank.length) {
            Toast.makeText(this, "Your score: " + mScore + "/" + mNumAnswers,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            if (CheatActivity.wasAnswerShown(data)) {
                mQuestionBank[mCurrentIndex].cheatQuestion();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putSerializable(USER_INPUT_INDEX, mQuestionBank);
        savedInstanceState.putInt(SCORE_INDEX, mScore);
        savedInstanceState.putInt(NUMBER_ANSWERS_INDEX, mNumAnswers);
        savedInstanceState.putInt(CHEAT_NUMBERS_INDEX, mCheatNumbers);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
