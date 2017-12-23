package engine.guis.font;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.guis.font.mesh.FontType;
import engine.guis.font.mesh.GUIText;

public class FontManager {
	
	private static Map<String, FontType> fonts = new HashMap<String, FontType>();
	private static FontRenderer renderer;
	
	public static void initialize(){
		renderer = new FontRenderer();
	}
	
	public static void render(HashMap<FontType, List<GUIText>> texts){
		renderer.render(texts);
	}
	
	
	public static void addFont(String font) {
		FontType fontType = new FontType(font);
		FontManager.fonts.put(font, fontType);
	}
	
	public static FontType font(String font) {
		return FontManager.fonts.get(font);
	}
	
	public static void cleanUp(){
		renderer.cleanUp();
	}

}
