package phdhtl.khoa63.foodapp.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import phdhtl.khoa63.foodapp.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<User> userList;
    private DatabaseReference databaseReference;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("Users");
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText("Tên: " + user.getName());
        holder.userEmail.setText("Email: " + user.getEmail());
        holder.userRole.setText("Quyền: " + user.getRole());

        // Cập nhật trạng thái nút khóa/mở khóa
        if (user.isBlocked()) {
            holder.btnBlockUnblock.setText("Mở khóa");
            holder.btnBlockUnblock.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.btnBlockUnblock.setText("Khóa");
            holder.btnBlockUnblock.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        // Xử lý sự kiện bấm nút khóa/mở khóa
        holder.btnBlockUnblock.setOnClickListener(v -> {
            boolean newStatus = !user.isBlocked(); // Đảo trạng thái
            databaseReference.child(user.getUserId()).child("blocked").setValue(newStatus)
                    .addOnSuccessListener(aVoid -> {
                        user.setBlocked(newStatus); // Cập nhật trạng thái trong danh sách
                        notifyItemChanged(position); // Cập nhật giao diện
                        Toast.makeText(context, newStatus ? "Đã khóa người dùng" : "Đã mở khóa", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Lỗi cập nhật trạng thái!", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userEmail, userRole;
        Button btnBlockUnblock;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userEmail = itemView.findViewById(R.id.userEmail);
            userRole = itemView.findViewById(R.id.userRole);
            btnBlockUnblock = itemView.findViewById(R.id.btnBlockUnblock);
        }
    }
}
