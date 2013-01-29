package models;

import com.google.code.morphia.annotations.Entity;
import models.enums.Role;
import play.modules.morphia.Model;

import java.io.Serializable;

@Entity
public class User extends Model implements Serializable {
	public String name;
	public String password;
	public Role role = Role.USER;
}
