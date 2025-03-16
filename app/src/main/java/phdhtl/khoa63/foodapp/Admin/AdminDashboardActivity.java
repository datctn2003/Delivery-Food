package phdhtl.khoa63.foodapp.Admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import phdhtl.khoa63.foodapp.Activity.MainActivity;
import phdhtl.khoa63.foodapp.R;

public class AdminDashboardActivity extends AppCompatActivity {
    private Button btnManageProducts, btnManageOrders, btnManageUsers;
    private TextView txtTotalProducts, txtTotalOrders, txtTotalUsers;
    private PieChart pieChart;
    private ImageView imgLogout;
    private DatabaseReference databaseReference;
    private long totalProducts = 0, totalOrders = 0, totalUsers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Ánh xạ View
        btnManageProducts = findViewById(R.id.btnManageProducts);
        btnManageOrders = findViewById(R.id.btnManageOrders);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        txtTotalProducts = findViewById(R.id.txtTotalProducts);
        txtTotalOrders = findViewById(R.id.txtTotalOrders);
        txtTotalUsers = findViewById(R.id.txtTotalUsers);
        imgLogout = findViewById(R.id.logout);
        pieChart = findViewById(R.id.pieChart);

        // Xử lý sự kiện
        btnManageProducts.setOnClickListener(v -> startActivity(new Intent(this, AdminProductActivity.class)));
        btnManageOrders.setOnClickListener(v -> startActivity(new Intent(this, AdminOrderActivity.class)));
        btnManageUsers.setOnClickListener(v -> startActivity(new Intent(this, AdminUserActivity.class)));

        imgLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Load dữ liệu từ Firebase
        loadDashboardStatistics();
    }

    private void loadDashboardStatistics() {
        // Lấy tổng số sản phẩm
        databaseReference.child("Foods").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalProducts = snapshot.getChildrenCount();
                txtTotalProducts.setText("Products: " + totalProducts);
                updatePieChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Lấy tổng số đơn hàng
        databaseReference.child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalOrders = snapshot.getChildrenCount();
                txtTotalOrders.setText("Orders: " + totalOrders);
                updatePieChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Lấy tổng số người dùng
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalUsers = snapshot.getChildrenCount();
                txtTotalUsers.setText("Users: " + totalUsers);
                updatePieChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updatePieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // Kiểm tra dữ liệu trước khi cập nhật biểu đồ
        if (totalProducts > 0) entries.add(new PieEntry(totalProducts, "Products"));
        if (totalOrders > 0) entries.add(new PieEntry(totalOrders, "Orders"));
        if (totalUsers > 0) entries.add(new PieEntry(totalUsers, "Users"));

        if (entries.isEmpty()) {
            pieChart.clear();
            pieChart.invalidate();
            return;
        }

        // Cấu hình dataset
        PieDataSet dataSet = new PieDataSet(entries, "Statistics");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(16f);
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // Cấu hình PieChart
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setUsePercentValues(false);
        pieChart.setDrawEntryLabels(true);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setDescription(null);
        pieChart.invalidate();

        // Cấu hình Legend
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setTextSize(14f);
        legend.setWordWrapEnabled(true);
        legend.setYOffset(10f);
    }
}
