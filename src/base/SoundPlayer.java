package base;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


/* 音楽を流すためのオブジェクト */
/* 処理速度等の観点から、1つのオブジェクトのみが音楽を流して
   いることを保障したい. そこで、singletonパターンを使用 */
public class SoundPlayer {
    private static Clip clipBGM;
    private static Clip clipSE;
    private static boolean isPlayingBGM = false;

    private static SoundPlayer soundPlayer = new SoundPlayer(); // クラス共通、唯一のインスタンス

    private SoundPlayer() {}

    public static SoundPlayer getInstance() {
        return soundPlayer;
    }

    public void playBGM(String path) {
        /* 既に鳴っているBGMを消す. */
        if(this.isPlayingBGM) { this.killBGM(); }

        this.isPlayingBGM = true;
        clipBGM = createClip(new File(path));
        clipBGM.loop(Clip.LOOP_CONTINUOUSLY); // 無限ループ
    }

    public void playSE(String path) {
        clipSE = createClip(new File(path));
        clipSE.start();
    }

    public void killBGM() {
        clipBGM.close();
    }

    public void killSE() {
        clipSE.close();
    }

    public static Clip createClip(File path) {
        /* ファイルクラスから音楽再生を行う準備 */
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(path)) {
            AudioFormat af = ais.getFormat();
            DataLine.Info dataLine = new DataLine.Info(Clip.class,af);
            Clip c = (Clip)AudioSystem.getLine(dataLine);
            c.open(ais);
            return c;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        return null;
    }
}