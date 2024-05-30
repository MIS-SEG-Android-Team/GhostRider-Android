package org.rmj.guanzongroup.ghostrider.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import org.rmj.guanzongroup.ghostrider.R;

public class DialogRaffle {
    public DialogRaffle(Context context){
        AlertDialog.Builder poBuilder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_raffle, null);
    }
}
