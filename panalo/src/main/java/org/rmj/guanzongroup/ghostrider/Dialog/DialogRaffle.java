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
    private MaterialButton btn_startdraw;
    private MaterialButton btn_enddraw;

    public DialogRaffle(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_raffle, null);

        AlertDialog.Builder poBuilder = new AlertDialog.Builder(context);
        poBuilder.setCancelable(false)
                .setView(view);

        poDialog = poBuilder.create();
        poDialog.setCancelable(true);

        btn_startdraw = view.findViewById(R.id.btn_startdraw);
        btn_enddraw = view.findViewById(R.id.btn_enddraw);

        btn_startdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_enddraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void AnimateNumbers(MaterialTextView item){

    }

    public void show() {
        if(!poDialog.isShowing()){
            poDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            poDialog.getWindow().getAttributes().windowAnimations = org.rmj.g3appdriver.R.style.PopupAnimation;
            poDialog.show();
        }
    }
}
