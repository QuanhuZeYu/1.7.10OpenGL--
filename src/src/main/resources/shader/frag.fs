#version 330
out vec4 FragColor;

struct Material {
    sampler2D diffuse;
    sampler2D specular;
    float shininess;
    vec3 diffuseColor;
    vec3 specularColor;

    bool useTexture;
    bool useDiffuseTexture;
    bool useSpecularTexture;
};

struct Light {
    vec3 position;
    vec3 direction;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    float constant;
    float linear;
    float quadratic;
    float cutOff;
    float outerCutOff;

    bool isSpot;
    bool isDirection;
};

in vec3 Normal;
in vec3 FragPos;
in vec2 TexCoords;

uniform vec3 viewPos;
uniform Material material;
uniform Light light;

void main()
{
    // 片段位置到光源位置的向量
    vec3 lightDir = normalize(light.position.rgb - FragPos); // 光源位置到片段位置的向量
    if (light.isDirection) { // 平行光
        lightDir = normalize(-light.position.rgb);
    }
    // 计算距离和衰减比率
    float distance    = length(light.position.xyz - FragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));

    // ambient
    vec3 ambient = material.useTexture ? light.ambient * texture(material.diffuse, TexCoords).rgb : light.ambient * material.diffuseColor * 0.2f;
    ambient *= attenuation;

    // diffuse
    vec3 norm = normalize(Normal);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = material.useDiffuseTexture ? light.diffuse * diff * texture(material.diffuse, TexCoords).rgb : light.diffuse * diff * material.diffuseColor;
    diffuse *= attenuation;

    // specular
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = material.useSpecularTexture ? light.specular * spec * texture(material.specular, TexCoords).rgb : light.specular * spec * material.specularColor;
    specular *= attenuation;

    vec3 result = ambient + diffuse + specular;
    if (light.isSpot) {
        float theta = dot(lightDir, normalize(-light.direction));
        // 计算光强度I
        float intensity = 0.0;
        if (theta > light.cutOff) { // 片段位置在光 sources 内
            FragColor = vec4(result, 1.0);
        } else if (theta > light.outerCutOff) {
            intensity = clamp((theta - light.outerCutOff) / (light.cutOff - light.outerCutOff), 0.0, 1.0);
        } else {
            FragColor = vec4(light.ambient * texture(material.diffuse, TexCoords).rgb, 1.0);
        }
    } else {
        FragColor = vec4(result, 1.0);
    }
}