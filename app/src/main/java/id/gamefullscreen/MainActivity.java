package id.gamefullscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private boolean demoFullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHome();
    }

    private TextView text(String value, int size) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(0xFFFFFFFF);
        t.setGravity(Gravity.CENTER);
        t.setPadding(24, 18, 24, 18);
        return t;
    }

    private Button button(String label, Runnable action) {
        Button b = new Button(this);
        b.setText(label);
        b.setOnClickListener(v -> action.run());
        return b;
    }

    private void showHome() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(24, 28, 24, 28);
        root.setBackgroundColor(0xFF10131B);

        root.addView(text("GAME FULLSCREEN", 26));
        root.addView(text("Asisten layar penuh untuk bermain game", 16));
        root.addView(text("Penting: Android membatasi aplikasi biasa agar tidak bisa menyembunyikan status bar dan tombol navigasi di atas aplikasi lain. Aplikasi ini tidak akan mengubah MusikPlayer atau aplikasi game kamu.", 15));

        root.addView(button("Uji layar penuh di aplikasi ini", () -> {
            demoFullscreen = !demoFullscreen;
            if (demoFullscreen) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }));

        root.addView(button("Buka pengaturan Game Mode", () -> {
            try {
                startActivity(new Intent("android.settings.GAME_SETTINGS"));
            } catch (Exception e) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        }));

        root.addView(button("Pengaturan layar penuh Android", () ->
                startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS))));

        root.addView(text("Jika ponsel menyediakan Game Mode atau pengaturan aplikasi layar penuh, aktifkan khusus untuk game yang kamu pilih.", 14));
        setContentView(root);
    }
}
