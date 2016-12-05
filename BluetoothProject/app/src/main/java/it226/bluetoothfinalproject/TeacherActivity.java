package it226.bluetoothfinalproject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class TeacherActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private TextView bluetoothSetupText;
    private Button listBtn;
    private Button findBtn;
    private ListView myListView;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayAdapter<String> BTArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        Intent intent = getIntent();

        // Initate Variables
        bluetoothSetupText = (TextView) findViewById(R.id.bluetoothSetupTeacherText);
        listBtn = (Button)findViewById(R.id.teacher_list_button);
        findBtn = (Button)findViewById(R.id.teacher_find_button);
        myListView = (ListView)findViewById(R.id.listView2);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            listBtn.setEnabled(false);
            findBtn.setEnabled(false);
            Toast.makeText(this, "Bluetooth is not available on this device", Toast.LENGTH_LONG).show();

            bluetoothSetupText.setText("Bluetooth is not available");
            //activity.finish();
        }else {
            // Make Device discoverable by other devices.
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);

            // Setup bluetooth
            bluetoothSetupText.setText("Select a Device to pair with:");

            // create the arrayAdapter that contains the BTDevices, and set it to the ListView
            BTArrayAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_1);
            myListView.setAdapter(BTArrayAdapter);
        }
    }

    /** Called when the user clicks the Test button */
    public void teacherChatInterface(View view) {
        setContentView(R.layout.activity_teacher_chat);

    }

    /** Called when the user returns to the Bluetooth Selection Menu **/
    public void teacherBluetoothInterface(View view) {
        setContentView(R.layout.activity_teacher);

        // Initate Variables
        bluetoothSetupText = (TextView) findViewById(R.id.bluetoothSetupTeacherText);
        listBtn = (Button)findViewById(R.id.teacher_list_button);
        findBtn = (Button)findViewById(R.id.teacher_find_button);
        myListView = (ListView)findViewById(R.id.listView2);
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            listBtn.setEnabled(false);
            findBtn.setEnabled(false);
            Toast.makeText(this, "Bluetooth is not available on this device", Toast.LENGTH_LONG).show();

            bluetoothSetupText.setText("Bluetooth is not available");
            //activity.finish();
        }else {
            // Make Device discoverable by other devices.
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);

            // Setup bluetooth
            bluetoothSetupText.setText("Select a Device to pair with:");

            // create the arrayAdapter that contains the BTDevices, and set it to the ListView
            BTArrayAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_1);
            myListView.setAdapter(BTArrayAdapter);
        }
    }

    public void list(View view){
        // get paired devices
        pairedDevices = mBluetoothAdapter.getBondedDevices();

        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public void find(View view) {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        BTArrayAdapter.clear();
        mBluetoothAdapter.startDiscovery();
        registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }
}
