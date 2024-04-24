package com.thena3ik.weatherbot.commands;

import com.thena3ik.weatherbot.image.ImageGenerator;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        if (command.equals("ping")) {
            event.deferReply().setEphemeral(true).queue();
            event.getHook().sendMessage("Pong!").queue();

        } else if (command.equals("weather")) {

            OptionMapping messageOption = event.getOption("city");
            assert messageOption != null;

            String city = messageOption.getAsString();
            city = city.substring(0, 1).toUpperCase() + city.substring(1).toLowerCase();

            event.deferReply().queue();

            try {
                event.getHook().sendMessage("This is the weather in " + city + "!")
                        .addFiles(FileUpload.fromData(new ImageGenerator(city).getAsFile()))
                        .addActionRow(Button.secondary("changeAppearance", "Change appearance (black/white)"))
                        .queue(null, error -> event.getHook().sendMessage("Failed to send the weather information. Please try again.")
                                .setEphemeral(true)
                                .queue());
            } catch (IOException e) {
                event.getHook().sendMessage("An error occurred while processing your request.")
                        .setEphemeral(true)
                        .queue();
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("changeAppearance")) {
            ImageGenerator.changeTheme();
            try {
                event.editMessage(MessageEditData.fromFiles(FileUpload.fromData(new ImageGenerator().getAsFile())))
                        .queue();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("ping", "Default ping-pong game :)"));

        // Command: /weather: <city>
        OptionData option1 = new OptionData(OptionType.STRING, "city",
                "City where you want to check the weather", true);

        commandData.add(Commands.slash("weather", "Shows an actual weather").addOptions(option1));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
