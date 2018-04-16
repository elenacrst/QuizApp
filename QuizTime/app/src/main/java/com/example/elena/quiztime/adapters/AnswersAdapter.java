package com.example.elena.quiztime.adapters;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.elena.quiztime.R;
import com.example.elena.quiztime.ui.QuestionActivity;

import java.util.List;

/**
 * Created by User on 01/01/2018.
 */

public class AnswersAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mAnswers;
    private String mCorrectAnswer;

    private String mSelectedAnswer="";

    // Gets the context so it can be used later
    public AnswersAdapter(Context c, List<String> answers, String sCorrectAnswer) {
        mContext = c;
        mAnswers = answers;
        mCorrectAnswer = sCorrectAnswer;
        notifyDataSetChanged();
    }

    // Total number of things contained within the adapter
    public int getCount() {
        if (mAnswers == null) return 0;
        return mAnswers.size();
    }

    // Require for structure, not really used in my code.
    public Object getItem(int position) {
        return mAnswers.get(position);
    }

    public View getView(int position,
                        View convertView, ViewGroup parent) {
        View grid;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            grid = inflater.inflate(R.layout.answer_layout, parent, false);
        } else {
            grid =  convertView;
        }

        final TextView textView =  grid.findViewById(R.id.text_answer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(mAnswers.get(position), Html.FROM_HTML_MODE_LEGACY));

        }
        else{
            textView.setText(Html.fromHtml(mAnswers.get(position)));
        }

        grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedAnswer = textView.getText().toString();
                QuestionActivity.setSelection(mSelectedAnswer);
                if (QuestionActivity.isAnsweringEnabled()){
                    if (textView.getText().toString().equals(mCorrectAnswer)){
                        textView.setBackgroundResource(R.drawable.rounded_rec_green);
                        QuestionActivity.increaseScore(true);
                    }else{
                        textView.setBackgroundResource(R.drawable.rounded_rec_red);
                        QuestionActivity.increaseScore(false);
                    }
                }

            }
        });

        return grid;
    }

    public List<String> getmAnswers() {
        return mAnswers;
    }

    @Override
    public long getItemId(int position) {
        //Return a unique and stable id for the given position
        //While unique, Returning the position number does not count as stable.
        //For example:
        return (long)position;
    }

    public String getmSelectedAnswer() {
        return mSelectedAnswer;
    }
}