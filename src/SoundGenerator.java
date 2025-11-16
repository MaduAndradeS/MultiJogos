import javax.sound.sampled.*;

public class SoundGenerator {
    public static void playTone(double freq, int durationMs) {
        try {
            float sampleRate = 44100;
            byte[] buf = new byte[(int) (durationMs * sampleRate / 1000)];

            for (int i = 0; i < buf.length; i++) {
                double angle = 2.0 * Math.PI * i * freq / sampleRate;
                buf[i] = (byte) (Math.sin(angle) * 127);
            }

            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);

            line.open(format);
            line.start();
            line.write(buf, 0, buf.length);
            line.drain();
            line.close();
        } catch (Exception e) {
            System.out.println("Erro ao tocar som: " + e.getMessage());
        }
    }
}
