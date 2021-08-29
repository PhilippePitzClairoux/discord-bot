package ca.pitz.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MusicManager extends AudioEventAdapter {

  private final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();
  private final AudioPlayer audioPlayer;

  public MusicManager(AudioPlayer audioPlayer) {
    this.audioPlayer = audioPlayer;
  }

  public void queueTrack(AudioTrack track) {
    if (!audioPlayer.startTrack(track, true)) {
      queue.offer(track);
    }
  }

  public void nextTrack() {
    // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
    // giving null to startTrack, which is a valid argument and will simply stop the player.
    audioPlayer.startTrack(queue.poll(), false);
  }

  public void clearTracks() {
    audioPlayer.stopTrack();
    queue.clear();
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
    if (endReason.mayStartNext) {
      nextTrack();
    }
  }

  public boolean isPlaying() {
    return !audioPlayer.isPaused();
  }

}
