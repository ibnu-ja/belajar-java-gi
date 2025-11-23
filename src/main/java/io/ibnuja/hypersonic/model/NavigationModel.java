package io.ibnuja.hypersonic.model;

import io.ibnuja.hypersonic.navigation.Route;
import io.ibnuja.hypersonic.state.App;
import io.ibnuja.hypersonic.trait.Dispatcher;

public class NavigationModel {
    private final App app;
    private final Dispatcher<App.Action> dispatcher;

    public NavigationModel(App app, Dispatcher<App.Action> dispatcher) {
        this.app = app;
        this.dispatcher = dispatcher;
    }

    public void push(Route route) {
        dispatcher.dispatch(new App.Action.Navigate(route));
    }

    public void pop() {
        dispatcher.dispatch(new App.Action.NavigateBack());
    }

    public void resetTo(Route route) {
        dispatcher.dispatch(new App.Action.ResetNavigation(route));
    }

    public Route getCurrentRoute() {
        return app.getCurrentRoute();
    }
}
