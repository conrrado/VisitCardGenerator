package com.ccamacho.visitcardgenerator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int CAMERA_REQUEST = 1;
    private static final int CAMERA_PERMISSION_REQUEST = 2;

    ImageView imageView;
    EditText editTextName;
    EditText editTextCompany;
    EditText editTextEmail;
    Button buttonCreate;

    LinearLayout linearContent;
    ImageView imageViewCreated;
    TextView textViewName;
    TextView textViewCompany;
    TextView textViewEmail;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        editTextName = findViewById(R.id.editText_name);
        editTextCompany = findViewById(R.id.editText_company);
        editTextEmail = findViewById(R.id.editText_email);
        buttonCreate = findViewById(R.id.button_create);

        linearContent = findViewById(R.id.linear_content);
        imageViewCreated = findViewById(R.id.imageView_created);
        textViewName = findViewById(R.id.textView_name);
        textViewCompany = findViewById(R.id.textView_company);
        textViewEmail = findViewById(R.id.textView_email);

        linearContent.setVisibility(View.GONE);
        bitmap = null;

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // permission is not granted
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                        // show explanation
                        Log.d(TAG, "Não foi possível abrir a câmera, verifique se as permissões estão habilitadas");
                    } else {
                        // request permission
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                    }
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(MainActivity.this);
                if (isFieldValid()) {
                    createVisitCard();
                }
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean isFieldValid() {
        if (editTextName.getText().toString().isEmpty()) {
            editTextName.setError(getString(R.string.message_error_name));
            return false;
        }
        if (editTextCompany.getText().toString().isEmpty()) {
            editTextCompany.setError(getString(R.string.message_error_company));
            return false;
        }
        if (editTextEmail.getText().toString().isEmpty()) {
            editTextEmail.setError(getString(R.string.message_error_email));
            return false;
        }
        if (bitmap == null) {
            showMessageError(R.string.message_error_imageview);
            return false;
        }
        return true;
    }

    private void showMessageError(int messageRes) {
        Toast.makeText(this, messageRes, Toast.LENGTH_LONG).show();
    }

    private void createVisitCard() {
        linearContent.setVisibility(View.VISIBLE);
        imageViewCreated.setImageBitmap(bitmap);
        textViewName.setText(editTextName.getText());
        textViewCompany.setText(editTextCompany.getText());
        textViewEmail.setText(editTextEmail.getText());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    bitmap = (Bitmap) extras.get("data");
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}
