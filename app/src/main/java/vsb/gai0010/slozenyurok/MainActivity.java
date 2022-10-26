package vsb.gai0010.slozenyurok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final int START_SETTINGS = 1;
    private final String[] names = new String[] {
            "Uroky",
            "Vklad"
    };
    private final int[] colors = new int[] {
            ColorTemplate.rgb("#ff6600"),
            ColorTemplate.rgb("#22bc22")
    };
    private boolean isPie = true;

    private EditText vkladEditText;
    private EditText sazbaEditText;
    private EditText dobaEditText;
    private PieChart pieChart;
    private BarChart barChart;
    private TextView descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        this.vkladEditText = this.findViewById(R.id.vkladEditText);
        this.sazbaEditText = this.findViewById(R.id.sazbaEditText);
        this.dobaEditText = this.findViewById(R.id.dobaEditText);
        this.descriptionTextView = this.findViewById(R.id.descriptionEditText);

        if (isPie) {
            pieInit();
        } else {
            barInit();
        }

        String key = getString(R.string.history_data);
        SharedPreferences preferences = getSharedPreferences(key, Context.MODE_PRIVATE);

        int vklad = preferences.getInt(key + "_vklad", 0);
        int sazba = preferences.getInt(key + "_sazba", 0);
        int doba = preferences.getInt(key + "_doba", 0);
        int out = preferences.getInt(key + "_out", 0);

        vkladEditText.setText(String.valueOf(vklad));
        sazbaEditText.setText(String.valueOf(sazba));
        dobaEditText.setText(String.valueOf(doba));

        setData(vklad, out);
    }

    public void calculate(View view) {
        int vklad = NumberUtils.parseInt(this.vkladEditText.getText().toString());
        int sazba = NumberUtils.parseInt(this.sazbaEditText.getText().toString());
        int doba = NumberUtils.parseInt(this.dobaEditText.getText().toString());

        if (vklad <= 0 || sazba <= 0 || doba <= 0) {
            this.setData(100, 200);
        }

        float nasporene = vklad;
        for (int i = 0; i < doba; i++) {
            float urok = nasporene * (sazba / (float)100);
            nasporene += urok;
        }

        int nasporeneRounded = Math.round(nasporene);
        setData(vklad, nasporeneRounded);
        saveData(vklad, sazba, doba, nasporeneRounded);
    }

    private void pieInit() {
        this.pieChart = this.findViewById(R.id.pieChart);

        float holeRadius = 30f;
        this.pieChart.setUsePercentValues(true);
        this.pieChart.setHoleRadius(holeRadius);
        this.pieChart.setTransparentCircleRadius(holeRadius + 5);
        this.pieChart.setDrawCenterText(true);
        this.pieChart.setHighlightPerTapEnabled(true);
        this.pieChart.setCenterTextOffset(0, -20);

        makeLegend(pieChart.getLegend());

        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.getDescription().setEnabled(false);
    }

    private void barInit() {
        this.barChart = findViewById(R.id.barChart);

        barChart.getDescription().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setEnabled(false);
        xAxis.setDrawGridLines(false);
        barChart.getAxisLeft().setEnabled(false);

        makeLegend(barChart.getLegend());
    }

    private void makeLegend(Legend legend) {
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);
    }

    private void setDataPie(int vklad, int nasporene) {
        PieEntry[] values = new PieEntry[] {
                new PieEntry(nasporene - vklad, names[0]),
                new PieEntry(vklad, names[1]),
        };

        PieDataSet dataSet = new PieDataSet(Arrays.asList(values), "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);

        pieChart.invalidate();
        updateDescription(nasporene, nasporene - vklad);
    }

    private void setDataBar(int vklad, int nasporene) {
        BarEntry[] values = new BarEntry[] {
                new BarEntry(0, nasporene - vklad),
                new BarEntry(1, vklad)
        };

        BarDataSet dataSet = new BarDataSet(Arrays.asList(values), "");

        dataSet.setStackLabels(names);
        dataSet.setColors(colors);
        dataSet.setLabel("Uroky/Vklady");

        BarData data = new BarData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        barChart.setData(data);

        barChart.invalidate();
    }

    private void setData(int vklad, int nasporene) {
        if (vklad == 0 || nasporene == 0) {
            vklad = 100;
            nasporene = 200;
        }

        if (isPie) {
            setDataPie(vklad, nasporene);
        } else {
            setDataBar(vklad, nasporene);
        }

        updateDescription(nasporene, nasporene - vklad);
    }

    private void updateDescription(int nasporene, int uroky) {
        String format = "Nasporena suma: %d\nZ toho uroky: %d";
        String description = String.format(Locale.getDefault(), format, nasporene, uroky);

        descriptionTextView.setText(description);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.historyItem:
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            case R.id.settingsItem:
                startActivityForResult(new Intent(this, SettingsActivity.class),
                        START_SETTINGS);
                return true;
            case R.id.aboutItem:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveData(int vklad, int sazba, int doba, int out) {
        String key = getString(R.string.history_data);
        SharedPreferences preferences = getSharedPreferences(key, Context.MODE_PRIVATE);
        String format = "vklad: %d, sazba: %d, doba: %d, celkem: %d";
        String description = String.format(Locale.getDefault(), format, vklad, sazba, doba, out);

        Log.i("data", format);

        String history = preferences.getString(key + "_history", "");
        if (history.isEmpty()) {
            history = description;
        } else {
            history += "\n" + description;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key + "_vklad", vklad);
        editor.putInt(key + "_sazba", sazba);
        editor.putInt(key + "_doba", doba);
        editor.putInt(key + "_out", out);
        editor.putString(key + "_history", history);
        editor.apply();
    }

    public void updateData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == START_SETTINGS && resultCode == Activity.RESULT_OK && data != null) {
            String name = getString(R.string.intent_result_settings);
            isPie = data.getBooleanExtra(name, true);
            setContentView(R.layout.activity_main_bar);
            init();
        }
    }
}