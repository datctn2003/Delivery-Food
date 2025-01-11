package phdhtl.khoa63.foodapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import phdhtl.khoa63.foodapp.R;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText editTextEmail, getEditTextPassword, editTextUsername;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        getEditTextPassword = findViewById(R.id.password);
        editTextUsername = findViewById(R.id.username);  // Lấy trường tên người dùng
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.processBar);
        textView = findViewById(R.id.loginNow);

        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        buttonReg.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = String.valueOf(editTextEmail.getText());
            String password = String.valueOf(getEditTextPassword.getText());
            String username = String.valueOf(editTextUsername.getText()); // Lấy tên người dùng

            // Kiểm tra các trường thông tin
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(RegisterActivity.this, "Hãy điền Tên người dùng", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(RegisterActivity.this, "Hãy điền Email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "Hãy điền Password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Mật khẩu tối thiểu 6 kí tự", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            // Tạo tài khoản mới
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                // Lưu tên người dùng vào SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("username", username);
                                editor.apply();

                                // Thông báo thành công
                                Toast.makeText(RegisterActivity.this, "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();

                                // Chuyển đến MainActivity
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Hiển thị lỗi
                                String error = task.getException() != null ? task.getException().getMessage() : "Tạo tài khoản thất bại";
                                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}
