package com.lab14_10;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword, editTextRePassword;
    private Button buttonRegister;
    private com.lab14_10.DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_layout);

        // Kết nối các View từ layout
        editTextEmail = findViewById(R.id.id_sign);
        editTextPassword = findViewById(R.id.st_password);
        editTextRePassword = findViewById(R.id.end_password);
        buttonRegister = findViewById(R.id.b_login);
        Button buttonBackToLogin = findViewById(R.id.re_login);

        // Khởi tạo DatabaseHelper và mở cơ sở dữ liệu
        databaseHelper = new com.lab14_10.DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();

        // Sự kiện khi nhấn nút đăng ký
        buttonRegister.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String repass = editTextRePassword.getText().toString().trim();

            // Kiểm tra nhập liệu
            if (email.isEmpty() || password.isEmpty() || repass.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(repass)) {
                Toast.makeText(SignUpActivity.this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
            } else {
                // Kiểm tra xem tài khoản đã tồn tại chưa
                Cursor cursor = null;
                try {
                    cursor = database.rawQuery("SELECT * FROM " + com.lab14_10.DatabaseHelper.TABLE_USER + " WHERE " + com.lab14_10.DatabaseHelper.COLUMN_EMAIL + " = ?", new String[]{email});
                    if (cursor.getCount() > 0) {
                        Toast.makeText(SignUpActivity.this, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                    } else {
                        // Lưu tài khoản mới vào database
                        ContentValues values = new ContentValues();
                        values.put(com.lab14_10.DatabaseHelper.COLUMN_EMAIL, email);
                        values.put(com.lab14_10.DatabaseHelper.COLUMN_PASSWORD, password);
                        values.put(com.lab14_10.DatabaseHelper.COLUMN_REPASSWORD, repass);

                        long result = database.insert(com.lab14_10.DatabaseHelper.TABLE_USER, null, values);
                        if (result == -1) {
                            Toast.makeText(SignUpActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            // Chuyển sang màn hình đăng nhập
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Xóa bộ nhớ Activity này trong ngăn xếp
                        }
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        });
        // Sự kiện khi nhấn nút quay lại đăng nhập
        buttonBackToLogin.setOnClickListener(v -> {
            // Chuyển về màn hình đăng nhập
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Kết thúc hoạt động hiện tại để không quay lại trang đăng ký khi nhấn nút quay lại
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
