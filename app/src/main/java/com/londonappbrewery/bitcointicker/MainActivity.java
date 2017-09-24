package com.londonappbrewery.bitcointicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    // Constants:
    // TODO: Create the base URL
    private final String BASE_URL = "https://apiv2.bitcoinaverage.com/indices/global/ticker/BTC";
    private final static String LOG_ID = "BitCoin";
    private final static String COMMA_SEPERATED = "###,###.00";

    // Member Variables:
    TextView mPriceTextView;
    TextView mMoveTextView;
    TextView mDayOpenTextView;
    TextView mDayAvgTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPriceTextView = (TextView) findViewById(R.id.priceLabel);
        Spinner spinner = (Spinner) findViewById(R.id.currency_spinner);
        mMoveTextView = (TextView) findViewById(R.id.moveTV);
        mDayOpenTextView = (TextView) findViewById(R.id.dayOpenTV);
        mDayAvgTextView = (TextView) findViewById(R.id.dayAvgTV);


        // Create an ArrayAdapter using the String array and a spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, R.layout.spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // TODO: Set an OnItemSelected listener on the spinner
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_ID, "" + parent.getItemAtPosition(position));
                String currency = (String) parent.getItemAtPosition(position);
                String tickerURL = BASE_URL + currency;
                Log.d(LOG_ID, tickerURL);

                letsDoSomeNetworking(tickerURL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(LOG_ID, "Nothing selected");
            }
        });

    }

    // TODO: complete the letsDoSomeNetworking() method
    private void letsDoSomeNetworking(String tickerURL) {

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(tickerURL, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                Log.d(LOG_ID, "JSON: " + response.toString());
                BitcoinDataModel bitcoinData = BitcoinDataModel.fromJson(response);
                updateUI(bitcoinData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d(LOG_ID, "Request fail! Status code: " + statusCode);
                Log.d(LOG_ID, "Fail response: " + response);
                Log.e("ERROR", e.toString());

            }
        });
    }
    private void updateUI(BitcoinDataModel bitcoin) {

        DecimalFormat decimalFormat = new DecimalFormat(COMMA_SEPERATED);

        Double moveDouble = bitcoin.getAsk() - bitcoin.getDayOpen();

        String askString = decimalFormat.format(bitcoin.getAsk());
        String dayAvgString = decimalFormat.format(bitcoin.getDayAvg());
        String dayOpenString = decimalFormat.format(bitcoin.getDayOpen());
        String moveString = decimalFormat.format(moveDouble);
        Log.d(LOG_ID,"Ask="+askString);

        mPriceTextView.setText(askString);
        mDayAvgTextView.setText(dayAvgString);
        mDayOpenTextView.setText(dayOpenString);
        mMoveTextView.setText(moveString);
    }

}
