package com.kanjian.cappy.model;

public class RabbitPair {
    private String inputUri;
    private String inputQueue;
    private String outputUri;
    private String outputExchange;
    private String outputRoutingKey;

    RabbitPair(String inputUri, String inputQueue, String outputUri, String outputExchange, String outputRoutingKey) {
        this.inputUri = inputUri;
        this.inputQueue = inputQueue;
        this.outputUri = outputUri;
        this.outputExchange = outputExchange;
        this.outputRoutingKey = outputRoutingKey;
    }

    public String getInputUri() {
        return inputUri;
    }

    public String getInputQueue() {
        return inputQueue;
    }

    public String getOutputUri() {
        return outputUri;
    }

    public String getOutputExchange() {
        return outputExchange;
    }

    public String getOutputRoutingKey() {
        return outputRoutingKey;
    }
}