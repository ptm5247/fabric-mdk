package cmd5247.task;

import java.util.Optional;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

@Environment(value=EnvType.CLIENT)
public class Util {

  public static enum Key {
    USE, ATTACK;

    public KeyMapping getMapping() {
      return switch (this) {
        case ATTACK:  yield Minecraft.getInstance().options.keyAttack;
        case USE:     yield Minecraft.getInstance().options.keyUse;
      };
    }
  }

	public static Runnable click(Key key) {
		return () -> KeyMapping.click(KeyBindingHelper.getBoundKeyOf(key.getMapping()));
	}
	
	public static Runnable press(Key key) {
		return () -> key.getMapping().setDown(true);
	}
	
	public static Runnable release(Key key) {
		return () -> key.getMapping().setDown(false);
  }

  @FunctionalInterface static interface RequiresReset { void reset(); }
	
	static final Supplier<Boolean> NOW = () -> true;
	static final Supplier<Boolean> NEVER = () -> false;
	
	/** Returns true after a certain number of tests. */
	static class Counter implements Supplier<Boolean>, RequiresReset {
		
		private final int reset;
		private int count;
		private String fmt;
		
		/** When ticks = 1, the first call will return true. */
		Counter(int ticks) {
			this.count = this.reset = ticks;
			this.fmt = "[%0" + Integer.toString(ticks).length() + "d]";
		}
		
		@Override
		public void reset() {
			count = reset;
		}
		
		@Override
		public Boolean get() {
			count = Integer.max(0, count - 1);
			return count == 0;
		}
		
		@Override
		public String toString() {
			return String.format(fmt, count);
		}
		
	}
	
	/** 
	 * Stores the first value supplied by the supplier,
	 * and accepts a tick event once a subsequent call supplies a different value.
	 */
	static class Observer<T> implements Supplier<Boolean>, RequiresReset {
		
		private Optional<T> initial = Optional.empty();
		private Supplier<T> target;
		private String observedType = "Unknown";
		
		Observer(Supplier<T> target) {
			this.target = target;
		}
		
		@Override
		public void reset() {
			this.initial = Optional.empty();
		}
		
		@Override
		public Boolean get() {
			if (initial.isEmpty()) {
				initial = Optional.of(target.get());
				observedType = initial.get().getClass().getSimpleName();
				return false;
			} else return !initial.get().equals(target.get());
		}
		
		@Override
		public String toString() {
			return "OBSERVE " + observedType;
		}
		
	}
  
}
