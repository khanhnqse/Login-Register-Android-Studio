package com.lab14_10;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonRegister; // Khai báo nút đăng kí tài khoản
    private com.lab14_10.DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
       // Kết nối các View từ layout
        editTextUsername = findViewById(R.id.log_username);
        editTextPassword = findViewById(R.id.Log_password);
        buttonLogin = findViewById(R.id.login);
        buttonRegister = findViewById(R.id.signin); // Kết nối nút đăng kí tài khoản

        // Khởi tạo DatabaseHelper và mở cơ sở dữ liệu
        databaseHelper = new com.lab14_10.DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();


        // Thiết lập sự kiện khi nhấn nút Đăng kí tài khoản
        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.lab14_10.SignUpActivity.class);
            startActivity(intent);
        });

        // Thiết lập sự kiện khi nhấn nút Đăng nhập
        buttonLogin.setOnClickListener(v -> {
            String email = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                // Kiểm tra tài khoản trong cơ sở dữ liệu
                Cursor cursor = database.rawQuery("SELECT * FROM Tbl_user WHERE Email = ? AND pass = ?", new String[]{email, password});
                if (cursor.getCount() > 0) {
                    Toast.makeText(MainActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    // Chuyển sang màn hình chào mừng
                    Intent intent = new Intent(MainActivity.this, com.lab14_10.WellcomActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Sai thông tin đăng nhập", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            }
        });

        // Kích hoạt nút "Đăng kí tài khoản"
        buttonRegister.setEnabled(true);

        // Thiết lập lắng nghe sự kiện thay đổi văn bản để kích hoạt nút "Đăng nhập"
        editTextUsername.addTextChangedListener(new SimpleTextWatcher());
        editTextPassword.addTextChangedListener(new SimpleTextWatcher());
    }

    // Lớp lắng nghe sự kiện thay đổi văn bản đơn giản
    private class SimpleTextWatcher implements android.text.TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Kiểm tra nếu cả hai trường đều có văn bản thì kích hoạt nút "Đăng nhập"
            buttonLogin.setEnabled(!editTextUsername.getText().toString().trim().isEmpty() &&
                    !editTextPassword.getText().toString().trim().isEmpty());
        }

        @Override
        public void afterTextChanged(android.text.Editable s) {
        }
    }
}
