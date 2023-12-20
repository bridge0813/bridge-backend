package com.Bridge.bridge.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Stack {

    SWIFT("Swift"), OBJECTIVE_C("Objective-c"), UIKIT("UIKit"), SWIFTUI("SwiftUI"), RXSWIFT("RxSwift"),
    COMBINE("Combine"), XCTEST("XCTest"), TUIST("Tuist"), REACTNATIVE("Reactive Native"), FLUTTER("Flutter"),
    KOTLIN("Kotlin"), JAVA("Java"), COMPOSE("Compose"), RXJAVA("RXJava"), COROUTINE("Coroutine"),
    JAVASCRIPT("JavaScript"), TYPESCRIPT("TypeScript"), HTML("HTML"), CSS("CSS"), REACT("React"),
    VUE("Vue"), ANGULAR("Angular"), SVELTE("Svelte"), JQUERY("JQuery"), BACKBONE("Backbone"), PINIA("Pinia"),
    PYTHON("python"), C("C"), CPP("C++"), SPRING("Spring"), SPRINGBOOT("SpringBoot"),
    NODEJS("NodeJS"), DJANGO("Django"), HIBERNATE("Hibernate"), WEBRTC("WebRTC"), MONGODB("MongoDB"),
    MYSQL("MySQL"), POSTGRESQL("PostgreSQL"), REDIS("Redis"), MARIADB("MariaDB"), PHOTOSHOP("Photoshop"),
    ILLUSTRATOR("Illustrator"), INDESIGN("Indesign"), ADOBEXD("AdobeXD"), FIGMA("Figma"), SKETCH("Sketch"),
    ADOBEFLASH("AdobeFlash"), NOTION("Notion"), JIRA("Jira"), SLACK("Slack");

    private final String value;

}
