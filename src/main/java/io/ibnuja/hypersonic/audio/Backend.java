package io.ibnuja.hypersonic.audio;

public interface Backend {

    void setUri(String uri);

    void play();

    void pause();

    void stop();

    long queryPosition();

    long queryDuration();
}
