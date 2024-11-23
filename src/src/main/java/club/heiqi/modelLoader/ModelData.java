package club.heiqi.modelLoader;

import club.heiqi.shader.ShaderProgram;
import club.heiqi.updater.render.plane.Drawable;
import org.joml.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_CURRENT_PROGRAM;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ModelData implements Drawable {
    public List<MeshData> meshes;
    public List<TextureData> textures;

    public ModelData(List<MeshData> meshes, List<TextureData> textures) {
        this.meshes = meshes;
        this.textures = textures;
    }

    public ModelData(String objPath) {
         this.meshes = loadModel(objPath);
    }

    @Override
    public void draw() {
        for (MeshData mesh : meshes) {
            glBindVertexArray(mesh.vao);
            glDrawElements(GL_TRIANGLES, mesh.vertices.indices.size(), GL_UNSIGNED_INT, 0);
            glBindVertexArray(0);
        }
    }

    public List<MeshData> loadModel(String filePath) {
        AIScene scene = aiImportFile(filePath, aiProcess_Triangulate | aiProcess_JoinIdenticalVertices | aiProcess_CalcTangentSpace | aiProcess_FixInfacingNormals);
        List<MeshData> meshes = new ArrayList<>();
        List<TextureData> textures = new ArrayList<>();
        if (scene == null)
            throw new RuntimeException("加载模型: " + filePath + " 失败\n" + aiGetErrorString());
        try {
            int numMeshes = scene.mNumMeshes();
            int numTextures = scene.mNumMaterials();
            PointerBuffer aiMeshes = scene.mMeshes();
            if (aiMeshes == null || numMeshes == 0) {
                throw new RuntimeException("模型: " + filePath + " 中没有可用的网格");
            }
            for (int i = 0; i < numMeshes; i++) {
                AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));
                MeshData meshData = loadModel_processVertices(mesh);
                meshes.add(meshData);
            }
            return meshes;
        } finally {
            aiReleaseImport(scene);
        }
    }

    public MeshData loadModel_processVertices(AIMesh mesh) {
        int vecCount = mesh.mNumVertices();
        boolean hasNorms = mesh.mNormals() != null;
        boolean hasTexCoords = mesh.mTextureCoords(0) != null;
        boolean hasColors = mesh.mColors(0) != null;
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textureCoords = new ArrayList<>();
        List<Vector4f> colors = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < vecCount; i++) {
            AIVector3D vec = mesh.mVertices().get(i);
            vertices.add(new Vector3f(vec.x(), vec.y(), vec.z()));
            if (hasNorms) {
                AIVector3D aiNormal = mesh.mNormals().get(i);
                normals.add(new Vector3f(aiNormal.x(), aiNormal.y(), aiNormal.z()));
            }
            if (hasTexCoords) {
                AIVector3D aiTexCoord = mesh.mTextureCoords(0).get(i);
                textureCoords.add(new Vector2f(aiTexCoord.x(), aiTexCoord.y()));
            }
            if (hasColors) {
                AIColor4D aiColor4D = mesh.mColors(0).get(i);
                colors.add(new Vector4f(aiColor4D.r(), aiColor4D.g(), aiColor4D.b(), aiColor4D.a()));
            }
        }
        for (int i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = mesh.mFaces().get(i);
            for (int j = 0; j < face.mNumIndices(); j++) {
                int index = face.mIndices().get(j)/* + faceVertexIDOffset*/;
                indices.add(index);
            }
        }
        VertexData vertexData = new VertexData(vertices, normals, textureCoords, colors, indices);
        return new MeshData(vertexData, null);
    }

    private static String getTextureTypeName(int textureType) {
        switch (textureType) {
            case aiTextureType_DIFFUSE:
                return "Diffuse";
            case aiTextureType_SPECULAR:
                return "Specular";
            case aiTextureType_AMBIENT:
                return "Ambient";
            case aiTextureType_EMISSIVE:
                return "Emissive";
            case aiTextureType_HEIGHT:
                return "Height";
            case aiTextureType_NORMALS:
                return "Normals";
            case aiTextureType_SHININESS:
                return "Shininess";
            case aiTextureType_OPACITY:
                return "Opacity";
            case aiTextureType_DISPLACEMENT:
                return "Displacement";
            case aiTextureType_LIGHTMAP:
                return "Lightmap";
            case aiTextureType_REFLECTION:
                return "Reflection";
            default:
                return "Unknown";
        }
    }
}
