package ca.pitz.commands.music;

import ca.pitz.utils.MessageUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;

public class AudioLoadResultHandlerImpl implements AudioLoadResultHandler {

  private final TextChannel textChannel;
  private final GuildMusicManager guildMusicManager;
  private final boolean search;

  public AudioLoadResultHandlerImpl(TextChannel textChannel,
                                    GuildMusicManager guildMusicManager, boolean search) {
    this.textChannel = textChannel;
    this.guildMusicManager = guildMusicManager;
    this.search = search;
  }

  @Override
  public void trackLoaded(AudioTrack audioTrack) {
    MessageUtils.sendMessage(textChannel,"Song added to the queue.");
    guildMusicManager.queueTrack(audioTrack);
  }

  @Override
  public void playlistLoaded(AudioPlaylist audioPlaylist) {

    if (search) {
      this.trackLoaded(audioPlaylist.getTracks().get(0));
      return;
    }

    MessageUtils.sendMessage(textChannel,"Added playlist to queue.");
    audioPlaylist.getTracks().forEach(guildMusicManager::queueTrack);
  }

  @Override
  public void noMatches() {
    textChannel.sendMessage("Could not find anything matching your search.").queue();
  }

  @Override
  public void loadFailed(FriendlyException e) {
    textChannel.sendMessage("Could not load song : " + e.getCause()).queue();
  }
}
