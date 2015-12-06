package com.example.android_denoise;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
//import android.util.Log;
import android.widget.Toast;

public class MyGLRenderer implements GLSurfaceView.Renderer {
	private int[] textures;
	
	GLSurfaceView m_glView;
	
	public MyGLRenderer(GLSurfaceView parent, final Context activityContext)
	{
		m_glView = parent;
		textures = new int[1];
		textures[0] = 0;
		mActivityContext = activityContext;
	}
	
	
	public volatile float mAngle;

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }
	
	public static int loadShader(int type, String shaderCode){
	    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
	    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
	    int shader = GLES20.glCreateShader(type);

	    // add the source code to the shader and compile it
	    GLES20.glShaderSource(shader, shaderCode);
	    GLES20.glCompileShader(shader);

	    return shader;
	}
	private final Context mActivityContext;
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		int VERTEX_SIZE = (2+2)*4;
        byteBuffer = ByteBuffer.allocateDirect(3*VERTEX_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertices = byteBuffer.asFloatBuffer();
        vertices.put(new float[] { 0.0f, 0.0f, 0.0f, 1.0f,
                                   319.0f, 0.0f, 1.0f, 1.0f,
                                   160.0f, 479.0f, 0.5f, 0.0f } );
        vertices.flip();
        
        textures[0] = TextureHelper.loadTextureFromResource(mActivityContext, R.drawable.ic_launcher);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);		
	}

	ByteBuffer byteBuffer;
    FloatBuffer vertices;
    
	@Override
	public void onDrawFrame(GL10 gl) {
		
		gl.glViewport(0, 0, m_glView.getWidth(), m_glView.getHeight());
        gl.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(0, 320, 0, 480, 1, -1);

        //Here two lines that I added. Two lines I have declared above in try_catch
        //So, WHY I NEED TO DECLARE AGAIN ??!!!
        gl.glEnable(GLES20.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        int VERTEX_SIZE = (2+2)*4;
        vertices.position(0);
        gl.glVertexPointer(2, GL10.GL_FLOAT, VERTEX_SIZE, vertices);
        vertices.position(2);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, VERTEX_SIZE, vertices);

        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
	}
		
	public int loadTexture (String filename)
	{
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;

		// Read in the resource
		final Bitmap bitmap = BitmapFactory.decodeFile(filename, options);

		int[] textureId = new int[1];

	    GLES20.glGenTextures ( 1, textureId, 0 );
	    GLES20.glBindTexture ( GLES20.GL_TEXTURE_2D, textureId[0] );

	    GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR );
	    GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR );
	    GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE );
	    GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE );

	    GLES20.glEnable(GLES20.GL_BLEND);
	    GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    try{
	    	GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
	    }
	    catch(Exception ex)
	    {
	        ex.printStackTrace();
	    }

	    textures[0] = textureId[0];
	    
	    return textureId[0];
	}
}