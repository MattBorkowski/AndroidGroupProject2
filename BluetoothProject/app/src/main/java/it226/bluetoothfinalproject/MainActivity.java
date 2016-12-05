package it226.bluetoothfinalproject;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /** Called when the user clicks the Student button */
    public void openStudent(View view) {
        // Start the Student Activity
        Intent intent = new Intent(this, StudentActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Teacher button */
    public void openTeacher(View view) {
        // Start the Student Activity
        Intent intent = new Intent(this, TeacherActivity.class);
        startActivity(intent);
    }
}
