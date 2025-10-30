package moe.akari.learning.personal_finance_tracker.repository;

import moe.akari.learning.personal_finance_tracker.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 用户数据访问层 - 使用JdbcTemplate实现
 */
@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper = new UserRowMapper();

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名（主键）
     * @return Optional包装的用户对象
     */
    public Optional<User> findById(String username) {
        //noinspection SqlResolve
        String sql = "SELECT username, name, password, balance FROM user WHERE username = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, username);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    public boolean existsById(String username) {
        String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    /**
     * 保存用户（新增或更新）
     *
     * @param user 用户对象
     * @return 保存的用户对象
     */
    @SuppressWarnings({"UnusedReturnValue", "SqlInsertValues", "SqlResolve"})
    public User save(User user) {
        if (existsById(user.getUsername())) {
            // 更新
            //noinspection SqlResolve
            String sql = "UPDATE user SET name = ?, password = ?, balance = ? WHERE username = ?";
            jdbcTemplate.update(sql, user.getName(), user.getPassword(), user.getBalance(), user.getUsername());
        } else {
            // 插入
            //noinspection SqlInsertValues,SqlResolve
            String sql = "INSERT INTO user (username, name, password, balance) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, user.getUsername(), user.getName(), user.getPassword(), user.getBalance());
        }
        return user;
    }

    /**
     * 删除用户
     *
     * @param user 用户对象
     */
    public void delete(User user) {
        String sql = "DELETE FROM user WHERE username = ?";
        jdbcTemplate.update(sql, user.getUsername());
    }

    /**
     * RowMapper实现 - 将ResultSet映射为User对象
     */
    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUsername(rs.getString("username"));
            //noinspection SqlResolve
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setBalance(rs.getBigDecimal("balance"));
            return user;
        }
    }
}

