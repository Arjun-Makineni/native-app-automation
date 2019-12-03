package com.riverisland.app.automation.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prashant Ramcharan on 20/03/2017
 */
public final class YamlLoaderUtils {

    public static <T> List<T> loadYamlDataCollection(String yamlFile, Class<T> type) {
        final Yaml yaml = new Yaml();

        final ObjectMapper objectMapper = new ObjectMapper();
        final List<T> yamlData = new ArrayList<>();

        try {
            final Iterable<T> entries = (Iterable<T>) yaml.loadAll(ClassLoader.getSystemResourceAsStream(yamlFile));
            entries.forEach(e -> yamlData.add(objectMapper.convertValue(e, type)));
        } catch (Exception e) {
            throw new RuntimeException("Unable to load YAML data. Error: " + e.getMessage());
        }
        return yamlData;
    }
}