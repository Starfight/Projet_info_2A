package activity;

import com.projetinfo.soundofinfinity.R;

import score.FileScore;
import utiles.Ressources;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends Activity {
	
	//Constante
	public static final String NAME_SCORE = "score";
	
	//Création de l'activité
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Mise en place de l'interface
		setContentView(R.layout.score_layout);
		
		//Ouverture du fichier de score
		FileScore fScore = new FileScore(Ressources.currentTitle.getScorePath());
		fScore.init();
		
		//Récupération du score
		Intent i = getIntent();
		int score = i.getIntExtra(NAME_SCORE, 0);
		
		//Affichage du score
		TextView tvScore = (TextView) findViewById(R.id.tvScore);
		tvScore.append(String.valueOf(score));
		
		//Ajout aux scores
		tvScore.append("\n");
		if(fScore.addScore(score)) { //Si le score a été ajouté
			tvScore.append(getString(R.string.MeilleurScore));
		} 
		
		//Bouton Rejouer
		Button bRejouer = (Button) findViewById(R.id.bScoreRejouer);
		bRejouer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.putExtra(SOIActivity.NAME_RESULT, SOIActivity.REJOUER);
				setResult(RESULT_OK, i);
				finish();
			}
		});
		
		//Bouton Retour
		Button bRetour = (Button) findViewById(R.id.bScoreRetour);
		bRetour.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.putExtra(SOIActivity.NAME_RESULT, SOIActivity.RETOUR);
				setResult(RESULT_OK, i);
				finish();
			}
		});
	}
}
