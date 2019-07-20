package amr.your.FciNews;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView NewsRecyclerview;
    NewsAdapter newsAdapter;
    List<NewsItem> mData;
    FloatingActionButton fabSwitcher;
    boolean isDark = false;
    LinearLayoutManager layoutManager;
    ConstraintLayout rootLayout;
    EditText searchInput;
    CharSequence search = "";

    private DatabaseReference databaseReference;
    private static FirebaseRecyclerAdapter<Data, NewsViewHolder> firebaseRecyclerAdapter;
    static Activity context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // let's make this activity on full screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // hide the action bar

        //getSupportActionBar().hide();
        context = MainActivity.this;
        /*
        if (databaseReference == null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
*/
        //getIntent
        Intent intent = getIntent();
        switch (intent.getIntExtra("position", 0)) {
            case 0:
                databaseReference = FirebaseDatabase.getInstance().getReference("facultyNews");
                break;
            case 1:
                databaseReference = FirebaseDatabase.getInstance().getReference("UniversityInTheEyesOfThePress");
                break;
            case 2:
                databaseReference = FirebaseDatabase.getInstance().getReference("GeneralAdministrationTraining");
                break;
            case 3:
                databaseReference = FirebaseDatabase.getInstance().getReference("conferences");
                break;
            case 4:
                databaseReference = FirebaseDatabase.getInstance().getReference("UniversityResults");
                break;
            case 5:
                databaseReference = FirebaseDatabase.getInstance().getReference("TheAnnualCelebrationOfScience");
                break;
            case 6:
                databaseReference = FirebaseDatabase.getInstance().getReference("GeneralTopics");
                break;
            default:
                databaseReference = FirebaseDatabase.getInstance().getReference("ScholarshipsAndScientificAssignments");
        }
        // ini view

        fabSwitcher = findViewById(R.id.fab_switcher);
        rootLayout = findViewById(R.id.root_layout);
        searchInput = findViewById(R.id.search_input);
        NewsRecyclerview = findViewById(R.id.news_rv);
        mData = new ArrayList<>();

        // load theme state

        isDark = getThemeStatePref();
        if (isDark) {
            // dark theme is on

            searchInput.setBackgroundResource(R.drawable.search_input_dark_style);
            rootLayout.setBackgroundColor(getResources().getColor(R.color.black));

        } else {
            // light theme is on
            searchInput.setBackgroundResource(R.drawable.search_input_style);
            rootLayout.setBackgroundColor(getResources().getColor(R.color.white));

        }


        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        NewsRecyclerview.setHasFixedSize(true);
        NewsRecyclerview.setLayoutManager(layoutManager);

        fabSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDark = !isDark;
                if (isDark) {

                    rootLayout.setBackgroundColor(getResources().getColor(R.color.black));
                    searchInput.setBackgroundResource(R.drawable.search_input_dark_style);

                } else {
                    rootLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    searchInput.setBackgroundResource(R.drawable.search_input_style);
                }

                //  newsAdapter = new NewsAdapter(getApplicationContext(), mData, isDark);
                if (!search.toString().isEmpty()) {

                    newsAdapter.getFilter().filter(search);

                }
                //NewsRecyclerview.setAdapter(newsAdapter);
                saveThemeStatePref(isDark);


            }
        });


        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        databaseReference.keepSynced(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Iterable<DataSnapshot> childrens = dataSnapshot.getChildren();
                notificationM();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Data, NewsViewHolder>(Data.class, R.layout.item_news, NewsViewHolder.class, databaseReference) {
                @Override
                protected void populateViewHolder(NewsViewHolder viewHolder, Data model, int position) {
                    viewHolder.setDescription(model.getDescription());
                    viewHolder.setNews_name(model.getNews_name());
                    viewHolder.setImageUri(getApplicationContext(), model.getImage());
                    viewHolder.setPostDate(model.getPostDate());
                    viewHolder.setPostLink(model.getPostLink());
                }

                @Override
                public void onViewRecycled(@NonNull NewsViewHolder holder) {
                    super.onViewRecycled(holder);
                }
            };
            NewsRecyclerview.setAdapter(firebaseRecyclerAdapter);
/*
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (!dataSnapshot.exists()) {
                            // emptyView.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(MainActivity.this, " Requst new data", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("emptyViewHome", "onDataChange: ", e);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/


        } catch (Exception e) {
            Log.e("FirebaseRecyclerAdapter", "onStart: ", e);
        }
    }

    private void saveThemeStatePref(boolean isDark) {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isDark", isDark);
        editor.commit();
    }

    private boolean getThemeStatePref() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPref", MODE_PRIVATE);
        boolean isDark = pref.getBoolean("isDark", false);
        return isDark;

    }


    //View holder
    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView tv_title, tv_date, tv_description;
        String postLink;
        ImageView img_user;

        public NewsViewHolder(@NonNull final View itemView) {
            super(itemView);
            mView = itemView;

            Button ExtraInfo = mView.findViewById(R.id.ExtraInfo);
            ExtraInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(context, WebviewActivity.class);
                    browserIntent.putExtra("uri", postLink);
                    context.startActivity(browserIntent);
                }
            });
        }

        public void setDescription(String description) {
            tv_description = mView.findViewById(R.id.tv_description);
            tv_description.setText(description);

        }

        public void setNews_name(String news_name) {
            tv_title = mView.findViewById(R.id.tv_title);
            tv_title.setText(news_name);

        }

        public void setImageUri(Context ctx, String imageUri) {
            img_user = mView.findViewById(R.id.img_user);
            Glide.with(ctx)
                    .load(imageUri)
                    .into(img_user);

        }

        public void setPostDate(String postDate) {
            tv_date = mView.findViewById(R.id.tv_date);
            tv_date.setText(postDate);

        }

        public void setPostLink(String postLink) {
            this.postLink = postLink;
        }

    }


    public void notificationM() {
        Intent notificationIntent = new Intent(MainActivity.this, Main2Activity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        Notification.Builder builder = new Notification.Builder(MainActivity.this);
        builder.setContentTitle("New Post");
        builder.setSmallIcon(R.drawable.uv);
        builder.setContentIntent(pendingIntent);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        //Vibration
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        //LED
        builder.setLights(Color.RED, 3000, 3000);
        builder.setOnlyAlertOnce(true);

        Notification noti = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, noti);
    }
}
