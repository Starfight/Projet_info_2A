package musique;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import utiles.FFT;
import utiles.Ressources;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

import com.threed.jpct.Logger;

public class AnalyseMusique {

	//Constantes
	private static Random Rnd = new Random();
	
	/**
	 * Analyse si besoin de TitreMusique
	 */
	public static void analyse(TitreMusique tSong) {
		File file = new File(tSong.getBonusPath());
		//si le fichier CSV n'existe pas 
		if (!file.exists()) {
			createSoundFile(file, tSong);
		}
	}

	/**
	 * extrait les positions des fr�quences et cr�e le fichier r�f�rence
	 */
	public static void createSoundFile(File fileBonus, TitreMusique tSong) {
		//variables
		double[] don = new double[3]; //donn�es pour l'insertion au fichier csv 
		int[] note = null; //note courante
		double seuil = Ressources.SEUIL_AMPL; //seuil des amplitudes des fr�quences de la valeur max 
		double seuil_note = Ressources.SEUIL_NOTE; //seuil auquel 2 notes sont consid�r�es diff�rentes
		int tFenetre = Ressources.TAILLE_FENETRE; //taille de la fenetre au format 2^n
		int fTime = (Ressources.DECALAGE_BYTES/4)*1000/44100; //intervalle de temps de la fen�tre
		int nbNote = 0; //nombre de note analys�es
		double angle = Math.PI/8; //angle des bonus
		int currentTime = 0; //Temps du mp3 d�cod� en ms
		int currentIndD = 0; //indice du d�but dans songBytes
		int currentIndF = Ressources.TAILLE_FENETRE*4; //indice de fin dans songBytes
		
		try {
			//cr�e le fichier 
			fileBonus.createNewFile();
			//Pour �crire dans le fichier
			PrintWriter out  = new PrintWriter(fileBonus);
			
			//FFT
			FFT fastFourrier = new FFT(tFenetre);
			//fenetres pour la fft
			double[] fenetreFFT = new double[tFenetre];
			double[] fenetreFFTi = new double[tFenetre]; //valeurs imaginaires nulles
				
			//Pour toute la dur�e du titre
			long duration = tSong.getDuration();
			String pathSong = tSong.getPath().getPath();
			while (currentTime<duration) {
				
				
				//d�codage du fichier mp3
				byte[] songBytes;
				if ((currentTime+Ressources.ECH_TIME)<duration)
					songBytes = decode(pathSong, currentTime, (currentTime+Ressources.ECH_TIME));
				else
					songBytes = decode(pathSong, currentTime, (int)duration);
				
				//incr�mentation de currentTime
				currentTime+=Ressources.ECH_TIME;
				 
				//Pour tout le tableau songBytes
				while (currentIndF<songBytes.length) {
					
					//Pour remplir tout le tableau 
					for (int i = currentIndD; i<currentIndF; i+=4) {
						
						//converti en double (� partir de 2 octets)
						double doubleVal = convert2BytesInShort(songBytes[i], songBytes[i+1]);
						//ajout de la valeur
						int ind = (i-currentIndD)/4;
						fenetreFFT[ind] = doubleVal;
						
						//si on a r�uni toutes les donn�es de la fen�tre
						if (ind==tFenetre-1) {
							double[] clone = fenetreFFTi.clone(); //double du tableau vide (valeur imaginaires)
							fastFourrier.fft(fenetreFFT, fenetreFFTi); //transform�e de fourrier
							
							//note courrante
							int[] noteCurrent = new int[Ressources.NB_FREQ_NOTE];
							
							//pour toutes les fr�quences
							int c = 0;
							for (int j = Ressources.TRONCAT_FREQ; j<fenetreFFT.length-Ressources.TRONCAT_FREQ; j++) {
								//si l'amplitude de la fr�quence est sup�rieur au seuil
								if ((Math.abs(fenetreFFT[j])>seuil)&&(c<noteCurrent.length)) {
									noteCurrent[c] = j;
									//Logger.log("Amplitude "+j+"= "+fenetreFFT[j]);
									c++;	
								}	
							}
							
							//si le tableau de note existe 
							if (note!=null) {
								//si le tableau de note est diff�rent
								if (calculDifferencesNotes(note, noteCurrent)>seuil_note) {
									//inscription des nouvelles notes dans le tableau
									don[0] = nbNote*fTime; //temps en ms
							    	don[1] = angle; //angle
							    	don[2] = 0; //cat�gorie 
							    	out.println(don[0] + ";" + don[1] + ";" + don[2]);
								}
								else { //sinon on change l'angle
									angle += getRandomAngle();
								}
								//Logger.log("Diff "+nbNote+" = " + test);
							}
							
							//r��criture du tableau vide 
							fenetreFFTi = clone;
							
							//attribution du tableau de note pour le suivant
							note = noteCurrent.clone();
							nbNote++;
						}
					}
					//incr�mentation de l'indice de d�but
					currentIndD+=Ressources.DECALAGE_BYTES;
					
					//v�rification de l'indice de fin
					if (currentIndF+Ressources.DECALAGE_BYTES>songBytes.length)
						currentIndF = songBytes.length;
					else
						currentIndF+= Ressources.DECALAGE_BYTES;
				}
				
				//initialisation des indices de lecture de songBytes
				currentIndF = Ressources.TAILLE_FENETRE*4;
				currentIndD = 0;
			}
			
		    //ferme le fichier
		    out.close();
		}
		catch(Exception e){
			Logger.log(e.getLocalizedMessage());
		}

	}
	
