package com.example.android_denoise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import android.content.Context;
import android.opengl.GLES20;

class RawResourceReader
{
	public static String readTextFileFromRawResource(final Context context,
			final int resourceId)
	{
		final InputStream inputStream = context.getResources().openRawResource(
				resourceId);
		final InputStreamReader inputStreamReader = new InputStreamReader(
				inputStream);
		final BufferedReader bufferedReader = new BufferedReader(
				inputStreamReader);

		String nextLine;
		final StringBuilder body = new StringBuilder();

		try
		{
			while ((nextLine = bufferedReader.readLine()) != null)
			{
				body.append(nextLine);
				body.append('\n');
			}
		}
		catch (IOException e)
		{
			return null;
		}

		return body.toString();
	}
}

public class MyTexture {
	private int m_init_lines_program, m_find_lines_program;
	int [] m_textures; // 0=initial, 1=temp, 2=result to render
	public MyTexture(){
		
	}
	
	private void initPrograms(Context actvity_context){
		int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, RawResourceReader.readTextFileFromRawResource(actvity_context,
								R.raw.single_tex));
		int fragmentShaderHandleInitLines = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, RawResourceReader.readTextFileFromRawResource(actvity_context, R.raw.init_denoise_lines));
		int fragmentShaderHandleFindLines = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, RawResourceReader.readTextFileFromRawResource(actvity_context, R.raw.find_denoise_lines));
		
		m_init_lines_program = ShaderHelper.createAndLinkProgram(
				vertexShaderHandle,
				fragmentShaderHandleInitLines, 
				new String[] { "v_tex_coord", "s_original", "s_square_avg", "u_sigma", "u_points" });
		m_find_lines_program = ShaderHelper.createAndLinkProgram(
				vertexShaderHandle,
				fragmentShaderHandleFindLines, 
				new String[] { "v_tex_coord", "s_result", "s_original", "s_square_avg", "u_sigma", "u_square", "u_line_angle", "u_points"}
				);
	}
	
	public void setTextures(int[] textures, Context actvity_context){
		m_textures = textures;
		initPrograms(actvity_context);
	}
	
	private FloatBuffer mTextureBuffer; 
	protected void setTextureCoordinates(float[] textureCoords) {
		// float is 4 bytes, therefore we multiply the number if
	        // vertices with 4.
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(
	                                           textureCoords.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		mTextureBuffer = byteBuf.asFloatBuffer();
		mTextureBuffer.put(textureCoords);
		mTextureBuffer.position(0);
	}
	
	public void render(float param_sigma, int param_square){
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m_textures[0]);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		
		float textureCoordinates[] = {0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f };
		setTextureCoordinates(textureCoordinates);
		
		GLES20.glEnableVertexAttribArray(0);
	    GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
	    GLES20.glDrawElements(GLES20.GL_TRIANGLES, mTextureBuffer.capacity(), GLES20.GL_UNSIGNED_INT, mTextureBuffer);
	}
}
