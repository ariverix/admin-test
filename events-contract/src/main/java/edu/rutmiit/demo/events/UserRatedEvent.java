package edu.rutmiit.demo.events;

import java.io.Serializable;

public record UserRatedEvent(Long userId, Integer score, String verdict) implements Serializable {}
