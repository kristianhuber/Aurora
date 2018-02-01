package engine.postprocessing;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import aurora.main.Aurora;
import engine.postprocessing.bloom.BrightFilter;
import engine.postprocessing.bloom.CombineFilter;
import engine.postprocessing.contrast.ContrastChanger;
import engine.postprocessing.gaussian.HorizontalBlur;
import engine.postprocessing.gaussian.VerticalBlur;
import engine.postprocessing.grayscale.GrayScaleFilter;
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
	
	private static GrayScaleFilter testing;

	public static void initialize() {
		quad = ModelManager.loadToVAO(POSITIONS, 2);
		effects = new ArrayList<Effect>();
		
		noFilter = new CombineFilter();

		vBlur = new VerticalBlur(Aurora.WIDTH / 8, Aurora.HEIGHT / 8);
		hBlur = new HorizontalBlur(Aurora.WIDTH / 8, Aurora.HEIGHT / 8);
		vBlur2 = new VerticalBlur(Aurora.WIDTH / 2, Aurora.HEIGHT / 2);
		hBlur2 = new HorizontalBlur(Aurora.WIDTH / 2, Aurora.HEIGHT / 2);
		
		//This does not contain an FBO
		contrastChanger = new ContrastChanger();

		//effects.add(hBlur2);
		//effects.add(vBlur2);
		//effects.add(hBlur);
		//effects.add(vBlur);
		//effects.add(contrastChanger);

		brightFilter = new BrightFilter(Aurora.WIDTH / 2, Aurora.HEIGHT / 2);
		hBloom = new HorizontalBlur(Aurora.WIDTH / 5, Aurora.HEIGHT / 5);
		vBloom = new VerticalBlur(Aurora.WIDTH / 5, Aurora.HEIGHT / 5);
		
		//This does not contain an FBO because it will render something
		combineFilter = new CombineFilter();

		testing = new GrayScaleFilter();
		
		effects.add(brightFilter);
		effects.add(hBloom);
		effects.add(vBloom);
		effects.add(contrastChanger);
		effects.add(combineFilter);
		//effects.add(testing);
	}

	public static void doPostProcessing(int colourTexture) {
		PostProcessing.start();

		// Iterate through all the effects
		int texture = colourTexture;
		int count = 0;
		for (Effect e : effects) {
			
			boolean render = (count == effects.size() - 1);
			
			if (e instanceof CombineFilter) {
				((CombineFilter) e).render(texture, colourTexture, render);
			} else {
				e.render(texture, render);
			}
			
			texture = e.getOutputTexture();
			count++;
		}

		// Make sure something is rendered
		if (effects.isEmpty()) {
			noFilter.render(colourTexture, 0, true);
		}

		PostProcessing.stop();
	}

	public static void cleanUp() {
		noFilter.cleanUp();
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
