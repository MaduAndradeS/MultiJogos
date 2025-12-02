import javax.sound.sampled.*;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AudioPlayer {

    private static final Logger LOGGER = Logger.getLogger(AudioPlayer.class.getName());
    private static Clip clip;

    public static void playLoop(String caminho) {
        stop(); // para música anterior, se houver

        try {
            InputStream audioSrc = AudioPlayer.class.getResourceAsStream(caminho);
            if (audioSrc == null) {
                LOGGER.warning("Arquivo de áudio não encontrado: " + caminho);
                return;
            }

            InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao tentar reproduzir áudio: " + caminho, e);
        }
    }

    public static void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}
