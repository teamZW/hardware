package com.hardware.sample.animation;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;

import com.hardware.sample.R;
import com.tc.animation.Evaluator.FallingBallEvaluator;
import com.tc.animation.FallingBallAnimation;

public class FallingBallAnimationActivity extends Activity {
    private FallingBallAnimation ball_img;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载MainView
        setContentView(R.layout.activity_fallingballanimation);

        ball_img = (FallingBallAnimation) findViewById(R.id.ball_img);

        findViewById(R.id.start_anim).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofObject(ball_img, "fallingPos", new FallingBallEvaluator(), new Point(0, 0), new Point(500, 500));

                //如果只给一个值的画，需要在view中添加get方法
//                ObjectAnimator animator = ObjectAnimator.ofObject(ball_img, "fallingPos", new FallingBallEvaluator(), new Point(500, 500));
                animator.setDuration(2000);
                animator.start();
            }
        });

    }

    public void onBack(View view){
        finish();
    }
}