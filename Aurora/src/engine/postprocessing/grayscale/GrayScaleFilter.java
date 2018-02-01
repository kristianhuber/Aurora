package engine.postprocessing.grayscale;

import engine.postprocessing.Effect;

public class GrayScaleFilter extends Effect{

	public GrayScaleFilter() {
		super(new GrayScaleShader());
	}
}
