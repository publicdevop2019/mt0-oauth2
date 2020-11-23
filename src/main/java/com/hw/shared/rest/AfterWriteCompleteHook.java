package com.hw.shared.rest;

import java.util.Set;

public interface AfterWriteCompleteHook<T> {
    default void afterWriteComplete(){

    }
    default void afterCreateComplete(T execute) {

    }
    default void afterReplaceByIdComplete(Long id) {

    }
    default void afterPatchByIdComplete(Long id) {

    }
    default void afterPatchBatchComplete() {

    }
    default void afterDeleteComplete(Set<Long> id) {

    }
}
