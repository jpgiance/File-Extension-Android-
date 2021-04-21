package com.example.fileextension;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Toast;

import com.example.fileextension.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            try {
                startActivityForResult(Intent.createChooser(intent, "Pick a file"), 0);
            }catch (android.content.ActivityNotFoundException e){
                Toast.makeText(this, "No file manager installed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK){

            Uri uri = data.getData();

            String fileName = null;
            if (uri.getScheme().equals("content")){
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()){
                        fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }finally {
                    cursor.close();
                }
            }
            if (fileName == null){
                fileName = uri.getPath();
                int mark = fileName.lastIndexOf("/");
                if (mark != -1){
                    fileName = fileName.substring(mark + 1);
                }
            }

            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

            binding.extensionTV.setText(String.format("File extension is: %s", extension));

        }else{
            Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}