/*
 *  Author: Jose Antonio Luce–o Castilla
 *  Date  : Septempber 2013
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.jalc.startarduinobtandroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private ListView listview1;
	private Button btToggle;
	private ProgressDialog pDialog;
	private ArrayAdapter<String> mArrayAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private ArrayList<BluetoothDevice> BtDevicesArray = new ArrayList<BluetoothDevice>();
	private ConnectAsyncTask connectAsyncTask;
	private ComunicationAsyncTask comunicationAsyncTask;
	private BluetoothSocket btSocket;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		listview1 = (ListView) findViewById(R.id.listView1);
		btToggle = (Button) findViewById(R.id.button1);
		
		// Setting ArrayAdaptar and ListView.
		mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		listview1.setAdapter(mArrayAdapter);
		
		// Create AsyncTask's
		connectAsyncTask = new ConnectAsyncTask();
		comunicationAsyncTask = new ComunicationAsyncTask();
		
		// Conifguration ProgressBar
		
		// Get Bluetooth Adapter.
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		// Check smartphone support Bluetooth
		if(mBluetoothAdapter == null){
			// Device does not support Bluetooth
			Toast.makeText(getApplicationContext(), "Not Support bluetooth",0).show();
			finish();
		}
		
		// Check Bluetooth enabled.
		if(!mBluetoothAdapter.isEnabled()){
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1);
		}
	
		// Querying paried devices	
		Set<BluetoothDevice> pariedDevices = mBluetoothAdapter.getBondedDevices();
		if(pariedDevices.size() > 0){
			for(BluetoothDevice device : pariedDevices) {
				// Add the name and mac address to array adapter
				mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				BtDevicesArray.add(device);
			}
		}
		// END Querying paried devices 
		
		// Event click on list View
		listview1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View view, int position, long id) {
				
				pDialog = new ProgressDialog(MainActivity.this);
		        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		        pDialog.setMessage("Connecting...");
		        pDialog.setCancelable(true);
		        pDialog.setMax(100);
		        pDialog.setProgress(0);
	            pDialog.show();
				
				BluetoothDevice device = BtDevicesArray.get(position);

				// Cancel Discovering for best performance.
				if(mBluetoothAdapter.isDiscovering()){
					mBluetoothAdapter.cancelDiscovery();
				}
				
				// Run async task connect Blutooth device.			
				connectAsyncTask.execute(device);
				
			}
		});
		
		// Event click on Button Toggle.
		btToggle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
					comunicationAsyncTask.write(new String("L").getBytes());
			}
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
		
	}
	
	public class ConnectAsyncTask extends AsyncTask<BluetoothDevice, Integer, BluetoothSocket>{

		private BluetoothSocket mmSocket;
		private BluetoothDevice mmDevice;
		
		@Override
		protected BluetoothSocket doInBackground(BluetoothDevice... device) {
			
			mmDevice = device[0];
			
			try {
				
				mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				mmSocket.connect();
						
			} catch (IOException e) {
				try {
					mmSocket.close();
				} catch (IOException closeException) { }
				return null;
			}
			return mmSocket;
		}

		@Override
		protected void onCancelled() {
			
			try {
				mmSocket.close();
			} catch (IOException e) { }
			
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(BluetoothSocket result) {
			if (result != null){
				btSocket = result;
				// Enable Button ****
				btToggle.setEnabled(true);
				pDialog.dismiss();
			}else{
				pDialog.dismiss();
			}
			super.onPostExecute(result);

		}
		
	}

	public class ComunicationAsyncTask extends AsyncTask<BluetoothSocket, Integer, Boolean>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
	    
		@Override
		protected Boolean doInBackground(BluetoothSocket... socket) {
						
			return null;
		}

		public void write(byte[] bytes){
			
			OutputStream mmOutStream = null;
			
			try {
				if (btSocket.isConnected()){
					mmOutStream = btSocket.getOutputStream();
					mmOutStream.write(bytes);
				}else{
					Toast.makeText(getApplicationContext(), "Not connected",0).show();
				}
			} catch (IOException e) {}
						
		}
		
	}
}
