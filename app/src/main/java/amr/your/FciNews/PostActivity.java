package amr.your.FciNews;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PostActivity extends AppCompatActivity implements IPickResult, AdapterView.OnItemSelectedListener {
    private Uri imageUri;
    private static final int GALLARY_REQUIST = 1;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;
    Task<Uri> result;
    EditText tv_title, tv_description;
    TextView tv_date, postLink;
    ImageView img_user;
    Spinner spin;
    String[] bankNames = {"اخبارالجامعة", "الجامعة في عيون الصحافة", "الاداره العامه للتدريب", "مؤتمرات",
            "نتائج الجامعة", "الأحتفال السنوى بعيد العلم", "الموضوعات العامة", "منح ومهمات علمية"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        tv_title = findViewById(R.id.tv_title);
        tv_date = findViewById(R.id.tv_date);
        tv_description = findViewById(R.id.tv_description);
        img_user = findViewById(R.id.img_user);
        postLink = findViewById(R.id.postLink);
        check();
        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        spin = (Spinner) findViewById(R.id.simpleSpinner);
        spin.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, bankNames);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        //format date
        Date CDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        tv_date.setText(df.format(CDate));


    }

    public void PostAction(View view) {

      /*  if (tv_title.getText().toString().isEmpty()&&tv_date.getText().toString().isEmpty()&&tv_description.getText().toString().isEmpty()&&img_user.toString().isEmpty()
        &&postLink.getText().toString().isEmpty())
        {
            Toast.makeText(this,"check valdaid data", Toast.LENGTH_SHORT).show();

        }
*/
        // check();
        StartPosting();
    }

    public void check() {

        String sUsername = tv_title.getText().toString();
        if (sUsername.equals("")) {
            Toast.makeText(this, "You did not enter a name", Toast.LENGTH_SHORT).show();
            return;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //picture Gallary selection
        if (requestCode == GALLARY_REQUIST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            //picture.setImageURI(imageUri);
        }
    }

    public void ImageAction(View view) {
        PickImageDialog.build(new PickSetup()).show(PostActivity.this);

    }

    @Override
    public void onPickResult(PickResult pickResult) {
        if (pickResult.getError() == null) {
            //get drwable pic
            img_user.setImageBitmap(pickResult.getBitmap());
            imageUri = pickResult.getUri();
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(getApplicationContext(), "This is  erorr message", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public void addUriImg(UploadTask.TaskSnapshot taskSnapshot, final DatabaseReference newPost) {
        result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String photoStringLink = uri.toString();
                newPost.child("image").setValue(photoStringLink);
            }
        });
    }


    private void StartPosting() {
        switch (spin.getSelectedItemPosition()) {
            case 0:
                databaseReference = FirebaseDatabase.getInstance().getReference("facultyNews");
                mStorageRef = FirebaseStorage.getInstance().getReference("facultyNews");
                break;
            case 1:
                databaseReference = FirebaseDatabase.getInstance().getReference("UniversityInTheEyesOfThePress");
                mStorageRef = FirebaseStorage.getInstance().getReference("UniversityInTheEyesOfThePress");
                break;
            case 2:
                databaseReference = FirebaseDatabase.getInstance().getReference("GeneralAdministrationTraining");
                mStorageRef = FirebaseStorage.getInstance().getReference("GeneralAdministrationTraining");
                break;
            case 3:
                databaseReference = FirebaseDatabase.getInstance().getReference("conferences");
                mStorageRef = FirebaseStorage.getInstance().getReference("conferences");
                break;
            case 4:
                databaseReference = FirebaseDatabase.getInstance().getReference("UniversityResults");
                mStorageRef = FirebaseStorage.getInstance().getReference("UniversityResults");
                break;
            case 5:
                databaseReference = FirebaseDatabase.getInstance().getReference("TheAnnualCelebrationOfScience");
                mStorageRef = FirebaseStorage.getInstance().getReference("TheAnnualCelebrationOfScience");
                break;
            case 6:
                databaseReference = FirebaseDatabase.getInstance().getReference("GeneralTopics");
                mStorageRef = FirebaseStorage.getInstance().getReference("GeneralTopics");
                break;
            default:
                databaseReference = FirebaseDatabase.getInstance().getReference("ScholarshipsAndScientificAssignments");
                mStorageRef = FirebaseStorage.getInstance().getReference("ScholarshipsAndScientificAssignments");
        }

        final String news_name, description, postDate, postLinkStr;
        news_name = tv_title.getText().toString().trim();
        description = tv_description.getText().toString().trim();
        postDate = tv_date.getText().toString().trim();
        postLinkStr = postLink.getText().toString().trim();

        if (!TextUtils.isEmpty(news_name) && !TextUtils.isEmpty(description) && !TextUtils.isEmpty(postDate) && !TextUtils.isEmpty(postLinkStr) && imageUri!=null) {
            StorageReference filepath = mStorageRef.child("News_Images").child(imageUri.getLastPathSegment());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final DatabaseReference newPost = databaseReference.push();
                    // check();
                    //add image uri function
                    addUriImg(taskSnapshot, newPost);

                    newPost.child("news_name").setValue(news_name);
                    newPost.child("description").setValue(description);
                    newPost.child("postDate").setValue(postDate);
                    newPost.child("postLink").setValue(postLinkStr);

                    Toast.makeText(PostActivity.this, "Done", Toast.LENGTH_LONG).show();
                }
            });


        } else {
            Toast.makeText(PostActivity.this, "check data or empty text", Toast.LENGTH_LONG).show();

        }


    }

    //Performing action onItemSelected and onNothing selected-->spinner
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {


    }


}
