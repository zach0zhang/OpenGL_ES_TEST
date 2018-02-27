package com.example.zkj.opengles2test;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by zkj on 2018/2/25.
 */

public class MyRenderer implements GLSurfaceView.Renderer{

    private static final int POSITION_COMPONENT_COUNT = 2;

    private final FloatBuffer vertexData;

    private int program;

    private static final String U_COLOR = "u_Color";
    private int uColorLocation;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private static final String U_MATRIX = "u_Matrix";
    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;

    private final float[] modelMatrix = new float[16];

    private static final String VERTEX_SHADER =
            "uniform mat4 u_Matrix;\n"
                    +"attribute vec4 a_Position;\n"
                    +"void main() {\n"
                    +"gl_Position = u_Matrix * a_Position;\n"
                    +"gl_PointSize = 10.0;\n"
                    +"}";
    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n"
                    +"uniform vec4 u_Color;\n"
                    + "void main() {\n"
                    + " gl_FragColor = u_Color;\n"
                    + "}";
    private static final float[] VERTEX = {
            //Triangle 1
            -0.5f, -0.8f,
             0.5f,  0.8f,
            -0.5f,  0.8f,

            //Triangle 2
            -0.5f, -0.8f,
            0.5f, -0.8f,
            0.5f, 0.8f
    };

    public MyRenderer() {
        vertexData = ByteBuffer.allocateDirect(VERTEX.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(VERTEX);
    }

    static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f,0.0f,0.0f,0.0f);

        program = GLES20.glCreateProgram();
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        glUseProgram(program);

        uColorLocation = glGetUniformLocation(program,U_COLOR);
        aPositionLocation = glGetAttribLocation(program,A_POSITION);
        uMatrixLocation = glGetUniformLocation(program,U_MATRIX);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation,POSITION_COMPONENT_COUNT,GL_FLOAT,false,0,vertexData);
        glEnableVertexAttribArray(aPositionLocation);
        }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0,width,height);

        perspectiveM(projectionMatrix,0,45,(float)width/(float)height,1f,10f);

        setIdentityM(modelMatrix,0);
        translateM(modelMatrix,0,0f,0f,-2.5f);
        rotateM(modelMatrix,0,-60f,1f,0f,0f);

        final float[] temp = new float[16];
        multiplyMM(temp,0,projectionMatrix,0,modelMatrix,0);
        System.arraycopy(temp,0,projectionMatrix,0,temp.length);

        /*
        final float aspectRatio = width > height ? (float)width / (float)height : (float)height / (float)width;

        if (width > height) {
            orthoM(projectionMatrix,0,-aspectRatio,aspectRatio,-1f,1f,-1f,1f);
        }
        else{
            orthoM(projectionMatrix,0,-1f,1f,-aspectRatio,aspectRatio,-1f,1f);
        }
        */
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        glUniformMatrix4fv(uMatrixLocation,1,false,projectionMatrix,0);

        glUniform4f(uColorLocation,1.0f,1.0f,1.0f,1.0f);
        glDrawArrays(GL_TRIANGLES,0,6);

    }
}
