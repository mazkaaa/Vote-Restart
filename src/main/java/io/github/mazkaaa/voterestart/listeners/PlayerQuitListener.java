package io.github.mazkaaa.voterestart.listeners;

import io.github.mazkaaa.voterestart.VoteCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private VoteCommand voteCommand;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        if (voteCommand.PlayerWhoVoteYes.contains(event.getPlayer().getName())){
            voteCommand.PlayerWhoVoteYes.remove(event.getPlayer().getName());
        } else if (voteCommand.PlayerWhoVoteNo.contains(event.getPlayer().getName())){
            voteCommand.PlayerWhoVoteNo.remove(event.getPlayer().getName());
        }
        if (voteCommand.VoteStarter.equals(event.getPlayer().getName())){
            voteCommand.CancelVote();
            Bukkit.broadcastMessage(ChatColor.RED + "The votes cancelled because the vote starter is leaving the server! you can start another votes.");
        }
    }
}
