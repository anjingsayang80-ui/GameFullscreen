package id.gamefullscreen;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity {
    private LinearLayout appList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHome();
    }

    private TextView text(String value, int size) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(Color.WHITE);
        t.setGravity(Gravity.CENTER);
        t.setPadding(18, 12, 18, 12);
        return t;
    }

    private Button button(String label, Runnable action) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setOnClickListener(v -> action.run());
        return b;
    }

    private void showHome() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 20, 18, 12);
        root.setBackgroundColor(0xFF10131B);

        root.addView(text("GAME FULLSCREEN", 25));
        root.addView(text("Pilih game untuk langsung membukanya.", 16));
        root.addView(text("Catatan: tanpa izin khusus Android, aplikasi ini tidak dapat memblokir tombol Beranda/Kembali atau panel notifikasi di game lain. Gunakan fitur Sematkan layar Android bila tersedia.", 14));

        root.addView(button("Cara mengaktifkan Sematkan layar", this::openPinningSettings));
        root.addView(button("Muat ulang daftar aplikasi", this::loadApps));

        TextView heading = text("DAFTAR GAME / APLIKASI", 18);
        root.addView(heading);

        ScrollView scroll = new ScrollView(this);
        appList = new LinearLayout(this);
        appList.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(appList);
        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(root);
        loadApps();
    }

    private void openPinningSettings() {
        try {
            startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
        } catch (Exception e) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
        Toast.makeText(this,
                "Cari menu Sematkan layar atau Screen pinning. Nama menu bisa berbeda.",
                Toast.LENGTH_LONG).show();
    }

    private void loadApps() {
        if (appList == null) return;
        appList.removeAllViews();

        Intent query = new Intent(Intent.ACTION_MAIN, null);
        query.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> apps = pm.queryIntentActivities(query, 0);

        Collections.sort(apps, new Comparator<ResolveInfo>() {
            @Override public int compare(ResolveInfo a, ResolveInfo b) {
                return String.valueOf(a.loadLabel(pm)).compareToIgnoreCase(
                        String.valueOf(b.loadLabel(pm)));
            }
        });

        int count = 0;
        for (ResolveInfo info : apps) {
            if (info.activityInfo.packageName.equals(getPackageName())) continue;
            String label = String.valueOf(info.loadLabel(pm));
            String packageName = info.activityInfo.packageName;

            Button item = button("▶  " + label, () -> launchApp(packageName, label));
            item.setGravity(Gravity.CENTER_VERTICAL);
            appList.addView(item);
            count++;
        }

        if (count == 0) {
            appList.addView(text("Tidak ada aplikasi yang bisa dibuka ditemukan.", 14));
        }
    }

    private void launchApp(String packageName, String label) {
        try {
            Intent launch = getPackageManager().getLaunchIntentForPackage(packageName);
            if (launch == null) {
                Toast.makeText(this, "Game tidak bisa dibuka.", Toast.LENGTH_SHORT).show();
                return;
            }
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launch);
        } catch (Exception e) {
            Toast.makeText(this, "Gagal membuka " + label, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appList != null) loadApps();
    }
}
