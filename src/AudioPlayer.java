import javax.sound.sampled.*;
import java.io.InputStream;

public class AudioPlayer {

    private static Clip clip;

    // Toca música em loop
    public static void playLoop(String caminho) {
        stop(); // para música anterior, se houver
        try {
            InputStream audioSrc = AudioPlayer.class.getResourceAsStream(caminho);
            if (audioSrc == null) {
                System.err.println("Arquivo de áudio não encontrado: " + caminho);
                return;
            }
            InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // loop infinito
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Para a música
    public static void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}
