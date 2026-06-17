import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {

    private static Clip backgroundClip;
    private static boolean muted = false;

    // =============================
    // Background Music
    // =============================
    public static void playBackgroundMusic(String path) {

        if (muted) return;

        try {

            stopBackgroundMusic();

            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(
                            new File(path));

            backgroundClip = AudioSystem.getClip();

            backgroundClip.open(audio);

            // Lower music volume
            FloatControl gainControl =
                    (FloatControl) backgroundClip.getControl(
                            FloatControl.Type.MASTER_GAIN);

            gainControl.setValue(-10.0f);

            backgroundClip.loop(
                    Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================
    // Stop Music
    // =============================
    public static void stopBackgroundMusic() {

        if (backgroundClip != null) {

            backgroundClip.stop();
            backgroundClip.close();
        }
    }

    // =============================
    // Normal Sound Effects
    // =============================
    public static void playSound(String path) {

        if (muted) return;

        try {

            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(
                            new File(path));

            Clip clip = AudioSystem.getClip();

            clip.open(audio);

            FloatControl gainControl =
                    (FloatControl) clip.getControl(
                            FloatControl.Type.MASTER_GAIN);

            gainControl.setValue(4.0f);

            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================
    // Game Over Sound
    // =============================
    public static void playGameOverSound(String path) {

        if (muted) return;

        try {

            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(
                            new File(path));

            Clip clip = AudioSystem.getClip();

            clip.open(audio);

            FloatControl gainControl =
                    (FloatControl) clip.getControl(
                            FloatControl.Type.MASTER_GAIN);

            gainControl.setValue(10.0f);

            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================
    // Mute / Unmute
    // =============================
    public static void toggleMute() {

        muted = !muted;

        if (backgroundClip != null) {

            if (muted) {
                backgroundClip.stop();
            }
            else {
                backgroundClip.loop(
                        Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    public static boolean isMuted() {
        return muted;
    }
}