package lazy.lwjgl;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;

public class Main {

    public static final float[] vertices = {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.0f, 0.5f, 0.0f
    };

    public static final String vertex = "#version 330 core\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "out vec4 outPos;\n" +
            "void main()\n" +
            "{\n" +
            "   gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" +
            "   outPos = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" +
            "}";

    public static final String fragment = "#version 330 core\n" +
            "out vec4 FragColor;\n" +
            "in vec4 outPos;\n" +
            "void main()\n" +
            "{\n" +
            "FragColor = outPos;\n" +
            "} ";

    public static void main(String[] args) {

        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit())
            System.out.println("GLFW couldn't initialize!");

        GLFW.glfwDefaultWindowHints();
        long window = GLFW.glfwCreateWindow(1280, 720, "Triangle", 0, 0);
        if (window == 0) {
            System.out.println("Window wasn't created!");
            GLFW.glfwTerminate();
        }

        GLFW.glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
                GLFW.glfwSetWindowShouldClose(window, true);
        });

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);

        GL.createCapabilities();

        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexShader, vertex);
        GL20.glCompileShader(vertexShader);

        if (GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(vertexShader));
        }

        int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentShader, fragment);
        GL20.glCompileShader(fragmentShader);

        if (GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(fragmentShader));
        }

        int shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexShader);
        GL20.glAttachShader(shaderProgram, fragmentShader);
        GL20.glLinkProgram(shaderProgram);

        if (GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(shaderProgram));
        }

        int vbo = GL15.glGenBuffers();
        int vao = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 3, GL15.GL_FLOAT, false, 3 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        GL33.glBindVertexArray(0);

        GL20.glUseProgram(shaderProgram);

        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            GL11.glClearColor(1f, 1f, 0f, 1f);
            GL33.glBindVertexArray(vao);
            GL20.glDrawArrays(GL20.GL_TRIANGLES, 0, 3);
            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }

        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);

        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }
}