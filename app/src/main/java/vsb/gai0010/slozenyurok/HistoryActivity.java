package vsb.gai0010.slozenyurok;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity {
    private TextView historyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("History");

        this.historyTextView = findViewById(R.id.historyTextView);

        String key = getString(R.string.history_data);
        SharedPreferences preferences = getSharedPreferences(key, Context.MODE_PRIVATE);
        String history = preferences.getString(key + "_history", "");
        this.historyTextView.setText(history);
    }

    public void clearHistory(View view) {
        if (this.historyTextView.getText().toString().isEmpty()) {
            return;
        }

        String key = getString(R.string.history_data);
        SharedPreferences preferences = getSharedPreferences(key, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key + "_vklad", 0);
        editor.putInt(key + "_sazba", 0);
        editor.putInt(key + "_doba", 0);
        editor.putInt(key + "_out", 0);
        editor.putString(key + "_history", "");
        editor.apply();

        this.historyTextView.setText("");
    }
}