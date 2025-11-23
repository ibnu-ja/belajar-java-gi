package io.ibnuja.hypersonic.model;

public record Song(
        String id,
        String title,
        String artist,
        String uri,
        long duration
) {}
