package models;

import models.enums.Role;
import play.db.jpa.Model;

import javax.persistence.Entity;
import java.io.Serializable;

@Entity
public class User extends Model implements Serializable {
	public String name;
	public String password;
	public Role role = Role.USER;
}
