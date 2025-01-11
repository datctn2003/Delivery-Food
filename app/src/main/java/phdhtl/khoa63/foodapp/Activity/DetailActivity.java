package phdhtl.khoa63.foodapp.Activity;

import static androidx.core.view.WindowCompat.getInsetsController;
import static java.util.ResourceBundle.getBundle;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bumptech.glide.Glide;

import eightbitlab.com.blurview.RenderScriptBlur;
import phdhtl.khoa63.foodapp.Domain.Foods;
import phdhtl.khoa63.foodapp.Helper.ManagmentCart;
import phdhtl.khoa63.foodapp.R;
import phdhtl.khoa63.foodapp.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private Foods object;
    private int num=1;
    private ManagmentCart managmentCart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
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
        setContentView(binding.getRoot());
        managmentCart= new ManagmentCart(this);
        getBundleExtra();
        setVariable();
        setBlurEffect();


    }


    private void setBlurEffect() {
        float radius=10f;
        View decorView=(this).getWindow().getDecorView();
        ViewGroup rootView=(ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground=decorView.getBackground();
        binding.blurView.setupWith(rootView,new RenderScriptBlur(this))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius);
        binding.blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.blurView.setClipToOutline(true);
        binding.blurView2.setupWith(rootView,new RenderScriptBlur(this))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius);
        binding.blurView2.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.blurView2.setClipToOutline(true);
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
        Glide.with(DetailActivity.this)
                .load(object.getImagePath())
                .into(binding.pic);
        binding.priceTxt.setText("$"+object.getPrice());
        binding.titleTxt.setText(object.getTitle());
        binding.decriptionTxt.setText(object.getDescription());
        binding.ratingTxt.setText(object.getStar()+"Rating");
        binding.ratingBar.setRating((float) object.getStar());
        binding.totalTxt.setText((num*object.getPrice())+"$");
        binding.plusBtn.setOnClickListener(v -> {
            num=num+1;
            binding.numTxt.setText(num+"");
            binding.totalTxt.setText((num*object.getPrice())+"$");
        });
        binding.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(num>1){
                    num=num-1;
                    binding.numTxt.setText(num+"");
                    binding.totalTxt.setText(num*object.getPrice()+"$");
                }
            }
        });
        binding.addBtn.setOnClickListener(v -> {
            object.setNumberInCart(num);
            managmentCart.insertFood(object);
        });

    }

    private void getBundleExtra() {
        object= (Foods) getIntent().getSerializableExtra("object");

    }
}