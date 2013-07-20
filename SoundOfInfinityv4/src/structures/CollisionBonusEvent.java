package structures;

import score.Score;

import com.threed.jpct.*;

public class CollisionBonusEvent implements CollisionListener {

	/**
	 * SerialID G�n�r� automatiquement
	 */
	private static final long serialVersionUID = -4201629960372922771L;
	
	//Attributs
	private Score _score;
	private World _monde;
	
	//Constructeur 
	public CollisionBonusEvent(Score score, World monde) {
		_score = score;
		_monde = monde;
	}
	
	@Override
	//En cas de collision
	public void collision(CollisionEvent col) {
		//retire l'objet 3D
		_monde.removeObject(col.getObject());
		
		//ajoute des points (par d�faut)
		_score.ajouter(10);
	}

	@Override
	//M�thode non necessaire pour le listener
	public boolean requiresPolygonIDs() {
		return false;
	}

}
