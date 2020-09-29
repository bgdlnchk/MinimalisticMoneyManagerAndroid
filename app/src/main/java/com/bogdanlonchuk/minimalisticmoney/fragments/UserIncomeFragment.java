package com.bogdanlonchuk.minimalisticmoney.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bogdanlonchuk.minimalisticmoney.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

import com.bogdanlonchuk.minimalisticmoney.model.UserCardData;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserIncomeFragment extends Fragment {

    private DatabaseReference mIncomeDatabase;
    private RecyclerView mRecycleView;
    private TextView mIncomeTotalSum;
    private EditText mAmountEdit;
    private EditText mTypeEdit;
    private EditText mNoteEdit;
    private String mType;
    private String mNote;
    private int mAmount;
    private String mPostKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        //Initialize AdMob banner
        AdView mAdView =view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Initialize FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String mUserUid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(mUserUid);
        mIncomeTotalSum = view.findViewById(R.id.income_txt_result);
        mRecycleView = view.findViewById(R.id.recycler_id_income);

        //Configure RecycleView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(linearLayoutManager);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int mTotalValue = 0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    UserCardData userCardData = snapshot.getValue(UserCardData.class);
                    mTotalValue += userCardData.getUserAmount();
                    String mTotalValString=String.valueOf(mTotalValue);
                    mIncomeTotalSum.setText(mTotalValString+".00");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Configure FirebaseRecycleAdapter
        FirebaseRecyclerAdapter<UserCardData, ViewHolder>adapter=new FirebaseRecyclerAdapter<UserCardData, ViewHolder>
                (UserCardData.class, R.layout.income_recycler_data, ViewHolder.class, mIncomeDatabase) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, final UserCardData model, final int position) {
                viewHolder.setType(model.getAmountType());
                viewHolder.setNote(model.getAmountNote());
                viewHolder.setDate(model.getAmountDate());
                viewHolder.setAmount(model.getUserAmount());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPostKey =getRef(position).getKey();
                        mType =model.getAmountType();
                        mNote =model.getAmountNote();
                        mAmount =model.getUserAmount();
                        updateDataItem();
                    }
                });

            }
        };
        mRecycleView.setAdapter(adapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        private void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        private void setNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        private void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        private void setAmount(int amount){
            TextView mAmount = mView.findViewById(R.id.ammount_txt_income);
            String mAmountString=String.valueOf(amount);
            mAmount.setText(mAmountString);
        }
    }

    //Show dialog for item data changing
    private void updateDataItem(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.update_data_item,null);
        builder.setView(view);

        mAmountEdit = view.findViewById(R.id.ammount_edt);
        mTypeEdit = view.findViewById(R.id.type_edt);
        mNoteEdit = view.findViewById(R.id.note_edt);
        mTypeEdit.setText(mType);
        mTypeEdit.setSelection(mType.length());
        mNoteEdit.setText(mNote);
        mNoteEdit.setSelection(mNote.length());
        mAmountEdit.setText(String.valueOf(mAmount));
        mAmountEdit.setSelection(String.valueOf(mAmount).length());
        Button mUpdateBtn = view.findViewById(R.id.btn_upd_Update);
        Button mDeleteBtn = view.findViewById(R.id.btnuPD_Delete);

        final AlertDialog alertDialog = builder.create();

        //Update item
        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mType = mTypeEdit.getText().toString().trim();
                mNote = mNoteEdit.getText().toString().trim();

                String mAmount = String.valueOf(UserIncomeFragment.this.mAmount);
                mAmount= mAmountEdit.getText().toString().trim();

                int myAmount=Integer.parseInt(mAmount);
                String mDate= DateFormat.getDateInstance().format(new Date());
                UserCardData userCardData =new UserCardData(myAmount, mType, mNote, mPostKey,mDate);
                mIncomeDatabase.child(mPostKey).setValue(userCardData);
                alertDialog.dismiss();
            }
        });

        //Delete item
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIncomeDatabase.child(mPostKey).removeValue();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
