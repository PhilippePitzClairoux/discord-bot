package ca.pitz.commands.usable;

import ca.pitz.commands.DiscordCommand;
import ca.pitz.commands.DiscordCommandInterface;
import ca.pitz.database.Config;
import ca.pitz.database.Guild;
import ca.pitz.database.GuildConfiguration;
import ca.pitz.database.Whitelist;
import ca.pitz.database.WhitelistType;
import ca.pitz.database.repository.*;
import ca.pitz.utils.MessageUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.entities.MemberImpl;
import net.dv8tion.jda.internal.entities.UserById;
import net.dv8tion.jda.internal.entities.UserImpl;
import net.dv8tion.jda.internal.requests.Route.Guilds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AdminCommands implements DiscordCommandInterface {

  private final GuildsRepository guildsRepository;
  private final WhitelistRepository whitelistRepository;
  private final WhitelistTypeRepository whitelistTypeRepository;
  private final GuildConfigurationRepository guildConfigurationRepository;
  private final ConfigsRepository configsRepository;

  private Map<String, String> accessDenied;

  @Autowired
  public AdminCommands(GuildsRepository guildsRepository,
      WhitelistRepository whitelistRepository,
      WhitelistTypeRepository whitelistTypeRepository,
      GuildConfigurationRepository guildConfigurationRepository,
      ConfigsRepository configsRepository) {
    this.guildsRepository = guildsRepository;
    this.whitelistRepository = whitelistRepository;
    this.whitelistTypeRepository = whitelistTypeRepository;
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

    if (userIsPartOfGroup(Objects.requireNonNull(message.getMember()), guild.getName())) {
      MessageUtils.sendMessage(message.getChannel(), accessDenied.get(guild.getName())
      );
      return;
    }

    List<GuildConfiguration> configs = guildConfigurationRepository.findByGuild(guild.getId());
    message.getChannel().sendMessage("Current permissions : ").queue();

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

  @DiscordCommand(name = "!whitelisting", numberOfArgs = "0", help = "!whitelist-status (shows whitelisted users)")
  public void listWhitelists(MessageReceivedEvent message, List<String> args) {
    Guild guild = guildsRepository.findByName(message.getGuild().getName());

    if (userIsPartOfGroup(Objects.requireNonNull(message.getMember()), guild.getName())) {
      MessageUtils.sendMessage(message.getChannel(), accessDenied.get(guild.getName())
      );
      return;
    }

    showWhitelistingTypes(message);
    List<Whitelist> whitelists = whitelistRepository.findByGuild(guild.getId());
    MessageUtils.sendMessage(message.getChannel(), "Current permissions : ");
    for (Whitelist whitelist : whitelists) {
      Guild guild1 = guildsRepository.findById(whitelist.getGuild()).orElse(null);
      WhitelistType type1 = whitelistTypeRepository.findById(whitelist.getType()).orElse(null);

      if (Objects.isNull(guild1) || Objects.isNull(type1)) {
        log.warn("a record was skipped because some info was null.");
        continue;
      }
      MessageUtils.sendFormattedMessage(message.getChannel(), List.of(whitelist.getGroup(),
          type1.getType(),
          guild1.getName()));
    }
  }

  @DiscordCommand(name = "!whitelist", numberOfArgs = "2", help = "!whitelist user [tag] (ex: !whitelist user bob")
  public void whitelists(MessageReceivedEvent message, List<String> args) {
    Guild guild = guildsRepository.findByName(message.getGuild().getName());

    if (userIsPartOfGroup(Objects.requireNonNull(message.getMember()), guild.getName())) {
      MessageUtils.sendMessage(message.getChannel(), accessDenied.get(guild.getName())
      );
      return;
    }

    Member user = message.getGuild().getMembers().stream()
        .filter(member -> member.getUser().getName().contentEquals(args.get(1))).findFirst().orElse(null);

    if (Objects.isNull(user)) {
      MessageUtils.sendMessage(message.getChannel(), "Could not find user id.");
      return;
    }

//    WhitelistType type = whitelistTypeRepository.findByType(args.get(0));
//    whitelistRepository.save(Whitelist.builder()
//        .guild(guild.getId())
//        .type(type.getId())
//        .group(user.getId())
//        .build());
  }

  private void showWhitelistingTypes(MessageReceivedEvent originalMessage) {
    List<WhitelistType> types = whitelistTypeRepository.findAll();

    MessageUtils.sendMessage(originalMessage.getChannel(), "Whitelist types : ");
    for (WhitelistType type : types) {
      MessageUtils.sendFormattedMessage(originalMessage.getChannel(),
          List.of(String.valueOf(type.getId()), type.getType(), type.getDescription()));
    }

  }

  private boolean userIsPartOfGroup(Member member, String guildName) {
    Whitelist group = whitelistRepository.findByGuild(guildName);
    return member.getRoles().stream().filter(role -> role.getName().equals(group.getGroup())).count() >= 1;
  }

  @Override
  public boolean commandIsAvailable() {
    return true;
  }
}
