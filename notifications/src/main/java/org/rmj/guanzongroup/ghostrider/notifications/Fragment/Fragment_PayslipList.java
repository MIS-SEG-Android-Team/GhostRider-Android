package org.rmj.guanzongroup.ghostrider.notifications.Fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DPayslip;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.notifications.Adapter.AdapterPayslip;
import org.rmj.guanzongroup.ghostrider.notifications.R;
import org.rmj.guanzongroup.ghostrider.notifications.ViewModel.VMPaySlipList;

import java.util.List;

public class Fragment_PayslipList extends Fragment {

    private VMPaySlipList mViewModel;

    private LoadDialog poLoad;
    private MessageBox poDialog;

    private LinearLayout lnEmpty;
    private RecyclerView recyclerView;

    public static Fragment_PayslipList newInstance() {
        return new Fragment_PayslipList();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VMPaySlipList.class);
        poLoad = new LoadDialog(requireActivity());
        poDialog = new MessageBox(requireActivity());
        View view = inflater.inflate(R.layout.fragment_payslip_list, container, false);

        lnEmpty = view.findViewById(R.id.ln_empty);
        recyclerView = view.findViewById(R.id.recyclerview);
        mViewModel.GetPaySlipList().observe(getViewLifecycleOwner(), new Observer<List<DPayslip.Payslip>>() {
            @Override
            public void onChanged(List<DPayslip.Payslip> payslips) {
                try{
                    if(payslips.size() == 0){
                        lnEmpty.setVisibility(View.VISIBLE);
                        return;
                    }

                    lnEmpty.setVisibility(View.GONE);
                    AdapterPayslip loAdapter = new AdapterPayslip(payslips, new AdapterPayslip.OnDownloadPayslipListener() {
                        @Override
                        public void DownloadPayslip(String messageID, String link) {

                            mViewModel.SendReadResponse(messageID);

                            mViewModel.DownloadPaySlip(link, new VMPaySlipList.OnDownloadPayslipListener() {
                                @Override
                                public void OnDownload() {
                                    poLoad.initDialog("Payslip", "Downloading payslip. Please wait...", false);
                                    poLoad.show();
                                }

                                @Override
                                public void OnSuccess(Uri payslip) {
                                    poLoad.dismiss();
                                    Intent loIntent = new Intent(Intent.ACTION_VIEW);
                                    loIntent.setDataAndTypeAndNormalize(payslip, "application/pdf");
                                    loIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    requireActivity().startActivity(loIntent);
                                }

                                @Override
                                public void OnFailed(String message) {
                                    poLoad.dismiss();
                                    poDialog.initDialog();
                                    poDialog.setIcon(R.drawable.baseline_error_24);
                                    poDialog.setTitle("Payslip");
                                    poDialog.setMessage(message);
                                    poDialog.setPositiveButton("Okay", (view1, dialog) -> dialog.dismiss());
                                    poDialog.show();
                                }
                            });
                        }
                    });

                    LinearLayoutManager loManager = new LinearLayoutManager(requireActivity());
                    loManager.setOrientation(RecyclerView.VERTICAL);
                    loManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(loManager);
                    recyclerView.setAdapter(loAdapter);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        return view;
    }
}