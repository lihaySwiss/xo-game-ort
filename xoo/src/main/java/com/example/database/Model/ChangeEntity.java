package com.example.database.Model;

import com.example.database.Model.BaseEntity;
import com.example.database.viewModel.SQLCreator;

public class ChangeEntity {
    private SQLCreator sqlCreator;
    private BaseEntity entity;

    public ChangeEntity(BaseEntity entity, SQLCreator sqlCreator) {
        super();
        this.entity = entity;
        this.sqlCreator = sqlCreator;
    }

    public SQLCreator getSqlCreator() {
        return sqlCreator;
    }

    public void setSqlCreator(SQLCreator sqlCreator) {
        this.sqlCreator = sqlCreator;
    }

    public BaseEntity getEntity() {
        return entity;
    }

    public void setEntity(BaseEntity entity) {
        this.entity = entity;
    }

}
