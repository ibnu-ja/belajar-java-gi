package io.ibnuja.hypersonic.model;

import io.ibnuja.hypersonic.state.App;
import lombok.Getter;

import java.util.List;

public class AppModel {

    @Getter
    private final App appState;

    public AppModel() {
        this.appState = new App();
    }

    public List<App.Event> update(App.Action action) {
        return appState.update(action);
    }
}
