package dev.zawarudo.holo.music.cmds;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.misc.Emote;
import dev.zawarudo.holo.music.AbstractMusicCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Command(name = "join",
        description = "Makes me join the voice channel you are currently in.",
        category = CommandCategory.MUSIC)
public class JoinCmd extends AbstractMusicCommand {

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        deleteInvoke(event);

        AudioManager audioManager = event.getGuild().getAudioManager();
        EmbedBuilder builder = new EmbedBuilder();

        if (audioManager.getConnectedChannel() != null) {
            String msg = "I'm already connected to a voice channel!\nJoin me in " + audioManager.getConnectedChannel().getAsMention() + "!";
            sendErrorEmbed(event, msg);
            return;
        }

        Member member = Objects.requireNonNull(event.getMember());

        if (!isUserInAudioChannel(member)) {
            sendErrorEmbed(event, "You need to be in a voice channel to use this command!");
            return;
        }

        audioManager.openAudioConnection(getMemberVoiceState(member).getChannel());

        builder.setTitle("Connected " + Emote.NOTE.getAsText());
        builder.setDescription("Join me in " + audioManager.getConnectedChannel().getAsMention());

        sendEmbed(event, builder, true, 5, TimeUnit.MINUTES);
    }
}