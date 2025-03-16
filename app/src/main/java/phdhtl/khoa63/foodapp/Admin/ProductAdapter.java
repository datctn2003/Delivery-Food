package phdhtl.khoa63.foodapp.Admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import android.util.Log;
import android.widget.Toast;

import phdhtl.khoa63.foodapp.Domain.Foods;
import phdhtl.khoa63.foodapp.R;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Foods> productList;

    public ProductAdapter(Context context, List<Foods> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        if (productList == null || productList.isEmpty()) {
            Log.e("ProductAdapter", "Danh sách sản phẩm rỗng!");
            return;
        }


        Foods product = productList.get(position);
        if (product == null) {
            Log.e("ProductAdapter", "Sản phẩm tại vị trí " + position + " là null!");
            return;
        }

        // Kiểm tra null trước khi đặt dữ liệu
        holder.title.setText(product.getTitle() != null ? product.getTitle() : "Không có tên");

        // Chuyển đổi double thành String trước khi gán vào TextView
        Double price = product.getPrice();
        holder.price.setText(price != null ? String.valueOf(price) + " VNĐ" : "0 VNĐ");

        // Load hình ảnh an toàn
        String imagePath = product.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(context).load(imagePath).into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.food); // Ảnh mặc định nếu không có ảnh
        }

        // Xử lý sự kiện nút sửa
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditProductActivity.class);
            intent.putExtra("productId", String.valueOf(product.getId())); // Đảm bảo ID là String
            context.startActivity(intent);
        });

        // Xử lý sự kiện nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            String productId = String.valueOf(product.getId());
            if (productId.isEmpty()) {
                Toast.makeText(context, "Lỗi: ID sản phẩm không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Foods").child(productId);
            productRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        if (position >= 0 && position < productList.size()) {
                            productList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, productList.size());
                        }
                        Toast.makeText(context, "Đã xóa sản phẩm!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ProductAdapter", "Lỗi khi xóa sản phẩm: ", e);
                    });
        });

    }

    @Override
    public int getItemCount() {
        return (productList != null) ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title, price;
        ImageView image;
        Button btnEdit, btnDelete;

        public ProductViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.productTitle);
            price = itemView.findViewById(R.id.productPrice);
            image = itemView.findViewById(R.id.productImage);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
