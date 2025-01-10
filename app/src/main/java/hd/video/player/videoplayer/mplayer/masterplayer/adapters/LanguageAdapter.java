package hd.video.player.videoplayer.mplayer.masterplayer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.model.Language;

//public final class LanguageAdapter extends Adapter<LanguageAdapter.MyViewHolder> {
//    private String code = AppConstant.CODE;
//    private Context context;
//    private ArrayList<Country> countries;
//    private OnItemLanguageListener onItemLanguageListener;
//
//    public interface OnItemLanguageListener {
//        void onItemLanguageClick(int i);
//    }
//
//    public final class MyViewHolder extends ViewHolder {
//        ImageView imgIconFlag;
//        LinearLayout ll_main;
//        ImageView selected_img;
//        TextView tvLanguages,tvLanguageCode;
//
//        public MyViewHolder(LanguageAdapter languageAdapter, View view) {
//            super(view);
//            this.selected_img = (ImageView) view.findViewById(R.id.rbCheck);
//            this.ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
//            this.imgIconFlag = (ImageView) view.findViewById(R.id.imgIconFlag);
//            this.tvLanguages = (TextView) view.findViewById(R.id.tvLanguages);
//            this.tvLanguageCode = (TextView) view.findViewById(R.id.tvLanguageCode);
//        }
//    }
//
//    public LanguageAdapter(Context context, ArrayList<Country> arrayList, OnItemLanguageListener onItemLanguageListener) {
//        this.context = context;
//        this.countries = arrayList;
//        this.onItemLanguageListener = onItemLanguageListener;
//    }
//
//    public final Context getContext() {
//        return this.context;
//    }
//
//    public final void setContext(Context context) {
//        this.context = context;
//    }
//
//    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        return new MyViewHolder(this, LayoutInflater.from(this.context).inflate(R.layout.layout_item_language, viewGroup, false));
//    }
//
//    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
//        View view = myViewHolder.itemView;
//        Country country = (Country) this.countries.get(i);
////        ((ImageView) view.findViewById(R.id.imgIconFlag)).setImageResource(country.getIcon());
//        ((TextView) view.findViewById(R.id.tvLanguages)).setText(country.getName());
//        myViewHolder.selected_img.setImageResource(R.drawable.u_item);
//        myViewHolder.tvLanguages.setTextColor(Color.parseColor("#181974"));
//        myViewHolder.tvLanguageCode.setTextColor(Color.parseColor("#181974"));
//        if (country.getLocale().equals(this.code)) {
//            myViewHolder.selected_img.setImageResource(R.drawable.s_item);
//            myViewHolder.ll_main.setBackgroundResource(R.drawable.s_lang);
//            myViewHolder.tvLanguages.setTextColor(ContextCompat.getColor(this.context, R.color.white));
//        }
//        myViewHolder.ll_main.setOnClickListener(view1 -> {
//            String locale = country.getLocale();
//            this.code = locale;
//            AppConstant.CODE = locale;
//            notifyDataSetChanged();
//        });
//    }
//    public int getItemCount() {
//        return this.countries.size();
//    }
//}
public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    private List<Language> languages;
    private int selectedPosition = -1;

    public LanguageAdapter(List<Language> languages) {
        this.languages = languages;
    }
    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_language, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {

        Language language = languages.get(position);
        holder.languageName.setText(language.getName());
        holder.languageCode.setText(language.getCode());

        if (selectedPosition == -1 && "en".equals(language.getCode())) {
            selectedPosition = position;  // Set the position to English
        }

        holder.radioButton.setChecked(position == selectedPosition);

        if (position == selectedPosition) {
            holder.itemView.setSelected(true);
            holder.radioButton.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
            holder.radioButton.setSelected(false);
        }

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
        });

        holder.radioButton.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public String getSelectedLanguageCode() {
        if (selectedPosition != -1) {
            return languages.get(selectedPosition).getCode();
        }
        return null;
    }

    public static class LanguageViewHolder extends RecyclerView.ViewHolder {
        TextView languageName,languageCode;
        RadioButton radioButton;

        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            languageName = itemView.findViewById(R.id.tvLanguages);
            languageCode = itemView.findViewById(R.id.tvLanguageCode);
            radioButton = itemView.findViewById(R.id.tvRadio);
        }
    }
}


