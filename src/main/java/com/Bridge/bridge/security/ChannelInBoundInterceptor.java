package com.Bridge.bridge.security;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class ChannelInBoundInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor header = StompHeaderAccessor.wrap(message);
        System.out.println("command : "+header.getCommand());
        System.out.println("destination : "+header.getDestination());
        System.out.println("name header : "+header.getFirstNativeHeader("name"));
        return message;
    }
}
