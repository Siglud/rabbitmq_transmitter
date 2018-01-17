package com.kanjian.cappy.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 负责RabbitMQ的配置存储
 */
public class RabbitConfig {
    private static Logger LOGGER = LoggerFactory.getLogger(RabbitConfig.class.getName());

    private List<RabbitPair> pairs;

    private static class ConfigHolder {
        private static final RabbitConfig holder = new RabbitConfig();
    }

    public static RabbitConfig getInstance() {
        return ConfigHolder.holder;
    }

    public List<RabbitPair> getPairs() {
        return pairs;
    }

    private RabbitConfig() { }

    private List<RabbitPair> parseArgs(String inputString) {
        String[] settings = inputString.split(";");
        List<RabbitPair> res = new ArrayList<>();

        for (String setting : settings) {
            if (setting.isEmpty()) {
                continue;
            }
            String[] inAndOut = setting.split("\\|");
            if (inAndOut.length != 2) {
                continue;
            }
            String[] in = inAndOut[0].split(",");
            String[] out = inAndOut[1].split(",");
            if (in.length != 2 || out.length != 3) {
                continue;
            }
            res.add(new RabbitPair(in[0], in[1], out[0], out[1], out[2]));
        }
        return res;
    }

    public void setEnv(String args, String env) {
        if ((env == null || env.isEmpty()) && (args == null || args.isEmpty())) {
            return;
        }
        if (env != null && !env.isEmpty()) {
            List<RabbitPair> config = parseArgs(env);
            if (config.isEmpty()) {
                LOGGER.info("system env setting seems error! your setting is ", env);
            } else {
                pairs = config;
                return;
            }
        }
        if (args != null && !args.isEmpty()) {
            List<RabbitPair> config = parseArgs(args);
            if (config.isEmpty()) {
                LOGGER.info("args settings seems error! your setting is ", env);
            } else {
                pairs = config;
            }
        }
    }
}
