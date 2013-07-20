package classe3d;


import java.util.ArrayList;

import structures.Base;
import structures.PositionBonus;
import structures.Structure;
import utiles.Ressources;


import com.threed.jpct.CollisionListener;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.TextureInfo;
import com.threed.jpct.TextureManager;

public class Terrain extends Object3D {

	/**
	 * SerialID Généré automatiquement 
	 */
	private static final long serialVersionUID = 1L;
	
	//construction du terrain
	private double _rayon;
	private int _nbPoints;
	private int _nbProfondeur;
	private TextureInfo[] _textureInfos;
	
	//Bonus
	private PositionBonus _positionbonus;
	private CollisionListener _colBonusListener;
	
	//courbe du terrain
	private Structure _struct;
	
	/**
	 * Constructeur
	 * @param colBonusListener Listener de collision des bonus
	 * @param struct Structure du terrain
	 */
	public Terrain(CollisionListener colBonusListener, Structure struct)
	{
		super(Ressources.TERRAIN_NBPOINTS*2*Ressources.TERRAIN_NBTILES*struct.getnbBases());
		
		//attribution
		_rayon = Ressources.TERRAIN_RAYON;
		_nbPoints = Ressources.TERRAIN_NBPOINTS;
		_nbProfondeur = Ressources.TERRAIN_NBTILES;
		_struct = struct;
		_colBonusListener = colBonusListener;
		
		//textures
		_textureInfos = new TextureInfo[2];
		_textureInfos[0] = new TextureInfo(TextureManager.getInstance().getTextureID("tile"),0,0,1,0,0,1);
		_textureInfos[1] = new TextureInfo(TextureManager.getInstance().getTextureID("tile"),0,1,0,0,1,0);
	}
	
	/**
	 * initalise le terrain
	 */
	public void init() {		
		//lecture de la structure et construction des jonctions
		constructPoints();
		setTransparency(250);
		
		//ajout des bonus
		_positionbonus = new PositionBonus(_colBonusListener);
		_positionbonus.init();
		_positionbonus.placeBonus(_struct);
	}
	
	//Construit les points du terrain à partir de la structure
	private void constructPoints() {
		//Variables
		double angle; //angle pour la position des points
		SimpleVector[] lsVectorsAvant = null, lsVectorsArriere = new SimpleVector[_nbPoints];
		SimpleVector currentPoint, U, V; //Vecteur pour la base 1
		Base b1, currentBase = _struct.nextBase();
		int nbcurrent = -1; //nombre de point de la base courante
		double positionAngle = 0;
		
		//parcours des points
		while ((currentPoint=_struct.nextPoint())!=null) {			
			//changement de base
			if (nbcurrent==_nbProfondeur) {
				//angle 
				positionAngle += _struct.nextBaseRotation();
				//base
				currentBase = _struct.nextBase();
				nbcurrent = 0;
			}
			nbcurrent++;
			
			//création de la base locale
			U = currentBase.getNormale();
			V = Base.getVecteurNormalise(currentBase._centre, currentPoint);
			b1 = new Base(U, V, currentPoint);
			
			//création des vecteurs arrières
			for (int i = 0; i < _nbPoints; i++) {
				angle = (i*(2*Math.PI)/_nbPoints)+positionAngle;
				lsVectorsArriere[i] = b1.positionDansCercle(_rayon, angle);
			}
			
			//ajoute une jonction
			if (lsVectorsAvant != null)
				addJonction(lsVectorsAvant, lsVectorsArriere);
			lsVectorsAvant = lsVectorsArriere.clone();
		}
	}
	
	//construction une jonction
	private void addJonction(SimpleVector[] lsVectorsAvant, SimpleVector[] lsVectorsArriere) {
		for (int i = 0; i<_nbPoints-1; i++)
		{
			this.addTriangle(lsVectorsAvant[i], lsVectorsAvant[i+1], lsVectorsArriere[i],_textureInfos[0]);
			this.addTriangle(lsVectorsAvant[i+1], lsVectorsArriere[i+1], lsVectorsArriere[i],_textureInfos[1]);
			
		}
		this.addTriangle(lsVectorsAvant[_nbPoints-1], lsVectorsAvant[0], lsVectorsArriere[_nbPoints-1],_textureInfos[0]);
		this.addTriangle(lsVectorsAvant[0], lsVectorsArriere[0], lsVectorsArriere[_nbPoints-1],_textureInfos[1]);
	}

	//ajouter des objets enfants
	public ArrayList<Object3D> getLsBonus() {
		return _positionbonus.getLsBonus();
	}
	
}
