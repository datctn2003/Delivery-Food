package phdhtl.khoa63.foodapp.Activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import eightbitlab.com.blurview.RenderScriptBlur;
import phdhtl.khoa63.foodapp.Adapter.CartAdapter;
import phdhtl.khoa63.foodapp.Helper.ChangeNumberItemsListener;
import phdhtl.khoa63.foodapp.Helper.ManagmentCart;
import phdhtl.khoa63.foodapp.R;
import android.widget.Toast;
import android.content.Intent;
import java.io.Serializable;

import phdhtl.khoa63.foodapp.databinding.ActivityCartBinding;
import phdhtl.khoa63.foodapp.databinding.ActivityDetailBinding;

public class CartActivity extends BaseActivity {
    private ActivityCartBinding binding;
    private RecyclerView.Adapter adapter;
    private ManagmentCart managmentCart;
    private double tax;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        managmentCart =new ManagmentCart(this);
        setVariable();
        calculateCart();
        initList();
        setBlurEffect();

        binding.button2.setOnClickListener(v -> {
            // Xử lý thanh toán
            processPayment();
        });
    }
    private void processPayment() {
        // Lấy thông tin thanh toán từ giỏ hàng
        double totalAmount = managmentCart.getTotalFee() + tax + 10; // Bao gồm thuế và phí vận chuyển
        String paymentDetails = "Total Amount: $" + totalAmount;

        // Chuyển đến Activity thanh toán
        Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
        intent.putExtra("totalAmount", totalAmount);  // Chuyển tổng tiền đến Activity thanh toán
        intent.putExtra("cartItems", managmentCart.getListCart()); // Chuyển danh sách sản phẩm
        startActivity(intent);
    }

    private void setBlurEffect() {
        float radius=10f;
        View decorView=(this).getWindow().getDecorView();
        ViewGroup rootView=(ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground=decorView.getBackground();
        // Thiết lập blur cho `blurView`
        binding.blurView.setupWith(rootView,new RenderScriptBlur(this))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius);
        binding.blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.blurView.setClipToOutline(true);
        // Thiết lập blur cho `blurView2`
        binding.blurView2.setupWith(rootView,new RenderScriptBlur(this))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius);
        binding.blurView2.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.blurView2.setClipToOutline(true);
    }

    private void initList() {
        if(managmentCart.getListCart().isEmpty()){
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollview.setVisibility(View.GONE);
        }else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollview.setVisibility(View.VISIBLE);
        }
        binding.cartView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        adapter=new CartAdapter(managmentCart.getListCart(),CartActivity.this, () -> calculateCart());
        binding.cartView.setAdapter(adapter);


    }

    private void setVariable () {
        binding.backBtn.setOnClickListener(v -> finish());
    }
    private void calculateCart(){
        double percentTax = 0.02; // Tỷ lệ thuế 2%
        double delivery = 10; // Phí vận chuyển cố định
        tax = Math.round(managmentCart.getTotalFee() * percentTax * 100.0) / 100.0; // Tính thuế
        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 100; // Tổng tiền
        double itemtotal = Math.round(managmentCart.getTotalFee() * 100) / 100; // Tổng tiền sản phẩm
        binding.totalFeeTxt.setText("$" + itemtotal); // Hiển thị tổng tiền sản phẩm
        binding.taxTxt.setText("$" + tax); // Hiển thị thuế
        binding.deliveryTxt.setText("$" + delivery); // Hiển thị phí vận chuyển
        binding.totalTxt.setText("$" + total); // Hiển thị tổng tiền thanh toán
    }



}

