package io.ibnuja.hypersonic.trait;

import java.util.List;

/**
 * <a href="https://github.com/Diegovsky/riff/blob/dd78fd9700b5f90ab16a448a5502880bfb6255f3/src/app/state/mod.rs#L20">sorse</a>
 * @param <A> action
 * @param <E> event
 */
public interface UpdatableState<A,E> {

    /**
     * <a href="https://github.com/Diegovsky/riff/blob/dd78fd9700b5f90ab16a448a5502880bfb6255f3/src/app/state/mod.rs#L24">sorse</a>
     * @param action what it does
     * @return what it emits
     */
    List<E> update(A action);
}
