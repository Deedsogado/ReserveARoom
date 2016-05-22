package edu.ldsbc.reservearoom;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import edu.ldsbc.reservearoom.dummy.App;
import edu.ldsbc.reservearoom.dummy.RoomListSampleContent;


public class RoomVerifyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_verify);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // Set the title in the action bar to the selected list item.
        getActionBar().setTitle(getIntent().getStringExtra("vRoom"));


        TextView verifyDateTextView = (TextView) findViewById(R.id.verify_date_textview);
        TextView verifyTimeTextView = (TextView) findViewById(R.id.verify_time_textview);
        Spinner verifyDurationSpinner = (Spinner) findViewById(R.id.verify_duration_spinner);
        EditText verifyClassEditText = (EditText) findViewById(R.id.verify_classname_edittext);
        EditText verifyEmailEditText = (EditText) findViewById(R.id.verify_email_edittext);
        Button btnReserve = (Button) findViewById(R.id.btnReserve);

        verifyDateTextView.setText(getIntent().getStringExtra("vDate"));
        verifyTimeTextView.setText(getIntent().getStringExtra("vTime"));
        verifyClassEditText.setText("to be updated by preferences");
        verifyEmailEditText.setText("to be updated by preferences");


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> durationSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.durations, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        durationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        verifyDurationSpinner.setAdapter(durationSpinnerAdapter);

    }

    public boolean onFinalReserve(View view) {
        if (App.DEBUG_MODE) {
            Log.i("ReserveARoom", "Final Reserve Button Pressed");
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_room_verify, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar would normally
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml, however, we
        // can't know for sure who this activities parent is (could be created by
        // either RoomDetailActivity on phones, or RoomListActivity on Tablets)
        // the home case fixes this by calling finish() on this activity, which returns
        // to previous activity in stack.
        //
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
