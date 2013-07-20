package classe3d;

import java.io.InputStream;

import score.Score;
import structures.Base;
import structures.Structure;
import utiles.Ressources;
import jeu.Partie;
import jeu.PartieFinieListener;


import com.threed.jpct.*;

public class Avatar extends Object3D {
	
	/**
	 * SerialID Généré automatiquement
	 */
	private static final long serialVersionUID = -8019317899283606465L;
	
	//Attributs
	private Camera _camera;
	private Structure _struct;
	private Score _score;

	//Déplacement
	private boolean _moveTurn;
	private SimpleVector _move;
	private float _positionAngle = 0;
	private Partie.TURN _sens;
	private Base _currentBase;
	private double _currentAngle;
	
	//Vecteur de position
	private SimpleVector _dir;
	private SimpleVector _up;
	
	//Temps pour l'update
	private long timeDebut;
	private long timeElapsed;
	
	//Evenements
	private PartieFinieListener PFlistener;
	
	//Constructeur
	public Avatar(InputStream obj3ds, Camera cam, Structure struc, Score score) {
		super(Loader.load3DS(obj3ds, 0.01f)[0]);
		setTexture("sf_01");
		_camera = cam;
		_struct = struc;
		_score = score;
	}
	
	/**
	 * initalisation
	 */
	public void init() {
		//Instancie l'avatar
		rotateX((float)-Math.PI/2);
		
		//place la caméra
		_camera.setPosition(getTranslation());
		
		//positionne la caméra et l'avatar
		_camera.moveCamera(Camera.CAMERA_MOVEOUT, Ressources.CAMERA_DOUT);
		_camera.moveCamera(Camera.CAMERA_MOVEUP, Ressources.CAMERA_DUP);
		translate(new SimpleVector(0,Ressources.AVATAR_RAYON,0));
		
		//Instancie la structure
		_struct.init();
		
		//Instancie les déplacement
		_move = new SimpleVector();
		_moveTurn = false;
		_currentAngle = -Math.PI/2;
		_currentBase = _struct.nextBase();
		
		//instancie l'orientation
		_dir = new SimpleVector();
		_up = new SimpleVector();
		
		//Instancie les collisions
		setCollisionMode(COLLISION_CHECK_SELF);
		setCollisionOptimization(COLLISION_DETECTION_OPTIMIZED);
		
		//Initialise le temps
		timeDebut = System.currentTimeMillis();
	}
	
	/**
	 * méthode externe pour tourner
	 * @param t Sens du virage
	 */
	public void CommenceTourne(Partie.TURN t)
	{
		_sens = t;
		_moveTurn = true;
	}
	
	/**
	 * méthode externe pour arreter de tourner
	 */
	public void ArreteTourner()
	{
		_moveTurn = false;		
	}
	
	/**
	 * Avance l'avatar
	 */
	public void Avancer() {				
		//avance angulaire
		_currentAngle += Ressources.VITESSE_AVANCER*timeElapsed;
		
		//base de la trajectoire 
		if (_currentAngle >= _struct.getangleMax()-Math.PI/2)
		{
			//modification des angles (avance et rotation)
			_positionAngle += _struct.nextBaseRotation();
			_currentAngle -= _struct.getangleMax();
			//changement de base
			_currentBase = _struct.nextBase();
			
			//vérification que la partie n'est pas finie
			if (_struct.isFinished()) {
				PFlistener.PartieFinie(_score.getCurrentScore());
			}
		}

		//Nouveau centre
		SimpleVector NewO = _currentBase.positionDansCercle(_struct.getRayon(), _currentAngle);
		//vecteur V
		SimpleVector NewV = Base.getVecteurNormalise(_currentBase._centre, NewO);
		//vecteur U
		SimpleVector NewU = _currentBase.getNormale();
		//Nouvelle base pour la rotation
		Base b1 = new Base(NewU,NewV,NewO);
		
		//position de l'avatar
		SimpleVector Orient = b1.positionDansCercle(Ressources.AVATAR_RAYON, _positionAngle);
		
		//Calcul du vecteur de translation
		_move.x += Orient.x - getTranslation().x;
		_move.y += Orient.y  - getTranslation().y;
		_move.z += Orient.z - getTranslation().z;
		
		//orientation par rapport au centre
		_up = NewO.calcSub(Orient).normalize();
		//orientation par rapport au sens d'avance
		_dir = b1.getNormale();	
		
		//vérifie les collision
		checkForCollisionSpherical(_move, 1);
	}

	//déplacement sur les cotés
	private void Tourne(Partie.TURN t, float angle) {
		//sens
		int coef = 1;
		if (t == Partie.TURN.LEFT)
			coef*=-1;
		
		//rotation
		 double moveAngle = angle*coef;
		_positionAngle += moveAngle;
		_positionAngle %= Math.PI*2;
	}
	
	//met à jour les éléments
	public void update() {
		//Calcul du temps de l'update
		timeElapsed = System.currentTimeMillis() - timeDebut;
		timeDebut = System.currentTimeMillis();
		
		//Calcul des déplacements
		if (_moveTurn)
			Tourne(_sens, Ressources.VITESSE_TOURNER);	
		Avancer();
	}	
	
	//dessine les éléments
	public void draw(FrameBuffer fb) {		
		synchronized (_move) {
			//Déplacement des objets 3D
			translate(_move);
			//rotation de l'obj3D
			this.setOrientation(_up, _dir);
			
			//camera
			_camera.setOrientation(_dir, _up);
			_camera.setPosition(getTranslation());
			_camera.moveCamera(Camera.CAMERA_MOVEOUT, Ressources.CAMERA_DOUT);
			_camera.moveCamera(Camera.CAMERA_MOVEUP, Ressources.CAMERA_DUP);
			
			//Remise à zero
			_move = new SimpleVector();
		}
	}
	
	/**
	 * Ajoute le listener
	 * @param listener Listener quand la partie est finie
	 */
	public void addPartieFinieListener(PartieFinieListener listener) {
		PFlistener = listener; 
	}
}
