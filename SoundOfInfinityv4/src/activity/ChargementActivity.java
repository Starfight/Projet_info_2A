package activity;

import utiles.Ressources;
import musique.AnalyseMusique;

import com.projetinfo.soundofinfinity.R;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.util.BitmapHelper;

import android.app.Activity;
import android.os.Bundle;

public class ChargementActivity extends Activity {
	
	//Constantes
	private final static int QUALITE_TEXTURE = 128;
	private final static int QUALITE_SKYBOX = 256;
	
	//Création de l'activité
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    //Affichage 
	    setContentView(R.layout.chargement_layout);
	    
	    //Thread de chargement
	    Thread tChrgmnt = new Thread(new Runnable() {
			
			@Override
			public void run() {
				//Analyse de la musique (se déclenche uniquement si le fichier est inexistant)
				AnalyseMusique.analyse(Ressources.currentTitle);
				Runtime.getRuntime().gc();
				
				//Charge les textures
				chargeTextures();
				
				//Initialisation de la partie
				SOIActivity.partie.init();
				
				//renvoi de la partie initialisée
		        finish();
			}
		});
	    tChrgmnt.start();
	}
	
	//charge les textures
	public void chargeTextures()
	{
		TextureManager.getInstance().flush();
		TextureManager.getInstance().addTexture("tile",new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.carre_bleu)),QUALITE_TEXTURE,QUALITE_TEXTURE)));
		TextureManager.getInstance().addTexture("sf_01",new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.sf_01)),QUALITE_TEXTURE,QUALITE_TEXTURE)));
		
		//chargement de la skybox
		TextureManager.getInstance().addTexture("back",new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.stars_back)),QUALITE_SKYBOX,QUALITE_SKYBOX)));
		TextureManager.getInstance().addTexture("front",new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.stars_front)),QUALITE_SKYBOX,QUALITE_SKYBOX)));
		TextureManager.getInstance().addTexture("right",new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.stars_right)),QUALITE_SKYBOX,QUALITE_SKYBOX)));
		TextureManager.getInstance().addTexture("up",new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.stars_top)),QUALITE_SKYBOX,QUALITE_SKYBOX)));
		TextureManager.getInstance().addTexture("left",new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.stars_left)),QUALITE_SKYBOX,QUALITE_SKYBOX)));
		TextureManager.getInstance().addTexture("down",new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.stars_top)),QUALITE_SKYBOX,QUALITE_SKYBOX)));
	}
}
