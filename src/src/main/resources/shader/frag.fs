#version 330
out vec4 FragColor;

struct Material {
    sampler2D diffuse;
    sampler2D specular;
    float shininess;
};

struct Light {
    vec4 position;
    vec3 direction;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    float constant;
    float linear;
    float quadratic;
    float cutOff;

    bool isSpot;
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
    if (light.position.w == 0.0) { // 平行光
        lightDir = normalize(-light.position.rgb);
    }
    // 计算距离和衰减比率
    float distance    = length(light.position.xyz - FragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));

    // ambient
    vec3 ambient = light.ambient * texture(material.diffuse, TexCoords).rgb;
    ambient *= attenuation;

    // diffuse
    vec3 norm = normalize(Normal);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = light.diffuse * diff * texture(material.diffuse, TexCoords).rgb;
    diffuse *= attenuation;

    // specular
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = light.specular * spec * texture(material.specular, TexCoords).rgb;
    specular *= attenuation;

    vec3 result = ambient + diffuse + specular;
    if (light.isSpot) {
        float theta = dot(lightDir, normalize(-light.direction));
        if (theta > light.cutOff) { // 片段位置在光 sources 内
            FragColor = vec4(result, 1.0);
        } else {
            FragColor = vec4(light.ambient * texture(material.diffuse, TexCoords).rgb, 1.0);
        }
    } else {
        FragColor = vec4(result, 1.0);
    }
}