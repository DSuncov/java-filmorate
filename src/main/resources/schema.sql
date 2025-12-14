CREATE TABLE IF NOT EXISTS rating (
	id INTEGER,
	name VARCHAR(16) NOT NULL,
	CONSTRAINT rating_PK PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS genre (
	id INTEGER,
	name VARCHAR(32) NOT NULL,
	CONSTRAINT genre_PK PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS directors (
    id BIGINT AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT directors_PK PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS films (
	id BIGINT AUTO_INCREMENT,
	name VARCHAR(32) NOT NULL,
	description VARCHAR(255) NOT NULL,
	releaseDate DATE NOT NULL,
	duration INTEGER NOT NULL,
	rating_id INTEGER,
	FOREIGN KEY (rating_id) REFERENCES rating (id) ON DELETE CASCADE,
	CONSTRAINT films_PK PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS directors_films (
    film_id BIGINT,
    director_id BIGINT,
    FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
	FOREIGN KEY (director_id) REFERENCES directors (id) ON DELETE CASCADE,
	CONSTRAINT directors_films_PK PRIMARY KEY (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS genre_films (
    film_id BIGINT,
    genre_id INTEGER,
    FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
	FOREIGN KEY (genre_id) REFERENCES genre (id) ON DELETE CASCADE,
	CONSTRAINT genre_films_PK PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users (
	id BIGINT AUTO_INCREMENT,
	email VARCHAR(32) NOT NULL,
	login VARCHAR(32) NOT NULL,
	name VARCHAR(32),
	birthday DATE NOT NULL,
	CONSTRAINT users_PK PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS likes_films (
	user_id INTEGER,
	film_id INTEGER,
	FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
	FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
	CONSTRAINT likes_films_PK PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS friendship (
	user_id INTEGER,
	friend_id INTEGER,
	status VARCHAR(16),
	FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
	FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE,
	CONSTRAINT friendship_PK PRIMARY KEY (user_id, friend_id)
);