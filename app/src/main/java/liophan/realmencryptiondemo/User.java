package liophan.realmencryptiondemo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Copyright (c) 2017, Stacck Pte Ltd. All rights reserved.
 *
 * @author Lio <lphan@stacck.com>
 * @version 1.0
 * @since April 09, 2017
 */

public class User extends RealmObject {
    @PrimaryKey
    private int userId;

    private String name;

    public User() {
    }

    public User(int userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
