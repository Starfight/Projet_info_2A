package activity;

import service.MusicService;
import service.MusicService.MusicBinder;

import com.projetinfo.soundofinfinity.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	//Attributs
	private boolean mBound;
	private MusicService mService;
	private boolean changeActivity = false;
	//Connexion au service
	private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            //Binding
            MusicBinder binder = (MusicBinder)service;
            mService = binder.getService();
            mBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName component) {
            mBound = false;
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//met en place l'interface
		setContentView(R.layout.mainlayout);
		//bloque la rotation de l'écran
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	    
		//Démarre la lecture du thème via le service
		Intent intentMusicService = new Intent(MainActivity.this, MusicService.class);
        bindService(intentMusicService, mConnection, Context.BIND_AUTO_CREATE);

		//Bouton qui démarre le choix du titre
		Button bJouer = (Button) findViewById(R.id.bMainJouer);
		bJouer.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
	        // Le premier paramètre est le nom de l'activité actuelle
	        // Le second est le nom de l'activité de destination
	        Intent secondeActivite = new Intent(MainActivity.this, ChoixMusiqueActivity.class);
	        changeActivity = true;
	        
	        // Puis on lance l'intent !
	        startActivity(secondeActivite);
	      }
		});
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        changeActivity = false;
        if (mBound) {
	        // Reprise du MusicService
	        mService.play();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mise en pause du service
        if (mBound&&!changeActivity) {
        	mService.pause();
        }
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (mBound) { 
        	mService.stop();
            unbindService(mConnection); // Unbind from the service
            mBound = false;
        }
    }
}
