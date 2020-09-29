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
public class UserExpenseFragment extends Fragment {

    private DatabaseReference mExpenseDatabase;
    private RecyclerView mRecyclerView;
    private TextView mExpenseSumResult;
    private EditText mAmountEdit;
    private EditText mTypeEdit;
    private EditText mNoteEdit;
    private String mType;
    private String mNote;
    private int mAmountInt;
    private String mPosKey;
    FirebaseRecyclerAdapter<UserCardData,MyViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        //Initialize AdMob banner
        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Initialize FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String mUserUid = mUser.getUid();

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(mUserUid);
        mExpenseSumResult = view.findViewById(R.id.expense_txt_result);
        mRecyclerView = view.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setStackFromEnd(true);
        mLayoutManager.setReverseLayout(true);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Initialize Expense Database
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int mExpenseSum = 0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    UserCardData userCardData =snapshot.getValue(UserCardData.class);
                    mExpenseSum+= userCardData.getUserAmount();
                    String mExpenseSumString=String.valueOf(mExpenseSum);
                    mExpenseSumResult.setText(mExpenseSumString+".00");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Configure FirebaseRecycleAdapter
         adapter = new FirebaseRecyclerAdapter<UserCardData, MyViewHolder>
                (UserCardData.class, R.layout.expense_recycler_data, MyViewHolder.class, mExpenseDatabase) {
            @Override
            protected void populateViewHolder(final MyViewHolder viewHolder, final UserCardData model, final int position) {
                viewHolder.setDate(model.getAmountDate());
                viewHolder.setType(model.getAmountType());
                viewHolder.setNote(model.getAmountNote());
                viewHolder.setAmount(model.getUserAmount());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPosKey = getRef(viewHolder.getAdapterPosition()).getKey();
                        mType = model.getAmountType();
                        mNote = model.getAmountNote();
                        mAmountInt = model.getUserAmount();
                        updateDataItem();
                    }
                });
            }
        };
        mRecyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        private void setDate(String date){
            TextView mDate = mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        private void setType(String type){
            TextView mType = mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        private void setNote(String note){
            TextView mNote = mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }

        private void setAmount(int amount){
            TextView mAmount = mView.findViewById(R.id.ammount_txt_expense);
            String mAmountString = String.valueOf(amount);
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
        mNoteEdit = view.findViewById(R.id.note_edt);
        mTypeEdit = view.findViewById(R.id.type_edt);
        mTypeEdit.setText(mType);
        mTypeEdit.setSelection(mType.length());
        mNoteEdit.setText(mNote);
        mNoteEdit.setSelection(mNote.length());
        mAmountEdit.setText(String.valueOf(mAmountInt));
        mAmountEdit.setSelection(String.valueOf(mAmountInt).length());
        Button mUpdateBtn = view.findViewById(R.id.btn_upd_Update);
        Button mDeleteBtn = view.findViewById(R.id.btnuPD_Delete);

        final AlertDialog alertDialog = builder.create();

        //Update item
        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mType = mTypeEdit.getText().toString().trim();
                mNote = mNoteEdit.getText().toString().trim();
                String mAmountString = String.valueOf(mAmountInt);
                mAmountString = mAmountEdit.getText().toString().trim();
                int mIntAmount = Integer.parseInt(mAmountString);
                String mDate = DateFormat.getDateInstance().format(new Date());

                UserCardData userCardData =new UserCardData(mIntAmount, mType, mNote, mPosKey,mDate);
                mExpenseDatabase.child(mPosKey).setValue(userCardData);

                alertDialog.dismiss();
            }
        });

        //Delete item
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExpenseDatabase.child(mPosKey).removeValue();
                adapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
