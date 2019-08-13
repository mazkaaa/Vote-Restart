package io.github.mazkaaa.voterestart;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class VoteCommand implements CommandExecutor{
    Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

    public boolean VoteHasStarted = false;
    public String VoteStarter;
    public ArrayList<String> PlayerWhoVoteYes = new ArrayList<>();
    public ArrayList<String> PlayerWhoVoteNo = new ArrayList<>();
    public ArrayList<String> PlayersOnline = new ArrayList<>();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("voterestart")){
            if (sender instanceof Player){
                Player p = (Player) sender;
                if (args.length == 0){
                    if (VoteHasStarted){
                        p.sendMessage(ChatColor.RED + "Vote has already started!");
                        p.sendMessage(ChatColor.YELLOW + "type " + ChatColor.BOLD + "/voterestart yes" + ChatColor.RESET + ChatColor.YELLOW + " to accept or " + ChatColor.BOLD + "/voterestart no" + ChatColor.RESET + ChatColor.YELLOW + " to decline.");


                    } else {
                        if (PlayerWhoVoteYes.contains(p.getName()) || PlayerWhoVoteNo.contains(p.getName())){
                            p.sendMessage(ChatColor.RED + "You already voted! please wait until the votes completed.");
                        } else {
                            VoteHasStarted = true;
                            VoteStarter = p.getName();
                            p.sendMessage(ChatColor.GREEN + "You started the vote!");
                            p.sendMessage(ChatColor.YELLOW + "type " + ChatColor.BOLD + "/voterestart yes" + ChatColor.RESET + ChatColor.YELLOW + " to accept or " + ChatColor.BOLD + "/voterestart no" + ChatColor.RESET + ChatColor.YELLOW + " to decline.");

                            Bukkit.broadcastMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + VoteStarter + ChatColor.RESET + ChatColor.YELLOW + " started votes to reboot the server!");
                            Bukkit.broadcastMessage(ChatColor.YELLOW + "type " + ChatColor.BOLD + "/voterestart yes" + ChatColor.RESET + ChatColor.YELLOW + " to accept or " + ChatColor.BOLD + "/voterestart no" + ChatColor.RESET + ChatColor.YELLOW + " to decline.");
                            StartTheVoteBroadcast();
                        }
                    }
                } else {
                    if (args[0].equalsIgnoreCase("yes")){
                        if (VoteHasStarted){
                            if (PlayerWhoVoteYes.contains(p.getName()) || PlayerWhoVoteNo.contains(p.getName())){
                                p.sendMessage(ChatColor.RED + "You already voted! please wait until the votes completed.");
                            } else {
                                PlayerWhoVoteYes.add(p.getName());
                                p.sendMessage(ChatColor.GREEN + "You voted " + ChatColor.BOLD + "YES" + ChatColor.RESET + ChatColor.GREEN + ".");
                                Bukkit.broadcastMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + p.getName() + ChatColor.RESET + ChatColor.YELLOW + "choose " + ChatColor.GREEN + ChatColor.BOLD + "YES" +ChatColor.RESET + ChatColor.YELLOW + " to reboot the server. (" + (PlayerWhoVoteYes.size() + PlayerWhoVoteNo.size()) + "/" + PlayersOnline.size() + ")");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "You must start the vote first!");
                            p.sendMessage(ChatColor.GRAY + "type /voterestart to start the vote.");
                        }
                    }
                    else if (args[0].equalsIgnoreCase("no")) {
                        if (VoteHasStarted) {
                            if (PlayerWhoVoteNo.contains(p.getName()) || PlayerWhoVoteYes.contains(p.getName())) {
                                p.sendMessage(ChatColor.RED + "You already voted! please wait until the votes completed.");
                            } else {
                                PlayerWhoVoteNo.add(p.getName());
                                p.sendMessage(ChatColor.GREEN + "You voted " + ChatColor.RED + ChatColor.BOLD + "NO" + ChatColor.RESET + ChatColor.GREEN + ".");
                                Bukkit.broadcastMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + p.getName() + ChatColor.RESET + ChatColor.YELLOW + "choose " + ChatColor.RED + ChatColor.BOLD + "NO" + ChatColor.RESET + ChatColor.YELLOW + " to reboot the server. (" + (PlayerWhoVoteYes.size() + PlayerWhoVoteNo.size()) + "/" + PlayersOnline.size() + ")");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "You must start the vote first!");
                            p.sendMessage(ChatColor.GRAY + "type /voterestart to start the vote.");
                        }

                    } else if(args[0].equalsIgnoreCase("cancel")){
                        if (VoteHasStarted){
                            if (VoteStarter.equals(p.getName())){
                                CancelVote();
                                Bukkit.broadcastMessage(ChatColor.RED + "Votes cancelled!");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "You must start the vote first!");
                            p.sendMessage(ChatColor.GRAY + "type /voterestart to start the vote.");
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "Unknown Command!");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You are not allowed to run this command because you are not player!");
            }
        }
        return false;
    }

    public void StartTheVoteBroadcast(){
        final int[] DelayValue = {15};
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(VoteRestart.getPlugin(), new Runnable() {
            @Override
            public void run() {
                PutOnlinePlayerToList();
                StartTheVoteCheck();
                DelayValue[0]--;
                if (DelayValue[0] == 0){ //broadcasting message
                    PrintRequiredMsg();
                    DelayValue[0] = 15;
                }
            }
        }, 0L, 20L);
    }

    public void CancelVote(){
        VoteHasStarted = false;
        Bukkit.getServer().getScheduler().cancelTasks(VoteRestart.getPlugin());
        VoteStarter = null;
        PlayersOnline.clear();
        PlayerWhoVoteNo.clear();
        PlayerWhoVoteYes.clear();
    }

    public void StartRebootCountDown(int value){
        final int[] cdValue = {value};
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(VoteRestart.getPlugin(), new Runnable() {
            @Override
            public void run() {
                cdValue[0]--;
                Bukkit.broadcastMessage(ChatColor.AQUA + "The server will reboot in " + ChatColor.BOLD + cdValue[0]);
                if (cdValue[0] == 0){ //start rebooting
                    Bukkit.broadcastMessage(ChatColor.AQUA + "" + ChatColor.UNDERLINE + "Server is rebooting...");
                    Bukkit.getServer().reload();
                }
            }
        }, 0L, 20L);
    }

    public void StartTheVoteCheck(){
        int TotalVoter = PlayerWhoVoteYes.size() + PlayerWhoVoteNo.size();
        if (TotalVoter == PlayersOnline.size()){
            VoteHasStarted = false;
            Bukkit.getServer().getScheduler().cancelTasks(VoteRestart.getPlugin());
            if (PlayerWhoVoteYes.size() > PlayerWhoVoteNo.size()){
                // reboot
                Bukkit.broadcastMessage(ChatColor.GREEN + "Reboot approved!");
                PrintRequiredMsg();
                StartRebootCountDown(10);
            }
            else if (PlayerWhoVoteNo.size() > PlayerWhoVoteYes.size()){
                // not reboot
                CancelVote();
                Bukkit.broadcastMessage(ChatColor.RED + "Reboot declined!");
                PrintRequiredMsg();
            }
            else if (PlayerWhoVoteYes.size() == PlayerWhoVoteNo.size()){
                CancelVote();
                Bukkit.broadcastMessage(ChatColor.RED + "Reboot declined!");
                PrintRequiredMsg();
            }
        }
    }

    public void PrintRequiredMsg(){
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Players who vote " + ChatColor.GREEN + ChatColor.BOLD + "YES" + ChatColor.RESET + ChatColor.YELLOW + ": " + ChatColor.BOLD + PlayerWhoVoteYes.size());
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Players who vote " + ChatColor.RED + ChatColor.BOLD + "NO" + ChatColor.RESET + ChatColor.YELLOW + ": " + ChatColor.BOLD + PlayerWhoVoteNo.size());
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Total votes: " + ChatColor.BOLD + (PlayerWhoVoteYes.size() + PlayerWhoVoteNo.size()));
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Votes required to reboot: " + ChatColor.BOLD + PlayersOnline.size());
    }

    public void PutOnlinePlayerToList(){
        if (PlayersOnline.size() > 0){ // kalo ada data
            PlayersOnline.clear();
        }
        for (Player pl : Bukkit.getOnlinePlayers()){
            PlayersOnline.add(pl.getName());
        }

        for (int i = 0; i < PlayersOnline.size(); i++){
            if (ess.getUser(PlayersOnline.get(i)).isAfk()){
                PlayersOnline.remove(i);
            }
        }


    }
}
