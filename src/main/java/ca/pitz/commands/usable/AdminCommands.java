package ca.pitz.commands.usable;

import ca.pitz.commands.DiscordCommand;
import ca.pitz.commands.DiscordCommandInterface;
import ca.pitz.database.Config;
import ca.pitz.database.Guild;
import ca.pitz.database.GuildConfiguration;
import ca.pitz.database.Whitelist;
import ca.pitz.database.repository.*;
import ca.pitz.utils.MessageUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AdminCommands implements DiscordCommandInterface {

  private static final String DEFAULT_ACCESS_DENIED = "Whitelisting is not enabled. Cannot do this command.";

  private final GuildsRepository guildsRepository;
  private final WhitelistRepository whitelistRepository;
  private final GuildConfigurationRepository guildConfigurationRepository;
  private final ConfigsRepository configsRepository;

  private final Map<String, String> accessDenied;

  @Autowired
  public AdminCommands(GuildsRepository guildsRepository,
      WhitelistRepository whitelistRepository,
      GuildConfigurationRepository guildConfigurationRepository,
      ConfigsRepository configsRepository) {
    this.guildsRepository = guildsRepository;
    this.whitelistRepository = whitelistRepository;
    this.guildConfigurationRepository = guildConfigurationRepository;
    this.configsRepository = configsRepository;

    accessDenied = new HashMap<>();
    loadWarningMessages();
  }

  private void loadWarningMessages() {
    List<GuildConfiguration> guildConfigurations = guildConfigurationRepository.findByConfig(2);

    for (GuildConfiguration configuration : guildConfigurations) {
      Guild guild = guildsRepository.findById(configuration.getGuild()).orElse(null);

      if (!Objects.isNull(guild)) {
        accessDenied.put(guild.getName(), configuration.getExtra());
      }
    }
  }

  @DiscordCommand(name = "!server-config", numberOfArgs = "0", help = "!server-config (shows server configs)")
  public void serverConfigs(MessageReceivedEvent message, List<String> args) {
    Guild guild = guildsRepository.findByName(message.getGuild().getName());

    if (userNotPartOfGroup(Objects.requireNonNull(message.getMember()), guild)) {
      MessageUtils.sendMessage(message.getChannel(), accessDenied.get(message.getGuild().getName())
      );
      return;
    }

    List<GuildConfiguration> configs = guildConfigurationRepository.findByGuild(guild.getId());
    message.getChannel().sendMessage("Current settings : ").queue();

    for (GuildConfiguration configuration : configs) {
      Guild guild1 = guildsRepository.findById(configuration.getGuild()).orElse(null);
      Config config = configsRepository.findById(configuration.getConfig()).orElse(null);

      if (Objects.isNull(guild1) || Objects.isNull(config)) {
        log.warn("a record was skipped because some info was null.");
        return;
      }

      MessageUtils.sendFormattedMessage(message.getChannel(),
          List.of(guild1.getName(), config.getConfig(), String.valueOf(configuration.isEnabled())));
    }
  }


  @DiscordCommand(name = "!whitelisting-enable", numberOfArgs = "2", help = "!whitelist-enable [group] [error message] (shows whitelisted users)")
  public void listWhitelistsEnable(MessageReceivedEvent message, List<String> args) {
    Config config = configsRepository.findByConfig("whitelisting");
    Guild guild = guildsRepository.findByName(message.getGuild().getName());

    if (Objects.isNull(guild)) {
      guild = guildsRepository.save(Guild.builder()
          .name(message.getGuild().getName())
          .build());
    }

    if (Objects.isNull(whitelistRepository.findByGuild(guild.getId()))) {
      MessageUtils.sendMessage(message.getChannel(), "Whitelisting already enabled dummy.");
      return;
    }

    guildConfigurationRepository.save(GuildConfiguration.builder()
        .config(config.getId())
        .enabled(true)
        .extra(args.get(1))
        .guild(guild.getId())
        .build());

    whitelistRepository.save(Whitelist.builder()
        .group(args.get(0))
        .guild(guild.getId())
        .build());

    accessDenied.put(guild.getName(), args.get(1));

    MessageUtils.sendMessage(message.getChannel(), String
        .format("Whitelisting enabled for %s. Default group is %s", guild.getName(), args.get(0)));
  }

  @DiscordCommand(name = "!whitelisting", numberOfArgs = "0", help = "!whitelist-status (shows whitelisted users)")
  public void listWhitelists(MessageReceivedEvent message, List<String> args) {
    Guild guild = guildsRepository.findByName(message.getGuild().getName());

    if (userNotPartOfGroup(Objects.requireNonNull(message.getMember()), guild)) {
      showPermissionError(message.getTextChannel(), guild);
      return;
    }

    List<Whitelist> whitelists = whitelistRepository.findByGuild(guild.getId());
    MessageUtils.sendMessage(message.getChannel(), "Current permissions : ");
    for (Whitelist whitelist : whitelists) {
      Guild guild1 = guildsRepository.findById(whitelist.getGuild()).orElse(null);

      if (Objects.isNull(guild1)) {
        log.warn("a record was skipped because some info was null.");
        continue;
      }
      MessageUtils.sendFormattedMessage(message.getChannel(), List.of(whitelist.getGroup(),
          guild1.getName()));
    }
  }

  @DiscordCommand(name = "!whitelist", numberOfArgs = "1", help = "!whitelist group (ex: !whitelist group")
  public void whitelists(MessageReceivedEvent message, List<String> args) {
    Guild guild = guildsRepository.findByName(message.getGuild().getName());

    if (Objects.isNull(guild) || userNotPartOfGroup(Objects.requireNonNull(message.getMember()),
        guild)) {
      showPermissionError(message.getTextChannel(), guild);
      return;
    }

    whitelistRepository.save(Whitelist.builder()
        .guild(guild.getId())
        .group(args.get(0))
        .build());

    MessageUtils.sendMessage(message.getChannel(), "Groupe successfully added.");
  }

  @DiscordCommand(name = "!whitelist-remove", numberOfArgs = "1", help = "!whitelist group (ex: !whitelist group")
  public void whitelistRemove(MessageReceivedEvent message, List<String> args) {
    Guild guild = guildsRepository.findByName(message.getGuild().getName());

    if (userNotPartOfGroup(Objects.requireNonNull(message.getMember()), guild)) {
      showPermissionError(message.getTextChannel(), guild);
      return;
    }

    int guildId = guildsRepository.findByName(guild.getName()).getId();

    whitelistRepository.removeByGroupAndGuild(args.get(0), guildId);
    MessageUtils.sendMessage(message.getChannel(), "Groupe successfully added.");
  }

  private boolean whitelistingEnabledForGuild(Guild guild) {
    Config config = configsRepository.findByConfig("whitelisting");

    return guildConfigurationRepository
        .existsByGuildAndEnabledAndConfig(guild.getId(), true, config.getId());

  }

  private boolean userNotPartOfGroup(Member member, Guild guild) {

    if (Objects.isNull(guild)) {
      return true;
    }

    List<Whitelist> whitelists = whitelistRepository.findByGuild(guild.getId());
    List<String> groups = whitelists.stream()
        .flatMap(whitelist -> Stream.of(whitelist.getGroup())).collect(Collectors.toList());
    return member.getRoles().stream().flatMap(role -> Stream.of(role.getName()))
        .collect(Collectors.toList())
        .stream().filter(groups::contains).count() < 1 || !whitelistingEnabledForGuild(guild);
  }

  private void showPermissionError(TextChannel channel, Guild guild) {
    String message;

    if (Objects.isNull(guild)) {
      message = DEFAULT_ACCESS_DENIED;
    } else {
      message = accessDenied.get(guild.getName());
    }

    MessageUtils.sendMessage(channel, message);
  }

  @Override
  public boolean commandIsAvailable() {
    return true;
  }
}
