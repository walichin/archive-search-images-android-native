package com.walichin.archivoalejos.buscafichas;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * An activity representing a list of Cards. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CardDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CardListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    //private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setTitle("Hay " + CardContent.NUM_REG + " registros");

        // Show the Up button in the action bar.
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }

        final FloatingActionButton search_previous = (FloatingActionButton) findViewById(R.id.search_previous);
        search_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search_previous.setEnabled(false);
                CardContent.MensajeProcInfo(CardListActivity.this);

                if (CardContent.OFFSET > 0) {
                    CardContent.OFFSET = CardContent.OFFSET - CardContent.LIMIT;
                } else {
                    CardContent.OFFSET = ((int)(CardContent.NUM_REG / CardContent.LIMIT)) * CardContent.LIMIT;
                    if (CardContent.OFFSET == CardContent.NUM_REG) {
                        CardContent.OFFSET = CardContent.OFFSET - CardContent.LIMIT;
                    }
                }

                LeeFichas leeFichas = new LeeFichas();
                leeFichas.execute(CardContent.URL + "card/search", CardContent.PARAMS_SEARCH + "&offset=" + CardContent.OFFSET + "&aditional_data=false");

            }
        });

        final FloatingActionButton search_next = (FloatingActionButton) findViewById(R.id.search_next);
        search_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search_next.setEnabled(false);
                CardContent.MensajeProcInfo(CardListActivity.this);

                if (CardContent.NUM_REG <= (CardContent.OFFSET + CardContent.LIMIT)) {
                    CardContent.OFFSET = 0;
                } else {
                    CardContent.OFFSET = CardContent.OFFSET + CardContent.LIMIT;
                }

                LeeFichas leeFichas = new LeeFichas();
                leeFichas.execute(CardContent.URL + "card/search", CardContent.PARAMS_SEARCH + "&offset=" + CardContent.OFFSET + "&aditional_data=false");

            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
//
//            }
//        });

        View recyclerView = findViewById(R.id.card_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.card_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        // apaga la ventana de "procesando informacion..."
        CardContent.handler.sendEmptyMessage(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(CardContent.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<CardContent.CardItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<CardContent.CardItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // NEVER TO INITIATE A DIALOG AND TO LEAVE THE LAYOUT WITHOUT CLOSING
                    // (TO AVOID THE INFINITE LOOP WHEN COMES BACK)
                    //CardContent.MensajeProcInfo(CardListActivity.this);

                    CardContent.CARD_ID = holder.mItem.id;
                    Context context = v.getContext();
                    Intent intent = new Intent(context, CardDetailActivity.class);
                    //intent.putExtra(CardDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public CardContent.CardItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
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
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
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
            } catch (Exception exception) {
                Log.e("LeeFichas", exception.toString());
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("LeeFichas", "Result was: " + result);

            CardContent.parse_JSON_Cards(result, false);

            View recyclerView = findViewById(R.id.card_list);
            assert recyclerView != null;
            setupRecyclerView((RecyclerView) recyclerView);

            if (findViewById(R.id.card_detail_container) != null) {
                // The detail container view will be present only in the
                // large-screen layouts (res/values-w900dp).
                // If this view is present, then the
                // activity should be in two-pane mode.
                mTwoPane = true;
            }

            FloatingActionButton search_previous = (FloatingActionButton) findViewById(R.id.search_previous);
            search_previous.setEnabled(true);

            FloatingActionButton search_next = (FloatingActionButton) findViewById(R.id.search_next);
            search_next.setEnabled(true);

            // apaga la ventana de "procesando informacion..."
            CardContent.handler.sendEmptyMessage(0);
        }
    }
}
