package engine.guis;
import java.util.ArrayList;
import java.util.HashMap;

public class GuiList {

	private HashMap<Integer, ArrayList<Gui>> items;
	private HashMap<Gui, Integer> keys;

	public GuiList() {
		items = new HashMap<Integer, ArrayList<Gui>>();
		keys = new HashMap<Gui, Integer>();
	}

	public void add(int key, Gui value) {
		ArrayList<Gui> batch = items.get(new Integer(key));
		keys.put(value, key);
		if (batch != null) {
			batch.add(value);
		} else {
			ArrayList<Gui> newBatch = new ArrayList<Gui>();
			newBatch.add(value);
			items.put(new Integer(key), newBatch);
		}
	}

	public void remove(Gui value) {
		try {
			Integer batchNo = keys.get(value);
			items.get(batchNo).remove(value);
		} catch (Exception e) {
			System.err.println("[Warning]: Priority Queue does not contain " + value);
		}
	}

	public void update() {
		for (Integer i : items.keySet()) {
			ArrayList<Gui> batch = items.get(i);
			for (Gui s : batch) {
				s.update();
			}
		}
	}
	
	public ArrayList<Gui> getAllItems() {
		ArrayList<Gui> output = new ArrayList<Gui>();
		for (Integer i : items.keySet()) {
			ArrayList<Gui> batch = items.get(i);
			for (Gui s : batch) {
				output.add(s);
			}
		}
		return output;
	}
	
	public void printItems() {
		for (Integer i : items.keySet()) {
			ArrayList<Gui> batch = items.get(i);
			for (Gui s : batch) {
				System.out.println(i + ", " + s);
			}
		}
	}
}
