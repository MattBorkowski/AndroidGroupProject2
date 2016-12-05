package it226.bluetoothfinalproject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class StudentActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_student);
        Intent intent = getIntent();

        // Initate Variables
        bluetoothSetupText = (TextView) findViewById(R.id.bluetoothSetupStudentText);
        listBtn = (Button)findViewById(R.id.student_list_button);
        findBtn = (Button)findViewById(R.id.student_find_button);
        myListView = (ListView)findViewById(R.id.listView1);
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
    public void studentChatInterface(View view) {
        setContentView(R.layout.activity_student_chat);

        /*
        final ChatService thread = new ChatService(socket);
        //change "socket" to the socket passed when the devices are connected
        //socket needs to be passed from Niranjans connecting method directly
        //or with a getter or setting the socket to a global variable.
        final EditText entry = (EditText) findViewById(R.id.entry);
        Button sendButton = (Button) findViewById(R.id.send);
        Button closeButton = (Button) findViewById(R.id.close);

        //Send Button
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                thread.start();
//                insert a toast to make sure that the get text works correctly.
                thread.sendMessage(entry.getText().toString());
            }
        });

        //Close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        */
    }

    /** Called when the user returns to the Bluetooth Selection Menu **/
    public void studentBluetoothInterface(View view) {
        setContentView(R.layout.activity_student);

        // Initate Variables
        bluetoothSetupText = (TextView) findViewById(R.id.bluetoothSetupStudentText);
        listBtn = (Button)findViewById(R.id.student_list_button);
        findBtn = (Button)findViewById(R.id.student_find_button);
        myListView = (ListView)findViewById(R.id.listView1);
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

    private class ChatService extends Thread {
        private static final int MESSAGE_READ = 1;
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final Handler mHandler = new Handler();

        public ChatService(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e){}

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        //listens to any incoming data and displays them in the active UI
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while(true){
                try{
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                }
                catch (IOException e){
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }

        private void sendMessage(String message) {
            if (message.length() > 0) {
                // Get the message bytes and tell the BluetoothChatService to write
                byte[] send = message.getBytes();
                write(send);

                // Reset out string buffer to zero and clear the edit text field
                EditText entry = (EditText) findViewById(R.id.entry);
                entry.setText("");
            }

        }
    }
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){

            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }
}
