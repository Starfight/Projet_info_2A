package activity;

//import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;

import java.lang.reflect.Field;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import service.MusicService;
import service.MusicService.MusicBinder;
import utiles.Ressources;
import jeu.Partie;
import jeu.PartieFinieListener;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.widget.TextView;

import com.projetinfo.soundofinfinity.R;
import com.threed.jpct.*;

@SuppressLint("HandlerLeak")
public class SOIActivity extends Activity {
	
		//Constantes
		public static final int REJOUER = 1;
		public static final int RETOUR = 2;
		public static final String NAME_RESULT = "res";

		// Used to handle pause and resume...
		private static SOIActivity master = null;

		//éléments d'affichage
		private GLSurfaceView mGLView;
		private MyRenderer renderer = null;
		private FrameBuffer fb = null;
		private RGBColor back = new RGBColor(150, 200, 230);
		
		//Service pour la musique
		private boolean mBound;
		private MusicService mService;
		private boolean isFirst;
		//Connexion au service
		private ServiceConnection mConnection = new ServiceConnection() {
			
	        @Override
	        public void onServiceConnected(ComponentName className, IBinder service) {
	            //Binding
	            MusicBinder binder = (MusicBinder)service;
	            mService = binder.getService();
	            mBound = true;
	        }

	        @Override
	        public void onServiceDisconnected(ComponentName component) {
	            mBound = false;
	        }
	    };
		
		//Partie
		public static Partie partie;
		
		//Handler pour l'affichage des scores
		public Handler mHandler = new Handler() {
		        @Override
		        public void handleMessage(Message msg) {
		            String text = (String)msg.obj;
		            TextView tScore = (TextView) findViewById(R.id.tvSOIScore);
		            tScore.setText(text);
		        }
		};

		//Frame par seconde
		private int fps = 0;

		//Création de l'activité
		protected void onCreate(Bundle savedInstanceState) {
			if (master != null) {
				copy(master);
			}
			super.onCreate(savedInstanceState);
			//initialisation du statut de la musique 
			 isFirst = true;
			
			//Initialisation de la partie
			partie = new Partie(this);
			//Ajout du listener de fin de partie
			partie.addPartieFinieListener(new PartieFinieListener() {
				
				@Override
				public void PartieFinie(int score) {
					Intent ActiviteScore = new Intent(SOIActivity.this, ScoreActivity.class);
					ActiviteScore.putExtra(ScoreActivity.NAME_SCORE, score);
			        startActivityForResult(ActiviteScore,1);
				}
			});
			
			//Chargement du Renderer
	        setContentView(R.layout.soi_layout);
	        mGLView = (GLSurfaceView)findViewById(R.id.surfaceview);
			renderer = new MyRenderer();
			mGLView.setRenderer(renderer);

			//bloque la rotation de l'écran
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			
			//Démarre la lecture via le service
			Intent intentMusicService = new Intent(SOIActivity.this, MusicService.class);
	        bindService(intentMusicService, mConnection, Context.BIND_AUTO_CREATE);
		}
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			
			if (resultCode == RESULT_OK) {
				if (requestCode == 1) {
					int res = data.getIntExtra(NAME_RESULT, RETOUR);
					
					//Si on cliqué sur rejouer
					if (res == REJOUER) {
						Intent starterIntent = getIntent();
						finish();
						startActivity(starterIntent); 
					} else { //retour au menu
						finish();
					}
				}
			}
		}
		
		@Override
	    protected void onStart() {
	        super.onStart();
	        if (!partie.isInit()) {
		        //Chargement de la partie
				Intent ActiviteChargement = new Intent(SOIActivity.this, ChargementActivity.class);
		        startActivity(ActiviteChargement);
	        }
	        
	        if (mBound) {
		        // Si c'est le lancement du jeu
	        	if (isFirst) {
	        		mService.playThis(Ressources.currentTitle);
	        		isFirst = false;
	        	} else { //Reprise du MusicService
	        		mService.play();
	        	}
	        }
	    }

		@Override
		protected void onPause() {
			super.onPause();
			mGLView.onPause();
		}

		@Override
		protected void onResume() {
			super.onResume();
			mGLView.onResume();
		}

		@Override
		protected void onStop() {
			super.onStop();
	        //mise en pause du service
	        if (mBound) {
	        	mService.pause();
	        }
		}
		
		@Override
	    protected void onDestroy() {
	    	super.onDestroy();
	    	if (mBound) { 
	            unbindService(mConnection); // Unbind from the service
	        }
	    }

		//copie pour pause/resume
		private void copy(Object src) {
			try {
				Field[] fs = src.getClass().getDeclaredFields();
				for (Field f : fs) {
					f.setAccessible(true);
					f.set(this, f.get(src));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		//touché sur l'écran
		public boolean onTouchEvent(MotionEvent me) {
			
			renderer.onTouchEvent(me);
			
			return super.onTouchEvent(me);
		}
		
		//**********************************************************************************************************************

		//Renderer
		class MyRenderer implements GLSurfaceView.Renderer {

			//Attributs
			private long time = System.currentTimeMillis();

			//Constructeur
			public MyRenderer() {
			}

			//gestion du touché 
			public void onTouchEvent(MotionEvent me) {
				partie.onTouch(me);
				//Mise en pause pour ne pas surchager la méthode
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					Logger.log("Error : "+e.getMessage());
				}
			}

			//appelé au changement de dimension de l'affichage
			public void onSurfaceChanged(GL10 gl, int w, int h) {
				if (fb != null) {
					fb.dispose();
				}
				fb = new FrameBuffer(gl, w, h);

				if (master == null) {
					
					//initialisation du monde
					//_partie = new Partie(SOIActivity.this);
					//_partie.init();

					if (master == null) {
						Logger.log("Saving master Activity!");
						master = SOIActivity.this;
					}
				}
			}

			//appelé lors de la création du renderer
			public void onSurfaceCreated(GL10 gl, EGLConfig config) {
				
			}

			//dessine l'image courante
			public void onDrawFrame(GL10 gl) {
				partie.update();
				fb.clear(back);
				partie.draw(fb);
				fb.display();
				
				//Calcul des FPS
				if (System.currentTimeMillis() - time >= 1000) {
					Logger.log(fps + "fps");
					fps = 0;
					time = System.currentTimeMillis();
				}
				fps++;
			}
		}
	}
