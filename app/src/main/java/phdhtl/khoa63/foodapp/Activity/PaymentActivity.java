package phdhtl.khoa63.foodapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import phdhtl.khoa63.foodapp.Adapter.CartAdapter;
import phdhtl.khoa63.foodapp.Admin.Order;
import phdhtl.khoa63.foodapp.Domain.Category;
import phdhtl.khoa63.foodapp.Helper.ManagmentCart;
import phdhtl.khoa63.foodapp.Domain.Foods;
import phdhtl.khoa63.foodapp.R;

public class PaymentActivity extends AppCompatActivity {
    private double totalAmount;
    private ManagmentCart managmentCart;
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView totalAmountText;
    private Button zaloPayBtn, momoPayBtn;
    private ImageView backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Ánh xạ UI
        totalAmountText = findViewById(R.id.totalAmountText);
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        zaloPayBtn = findViewById(R.id.zaloPayBtn);
        momoPayBtn = findViewById(R.id.momoPayBtn);
        backButton = findViewById(R.id.back);

        // Kiểm tra xem các thành phần UI có bị null không
        if (totalAmountText == null || cartRecyclerView == null || zaloPayBtn == null || momoPayBtn == null || backButton == null) {
            Log.e("PaymentActivity", "Một trong các View bị null! Kiểm tra ID trong XML.");
            return;
        }

        // Quản lý giỏ hàng
        managmentCart = new ManagmentCart(this);
        List<Foods> cartList = managmentCart.getListCart();

        // Nhận tổng tiền từ Intent
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0.0);
        DecimalFormat decimalFormat = new DecimalFormat("$#,###.00");
        totalAmountText.setText("Total Amount: " + decimalFormat.format(totalAmount));
        // Hiển thị danh sách sản phẩm trong giỏ hàng
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter((ArrayList<Foods>) cartList, this, () -> {}, false);

        cartRecyclerView.setAdapter(cartAdapter);

        // Xử lý khi giỏ hàng trống
        if (cartList.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống, hãy thêm sản phẩm!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Sự kiện khi bấm nút thanh toán
        zaloPayBtn.setOnClickListener(v -> handleZaloPayPayment());
        momoPayBtn.setOnClickListener(v -> handleMomoPayment());

        // Nút quay lại
        backButton.setOnClickListener(v -> finish());
    }

    private void handleZaloPayPayment() {
        Toast.makeText(this, "Đang chuyển hướng đến ZaloPay...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_VIEW);
       // intent.setData(Uri.parse("https://sandbox.zalopay.vn/")); // Chuyển hướng ZaloPay
        startActivity(intent);

        // Gọi xử lý thanh toán
        processPayment("ZaloPay");
    }

    private void handleMomoPayment() {
        Toast.makeText(this, "Đang chuyển hướng đến MoMo...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_VIEW);
       // intent.setData(Uri.parse("https://momo.vn/")); // Chuyển hướng MoMo
        startActivity(intent);

        // Gọi xử lý thanh toán
     processPayment("MoMo");
    }

    private void processPayment(String method) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Foods> cartItems = managmentCart.getListCart();
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderId = FirebaseDatabase.getInstance().getReference("Orders").push().getKey();
        if (orderId == null) return;

        Order order = new Order(orderId, user.getUid(), cartItems, totalAmount, method, "Đã thanh toán");

        FirebaseDatabase.getInstance().getReference("Orders").child(orderId).setValue(order)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PaymentActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();

                    managmentCart.clearCart(); // ✅ Xóa giỏ hàng sau khi thanh toán

                    Intent intent = new Intent(PaymentActivity.this, PurchasedProductsActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(PaymentActivity.this, "Lỗi khi lưu đơn hàng!", Toast.LENGTH_SHORT).show());
    }


}
