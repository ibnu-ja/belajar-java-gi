package io.ibnuja.hypersonic.model;

import io.ibnuja.hypersonic.state.App;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AppModel {

    @Getter
    private final App appState;
    private final List<Consumer<App.Event>> listeners = new ArrayList<>();

    public AppModel() {
        this.appState = new App();
    }

    // Dispatch from anywhere, notify MainWindow automatically
    public void dispatch(App.Action action) {
        List<App.Event> events = appState.update(action);
        for (App.Event event : events) {
            notifyListeners(event);
        }
    }

    public void addListener(Consumer<App.Event> listener) {
        listeners.add(listener);
    }

    private void notifyListeners(App.Event event) {
        for (Consumer<App.Event> listener : listeners) {
            listener.accept(event);
        }
    }
}
