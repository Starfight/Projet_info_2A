package jeu;

import java.util.EventListener;

//Evenement lorsque la partie est finie
public interface PartieFinieListener extends EventListener {
	public void PartieFinie(int score);
}
