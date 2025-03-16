package phdhtl.khoa63.foodapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import phdhtl.khoa63.foodapp.Domain.Foods;
import phdhtl.khoa63.foodapp.Helper.ChangeNumberItemsListener;
import phdhtl.khoa63.foodapp.Helper.ManagmentCart;
import phdhtl.khoa63.foodapp.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Viewholder> {
    private ArrayList<Foods> listItemSelected;
    private ManagmentCart managmentCart;
    private ChangeNumberItemsListener changeNumberItemsListener;
    private boolean isPurchasedMode;

    public CartAdapter(ArrayList<Foods> listItemSelected, Context context, ChangeNumberItemsListener changeNumberItemsListener, boolean isPurchasedMode) {
        this.listItemSelected = listItemSelected;
        this.managmentCart = new ManagmentCart(context);
        this.changeNumberItemsListener = changeNumberItemsListener;
        this.isPurchasedMode = isPurchasedMode;
    }

    @NonNull
    @Override
    public CartAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.Viewholder holder, int position) {
        Foods item = listItemSelected.get(position);

        // Áp dụng hiệu ứng mờ
        float radius = 10f;
        View decorView = ((Activity) holder.itemView.getContext()).getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        holder.blurView.setupWith(rootView, new RenderScriptBlur(holder.itemView.getContext()))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius);
        holder.blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        holder.blurView.setClipToOutline(true);

        // Hiển thị thông tin sản phẩm
        Glide.with(holder.itemView.getContext())
                .load(item.getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.pic);

        holder.title.setText(item.getTitle());
        holder.feeEachItem.setText("$" + item.getPrice());
        holder.totalEachItem.setText(item.getNumberInCart() + " x $" + item.getPrice());

        holder.num.setText(String.valueOf(item.getNumberInCart()));

        // Nếu là chế độ đã mua, ẩn các nút chỉnh sửa số lượng
        if (isPurchasedMode) {
            holder.plusItem.setVisibility(View.GONE);
            holder.minusItem.setVisibility(View.GONE);
        } else {
            holder.plusItem.setOnClickListener(v -> {
                managmentCart.plusNumberItem(listItemSelected, position, () -> {
                    changeNumberItemsListener.change();
                    notifyDataSetChanged();
                });
            });

            holder.minusItem.setOnClickListener(v -> {
                managmentCart.minusNumberItem(listItemSelected, position, () -> {
                    changeNumberItemsListener.change();
                    notifyDataSetChanged();
                });
            });
        }
    }

    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView title, feeEachItem, plusItem, minusItem, totalEachItem, num;
        ImageView pic;
        BlurView blurView;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTxt);
            pic = itemView.findViewById(R.id.pic);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            plusItem = itemView.findViewById(R.id.plusBtn);
            minusItem = itemView.findViewById(R.id.minusBtn);
            num = itemView.findViewById(R.id.numTxt);
            blurView = itemView.findViewById(R.id.blurView);
        }
    }
}
