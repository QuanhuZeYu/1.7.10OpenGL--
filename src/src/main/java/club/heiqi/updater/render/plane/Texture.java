package club.heiqi.updater.render.plane;

public class Texture {
    public String path;
    public String registryName;
    public int textureID;
    public int activeTexturePose;

    public Texture(String path, String registryName, int textureID, int activeTexturePose) {
        this.path = path;
        this.registryName = registryName;
        this.textureID = textureID;
        this.activeTexturePose = activeTexturePose;
    }

    public Texture(String path, String registryName, int activeTexturePose) {
        this.path = path;
        this.registryName = registryName;
        this.activeTexturePose = activeTexturePose;
    }

    @Override
    public String toString() {
        return "Texture{" +
                "path='" + path + '\'' +
                ", registryName='" + registryName + '\'' +
                ", textureID=" + textureID +
                ", activeTexturePose=" + activeTexturePose +
                '}';
    }
}
