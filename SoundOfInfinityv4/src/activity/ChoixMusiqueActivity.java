package activity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import musique.TitreMusique;

import score.FileScore;
import service.MusicService;
import service.MusicService.MusicBinder;
import utiles.Ressources;

import com.projetinfo.soundofinfinity.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ChoixMusiqueActivity extends Activity {
	
	/**
	 * Attributs
	 */
	private boolean mBound;
	private MusicService mService;
	private boolean changeActivity = false;
	private List<TitreMusique> titresMusique;
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
	/**
	 * Creation de l'activity
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//met en place l'interface
		setContentView(R.layout.choixmusiquelayout);
		
		//Démarre la lecture du thème via le service
		Intent intentMusicService = new Intent(ChoixMusiqueActivity.this, MusicService.class);
        bindService(intentMusicService, mConnection, Context.BIND_AUTO_CREATE);
		
        //liste des titres musicaux
    	final ListView listTitresMusique = (ListView) findViewById(R.id.listTitresMusique);
        genereListeMusique(listTitresMusique);
        //clic sur un des élements
        listTitresMusique.setOnItemClickListener(new OnItemClickListener() {
        	//Vue précédente
        	private View ancienView;
			@Override
			public void onItemClick(AdapterView<?> arg0, View vue, int position, long arg3) {
				//Changement de la couleur de fond
				if (ancienView!=null) {
					ancienView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
				} else {
					//Affichage du bouton play
					Button bJouer = (Button) findViewById(R.id.bCMJouer);
					bJouer.setVisibility(View.VISIBLE);
				}
				vue.setBackgroundColor(getResources().getColor(android.R.color.white));
				ancienView = vue;
				
				//Récupération de la chanson
				Ressources.currentTitle = titresMusique.get(position);
				//Récupération des scores 
				FileScore fScores = new FileScore(Ressources.currentTitle.getScorePath());
				fScores.init();
				//Récupération de la liste 
				ListView lsScores = (ListView) findViewById(R.id.listScores);
				
				//Affichage du titre
				TextView titreMusique = (TextView) findViewById(R.id.textTitreMusique);
				titreMusique.setText(Ressources.currentTitle.getTitre());
				//Affichage des scores
				affScore(fScores, lsScores);
				//Lecture de la chanson
				mService.playThis(Ressources.currentTitle);
			}
		});
        
		//Bouton qui démarre le jeu
		Button bJouer = (Button) findViewById(R.id.bCMJouer);
		bJouer.setVisibility(View.INVISIBLE);
		bJouer.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
	        // Le premier paramètre est le nom de l'activité actuelle
	        // Le second est le nom de l'activité de destination
	        Intent secondeActivite = new Intent(ChoixMusiqueActivity.this, SOIActivity.class);
	 
	        // Puis on lance l'intent !
	        startActivity(secondeActivite);
	      }
		});
	}

	@Override
    protected void onStart() {
        super.onStart();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
		super.onKeyDown(keyCode, event);
		//apui sur le bouton back
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	changeActivity = true;
	    	
	    	//joue le theme
	    	mService.stop();
	    	mService.onCreate();
	    }
		return true;       
     }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (mBound) { 
            unbindService(mConnection); // Unbind from the service
            mBound = false;
        }
    }
    
    private void genereListeMusique(ListView listTitresMusique) {
    	//Liste du contenu 
    	List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
    	//Repertoire courant
    	File currentDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Android/data/" + this.getPackageName());
    	
    	//Recherche des fichiers MP3
    	File[] fichiers = currentDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				String lower = filename.toLowerCase(Locale.getDefault());
				if (lower.endsWith(Ressources.EXT_SONG_MP3)) {
					return true;
				}
				else {
					return false;
				}
			}
		});
    	
    	//Pour tous les fichiers mp3
    	HashMap<String, String> element;
    	titresMusique = new ArrayList<TitreMusique>();
    	TitreMusique song;
    	for(File fileSong : fichiers) {
    		//Initialisation des données des chansons
    		song = new TitreMusique(fileSong.getAbsolutePath()); 
    		titresMusique.add(song);
    		
    		//ajout de l'élément
    		element = new HashMap<String, String>();
    		element.put("text1", song.getTitre());
    		element.put("text2", song.toString());
    		liste.add(element);
    	}
    	
    	//Adapter à la liste
    	ListAdapter adapter = new SimpleAdapter(this, liste, android.R.layout.simple_list_item_2, new String[] {"text1", "text2"}, new int[] {android.R.id.text1, android.R.id.text2 });
    	listTitresMusique.setAdapter(adapter);
    }
    
    private void affScore(FileScore fScores, ListView lsScores)  {
    	//Liste des Scores
        List<String> lsContent = new ArrayList<String>();
        
        //pour tous les scores
        for (int score : fScores.getTabScores()) {
        	lsContent.add(String.valueOf(score)+" pts");
        }
             
        //mise en place du score
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lsContent);
        lsScores.setAdapter(adapter);
    }
}
