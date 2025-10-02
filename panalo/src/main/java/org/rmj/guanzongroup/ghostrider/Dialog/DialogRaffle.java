package org.rmj.guanzongroup.ghostrider.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import org.rmj.guanzongroup.ghostrider.R;

public class DialogRaffle {
    private Context context;
    private final AlertDialog poDialog;
    private ShapeableImageView icon_spin;
    private ShapeableImageView panalo_title;
    private ShapeableImageView panalo_megaphone;
    private ShapeableImageView panalo_megaphoneleft;
    private ShapeableImageView panalo_confetti;
    private MaterialTextView raffle_status;

    public DialogRaffle(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_raffle, null);

        AlertDialog.Builder poBuilder = new AlertDialog.Builder(context);
        poBuilder.setCancelable(false)
                .setView(view);

        this.context = context;

        poDialog = poBuilder.create();
        poDialog.setCancelable(true);

        icon_spin = view.findViewById(R.id.icon_spin);
        panalo_title = view.findViewById(R.id.panalo_title);
        panalo_megaphone = view.findViewById(R.id.panalo_megaphone);
        panalo_megaphoneleft = view.findViewById(R.id.panalo_megaphoneleft);
        panalo_confetti = view.findViewById(R.id.panalo_confetti);
        raffle_status = view.findViewById(R.id.raffle_status);

        icon_spin.setAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_intent_rotate));
        panalo_title.setAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_bounce));
        panalo_megaphone.setAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fade));
        panalo_megaphoneleft.setAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fade));
        raffle_status.setAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fade));
    }

    public void stopRaffle(String rafflestat){
        icon_spin.getAnimation().setRepeatCount(1);

        panalo_title.clearAnimation();
        panalo_megaphone.clearAnimation();
        panalo_megaphoneleft.clearAnimation();

        raffle_status.setText(rafflestat);
    }

    public void showWinnner(){
        stopRaffle("Congratulations!! You won the raffle");

        panalo_confetti.setVisibility(View.VISIBLE);
        panalo_confetti.setAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fade));
    }

    public void show() {
        if(!poDialog.isShowing()){
            poDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            poDialog.getWindow().getAttributes().windowAnimations = org.rmj.g3appdriver.R.style.PopupAnimation;
            poDialog.show();
        }
    }
}
