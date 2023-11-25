package cmd5247.commands;

import static cmd5247.task.Task.$;
import static cmd5247.task.Task.AFTER;
import static cmd5247.task.Task.GOTO;
import static cmd5247.task.Task.L;
import static cmd5247.task.Task.LABEL;
import static cmd5247.task.Task.LOOP;
import static cmd5247.task.Task.T;
import static cmd5247.task.Task.TRY;
import static cmd5247.task.Task.compile;
import static cmd5247.task.Util.Key.USE;
import static cmd5247.task.Util.click;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

@Environment(value=EnvType.CLIENT)
class FishCommand extends AbstractTaskCommand {
	
	/** Registers the $fish command. */
	static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("fish").executes(new FishCommand()));
	}
	
	@Override
	void performChecks(CommandContext<CommandSourceStack> context) throws CommandRuntimeException {
    for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
      if (player.getInventory().getItem(i).is(Items.FISHING_ROD)) return;
    }
    throw new CommandRuntimeException(
      Component.literal("You must have a fishing rod in your inventory to use this command!")
               .withStyle(ChatFormatting.RED)
    );
	}
	
	/**
	 * Gives access to player.fishing.biting without access transformers. Data 0-7 are defined in
	 * Entity, and 8 is FishingHook.hookedIn, which is an entity hooked while not fishing.
	 */
	private static final EntityDataAccessor<Boolean> DATA_BITING = EntityDataSerializers.BOOLEAN.createAccessor(9);
	
	private boolean isBiting() {
		var hook = player.fishing;
		return hook == null ? false : hook.getEntityData().get(DATA_BITING);
	}
	
	/** returns true upon failure. */
	private boolean equipBestFishingRod() {
		return !equipBestTool(Items.FISHING_ROD, new Enchantment[] {
				Enchantments.MENDING, Enchantments.FISHING_LUCK,
				Enchantments.UNBREAKING, Enchantments.FISHING_SPEED
		});
	}
	
	private FishCommand() {

		setTask(compile("Fish",
			L(),
			LABEL("equip"),
			GOTO("stop", this::equipBestFishingRod)         .comment("equip best fishing rod"),
      L(),
			LABEL("fish"),
	    T(1),   LOOP("equip", this::toolWillBreak)      .comment("LOOP equip IF rod will break"),
				      AFTER(20, click(USE))						        .comment("cast line"),
				      TRY(700, $(this::isBiting, click(USE)))	.comment("reel IF biting"),
	    T(-1),	LOOP("fish")
	
		));
		
	}
	
}
