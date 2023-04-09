package ru.clevertec.cashreceipt.servletremaster.config;

import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

@Getter
public class YamlConfig {

    private final Map<String, Map<String, String>> yamlMap;

    public YamlConfig() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("application.yaml");
        yamlMap = yaml.load(inputStream);
    }

}
