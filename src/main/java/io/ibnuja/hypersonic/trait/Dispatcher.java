package io.ibnuja.hypersonic.trait;

@FunctionalInterface
public interface Dispatcher<A> {
    void dispatch(A action);
}
