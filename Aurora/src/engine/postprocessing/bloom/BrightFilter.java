package engine.postprocessing.bloom;

import engine.postprocessing.Effect;

public class BrightFilter extends Effect{
	
	public BrightFilter(int width, int height){
		super(new BrightFilterShader(), width, height);
	}	
}
