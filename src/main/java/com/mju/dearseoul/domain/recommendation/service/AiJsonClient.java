package com.mju.dearseoul.domain.recommendation.service;

import java.util.function.UnaryOperator;

public interface AiJsonClient {
    <T> T generateJson(String operation, String instructions, Object input, Class<T> responseType,
                       UnaryOperator<T> validator);
}
