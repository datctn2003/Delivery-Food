package phdhtl.khoa63.foodapp.Admin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.UUID;
import phdhtl.khoa63.foodapp.Domain.Foods;
import phdhtl.khoa63.foodapp.R;

public class EditProductActivity extends AppCompatActivity {
    private EditText editTitle, editPrice, editCategory;
    private ImageView productImageView;
    private Button btnChooseImage, btnSave;
    private Uri imageUri; // Ảnh mới (nếu chọn)
    private String imagePath; // Ảnh cũ từ Firebase

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String productId;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Ánh xạ UI
        editTitle = findViewById(R.id.editTitle);
        editPrice = findViewById(R.id.editPrice);
        editCategory = findViewById(R.id.editCategory);
        productImageView = findViewById(R.id.productImageView);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);

        // Lấy productId từ Intent
        productId = getIntent().getStringExtra("productId");

        // Kết nối Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Foods").child(productId);
        storageReference = FirebaseStorage.getInstance().getReference("product_images");

        // Lấy dữ liệu sản phẩm từ Firebase
        loadProductData();

        // Chọn ảnh mới từ thư viện
        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

        // Lưu cập nhật sản phẩm
        btnSave.setOnClickListener(v -> updateProduct());


        backButton = findViewById(R.id.backout);
        backButton.setOnClickListener(v -> finish());
    }

    // Load dữ liệu sản phẩm từ Firebase
    private void loadProductData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Foods product = snapshot.getValue(Foods.class);
                if (product != null) {
                    editTitle.setText(product.getTitle());
                    editPrice.setText(String.valueOf(product.getPrice()));
                    editCategory.setText(String.valueOf(product.getCategoryId()));  // ✅ Lấy categoryId từ Firebase
                    imagePath = product.getImagePath(); // ✅ Lưu ảnh cũ

                    // Hiển thị ảnh sản phẩm
                    if (imagePath != null && !imagePath.isEmpty()) {
                        Glide.with(EditProductActivity.this).load(imagePath).into(productImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProductActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Nhận ảnh mới đã chọn từ thư viện
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                productImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Cập nhật sản phẩm lên Firebase
    private void updateProduct() {
        String newTitle = editTitle.getText().toString().trim();
        String newPriceText = editPrice.getText().toString().trim();
        String newCategory = editCategory.getText().toString().trim();

        if (newTitle.isEmpty() || newPriceText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        double newPrice = Double.parseDouble(newPriceText);
        int newCategoryId = Integer.parseInt(newCategory);  // Chuyển categoryId về dạng số nguyên

        if (imageUri != null) {
            // Nếu có ảnh mới => upload lên Firebase Storage
            uploadNewImage(newTitle, newPrice, newCategoryId);
        } else {
            // Nếu không chọn ảnh mới => Cập nhật thông tin sản phẩm với ảnh cũ
            saveProductData(newTitle, newPrice, newCategoryId, imagePath);
        }
    }

    // Upload ảnh mới lên Firebase Storage
    private void uploadNewImage(String title, double price, int categoryId) {
        String imageId = UUID.randomUUID().toString();
        StorageReference fileRef = storageReference.child(imageId + ".jpg");

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String newImagePath = uri.toString();
                    saveProductData(title, price, categoryId, newImagePath);
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProductActivity.this, "Lỗi khi upload ảnh!", Toast.LENGTH_SHORT).show();
                });
    }

    // Lưu thông tin sản phẩm mới vào Firebase
    private void saveProductData(String title, double price, int categoryId, String imagePath) {
        Foods updatedProduct = new Foods();
        updatedProduct.setId(Integer.parseInt(productId));  // ✅ Đảm bảo id là số nguyên
        updatedProduct.setTitle(title);
        updatedProduct.setPrice(price);
        updatedProduct.setCategoryId(categoryId);
        updatedProduct.setImagePath(imagePath);

        databaseReference.setValue(updatedProduct)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProductActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProductActivity.this, "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show();
                });
    }
}
