package phdhtl.khoa63.foodapp.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.RenderScriptBlur;
import phdhtl.khoa63.foodapp.Adapter.CartAdapter;
import phdhtl.khoa63.foodapp.Domain.Foods;
import phdhtl.khoa63.foodapp.Helper.ManagmentCart;
import phdhtl.khoa63.foodapp.R;
import phdhtl.khoa63.foodapp.databinding.ActivityCartBinding;

public class CartActivity extends AppCompatActivity {
    private ActivityCartBinding binding;
    private CartAdapter adapter;
    private ManagmentCart managmentCart;
    private double tax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        managmentCart = new ManagmentCart(this);
        setVariable();
        calculateCart();
        initList();
        setBlurEffect();

        // Xử lý khi bấm nút "Thanh toán ngay"
        binding.button2.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        // Lấy tổng tiền
        double totalAmount = managmentCart.getTotalFee() + tax + 10; // Bao gồm thuế & phí ship

        // Kiểm tra nếu giỏ hàng rỗng
        if (managmentCart.getListCart().isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống! Vui lòng thêm sản phẩm.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển đến trang thanh toán
        Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
        intent.putExtra("totalAmount", totalAmount);
        intent.putExtra("cartItems", new ArrayList<>(managmentCart.getListCart())); // Ép về ArrayList
        startActivity(intent);
    }

    private void setBlurEffect() {
        float radius = 10f;
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        binding.blurView.setupWith(rootView, new RenderScriptBlur(this))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius);
        binding.blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.blurView.setClipToOutline(true);

        binding.blurView2.setupWith(rootView, new RenderScriptBlur(this))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius);
        binding.blurView2.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.blurView2.setClipToOutline(true);
    }

    private void initList() {
        if (managmentCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollview.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollview.setVisibility(View.VISIBLE);
        }

        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new CartAdapter(new ArrayList<>(managmentCart.getListCart()), this, () -> calculateCart(), false);
        binding.cartView.setAdapter(adapter);
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void calculateCart() {
        double percentTax = 0.02; // Thuế 2%
        double delivery = 10; // Phí vận chuyển cố định
        tax = Math.round(managmentCart.getTotalFee() * percentTax * 100.0) / 100.0; // Tính thuế
        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 100; // Tổng tiền
        double itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100; // Tổng tiền sản phẩm

        binding.totalFeeTxt.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);
    }
}
