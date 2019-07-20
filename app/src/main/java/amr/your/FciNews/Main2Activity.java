package amr.your.FciNews;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    News2Adapter adapter;
    ArrayList<Data_item> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_main);
        recyclerView = findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        addData();
        adapter = new News2Adapter(data);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemclick(new News2Adapter.OnItemclick() {
            @Override
            public void onitemclick(Data_item data, int postion) {
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                intent.putExtra("position", postion);
                startActivity(intent);
            }
        });

    }


    public void addData() {
        data = new ArrayList<>();

        data.add(new Data_item("اخبارالجامعة", R.drawable.wal));
        data.add(new Data_item("الجامعة في عيون الصحافة", R.drawable.walll));
        data.add(new Data_item("الاداره العامه للتدريب", R.drawable.mmw));
        data.add(new Data_item("مؤتمرات", R.drawable.mo));
        data.add(new Data_item("نتائج الجامعة", R.drawable.natega));
        data.add(new Data_item("الأحتفال السنوى بعيد العلم", R.drawable.saa));
        data.add(new Data_item("الموضوعات العامة", R.drawable.mw));
        data.add(new Data_item("منح ومهمات علمية", R.drawable.walllll));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        if (item.getItemId() == R.id.item8) {
            startActivity(new Intent(Main2Activity.this, Login.class));
            return true;
        }*/
        if (item.getItemId() == R.id.item9) {
            // show dialog exit
            show_Dialog();


            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    public void show_Dialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("هل تريد حقا الخروج من التطبيق ؟")
                .setCancelable(false)
                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Main2Activity.this.finish();
                    }
                })
                .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
