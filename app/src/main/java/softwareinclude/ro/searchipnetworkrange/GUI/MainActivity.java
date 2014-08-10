package softwareinclude.ro.searchipnetworkrange.GUI;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import softwareinclude.ro.searchipnetworkrange.R;


public class MainActivity extends Activity implements View.OnClickListener{

    private Button helpButton;
    private ImageButton refreshSearch;
    private ListView ipListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        init();
    }

    /**
     * Init Activity UI and Data
     */
    public void init() {

        helpButton = (Button)findViewById(R.id.helpButton);
        helpButton.setOnClickListener(this);
        refreshSearch = (ImageButton)findViewById(R.id.refreshSearch);
        refreshSearch.setOnClickListener(this);

        ipListView = (ListView)findViewById(R.id.ipListView);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.helpButton: {
                    Toast.makeText(this,"Help Dialog",Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.refreshSearch: {
                   Toast.makeText(this,"Refresh",Toast.LENGTH_SHORT).show();
                break;
            }

             default: {
                break;
            }

        }
    }

}
