package com.bogdanlonchuk.minimalisticmoney.fragments;


import android.app.AlertDialog;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bogdanlonchuk.minimalisticmoney.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
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
public class UserDashboardFragment extends Fragment {

    private FloatingActionButton mFabIncomeBtn;
    private FloatingActionButton mFabExpenseBtn;
    private InterstitialAd mInterstitialAd;
    private TextView mFabIncomeText;
    private TextView mFabExpenseText;
    private boolean isOpen = false;
    private Animation mFadeOpen, mFadeClose;
    private TextView mTotalIncomeResult;
    private TextView mTotalExpenseResult;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;
    private int cardAddedTimes = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        //Initialize AdMob banner
        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Initialize AdMob interstitial
        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId(getString(R.string.admobInterstitialID));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        //Initialize FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        //Initialize Firebase Database
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);

        FloatingActionButton mFabMainBtn = view.findViewById(R.id.fb_main_plus_btn);
        mFabIncomeBtn = view.findViewById(R.id.income_Ft_btn);
        mFabExpenseBtn = view.findViewById(R.id.expense_Ft_btn);

        mFabIncomeText = view.findViewById(R.id.income_ft_text);
        mFabExpenseText = view.findViewById(R.id.expense_ft_text);

        mTotalIncomeResult = view.findViewById(R.id.income_set_result);
        mTotalExpenseResult = view.findViewById(R.id.expense_set_result);

        //Set animation for FloatingActionButton
        mFadeOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.open_fade);
        mFadeClose = AnimationUtils.loadAnimation(getActivity(),R.anim.close_fade);

        mFabMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
                if (isOpen){

                    mFabIncomeBtn.startAnimation(mFadeClose);
                    mFabExpenseBtn.startAnimation(mFadeClose);
                    mFabIncomeBtn.setClickable(false);
                    mFabExpenseBtn.setClickable(false);

                    mFabIncomeText.startAnimation(mFadeClose);
                    mFabExpenseText.startAnimation(mFadeClose);
                    mFabIncomeText.setClickable(false);
                    mFabExpenseText.setClickable(false);
                    isOpen=false;
                }else {

                    mFabIncomeBtn.startAnimation(mFadeOpen);
                    mFabExpenseBtn.startAnimation(mFadeOpen);
                    mFabIncomeBtn.setClickable(true);
                    mFabExpenseBtn.setClickable(true);

                    mFabIncomeText.startAnimation(mFadeOpen);
                    mFabExpenseText.startAnimation(mFadeOpen);
                    mFabIncomeText.setClickable(true);
                    mFabExpenseText.setClickable(true);
                    isOpen=true;
                }
            }
        });

        //Add value to Income page
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int mTotalSum = 0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    UserCardData userCardData = snapshot.getValue(UserCardData.class);
                    mTotalSum += userCardData.getUserAmount();
                    String stResult = String.valueOf(mTotalSum);
                    mTotalIncomeResult.setText(stResult+".00");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Add value to Expense page
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int mTotalSum = 0;
                for (DataSnapshot mysnapshot:dataSnapshot.getChildren()){
                    UserCardData userCardData = mysnapshot.getValue(UserCardData.class);
                    mTotalSum+= userCardData.getUserAmount();
                    String strTotalSum=String.valueOf(mTotalSum);
                    mTotalExpenseResult.setText(strTotalSum+".00");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mRecyclerIncome = view.findViewById(R.id.recycler_income);
        mRecyclerExpense = view.findViewById(R.id.recycler_expense);

        //Initialize Layout for Income
        LinearLayoutManager mLayoutManagerIncome = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        mLayoutManagerIncome.setReverseLayout(true);
        mLayoutManagerIncome.setStackFromEnd(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(mLayoutManagerIncome);

        //Initialize Layout for Expense
        LinearLayoutManager mLayoutManagerExpense=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        mLayoutManagerExpense.setReverseLayout(true);
        mLayoutManagerExpense.setStackFromEnd(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(mLayoutManagerExpense);

        return  view;
    }

    //Set animation for Fab button
    private void AnimateFab(){
        if (isOpen){
            mFabIncomeBtn.startAnimation(mFadeClose);
            mFabExpenseBtn.startAnimation(mFadeClose);
            mFabIncomeBtn.setClickable(false);
            mFabExpenseBtn.setClickable(false);
            mFabIncomeText.startAnimation(mFadeClose);
            mFabExpenseText.startAnimation(mFadeClose);
            mFabIncomeText.setClickable(false);
            mFabExpenseText.setClickable(false);
            isOpen=false;

        }else {
            mFabIncomeBtn.startAnimation(mFadeOpen);
            mFabExpenseBtn.startAnimation(mFadeOpen);
            mFabIncomeBtn.setClickable(true);
            mFabExpenseBtn.setClickable(true);
            mFabIncomeText.startAnimation(mFadeOpen);
            mFabExpenseText.startAnimation(mFadeOpen);
            mFabIncomeText.setClickable(true);
            mFabExpenseText.setClickable(true);
            isOpen=true;
        }
    }

    private void addData(){
        mFabIncomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertIncomeData();
            }
        });
        mFabExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertExpenseData();
            }
        });
    }

    //Show Add Income dialog
    private void insertIncomeData(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.custom_layout_for_insertdata,null);
        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.setCancelable(false);

        final EditText mAmountEdit = view.findViewById(R.id.ammount_edt);
        final EditText mTypeEdit = view.findViewById(R.id.type_edt);
        final EditText mNoteEdit = view.findViewById(R.id.note_edt);
        Button mSaveBtn = view.findViewById(R.id.btnSave);
        Button mCancelBtn = view.findViewById(R.id.btnCancel);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mType = mTypeEdit.getText().toString().trim();
                String mAmount = mAmountEdit.getText().toString().trim();
                String mNote = mNoteEdit.getText().toString().trim();

                if (TextUtils.isEmpty(mType)){
                    mTypeEdit.setError("Required Field");
                    return;
                }

                if (TextUtils.isEmpty(mAmount)){
                    mAmountEdit.setError("Required Field");
                    return;
                }

                int mAmountInt = Integer.parseInt(mAmount);

                if (TextUtils.isEmpty(mNote)){
                    mNoteEdit.setError("Required Field");
                    return;
                }

                String mUserID = mIncomeDatabase.push().getKey();

                String mDate = DateFormat.getDateInstance().format(new Date());
                UserCardData userCardData =new UserCardData(mAmountInt,mType,mNote,mUserID,mDate);
                mIncomeDatabase.child(mUserID).setValue(userCardData);
                cardAddedTimes ++;
                if(cardAddedTimes == 2){
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    cardAddedTimes = 0;
                }
                AnimateFab();
                dialog.dismiss();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimateFab();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //Show Add Expense dialog
    private void insertExpenseData(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.custom_layout_for_insertdata,null);
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);

        final EditText mAmountEdit = view.findViewById(R.id.ammount_edt);
        final EditText mTypeEdit = view.findViewById(R.id.type_edt);
        final EditText mNoteEdit = view.findViewById(R.id.note_edt);

        Button mSaveBtn=view.findViewById(R.id.btnSave);
        Button mCancelBtn=view.findViewById(R.id.btnCancel);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mAmountString = mAmountEdit.getText().toString().trim();
                String mTypeString = mTypeEdit.getText().toString().trim();
                String mNoteString = mNoteEdit.getText().toString().trim();

                if (TextUtils.isEmpty(mAmountString)){
                    mAmountEdit.setError("Required Field");
                    return;
                }

                int mAmountInt = Integer.parseInt(mAmountString);
                if (TextUtils.isEmpty(mTypeString)){
                    mTypeEdit.setError("Required Field");
                    return;
                }

                if (TextUtils.isEmpty(mNoteString )){
                    mNoteEdit.setError("Required Field");
                    return;
                }

                String mUserID = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                UserCardData userCardData =new UserCardData(mAmountInt,mTypeString,mNoteString ,mUserID,mDate);
                mExpenseDatabase.child(mUserID).setValue(userCardData);
                cardAddedTimes ++;
                if(cardAddedTimes == 2){
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    cardAddedTimes = 0;
                }
                AnimateFab();
                alertDialog.dismiss();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimateFab();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<UserCardData,IncomeViewHolder>incomeAdapter=new FirebaseRecyclerAdapter<UserCardData, IncomeViewHolder>
                (UserCardData.class, R.layout.dashboard_income, UserDashboardFragment.IncomeViewHolder.class, mIncomeDatabase) {
            @Override
            protected void populateViewHolder(IncomeViewHolder viewHolder, UserCardData model, int position) {
                viewHolder.setIncomeType(model.getAmountType());
                viewHolder.setIncomeAmount(model.getUserAmount());
                viewHolder.setIncomeDate(model.getAmountDate());
            }
        };
        mRecyclerIncome.setAdapter(incomeAdapter);

        FirebaseRecyclerAdapter<UserCardData,ExpenseViewHolder>expenseAdapter=new FirebaseRecyclerAdapter<UserCardData, ExpenseViewHolder>
                (
                        UserCardData.class,
                        R.layout.dashboart_expense,
                        UserDashboardFragment.ExpenseViewHolder.class,
                        mExpenseDatabase
                ) {
            @Override
            protected void populateViewHolder(ExpenseViewHolder viewHolder, UserCardData model, int position) {
                viewHolder.setExpenseType(model.getAmountType());
                viewHolder.setExpenseAmount(model.getUserAmount());
                viewHolder.setExpenseDate(model.getAmountDate());
            }
        };
        mRecyclerExpense.setAdapter(expenseAdapter);
    }

    //Show Income Cards
    public static class IncomeViewHolder extends RecyclerView.ViewHolder{
        View mIncomeView;

        public IncomeViewHolder(View itemView) {
            super(itemView);
            mIncomeView = itemView;
        }

        void setIncomeType(String type){
            TextView mType = mIncomeView.findViewById(R.id.type_Income_ds);
            mType.setText(type);
        }

        void setIncomeAmount(int amount){
            TextView mAmount = mIncomeView.findViewById(R.id.amount_income_ds);
            String mAmountString = String.valueOf(amount);
            mAmount.setText(mAmountString);
        }

        void setIncomeDate(String date){
            TextView mDate = mIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(date);
        }
    }

    //Show Expense cards
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{
        View mExpenseView;
        public ExpenseViewHolder(View itemView) {
            super(itemView);
            mExpenseView=itemView;
        }

        void setExpenseType(String type){
            TextView mType = mExpenseView.findViewById(R.id.type_expense_ds);
            mType.setText(type);
        }

        void setExpenseAmount(int ammount){
            TextView mAmount = mExpenseView.findViewById(R.id.ammoun_expense_ds);
            String mAmountString = String.valueOf(ammount);
            mAmount.setText(mAmountString);
        }

        void setExpenseDate(String date){
            TextView mDate = mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }
    }
}