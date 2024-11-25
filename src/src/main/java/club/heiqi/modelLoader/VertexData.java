package club.heiqi.modelLoader;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public class VertexData {
    public List<Vector3f> positions;
    public List<Vector3f> normals;
    public List<Vector2f> textureCoords;
    public List<Vector4f> colors;
    public List<Integer> indices;

    public VertexData(List<Vector3f> positions, List<Vector3f> normals,
                      List<Vector2f> textureCoords, List<Vector4f> colors,
                      List<Integer> indices) {
        this.positions = positions;
        this.normals = normals;
        this.textureCoords = textureCoords;
        this.colors = colors;
        this.indices = indices;
    }

    public float[] getVertices() {
        float[] vertices = new float[positions.size() * 3];
        for (int i = 0; i < positions.size(); i++) {
            vertices[i * 3] = positions.get(i).x;
            vertices[i * 3 + 1] = positions.get(i).y;
            vertices[i * 3 + 2] = positions.get(i).z;
        }
        return vertices;
    }

    public float[] getNormal() {
        float[] normals = new float[this.normals.size() * 3];
        for (int i = 0; i < this.normals.size(); i++) {
            normals[i * 3] = this.normals.get(i).x;
            normals[i * 3 + 1] = this.normals.get(i).y;
            normals[i * 3 + 2] = this.normals.get(i).z;
        }
        return normals;
    }

    public float[] getTextureCoords() {
        float[] textureCoords = new float[this.textureCoords.size() * 2];
        for (int i = 0; i < this.textureCoords.size(); i++) {
            textureCoords[i * 2] = this.textureCoords.get(i).x;
            textureCoords[i * 2 + 1] = this.textureCoords.get(i).y;
        }
        return textureCoords;
    }

    public float[] getColors() {
        float[] colors = new float[this.colors.size() * 4];
        for (int i = 0; i < this.colors.size(); i++) {
            colors[i * 4] = this.colors.get(i).x;
            colors[i * 4 + 1] = this.colors.get(i).y;
            colors[i * 4 + 2] = this.colors.get(i).z;
            colors[i * 4 + 3] = this.colors.get(i).w;
        }
        return colors;
    }

    public int[] getIndices() {
        return indices.stream().mapToInt(i -> i).toArray();
    }
}
