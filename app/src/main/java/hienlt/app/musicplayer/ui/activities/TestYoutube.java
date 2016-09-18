package hienlt.app.musicplayer.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import junit.framework.Test;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.media.MusicService;
import hienlt.app.musicplayer.utils.Common;

public class TestYoutube extends Activity implements View.OnClickListener {

    RequestQueue queue;
    TextView tvStep, tvInfo;
    ProgressBar progressBar;
    EditText edtUrl;
    Button btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_youtube);
        queue = Volley.newRequestQueue(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String youtubeUrl = bundle.getString(Intent.EXTRA_TEXT) + " gfdgfdgfjdljlk jlkfg xin chao http: gfgfdgfd g";
        //youtubeUrl = Common.getVideoId(youtubeUrl);
        youtubeUrl = "https://www.youtube.com/watch?v="+Common.getVideoId(youtubeUrl);

        tvStep = (TextView) findViewById(R.id.tvStep);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        edtUrl = (EditText) findViewById(R.id.edtUrl);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        //requestLink(youtubeUrl);
        Intent intentService = new Intent(this, MusicService.class);
        intentService.setAction(MusicService.ACTION_PARSE_YOUTUBE);
        intentService.putExtra(Intent.EXTRA_TEXT,youtubeUrl);
        startService(intentService);
        finish();
    }


    private void requestLink(final String youtubeUrl) {
        String url = "http://www.listentoyoutube.com/cc/conversioncloud.php";
        StringRequest requestLink = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.substring(1, response.length() - 1);
                Common.showLog(response);
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.has("error"))
                        Common.showToast(TestYoutube.this,object.getString("error"));
                    String statusUrl = object.getString("statusurl");
                    statusUrl = statusUrl + "&json";
                    if (!TextUtils.isEmpty(statusUrl)) {
                        requestGetProcess(statusUrl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Common.showLog(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Common.showLog(error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("mediaurl", youtubeUrl);
                return param;
            }
        };
        Volley.newRequestQueue(this).add(requestLink);
    }

    private void requestGetProcess(final String statusUrl) {
        StringRequest request = new StringRequest(statusUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.substring(1, response.length() - 1);
                Common.showLog(response);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONObject objStatus = object.getJSONObject("status");
                    JSONObject objAttr = objStatus.getJSONObject("@attributes");
                    String step = objAttr.getString("step");
                    tvStep.setText("Step: "+step);
                    if (step.equals("finished")) {
                        progressBar.setProgress(100);
                        String downloadurl = object.getString("downloadurl");
                        Common.showToast(TestYoutube.this, "Parse thành công");
                        tvInfo.setText("Đã convert thành công!!!");
                        edtUrl.setText(downloadurl);
                    } else {
                        int percent = objAttr.getInt("percent");
                        String timeInfo = objAttr.getString("info");
                        tvInfo.setText(timeInfo + " (" + percent + "%)");
                        progressBar.setProgress(percent);
                        requestGetProcess(statusUrl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Common.showLog(error.toString());
            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onClick(View v) {
        String url = edtUrl.getText().toString();
        if(!TextUtils.isEmpty(url)){
            Uri myUri = Uri.parse(url);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(myUri, "audio/*");
            startActivity(intent);
        }
    }
}
