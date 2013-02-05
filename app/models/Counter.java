package models;

import play.db.jpa.Model;

import javax.persistence.Entity;


/**
 * @author wujinliang
 * @since 2/5/13
 */
@Entity
public class Counter extends Model {
    public String name;
    public long count;
}
