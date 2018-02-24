package com.api.database.transaction;

import org.springframework.lang.NonNull;

public interface ITransaction<S, T> {
    public void save(@NonNull S data);
    public void delete(S data);
    public T convert(@NonNull S rawData) throws Exception;
}
