package com.kanjian.cappy.services;

import com.kanjian.cappy.error.RabbitMQError;
import com.kanjian.cappy.model.RabbitPair;
import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.DefaultExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class Transmitter {
    private static Logger LOGGER = LoggerFactory.getLogger(Transmitter.class.getName());

    private static int PUBLISH_CONFIRM_TIME_OUT = 100000;

    private RabbitPair config;
    private Channel channel;
    private Channel publishChannel;

    public Transmitter(RabbitPair config) throws RabbitMQError {
        this.config = config;
        ConnectionFactory connectionFactory = new ConnectionFactory();
        ConnectionFactory publishConnectionFactory = new ConnectionFactory();
        connectionFactory.setNetworkRecoveryInterval(15000);
        connectionFactory.setAutomaticRecoveryEnabled(true);
        // connectionFactory.setTopologyRecoveryEnabled(true);
        connectionFactory.setConnectionTimeout(15000);
        connectionFactory.setRequestedHeartbeat(15);
        publishConnectionFactory.setRequestedHeartbeat(15);
        publishConnectionFactory.setNetworkRecoveryInterval(15000);
        publishConnectionFactory.setAutomaticRecoveryEnabled(true);
        // publishConnectionFactory.setTopologyRecoveryEnabled(true);
        publishConnectionFactory.setConnectionTimeout(15000);
        try {
            connectionFactory.setUri(config.getInputUri());
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            LOGGER.error("consumer uri setting error! uri is ", config.getInputUri(), e);
            throw new RabbitMQError("uri error ");
        }
        try {
            publishConnectionFactory.setUri(config.getOutputUri());
        } catch (NoSuchAlgorithmException | KeyManagementException | URISyntaxException e) {
            LOGGER.error("publisher uri setting error! uri is ", config.getOutputUri(), e);
            throw new RabbitMQError("uri error ");
        }
        while (true) {
            try {
                Connection conn = connectionFactory.newConnection();
                channel = conn.createChannel();
                channel.basicQos(1);
                break;
            } catch (TimeoutException | IOException e) {
                LOGGER.error("connect to rabbitMQ encounter error!", e);
            }
        }
        while (true) {
            try {
                Connection publishConn = publishConnectionFactory.newConnection();
                publishChannel = publishConn.createChannel();
                publishChannel.basicQos(1);
                publishChannel.confirmSelect();
                break;
            } catch (TimeoutException | IOException e) {
                LOGGER.error("connection to rabbitMQ encounter error!", e);
            }
        }

        connectionFactory.setRecoveryDelayHandler(recoveryAttempts -> Integer.MAX_VALUE);

    }

    public void start() {
        LOGGER.debug("Star transmitter");
        try {
            channel.basicConsume(config.getInputQueue(), false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body) {
                    try {
                        while (true) {
                            try {
                                publishChannel.basicPublish(config.getOutputExchange(), config.getOutputRoutingKey(), properties, body);
                                publishChannel.waitForConfirms(PUBLISH_CONFIRM_TIME_OUT);
                                LOGGER.info("deliver complete");
                                break;
                            } catch (IOException | InterruptedException | TimeoutException e) {
                                LOGGER.error("publish message encounter error!", e);
                            }
                        }
                        while (true) {
                            try {
                                channel.basicAck(envelope.getDeliveryTag(), false);
                                break;
                            } catch (IOException e) {
                                LOGGER.error("ACK message encounter error!", e);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("something error!", e);
                        try {
                            channel.basicNack(envelope.getDeliveryTag(), false, false);
                        } catch (IOException e1) {
                            LOGGER.error("nack fail!", e);
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
