package ca.pitz.commands.usable;

import ca.pitz.commands.DiscordCommand;
import ca.pitz.commands.DiscordCommandInterface;
import ca.pitz.commands.music.AudioLoadResultHandlerImpl;
import ca.pitz.commands.music.AudioPlayerSendHandler;
import ca.pitz.commands.music.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import lombok.SneakyThrows;
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

  private final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
  private final HashMap<String, GuildMusicManager> guildMusic = new HashMap<>();

  public Groovy() {
    AudioSourceManagers.registerLocalSource(audioPlayerManager);
    AudioSourceManagers.registerRemoteSources(audioPlayerManager);
  }

  @SneakyThrows
  @DiscordCommand(name = "!play", numberOfArgs = "-1", help = "!play [youtube link]")
  public void playVideo(MessageReceivedEvent message, List<String> args) {
    User author = message.getAuthor();
    TextChannel textChannel = message.getTextChannel();
    Optional<VoiceChannel> channel = message.getGuild().getVoiceChannels().stream()
        .filter(voiceChannel ->
            voiceChannel.getMembers().stream().filter(member -> member.getUser().equals(author))
                .count() == 1)
        .findFirst();

    if (channel.isEmpty()) {
      message.getChannel()
          .sendMessage("Cannot play a song while not being in a voice chat.")
          .queue();
      return;
    }

    GuildMusicManager guildMusicManager = getGuildMusicManager(message.getGuild().getName(),
        channel.get(), textChannel);

    String trackUrl = buildSearchUrl(args);
    boolean songLoaded = false;
    for (int i = 0; i < 10; i++) {
      try {
        audioPlayerManager.loadItemOrdered(guildMusicManager, trackUrl,
            new AudioLoadResultHandlerImpl(textChannel,
                guildMusicManager,
                args.size() > 1)
        );
        songLoaded = true;
        break;
      } catch (FriendlyException e) {
        Thread.sleep(5000);
        log.info("Could not load song...");
      }
    }

    if (!songLoaded) {
      message.getChannel().sendMessage("Could not load the song. Please rety :(").queue();
    }

  }

  @DiscordCommand(name = "!skip", numberOfArgs = "0", help = "!skip (skips current song)")
  public void skipVideo(MessageReceivedEvent message, List<String> args) {
    GuildMusicManager guildMusicManager = guildMusic.get(message.getGuild().getName());

    if (guildMusicManager.isPlaying()) {
      guildMusicManager.nextTrack();
    } else {
      message.getChannel().sendMessage("Cannot skip song, there's nothing playing right now.")
          .queue();
    }
  }

  @DiscordCommand(name = "!die", numberOfArgs = "0", help = "!die (kills the music)")
  public void die(MessageReceivedEvent message, List<String> args) {
    message.getGuild().getAudioManager().closeAudioConnection();
    message.getChannel().sendMessage("Cya later aligator!").queue();
    guildMusic.remove(message.getGuild().getName());

  }

  @DiscordCommand(name = "!status", numberOfArgs = "0", help = "!status (kills the music)")
  public void status(MessageReceivedEvent message, List<String> args) {
    GuildMusicManager manager = guildMusic.get(message.getGuild().getName());
    message.getChannel().sendMessage(Objects.isNull(manager) ? "Bot is not connected right now."
        : String.valueOf(manager.isPlaying())).queue();

  }

  @Override
  public boolean commandIsAvailable() {
    return true;
  }

  private String buildSearchUrl(List<String> args) {
    String url = args.get(0);
    if (args.size() > 1 || !args.get(0).contains("youtub")) {
      url = "ytsearch: ";
      for (String arg : args) {
        url = url.concat(arg).concat(" ");
      }
    }

    return url;
  }

  private GuildMusicManager getGuildMusicManager(String guildName, VoiceChannel channel,
      TextChannel textChannel) {

    GuildMusicManager musicManager = guildMusic.getOrDefault(guildName, null);

    if (musicManager == null) {
      //connect to voice chat
      AudioPlayer audioPlayer = audioPlayerManager.createPlayer();
      AudioManager voiceChat = channel.getGuild().getAudioManager();
      AudioPlayerSendHandler audioPlayerSendHandler = new AudioPlayerSendHandler(audioPlayer);
      musicManager = new GuildMusicManager(audioPlayer, textChannel, voiceChat);

      //Tell audioPlayer to notify musicManager of events happening
      audioPlayer.addListener(musicManager);
      voiceChat.setSendingHandler(audioPlayerSendHandler);
      voiceChat.openAudioConnection(channel);
      guildMusic.put(guildName, musicManager);
    }

    return musicManager;
  }

}
