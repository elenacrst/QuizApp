package com.example.elena.quiztime.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.elena.quiztime.R;
import com.example.elena.quiztime.data.ScoreTable;
import com.example.elena.quiztime.retrofit.category.CategoryData;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 31/12/2017.
 */
public class ButtonsAdapter extends BaseAdapter {
    private Context mContext;
    private List<CategoryData> mCategories;
    private List<ScoreTable> mScores;
    private String[] mColors=new String[]{
            "#b71c1c",
            "#880e4f",
            "#4a148c",
            "#311b92",
            "#1a237e",
            "#0d47a1",
            "#01579b",
            "#006064",
            "#004d40",
            "#1b5e20",
            "#33691e",
            "#827717",
            "#f57f17",
            "#ff6f00",
            "#e65100",
            "#bf360c",
            "#c62828",
            "#ad1457",
            "#6a1b9a",
            "#4527a0",
            "#283593",
            "#1565c0",
            "#0277bd",
            "#00838f"
    };
    private String[] mColorsLight=new String[]{
            "#f44336",
            "#e91e63",
            "#9c27b0",
            "#673ab7",
            "#3f51b5",
            "#2196f3",
            "#03a9f4",
            "#00bcd4",
            "#009688",
            "#4caf50",
            "#8bc34a",
            "#cddc39",
            "#ffeb3b",
            "#ffc107",
            "#ff9800",
            "#ff5722",
            "#ef5350",
            "#ec407a",
            "#ab47bc",
            "#7e57c2",
            "#5c6bc0",
            "#42a5f5",
            "#29b6f6",
            "#26c6da"
    };

    public ButtonsAdapter(Context c, List<CategoryData> categories) {
        mContext = c;
        mCategories = categories;
        notifyDataSetChanged();
    }

    public void setScores(List<ScoreTable> scores){
        mScores = scores;
        notifyDataSetChanged();
    }

    public List<CategoryData> getmCategories() {
        return mCategories;
    }

    // Total number of things contained within the adapter
    public int getCount() {
        if (mCategories == null) return 0;
        return mCategories.size();
    }

    // Require for structure, not really used in my code.
    public Object getItem(int position) {
        return mCategories.get(position);
    }

    // Require for structure, not really used in my code. Can
    // be used to get the id of an item in the adapter for
    // manual control.
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position,
                        View convertView, ViewGroup parent) {
        View grid;

        if(convertView==null){
            LayoutInflater inflater= LayoutInflater.from(mContext);
            grid=inflater.inflate(R.layout.mygrid_layout, parent, false);
        }else{
            grid = convertView;
        }

        String initial=mCategories.get(position).getName();
        int id = mCategories.get(position).getId();
        initial = initial.replace("Entertainment: ","");
        initial = initial.replace("Science: ","");
        CategoryData categoryData = new CategoryData();
        categoryData.setName(initial);
        categoryData.setId(id);
        mCategories.set(position,categoryData);

        TextView textView = grid.findViewById(R.id.text_category);
        textView.setText(mCategories.get(position).getName());

        TextView scoreTextView = grid.findViewById(R.id.category_score_text);
        if (mScores != null && mScores.size()>0){
            for (int i=0; i<mScores.size(); i++){
                if (mCategories.get(position).getId()== mScores.get(i).getCategoryId()){
                    scoreTextView.setText("Highscore : "+mScores.get(i).getScore()+"");
                }
            }

        }

        LinearLayout view = grid.findViewById(R.id.grid_item);
        GradientDrawable gd = (GradientDrawable)view.getBackground();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            gd.setColors(new int[] {Color.parseColor(mColors[position%mColors.length]),
                    Color.parseColor(mColorsLight[position%mColorsLight.length])});
            gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gd.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        }else {
            gd.setColor(Color.parseColor(mColors[position%mColors.length]));
        }

        CircleImageView imageView = grid.findViewById(R.id.image);
        switch (mCategories.get(position).getName().toLowerCase()){
            case "general knowledge":
                imageView.setImageResource(R.drawable.general);
                break;
            case "books":
                imageView.setImageResource(R.drawable.book);
                break;
            case "film":
                imageView.setImageResource(R.drawable.film);
                break;
            case "music":
                imageView.setImageResource(R.drawable.music);
                break;
            case "musicals & theatres":
                imageView.setImageResource(R.drawable.theatre);
                break;
            case "television":
                imageView.setImageResource(R.drawable.television);
                break;
            case "video games":
                imageView.setImageResource(R.drawable.videogames);
                break;
            case "board games":
                imageView.setImageResource(R.drawable.boardgames);
                break;
            case "science & nature":
                imageView.setImageResource(R.drawable.nature);
                break;
            case "computers":
                imageView.setImageResource(R.drawable.computers);
                break;
            case "mathematics":
                imageView.setImageResource(R.drawable.math);
                break;
            case "mythology":
                imageView.setImageResource(R.drawable.mythology);
                break;
            case "sports":
                imageView.setImageResource(R.drawable.sports);
                break;
            case "geography":
                imageView.setImageResource(R.drawable.geography);
                break;
            case "history":
                imageView.setImageResource(R.drawable.history);
                break;
            case "politics":
                imageView.setImageResource(R.drawable.politics);
                break;
            case "art":
                imageView.setImageResource(R.drawable.art);
                break;
            case "celebrities":
                imageView.setImageResource(R.drawable.celebrities);
                break;
            case "animals":
                imageView.setImageResource(R.drawable.animals);
                break;
            case "vehicles":
                imageView.setImageResource(R.drawable.vehicles);
                break;
            case "comics":
                imageView.setImageResource(R.drawable.comics);
                break;
            case "gadgets":
                imageView.setImageResource(R.drawable.gadgets);
                break;
            case "japanese anime & manga":
                imageView.setImageResource(R.drawable.anime);
                break;
            case "cartoon & animations":
                imageView.setImageResource(R.drawable.cartoons);
                break;
            default:
                imageView.setImageResource(R.drawable.general);
                break;
        }

       /* new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                );
        gd.setCornerRadius(20f);*/

        view.setBackgroundDrawable(gd);

        return grid;
    }
}