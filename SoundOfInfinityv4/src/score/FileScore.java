package score;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import utiles.CSVFile;
import utiles.Ressources;

import com.threed.jpct.Logger;

import android.net.Uri;

public class FileScore {
	
	//Attributs
	private Uri _path;
	private int[] _tabScores;

	/**
	 * Tableau des scores
	 * @return
	 */
	public int[] getTabScores() {
		return _tabScores;
	}

	/**
	 * Constructeur
	 * @param path Chemin en string
	 */
	public FileScore(String path) {
		//initialisation du path
		_path = Uri.parse(path);
		
		//initialisation des scores
		_tabScores = new int[Ressources.NB_SCORES];
	}
	
	/**
	 * Initialistion de FileScore
	 */
	public void init() {
		File file = new File(_path.getPath());
		
		//si le fichier CSV n'existe pas 
		if (!file.exists()) {
			enregistreFichier(file);
		}
		else {
			chargerFichier();
		}
	}
	
	/**
	 * Enregistre le fichier
	 */
	private void enregistreFichier(File file) {
		try {
			//crée le fichier 
			file.createNewFile();
			//Pour écrire dans le fichier
			PrintWriter out  = new PrintWriter(file);
			
			//Ecriture des scores
			for (int score : _tabScores) {
				out.println(score);
			}
			
			//ferme le fichier
		    out.close();
		} catch(Exception e) {
		Logger.log(e.toString());
		}
	}
	
	/**
	 * Charge le fichier
	 */
	private void chargerFichier() {
		try {
			CSVFile csv = new CSVFile(_path.getPath());
			
			//lecture des lignes
			for (int i = 0; i < csv.getRowsCount(); i++) {
				_tabScores[i]=Integer.parseInt(csv.getData(i, 0));
			}
		} catch (IOException ioe) {
			Logger.log(ioe.toString());
		}
	}
	
	/**
	 * Ajoute le score au tableau (si assez haut)
	 * @return Si le score a été ajouté
	 * @param score Score à ajouter
	 */
	public boolean addScore(int score) {
		int tmp;
		boolean change = false;
		
		//Pour tous les scores
		for (int i = 0; i<_tabScores.length; i++) {
			//si le score est supérieur
			if (score>_tabScores[i]) {
				tmp = _tabScores[i];
				_tabScores[i] = score;
				score = tmp;
				change = true;
			}
		}
		
		//S'il y a modification, alors enregistrement
		if (change) {
			File file = new File(_path.getPath());
			enregistreFichier(file);
		}
		
		return change;
	}

}
