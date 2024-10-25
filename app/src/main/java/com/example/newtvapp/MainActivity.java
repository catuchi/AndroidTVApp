package com.example.newtvapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Handler handler = new Handler();
    private Runnable runnable;
    private ProgressBar progressBar;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);

        runnable = new Runnable() {
            @Override
            public void run() {
                CatImages catImages = new CatImages();
                int[] statusCodes = {100, 101, 102, 103, 200, 201, 202, 203, 204, 205, 206, 207, 208, 214, 226, 300,
                        301, 302, 303, 304, 305, 307, 308, 400, 401, 402, 403, 404, 405, 406, 407, 408, 409,
                        410, 411, 412, 413, 414, 415, 416, 417, 418, 420, 421, 422, 423, 424, 425, 426, 428,
                        429, 431, 444, 450, 451, 497, 498, 499, 500, 501, 502, 503, 504, 506, 507, 508, 509, 510,
                        511, 521, 522, 523, 525, 530, 599};
                int randomIndex = new Random().nextInt(statusCodes.length);
                String statusCode = String.valueOf(statusCodes[randomIndex]);

                catImages.execute("https://http.cat/" + statusCode); // Fetch random HTTP cat image
                handler.postDelayed(this, 8000);
            }
        };

        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    // Inner class to fetch and load cat images for HTTP status codes
    private class CatImages extends AsyncTask<String, Integer, String> {
        Bitmap catBitmap;

        @Override
        protected String doInBackground(String... params) {
            String imagePath = null;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();

                catBitmap = BitmapFactory.decodeStream(inputStream);

                File file = new File(MainActivity.this.getFilesDir(), "http_cat.jpg");
                FileOutputStream out = new FileOutputStream(file);
                catBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                imagePath = file.getAbsolutePath();

                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(30);
                    publishProgress(i);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return imagePath;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String imagePath) {
            if (imagePath != null) {
                Bitmap catBitmap = BitmapFactory.decodeFile(imagePath);
                imageView.setImageBitmap(catBitmap);
            }
        }
    }
}
