package engine.world.entities;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import engine.rendering.MasterRenderer;
import engine.rendering.ShaderProgram;
import engine.util.Calculator;

/**
 * @Description: Loads variables about entities to the shaders
 * 
 */

public class StaticShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/engine/world/entities/VertexShader.txt";
	private static final String FRAGMENT_FILE = "/engine/world/entities/FragmentShader.txt";

	private static final int MAX_LIGHTS = 4;

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColor[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColor;
	private int location_numberOfRows;
	private int location_offset;
	private int location_plane;
	private int location_toShadowMapSpace;
	private int location_shadowMap;
	private int location_fogdensity;
	private int location_foggradient;
	private int location_fogdivider;
	private int location_selected;
	
	/* Constructor Method */
	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	/* Loads the VAOs to the shader */
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	/* Loads the shadow map to the shader */
	public void connectTextureUnits() {
		super.loadInt(location_shadowMap, 5);
	}

	/* Gets the location of each uniform variable in the shader */
	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_skyColor = super.getUniformLocation("skyColor");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_offset = super.getUniformLocation("offset");
		location_plane = super.getUniformLocation("plane");
		location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
		location_shadowMap = super.getUniformLocation("shadowMap");
		location_fogdensity = super.getUniformLocation("density");
		location_foggradient = super.getUniformLocation("gradient");
		location_fogdivider = super.getUniformLocation("divider");
		location_selected = super.getUniformLocation("selected");
		
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColor = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];

		for (int i = 0; i < MAX_LIGHTS; i++) {
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
	}
	
	public void loadIsSelected(boolean selected) {
		super.loadBoolean(location_selected, selected);
	}

	/* Loads the fog settings to the shader */
	public void loadFogSettings() {
		super.loadFloat(location_fogdensity, MasterRenderer.FOG_DENSITY);
		super.loadFloat(location_foggradient, MasterRenderer.FOG_GRADIENT);
		super.loadFloat(location_fogdivider, MasterRenderer.FOG_DIVIDER);
	}
	
	/* Load the shadow matrix to the shader */
	public void loadToShadowSpaceMatrix(Matrix4f matrix) {
		super.loadMatrix(location_toShadowMapSpace, matrix);
	}

	/* Load the clipping plane to the shader */
	public void loadClipPlane(Vector4f plane) {
		super.load4DVector(location_plane, plane);
	}

	/* Load the numbers of rows in the texture to the shader */
	public void loadNumberOfRows(int numberOfRows) {
		super.loadFloat(location_numberOfRows, numberOfRows);
	}

	/* Load the offset for the textures in the texture map */
	public void loadOffset(float x, float y) {
		super.load2DVector(location_offset, new Vector2f(x, y));
	}

	/* Loads the sky color to the shader */
	public void loadSkyColor(Vector3f color) {
		super.loadVector(location_skyColor, color);
	}

	/* Loads the boolean if there is fake lighting */
	public void loadFakeLightingVariable(boolean useFake) {
		super.loadBoolean(location_useFakeLighting, useFake);
	}

	/* Loads the shine settings to the shader */
	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}

	/* Loads the light to the shader */
	public void loadLights(List<Light> lights) {
		for (int i = 0; i < MAX_LIGHTS; i++) {
			if (i < lights.size()) {
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(location_lightColor[i], lights.get(i).getColor());
				super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
			} else {
				super.loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector(location_lightColor[i], new Vector3f(0, 0, 0));
				super.loadVector(location_attenuation[i], new Vector3f(1, 0, 0));
			}
		}
	}

	/* Loads the view matrix from the camera to the shader */
	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Calculator.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

	/* Loads the transformation matrix to the shader */
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	/* Loads the projection matrix to the shader */
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}
}
