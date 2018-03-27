package com.api.database.transaction;

import org.springframework.lang.NonNull;

public interface ITransaction<S, T> {
    void save(@NonNull S data);
    void delete(S data);
    T convert(@NonNull S rawData) throws Exception;
}
