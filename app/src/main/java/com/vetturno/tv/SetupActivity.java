package com.vetturno.tv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SetupActivity extends AppCompatActivity {

    private static final String PREFS = "vetturno_prefs";
    private static final String KEY_CODE = "sala_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If code already saved, go directly to sala
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String savedCode = prefs.getString(KEY_CODE, null);
        if (!TextUtils.isEmpty(savedCode)) {
            launchSala(savedCode);
            return;
        }

        setContentView(R.layout.activity_setup);

        EditText etCode = findViewById(R.id.et_code);
        Button btnStart = findViewById(R.id.btn_start);
        TextView tvClear = findViewById(R.id.tv_clear);

        btnStart.setOnClickListener(v -> {
            String code = etCode.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                etCode.setError("Ingresá el código de sala");
                return;
            }
            prefs.edit().putString(KEY_CODE, code).apply();
            launchSala(code);
        });

        tvClear.setOnClickListener(v -> {
            prefs.edit().remove(KEY_CODE).apply();
            etCode.setText("");
            etCode.requestFocus();
        });
    }

    private void launchSala(String code) {
        Intent intent = new Intent(this, SalaActivity.class);
        intent.putExtra("sala_code", code);
        startActivity(intent);
        finish();
    }
}
