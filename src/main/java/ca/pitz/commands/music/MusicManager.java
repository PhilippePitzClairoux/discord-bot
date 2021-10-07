package ca.pitz.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import net.dv8tion.jda.api.entities.TextChannel;

public class MusicManager extends AudioEventAdapter {

  private static final String CURRENT_SONG_INFO = "Current song playing : %s (%s)\nSong Duration : %s\n";

  private final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();
  private final AudioPlayer audioPlayer;
  private final TextChannel textChannel;

  public MusicManager(AudioPlayer audioPlayer, TextChannel textChannel) {
    this.audioPlayer = audioPlayer;
    this.textChannel = textChannel;
  }

  public void queueTrack(AudioTrack track) {
    queue.offer(track);

    if (audioPlayer.getPlayingTrack() == null) {
      nextTrack();
    }
  }

  public void nextTrack() {
    // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
    // giving null to startTrack, which is a valid argument and will simply stop the player.
    audioPlayer.startTrack(queue.poll(), false);
    displayCurrentSong();
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

  @Override
  public void onTrackStart(AudioPlayer player, AudioTrack track) {
//    super.onTrackStart(player, track);
    displayCurrentSong();
  }



  public void displayCurrentSong() {
    AudioTrack track = audioPlayer.getPlayingTrack();

    if (track != null) {
      AudioTrackInfo info = track.getInfo();
      long duration = info.length / 1000;
      String message = String.format(CURRENT_SONG_INFO,
          info.title,
          info.author,
          String
              .format("%dh %02dm %02ds", duration / 3600, (duration % 3600) / 60, (duration % 60)));
      textChannel.sendMessage(message).queue();
    }
  }

  public boolean isPlaying() {
    return audioPlayer.getPlayingTrack() != null;
  }

}
