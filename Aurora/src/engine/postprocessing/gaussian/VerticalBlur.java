package engine.postprocessing.gaussian;

import engine.postprocessing.Effect;

public class VerticalBlur extends Effect{
	
	public VerticalBlur(int targetFboWidth, int targetFboHeight){
		super(new VerticalBlurShader(), targetFboWidth, targetFboHeight);
		
		shader.start();
		((VerticalBlurShader) shader).loadTargetHeight(targetFboHeight);
		shader.stop();
	}
}
