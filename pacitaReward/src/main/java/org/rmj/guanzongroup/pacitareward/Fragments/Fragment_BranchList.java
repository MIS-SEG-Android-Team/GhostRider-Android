package org.rmj.guanzongroup.pacitareward.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.pacitareward.Adapter.RecyclerViewAdapter_BranchList;
import org.rmj.guanzongroup.pacitareward.Dialog.Dialog_SelectAction;
import org.rmj.guanzongroup.pacitareward.R;
import org.rmj.guanzongroup.pacitareward.ViewModel.VMBranchList;

import java.util.List;

public class Fragment_BranchList extends Fragment {
    private RecyclerView rvc_branchlist;
    private TextInputEditText searchview;
    private RecyclerViewAdapter_BranchList rec_branchList;
    private VMBranchList mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VMBranchList.class);
        View view= inflater.inflate(R.layout.fragment_layoutbranchlist, container, false);

        rvc_branchlist = view.findViewById(R.id.branch_list);
        searchview = view.findViewById(R.id.searchview);

        MessageBox loadDialog = new MessageBox(requireActivity());
        loadDialog.initDialog();
        loadDialog.setIcon(R.drawable.baseline_error_24);
        loadDialog.setTitle("No Records");
        loadDialog.setMessage("No Branch Records Found");
        loadDialog.setPositiveButton("Okay", new MessageBox.DialogButton() {
            @Override
            public void OnButtonClick(View view, AlertDialog dialog) {
                dialog.dismiss();
            }
        });

        mViewModel.importCriteria();
        mViewModel.getBranchlist().observe(requireActivity(), new Observer<List<EBranchInfo>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<EBranchInfo> eBranchInfos) {
                if (eBranchInfos.size() <= 0){
                    loadDialog.show();
                }

                rec_branchList = new RecyclerViewAdapter_BranchList(eBranchInfos, new RecyclerViewAdapter_BranchList.OnBranchSelectListener() {
                    @Override
                    public void OnSelect(String BranchCode, String BranchName) {
                        Dialog_SelectAction selectAction = new Dialog_SelectAction(new Dialog_SelectAction.onDialogSelect() {
                            @Override
                            public void onEvaluate(Class className, String branchCd, String branchNm, Dialog dialog) {
                                Intent intent = new Intent(requireActivity(), className);
                                intent.putExtra("Branch Code", branchCd);
                                intent.putExtra("Branch Name", branchNm);

                                startActivity(intent);
                                dialog.dismiss();
                            }

                            @Override
                            public void onViewRecords(Class className, String branchCd, String branchNm, Dialog dialog) {
                                Intent intent = new Intent(requireActivity(), className);
                                intent.putExtra("Branch Code", branchCd);
                                intent.putExtra("Branch Name", branchNm);

                                startActivity(intent);
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancel(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                        selectAction.initDialog(requireActivity(), BranchCode, BranchName);
                    }
                });

                rec_branchList.notifyDataSetChanged();
                rvc_branchlist.setAdapter(rec_branchList);
                rvc_branchlist.setLayoutManager(new LinearLayoutManager(requireActivity()));
            }
        });
        searchview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                try{
                    String query = s.toString();
                    rec_branchList.getFilter().filter(query);
                    rec_branchList.notifyDataSetChanged();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    String query = s.toString();
                    rec_branchList.getFilter().filter(query);
                    rec_branchList.notifyDataSetChanged();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }
}
