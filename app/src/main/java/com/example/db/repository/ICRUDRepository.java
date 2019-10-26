package com.example.db.repository;

import com.google.android.gms.tasks.Task;

public interface ICRUDRepository<TEntity, TKey> {
    TEntity get(TKey id);
    boolean exist(TKey id);
    void create(TEntity entity);
    void update(TEntity entity);
}
