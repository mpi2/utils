package org.mousephenotype.dcc.utils.io.conf;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileReader<T> {

    private static final Logger logger = LoggerFactory.getLogger(FileReader.class);
    private PropertiesConfiguration propertiesConfiguration;

    private List<T> read(Path path, Command<T> command) {
        List<T> results = new ArrayList<>();
        T result = null;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                result = command.execute(line);
                if (result != null) {
                    results.add(result);
                }
            }
        } catch (IOException ex) {
            logger.error("", ex);
        }
        return results;
    }

    public List<T> printFile(String filename, Command<T> commmand) throws ConfigurationException {
        propertiesConfiguration = new PropertiesConfiguration(filename);
        String basePath = propertiesConfiguration.getBasePath();
        List<T> results = null;
        if (basePath.startsWith("file")) {
            results = this.read(propertiesConfiguration.getFile().toPath(), commmand);
        }

        if (basePath.startsWith("jar")) {
            URI create = URI.create(basePath.substring(0, basePath.indexOf("!")));
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");

            try (FileSystem zipfs = FileSystems.newFileSystem(create, env)) {
                Path pathInZipfile = zipfs.getPath(basePath.substring(basePath.indexOf("!") + 2, basePath.length()));
                results = this.read(pathInZipfile, commmand);
            } catch (IOException ex) {
                logger.error("", ex);
            }
        }
        return results;
    }

    public static String printFile(String filename) throws ConfigurationException {
        StringBuilder sb = new StringBuilder();
        List<String> results;
        FileReader<String> fileReader = new FileReader<>();
        results = fileReader.printFile(filename, new StringReaderCommand());
        for (String result : results) {
                sb.append(result);
                sb.append("\n");
        }
        return sb.toString();
    }
}
