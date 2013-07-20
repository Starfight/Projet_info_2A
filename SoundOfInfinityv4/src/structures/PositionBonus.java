package structures;

import java.io.IOException;
import java.util.ArrayList;

import utiles.CSVFile;
import utiles.Ressources;
import classe3d.*;

import com.threed.jpct.*;

public class PositionBonus {
	
	//Constantes
	private static RGBColor[] COLOR_DEFINITON = {RGBColor.BLUE, RGBColor.GREEN, RGBColor.RED}; 
	
	//Attributs
	private ArrayList<double[]> _positions;
	private ArrayList<Object3D> _lsBonus;
	private CollisionListener _collisionListener;
	private String _filepath;
	
	//GETTER
	public ArrayList<Object3D> getLsBonus() {
		return _lsBonus;
	}
	
	//Constructeur
	public PositionBonus(CollisionListener collisionListener) {
		_positions = new ArrayList<double[]>();
		_collisionListener = collisionListener;
		_filepath = Ressources.currentTitle.getBonusPath();
		_lsBonus = new ArrayList<Object3D>();
	}
	
	//initialisation
	public void init() {
		//Extrait les données
		extractPosition(); 
	
		//crée le bonus
		creeBonus();
	}
	
	/**
	 * Extrait les positions des bonus depuis un fichier
	 */
	private void extractPosition() {
		//ouverture du fichier
		try {
			CSVFile csv = new CSVFile(_filepath);
			
			//lecture des lignes
			for (int i = 0; i < csv.getRowsCount(); i++) {
				double[] d = new double[csv.getColsCount()];
				for (int j = 0; j <csv.getColsCount(); j++) {
					d[j]=Double.parseDouble(csv.getData(i, j));
				}
				_positions.add(d);
			}
		} catch (IOException ioe) {
			Logger.log(ioe.toString());
		}
	}
	
	/**
	 * Crée les objets 3D Bonus
	 */
	private void creeBonus() {		
		//pour toutes les positions
		for (int i = 0; i < _positions.size(); i++) {
			int ind = (int)_positions.get(i)[Ressources.POS_BONUS_ICOLOR];
			Bonus b = new Bonus(COLOR_DEFINITON[ind], _collisionListener);
			_lsBonus.add(b);
		}
	}
	
	/**
	 * Place les objet 3D bonus
	 * @param struct Structure de référence
	 */
	public void placeBonus(Structure struct) {
		//variables
		double anglePos = -Math.PI/2, angleCercle = 0; 
		double nbMsPrec = 0; //Nombre de ms du bonus précédent
		double angleCPerc = 0; //angle dans le cercle précédent
		struct.init();
		Base currentBase = struct.nextBase();
		
		//pour toutes les positions
		for (int i = 0; i < _positions.size(); i++) {
			
			//position angulaire
			anglePos += (Ressources.VITESSE_AVANCER)*(_positions.get(i)[0]-nbMsPrec);
			//position dans le cercle
			angleCercle += _positions.get(i)[1]-angleCPerc;
			
			
			//base de la trajectoire 
			while (anglePos >= struct.getangleMax()-Math.PI/2)
			{
				//modification des angles (avance et rotation)
				angleCercle += struct.nextBaseRotation();
				anglePos -= struct.getangleMax();
				//changement de base
				currentBase = struct.nextBase();
			}

			//Nouveau centre
			SimpleVector NewO = currentBase.positionDansCercle(struct.getRayon(), anglePos);
			//vecteur V
			SimpleVector NewV = Base.getVecteurNormalise(currentBase._centre, NewO);
			//vecteur U
			SimpleVector NewU = currentBase.getNormale();
			//Nouvelle base pour la rotation
			Base b1 = new Base(NewU,NewV,NewO);
			
			//position du bonus
			SimpleVector Pos = b1.positionDansCercle(Ressources.RAYON_BONUS, angleCercle);
			
			//Translation
			_lsBonus.get(i).translate(Pos);
			Logger.log("---------Translation = "+Pos.toString());
			
			//pour la différence par rapport au précédent
			nbMsPrec = _positions.get(i)[0];
			angleCPerc = _positions.get(i)[1];
		}
	}
}
