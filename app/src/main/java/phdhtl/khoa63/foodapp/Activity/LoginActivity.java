package phdhtl.khoa63.foodapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import phdhtl.khoa63.foodapp.Admin.AdminDashboardActivity;
import phdhtl.khoa63.foodapp.R;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText editTextEmail,editTextPassword ;
    Button buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        editTextEmail =findViewById(R.id.email);
        editTextPassword =findViewById(R.id.password);
        buttonLogin=findViewById(R.id.btn_login);
        progressBar= findViewById(R.id.processBar);
        textView =findViewById(R.id.RegisterNow);
        databaseReference = FirebaseDatabase.getInstance().getReference("Admins");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Hãy điền Email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Hãy điền Password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    checkUserRole(userId, email); // Kiểm tra role
                                }
                            } else {
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định!";
                                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


    }
    private void checkUserRole(String userId, String email) {
        Log.d("Firebase", "Đang kiểm tra role của UID: " + userId);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Admins").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Nếu tài khoản tồn tại trong bảng Admins, chuyển đến Admin Panel
                    Log.d("Firebase", "Tài khoản là Admin");
                    startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                } else {
                    // Nếu không có trong Admins, mặc định là User
                    Log.d("Firebase", "Tài khoản là User");
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi Firebase: " + error.getMessage());
                Toast.makeText(LoginActivity.this, "Lỗi kiểm tra quyền Admin!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