	/**
	 * D�code un fichier mpeg
	 * @param path chemin du fichier
	 * @param startMs d�but 
	 * @param maxMs fin
	 * @return PCM data
	 * @throws IOException
	 * @throws DecoderException
	 */
	private static byte[] decode(String path, int startMs, int maxMs) 
			  throws IOException, DecoderException {
			  ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);
			   
			  float totalMs = 0;
			  boolean seeking = true;
			   
			  File file = new File(path);
			  InputStream inputStream = new BufferedInputStream(new FileInputStream(file), 8 * 1024);
			  try {
			    Bitstream bitstream = new Bitstream(inputStream);
			    Decoder decoder = new Decoder();
			     
			    boolean done = false;
			    while (! done) {
			      Header frameHeader = bitstream.readFrame();
			      if (frameHeader == null) {
			        done = true;
			      } else {
			        totalMs += frameHeader.ms_per_frame();
			        
			 
			        if (totalMs >= startMs) {
			          seeking = false;
			        }
			         
			        if (! seeking) {
			          SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
			           
			          if (output.getSampleFrequency() != 44100
			              || output.getChannelCount() != 2) {
			            throw new DecoderException("mono or non-44100 MP3 not supported", null);
			          }
			           
			          short[] pcm = output.getBuffer();
			          for (short s : pcm) {
			            outStream.write(s & 0xff);
			            outStream.write((s >> 8 ) & 0xff);
			          }
			        }
			         
			        if (totalMs >= (startMs + maxMs)) {
			          done = true;
			        }
			      }
			      bitstream.closeFrame();
			    }
			     
			    return outStream.toByteArray();
			  } catch (BitstreamException e) {
			    throw new IOException("Bitstream error: " + e);
			  } catch (DecoderException e) {
			    Logger.log(e);
			    throw new DecoderException(e.toString(), null);
			  } finally {
			    inputStream.close();     
			  }
			}
	
	/**
	 * Converti en short � partir de 2 octets
	 * @param b1 byte n�1
	 * @param b2 byte n�2
	 * @return Valeur en short
	 */
	private static short convert2BytesInShort(byte b1, byte b2) {
		//
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(b1);
		bb.put(b2);
		return bb.getShort(0);
	}
	
	/**
	 * Calcule la diff�rence entre 2 tableaux de notes
	 * @param note1
	 * @param note2
	 * @return pourcentage de diff�rence
	 */
	private static double calculDifferencesNotes(int[] note1, int[] note2) {
		double diff = 0;
		int ind2 = 0;
		
		for(int n : note1) {
			while ((n<note2[ind2])&&(ind2<note2.length-1)) {
				ind2++;
			}
			if (n!=note2[ind2]) {
				diff++;
			}
			else if (ind2<note2.length-1) {
				ind2++;
			}
		}
		diff += Math.abs(note1.length-note2.length);
		
		double percent = diff/Math.max(note1.length, note2.length);
		
		return percent*100;
	}
	
	/**
	 * Retourne un angle al�atoire
	 * @return
	 */
	private static double getRandomAngle() {
		double angle = Math.PI/8;
		int coef = Rnd.nextInt(3)-1;
		return coef*angle;
	}
}
