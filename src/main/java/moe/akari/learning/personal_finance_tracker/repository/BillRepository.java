package moe.akari.learning.personal_finance_tracker.repository;

import moe.akari.learning.personal_finance_tracker.entity.Bill;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 账单数据访问层 - 使用JdbcTemplate实现
 */
@Repository
public class BillRepository {

    private final JdbcTemplate jdbcTemplate;
    private final BillRowMapper billRowMapper = new BillRowMapper();

    public BillRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据用户名查询所有账单，按时间倒序排列
     *
     * @param userUsername 用户名
     * @return 账单列表
     */
    public List<Bill> findByUserUsernameOrderByTimeDesc(String userUsername) {
        String sql = "SELECT uuid, amount, type, method, counterparty, time, category, note, user_username " +
                     "FROM bill WHERE user_username = ? ORDER BY time DESC";
        return jdbcTemplate.query(sql, billRowMapper, userUsername);
    }

    /**
     * 根据用户名和账单类型查询账单，按时间倒序排列
     *
     * @param userUsername 用户名
     * @param type 账单类型
     * @return 账单列表
     */
    public List<Bill> findByUserUsernameAndTypeOrderByTimeDesc(String userUsername, String type) {
        String sql = "SELECT uuid, amount, type, method, counterparty, time, category, note, user_username " +
                     "FROM bill WHERE user_username = ? AND type = ? ORDER BY time DESC";
        return jdbcTemplate.query(sql, billRowMapper, userUsername, type);
    }

    /**
     * 根据用户名、类型和时间范围查询账单
     *
     * @param userUsername 用户名
     * @param type 账单类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 账单列表
     */
    public List<Bill> findByUserUsernameAndTypeAndTimeBetween(String userUsername, String type,
                                                               LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT uuid, amount, type, method, counterparty, time, category, note, user_username " +
                     "FROM bill WHERE user_username = ? AND type = ? AND time >= ? AND time < ?";
        return jdbcTemplate.query(sql, billRowMapper, userUsername, type, startTime, endTime);
    }

    /**
     * 删除指定用户的所有账单
     *
     * @param userUsername 用户名
     */
    public void deleteByUserUsername(String userUsername) {
        String sql = "DELETE FROM bill WHERE user_username = ?";
        jdbcTemplate.update(sql, userUsername);
    }

    /**
     * RowMapper实现 - 将ResultSet映射为Bill对象
     */
    private static class BillRowMapper implements RowMapper<Bill> {
        @Override
        public Bill mapRow(ResultSet rs, int rowNum) throws SQLException {
            Bill bill = new Bill();
            bill.setUuid(rs.getString("uuid"));
            bill.setAmount(rs.getBigDecimal("amount"));
            bill.setType(rs.getString("type"));
            bill.setMethod(rs.getString("method"));
            bill.setCounterparty(rs.getString("counterparty"));
            bill.setTime(rs.getTimestamp("time").toLocalDateTime());
            bill.setCategory(rs.getString("category"));
            bill.setNote(rs.getString("note"));
            bill.setUserUsername(rs.getString("user_username"));
            return bill;
        }
    }
}

