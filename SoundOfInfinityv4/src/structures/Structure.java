package structures;

import java.util.ArrayList;
import java.util.Random;

import utiles.Ressources;




import com.threed.jpct.Logger;
import com.threed.jpct.SimpleVector;

public class Structure {

	//Variables statiques
	private static Random rnd = new Random();
	
	//Attributs
	private ArrayList<SimpleVector> lsPoints;
	private ArrayList<Base> lsBases;
	private int currentPoint;
	private int currentBase;
	private double _angleMax;
	private double _angleArc;
	private double _rayon;
	private double _tcote;
	private int _nbBases;
	
	//GETTER
	public int getnbBases() {
		return _nbBases;
	}
	public ArrayList<SimpleVector> getLsPoints() {
		return lsPoints;
	}
	public double getangleMax() {
		return _angleMax;
	}
	public double getRayon() {
		return _rayon;
	}
	
	//Constructeur
	public Structure() {
		lsPoints = new ArrayList<SimpleVector>();
		lsBases = new ArrayList<Base>();
		
		//taille d'une tile
		_tcote = Math.sqrt(2*Math.pow(Ressources.TERRAIN_RAYON, 2)*(1-Math.cos((2*Math.PI)/Ressources.TERRAIN_NBPOINTS)));
		//angle pour la corde d'une tile
		_angleArc = _tcote/Ressources.RAYON_COURBURE; 
		//angle actif de la base
		_angleMax = _angleArc*(Ressources.TERRAIN_NBTILES-1); 
		//Nombre de bases à fabriquer
		_nbBases = (int) (Math.floor((double)(Ressources.currentTitle.getDuration()*Ressources.VITESSE_AVANCER)/_angleMax))+1;
		
		//Initialisation
		init();
	}
	public void init() {
		currentPoint = -1;
		currentBase = -1;
	}
	
	//Retourne le prochain point (inutilisé)
	public SimpleVector nextPoint() {
		if (currentPoint < lsPoints.size()-1)
		{
			currentPoint++;
			return lsPoints.get(currentPoint);
		}
		else
			return null;
	}
	
	//Retourne la prochaine base
	public Base nextBase() {
		if (currentBase < lsBases.size()-1)
		{
			currentBase++;
			Logger.log("Base courante = "+currentBase);
			return lsBases.get(currentBase);
		}
		else
		{
			currentBase = 0;
			return lsBases.get(currentBase);
		}
	}
	
	//Retourne la rotation entre la base courante et la base suivante
	public double nextBaseRotation() {
		if (currentBase < lsBases.size()-1)
		{
			return lsBases.get(currentBase+1)._angleStructure;
		}
		else
		{
			return lsBases.get(0)._angleStructure;
		}
	}
	
	//si la structure est passée en revue
	public boolean isFinished() {
		if (currentBase == 0) 
			return true;
		else
			return false;
	}
	
	//calcule la structure
	public void calculeStructure() {
		//variables
		_rayon = Ressources.RAYON_COURBURE;
		double angle; //angle courrant
		
		//ajoute d'un point d'origine
		lsPoints.add(new SimpleVector(0,0,0));
		//direction du premier vecteur
		lsPoints.add(new SimpleVector(0d,0d,_tcote));
		
		//Base de départ 
		Base b1 = new Base(new SimpleVector(1,0,0), new SimpleVector(0, 1, 0), new SimpleVector(0, 0, 0));
		
		
		//boucle de création de la structure
		int currentI = 2;
		for (int k = 0; k < _nbBases; k++) {			
			//angle de rotation du plan
			angle = rnd.nextDouble()*Math.PI*2-Math.PI;	
			
			//définition de la base
			SimpleVector U = Base.inverseVector(b1.getNormale());
			SimpleVector V = Base.getVecteurAngle(b1, angle);
			SimpleVector centre = new SimpleVector(V.x*_rayon+lsPoints.get(currentI-2).x, 
												   V.y*_rayon+lsPoints.get(currentI-2).y, 
												   V.z*_rayon+lsPoints.get(currentI-2).z);
			
			//ajout de la base
			lsBases.add(new Base(U, V, centre, angle));	
			
			//retire le dernier point
			lsPoints.remove(currentI-1);
			
			//ajout des points
			double A = - V.calcAngle(U);
			for (int i = 0; i < Ressources.TERRAIN_NBTILES; i++) {
				A += _angleArc;
				lsPoints.add(lsBases.get(k).positionDansCercle(_rayon, A));	
			}
			
			//indice courant
			currentI = lsPoints.size();
			
			//Rédéfinition de b1
			b1 = new Base(Base.getVecteurNormalise(lsPoints.get(currentI-2),lsBases.get(k)._centre),
						  Base.inverseVector(lsBases.get(k).getNormale()),
						  lsPoints.get(currentI-2));
			
		}
	}
}
