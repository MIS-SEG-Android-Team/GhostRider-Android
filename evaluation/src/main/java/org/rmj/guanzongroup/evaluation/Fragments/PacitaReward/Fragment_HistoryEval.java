package org.rmj.guanzongroup.evaluation.Fragments.PacitaReward;

import android.content.Intent;
import android.os.Bundle;
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

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DPacita;
import org.rmj.guanzongroup.evaluation.Activity.PacitaReward.Activity_BranchRecord_Details;
import org.rmj.guanzongroup.evaluation.Adapter.PacitaReward.RecyclerViewAdapter_EvalHistory;
import org.rmj.guanzongroup.evaluation.R;
import org.rmj.guanzongroup.evaluation.ViewModel.PacitaReward.VMBranchList;

import java.util.List;

public class Fragment_HistoryEval extends Fragment{
    private VMBranchList mViewModel;
    private RecyclerView rcv_evallist;
    private RecyclerViewAdapter_EvalHistory rec_evalhist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_layouthisteval, container, false);

        rcv_evallist = view.findViewById(R.id.recenteval_list);

        mViewModel = new ViewModelProvider(this).get(VMBranchList.class);
        mViewModel.getRecentRecords().observe(getActivity(), new Observer<List<DPacita.RecentRecords>>() {
            @Override
            public void onChanged(List<DPacita.RecentRecords> recentRecords) {
                rec_evalhist = new RecyclerViewAdapter_EvalHistory(getContext(), recentRecords, new RecyclerViewAdapter_EvalHistory.onSelectItem() {
                    @Override
                    public void onItemSelected(String transactNox, String branchcode, String branchname) {
                        Intent intent = new Intent(getContext(), Activity_BranchRecord_Details.class);
                        intent.putExtra("Transaction No", transactNox);
                        intent.putExtra("Branch Code", branchcode);
                        intent.putExtra("Branch Name", branchname);

                        startActivity(intent);
                    }
                });

                rec_evalhist.notifyDataSetChanged();
                rcv_evallist.setAdapter(rec_evalhist);
                rcv_evallist.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });

        return view;
    }
}
