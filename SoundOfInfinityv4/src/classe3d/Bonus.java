package classe3d;

import  com.threed.jpct.*;

public class Bonus extends Object3D {

	/**
	 * SerialID Généré automatiquement
	 */
	private static final long serialVersionUID = 2510280804964148366L;

	//Constantes
	private static float SCALE = 1;
	//private static Object3D obj3D = Primitives.getSphere(SCALE); 
	
	/**
	 * attributs
	 */
	private RGBColor _color;
	
	/**
	 * Constructeur
	 * @param color Couleur de l'objet
	 * @param collisionListener Listener de collision
	 */
	public Bonus(RGBColor color, CollisionListener collisionListener) {
		super(Primitives.getSphere(SCALE),true);
		_color = color;
		setAdditionalColor(_color);

		//gestion des collisions
		addCollisionListener(collisionListener); 
		setCollisionMode(COLLISION_CHECK_OTHERS);
		enableCollisionListeners();
		enableLazyTransformations();
	}
}
