package com.example.android_denoise;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

class MySlider 
{
	final float[] m_line_color, m_circle_color;
	/** Store our model data in a float buffer. */
	private FloatBuffer mLineVertices4;
	private FloatBuffer mCircleVertices4;
	/** How many elements per vertex. XYZ+RGBA*/
	private final int mStrideBytes = 3 * 4; // XYZ * bytes per float
	/** Size of the position data in elements. */
	private final int mPositionDataSize = 3;
	
	private void drawCircle(int programHandle, int mMVPMatrixHandle, int mPositionHandle, float[] mMVPMatrix, float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix){
		mCircleVertices4.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mCircleVertices4);        
		GLES20.glEnableVertexAttribArray(mPositionHandle);       
		GLES20.glUniform4f(GLES20.glGetUniformLocation(programHandle, "v_Color"), m_circle_color[0], m_circle_color[1], m_circle_color[2], m_circle_color[3]);
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
	}
	private void drawLine(int programHandle, int mMVPMatrixHandle, int mPositionHandle, float[] mMVPMatrix, float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix){
		mLineVertices4.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mLineVertices4);        
		GLES20.glEnableVertexAttribArray(mPositionHandle);       
		GLES20.glUniform4f(GLES20.glGetUniformLocation(programHandle, "v_Color"), m_line_color[0], m_line_color[1], m_line_color[2], m_line_color[3]);        
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
	}
	public MySlider(float[] line_color, float[] circle_color){
		m_line_color = line_color;
		m_circle_color = circle_color;
		final float[] line_data = {
				-0.95f, 0.95f, 0.0f, 
	            0.95f, 0.95f, 0.0f, 
	            0.95f, 0.9f, 0.0f,
	            -0.95f, 0.9f, 0.0f
		};
		final float[] circle_data = {
				-0.9f, 0.97f, 0.0f, 
	            -0.8f, 0.97f, 0.0f, 
	            -0.8f, 0.87f, 0.0f,
	            -0.9f, 0.87f, 0.0f
		};
		mLineVertices4 = ByteBuffer.allocateDirect(line_data.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mLineVertices4.put(line_data).position(0);		
		mCircleVertices4 = ByteBuffer.allocateDirect(line_data.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mCircleVertices4.put(circle_data).position(0);		
	}
	public void slide(float line_percent){
		
	}
	public void draw(int programHandle, int mMVPMatrixHandle, int mPositionHandle, float[] mMVPMatrix, float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix){
		drawLine(programHandle, mMVPMatrixHandle, mPositionHandle, mMVPMatrix, mViewMatrix, mModelMatrix, mProjectionMatrix);
		drawCircle(programHandle, mMVPMatrixHandle, mPositionHandle, mMVPMatrix, mViewMatrix, mModelMatrix, mProjectionMatrix);
	}
}