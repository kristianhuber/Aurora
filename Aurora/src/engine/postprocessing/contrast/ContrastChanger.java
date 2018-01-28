package engine.postprocessing.contrast;

import engine.postprocessing.Effect;

public class ContrastChanger extends Effect{
	
	public ContrastChanger(){
		super(new ContrastShader());
	}
}
