package jeu;

import score.Score;
import structures.CollisionBonusEvent;
import structures.Structure;
import classe3d.*;
import com.projetinfo.soundofinfinity.R;
import com.threed.jpct.*;
import com.threed.jpct.util.SkyBox;

import activity.SOIActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public class Partie {

	//enumérateurs
	public enum TURN { LEFT, RIGHT };	
	
	//Référence de l'activité
	private SOIActivity _activitySource;
	
	/**
	 * Attributs
	 */
	private Terrain _terrain;
	private Avatar _avatar;
	private World _monde;
	private Structure _structure;
	private Score _score;
	private CollisionListener _collisionbonuslistener;
	private PartieFinieListener PFListener;
	private SkyBox _skybox;
	private boolean isinit = false;
	
	/**
	 * Etat de l'initialisation
	 * @return Retourne true si initialisé
	 */
	public boolean isInit() {
		return isinit;
	}

	/**
	 * Constructeur
	 * @param aSource Activité mère
	 */
	public Partie(SOIActivity aSource) {
		_activitySource = aSource;
	}

	/**
	 * Initialise la partie
	 */
	public void init() {
		//SkyBox
		_skybox = new SkyBox(50000);
		
		//Instancie le monde
		_monde = _skybox.getWorld();
		_monde.setAmbientLight(200, 200, 200);
		
		//Crée la structure 
		_structure = new Structure();
		_structure.calculeStructure();
		
		//Instancie le score
		_score = new Score(_activitySource);
		_score.init();
		
		//instancie le collisionListener
		_collisionbonuslistener = new CollisionBonusEvent(_score, _monde);
		
		//Instancie le terrain
		_terrain = new Terrain(_collisionbonuslistener, _structure);
		_terrain.init();
		_terrain.enableLazyTransformations(); //objet immobile
		addObject(_terrain);
		//ajout des bonus
		for (int i = 0; i < _terrain.getLsBonus().size(); i++) {
			addObject(_terrain.getLsBonus().get(i));
		}
		
		//Instancie l'avatar
		_avatar = new Avatar(_activitySource.getResources().openRawResource(R.raw.fighter), _monde.getCamera(), _structure, _score);
		_avatar.init();
		_avatar.addPartieFinieListener(PFListener);
		addObject(_avatar);	
		
		//Compression des textures 
		TextureManager.getInstance().compress();
		
		//fin de l'init
		isinit = true;
	}

	/**
	 * Update de la partie
	 */
	public void update() {
		//avatar
		_avatar.update();
	}

	/**
	 * Draw de la partie
	 * @param fb
	 */
	public void draw(FrameBuffer fb) {
		//monde
		_monde.renderScene(fb);
		_monde.draw(fb);
		
		//avatar
		_avatar.draw(fb);
	}

	//lors du touché
	public void onTouch(MotionEvent me) {
		//extraction de la largeur
		DisplayMetrics dm = new DisplayMetrics();
		_activitySource.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int largeur = dm.widthPixels;

		//en fonction de la position
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			if (me.getX()>largeur/2)
				_avatar.CommenceTourne(TURN.RIGHT);
			else
				_avatar.CommenceTourne(Partie.TURN.LEFT);
		}
		else if (me.getAction() == MotionEvent.ACTION_UP) {
			_avatar.ArreteTourner();
		}
	}
	
	/**
	 * Ajoute le listener
	 * @param listener Listener quand la partie est finie
	 */
	public void addPartieFinieListener(PartieFinieListener listener) {
		PFListener = listener; 
	}
	
	
	//ajoute l'objet au monde avec les méthode de construction et d'optimisation de la ram
	private void addObject(Object3D obj)	{
		obj.strip();
		obj.build();
		_monde.addObject(obj);
	}
}
