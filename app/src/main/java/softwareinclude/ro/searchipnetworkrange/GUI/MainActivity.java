package softwareinclude.ro.searchipnetworkrange.GUI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import softwareinclude.ro.searchipnetworkrange.Dialog.CustomDialog;
import softwareinclude.ro.searchipnetworkrange.R;
import softwareinclude.ro.searchipnetworkrange.Util.ApplicationConstants;
import softwareinclude.ro.searchipnetworkrange.Util.GlobalData;


public class MainActivity extends Activity implements View.OnClickListener{

    public static final int REFRESH_LIST = 0;

    private Button helpButton;
    private ImageButton refreshSearch;
    private ListView ipListView;

    private ArrayAdapter<String> adapter;

    private ProgressDialog progressDialog;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        init();
        initHandler();
    }

    /**
     * Init Activity UI and Data
     */
    private void init() {

        helpButton = (Button)findViewById(R.id.helpButton);
        helpButton.setOnClickListener(this);
        refreshSearch = (ImageButton)findViewById(R.id.refreshSearch);
        refreshSearch.setOnClickListener(this);

        ipListView = (ListView)findViewById(R.id.ipListView);

        progressDialog = CustomDialog.ctor(this);


        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, GlobalData.Data.getFoundIPList());
        ipListView.setAdapter(adapter);
    }

    /**
     * Declare events and action or this activity
     */
    public void initHandler() {

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int sentInt = msg.what;
                switch (sentInt) {

                    case REFRESH_LIST: {
                        adapter.notifyDataSetChanged();
                        adapter.notifyDataSetInvalidated();
                        break;
                    }
                }}};

    }

    /**
     * Search subent IP Address using the following format: 192.168.X
     * formatIpAddress deprecated from API level 12
     * @return
     */
    private String findSubnetAddress() {
        WifiManager wManager = (WifiManager) this
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wManager.getConnectionInfo();
        if (wInfo != null) {
            int ip = wInfo.getIpAddress();
            if (ip != 0) {
                String ipAddress = Formatter.formatIpAddress(ip);
                if (ipAddress != null && !ipAddress.isEmpty()) {
                    return ipAddress.substring(0,
                            ipAddress.lastIndexOf("."));
                }
            }
        }
        return "Unknown subnet";
    }

    /**
     * Search Internet Protocol address from 0 to 254
     * using the subnet from **findSubnetAddress** method
     */
    private void searchInternetProtocolAddress(String subnetAdddress) {

        final ExecutorService executorService = Executors.newFixedThreadPool(ApplicationConstants.SEARCH_THREAD_NUMBER);
        for (int i = 0; i < 255; i++) {

            Runnable searchThread = new RunnableSearch(subnetAdddress,i);
            executorService.execute(searchThread);
        }
        executorService.shutdown();

        /**
         *  Check if search ended - used this "bad" implementation because we
         *  need a while to determine when the executorService
         *  ended and after this dismiss the progress dialog, if we don't use this
         *  the progress dialog doesnt show
         */
        Thread dismissThread = new Thread() {
            public void run() {
               while (!executorService.isTerminated()){

               }
                handler.sendMessage(Message.obtain(handler, REFRESH_LIST, null));
                progressDialog.dismiss();
            }
        };
        dismissThread.start();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.helpButton: {
                    Toast.makeText(this,"Help Dialog",Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.refreshSearch: {
                if(isWifiAvailable()){
                    progressDialog.show();
                    GlobalData.Data.foundIPList.clear();
                    handler.sendMessage(Message.obtain(handler, REFRESH_LIST, null));
                    String subnetAddress = findSubnetAddress();
                    searchInternetProtocolAddress(subnetAddress);
                }else{
                    Toast.makeText(this,"No WI-FI Connection",Toast.LENGTH_SHORT).show();
                }
                break;
            }

             default: {
                break;
            }

        }
    }

    /**
     * Check wireless availability
     * @return
     */
    private boolean isWifiAvailable() {
        ConnectivityManager cm = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetwork != null && wifiNetwork.isConnected();
    }

    /**
     *
     */
    private static class RunnableSearch implements Runnable {
        private final String subnet;
        private final int lastDigit;

        RunnableSearch(String subnet, int lastDigit) {
            this.subnet = subnet;
            this.lastDigit = lastDigit;
        }

        @Override
        public void run() {
                String host = subnet + "." + lastDigit;
                try {
                    if (InetAddress.getByName(host).isReachable(3000)) {
                        GlobalData.Data.foundIPList.add(host);
                        Log.i("Found IP: ",""+host);
                    }
                } catch (UnknownHostException e) {
                    //TODO
                    return;
                } catch (IOException e) {
                    //TODO
                    return;
                }
        }
    }

}

