package ca.pitz.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;

public class AudioLoadResultHandlerImpl implements AudioLoadResultHandler {

  private final TextChannel textChannel;
  private final MusicManager musicManager;

  public AudioLoadResultHandlerImpl(TextChannel textChannel,
      MusicManager musicManager) {
    this.textChannel = textChannel;
    this.musicManager = musicManager;
  }

  @Override
  public void trackLoaded(AudioTrack audioTrack) {
    textChannel.sendMessage("Song added to the queue.").queue();
    musicManager.queueTrack(audioTrack);
  }

  @Override
  public void playlistLoaded(AudioPlaylist audioPlaylist) {
    textChannel.sendMessage("Added playlist to queue.").queue();
    audioPlaylist.getTracks().forEach(musicManager::queueTrack);
  }

  @Override
  public void noMatches() {
    textChannel.sendMessage("Could not find anything matching your search.").queue();
  }

  @Override
  public void loadFailed(FriendlyException e) {
    textChannel.sendMessage("Could not load file : " + e.getMessage()).queue();
  }
}
