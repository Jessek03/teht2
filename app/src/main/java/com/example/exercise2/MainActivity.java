package com.example.exercise2;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBarIka, seekBarAktiivisuus;
    private TextView tvIka, tvAktiivisuus, tvTulos;
    private EditText editTextPaino, editTextPituus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        seekBarIka = findViewById(R.id.seekBar);
        tvIka = findViewById(R.id.tvIka);

        seekBarAktiivisuus = findViewById(R.id.seekBar2);
        tvAktiivisuus = findViewById(R.id.tvAktiivisuus);

        editTextPaino = findViewById(R.id.editTextPaino);
        editTextPituus = findViewById(R.id.editTextPituus);
        tvTulos = findViewById(R.id.tvTulos);

        updateIkaText(seekBarIka.getProgress());
        updateAktiivisuusText(seekBarAktiivisuus.getProgress());
        laskeJaPaivi();

        seekBarIka.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                updateIkaText(progress);
                laskeJaPaivi();
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {}
            @Override public void onStopTrackingTouch(SeekBar sb) {}
        });

        seekBarAktiivisuus.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                updateAktiivisuusText(progress);
                laskeJaPaivi();
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {}
            @Override public void onStopTrackingTouch(SeekBar sb) {}
        });

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                laskeJaPaivi();
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        editTextPaino.addTextChangedListener(watcher);
        editTextPituus.addTextChangedListener(watcher);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Aktiivisuustaso), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateIkaText(int ika) {
        tvIka.setText("Ikä: " + ika);
    }

    private void updateAktiivisuusText(int progress) {
        double kerroin = aktiivisuusKerroin(progress);
        // Näytä myös taso sanana:
        String taso;
        if (kerroin == 1.2) taso = "matala";
        else if (kerroin == 1.5) taso = "keski";
        else taso = "korkea";
        tvAktiivisuus.setText("Aktiivisuus: " + taso + " (" + kerroin + ")");
    }

    private double aktiivisuusKerroin(int progress) {
        int max = seekBarAktiivisuus.getMax();

        if (max == 2) {
            if (progress <= 0) return 1.2;
            if (progress == 1) return 1.5;
            return 1.8;
        }

        double p = (max == 0) ? 0 : (progress / (double) max);
        if (p < 1.0 / 3.0) return 1.2;
        else if (p < 2.0 / 3.0) return 1.5;
        else return 1.8;
    }

    private void laskeJaPaivi() {
        double paino = parseDoubleSafe(editTextPaino.getText().toString());
        double pituus = parseDoubleSafe(editTextPituus.getText().toString());
        int ika = seekBarIka.getProgress();
        double aktiivisuus = aktiivisuusKerroin(seekBarAktiivisuus.getProgress());

        double perus = 10.0 * paino + 6.25 * pituus - 5.0 * ika + 5.0;

        double tulos = perus * aktiivisuus;

        tvTulos.setText(String.format("Tulos: %.0f", tulos));
    }

    private double parseDoubleSafe(String s) {
        if (s == null) return 0.0;
        s = s.trim().replace(",", ".");
        if (s.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }
}