package amr.your.FciNews;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class splash extends Activity {
    public static final String ACTION_START_SERVICE = "amr.your.FciNews.MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Intent startIntent = new Intent(getApplicationContext(), MyService.class);
        startIntent.setAction(ACTION_START_SERVICE);
        startService(startIntent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(splash.this, Main2Activity.class));
                finish();
            }
        }, 3000);

    }
}
