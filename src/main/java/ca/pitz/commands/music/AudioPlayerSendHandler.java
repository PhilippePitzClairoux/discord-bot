package ca.pitz.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioPlayerSendHandler implements AudioSendHandler {
  private final AudioPlayer audioPlayer;
  private final MutableAudioFrame frame;
  private final ByteBuffer buffer;

  public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
    this.audioPlayer = audioPlayer;
    this.buffer = ByteBuffer.allocate(1024);
    this.frame = new MutableAudioFrame();
    this.frame.setBuffer(this.buffer);
  }

  @Override
  public boolean canProvide() {
    return audioPlayer.provide(frame);
  }

  @Override
  public ByteBuffer provide20MsAudio() {
    ((Buffer) buffer).flip();
    return buffer;
  }

  @Override
  public boolean isOpus() {
    return true;
  }
}
