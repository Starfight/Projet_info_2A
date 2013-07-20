package service;

import musique.TitreMusique;

import com.projetinfo.soundofinfinity.R;
import com.threed.jpct.Logger;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service {
	
	//Attributs
	private MediaPlayer _mPlayer;
	private final IBinder mBinder = new MusicBinder(); 
	
	//Constructeur
	public MusicService() {
		super();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		//Mise en place du média player
		_mPlayer = MediaPlayer.create(MusicService.this, R.raw.maintheme);
		_mPlayer.setLooping(true);		
		play();
	}

	@Override
	//Gère les intent
	public IBinder onBind(Intent intent) {

		return mBinder;
	}

	/**
	 * Commence la lecture
	 */
	public void play() {
		_mPlayer.start();
	}
	
	/**
	 * Met en pause la lecture
	 */
	public void pause() {
		_mPlayer.pause();
	}
	
	/**
	 * Stoppe la lecture
	 */
	public void stop() {
		_mPlayer.stop();
		_mPlayer.release();
	}
	
	/**
	 * Joue le titre
	 */
	public void playThis(TitreMusique song) {
		//Arrêt si en cours de lecture
		if (_mPlayer.isPlaying())
			_mPlayer.stop();
		
		//Remise à zero
		_mPlayer.reset();
		
		//initialisation de la nouvelle chanson
		try {
			_mPlayer.setDataSource(MusicService.this, song.getPath());
			_mPlayer.prepare();
		} catch (Exception e) {
			Logger.log(e);
		}
		
		//lecture
		_mPlayer.start();
	}
	
	//Le Binder est représenté par une classe interne 
	public class MusicBinder extends Binder {
	    // Le Binder possède une méthode pour renvoyer le Service
		public MusicService getService() {
			return MusicService.this;
	    }
	  }
}
