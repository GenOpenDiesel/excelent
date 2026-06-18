package su.nightexpress.excellentcrates.config;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.api.crate.RewardType;
import su.nightexpress.excellentcrates.crate.limit.CooldownMode;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.*;
import su.nightexpress.nightcore.locale.message.MessageData;
import su.nightexpress.nightcore.util.bridge.RegistryType;

import static su.nightexpress.excellentcrates.Placeholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class Lang implements LangContainer {

    public static final TextLocale COMMAND_ARGUMENT_NAME_CRATE = LangEntry.builder("Command.Argument.Name.Crate").text("crate");
    public static final TextLocale COMMAND_ARGUMENT_NAME_KEY   = LangEntry.builder("Command.Argument.Name.Key").text("key");
    public static final TextLocale COMMAND_ARGUMENT_NAME_X     = LangEntry.builder("Command.Argument.Name.X").text("x");
    public static final TextLocale COMMAND_ARGUMENT_NAME_Y     = LangEntry.builder("Command.Argument.Name.Y").text("y");
    public static final TextLocale COMMAND_ARGUMENT_NAME_Z     = LangEntry.builder("Command.Argument.Name.Z").text("z");

    public static final MessageLocale ERROR_COMMAND_INVALID_CRATE_ARGUMENT = LangEntry.builder("Error.Command.Argument.InvalidCrate").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_VALUE) + " is not a valid crate!"));

    public static final MessageLocale ERROR_COMMAND_INVALID_KEY_ARGUMENT = LangEntry.builder("Error.Command.Argument.InvalidKey").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_VALUE) + " is not a valid key!"));

    public static final TextLocale COMMAND_EDITOR_DESC         = LangEntry.builder("Command.Editor.Desc").text("Open editor GUI.");
    public static final TextLocale COMMAND_DROP_DESC           = LangEntry.builder("Command.Drop.Desc").text("Spawn crate item in the world.");
    public static final TextLocale COMMAND_DROP_KEY_DESC       = LangEntry.builder("Command.DropKey.Desc").text("Spawn key item in the world.");
    public static final TextLocale COMMAND_OPEN_DESC           = LangEntry.builder("Command.Open.Desc").text("Open a crate.");
    public static final TextLocale COMMAND_OPEN_FOR_DESC       = LangEntry.builder("Command.OpenFor.Desc").text("Open crate for a player.");
    public static final TextLocale COMMAND_GIVE_DESC           = LangEntry.builder("Command.Give.Desc").text("Gives crate to a player.");
    public static final TextLocale COMMAND_KEY_DESC            = LangEntry.builder("Command.Key.Desc").text("Manage player's keys.");
    public static final TextLocale COMMAND_KEY_GIVE_DESC       = LangEntry.builder("Command.Key.Give.Desc").text("Give key to a player.");
    public static final TextLocale COMMAND_KEY_TAKE_DESC       = LangEntry.builder("Command.Key.Take.Desc").text("Take key from a player.");
    public static final TextLocale COMMAND_KEY_SET_DESC        = LangEntry.builder("Command.Key.Set.Desc").text("Set keys amount for a player.");
    public static final TextLocale COMMAND_KEY_INSPECT_DESC    = LangEntry.builder("Command.Key.Show.Desc").text("Inspect [player's] virtual keys.");
    public static final TextLocale COMMAND_PREVIEW_DESC        = LangEntry.builder("Command.Preview.Desc").text("Open crate preview.");
    public static final TextLocale COMMAND_RESET_COOLDOWN_DESC = LangEntry.builder("Command.ResetCooldown.Desc").text("Reset player's crate open cooldown.");
    public static final TextLocale COMMAND_MENU_DESC           = LangEntry.builder("Command.Menu.Desc").text("Open crate menu.");

    public static final MessageLocale COMMAND_DROP_DONE = LangEntry.builder("Command.Drop.Done").chatMessage(
        GRAY.wrap("Dropped " + SOFT_YELLOW.wrap(CRATE_NAME) + " at " + SOFT_YELLOW.wrap(LOCATION_X + ", " + LOCATION_Y + ", " + LOCATION_Z) + " in " + SOFT_YELLOW.wrap(LOCATION_WORLD) + "."));

    public static final MessageLocale COMMAND_DROP_KEY_DONE = LangEntry.builder("Command.DropKey.Done").chatMessage(
        GRAY.wrap("Dropped " + SOFT_YELLOW.wrap(KEY_NAME) + " at " + SOFT_YELLOW.wrap(LOCATION_X + ", " + LOCATION_Y + ", " + LOCATION_Z) + " in " + SOFT_YELLOW.wrap(LOCATION_WORLD) + "."));



    public static final MessageLocale COMMAND_OPEN_FOR_DONE = LangEntry.builder("Command.OpenFor.Done").chatMessage(
        GRAY.wrap("Opened " + SOFT_YELLOW.wrap(CRATE_NAME) + " for " + SOFT_YELLOW.wrap(PLAYER_NAME) + "."));

    public static final MessageLocale COMMAND_OPEN_FOR_NOTIFY = LangEntry.builder("Command.OpenFor.Notify").chatMessage(
        GRAY.wrap("You have been forced to open " + SOFT_YELLOW.wrap(CRATE_NAME) + "."));



    public static final MessageLocale COMMAND_GIVE_DONE = LangEntry.builder("Command.Give.Done").chatMessage(
        GRAY.wrap("Given " + SOFT_YELLOW.wrap("x" + GENERIC_AMOUNT) + " of " + SOFT_YELLOW.wrap(CRATE_NAME) + " crate(s) to " + SOFT_YELLOW.wrap(PLAYER_NAME) + "."));

    public static final MessageLocale COMMAND_GIVE_NOTIFY = LangEntry.builder("Command.Give.Notify").chatMessage(
        GRAY.wrap("You recieved " + SOFT_YELLOW.wrap("x" + GENERIC_AMOUNT) + " of " + SOFT_YELLOW.wrap(CRATE_NAME) + "."));



    public static final MessageLocale COMMAND_KEY_GIVE_DONE = LangEntry.builder("Command.Key.Give.Done").chatMessage(
        GRAY.wrap("Given " + SOFT_YELLOW.wrap("x" + GENERIC_AMOUNT) + " of " + SOFT_YELLOW.wrap(KEY_NAME) + " key(s) to " + SOFT_YELLOW.wrap(PLAYER_NAME) + "."));

    public static final MessageLocale COMMAND_KEY_GIVE_NOTIFY = LangEntry.builder("Command.Key.Give.Notify").chatMessage(
        GRAY.wrap("You recieved " + SOFT_YELLOW.wrap("x" + GENERIC_AMOUNT) + " of " + SOFT_YELLOW.wrap(KEY_NAME) + "!"));

    public static final TextLocale COMMAND_KEY_GIVE_ALL_DESC = LangEntry.builder("Command.Key.GiveAll.Desc").text(
        "Give key to all online players.");

    public static final MessageLocale COMMAND_KEY_GIVE_ALL_DONE = LangEntry.builder("Command.Key.GiveAll.Done").chatMessage(
        GRAY.wrap("Given " + SOFT_YELLOW.wrap("x" + GENERIC_AMOUNT) + " of " + SOFT_YELLOW.wrap(KEY_NAME) + " key(s) to " + SOFT_YELLOW.wrap("All Players") + "."));

    public static final MessageLocale COMMAND_KEY_TAKE_DONE = LangEntry.builder("Command.Key.Take.Done").chatMessage(
        GRAY.wrap("Taken " + SOFT_YELLOW.wrap("x" + GENERIC_AMOUNT) + " of " + SOFT_YELLOW.wrap(KEY_NAME) + " key(s) from " + SOFT_YELLOW.wrap(PLAYER_NAME) + "."));

    public static final MessageLocale COMMAND_KEY_TAKE_NOTIFY = LangEntry.builder("Command.Key.Take.Notify").chatMessage(
        GRAY.wrap("You lost " + SOFT_RED.wrap("x" + GENERIC_AMOUNT) + " " + SOFT_RED.wrap(KEY_NAME) + "."));

    public static final MessageLocale COMMAND_KEY_SET_DONE = LangEntry.builder("Command.Key.Set.Done").chatMessage(
        GRAY.wrap("Set " + SOFT_YELLOW.wrap("x" + GENERIC_AMOUNT) + " of " + SOFT_YELLOW.wrap(KEY_NAME) + " key(s) for " + SOFT_YELLOW.wrap(PLAYER_NAME) + "."));

    public static final MessageLocale COMMAND_KEY_SET_NOTIFY = LangEntry.builder("Command.Key.Set.Notify").chatMessage(
        GRAY.wrap("Your " + SOFT_YELLOW.wrap(KEY_NAME) + "'s amount has been changed to " + SOFT_YELLOW.wrap("x" + GENERIC_AMOUNT) + "."));



    public static final MessageLocale COMMAND_KEY_INSPECT_LIST = LangEntry.builder("Command.Key.Show.Format.List").message(
        MessageData.CHAT_NO_PREFIX,
        " ",
        SOFT_YELLOW.wrap(BOLD.wrap(PLAYER_NAME + "'s Virtual Keys: ")),
        GENERIC_ENTRY,
        " "
    );

    public static final TextLocale COMMAND_KEY_INSPECT_ENTRY = LangEntry.builder("Command.Key.Show.Format.Entry").text(
        SOFT_YELLOW.wrap("▪ " + GRAY.wrap(KEY_NAME + ": ") + "x" + GENERIC_AMOUNT)
    );

    public static final MessageLocale COMMAND_PREVIEW_DONE_OTHERS = LangEntry.builder("Command.Preview.Done.Others").chatMessage(
        GRAY.wrap("Opened " + SOFT_YELLOW.wrap(CRATE_NAME) + " preview for " + SOFT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + "."));

    public static final MessageLocale COMMAND_RESET_COOLDOWN_DONE = LangEntry.builder("Command.ResetCooldown.Done").chatMessage(
        GRAY.wrap("Reset " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s open cooldown for " + SOFT_YELLOW.wrap(CRATE_NAME) + "."));

    public static final MessageLocale COMMAND_MENU_DONE_OTHERS = LangEntry.builder("Command.Menu.Done.Others").chatMessage(
        GRAY.wrap("Opened crates menu for " + SOFT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + "."));



    public static final TextLocale COMMAND_WAR_DESC          = LangEntry.builder("Command.War.Desc").text("Crate war (PvP crate battle).");
    public static final TextLocale COMMAND_WAR_CHALLENGE_DESC = LangEntry.builder("Command.War.Challenge.Desc").text("Challenge a player to a crate war.");
    public static final TextLocale COMMAND_WAR_ACCEPT_DESC    = LangEntry.builder("Command.War.Accept.Desc").text("Accept a pending crate war invite.");
    public static final TextLocale COMMAND_WAR_DENY_DESC      = LangEntry.builder("Command.War.Deny.Desc").text("Deny a pending crate war invite.");

    public static final TextLocale WAR_DRAW_NAME = LangEntry.builder("CrateWar.DrawName").text("Draw");

    public static final MessageLocale WAR_DISABLED = LangEntry.builder("CrateWar.Disabled").chatMessage(
        SOFT_RED.wrap("Crate War feature is disabled."));

    public static final MessageLocale WAR_ERROR_SELF = LangEntry.builder("CrateWar.Error.Self").chatMessage(
        SOFT_RED.wrap("You can not start a war with yourself."));

    public static final MessageLocale WAR_ERROR_AMOUNT = LangEntry.builder("CrateWar.Error.Amount").chatMessage(
        GRAY.wrap("Key amount must be between " + SOFT_YELLOW.wrap(GENERIC_MIN) + " and " + SOFT_YELLOW.wrap(GENERIC_MAX) + "."));

    public static final MessageLocale WAR_ERROR_NO_COST = LangEntry.builder("CrateWar.Error.NoCost").chatMessage(
        GRAY.wrap(SOFT_YELLOW.wrap(CRATE_NAME) + " can not be used in a war (it has no key cost)."));

    public static final MessageLocale WAR_ERROR_NO_REWARDS = LangEntry.builder("CrateWar.Error.NoRewards").chatMessage(
        GRAY.wrap(SOFT_YELLOW.wrap(CRATE_NAME) + " has no rewards to fight for."));

    public static final MessageLocale WAR_ERROR_NOT_ENOUGH_KEYS = LangEntry.builder("CrateWar.Error.NotEnoughKeys").chatMessage(
        GRAY.wrap("You don't have enough keys to stake " + SOFT_YELLOW.wrap("x" + GENERIC_AMOUNT) + " openings of " + SOFT_YELLOW.wrap(CRATE_NAME) + "."));

    public static final MessageLocale WAR_ERROR_OPPONENT_BROKE = LangEntry.builder("CrateWar.Error.OpponentBroke").chatMessage(
        GRAY.wrap(SOFT_YELLOW.wrap(WAR_TARGET) + " does not have enough keys for the war."));

    public static final MessageLocale WAR_ERROR_ALREADY_INVITED = LangEntry.builder("CrateWar.Error.AlreadyInvited").chatMessage(
        GRAY.wrap(SOFT_YELLOW.wrap(WAR_TARGET) + " already has a pending war invite."));

    public static final MessageLocale WAR_ERROR_NO_INVITE = LangEntry.builder("CrateWar.Error.NoInvite").chatMessage(
        SOFT_RED.wrap("You don't have any pending war invites."));

    public static final MessageLocale WAR_ERROR_CHALLENGER_OFFLINE = LangEntry.builder("CrateWar.Error.ChallengerOffline").chatMessage(
        SOFT_RED.wrap("The player who challenged you is no longer online."));

    public static final MessageLocale WAR_INVITE_SENT = LangEntry.builder("CrateWar.Invite.Sent").chatMessage(
        GRAY.wrap("You challenged " + SOFT_YELLOW.wrap(WAR_TARGET) + " to a war on " + SOFT_YELLOW.wrap(CRATE_NAME) + " for " + SOFT_YELLOW.wrap("x" + GENERIC_AMOUNT) + " keys. Expires in " + SOFT_YELLOW.wrap(GENERIC_TIME) + "."));

    public static final MessageLocale WAR_INVITE_RECEIVED = LangEntry.builder("CrateWar.Invite.Received").chatMessage(
        GRAY.wrap(SOFT_YELLOW.wrap(WAR_CHALLENGER) + " challenged you to a war on " + SOFT_YELLOW.wrap(CRATE_NAME) + " for " + SOFT_YELLOW.wrap("x" + GENERIC_AMOUNT) + " keys!\n")
            + "<click:run_command:'/crates war accept'>" + GREEN.wrap(BOLD.wrap("[ACCEPT]")) + "</click>"
            + GRAY.wrap("   ")
            + "<click:run_command:'/crates war deny'>" + SOFT_RED.wrap(BOLD.wrap("[DENY]")) + "</click>"
            + GRAY.wrap("   (" + SOFT_YELLOW.wrap(GENERIC_TIME) + ")"));

    public static final MessageLocale WAR_PROMPT_TARGET = LangEntry.builder("CrateWar.Prompt.Target").chatMessage(
        GRAY.wrap("Type the " + SOFT_YELLOW.wrap("name") + " of the player you want to challenge on " + SOFT_YELLOW.wrap(CRATE_NAME) + " (" + SOFT_YELLOW.wrap("x" + GENERIC_AMOUNT) + " keys) in chat.\n")
            + GRAY.wrap("Type " + SOFT_RED.wrap("cancel") + " to cancel."));

    public static final MessageLocale WAR_PROMPT_CANCELLED = LangEntry.builder("CrateWar.Prompt.Cancelled").chatMessage(
        GRAY.wrap("War challenge cancelled."));

    public static final MessageLocale WAR_ERROR_TARGET_NOT_FOUND = LangEntry.builder("CrateWar.Error.TargetNotFound").chatMessage(
        GRAY.wrap("Player " + SOFT_RED.wrap(WAR_TARGET) + " was not found or is offline."));

    public static final MessageLocale WAR_DENIED_TARGET = LangEntry.builder("CrateWar.Denied.Target").chatMessage(
        GRAY.wrap("You denied the war invite from " + SOFT_YELLOW.wrap(WAR_CHALLENGER) + "."));

    public static final MessageLocale WAR_DENIED_CHALLENGER = LangEntry.builder("CrateWar.Denied.Challenger").chatMessage(
        GRAY.wrap(SOFT_YELLOW.wrap(WAR_TARGET) + " denied your war invite."));

    public static final MessageLocale WAR_EXPIRED = LangEntry.builder("CrateWar.Expired").chatMessage(
        GRAY.wrap("The war invite between " + SOFT_YELLOW.wrap(WAR_CHALLENGER) + " and " + SOFT_YELLOW.wrap(WAR_TARGET) + " has expired."));

    public static final MessageLocale WAR_RESULT = LangEntry.builder("CrateWar.Result").chatMessage(
        GRAY.wrap("Crate War on " + SOFT_YELLOW.wrap(CRATE_NAME) + ": ")
            + GRAY.wrap(SOFT_YELLOW.wrap(WAR_CHALLENGER) + " (" + WAR_CHALLENGER_SCORE + " pts) vs " + SOFT_YELLOW.wrap(WAR_TARGET) + " (" + WAR_TARGET_SCORE + " pts) ")
            + GRAY.wrap("→ Winner: " + GREEN.wrap(WAR_WINNER) + "!"));

    public static final MessageLocale WAR_RESULT_BROADCAST = LangEntry.builder("CrateWar.ResultBroadcast").chatMessage(
        GRAY.wrap(GREEN.wrap(WAR_WINNER) + " won a Crate War against " + SOFT_RED.wrap(WAR_LOSER) + " on " + SOFT_YELLOW.wrap(CRATE_NAME) + " and took all the rewards!"));





    public static final MessageLocale CRATE_OPEN_ERROR_INVENTORY_SPACE = LangEntry.builder("Crate.Open.Error.InventorySpace").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Inventory is Full!")),
        GRAY.wrap("Clean up inventory to open crates."),
        Sound.ENTITY_VILLAGER_NO
    );

    public static final MessageLocale CRATE_OPEN_ERROR_COOLDOWN_TEMPORARY = LangEntry.builder("Crate.Open.Error.Cooldown.Temporary").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Crate is on Cooldown!")),
        GRAY.wrap("You can open it again in " + SOFT_RED.wrap(GENERIC_TIME)),
        Sound.ENTITY_VILLAGER_NO
    );

    public static final MessageLocale CRATE_OPEN_ERROR_COOLDOWN_ONE_TIMED = LangEntry.builder("Crate.Open.Error.Cooldown.OneTimed").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Whoops!")),
        GRAY.wrap("You already have opened this one-timed crate!"),
        Sound.ENTITY_VILLAGER_NO
    );

    public static final MessageLocale CRATE_OPEN_ERROR_NO_REWARDS = LangEntry.builder("Crate.Open.Error.NoRewards").titleMessage(
        RED.wrap(BOLD.wrap("Whoops!")),
        GRAY.wrap("There are no rewards for you! Try later."),
        Sound.ENTITY_VILLAGER_NO
    );

    public static final MessageLocale CRATE_OPEN_ERROR_ALREADY = LangEntry.builder("Crate.Open.Error.Already").titleMessage(
        RED.wrap(BOLD.wrap("Whoops!")),
        GRAY.wrap("You're already opening a crate!"),
        Sound.ENTITY_VILLAGER_NO
    );

    public static final MessageLocale CRATE_OPEN_TOO_EXPENSIVE = LangEntry.builder("Crate.Open.TooExpensive").message(
        MessageData.CHAT_NO_PREFIX,
        " ",
        RED.and(BOLD).wrap("CRATE NOT OPENED:"),
        RED.wrap("» ") + GRAY.wrap("Crate: ") + WHITE.wrap(CRATE_NAME),
        RED.wrap("» ") + GRAY.wrap("You can't afford the open cost: " + GENERIC_COSTS),
        " "
    );

    public static final MessageLocale CRATE_OPEN_RESULT_INFO = LangEntry.builder("Crate.Rewards").message(
        MessageData.CHAT_NO_PREFIX,
        " ",
        YELLOW.and(BOLD).wrap("CRATE OPENED:"),
        YELLOW.wrap("» ") + GRAY.wrap("Crate: ") + WHITE.wrap(CRATE_NAME),
        YELLOW.wrap("» ") + GRAY.wrap("Rewards: ") + WHITE.wrap(GENERIC_REWARDS),
        " "
    );

    public static final TextLocale CRATE_OPEN_RESULT_REWARD = LangEntry.builder("Crate.Opened.Result.Reward").text(REWARD_NAME);

    public static final MessageLocale CRATE_OPEN_MILESTONE_COMPLETED = LangEntry.builder("Crate.Open.Milestone.Completed").message(
        MessageData.chat().usePrefix(false).sound(Sound.ENTITY_PLAYER_LEVELUP).build(),
        GRAY.wrap("You completed " + GREEN.wrap(MILESTONE_OPENINGS + " Openings ") + "milestone and got " + GREEN.wrap(REWARD_NAME) + " as reward!")
    );

    public static final MessageLocale CRATE_OPEN_REWARD_BROADCAST = LangEntry.builder("Crate.Open.Reward.Broadcast").message(
        MessageData.chat().usePrefix(false).sound(Sound.BLOCK_NOTE_BLOCK_BELL).build(),
        " ",
        GRAY.wrap(LIGHT_PURPLE.wrap(PLAYER_DISPLAY_NAME) + " opened " + LIGHT_PURPLE.wrap(CRATE_NAME) + " and received " + LIGHT_PURPLE.wrap(REWARD_NAME) + "!"),
        " ",
        GRAY.wrap("Purchase keys: " + OPEN_URL.with("https://YOUR_LINK_HERE.xyz").wrap(LIGHT_PURPLE.wrap("[Click to open Store]"))),
        " "
    );

    public static final MessageLocale CRATE_PREVIEW_ERROR_COOLDOWN = LangEntry.builder("Crate.Preview.Error.Cooldown").chatMessage(
        GRAY.wrap("You can preview this crate again in " + SOFT_RED.wrap(GENERIC_TIME))
    );

    public static final MessageLocale ERROR_DATA_IS_LOADING = LangEntry.builder("Error.DataIsLoading").chatMessage(
        SOFT_RED.wrap("Data is still loading... Please try again later.")
    );

    public static final TextLocale OTHER_COOLDOWN_READY       = LangEntry.builder("Other.Cooldown.Ready").text(GREEN.wrap("Ready to Open!"));
    public static final TextLocale OTHER_LAST_OPENER_EMPTY    = LangEntry.builder("Other.LastOpener.Empty").text("-");
    public static final TextLocale OTHER_LAST_REWARD_EMPTY    = LangEntry.builder("Other.LastReward.Empty").text("-");
    public static final TextLocale OTHER_NEXT_MILESTONE_EMPTY = LangEntry.builder("Other.NextMilestone.Empty").text("-");

    public static final TextLocale OTHER_MIDNIGHT = LangEntry.builder("Other.Midnight").text("Midnight");
    public static final TextLocale OTHER_FREE     = LangEntry.builder("Other.Free").text("Free");

    public static final TextLocale EFFECT_MODEL_NONE    = LangEntry.builder("EffectModel.None").text("None");
    public static final TextLocale EFFECT_MODEL_HELIX   = LangEntry.builder("EffectModel.Helix").text("Helix");
    public static final TextLocale EFFECT_MODEL_SPIRAL  = LangEntry.builder("EffectModel.Spiral").text("Spiral");
    public static final TextLocale EFFECT_MODEL_SPHERE  = LangEntry.builder("EffectModel.Sphere").text("Sphere");
    public static final TextLocale EFFECT_MODEL_HEART   = LangEntry.builder("EffectModel.Heart").text("Heart");
    public static final TextLocale EFFECT_MODEL_PULSAR  = LangEntry.builder("EffectModel.Pulsar").text("Pulsar");
    public static final TextLocale EFFECT_MODEL_BEACON  = LangEntry.builder("EffectModel.Beacon").text("Beacon");
    public static final TextLocale EFFECT_MODEL_TORNADO = LangEntry.builder("EffectModel.Tornado").text("Tornado");
    public static final TextLocale EFFECT_MODEL_VORTEX  = LangEntry.builder("EffectModel.Vortex").text("Vortex");
    public static final TextLocale EFFECT_MODEL_SIMPLE  = LangEntry.builder("EffectModel.Simple").text("Simple");

    public static final BooleanLocale INSPECTIONS_GENERIC_OVERVIEW = LangEntry.builder("Inspections.Generic.Overview").bool("No problems detected.", "Problems detected!");
    public static final BooleanLocale INSPECTIONS_GENERIC_ITEM     = LangEntry.builder("Inspections.Generic.Item").bool("Item is valid.", "Item is invalid!");
    public static final BooleanLocale INSPECTIONS_GENERIC_COMMANDS = LangEntry.builder("Inspections.Generic.Commands").bool("All commands are valid.", "Detected invalid commands!");

    public static final BooleanLocale INSPECTIONS_CRATE_PREVIEW      = LangEntry.builder("Inspections.Crate.Preview").bool("Preview is valid.", "Preview is invalid!");
    public static final BooleanLocale INSPECTIONS_CRATE_OPENING      = LangEntry.builder("Inspections.Crate.Opening").bool("Opening is valid.", "Opening is invalid!");
    public static final BooleanLocale INSPECTIONS_CRATE_HOLOGRAM     = LangEntry.builder("Inspections.Crate.Hologram").bool("Hologram template is valid.", "Hologram template is invalid!");
    public static final BooleanLocale INSPECTIONS_REWARD_PREVIEW     = LangEntry.builder("Inspections.Reward.Preview").bool("Preview item is valid.", "Preview item is invalid!");
    public static final BooleanLocale INSPECTIONS_REWARD_ITEMS       = LangEntry.builder("Inspections.Reward.Items").bool("All items are valid.", "Detected invalid items!");
    public static final TextLocale    INSPECTIONS_REWARD_NO_ITEMS    = LangEntry.builder("Inspections.Reward.NoItems").text("No items added.");
    public static final TextLocale    INSPECTIONS_REWARD_NO_COMMANDS = LangEntry.builder("Inspections.Reward.NoCommands").text("No commands defined.");

    public static final IconLocale UI_COSTS_OPTION_AVAILABLE = LangEntry.iconBuilder("UI.Costs.Option.Available")
        .rawName(WHITE.wrap(COST_NAME))
        .rawLore(
            GENERIC_COSTS,
            EMPTY_IF_ABOVE,
            YELLOW.and(BOLD).wrap("OPENINGS AVAILABLE: ") + WHITE.and(UNDERLINED).wrap(GENERIC_AVAILABLE),
            "",
            GREEN.wrap("→ " + UNDERLINED.wrap("Click to select"))
        )
        .build();

    public static final IconLocale UI_COSTS_OPTION_UNAVAILABLE = LangEntry.iconBuilder("UI.Costs.Option.Unavailable")
        .rawName(WHITE.wrap(COST_NAME))
        .rawLore(
            GENERIC_COSTS,
            EMPTY_IF_ABOVE,
            RED.and(BOLD).wrap("YOU CAN'T AFFORD THIS")
        )
        .build();

    public static final TextLocale UI_COSTS_ENTRY_AVAILABLE   = LangEntry.builder("UI.Costs.Entry0.Available").text(WHITE.wrap(GENERIC_ENTRY) + " " + GRAY.wrap("(" + GREEN.wrap("✔") + ")"));
    public static final TextLocale UI_COSTS_ENTRY_UNAVAILABLE = LangEntry.builder("UI.Costs.Entry0.Unavailable").text(WHITE.wrap(GENERIC_ENTRY) + " " + GRAY.wrap("(" + RED.wrap("✘") + ")"));

    public static final IconLocale UI_OPEN_AMOUNT_SINGLE = LangEntry.iconBuilder("UI.OpenAmount.Single")
        .rawName(YELLOW.and(BOLD).wrap("Open One"))
        .rawLore(
            GRAY.wrap("Open a single crate."),
            "",
            YELLOW.wrap("→ " + UNDERLINED.wrap("Click to select"))
        )
        .build();

    public static final IconLocale UI_OPEN_AMOUNT_ALL = LangEntry.iconBuilder("UI.OpenAmount.All")
        .rawName(YELLOW.and(BOLD).wrap("Open All"))
        .rawLore(
            GRAY.wrap("Open up to " + WHITE.wrap(GENERIC_MAX) + " crates."),
            "",
            YELLOW.wrap("→ " + UNDERLINED.wrap("Click to select"))
        )
        .build();

    public static final TextLocale EDITOR_TITLE_MAIN             = LangEntry.builder("Editor.Title.Main").text(BLACK.wrap("ExcellentCrates Editor"));
    public static final TextLocale EDITOR_TITLE_CRATE_LIST       = LangEntry.builder("Editor.Title.Crates").text(BLACK.wrap("Crates Editor"));
    public static final TextLocale EDITOR_TITLE_CRATE_SETTINGS   = LangEntry.builder("Editor.Title.Crate.Settings").text(BLACK.wrap("Crate Settings"));
    public static final TextLocale EDITOR_TITLE_CRATE_COSTS      = LangEntry.builder("Editor.Title.Crate.CostOptions").text(BLACK.wrap("Cost Options"));
    public static final TextLocale EDITOR_TITLE_CRATE_COST       = LangEntry.builder("Editor.Title.Crate.CostOption").text(BLACK.wrap("Cost Option Settings"));
    public static final TextLocale EDITOR_TITLE_CRATE_MILESTONES = LangEntry.builder("Editor.Title.Crate.Milestones").text(BLACK.wrap("Crate Milestones"));
    public static final TextLocale EDITOR_TITLE_REWARD_LIST      = LangEntry.builder("Editor.Title.Reward.List").text(BLACK.wrap("Crate Rewards"));
    public static final TextLocale EDITOR_TITLE_REWARD_CONTENT   = LangEntry.builder("Editor.Title.Reward.Content").text(BLACK.wrap("Reward Items"));
    public static final TextLocale EDITOR_TITLE_REWARD_SETTINGS  = LangEntry.builder("Editor.Title.Reward.Settings").text(BLACK.wrap("Reward Settings"));
    public static final TextLocale EDITOR_TITLE_KEY_LIST         = LangEntry.builder("Editor.Title.Keys").text(BLACK.wrap("Keys Editor"));
    public static final TextLocale EDITOR_TITLE_KEY_SETTINGS     = LangEntry.builder("Editor.Title.Key.Settings").text(BLACK.wrap("Key Settings"));

    @Deprecated
    public static final TextLocale EDITOR_ENTER_AMOUNT            = LangEntry.builder("Editor.Enter.Amount").text(GRAY.wrap("Enter " + GREEN.wrap("[Amount]")));
    @Deprecated
    public static final TextLocale EDITOR_ENTER_REWARD_ID         = LangEntry.builder("Editor.Reward.Enter.Id").text(GRAY.wrap("Enter " + GREEN.wrap("[Reward Identifier]")));


    public static final DialogElementLocale DIALOG_GENERIC_CREATION_BODY = LangEntry.builder("Dialog.Generic.Creation.Body").dialogElement(400,
        "Enter a " + SOFT_YELLOW.wrap("unique identifier") + " (ID) for the new object.",
        "",
        SOFT_ORANGE.wrap("⚠") + " You will need to reference this ID in " + SOFT_ORANGE.wrap("commands") + " and " + SOFT_ORANGE.wrap("config files") + ", so it's best to choose one that's " + SOFT_ORANGE.wrap("clear") + " and " + SOFT_ORANGE.wrap("easy") + " for you to remember.",
        "",
        SOFT_RED.wrap("→") + " Only " + SOFT_RED.wrap("letters") + ", " + SOFT_RED.wrap("digits") + " and an " + SOFT_RED.wrap("underscore") + " are allowed."
    );


    public static final DialogElementLocale DIALOG_GENERIC_NAME_BODY = LangEntry.builder("Dialog.Generic.Name.Body").dialogElement(400,
        "Enter the " + SOFT_YELLOW.wrap("display name") + "."
    );

    public static final TextLocale DIALOG_GENERIC_NAME_INPUT_NAME         = LangEntry.builder("Dialog.Generic.Name.Input.Name").text("Name");
    public static final TextLocale DIALOG_GENERIC_NAME_INPUT_REPLACE_NAME = LangEntry.builder("Dialog.Generic.Name.Input.ReplaceName").text("Replace Item Name");


    public static final DialogElementLocale DIALOG_GENERIC_DESCRIPTION_BODY = LangEntry.builder("Dialog.Generic.Description.Body").dialogElement(400,
        "Enter the " + SOFT_YELLOW.wrap("description") + "."
    );

    public static final TextLocale DIALOG_GENERIC_DESCRIPTION_INPUT_DESC         = LangEntry.builder("Dialog.Generic.Description.Input.Description").text("Description");
    public static final TextLocale DIALOG_GENERIC_DESCRIPTION_INPUT_REPLACE_LORE = LangEntry.builder("Dialog.Generic.Description.Input.ReplaceItemLore").text("Replace Item Lore");


    public static final DialogElementLocale DIALOG_GENERIC_ITEM_BODY_NORMAL = LangEntry.builder("Dialog.Generic.Item.Body.Normal").dialogElement(400,
        "Please confirm item replacement.",
        GRAY.wrap("Check the additional fields if needed.")
    );

    public static final DialogElementLocale DIALOG_GENERIC_ITEM_BODY_CUSTOM = LangEntry.builder("Dialog.Generic.Item.Body.Custom").dialogElement(400,
        "Please confirm item replacement.",
        GRAY.wrap("Check the additional fields if needed."),
        "",
        SOFT_RED.and(BOLD).wrap("IMPORTANT NOTE:"),
        "If the item above doesn't match the one you used, enable the " + SOFT_RED.wrap("Save as NBT") + " option.",
        GRAY.wrap("This ensures the exact item data is saved correctly.")
    );

    public static final TextLocale DIALOG_GENERIC_ITEM_INPUT_NBT      = LangEntry.builder("Dialog.Generic.Item.Input.NBT").text(SOFT_RED.wrap("Save as NBT"));
    public static final TextLocale DIALOG_GENERIC_ITEM_INPUT_REP_NAME = LangEntry.builder("Dialog.Generic.Item.Input.ReplaceName").text("Inherit Item's Name");
    public static final TextLocale DIALOG_GENERIC_ITEM_INPUT_REP_DESC = LangEntry.builder("Dialog.Generic.Item.Input.ReplaceDesc").text("Inherit Item's Lore");


    public static final EnumLocale<RewardType>   REWARD_TYPE   = LangEntry.builder("Enums.RewardType").enumeration(RewardType.class);
    public static final EnumLocale<CooldownMode> COOLDOWN_MODE = LangEntry.builder("Enums.CooldownMode").enumeration(CooldownMode.class);
    public static final RegistryLocale<Particle> PARTICLE      = LangEntry.builder("Assets.Particle").registry(RegistryType.PARTICLE_TYPE);

    @NotNull
    public static String inspection(@NotNull BooleanLocale locale, boolean state) {
        return CoreLang.formatEntry(locale.get(state), state);
    }
}
