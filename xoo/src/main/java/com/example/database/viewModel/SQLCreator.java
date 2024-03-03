
package com.example.database.viewModel;

import com.example.database.Model.BaseEntity;

/**
 * SQLCreator
 */
public interface SQLCreator {
    String CreateSql(BaseEntity entity);
}