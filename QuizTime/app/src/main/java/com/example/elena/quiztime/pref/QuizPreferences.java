package com.example.elena.quiztime.pref;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.elena.quiztime.R;
import com.example.elena.quiztime.ui.SettingsActivity;

/**
 * Created by User on 03/01/2018.
 */

public class QuizPreferences extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    SharedPreferences sharedPreferences;

    static Preference preference;
    static ListPreference listPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.quiz_preferences);



        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();

        final Preference amountPref =  findPreference(getString(R.string.key_amount_pref));

        amountPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                LayoutInflater inflater = (LayoutInflater)getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View editPref = inflater.inflate(R.layout.pref_edit_text_custom, null);
                final EditText currentAmount = (EditText)editPref.findViewById(android.R.id.edit);
                currentAmount.setText(sharedPreferences.getString(
                        getString(R.string.key_amount_pref),"20"));

                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(amountPref.getTitle());
                alert.setView(editPref);
                alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String stringData = currentAmount.getText().toString();
                        try {
                            int amount = Integer.valueOf(stringData);
                            if (amount>0){
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(getString(R.string.key_amount_pref),
                                        amount+"");
                                editor.apply();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.show();

                return true;
            }
        });
        Log.v("count", count+" prefs");
        for(int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            setSummary(getPreferenceScreen().getPreference(i));
        }


    }

    public void setSummary(Preference pref){

        if (pref instanceof ListPreference){
            ListPreference list = (ListPreference) pref;
            pref.setSummary(list.getEntry());
            Log.v("summary", "list"+list.getEntry());

        }else{
            String value = sharedPreferences.getString(pref.getKey(),"");
            pref.setSummary(value);
            Log.v("summary", "edit"+value);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        Log.v("changed",key+" key");
        if (preference != null){
            if (preference instanceof ListPreference){
                ListPreference list = (ListPreference) preference;
                preference.setSummary(list.getEntry());
            }else{
                String value = sharedPreferences.getString(preference.getKey(),"");
                preference.setSummary(value);
            }
        }
        SettingsActivity.changedPref(getActivity() );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public static QuizPreferences newInstance(Context context){
        QuizPreferences fragment = new QuizPreferences();
        preference = fragment.findPreference(context.getString(R.string.key_amount_pref));
        listPreference  = (ListPreference) fragment
                .findPreference(context.getString(R.string.key_difficulty_pref));
        return fragment;
    }

}
