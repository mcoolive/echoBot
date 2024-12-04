package fr.mcoolive.echo_bot.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RulesEngineFactory {
    private final RulesLoader rulesLoader;

    public RulesEngineFactory(RulesLoader rulesLoader) {
        this.rulesLoader = rulesLoader;
    }

    /**
     * We create one RulesEngine for each configuration file and then combine them all.
     */
    public RulesEngine<Object, String> newRulesEngine(List<String> cfgPaths, String fallbackOutput) {
        final RulesEngine<Object, String> fallback = RulesEngine.fallback(fallbackOutput);
        return cfgPaths.stream()
                .map(rulesLoader::loadRules).map(RulesEngine::basic)
                //.map(this::newReloadable)
                .reduce(RulesEngine::andThen)
                .map(e -> e.andThen(fallback)).orElse(fallback);
    }

    RulesEngine<Object, String> newReloadable(String cfgPath) {
        return new ReloadableRulesEngine(cfgPath);
    }


    class ReloadableRulesEngine implements RulesEngine<Object, String> {
        private final String cfgPath;
        private volatile RulesEngine<Object, String> delegate;

        public ReloadableRulesEngine(String cfgPath) {
            Objects.requireNonNull(cfgPath);
            this.cfgPath = cfgPath;
            reload();
        }

        public void reload() {
            this.delegate = RulesEngine.basic(rulesLoader.loadRules(cfgPath));
        }

        public Optional<Rule.Result<String>> execute(Object input) {
            return delegate.execute(input);
        }
    }
}
