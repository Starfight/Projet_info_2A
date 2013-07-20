package structures;

import com.threed.jpct.*;

public class Base {
	//Attributs
	public SimpleVector _centre;
	public SimpleVector _U;
	public SimpleVector _V;
	//Pour les bases de la structure
	public double _angleStructure;
	
	//Constructeur
	public Base(SimpleVector U, SimpleVector V, SimpleVector centre) {
		_centre = centre;
		_U = U;
		_V = V;
	}
	public Base(SimpleVector U, SimpleVector V, SimpleVector centre, double angle) {
		_centre = centre;
		_U = U;
		_V = V;
		_angleStructure = angle; 
	}
	
	//retourne un vecteur en fonction de l'angle
	public static SimpleVector getVecteurAngle(Base plan, double angle) {		
		//calcule le point dans le plan
		SimpleVector pos = plan.positionDansCercle(1, angle);
		//calcule le vecteur
		SimpleVector vec = getVecteurNormalise(plan._centre, pos);
		
		return vec;
		
	}
	
	//retourne la normale de la base
	public SimpleVector getNormale() {
		return _V.calcCross(_U).normalize();
	}
	
	//inverse le vecteur
	public static SimpleVector inverseVector(SimpleVector vec) {
		vec.scalarMul(-1.0f);
		return vec;
	}
	
	//vecteur entre 2 points
	public static SimpleVector getVecteurNormalise(SimpleVector point1, SimpleVector point2) {
		return point2.calcSub(point1).normalize();
	}
	
	//retourne la position d'un point du cercle dans le plan de la base
	public SimpleVector positionDansCercle(double rayon, double angle) {
		double pX, pY, pZ;
		pX = _centre.x + rayon * Math.cos(angle) * _U.x + rayon * Math.sin(angle) * _V.x;
		pY = _centre.y + rayon * Math.cos(angle) * _U.y + rayon * Math.sin(angle) * _V.y;
		pZ = _centre.z + rayon * Math.cos(angle) * _U.z + rayon * Math.sin(angle) * _V.z;
		
		return new SimpleVector(pX,pY,pZ);
	}
}
