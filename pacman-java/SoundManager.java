import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {

    private static Clip backgroundClip;
    private static boolean muted = false;

    public static void playBackgroundMusic(String path) {

        if (muted) return;

        try {

            stopBackgroundMusic();

            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(
                            new File(path));

            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audio);

                backgroundClip.loop(
                    Clip.LOOP_CONTINUOUSLY);
                backgroundClip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopBackgroundMusic() {

        if (backgroundClip != null) {
            backgroundClip.stop();
            backgroundClip.close();
        }
    }

    public static void playSound(String path) {

        if (muted) return;

        try {

            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(
                            new File(path));

            Clip clip = AudioSystem.getClip();

            clip.open(audio);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void toggleMute() {

        muted = !muted;

        if (backgroundClip != null) {

            if (muted) {
                backgroundClip.stop();
            } else {
                backgroundClip.loop(
                        Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    public static boolean isMuted() {
        return muted;
    }
}