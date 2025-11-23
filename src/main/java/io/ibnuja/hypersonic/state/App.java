package io.ibnuja.hypersonic.state;

import io.ibnuja.hypersonic.navigation.Route;
import io.ibnuja.hypersonic.trait.UpdatableState;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

@SuppressWarnings("LombokGetterMayBeUsed")
public class App implements UpdatableState<App.Action, App.Event> {

    public sealed interface Action {
        record Navigate(Route route) implements Action {}

        record NavigateBack() implements Action {}

        record ResetNavigation(Route root) implements Action {}
    }

    public sealed interface Event {
        record NavigationPushed(Route route) implements Event {}

        record NavigationPopped() implements Event {}

        record NavigationReset(Route root) implements Event {}
    }

    private final Deque<Route> navigationStack = new ArrayDeque<>();

    public App() {
        navigationStack.push(new Route.Home());
    }

    public Deque<Route> getNavigationStack() {
        return navigationStack;
    }

    public Route getCurrentRoute() {
        return navigationStack.peek();
    }

    @Override
    public List<Event> update(Action action) {
        return switch (action) {
            case Action.Navigate(var route) -> {
                navigationStack.push(route);
                yield List.of(new Event.NavigationPushed(route));
            }

            case Action.NavigateBack() -> {
                if (navigationStack.size() > 1) {
                    navigationStack.pop();
                    yield List.of(new Event.NavigationPopped());
                }
                yield Collections.emptyList();
            }

            case Action.ResetNavigation(var root) -> {
                navigationStack.clear();
                navigationStack.push(root);
                yield List.of(new Event.NavigationReset(root));
            }
        };
    }
}
