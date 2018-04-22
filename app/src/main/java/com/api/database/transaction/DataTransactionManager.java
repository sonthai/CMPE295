package com.api.database.transaction;


public abstract class DataTransactionManager<S,T> implements ITransaction<S,T> {
    @Override
    public void save(S data) {}

    @Override
    public void delete(S data) {}

    @Override
    public T convert(S rawData) throws Exception {
        return null;
    }
}
