package utiles;

import musique.TitreMusique;

public class Ressources {
	
	//Musique
	public static TitreMusique currentTitle;
	
	//Extensions 
	public static String EXT_SONG_BONUS = ".csv";
	public static String EXT_SONG_MP3 = ".mp3";
	
	//Analyse musique
	public static int TAILLE_FENETRE = 8192;
	public static int NB_FREQ_NOTE = 128;
	public static double SEUIL_AMPL = 1E6;
	public static double SEUIL_NOTE = 30;
	public static int TRONCAT_FREQ = 50;
	public static int ECH_TIME = 30000;
	public static int DECALAGE_BYTES = 16384;
	
	//File Score
	public static int NB_SCORES = 3;
	public static String EXT_SONG_SCORE = "score.csv";
	
	//Structure
	public static double RAYON_COURBURE = 1400;
	
	//Terrain
	public static float TERRAIN_RAYON = 15;
	public static int TERRAIN_NBPOINTS = 8;
	public static int TERRAIN_NBTILES = 30; 
	
	//Bonus
	public static int POS_BONUS_ICOLOR = 2;
	public static double RAYON_BONUS = 10;
	
	//Avatar
	public static float AVATAR_RAYON = 10;
	public static float VITESSE_TOURNER = 0.08f;
	public static float VITESSE_AVANCER = 0.00005f;
	
	//Camera
	public static float CAMERA_DOUT = 22;
	public static float CAMERA_DUP = 8;
	
	//Score
	public static double SCORE_RAYON = 10;
	public static double SCORE_ANGLE = -2*Math.PI/3;
}
