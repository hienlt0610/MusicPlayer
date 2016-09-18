package hienlt.app.connectdemo;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.zip.GZIPInputStream;

public class MainActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    Button btnConnect;
    TextView textView;
    EditText edtQuery;
    NetworkImageView imgArtist;
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        textView = (TextView) findViewById(R.id.tvContent);
        edtQuery = (EditText) findViewById(R.id.edtQuery);
        imgArtist = (NetworkImageView) findViewById(R.id.imgArtist);
        requestQueue = Volley.newRequestQueue(this);
        initImageLoader();

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSearchArtist(edtQuery.getText().toString());
            }
        });
    }

    private void initImageLoader() {
        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        imageLoader = new ImageLoader(requestQueue,imageCache);
    }


    private void requestSearchArtist(String artistName) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://mp3.zing.vn/tim-kiem/bai-hat.html?q=" + Uri.encode(artistName), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document document = Jsoup.parse(response);
                Element element = document.select(".box-artist").first();
                StringBuilder builder = new StringBuilder();
                if (element != null) {
                    builder.append("Ca sĩ: ").append(element.select(".artist-info h2 a").text()).append("\n");
                    String imgUrl = element.select("img.thumb-art").attr("src");
                    imgArtist.setImageUrl(imgUrl, imageLoader);
                } else {
                    builder.append("Không tìm thấy ca sĩ này");
                    imgArtist.setImageBitmap(null);
                }
                textView.setText(builder.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }
}
