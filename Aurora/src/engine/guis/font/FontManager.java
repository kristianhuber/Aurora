package engine.guis.font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.guis.font.mesh.FontType;
import engine.guis.font.mesh.GUIText;
import engine.guis.font.mesh.TextMeshData;
import engine.rendering.models.ModelManager;

public class FontManager {
	
	private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
	private static Map<String, FontType> fonts = new HashMap<String, FontType>();
	private static FontRenderer renderer;
	
	public static void initialize(){
		renderer = new FontRenderer();
	}
	
	public static void render(){
		renderer.render(texts);
	}
	
	public static void loadText(GUIText text){
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
	
	public static void addFont(String font) {
		FontType fontType = new FontType(font);
		FontManager.fonts.put(font, fontType);
	}
	
	public static FontType font(String font) {
		return FontManager.fonts.get(font);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public static void removeText(GUIText text){
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);
		if(textBatch.isEmpty()){
			texts.remove(texts.get(text.getFont()));
		}
	}
	
	public static void cleanUp(){
		renderer.cleanUp();
	}

}
