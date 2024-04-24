package com.thena3ik.weatherbot.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListener extends ListenerAdapter {
   /* @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        else {
            String user = event.getAuthor().getAsMention();
            String message = event.getMessage().getContentRaw();
            String channel = event.getGuildChannel().getAsMention();

            event.getChannel().sendMessage("User " + user + " sends a message: `" + message + "` in " +
                            channel + "!").queue();
        }
    } */
}
