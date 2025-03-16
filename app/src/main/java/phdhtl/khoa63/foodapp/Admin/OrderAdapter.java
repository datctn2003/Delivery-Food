package phdhtl.khoa63.foodapp.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import phdhtl.khoa63.foodapp.Domain.Foods;
import phdhtl.khoa63.foodapp.R;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private List<Order> orderList;
    private OnOrderActionListener listener;

    public OrderAdapter(Context context, List<Order> orderList, OnOrderActionListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Hiển thị thông tin đơn hàng
        holder.orderId.setText("Mã đơn: " + (order.getOrderId() != null ? order.getOrderId() : "N/A"));
        holder.userId.setText("Khách hàng: " + (order.getUserId() != null ? order.getUserId() : "N/A"));
        holder.totalAmount.setText("Tổng tiền: $" + order.getTotalAmount());
        holder.orderStatus.setText("Trạng thái: " + (order.getStatus() != null ? order.getStatus() : "Đang xử lý"));

        // Kiểm tra nếu danh sách sản phẩm không null
        if (order.getOrderedItems() != null && !order.getOrderedItems().isEmpty()) {
            // Gán danh sách sản phẩm vào RecyclerView bên trong
            ProductAdapter productAdapter = new ProductAdapter(context, order.getOrderedItems());
            holder.purchasedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.purchasedItemsRecyclerView.setAdapter(productAdapter);

            // Ẩn nút "Sửa" và "Xóa" sau khi danh sách đã được hiển thị
            holder.purchasedItemsRecyclerView.post(() -> {
                for (int i = 0; i < holder.purchasedItemsRecyclerView.getChildCount(); i++) {
                    View itemView = holder.purchasedItemsRecyclerView.getChildAt(i);
                    if (itemView != null) {
                        Button btnEdit = itemView.findViewById(R.id.btnEdit);
                        Button btnDelete = itemView.findViewById(R.id.btnDelete);
                        if (btnEdit != null) btnEdit.setVisibility(View.GONE);
                        if (btnDelete != null) btnDelete.setVisibility(View.GONE);
                    }
                }
            });

        } else {
            // Nếu không có sản phẩm, ẩn RecyclerView để tránh lỗi
            holder.purchasedItemsRecyclerView.setVisibility(View.GONE);
        }

        // Xử lý duyệt đơn
        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) listener.onApprove(order);
        });

        // Xử lý hủy đơn
        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancel(order);
        });
    }

    @Override
    public int getItemCount() {
        return (orderList != null) ? orderList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, userId, totalAmount, orderStatus;
        RecyclerView purchasedItemsRecyclerView;
        Button btnApprove, btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderId);
            userId = itemView.findViewById(R.id.userId);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            purchasedItemsRecyclerView = itemView.findViewById(R.id.RecyclerView);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }

    public interface OnOrderActionListener {
        void onApprove(Order order);
        void onCancel(Order order);
    }
}
