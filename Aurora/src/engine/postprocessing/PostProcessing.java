package engine.postprocessing;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import engine.postprocessing.bloom.BrightFilter;
import engine.postprocessing.bloom.CombineFilter;
import engine.postprocessing.contrast.ContrastChanger;
import engine.postprocessing.gaussian.HorizontalBlur;
import engine.postprocessing.gaussian.VerticalBlur;
import engine.rendering.models.ModelManager;
import engine.rendering.models.RawModel;

public class PostProcessing {

	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };
	private static RawModel quad;
	private static List<Effect> effects;
	private static CombineFilter noFilter;

	private static VerticalBlur vBlur;
	private static HorizontalBlur hBlur;
	private static VerticalBlur vBlur2;
	private static HorizontalBlur hBlur2;
	private static ContrastChanger contrastChanger;

	private static BrightFilter brightFilter;
	private static HorizontalBlur hBloom;
	private static VerticalBlur vBloom;
	private static CombineFilter combineFilter;

	public static void initialize() {
		quad = ModelManager.loadToVAO(POSITIONS, 2);
		effects = new ArrayList<Effect>();
		noFilter = new CombineFilter();

		vBlur = new VerticalBlur(Display.getWidth() / 8, Display.getHeight() / 8);
		hBlur = new HorizontalBlur(Display.getWidth() / 8, Display.getHeight() / 8);
		vBlur2 = new VerticalBlur(Display.getWidth() / 2, Display.getHeight() / 2);
		hBlur2 = new HorizontalBlur(Display.getWidth() / 2, Display.getHeight() / 2);
		contrastChanger = new ContrastChanger();

		/*effects.add(hBlur2);
		effects.add(vBlur2);
		effects.add(hBlur);
		effects.add(vBlur);
		effects.add(contrastChanger);*/

		brightFilter = new BrightFilter(Display.getWidth() / 2, Display.getHeight() / 2);
		hBloom = new HorizontalBlur(Display.getWidth() / 5, Display.getHeight() / 5);
		vBloom = new VerticalBlur(Display.getWidth() / 5, Display.getHeight() / 5);
		combineFilter = new CombineFilter();

		effects.add(brightFilter);
		effects.add(hBloom);
		effects.add(vBloom);
		effects.add(contrastChanger);
		effects.add(combineFilter);
	}

	public static void doPostProcessing(int colourTexture) {
		PostProcessing.start();

		// Iterate through all the effects
		int texture = colourTexture;
		int count = 0;
		for (Effect e : effects) {
			if (e instanceof CombineFilter) {
				((CombineFilter) e).render(colourTexture, texture);
			} else {
				e.render(texture);
			}
			count++;
			if(!(e instanceof ContrastChanger) && count < effects.size()) {
				texture = e.getOutputTexture();
			}
		}

		// Make sure something is rendered
		if (effects.isEmpty()) {
			noFilter.render(colourTexture, colourTexture);
		}

		PostProcessing.stop();
	}

	public static void cleanUp() {
		for(Effect e : effects) {
			e.cleanUp();
		}
	}

	private static void start() {
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	private static void stop() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

}
