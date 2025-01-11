package phdhtl.khoa63.foodapp.Activity;

import static androidx.core.view.WindowCompat.getInsetsController;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import phdhtl.khoa63.foodapp.Adapter.CartAdapter;
import phdhtl.khoa63.foodapp.Domain.Foods;
import phdhtl.khoa63.foodapp.Helper.ManagmentCart;
import phdhtl.khoa63.foodapp.R;

public class OrderConfirmationActivity extends AppCompatActivity {
    private TextView orderSuccessMsg, paymentMethodTxt, totalAmountText;
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private Button backToMainButton;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
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
        orderSuccessMsg = findViewById(R.id.orderSuccessMsg);
        paymentMethodTxt = findViewById(R.id.paymentMethodTxt);
        totalAmountText = findViewById(R.id.totalAmountText);
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        backToMainButton = findViewById(R.id.backToMainButton);
        ImageView back = findViewById(R.id.backde);

        // Lấy dữ liệu từ Intent
        String paymentMethod = getIntent().getStringExtra("paymentMethod");
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0.0);

        // Kiểm tra và lấy danh sách sản phẩm
        ArrayList<Foods> productDetails = (ArrayList<Foods>) getIntent().getSerializableExtra("productDetails");
        if (productDetails == null) {
            productDetails = new ArrayList<>(); // Khởi tạo danh sách trống nếu null
        }

        // Hiển thị tổng tiền với 2 chữ số sau dấu phẩy
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        totalAmountText.setText("Total Amount: $" + decimalFormat.format(totalAmount)); // Hiển thị tổng tiền

        // Hiển thị thông tin đặt hàng
        orderSuccessMsg.setText("Order Successful!");
        paymentMethodTxt.setText(paymentMethod != null && !paymentMethod.isEmpty()
                ? "Payment Method: " + paymentMethod
                : "Payment Method: Not specified");

        // Thiết lập RecyclerView cho giỏ hàng
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(productDetails, this, () -> {});
        cartRecyclerView.setAdapter(cartAdapter);

        // Sự kiện quay lại
        back.setOnClickListener(v -> finish());
        backToMainButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

}

