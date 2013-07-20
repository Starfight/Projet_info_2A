package score;

import activity.SOIActivity;
import android.os.Message;

public class Score {
	
	//Attributs
	private int _currentScore;
	private SOIActivity _aSource;
	
	//GETTER
	public int getCurrentScore() {
		return _currentScore;
	}

	//Constructeur
	public Score(SOIActivity activity) {
		//Activity
		_aSource = activity;
	}
	
	public void init() {
		//score 
		_currentScore = 0;
	}
	
	/**
	 * Ajoute les points et les affiche
	 * @param points
	 */
	public void ajouter(int points) {
		//Mise � jour
		_currentScore += points;
		
		//Envoi � l'activit� pour affichage
		Message msg = new Message();
		String textTochange = String.valueOf(_currentScore);
		msg.obj = textTochange;
		_aSource.mHandler.sendMessage(msg);
	}
}
