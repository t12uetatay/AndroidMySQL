package id.t12uetatay.crud.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import id.t12uetatay.crud.R;
import id.t12uetatay.crud.api.RetrofitService;
import id.t12uetatay.crud.models.Product;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Product> list;
    private AdapterListener listener;

    public static String currencyId(double nominal){
        Locale localID = new Locale("in", "ID");
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(localID);

        return rupiah.format(nominal);
    }

    public ProductAdapter(Context context, AdapterListener listener) {
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        Product current = list.get(position);

        itemViewHolder.text_name.setText(current.getProductName());
        itemViewHolder.text_desc.setText(current.getDescription());
        itemViewHolder.text_tarif.setText(currencyId(current.getPrice()));

        Glide.with(mContext)
                .load(RetrofitService.base_url_image +current.getPic())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        itemViewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        itemViewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(itemViewHolder.pic);

        itemViewHolder.act_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onActionMoreClick(current, itemViewHolder.act_more);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setDataList(List<Product> dataList) {
        this.list = dataList;
        notifyDataSetChanged();
    }

    public interface AdapterListener {
        void onClick(long lapangId, int position);
        void onActionMoreClick(Product product, ImageView imageView);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        ImageView pic, act_more;
        TextView text_name, text_tarif, text_desc;
        public ItemViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
            pic = itemView.findViewById(R.id.pic);
            act_more = itemView.findViewById(R.id.act_more);
            text_name = itemView.findViewById(R.id.text_name);
            text_tarif = itemView.findViewById(R.id.text_tarif);
            text_desc = itemView.findViewById(R.id.text_desc);
        }
    }
}
