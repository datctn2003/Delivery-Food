package phdhtl.khoa63.foodapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import phdhtl.khoa63.foodapp.Adapter.CartAdapter;
import phdhtl.khoa63.foodapp.Admin.Order;
import phdhtl.khoa63.foodapp.Domain.Foods;
import phdhtl.khoa63.foodapp.Helper.ChangeNumberItemsListener;
import phdhtl.khoa63.foodapp.R;

public class PurchasedProductsActivity extends AppCompatActivity {

    private RecyclerView purchaseRecyclerView;
    private CartAdapter cartAdapter;
    private ArrayList<Foods> purchasedItems;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private Button backToHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased_products);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders");

        // Ánh xạ UI
        purchaseRecyclerView = findViewById(R.id.purchaseRecyclerView);
        backToHomeButton = findViewById(R.id.btnBackToHome);

        // Khởi tạo danh sách sản phẩm đã mua
        purchasedItems = new ArrayList<>();

        // Khởi tạo adapter với ChangeNumberItemsListener
        cartAdapter = new CartAdapter(purchasedItems, this, new ChangeNumberItemsListener() {
            @Override
            public void change() {
                cartAdapter.notifyDataSetChanged(); // Cập nhật UI khi dữ liệu thay đổi
            }
        }, true);

        // Cấu hình RecyclerView
        purchaseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        purchaseRecyclerView.setAdapter(cartAdapter);

        // Lấy dữ liệu từ Firebase
        fetchPurchasedProducts();

        // Quay lại trang chủ
        backToHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(PurchasedProductsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchPurchasedProducts() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                purchasedItems.clear();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null && order.getUserId().equals(user.getUid())) {
                        purchasedItems.addAll(order.getOrderedItems());
                    }
                }
                if (purchasedItems.isEmpty()) {
                    Toast.makeText(PurchasedProductsActivity.this, "Chưa có sản phẩm nào đã mua!", Toast.LENGTH_SHORT).show();
                }
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi lấy dữ liệu: " + error.getMessage());
                Toast.makeText(PurchasedProductsActivity.this, "Lỗi lấy dữ liệu đơn hàng!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
