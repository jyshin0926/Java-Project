import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MusicPlayer {
	
	public void music(String file, Clip c) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
			c.open(ais);
			c.start();		
		}
		catch(Exception e) {
			
		}
	}
	public void musicRepeat(String file) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
			Clip c = AudioSystem.getClip();
			c.open(ais);
			c.start();		
		}
		catch(Exception e) {
			
		}
	}
	public void stop(Clip c) {
		c.stop();
		c.close();
	}
}
