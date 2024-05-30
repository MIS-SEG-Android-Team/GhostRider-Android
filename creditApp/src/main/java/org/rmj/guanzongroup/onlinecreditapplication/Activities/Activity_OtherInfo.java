package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DTownInfo;
import org.rmj.g3appdriver.etc.GToast;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppConstants;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.OtherReference;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.Reference;
import org.rmj.guanzongroup.onlinecreditapplication.Adapter.ReferencesAdapter;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.OnParseListener;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMOtherInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity_OtherInfo extends AppCompatActivity {

    private VMOtherInfo mViewModel;
    private MessageBox poMessage;
    private ReferencesAdapter adapter;
    private Reference reference;

    private static final int MOBILE_DIALER = 104;

    private MaterialAutoCompleteTextView spnUnitUser, spnUnitPrps, spnUnitPayr, spnSourcexx, spnOthrUser, spnOthrPayr,
            tieAddProv, tieAddTown;
    private TextInputLayout tilOthrUser, tilOthrPayr, tilOtherSrc;

    private TextInputEditText tieOthrSrc, tieRefAdd1, tieRefName, tieRefCntc;

    private RecyclerView recyclerView;
    private MaterialButton btnPrevs;
    private MaterialButton btnNext;
    private MaterialToolbar toolbar;
    private MaterialButton btnAddReference;

    private String TransNox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_OtherInfo.this).get(VMOtherInfo.class);
        poMessage = new MessageBox(Activity_OtherInfo.this);
        setContentView(R.layout.activity_other_info);
        initWidgets();
        mViewModel.InitializeApplication(getIntent());
        mViewModel.GetApplication().observe(Activity_OtherInfo.this, app -> {
            try {
                TransNox = app.getTransNox();
                mViewModel.getModel().setTransNox(app.getTransNox());
                mViewModel.ParseData(app, new OnParseListener() {
                    @Override
                    public void OnParse(Object args) {
                        OtherReference loDetail =  (OtherReference) args;
                        try {
                            setUpFieldsFromLocalDB(loDetail);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        mViewModel.getReferenceList().observe(Activity_OtherInfo.this, loList->{
            adapter = new ReferencesAdapter(loList, new ReferencesAdapter.OnAdapterClick() {
                @Override
                public void onRemove(int position) {
                    mViewModel.removeReference(position);
                    GToast.CreateMessage(Activity_OtherInfo.this, "Reference removed from list.", GToast.INFORMATION).show();
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCallMobile(String fsMobileN) {
                    Intent mobileIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", fsMobileN, null));
                    startActivityForResult(mobileIntent, MOBILE_DIALER);
                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(Activity_OtherInfo.this));
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });
        btnAddReference.setOnClickListener(v -> addReference());

        btnPrevs.setOnClickListener(v -> {
            returnPrevious();
        });
        btnNext.setOnClickListener(v -> SaveOtherInfo());

    }

    private void SaveOtherInfo() {

        mViewModel.getModel().setCompanyInfoSource(Objects.requireNonNull(tieOthrSrc.getText()).toString());
        mViewModel.getReferenceList().observe(Activity_OtherInfo.this, loList->{
            mViewModel.getModel().clear();
            for(int x = 0; x < loList.size(); x++){
                Reference info = loList.get(x);
                mViewModel.getModel().AddReference(info);
            }
        });
        mViewModel.SaveData(new OnSaveInfoListener() {
            @Override
            public void OnSave(String args) {
                Intent loIntent = new Intent(Activity_OtherInfo.this, Activity_CoMaker.class);
                loIntent.putExtra("sTransNox", args);
                startActivity(loIntent);
                overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
                finish();

            }

            @Override
            public void OnFailed(String message) {
                poMessage.initDialog();
                poMessage.setTitle("Credit Online Application");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Okay", (view1, dialog) -> dialog.dismiss());
                poMessage.show();
            }
        });

    }

    private void initWidgets() {
        reference = new Reference();
        toolbar = findViewById(R.id.toolbar_OtherInfo);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        tilOthrUser = findViewById(R.id.til_cap_otherUser);
        tilOthrPayr = findViewById(R.id.til_cap_otherPayer);
        tilOtherSrc = findViewById(R.id.til_cap_otherSource);

        spnUnitPrps = findViewById(R.id.spinner_cap_purposeOfBuying);
        spnUnitPayr = findViewById(R.id.spinner_cap_monthlyPayer);
        spnUnitUser = findViewById(R.id.spinner_cap_unitUser);
        spnOthrUser = findViewById(R.id.spinner_cap_otherUser);
        spnOthrPayr = findViewById(R.id.spinner_cap_otherPayer);
        spnSourcexx = findViewById(R.id.spinner_cap_source);

        tieOthrSrc = findViewById(R.id.tie_cap_otherSource);
        tieRefName = findViewById(R.id.tie_cap_referenceName);
        tieRefCntc = findViewById(R.id.tie_cap_refereceContact);
        tieRefAdd1 = findViewById(R.id.tie_cap_refereceAddress);
//        tieAddProv = findViewById(R.id.tie_cap_referenceAddProv);
        tieAddTown = findViewById(R.id.tie_cap_referenceAddTown);
        btnAddReference = findViewById(R.id.btn_fragment_others_addReference);

        recyclerView = findViewById(R.id.recyclerview_references);

        tilOthrUser.setVisibility(View.GONE);
        tilOthrPayr.setVisibility(View.GONE);
        tilOtherSrc.setVisibility(View.GONE);

        btnNext = findViewById(R.id.btn_creditAppNext);
        btnPrevs = findViewById(R.id.btn_creditAppPrvs);

        spnUnitUser.setAdapter(new ArrayAdapter<>(Activity_OtherInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.UNIT_USER));
        spnOthrUser.setAdapter(new ArrayAdapter<>(Activity_OtherInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.UNIT_USER_OTHERS));
        spnUnitPrps.setAdapter(new ArrayAdapter<>(Activity_OtherInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.UNIT_PURPOSE));
        spnUnitPayr.setAdapter(new ArrayAdapter<>(Activity_OtherInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.UNIT_USER));
        spnOthrPayr.setAdapter(new ArrayAdapter<>(Activity_OtherInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.UNIT_PAYER));
        spnSourcexx.setAdapter(new ArrayAdapter<>(Activity_OtherInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.UNIT_PAYER));

        spnUnitUser.setOnItemClickListener(new Activity_OtherInfo.SpinnerSelectionListener(spnUnitUser));
        spnUnitPayr.setOnItemClickListener(new Activity_OtherInfo.SpinnerSelectionListener(spnUnitPayr));
        spnSourcexx.setOnItemClickListener(new Activity_OtherInfo.SpinnerSelectionListener(spnSourcexx));

        spnUnitPrps.setOnItemClickListener(new Activity_OtherInfo.SpinnerSelectionListener(spnUnitPrps));
        spnOthrUser.setOnItemClickListener(new Activity_OtherInfo.SpinnerSelectionListener(spnOthrUser));
        spnOthrPayr.setOnItemClickListener(new Activity_OtherInfo.SpinnerSelectionListener(spnOthrPayr));
//
        mViewModel.GetTownProvinceList().observe(Activity_OtherInfo.this, new Observer<List<DTownInfo.TownProvinceInfo>>() {
            @Override
            public void onChanged(List<DTownInfo.TownProvinceInfo> loList) {
                try {
                    ArrayList<String> strings = new ArrayList<>();
                    for (int x = 0; x < loList.size(); x++) {
                        String lsTown = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                        strings.add(lsTown);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_OtherInfo.this, android.R.layout.simple_spinner_dropdown_item, strings.toArray(new String[0]));
                    tieAddTown.setAdapter(adapter);
                    tieAddTown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            for (int x = 0; x < loList.size(); x++) {
                                String lsLabel = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                                String lsSlctd = tieAddTown.getText().toString().trim();
                                if (lsSlctd.equalsIgnoreCase(lsLabel)) {
                                    reference.setTownCity(loList.get(x).sTownIDxx);
                                    reference.setTownName(lsLabel);
                                    break;
                                }
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    class SpinnerSelectionListener implements AdapterView.OnItemClickListener{
        private final AutoCompleteTextView poView;
        SpinnerSelectionListener(AutoCompleteTextView view){
            this.poView = view;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(spnUnitUser.equals(poView)){
                mViewModel.getModel().setsUnitUser(String.valueOf(i));
                if(i == 1){
                    tilOthrUser.setVisibility(View.VISIBLE);
                } else {
                    mViewModel.getModel().setsUsr2Buyr(null);
                    tilOthrUser.setVisibility(View.GONE);
                }
            } else if(spnUnitPayr.equals(poView)){
                mViewModel.getModel().setsUnitPayr(String.valueOf(i));
                if(i == 1){
                    tilOthrPayr.setVisibility(View.VISIBLE);
                } else {
                    mViewModel.getModel().setsPyr2Buyr(null);
                    tilOthrPayr.setVisibility(View.GONE);
                }
            } else if(spnSourcexx.equals(poView)){
                mViewModel.getModel().setSource(spnSourcexx.getText().toString());
                if(i == 5){
                    tilOtherSrc.setVisibility(View.VISIBLE);
                } else {
                    tilOtherSrc.setVisibility(View.GONE);
                }
            } else if(spnUnitPrps.equals(poView)){
                mViewModel.getModel().setsPurposex(String.valueOf(i));
            } else if(spnOthrUser.equals(poView)){
                mViewModel.getModel().setsUsr2Buyr(String.valueOf(i));
            } else if(spnOthrPayr.equals(poView)){
                mViewModel.getModel().setsPyr2Buyr(String.valueOf(i));
            }
        }
    }
    private void addReference(){
        try {
            if(tieRefName.getText().toString().isEmpty()){
                GToast.CreateMessage(Activity_OtherInfo.this,"Reference name required!", GToast.WARNING).show();
            }else  if(tieRefCntc.getText().toString().isEmpty()){
                GToast.CreateMessage(Activity_OtherInfo.this,"Reference Contact Number required!", GToast.WARNING).show();
            }else  if(tieRefAdd1.getText().toString().isEmpty()) {
                GToast.CreateMessage(Activity_OtherInfo.this, "Reference Address required!", GToast.WARNING).show();
            }else  if(tieAddTown.getText().toString().isEmpty()){
                GToast.CreateMessage(Activity_OtherInfo.this,"Reference Municipality required!", GToast.WARNING).show();
            }else{
                String refName = (Objects.requireNonNull(tieRefName.getText()).toString());
                String refContact = (Objects.requireNonNull(tieRefCntc.getText()).toString());
                String refAddress = (Objects.requireNonNull(tieRefAdd1.getText()).toString());
                reference.setFullname(refName);
                reference.setAddress1(refAddress);
                reference.setContactN(refContact);
                mViewModel.addReference(reference, new VMOtherInfo.AddPersonalInfoListener() {
                    @Override
                    public void OnSuccess() {
                        tieRefName.setText("");
                        tieRefCntc.setText("");
                        tieRefAdd1.setText("");
//                        tieAddProv.setText("");
                        tieAddTown.setText("");
                        reference = new Reference();
                    }

                    @Override
                    public void onFailed(String message) {
                        GToast.CreateMessage(Activity_OtherInfo.this, message, GToast.ERROR).show();
                    }
                });


            }

        }catch (NullPointerException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public void setUpFieldsFromLocalDB(OtherReference infoModel) throws JSONException {
        if(infoModel != null) {

            if(infoModel.getsUnitUser() != null){
                spnUnitUser.setText(CreditAppConstants.UNIT_USER[Integer.parseInt(infoModel.getsUnitUser())], false);
//                spnUnitUser.setSelection(Integer.parseInt(infoModel.getsUnitUser()));
                mViewModel.getModel().setsUnitUser(infoModel.getsUnitUser());
            }
            if(infoModel.getsPurposex() != null){
                spnUnitPrps.setText(CreditAppConstants.UNIT_PURPOSE[Integer.parseInt(infoModel.getsPurposex())], false);
//                spnUnitPrps.setSelection(Integer.parseInt(infoModel.getsPurposex()));
                mViewModel.getModel().setsPurposex(infoModel.getsPurposex());
            }
            if(infoModel.getsUnitPayr() != null) {
                spnUnitPayr.setText(CreditAppConstants.UNIT_USER[Integer.parseInt(infoModel.getsUnitPayr())], false);
//                spnUnitPayr.setSelection(Integer.parseInt(infoModel.getsUnitPayr()));
                mViewModel.getModel().setsUnitPayr(infoModel.getsUnitPayr());
            }

            if(infoModel.getSource() != null) {
                spnSourcexx.setText(infoModel.getSource(), false);
//                spnSourcexx.setSelection(Integer.parseInt(infoModel.getSource()));
                mViewModel.getModel().setSource(infoModel.getSource());
            }
            if(infoModel.getReferences() != null){
//                mViewModel.getReferenceList().getValue().clear();
                mViewModel.setListOfReference(infoModel.getReferences());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            returnPrevious();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        returnPrevious();
    }

    @Override
    protected void onDestroy() {
        getViewModelStore().clear();
        super.onDestroy();
    }

    private void returnPrevious(){
        Intent loIntent = new Intent(Activity_OtherInfo.this, Activity_Properties.class);
        loIntent.putExtra("sTransNox", TransNox);
        startActivity(loIntent);
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
        finish();
    }
}