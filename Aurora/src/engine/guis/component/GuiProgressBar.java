package engine.guis.component;

import org.lwjgl.util.vector.Vector4f;

import engine.guis.Gui;
import engine.rendering.RenderMode;

public class GuiProgressBar extends GuiComponent {

	private boolean showProgress;
	private float maxProgress;
	private float progress;
	private Gui bar;

	public GuiProgressBar(RenderMode render, String texture, float x, float y, float width, float height) {
		this(render, texture, x, y, width, height, GuiComponent.TEXT_ALIGN_CENTERED);
	}
	
	public GuiProgressBar(RenderMode render, String texture, float x, float y, float width, float height, int textAlignment) {
		super(render, texture, x, y, width, height);
		bar = new Gui(new Vector4f(0, 1, 0, 1), x, y + 5, 0, height * 0.815f);
		this.setTextAlign(textAlignment);
		render.addGui(2, bar);
		this.maxProgress = 100;
		this.progress = 0;
	}
	
	public void showProgress(boolean progress) {
		this.showProgress = progress;
		if(showProgress) {
			this.setText((int)this.progress + "%");
		}
	}

	public void setMaxProgress(float progress) {
		this.maxProgress = progress;
	}

	public void increaseProgress(float progress) {
		this.progress += progress;
		if(progress > maxProgress) {
			progress = maxProgress;
		}
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}

	@Override
	public void update() {
		super.update();

		if(showProgress) {
			this.setText((int)progress + "%");
		}
		
		if (progress > maxProgress)
			progress = maxProgress;
		if (progress < 0)
			progress = 0;

		float maxWidth = area.width * 0.98f;
		maxWidth *= (progress / maxProgress);
		bar.setWidth(maxWidth);
		bar.setPosition(area.x * 1.06f, area.y * 1.1f);
	}
}
