package cmd5247.task;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.ArrayUtils;

import cmd5247.CMD;
import cmd5247.gui.components.TaskManagerOverlay.DebugLine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public record Task(String name, SimpleInstruction head) {
	
	public static Task compile(String name, SimpleInstruction...instructions) {
		Task task = new Task(name, instructions[0]);
		instructions = ArrayUtils.addAll(instructions, L(), LABEL("stop"), T(1), STOP());
		
		for (int i = 0; i < instructions.length; i++) {
			if (i > 0)
				instructions[i].prev = instructions[i - 1];
			if (i < instructions.length - 1)
				instructions[i].next = instructions[i + 1];
		}
		
		return task;
	}
	
	void start() {
		for (var ptr = head; ptr != null; ptr = ptr.next)
			ptr.complete = false;
		head.process();
	}
	
	void stop() {
		head.cancel();
	}

  public List<DebugLine> getScript() {
    var script = new ArrayList<DebugLine>();
		int indentation = 0;
		boolean prev = true;
		var header = String.format("Task: %s - %s", name, CMD.getInstance().taskManager.getActiveElapsed());
		
		script.add(new DebugLine(header, 0, true));
		for (var ptr = head; ptr != null; prev = ptr.complete, ptr = ptr.next)
			if (ptr instanceof SimpleInstruction.Indentation indent)
				indentation += indent.distance;
			else
				script.add(new DebugLine(ptr.toString(), indentation, prev && !ptr.complete));
		
		return script;
  }

	public static SimpleInstruction $(Runnable action) {
		return new SimpleInstruction().withAction(action);
	}
	
	public static TickInstruction $(Supplier<Boolean> condition, Runnable action) {
		return (TickInstruction) new TickInstruction(condition).withAction(action);
	}
	
	public static SimpleInstruction AFTER(int ticks, Runnable action) {
		return $(new Util.Counter(ticks), action);
	}
	
	public static SimpleInstruction FORK(String label) {
		return new SimpleInstruction.Fork(label);
	}
	
	public static SimpleInstruction GOTO(String label, Supplier<Boolean> condition) {
		return new TickInstruction.Goto(label, condition);
	}
	
	public static SimpleInstruction L() {
		return new SimpleInstruction.LineBreak();
	}
	
	public static SimpleInstruction LABEL(String label) {
		return new SimpleInstruction.Label(label);
	}
	
	public static SimpleInstruction LOOP(String label) {
		return LOOP(label, Util.NOW);
	}
	
	public static SimpleInstruction LOOP(String label, Supplier<Boolean> condition) {
		return new TickInstruction.Loop(label, condition);
	}
	
	public static SimpleInstruction NOOP() {
		return $(() -> {});
	}
	
	private static SimpleInstruction STOP() {
		return new SimpleInstruction.Stop();
	}
	
	public static SimpleInstruction T(int distance) {
		return new SimpleInstruction.Indentation(distance);
	}
	
	public static SimpleInstruction TRY(int ticks, TickInstruction tryInstruction) {
		return new TickInstruction.Try(ticks, tryInstruction, NOOP());
	}
	
	public static <T> SimpleInstruction WATCH(Supplier<T> target, Runnable action) {
		return $(new Util.Observer<T>(target), action); 
	}


	
}
