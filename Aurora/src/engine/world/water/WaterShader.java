package engine.world.water;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.rendering.MasterRenderer;
import engine.rendering.ShaderProgram;
import engine.util.Calculator;
import engine.util.Engine;
import engine.world.entities.Light;

/**
 * @Description: Loads settings to the water shaders
 * 
 * */

public class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = "/engine/world/water/waterVertex.txt";
	private final static String FRAGMENT_FILE = "/engine/world/water/waterFragment.txt";

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int location_reflectionTexture;
	private int location_refractionTexture;
	private int location_dudvMap;
	private int location_moveFactor;
	private int location_cameraPosition;
	private int location_normalMap;
	private int location_lightColor;
	private int location_lightPosition;
	private int location_depthMap;
	private int location_nearPlane;
	private int location_farPlane;
	private int location_skyColor;
	private int location_fogdensity;
	private int location_foggradient;
	private int location_fogdivider;
	
	/* Constructor Method */
	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	/* Binds the VAOs to the shader */
	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}

	/* Gets all of the variables from the shader */
	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");
		location_reflectionTexture = getUniformLocation("reflectionTexture");
		location_refractionTexture = getUniformLocation("refractionTexture");
		location_dudvMap = getUniformLocation("dudvMap");
		location_moveFactor = getUniformLocation("moveFactor");
		location_cameraPosition = getUniformLocation("cameraPosition");
		location_normalMap = getUniformLocation("normalMap");
		location_lightColor = getUniformLocation("lightColor");
		location_lightPosition = getUniformLocation("lightPosition");
		location_depthMap = getUniformLocation("depthMap");
		location_nearPlane = getUniformLocation("nearPlane");
		location_farPlane = getUniformLocation("farPlane");
		location_skyColor = getUniformLocation("skyColor");
		location_fogdensity = getUniformLocation("density");
		location_foggradient = getUniformLocation("gradient");
		location_fogdivider = getUniformLocation("divider");
	}
	
	/* Loads plane settings to the shader */
	public void loadPlaneSettings(float nearPlane, float farPlane){
		super.loadFloat(location_nearPlane, nearPlane);
		super.loadFloat(location_farPlane, farPlane);
	}
	
	/* Loads a light to the shader */
	public void loadLight(Light sun){
		super.loadVector(location_lightColor, sun.getColor());
		super.loadVector(location_lightPosition, sun.getPosition());
	}
	
	/* Loads the water movement factor to the shader */
	public void loadMoveFactor(float factor){
		super.loadFloat(location_moveFactor, factor);
	}
	
	/* Loads all of the textures to the shader */
	public void connectTextureUnits(){
		super.loadInt(location_reflectionTexture, 0);
		super.loadInt(location_refractionTexture, 1);
		super.loadInt(location_dudvMap, 2);
		super.loadInt(location_normalMap, 3);
		super.loadInt(location_depthMap, 4);
	}
	
	/* Loads the fog settings to the shader */
	public void loadFogSettings() {
		super.loadFloat(location_fogdensity, MasterRenderer.FOG_DENSITY);
		super.loadFloat(location_foggradient, MasterRenderer.FOG_GRADIENT);
		super.loadFloat(location_fogdivider, MasterRenderer.FOG_DIVIDER);
	}
	
	/* Loads the sky color to the shader */
	public void loadSkyColor(Vector3f skyColor){
		super.loadVector(location_skyColor, skyColor);
	}
	
	/* Loads the projection matrix to the shader */
	public void loadProjectionMatrix(Matrix4f projection) {
		loadMatrix(location_projectionMatrix, projection);
	}
	
	/* Loads the view matrix to the shader */
	public void loadViewMatrix(){
		Matrix4f viewMatrix = Calculator.createViewMatrix(Engine.getCamera());
		loadMatrix(location_viewMatrix, viewMatrix);
		super.loadVector(location_cameraPosition, Engine.getCamera().getPosition());
	}

	/* Loads the model matrix to the shader */
	public void loadModelMatrix(Matrix4f modelMatrix){
		loadMatrix(location_modelMatrix, modelMatrix);
	}

}
