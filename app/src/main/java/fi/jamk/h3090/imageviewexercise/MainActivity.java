package fi.jamk.h3090.imageviewexercise;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private ProgressBar progressbar;
    private final String[] images = {"http://kids.nationalgeographic.com/content/dam/kids/My%20Shot%20Promos/MyShot-Monkey.adapt.470.1.jpg",
                                    "http://ichef.bbci.co.uk/news/660/cpsprodpb/025B/production/_85730600_monkey2.jpg",
                                    "http://kurld.com/images/wallpapers/monkey-images/monkey-images-18.jpg"};
    private int imageIndex;
    private DownloadImageTask task;
    private float x1, x2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        imageIndex = 0;
        showImage();
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX(); break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                if(x1 < x2) {
                    imageIndex--;
                    if(imageIndex < 0) {
                        imageIndex = images.length - 1;
                    }
                } else {
                    imageIndex++;
                    if(imageIndex > (images.length - 1)) {
                        imageIndex = 0;
                    }
                }
                showImage();
                break;
        }
        return false;
    }

    public void showImage() {
        task = new DownloadImageTask();
        task.execute(images[imageIndex]);
    }

    private class DownloadImageTask extends AsyncTask<String,Void,Bitmap> {
        @Override
        protected void onPreExecute() {
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL imageUrl;
            Bitmap bitmap = null;
            try {
                imageUrl = new URL(urls[0]);
                InputStream in = imageUrl.openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("<<LOADIMAGE>>", e.getMessage());
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
            textView.setText("Image " + (imageIndex + 1) + "/" + images.length);
            progressbar.setVisibility(View.INVISIBLE);
        }
    }

}
