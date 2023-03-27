CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL
);
CREATE TABLE books (
                       id SERIAL PRIMARY KEY,
                       title VARCHAR(100) NOT NULL,
                       author VARCHAR(50) NOT NULL,
                       user_id INTEGER REFERENCES users(id)
);
CREATE TABLE libraries (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(50) NOT NULL
);
CREATE TABLE user_library (
                              user_id INTEGER REFERENCES users(id),
                              library_id INTEGER REFERENCES libraries(id),
                              PRIMARY KEY (user_id, library_id)
);