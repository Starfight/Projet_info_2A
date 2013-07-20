package musique;

import utiles.Ressources;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

public class TitreMusique {

	//Attributs
	private Uri _path;
	private MediaMetadataRetriever _retriever;
	
	/**
	 * Constructeur
	 * @param path Chemin de la chanson
	 */
	public TitreMusique(String path) {
		//Mise en forme d'Uri
		_path = Uri.parse(path);
		
		//Initialisation du retriver
		_retriever = new MediaMetadataRetriever(); 
		_retriever.setDataSource(_path.getPath());
		
		/*Example myUri = Uri.parse("android.resource://your.package.name/" + R.raw.yourSoundFile); 
		retriever = new MediaMetadataRetriever(); 
		retriever.setDataSource(this, myUri); 
		album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM); 
		Toast.makeText(this,album , Toast.LENGTH_SHORT).show();

		The Other fields can be seen on the link such as METADATA_KEY_ALBUMARTIST . METADATA_KEY_ARTIST METADATA_KEY_AUTHOR*/
	}
	
	/**
	 * Titre de la chanson
	 * @return Titre
	 */
	public String getTitre() {
		return _retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE); 
	}
	
	/**
	 * Album de la chanson
	 * @return Album
	 */
	public String getAlbum() {
		return _retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM); 
	}
	
	/**
	 * Artiste de la chanson
	 * @return Artiste 
	 */
	public String getArtiste() {
		return _retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
	}
	
	/**
	 * Durée du titre
	 * @return Durée en ms
	 */
	public long getDuration() {
		return Long.parseLong(_retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
	}
	
	/**
	 * Retourne le nom du fichier de score de la chanson
	 * @return Nom du fichier de score
	 */
	public String getScorePath() {
		String name = _path.getPath();
		int pos = name.lastIndexOf(".");
		return name.substring(0, pos)+Ressources.EXT_SONG_SCORE;
	}
	
	/**
	 * Retourne le nom du fichier des bonus de la chanson
	 * @return Nom du fichier des bonus
	 */
	public String getBonusPath() {
		String name = _path.getPath();
		int pos = name.lastIndexOf(".");
		return name.substring(0, pos)+Ressources.EXT_SONG_BONUS;
	}
	
	/**
	 * Accede au chemin
	 * @return le chemin de la chanson
	 */
	public Uri getPath() {
		return _path;
	}
	
	@Override
	public String toString() {
		return getAlbum() +" - "+ getArtiste();
	}
}
