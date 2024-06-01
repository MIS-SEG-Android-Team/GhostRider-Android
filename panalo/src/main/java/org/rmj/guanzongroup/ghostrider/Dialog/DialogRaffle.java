package org.rmj.guanzongroup.ghostrider.Dialog;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.guanzongroup.ghostrider.R;

import java.util.Random;

public class DialogRaffle {
    private final AlertDialog poDialog;
    private MaterialCardView layout_item1;
    private MaterialCardView layout_item2;
    private MaterialCardView layout_item3;
    private MaterialCardView layout_item4;
    private MaterialCardView layout_item5;
    private MaterialTextView txt_item1;
    private MaterialTextView txt_item2;
    private MaterialTextView txt_item3;
    private MaterialTextView txt_item4;
    private MaterialTextView txt_item5;
    private MaterialButton btn_startdraw;
    private MaterialButton btn_enddraw;
    private ValueAnimator valueAnimator;

    public DialogRaffle(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_raffle, null);

        AlertDialog.Builder poBuilder = new AlertDialog.Builder(context);
        poBuilder.setCancelable(false)
                .setView(view);

        poDialog = poBuilder.create();
        poDialog.setCancelable(true);

        layout_item1 = view.findViewById(R.id.layout_item1);
        layout_item2 = view.findViewById(R.id.layout_item2);
        layout_item3 = view.findViewById(R.id.layout_item3);
        layout_item4 = view.findViewById(R.id.layout_item4);
        layout_item5 = view.findViewById(R.id.layout_item5);

        txt_item1 = view.findViewById(R.id.txt_item1);
        txt_item2 = view.findViewById(R.id.txt_item2);
        txt_item3 = view.findViewById(R.id.txt_item3);
        txt_item4 = view.findViewById(R.id.txt_item4);
        txt_item5 = view.findViewById(R.id.txt_item5);

        btn_startdraw = view.findViewById(R.id.btn_startdraw);
        btn_enddraw = view.findViewById(R.id.btn_enddraw);

        valueAnimator = new ValueAnimator();

        btn_startdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout_item1.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_intent_slide_down));
                layout_item2.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_intent_slide_down));
                layout_item3.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_intent_slide_down));
                layout_item4.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_intent_slide_down));
                layout_item5.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_intent_slide_down));

                AnimateNumbers(txt_item1);
                AnimateNumbers(txt_item2);
                AnimateNumbers(txt_item3);
                AnimateNumbers(txt_item4);
                AnimateNumbers(txt_item5);
            }
        });

        btn_enddraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_item1.clearAnimation();
                layout_item2.clearAnimation();
                layout_item3.clearAnimation();
                layout_item4.clearAnimation();
                layout_item5.clearAnimation();

                valueAnimator.end();
            }
        });
    }

    private void AnimateNumbers(MaterialTextView item){
        valueAnimator.setIntValues(1, 2, 3, 4, 5, 6, 7, 8, 9);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                animation.setRepeatCount(Animation.INFINITE);

                item.setText(animation.getAnimatedValue().toString());
            }
        });
        valueAnimator.start();
    }

    public void show() {
        if(!poDialog.isShowing()){
            poDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            poDialog.getWindow().getAttributes().windowAnimations = org.rmj.g3appdriver.R.style.PopupAnimation;
            poDialog.show();
        }
    }
}
