package engine.rendering;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 * @Description: Base class for all shaders in the game
 * 
 */

public abstract class ShaderProgram {

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	private int fragmentShaderID, vertexShaderID, programID;

	/* Constructor Method */
	public ShaderProgram(String vertexFile, String fragmentFile) {

		// Loads the shaders
		this.fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		this.vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);

		// Creates a shader with the two files
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);

		// Loads attributes to the input of the shader
		this.bindAttributes();

		// Registers the shader with OpenGL
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);

		// Loads location of uniform variables in the shader
		this.getAllUniformLocations();
	}

	protected abstract void getAllUniformLocations();

	protected abstract void bindAttributes();

	/* Gets a uniform location in the shader */
	protected int getUniformLocation(String name) {
		return GL20.glGetUniformLocation(programID, name);
	}

	/* Loads a float to the shader */
	protected void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}

	/* Loads an integer to the shader */
	protected void loadInt(int location, int value) {
		GL20.glUniform1i(location, value);
	}

	/* Loads a 2D vector to the shader */
	protected void load2DVector(int location, Vector2f vector) {
		GL20.glUniform2f(location, vector.x, vector.y);
	}

	/* Loads a 3D vector to the shader */
	protected void loadVector(int location, Vector3f vector) {
		GL20.glUniform3f(location, vector.x, vector.y, vector.z);
	}

	/* Loads a 4D vector to the shader */
	protected void load4DVector(int location, Vector4f vector) {
		GL20.glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
	}

	/* Loads a boolean to the shader as an integer */
	protected void loadBoolean(int location, boolean value) {
		float toLoad = 0;
		if (value) {
			toLoad = 1;
		}
		GL20.glUniform1f(location, toLoad);
	}

	/* Loads a matrix the shader */
	protected void loadMatrix(int location, Matrix4f matrix) {
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(location, false, matrixBuffer);
	}

	/* Loads a VAO to the input of a shader */
	protected void bindAttribute(int attribute, String variableName) {
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}

	/* Sets the current shader to this */
	public void start() {
		GL20.glUseProgram(programID);
	}

	/* Sets the current shader to nothing */
	public void stop() {
		GL20.glUseProgram(0);
	}

	/* Cleans up memory */
	public void cleanUp() {
		this.stop();

		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);

		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);

		GL20.glDeleteProgram(programID);
	}

	/* Parses the shader file and compiles it */
	private static int loadShader(String file, int type) {

		StringBuilder shaderSource = new StringBuilder();

		try {
			// Tries to read the file
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(ShaderProgram.class.getResourceAsStream(file)));

			// Reads line by line
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}

			reader.close();

		} catch (Exception e) {
			// Closes if the shader doesn't work
			System.err.println("[Console]: Could not read '" + file + "'");

			e.printStackTrace();

			System.exit(0);
		}

		// Creates a shader
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);

		// Complies the shader
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {

			System.err.println("Could not compile shader " + file);

			System.err.println(GL20.glGetShaderInfoLog(shaderID, 500));

			System.exit(0);
		}

		return shaderID;
	}
}
