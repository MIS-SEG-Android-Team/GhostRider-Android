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
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.guanzongroup.ghostrider.R;

import java.util.Random;

public class DialogRaffle {
    private final AlertDialog poDialog;
    private ShapeableImageView icon_spin;
    private ShapeableImageView panalo_title;

    public DialogRaffle(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_raffle, null);

        AlertDialog.Builder poBuilder = new AlertDialog.Builder(context);
        poBuilder.setCancelable(false)
                .setView(view);

        poDialog = poBuilder.create();
        poDialog.setCancelable(true);

        icon_spin = view.findViewById(R.id.icon_spin);
        panalo_title = view.findViewById(R.id.panalo_title);

        icon_spin.setAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_intent_rotate));
        panalo_title.setAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_bounce));
    }

    public void show() {
        if(!poDialog.isShowing()){
            poDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            poDialog.getWindow().getAttributes().windowAnimations = org.rmj.g3appdriver.R.style.PopupAnimation;
            poDialog.show();
        }
    }
}
