package fr.mcoolive.echo_bot.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class RulesLoader {

    private final RulesLoaderFromCsv csvLoader;
    private final RulesLoaderFromYaml yamlLoader;

    protected RulesLoader(RulesLoaderFromCsv csvLoader, RulesLoaderFromYaml yamlLoader) {
        this.csvLoader = csvLoader;
        this.yamlLoader = yamlLoader;
    }

    public RulesLoader() {
        this(new RulesLoaderFromCsv(), new RulesLoaderFromYaml());
    }

    public List<Rule<Object, String>> loadRules(String path) throws UncheckedIOException {
        final Path pathFile = Paths.get(path);
        if (RulesLoader.isCsvFile(path)) {
            try (InputStream csv = Files.newInputStream(pathFile)) {
                return csvLoader.loadRules(csv, path);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        } else if (RulesLoader.isYamlFile(path)) {
            try (InputStream yaml = Files.newInputStream(pathFile)) {
                return yamlLoader.loadRules(yaml, path);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        } else {
            throw new IllegalArgumentException("The rules configuration file '" + path + "' cannot be loaded. Only '.csv' and '.yaml' extensions are supported.");
        }
    }

    static boolean isCsvFile(String path) {
        final String lowerCase = path.toLowerCase();
        return lowerCase.endsWith(".csv");
    }

    static boolean isYamlFile(String path) {
        final String lowerCase = path.toLowerCase();
        return lowerCase.endsWith(".yaml") || lowerCase.endsWith(".yml");
    }

    /**
     * The delay expressed a duration to wait before to return the response.
     * When the delay is missing, we return 0 that means "no-delay".
     * When the delay is not a number (NO_RESPONSE for example), we return -1 that means "no-response".
     */
    static int parseDelay(String value) {
        if (value == null || value.isEmpty()) return 0;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
