package com.bignerdranch.android.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import static com.bignerdranch.android.geoquiz.Constants.*;

public class CheatActivity extends AppCompatActivity {

    private static final String TAG = "CheatActivity";

    private boolean answerIsTrue;
    private boolean isAnswerShown;

    private TextView answerTextView;
    private TextView APIVersionView;

    private Button showAnswerButton;

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        saveInstanceState.putBoolean(EXTRA_ANSWER_SHOWN, isAnswerShown);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        if (savedInstanceState != null) {
            isAnswerShown = savedInstanceState.getBoolean(EXTRA_ANSWER_SHOWN, false);
        }

        answerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        initViews();

        setOnClickListeners();

        if (isAnswerShown) {
            setAnswerTextView();
            showAnswerButton.setVisibility(View.INVISIBLE);
        }

        setAnswerShownResult(isAnswerShown);
    }

    private void initViews() {
        answerTextView = findViewById(R.id.answer_text_view);
        APIVersionView = findViewById(R.id.api_version_view);
        showAnswerButton = findViewById(R.id.show_answer_button);
        APIVersionView.append("API level " + Build.VERSION.SDK_INT);
    }

    private void setOnClickListeners() {
        showAnswerButton.setOnClickListener((view) -> {
                    setAnswerTextView();
                    isAnswerShown = true;
                    setAnswerShownResult(isAnswerShown);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        int cx = showAnswerButton.getWidth() / 2;
                        int cy = showAnswerButton.getHeight() / 2;
                        float radius = showAnswerButton.getWidth();
                        Animator anim = ViewAnimationUtils.createCircularReveal(
                                showAnswerButton, cx, cy, radius, 0
                        );
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                showAnswerButton.setVisibility(View.INVISIBLE);
                            }
                        });
                        anim.start();
                    } else {
                        showAnswerButton.setVisibility(View.INVISIBLE);
                    }
                }
        );
    }

    private void setAnswerTextView() {
        if (answerIsTrue) {
            answerTextView.setText(R.string.true_button);
        } else {
            answerTextView.setText(R.string.false_button);
        }
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }

    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return intent;
    }
}
