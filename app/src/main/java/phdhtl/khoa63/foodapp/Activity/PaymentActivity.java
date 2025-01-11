package phdhtl.khoa63.foodapp.Activity;

import static androidx.core.view.WindowCompat.getInsetsController;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.text.DecimalFormat;

import phdhtl.khoa63.foodapp.Adapter.CartAdapter;
import phdhtl.khoa63.foodapp.Helper.ManagmentCart;
import phdhtl.khoa63.foodapp.R;

public class PaymentActivity extends AppCompatActivity {
    private double totalAmount;
    private ManagmentCart managmentCart;
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView totalAmountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        WindowInsetsControllerCompat windowInsetsController =
                getInsetsController(getWindow(), getWindow().getDecorView());
        ViewCompat.setOnApplyWindowInsetsListener(
                getWindow().getDecorView(),
                (view, windowInsets) -> {

                    windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());
                    return ViewCompat.onApplyWindowInsets(view, windowInsets);
                });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        }

        // Khởi tạo các thành phần giao diện
        totalAmountText = findViewById(R.id.totalAmountText);
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        Button creditCardBtn = findViewById(R.id.creditCardBtn);
        Button payOnDeliveryBtn = findViewById(R.id.payOnDeliveryBtn);
        ImageView backButton = findViewById(R.id.back);

        // Khởi tạo ManagmentCart
        managmentCart = new ManagmentCart(this);

        // Lấy giá trị tổng số tiền từ CartActivity
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0.0);

        // Hiển thị tổng tiền với 2 chữ số sau dấu phẩy
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        totalAmountText.setText("Total Amount: $" + decimalFormat.format(totalAmount));

        // Thiết lập RecyclerView cho giỏ hàng
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(managmentCart.getListCart(), this, () -> {});
        cartRecyclerView.setAdapter(cartAdapter);

        // Thiết lập sự kiện cho các nút thanh toán
        creditCardBtn.setOnClickListener(v -> handleCreditCardPayment());
        payOnDeliveryBtn.setOnClickListener(v -> handleCashOnDelivery());

        // Thiết lập sự kiện nhấp cho nút quay lại
        backButton.setOnClickListener(v -> finish());
    }

    private void handleCreditCardPayment() {
        String paymentMethod = "Credit Card";
        Toast.makeText(this, "Processing credit card payment...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PaymentActivity.this, OrderConfirmationActivity.class);
        intent.putExtra("orderStatus", "success");
        intent.putExtra("paymentMethod", paymentMethod);
        intent.putExtra("totalAmount", totalAmount); // Thêm dòng này để truyền tổng số tiền
        intent.putExtra("productDetails", (Serializable) managmentCart.getListCart());
        startActivity(intent);
        finish();
    }

    private void handleCashOnDelivery() {
        String paymentMethod = "Cash on Delivery";
        Toast.makeText(this, "Cash on delivery selected. Thank you!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PaymentActivity.this, OrderConfirmationActivity.class);
        intent.putExtra("orderStatus", "success");
        intent.putExtra("paymentMethod", paymentMethod);
        intent.putExtra("totalAmount", totalAmount); // Thêm dòng này để truyền tổng số tiền
        intent.putExtra("productDetails", (Serializable) managmentCart.getListCart());
        startActivity(intent);
        finish();
    }



}
