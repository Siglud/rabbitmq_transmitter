package com.kanjian.cappy;

import com.kanjian.cappy.error.RabbitMQError;
import com.kanjian.cappy.model.RabbitConfig;
import com.kanjian.cappy.model.RabbitPair;
import com.kanjian.cappy.services.Transmitter;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private static Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("h", "help", false, "打印此信息");
        options.addOption("p", "pair", true,
                "来源与目标的对应关系，首先写来源的URI，然后,间隔Queue名，然后|间隔目标URI，" +
                        "用逗号间隔依次写Exchange名和RoutingKey。用分号间隔每个分组，" +
                        "形如：amqp://guest:123@192.168.0.1:4567/vhost,queue|amqp://admin:123@192.168.0.2:4567/vhost,exchange,key");

        String argFromParser = "";
        String envFromParser = "";

        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("h")) {
                HelpFormatter help = new HelpFormatter();
                help.printHelp("options", options);
                return;
            }
            argFromParser = line.getOptionValue("p");
        }
        catch (ParseException exp) {
            LOGGER.info("Unexpected args parse!", exp);
        }
        try {
            envFromParser = System.getenv().get("pair");
        } catch (SecurityException| ClassCastException e) {
            LOGGER.info("Unexpected env parse!", e);
        }
        RabbitConfig config = RabbitConfig.getInstance();
        config.setEnv(argFromParser, envFromParser);

        for (RabbitPair pair : config.getPairs()) {
            try {
                Transmitter transmitter = new Transmitter(pair);
                transmitter.start();
            } catch (RabbitMQError rabbitMQError) {
                LOGGER.error("RabbitMQ Config Error!", rabbitMQError);
                return;
            }
        }
    }
}
