package com.bignerdranch.android.geoquiz;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import static com.bignerdranch.android.geoquiz.Constants.*;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";

    private int currentIndex = 0;
    private int score = 0;
    private int numAnswers = 0;
    private int cheatsLeft = CHEAT_NUMBER;

    private Button trueButton;
    private Button falseButton;
    private Button cheatButton;

    private ImageButton nextButton;
    private ImageButton prevButton;

    private TextView questionTextView;
    private TextView cheatNumbersView;

    private Question[] questionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, currentIndex);
        savedInstanceState.putSerializable(USER_INPUT_INDEX, questionBank);
        savedInstanceState.putInt(SCORE_INDEX, score);
        savedInstanceState.putInt(NUMBER_ANSWERS_INDEX, numAnswers);
        savedInstanceState.putInt(CHEAT_NUMBERS_INDEX, cheatsLeft);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            restorePreviousState(savedInstanceState);
        }

        initViews();

        setOnClickListeners();

        setTextCheatNumberView();
    }

    private void restorePreviousState(Bundle savedInstanceState) {
        currentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        questionBank = (Question[]) savedInstanceState.getSerializable(USER_INPUT_INDEX);
        score = savedInstanceState.getInt(SCORE_INDEX, 0);
        numAnswers = savedInstanceState.getInt(NUMBER_ANSWERS_INDEX, 0);
        cheatsLeft = savedInstanceState.getInt(CHEAT_NUMBERS_INDEX, CHEAT_NUMBER);
    }

    private void initViews() {
        questionTextView = findViewById(R.id.question_text_view);
        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.previous_button);
        cheatButton = findViewById(R.id.cheat_button);
        cheatNumbersView = findViewById(R.id.cheat_numbers_view);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
    }

    private void setOnClickListeners() {
        nextButton.setOnClickListener(v -> {
            currentIndex += 1;
            updateQuestion();
        });

        prevButton.setOnClickListener(v -> {
            currentIndex -= 1;
            updateQuestion();
        });

        cheatButton.setOnClickListener(v -> {
            setTextCheatNumberView();
            AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
            builder.setTitle(R.string.warning_text)
                    .setPositiveButton(R.string.show_answer_button, (dialog, which) -> {
                        questionBank[currentIndex].cheatQuestion();
                        cheatsLeft--;
                        setAnswersButtonsColor();
                        setTextCheatNumberView();
                        cheatButton.setEnabled(false);
                    })
                    .setNegativeButton(R.string.cancel_button, (dialog, which) -> {

                    });
            builder.show();
        });

        trueButton.setOnClickListener(v -> {
            checkAnswer(true);
            currentIndex = (currentIndex + 1) % questionBank.length;
            if (currentIndex != questionBank.length - 1)
                updateQuestion();
        });

        falseButton.setOnClickListener(v -> {
            checkAnswer(false);
            currentIndex = (currentIndex + 1) % questionBank.length;
            if (currentIndex != questionBank.length - 1)
                updateQuestion();
        });

        updateQuestion();
    }

    private void setTextCheatNumberView() {
        cheatNumbersView.setText("You have " + cheatsLeft + " helps.");
        if (cheatsLeft == 0) {
            cheatButton.setEnabled(false);
        }
    }

    private void updateQuestion() {
        int question = questionBank[currentIndex].getTextResId();
        questionTextView.setText(question);

        trueButton.setEnabled(!questionBank[currentIndex].isAlreadyDone());
        falseButton.setEnabled(!questionBank[currentIndex].isAlreadyDone());

        setAnswersButtonsColor();

        cheatButton.setEnabled(true);
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);

        if (questionBank[currentIndex].isAlreadyDone()
                || questionBank[currentIndex].isCheat()
                || cheatsLeft == 0) {
            cheatButton.setEnabled(false);
        }

        if (currentIndex == 0) {
            prevButton.setEnabled(false);
        }
        if (currentIndex == questionBank.length - 1) {
            nextButton.setEnabled(false);
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        numAnswers++;
        boolean answerIsTrue = questionBank[currentIndex].isAnswerTrue();

        int messageResId;

        if (questionBank[currentIndex].isCheat()) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                score++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        questionBank[currentIndex].setAlreadyDone();

        if (numAnswers == questionBank.length) {
            createResultScreen();
        }
    }

    private void createResultScreen() {
        AlertDialog.Builder ad = new AlertDialog.Builder(QuizActivity.this);
        ad.setTitle("Finish!");
        ad.setMessage("Your score: " + score);
        ad.setPositiveButton(R.string.start_game_again_button, (dialog, which) -> {
            Intent intent = new Intent(QuizActivity.this, QuizActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            QuizActivity.this.startActivity(intent);
            Runtime.getRuntime().exit(0);
        });
        ad.setNegativeButton(R.string.finish_game_button, (dialog, which) -> {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        });
        ad.show();
    }

    private void setAnswersButtonsColor() {
        if (questionBank[currentIndex].isCheat()) {
            if (questionBank[currentIndex].isAnswerTrue()) {
                trueButton.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                falseButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            } else {
                trueButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                falseButton.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
            }
        } else {
            trueButton.getBackground().clearColorFilter();
            falseButton.getBackground().clearColorFilter();
        }
    }
}
