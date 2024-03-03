package com.example.database.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GameList extends ArrayList<BaseEntity.GameModel> {
    public GameList() {
        super();
    }

    public GameList(Collection<? extends BaseEntity> lst) {
        super(lst.stream().map(item -> (BaseEntity.GameModel) item).collect(Collectors.toList()));
    }

    public List<BaseEntity.GameModel> orderByGameID() {
        Comparator<? super BaseEntity.GameModel> comp = null;
        if (this.size() == 0) {
            return null;
        } else {
            comp = new Comparator<BaseEntity.GameModel>() {
                @Override
                public int compare(BaseEntity.GameModel game1, BaseEntity.GameModel game2) {
                    return game1.getId() - game2.getId();
                }
            };
        }
        this.sort(comp);
        return this;
    }

    public List<BaseEntity.GameModel> orderByBoardSize() {
        Comparator<? super BaseEntity.GameModel> comp = null;
        if (this.size() == 0) {
            return null;
        } else {
            comp = new Comparator<BaseEntity.GameModel>() {
                @Override
                public int compare(BaseEntity.GameModel game1, BaseEntity.GameModel game2) {
                    return game1.getBoardSize() - game2.getBoardSize();
                }
            };
        }
        this.sort(comp);
        return this;
    }
}
