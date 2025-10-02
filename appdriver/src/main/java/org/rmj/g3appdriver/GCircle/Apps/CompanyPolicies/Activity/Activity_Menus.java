package org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Adapter.PolicyMenuAdapter;
import org.rmj.g3appdriver.GCircle.Apps.User_Guide.ViewModel.VMGuide;
import org.rmj.g3appdriver.R;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;

public class Activity_Menus extends AppCompatActivity {

    private PolicyMenuAdapter poAdapter;
    private RecyclerView rcv_menu;
    private VMGuide mViewmodel;
    private LoadDialog poLoad;
    private MessageBox poMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menus);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
        );

        mViewmodel = new ViewModelProvider(this).get(VMGuide.class);
        poLoad = new LoadDialog(this);
        poMessage = new MessageBox(this);
        rcv_menu = findViewById(R.id.rcv_menu);

        poMessage.initDialog();
        poMessage.setTitle("Guanzon Circle");
        poMessage.setPositiveButton("Okay", new MessageBox.DialogButton() {
            @Override
            public void OnButtonClick(View view, AlertDialog dialog) {
                dialog.dismiss();
            }
        });

        mViewmodel.DownloadPolicySummary(new VMGuide.OnDownloadGuides() {
            @Override
            public void OnDownloading() {
                poLoad.initDialog("Guanzon Circle", "Downloading menus . .", false);
                poLoad.show();
            }

            @Override
            public void OnSuccess() {
                poLoad.dismiss();

                mViewmodel.GetPolicyMenus().observe(Activity_Menus.this, ePolicyMenus -> {

                    if (ePolicyMenus != null) {

                        try {

                            if (ePolicyMenus.size() < 1){

                                poLoad.dismiss();

                                poMessage.setIcon(R.drawable.baseline_message_24);
                                poMessage.setMessage("No policy menus found");
                                poMessage.show();

                                return;
                            }

                            poAdapter = new PolicyMenuAdapter(ePolicyMenus, new PolicyMenuAdapter.OnViewGuide() {
                                @Override
                                public void OnView(String sMenuTitle, String sDescriptxx, String sMenuIDxx) {
                                    if (!sMenuIDxx.equals("003")){
                                        Intent intent = new Intent(Activity_Menus.this, Activity_Policy_Contents.class);
                                        intent.putExtra("sMenuTitle", sMenuTitle);
                                        intent.putExtra("sMenuIDxx", sMenuIDxx);
                                        startActivity(intent);
                                    }else {
                                        Intent intent = new Intent(Activity_Menus.this, Activity_Policy_Contents.class);
                                        intent.putExtra("sMenuTitle", sMenuTitle);
                                        intent.putExtra("sMenuDescription", sDescriptxx);
                                        intent.putExtra("sMenuIDxx", sMenuIDxx);
                                        startActivity(intent);
                                    }
                                }
                            });
                            rcv_menu.setAdapter(poAdapter);
                            rcv_menu.setLayoutManager(new GridLayoutManager(Activity_Menus.this, 2));

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else {
                        poLoad.dismiss();

                        poMessage.setIcon(R.drawable.baseline_message_24);
                        poMessage.setMessage("No policy menus found");
                        poMessage.show();
                    }
                });
            }

            @Override
            public void OnFailed(String message) {
                poLoad.dismiss();

                poMessage.setIcon(R.drawable.baseline_error_24);
                poMessage.setMessage(message);
            }
        });
    }
}