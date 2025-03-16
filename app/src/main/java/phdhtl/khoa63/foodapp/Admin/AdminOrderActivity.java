package phdhtl.khoa63.foodapp.Admin;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
import phdhtl.khoa63.foodapp.Domain.Foods;
import phdhtl.khoa63.foodapp.R;

public class AdminOrderActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private DatabaseReference databaseReference;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();

        adapter = new OrderAdapter(this, orderList, new OrderAdapter.OnOrderActionListener() {
            @Override
            public void onApprove(Order order) {
                updateOrderStatus(order, "Đã duyệt");
            }

            @Override
            public void onCancel(Order order) {
                updateOrderStatus(order, "Đã hủy");
            }
        });

        recyclerView.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders");
        loadOrders();

        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> finish());
    }

    private void loadOrders() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String orderId = dataSnapshot.child("orderId").getValue(String.class);
                    String userId = dataSnapshot.child("userId").getValue(String.class);
                    double totalAmount = dataSnapshot.child("totalAmount").getValue(Double.class);
                    String paymentMethod = dataSnapshot.child("paymentMethod").getValue(String.class);
                    String status = dataSnapshot.child("status").getValue(String.class);

                    // Lấy danh sách sản phẩm đã mua
                    List<Foods> orderedItems = new ArrayList<>();
                    for (DataSnapshot productSnapshot : dataSnapshot.child("orderedItems").getChildren()) {
                        String title = productSnapshot.child("title").getValue(String.class);
                        double price = productSnapshot.child("price").getValue(Double.class);
                        int numberInCart = productSnapshot.child("numberInCart").getValue(Integer.class);
                        Foods food = new Foods();
                        food.setTitle(title);
                        food.setPrice(price);
                        food.setNumberInCart(numberInCart);
                        orderedItems.add(food);
                    }

                    Order order = new Order(orderId, userId, orderedItems, totalAmount, paymentMethod, status);
                    orderList.add(order);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminOrderActivity.this, "Lỗi tải đơn hàng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateOrderStatus(Order order, String newStatus) {
        databaseReference.child(order.getOrderId()).child("status").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật trạng thái thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi cập nhật trạng thái!", Toast.LENGTH_SHORT).show();
                });
    }
}
