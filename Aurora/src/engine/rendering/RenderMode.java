package engine.rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import engine.guis.Gui;
import engine.guis.GuiList;
import engine.guis.font.mesh.FontType;
import engine.guis.font.mesh.GUIText;
import engine.guis.font.mesh.TextMeshData;
import engine.rendering.models.ModelManager;

public abstract class RenderMode {
	protected HashMap<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
	protected GuiList guis = new GuiList();
	
	public abstract void render();
	
	public void initialize() {
		
	}
	
	public void addGui(int priority, Gui g) {
		guis.add(priority, g);
	}
	
	public void removeGui(Gui g) {
		guis.remove(g);
	}
	
	public void loadText(GUIText text){
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);
		int vao = ModelManager.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		text.setMeshInfo(vao, data.getVertexCount());
		List<GUIText> textBatch = texts.get(font);
		if(textBatch == null){
			textBatch = new ArrayList<GUIText>();
			texts.put(font, textBatch);
		}
		textBatch.add(text);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void removeText(GUIText text){
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);
		if(textBatch.isEmpty()){
			texts.remove(texts.get(text.getFont()));
		}
	}
	
	protected void resetScreen() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0, 0, 0, 1);
	}
	
	/* Toggles if the mouse is shown */
	public void showMouse(boolean show) {
		Mouse.setGrabbed(!show);
	}
}
