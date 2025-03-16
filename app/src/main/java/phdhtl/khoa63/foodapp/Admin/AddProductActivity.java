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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import phdhtl.khoa63.foodapp.Domain.Foods;
import phdhtl.khoa63.foodapp.R;

public class AddProductActivity extends AppCompatActivity {
    private EditText editTitle, editPrice, editCategory;
    private Button btnChooseImage, btnAddProduct;
    private ImageView productImageView;
    private Uri imageUri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Ánh xạ UI
        editTitle = findViewById(R.id.editTitle);
        editPrice = findViewById(R.id.editPrice);
        editCategory = findViewById(R.id.editCategory); // Nếu có danh mục

        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        productImageView = findViewById(R.id.productImageView);

        // Kết nối Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Foods");
        storageReference = FirebaseStorage.getInstance().getReference("product_images");

        // Chọn ảnh từ thư viện
        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

        // Thêm sản phẩm
        btnAddProduct.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String priceText = editPrice.getText().toString().trim();
            String categoryText = editCategory.getText().toString().trim(); // Danh mục sản phẩm

            if (title.isEmpty() || priceText.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceText);
            int categoryId = categoryText.isEmpty() ? 0 : Integer.parseInt(categoryText); // Chuyển thành số nguyên

            uploadImageAndSaveProduct(title, price, categoryId);
        });
        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> finish());
    }

    // Nhận ảnh đã chọn từ thư viện
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

    // Upload ảnh lên Firebase Storage và lưu sản phẩm vào database
    private void uploadImageAndSaveProduct(String title, double price, int categoryId) {
        String imageId = UUID.randomUUID().toString();
        StorageReference fileRef = storageReference.child(imageId + ".jpg");

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    String productId = databaseReference.push().getKey();

                    // Tạo đối tượng sản phẩm dựa trên `Foods.java`
                    Foods product = new Foods();
                    product.setId(Integer.parseInt(productId)); // Firebase trả về String, cần chuyển sang int
                    product.setTitle(title);
                    product.setPrice(price);
                    product.setImagePath(imageUrl);
                    product.setCategoryId(categoryId);

                    // Lưu vào Firebase Database
                    databaseReference.child(productId).setValue(product)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(AddProductActivity.this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(AddProductActivity.this, "Lỗi khi thêm sản phẩm!", Toast.LENGTH_SHORT).show()
                            );
                }))
                .addOnFailureListener(e ->
                        Toast.makeText(AddProductActivity.this, "Lỗi khi upload ảnh!", Toast.LENGTH_SHORT).show()
                );
    }
}
