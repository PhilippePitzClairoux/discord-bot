package ca.pitz.commands.music;

import ca.pitz.utils.MessageUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class GuildMusicManager extends AudioEventAdapter {

    private static final String CURRENT_SONG_INFO = "Current song playing : %s (%s)\nSong Duration : %s\n";

    private final LinkedList<AudioTrack> queue = new LinkedList<>();
    private final AudioPlayer audioPlayer;
    private final TextChannel textChannel;
    private final AudioManager audioManager;

    public GuildMusicManager(AudioPlayer audioPlayer, TextChannel textChannel, AudioManager audioManager) {
        this.audioPlayer = audioPlayer;
        this.textChannel = textChannel;
        this.audioManager = audioManager;
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
    }

    public void clearTracks() {
        audioPlayer.stopTrack();
        queue.clear();
    }


    @Override
    public void onPlayerPause(AudioPlayer player) {
//    super.onPlayerPause(player);
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
//    super.onPlayerResume(player);
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace) {
        super.onTrackStuck(player, track, thresholdMs, stackTrace);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }

        //if queue is empty, disconnect bot from voice chat
        if (queue.isEmpty()) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    audioManager.closeAudioConnection();
                }
            }, 500);

        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        displayCurrentSong();
    }

    @SneakyThrows
    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        MessageUtils.sendMessage(textChannel,"Track stucked, clearing queue. Please retry.");
        this.nextTrack();
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        MessageUtils.sendMessage(textChannel,"Track exception...Beep Boop");
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
