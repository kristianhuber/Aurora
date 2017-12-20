package engine.postprocessing;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import engine.postprocessing.bloom.BrightFilter;
import engine.postprocessing.bloom.CombineFilter;
import engine.postprocessing.gaussian.HorizontalBlur;
import engine.postprocessing.gaussian.VerticalBlur;
import engine.rendering.models.ModelManager;
import engine.rendering.models.RawModel;

public class PostProcessing {

	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };
	private static RawModel quad;
	private static ContrastChanger contrastChanger;
	private static VerticalBlur vBlur;
	private static HorizontalBlur hBlur;
	private static VerticalBlur vBlur2;
	private static HorizontalBlur hBlur2;
	private static BrightFilter brightFilter;
	private static HorizontalBlur hBloom;
	private static VerticalBlur vBloom;
	private static CombineFilter combineFilter;
	
	public static void initialize() {
		quad = ModelManager.loadToVAO(POSITIONS, 2);
		contrastChanger = new ContrastChanger();
		vBlur = new VerticalBlur(Display.getWidth()/8, Display.getHeight()/8);
		hBlur = new HorizontalBlur(Display.getWidth()/8, Display.getHeight()/8);
		vBlur2 = new VerticalBlur(Display.getWidth()/2, Display.getHeight()/2);
		hBlur2 = new HorizontalBlur(Display.getWidth()/2, Display.getHeight()/2);
		brightFilter = new BrightFilter(Display.getWidth() / 2, Display.getHeight() / 2);
		hBloom = new HorizontalBlur(Display.getWidth() / 5, Display.getHeight() / 5);
		vBloom = new VerticalBlur(Display.getWidth() / 5, Display.getHeight() / 5);
		combineFilter = new CombineFilter();
	}

	public static void doPostProcessing(int colourTexture) {
		start();
		//hBlur2.render(colourTexture);
		//vBlur2.render(hBlur2.getOutputTexture());
		//hBlur.render(vBlur2.getOutputTexture());
		//vBlur.render(hBlur.getOutputTexture());
		//contrastChanger.render(vBlur.getOutputTexture());
		brightFilter.render(colourTexture);
		hBloom.render(brightFilter.getOutputTexture());
		vBloom.render(hBloom.getOutputTexture());
		//contrastChanger.render(vBloom.getOutputTexture());
		combineFilter.render(colourTexture, vBloom.getOutputTexture());
		end();
	}

	public static void cleanUp() {
		contrastChanger.cleanUp();
		hBlur.cleanUp();
		vBlur.cleanUp();
		hBlur2.cleanUp();
		vBlur2.cleanUp();
		brightFilter.cleanUp();
		hBloom.cleanUp();
		vBloom.cleanUp();
		combineFilter.cleanUp();
	}

	private static void start() {
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	private static void end() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

}
