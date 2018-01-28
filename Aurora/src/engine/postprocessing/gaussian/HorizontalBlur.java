package engine.postprocessing.gaussian;

import engine.postprocessing.Effect;

public class HorizontalBlur extends Effect{
	
	public HorizontalBlur(int targetFboWidth, int targetFboHeight){
		super(new HorizontalBlurShader(), targetFboWidth, targetFboHeight);
		
		shader.start();
		((HorizontalBlurShader) shader).loadTargetWidth(targetFboWidth);
		shader.stop();
	}
}
