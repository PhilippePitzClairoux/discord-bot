package ca.pitz.commands.usable;

import ca.pitz.commands.DiscordCommand;
import ca.pitz.commands.DiscordCommandInterface;
import ca.pitz.commands.music.AudioLoadResultHandlerImpl;
import ca.pitz.commands.music.AudioPlayerSendHandler;
import ca.pitz.commands.music.MusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Groovy implements DiscordCommandInterface {

  private MusicManager musicManager;
  private final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();

  public Groovy() {
    AudioSourceManagers.registerLocalSource(audioPlayerManager);
    AudioSourceManagers.registerRemoteSources(audioPlayerManager);
  }

  @DiscordCommand(name = "!play", numberOfArgs = "1", help = "!play [youtube link]")
  public void playVideo(MessageReceivedEvent message, List<String> args)  {
    User author = message.getAuthor();
    TextChannel textChannel = message.getTextChannel();
    Optional<VoiceChannel> channel = message.getJDA().getVoiceChannels().stream()
        .filter(voiceChannel -> voiceChannel.getMembers().stream().filter(member -> member.getUser().equals(author)).count() == 1)
        .findFirst();

    if (channel.isEmpty()) {
      message.getChannel()
          .sendMessage("Cannot play a song while not being in a voice chat.")
          .queue();
      return;
    }

    if (this.musicManager == null) {
      //connect to voice chat
      VoiceChannel confirmedChannel = channel.get();
      AudioPlayer audioPlayer = audioPlayerManager.createPlayer();
      AudioPlayerSendHandler audioPlayerSendHandler = new AudioPlayerSendHandler(audioPlayer);
      this.musicManager = new MusicManager(audioPlayer, message.getTextChannel());

      AudioManager audioManager = confirmedChannel.getGuild().getAudioManager();
      audioManager.setSendingHandler(audioPlayerSendHandler);
      audioManager.openAudioConnection(confirmedChannel);
    }

    String trackUrl = args.get(0);
    audioPlayerManager.loadItem(trackUrl, new AudioLoadResultHandlerImpl(textChannel, this.musicManager));
  }

  @DiscordCommand(name = "!skip", numberOfArgs = "0", help = "!skip (skips current song)")
  public void skipVideo(MessageReceivedEvent message, List<String> args) {
    if (musicManager.isPlaying()) {
      musicManager.nextTrack();
    } else {
      message.getChannel().sendMessage("Cannot skip song, there's nothing playing right now.").queue();
    }
  }

  @DiscordCommand(name = "!die", numberOfArgs = "0", help = "!die (kills the music)")
  public void die(MessageReceivedEvent message, List<String> args) {
    message.getGuild().getAudioManager().closeAudioConnection();
    message.getChannel().sendMessage("Cya later aligator!").queue();
    this.musicManager = null;
  }

  @Override
  public boolean commandIsAvailable() {
    return true;
  }
}
