package users.vo;

import users.domain.Level;

public class User {
	String id;
	String email;
	String name;
	String password;
	Level level;
	int login;
	int recommend;

	public User () {}

	public User (String id, String name, String password) {
		this.id = id;
		this.name = name;
		this.password = password;
	}

	public User (String id, String email, String name, String password,
				 Level level, int login, int recommend) {
	    this.id = id;
	    this.email = email;
	    this.name = name;
	    this.password = password;
	    this.level = level;
	    this.login = login;
	    this.recommend = recommend;
    }
	
	public String getId() {
		return this.id;
	}

	public String getEmail() { return email; }

	public String getName() {
		return this.name;
	}
	
	public String getPassword() {
		return this.password;
	}

	public int getLogin() { return login; }

	public int getRecommend() { return recommend; }

	public Level getLevel() { return level; }

	public void setId(String id) {
		this.id = id;
	}

	public void setEmail(String email) { this.email = email; }

	public void setName(String name) {
		this.name = name;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

    public void setLogin(int login) { this.login = login; }

    public void setLevel(Level level) { this.level = level; }

    public void setRecommend(int recommend) { this.recommend = recommend; }

    public void upgradeLevel() {
		Level nextLevel = this.level.nextLevel();

		if (nextLevel == null) {
			throw new IllegalStateException(this.level + "은 업그레이드가 불가능합니다.");
		} else {
			this.level = nextLevel;
		}
	}
}
