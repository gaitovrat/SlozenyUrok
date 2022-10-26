package vsb.gai0010.slozenyurok;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("Settings");
    }

    private void sendIntent(boolean value) {
        Intent intent = new Intent();
        String name = getString(R.string.intent_result_settings);
        intent.putExtra(name, value);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void setPie(View view) {
        sendIntent(true);
    }

    public void setBar(View view) {
        sendIntent(false);
    }
}