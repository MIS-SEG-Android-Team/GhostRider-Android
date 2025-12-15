package org.rmj.guanzongroup.ghostrider.approvalcode.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.guanzongroup.ghostrider.approvalcode.R;

public class Fragment_Transaction_History extends Fragment {

    private MaterialToolbar toolbar;
    private MaterialTextView mtv_title;
    private TextInputEditText tie_search;
    private ImageButton ib_filter;
    private RecyclerView rcv_list;

    private String lsArg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.layout_transaction_history, container, false);

        toolbar = v.findViewById(R.id.toolbar);
        mtv_title = v.findViewById(R.id.mtv_title);
        tie_search = v.findViewById(R.id.tie_search);
        ib_filter = v.findViewById(R.id.ib_filter);
        rcv_list = v.findViewById(R.id.rcv_list);

        return v;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);

        if(args != null){
            if (args.containsKey("source")){
                return;
            }
            lsArg = args.getString("source");
        }
    }
}
