package com.walichin.archivoalejos.buscafichas;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by walteralejosgongora on 2/21/16.
 */
public class CardSearchActivity extends AppCompatActivity {

    public static final String ARG_PARAMS = "params";
    //private Handler handler;
    //private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_search);

        CardContent.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                CardContent.progress.dismiss();
                super.handleMessage(msg);
            }
        };

        final EditText txtCardId = (EditText) findViewById(R.id.txtCardId);
        final EditText txtCardNumber = (EditText) findViewById(R.id.txtCardNumber);
        final EditText txtTitle = (EditText) findViewById(R.id.txtTitle);
        final EditText txtDescription = (EditText) findViewById(R.id.txtDescription);

        final Button btnSearch = (Button) findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnSearch.setEnabled(false);
                CardContent.MensajeProcInfo(CardSearchActivity.this);

                //Toast.makeText(SearchActivity.this, "Espere un momento por favor...", Toast.LENGTH_SHORT).show();
                //Snackbar.make(v, "Espere un momento por favor...", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();

                String params_url = "?limit=" + CardContent.LIMIT;

                String card_id_search = txtCardId.getText().toString().trim();
                String card_number_search = txtCardNumber.getText().toString().trim();
                String title_search = txtTitle.getText().toString().trim();
                String description_search = txtDescription.getText().toString().trim();

                if (!card_id_search.isEmpty()) {
                    //if (params_url.length() > 0) { params_url += "&"; }
                    params_url += "&card_id_search=" + card_id_search;
                }
                if (!card_number_search.isEmpty()) {
                    //if (params_url.length() > 0) { params_url += "&"; }
                    params_url += "&card_number_search=" + card_number_search;
                }
                if (!title_search.isEmpty()) {
                    //if (params_url.length() > 0) { params_url += "&"; }
                    params_url += "&title_search=" + title_search;
                }
                if (!description_search.isEmpty()) {
                    //if (params_url.length() > 0) { params_url += "&"; }
                    params_url += "&description_search=" + description_search;
                }
                //if (params_url.length() > 0) { params_url = "?" + params_url; }

                CardContent.PARAMS_SEARCH = params_url;
                CardContent.OFFSET = 0;
                params_url = params_url + "&offset=" + CardContent.OFFSET + "&aditional_data=true";

                LeeFichas leeFichas = new LeeFichas();
                leeFichas.execute(CardContent.URL + "card/search", params_url);

            }
        });

        Button btnClear = (Button) findViewById(R.id.btnClear);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtCardId.setText("");
                txtCardNumber.setText("");
                txtTitle.setText("");
                txtDescription.setText("");
            }
        });
    }

    class LeeFichas extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;

            try {

                String urlpath = params[0] + params[1];
                String json_string = "";

                URL url = new URL(urlpath);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());

                streamWriter.write(json_string);

                streamWriter.flush();
                StringBuilder stringBuilder = new StringBuilder();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(streamReader);
                    String response = null;
                    while ((response = bufferedReader.readLine()) != null) {
                        stringBuilder.append(response + "\n");
                    }
                    bufferedReader.close();

                    Log.d("LeeFichas", stringBuilder.toString());
                    return stringBuilder.toString();
                } else {
                    Log.e("LeeFichas", connection.getResponseMessage());
                    return null;
                }
            } catch (Exception exception){
                Log.e("LeeFichas", exception.toString());
                return null;
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
            }
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("LeeFichas", "Result was: " + result);

            CardContent.parse_JSON_Cards(result, true);

            Button btnSearch = (Button) findViewById(R.id.btnSearch);
            btnSearch.setEnabled(true);

            // ALWAYS TO CLOSE THE DIALOG BEFORE TO LEAVE THE LAYOUT
            // (TO AVOID THE INFINITE LOOP WHEN COMES BACK)
            CardContent.handler.sendEmptyMessage(0);

            if (CardContent.NUM_REG == 0) {

                //Toast.makeText(CardSearchActivity.this, "Espere un momento por favor...", Toast.LENGTH_SHORT).show();
                View v = findViewById(R.id.id_linear_layout);
                Snackbar.make(v, "No se encontraron registros", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            } else {

                LinearLayout searchLinearLayout= (LinearLayout) findViewById(R.id.id_linear_layout);
                Context context = searchLinearLayout.getContext();
                Intent intent = new Intent(context, CardListActivity.class);
                //intent.putExtra(ARG_PARAMS, holder.mItem.id);
                context.startActivity(intent);

            }
        }
    }
}
