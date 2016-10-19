package com.walichin.archivoalejos.buscafichas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.walichin.archivoalejos.buscafichas.adapters.ItemClickListener;
import com.walichin.archivoalejos.buscafichas.adapters.Section;
import com.walichin.archivoalejos.buscafichas.adapters.SectionedExpandableLayoutHelper;
import com.walichin.archivoalejos.buscafichas.models.Image;
import com.walichin.archivoalejos.buscafichas.models.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class CardDetailActivity extends AppCompatActivity implements ItemClickListener {

    int total_fotos;
    int fotos_guardadas;

    SectionedExpandableLayoutHelper sectionedExpandableLayoutHelper;
    ArrayList<Object> arrayListImage = new ArrayList<>();
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //setting the recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(this,
                mRecyclerView, this, 2);


        final FloatingActionButton fab_previous = (FloatingActionButton) findViewById(R.id.search_previous2);
        fab_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sectionedExpandableLayoutHelper.mSectionMap.clear();
                sectionedExpandableLayoutHelper.mSectionDataMap.clear();
                fab_previous.setEnabled(false);
                CardContent.CARD_ID = CardContent.EXTRA_DATA.optJSONObject(CardContent.CARD_ID).optString("previous_card_id");
                CardContent.MensajeProcInfo(CardDetailActivity.this);
                CargaFicha cargaFicha = new CargaFicha();
                cargaFicha.execute(CardContent.URL + "card/view/", CardContent.CARD_ID);
            }
        });

        final FloatingActionButton fab_next = (FloatingActionButton) findViewById(R.id.search_next2);
        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sectionedExpandableLayoutHelper.mSectionMap.clear();
                sectionedExpandableLayoutHelper.mSectionDataMap.clear();
                fab_next.setEnabled(false);
                CardContent.CARD_ID = CardContent.EXTRA_DATA.optJSONObject(CardContent.CARD_ID).optString("next_card_id");
                CardContent.MensajeProcInfo(CardDetailActivity.this);
                CargaFicha cargaFicha = new CargaFicha();
                cargaFicha.execute(CardContent.URL + "card/view/", CardContent.CARD_ID);
            }
        });

        CardContent.MensajeProcInfo(CardDetailActivity.this);
        CargaFicha cargaFicha = new CargaFicha();
        cargaFicha.execute(CardContent.URL + "card/view/", CardContent.CARD_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            //navigateUpTo(new Intent(this, CardSearchActivity.class));

            // EL MISMO EFECTO QUE PRESIONAR EL BOTON "BACK"
            // EL BOTON "BACK" POR DEFECTO EJECUTA finish(); QUE TERMINA/CIERRA LA ACTIVIDAD ACTUAL
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(Item item) {
        Toast.makeText(this, "Item: " + item.getName() + " clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void itemClicked(Section section) {
        Toast.makeText(this, "Section: " + section.getName() + " clicked", Toast.LENGTH_SHORT).show();
    }

    /****************** CARGA UNA FOTO *******************/

    public void GuardaFoto(String filename, String url_google_drive) {

        CargaFoto cargaFoto = new CargaFoto(filename, url_google_drive);
        cargaFoto.execute(url_google_drive);

    }

    private class CargaFoto extends AsyncTask<String, Void, Bitmap> {
        String filename;
        String url_google_drive;

        public CargaFoto(String filename, String url_google_drive) {
            this.filename = filename;
            this.url_google_drive = url_google_drive;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("CargaFoto", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            arrayListImage.add(new Image(filename, url_google_drive, result));

            fotos_guardadas = fotos_guardadas + 1;

            if (fotos_guardadas >= total_fotos) {

                sectionedExpandableLayoutHelper.addSection("Fotos", true, arrayListImage);

                Guarda_Comentarios_Historial();

                FloatingActionButton fab_previous = (FloatingActionButton) findViewById(R.id.search_previous2);
                fab_previous.setEnabled(true);

                FloatingActionButton fab_next = (FloatingActionButton) findViewById(R.id.search_next2);
                fab_next.setEnabled(true);

                // apaga la ventana de "procesando informacion..."
                CardContent.handler.sendEmptyMessage(0);

                sectionedExpandableLayoutHelper.notifyDataSetChanged();
            }
        }
    }

    /************ GUARDA LOS COMENTARIOS Y EL HISTORIAL *************/

    public void Guarda_Comentarios_Historial () {

        // GUARDAMOS LOS COMENTARIOS

        if (CardContent.COMMENTS == null || CardContent.COMMENTS.length() == 0) {
            sectionedExpandableLayoutHelper.addSection("Comentarios", false, new ArrayList());
        } else {

            ArrayList<Object> arrayList = new ArrayList<>();
            for (int i = 0; i < CardContent.COMMENTS.length(); i++) {
                arrayList.add(new Item(
                        CardContent.COMMENTS.optJSONObject(i).optString("comment") + " | " +
                                CardContent.COMMENTS.optJSONObject(i).optString("create_user") + " | " +
                                CardContent.COMMENTS.optJSONObject(i).optString("create_date"),
                        "Comentario"));
            }
            sectionedExpandableLayoutHelper.addSection("Comentarios", true, arrayList);
        }

        // GUARDAMOS EL HISTORIAL

        if (CardContent.HISTORY == null || CardContent.HISTORY.length() == 0) {
            sectionedExpandableLayoutHelper.addSection("Historial", false, new ArrayList());
        } else {

            ArrayList<Object> arrayList = new ArrayList<>();
            for (int i = 0; i < CardContent.HISTORY.length(); i++) {
                arrayList.add(new Item(
                        CardContent.HISTORY.optJSONObject(i).optString("description") + " | " +
                                CardContent.HISTORY.optJSONObject(i).optString("create_user") + " | " +
                                CardContent.HISTORY.optJSONObject(i).optString("create_date"),
                        "Actividad"));
            }
            sectionedExpandableLayoutHelper.addSection("Historial", true, arrayList);
        }
    }

    /****************** CARGA UNA FICHA *******************/

    class CargaFicha extends AsyncTask<String, Void, String> {

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

                    Log.d("CargaFicha", stringBuilder.toString());
                    return stringBuilder.toString();
                } else {
                    Log.e("CargaFicha", connection.getResponseMessage());
                    return null;
                }
            } catch (Exception exception){
                Log.e("CargaFicha", exception.toString());
                return null;
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
            }
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("CargaFicha", "Result was: " + result);

            parse_JSON (result);

            // GUARDA LA FICHA

            ArrayList<Object> arrayList = new ArrayList<>();
            //arrayList.add(new Item(CardContent.CARD.optString("card_id"), "Ficha Id"));
            //arrayList.add(new Item(CardContent.CARD.optString("card_number"), "Ficha Num"));
            arrayList.add(new Item(CardContent.CARD.optString("title"), "Titulo"));
            arrayList.add(new Item(CardContent.CARD.optString("description"), "Descripcion"));
            sectionedExpandableLayoutHelper.addSection(
                    "ID: " + CardContent.CARD.optString("card_id") +
                    " - Numero: " + CardContent.CARD.optString("card_number"),
                    true, arrayList);

            arrayList = new ArrayList<>();
            arrayList.add(new Item(CardContent.CARD.optString("category_id"), "category_id"));
            arrayList.add(new Item(CardContent.CARD.optString("section_id"), "section_id"));
            arrayList.add(new Item(CardContent.CARD.optString("box_id"), "box_id"));
            arrayList.add(new Item(CardContent.CARD.optString("photo_type_id"), "photo_type_id"));
            arrayList.add(new Item(CardContent.CARD.optString("process_id"), "process_id"));
            arrayList.add(new Item(CardContent.CARD.optString("format_id"), "format_id"));
            arrayList.add(new Item(CardContent.CARD.optString("negative_type_id"), "negative_type_id"));
            arrayList.add(new Item(CardContent.CARD.optString("card_number_totarc"), "card_number_totarc"));
            arrayList.add(new Item(CardContent.CARD.optString("card_number_imalim"), "card_number_imalim"));
            arrayList.add(new Item(CardContent.CARD.optString("year"), "year"));
            arrayList.add(new Item(CardContent.CARD.optString("decade"), "decade"));
            arrayList.add(new Item(CardContent.CARD.optString("observation"), "observation"));
            arrayList.add(new Item(CardContent.CARD.optString("card_sequence_in_negative"), "card_sequence_in_negative"));
            arrayList.add(new Item(CardContent.CARD.optString("format_additional"), "format_additional"));
            arrayList.add(new Item(CardContent.CARD.optString("condition_desvanecimiento"), "condition_desvanecimiento"));
            arrayList.add(new Item(CardContent.CARD.optString("condition_amarillento"), "condition_amarillento"));
            arrayList.add(new Item(CardContent.CARD.optString("condition_espejo_plata"), "condition_espejo_plata"));
            arrayList.add(new Item(CardContent.CARD.optString("condition_rotura"), "condition_rotura"));
            arrayList.add(new Item(CardContent.CARD.optString("condition_rayadura"), "condition_rayadura"));
            arrayList.add(new Item(CardContent.CARD.optString("condition_abrasion"), "condition_abrasion"));
            arrayList.add(new Item(CardContent.CARD.optString("condition_manchas"), "condition_manchas"));
            arrayList.add(new Item(CardContent.CARD.optString("condition_ampollas"), "condition_ampollas"));
            arrayList.add(new Item(CardContent.CARD.optString("condition_levantamiento"), "condition_levantamiento"));
            arrayList.add(new Item(CardContent.CARD.optString("condition_hongos"), "condition_hongos"));
            arrayList.add(new Item(CardContent.CARD.optString("negative_vidrio"), "negative_vidrio"));
            arrayList.add(new Item(CardContent.CARD.optString("condition_lagunas"), "condition_lagunas"));
            arrayList.add(new Item(CardContent.CARD.optString("negative_flexible"), "negative_flexible"));
            arrayList.add(new Item(CardContent.CARD.optString("condition_additional"), "condition_additional"));
            arrayList.add(new Item(CardContent.CARD.optString("negative_nitrato"), "negative_nitrato"));
            arrayList.add(new Item(CardContent.CARD.optString("negative_acetato"), "negative_acetato"));
            arrayList.add(new Item(CardContent.CARD.optString("negative_poliester"), "negative_poliester"));
            arrayList.add(new Item(CardContent.CARD.optString("condition_additional_description"), "condition_additional_description"));
            arrayList.add(new Item(CardContent.CARD.optString("card_position_in_negative"), "card_position_in_negative"));
            arrayList.add(new Item(CardContent.CARD.optString("image_path_1"), "image_path_1"));
            arrayList.add(new Item(CardContent.CARD.optString("image_path_2"), "image_path_2"));
            arrayList.add(new Item(CardContent.CARD.optString("create_user"), "create_user"));
            arrayList.add(new Item(CardContent.CARD.optString("create_date"), "create_date"));
            arrayList.add(new Item(CardContent.CARD.optString("update_user"), "update_user"));
            arrayList.add(new Item(CardContent.CARD.optString("update_date"), "update_date"));
            arrayList.add(new Item(CardContent.CARD.optString("card_status"), "card_status"));
            arrayList.add(new Item(CardContent.CARD.optString("active"), "active"));
            arrayList.add(new Item(CardContent.CARD.optString("photo_type"), "photo_type"));
            arrayList.add(new Item(CardContent.CARD.optString("primary_topics"), "primary_topics"));
            arrayList.add(new Item(CardContent.CARD.optString("secondary_topics"), "secondary_topics"));
            arrayList.add(new Item(CardContent.CARD.optString("negative_type"), "negative_type"));
            arrayList.add(new Item(CardContent.CARD.optString("negative_type_code"), "negative_type_code"));
            arrayList.add(new Item(CardContent.CARD.optString("category"), "category"));
            arrayList.add(new Item(CardContent.CARD.optString("format"), "format"));
            arrayList.add(new Item(CardContent.CARD.optString("process"), "process"));
            arrayList.add(new Item(CardContent.CARD.optString("supports"), "supports"));
            arrayList.add(new Item(CardContent.CARD.optString("conditions"), "conditions"));

            // ESTO SE COMENTO PARA OCULTAR LA SECCION DE "DATOS ADICIONALES" HASTA ARREGLAR LA DATA
            //sectionedExpandableLayoutHelper.addSection("Datos adicionales", false, arrayList);

            // CARGA EL BITMAP DE LAS FOTOS

            if (CardContent.PHOTOS == null || CardContent.PHOTOS.length() == 0) {

                sectionedExpandableLayoutHelper.addSection("Fotos", false, new ArrayList());

                Guarda_Comentarios_Historial();

                FloatingActionButton fab_previous = (FloatingActionButton) findViewById(R.id.search_previous2);
                fab_previous.setEnabled(true);

                FloatingActionButton fab_next = (FloatingActionButton) findViewById(R.id.search_next2);
                fab_next.setEnabled(true);

                // apaga la ventana de "procesando informacion..."
                CardContent.handler.sendEmptyMessage(0);

                sectionedExpandableLayoutHelper.notifyDataSetChanged();

            } else {

                arrayListImage.clear();
                total_fotos = CardContent.PHOTOS.length();
                fotos_guardadas = 0;

                for (int i = 0; i < total_fotos; i++) {

                    String url_google_drive = CardContent.PHOTOS.optJSONObject(i).optString("url_google_drive");

                    GuardaFoto(CardContent.PHOTOS.optJSONObject(i).optString("name"),
                            CardContent.PHOTOS.optJSONObject(i).optString("path"));
                }
            }

//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVaGdnd1F0aDR2Y0U&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVOEFlUjhDaXFmaXc&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVNWNFNFR3a29pRkU&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVZXFkMURXMlR4c1U&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVNHA5TjdLbkpGZFE&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVT3Mya3JjQWNmTms&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVSzV1bFBNQi1WME0&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVbXdocFZ5NnJVSXc&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVOWxpZFZrZldwX3c&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVUFhrbnBOXzZEWlU&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVOVREcll4T2xXeHM&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVd0RkYmFkYWN4Ylk&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVdDM5ekQ1S0N4SzQ&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVWlBNWi1aUTFuTWs&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVRGlZVjF2MHU4bnc&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVSW9BWXJ2c3ZZVjA&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVSklLdmhNajBUbUk&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVSDEybGltMkx0X2s&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVTHZxRV82dlQ0UkU&export=download");
//        GuardaFoto("abc.jpg", "https://docs.google.com/uc?id=0B0kjQxFxOJIVaEVheXdUdjF0YUk&export=download");

        }

        public void parse_JSON (String strJson) {

            try {

                JSONObject jsonRootObject = new JSONObject(strJson);
                CardContent.CARD = jsonRootObject.getJSONObject("item");
                //CardContent.CARD_ID = CardContent.CARD.optString("card_id");

                CardContent.PHOTOS = CardContent.CARD.getJSONArray("photos");
                CardContent.COMMENTS = CardContent.CARD.getJSONArray("comments");
                CardContent.HISTORY = CardContent.CARD.getJSONArray("history");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
