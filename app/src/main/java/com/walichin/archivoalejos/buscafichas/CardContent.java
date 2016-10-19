package com.walichin.archivoalejos.buscafichas;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by walteralejosgongora on 2/21/16.
 */
public class CardContent {

    public static Handler handler;
    public static ProgressDialog progress;

    public static List<CardItem> ITEMS;
    public static Map<String, CardItem> ITEM_MAP;
    public static String CARD_ID;
    public static JSONObject CARD;
    public static JSONArray PHOTOS;
    public static JSONArray COMMENTS;
    public static JSONArray HISTORY;
    public static JSONObject EXTRA_DATA;
    public static int NUM_REG;
    public static int LIMIT = 13;
    public static int OFFSET = 0;
    public static String PARAMS_SEARCH;
    public static String URL = "http://php-archivoalejos.rhcloud.com/photo-adm/index.php/";
    //public static String URL = "http://172.16.0.6/Archivo-Alejos/archivo-backend/photo-adm/index.php/";

    public static class CardItem {
        public final String id;
        public final String content;
        public final String details;

        public CardItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }

    public static void MensajeProcInfo(Context context) {

        CardContent.progress = new ProgressDialog(context);
        CardContent.progress.setTitle("Procesando Informacion");
        CardContent.progress.setMessage("Por favor espere...");
        CardContent.progress.setCancelable(false);
        CardContent.progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        CardContent.progress.show();
    }

    public static void parse_JSON_Cards (String strJson, boolean aditional_data) {

        try {

            JSONObject jsonRootObject = new JSONObject(strJson);
            CardContent.NUM_REG = jsonRootObject.optInt("totalCount");

            if (CardContent.NUM_REG > 0) {

                JSONArray jsonArray = jsonRootObject.optJSONArray("items");

                if (aditional_data) {

                    JSONArray extradataArray = jsonRootObject.optJSONArray("extra_data");


                    CardContent.EXTRA_DATA = new JSONObject();

                    for (int i = 0; i < extradataArray.length(); i++) {

                        JSONObject jsonPair = new JSONObject();
                        jsonPair.put("previous_card_id", extradataArray.getJSONObject(i).optString("previous_card_id"));
                        jsonPair.put("next_card_id", extradataArray.getJSONObject(i).optString("next_card_id"));
                        CardContent.EXTRA_DATA.put(extradataArray.getJSONObject(i).optString("card_id"), jsonPair);
                    }
                }

                CardContent.ITEMS = new ArrayList<>();
                CardContent.ITEM_MAP = new HashMap<>();

                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String card_id = jsonObject.optString("card_id");
                    String card_number = jsonObject.optString("card_number");
                    String title = jsonObject.optString("title");
                    String description = jsonObject.optString("description");
                    String create_user = jsonObject.optString("create_user");
                    String create_date = jsonObject.optString("create_date");
                    String card_status = jsonObject.optString("card_status");
                    String active = jsonObject.optString("active");

                    String details = "Id Ficha: "+ card_id + "\n" +
                            "Num.Ficha: " + card_number + "\n" +
                            "Titulo: "+ title + "\n" +
                            "Descripcion: "+ description + "\n" +
                            "Usuario: "+ create_user + "\n" +
                            "Fecha: "+ create_date + "\n" +
                            "Estado: "+ card_status + "\n" +
                            "Activo: "+ active;


                    CardContent.CardItem cardItem = new CardContent.CardItem(
                            card_id,
                            title + " / " + description,
                            details);

                    CardContent.ITEMS.add(cardItem);
                    CardContent.ITEM_MAP.put(card_id, cardItem);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


//"archive_id":"1",
//"card_id":"1",
//"category_id":"5",
//"section_id":"0",
//"box_id":"0",
//"photo_type_id":"1",
//"process_id":"1",
//"format_id":"3",
//"negative_type_id":"2",
//"card_number":"1",
//"card_number_totarc":"1",
//"card_number_imalim":"0",
//"title":"Fabiola Original Updated Again Mi Titulo Nuevo MAs MAsoo",
//"description":"Se\u00f1orita",
//"year":"0",
//"decade":"0",
//"observation":"",
//"card_sequence_in_negative":"",
//"format_additional":"",
//"FICHST":"",
//"IMPOSI":"",
//"condition_desvanecimiento":"",
//"condition_amarillento":"",
//"condition_espejo_plata":"",
//"condition_rotura":"",
//"condition_rayadura":"",
//"condition_abrasion":"",
//"condition_manchas":"1",
//"condition_ampollas":"",
//"condition_levantamiento":"",
//"condition_hongos":"",
//"negative_vidrio":"N",
//"condition_lagunas":"",
//"negative_flexible":"Y",
//"condition_additional":"",
//"negative_nitrato":"N",
//"negative_acetato":"N",
//"negative_poliester":"Y",
//"condition_additional_description":"",
//"card_position_in_negative":"-",
//"image_path_1":"00001_041-1.jpg",
//"image_path_2":"0,,,",
//"create_user":"admin",
//"create_date":"2008-06-19 02:05:53",
//"update_user":"admin",
//"update_date":"2011-08-18 12:52:35",
//"card_status":"PEND",
//"active":"Y",
//"negative_id":null,
//"photo_type":"Retrato - Estudio",
//"primary_topics":[{"primary_topic_id":"2","description":"Mujer"}],
//"secondary_topics":[],
//"negative_type":"Flexible",
//"negative_type_code":"NF",
//"category":"B","observations":[],
//"format":"6x9",
//"process":"Gelatina de Plata",
//"supports":["Flexible","Polyester"],
//"conditions":[]
