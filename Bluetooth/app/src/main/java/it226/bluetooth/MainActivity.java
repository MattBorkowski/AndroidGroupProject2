package it226.bluetooth;
        import android.app.Activity;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothSocket;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.view.View;
        import android.widget.TextView;
        import android.widget.EditText;
        import android.widget.Button;
        import android.widget.Toast;

        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.util.Set;
        import java.util.UUID;

public class MainActivity extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}

//    private void checkBTOn(){
//        BluetoothAdapter mba = BluetoothAdapter.getDefaultAdapter();
//        int REQUEST_ENABLE_BT = 1;
//
//        if (mba == null) {
//            Toast.makeText(this, "BlueTooth is not supported on this device", Toast.LENGTH_SHORT);
//        }
//        if (!mba.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//    }
// taken from the btchat tutorial
//    public ConnectThread(BluetoothDevice device, boolean secure) {
//        mmDevice = device;
//        BluetoothSocket tmp = null;
//        mSocketType = secure ? "Secure" : "Insecure";
//
//        // Get a BluetoothSocket for a connection with the
//        // given BluetoothDevice
//        try {
//            if (secure) {
//                tmp = device.createRfcommSocketToServiceRecord(
//                        MY_UUID_SECURE);
//            } else {
//                tmp = device.createInsecureRfcommSocketToServiceRecord(
//                        MY_UUID_INSECURE);
//            }
//        } catch (IOException e) {
//            Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
//        }
//        mmSocket = tmp;
//    }
// UUIDS for the app
//private static final UUID MY_UUID_SECURE =
//  UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
//private static final UUID MY_UUID_INSECURE =
//  UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");