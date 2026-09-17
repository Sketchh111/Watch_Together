package com.example.watch_together.Repository;

import com.example.watch_together.Model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class UserJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // CREATE
    public int createUser(User user) {

        String sql = """
                INSERT INTO users
                (username, email, password, created_at)
                VALUES (?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                Timestamp.valueOf(user.getCreatedAt())
        );
    }

    // READ ALL
    public List<User> getAllUsers() {

        String sql = """
                SELECT id, username, email, password, created_at
                FROM users
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    User user = new User();

                     user.setId(rs.getLong("id"));

                    user.setUsername(
                            rs.getString("username")
                    );

                    user.setEmail(
                            rs.getString("email")
                    );

                    user.setPassword(
                            rs.getString("password")
                    );

                    user.setCreatedAt(
                            rs.getTimestamp("created_at")
                                    .toLocalDateTime()
                    );

                    return user;
                }
        );
    }

    // READ BY ID
    public User getUserById(Long id) {

        String sql = """
                SELECT id, username, email, password, created_at
                FROM users
                WHERE id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {

                    User user = new User();

                    user.setId(rs.getLong("id"));

                    user.setUsername(
                            rs.getString("username")
                    );

                    user.setEmail(
                            rs.getString("email")
                    );

                    user.setPassword(
                            rs.getString("password")
                    );

                    user.setCreatedAt(
                            rs.getTimestamp("created_at")
                                    .toLocalDateTime()
                    );

                    return user;
                },
                id
        );
    }

    // UPDATE
    public int updateUser(Long id, User user) {

        String sql = """
                UPDATE users
                SET username = ?,
                    email = ?,
                    password = ?
                WHERE id = ?
                """;

        return jdbcTemplate.update(
                sql,
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                id
        );
    }

    // DELETE
    public int deleteUser(Long id) {

        String sql = """
                DELETE FROM users
                WHERE id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }
}