
package com.example.database;

import com.example.database.BaseEntity;

/**
 * SQLCreator
 */
public interface SQLCreator {
    String CreateSql(BaseEntity entity);
}