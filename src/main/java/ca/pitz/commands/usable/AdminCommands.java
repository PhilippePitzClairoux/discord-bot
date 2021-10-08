package ca.pitz.commands.usable;

import ca.pitz.commands.DiscordCommand;
import ca.pitz.commands.DiscordCommandInterface;
import ca.pitz.database.Guild;
import ca.pitz.database.GuildConfiguration;
import ca.pitz.database.Whitelist;
import ca.pitz.database.WhitelistType;
import ca.pitz.database.repository.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminCommands implements DiscordCommandInterface {

    private final GuildsRepository guildsRepository;
    private final WhitelistRepository whitelistRepository;
    private final WhitelistTypeRepository whitelistTypeRepository;
    private final GuildConfigurationRepository guildConfigurationRepository;
    private final ConfigsRepository configsRepository;

    @Autowired
    public AdminCommands(GuildsRepository guildsRepository,
                         WhitelistRepository whitelistRepository,
                         WhitelistTypeRepository whitelistTypeRepository, GuildConfigurationRepository guildConfigurationRepository, ConfigsRepository configsRepository) {
        this.guildsRepository = guildsRepository;
        this.whitelistRepository = whitelistRepository;
        this.whitelistTypeRepository = whitelistTypeRepository;
        this.guildConfigurationRepository = guildConfigurationRepository;
        this.configsRepository = configsRepository;
    }


    @DiscordCommand(name = "!server-config", numberOfArgs = "0", help = "!server-config (shows server configs)")
    public void serverConfigs(MessageReceivedEvent message, List<String> args) {
        Guild guild = guildsRepository.findByName(message.getGuild().getName());

        List<GuildConfiguration> configs = guildConfigurationRepository.findByGuild(guild.getId());
        message.getChannel().sendMessage("Current permissions : ").queue();

        for (GuildConfiguration configuration : configs) {
            message.getChannel().sendMessage(String.format("%s  -  %s  -  %s",
                    guildsRepository.findById(configuration.getGuild()),
                    configsRepository.findById(configuration.getConfig()),
                    configuration.isEnabled())).queue();
        }
    }

    @DiscordCommand(name = "!whitelisting", numberOfArgs = "0", help = "!whitelist-status (shows whitelisted users)")
    public void listWhitelists(MessageReceivedEvent message, List<String> args) {
        Guild guild = guildsRepository.findByName(message.getGuild().getName());

        if (whitelistRepository.findByUsername(message.getAuthor().getName()) == null) {
            message.getChannel().sendMessage("Fuck out of here boy, you don't have the right to do this.").queue();
            return;
        }

        List<Whitelist> whitelists = whitelistRepository.findByGuild(guild.getId());
        message.getChannel().sendMessage("Current permissions : ").queue();

        for (Whitelist whitelist : whitelists) {
            message.getChannel().sendMessage(String.format("%s  -  %s  -  %s", whitelist.getUsername(),
                    guildsRepository.findById(whitelist.getGuild()),
                    whitelistTypeRepository.findById(whitelist.getType()))).queue();
        }
    }

    @DiscordCommand(name = "!whitelist", numberOfArgs = "2", help = "!whitelist user [name] (ex: !whitelist user bob")
    public void whitelists(MessageReceivedEvent message, List<String> args) {
        Guild guild = guildsRepository.findByName(message.getGuild().getName());

        if (whitelistRepository.findByUsername(message.getAuthor().getName()) == null) {
            message.getChannel().sendMessage("Fuck out of here boy, you don't have the right to do this.").queue();
            return;
        }

        WhitelistType type = whitelistTypeRepository.findByType(args.get(0));
       whitelistRepository.save(Whitelist.builder()
                       .guild(guild.getId())
                       .type(type.getId())
                       .username(message.getAuthor().getName())
               .build());
    }


    @Override
    public boolean commandIsAvailable() {
        return true;
    }
}
