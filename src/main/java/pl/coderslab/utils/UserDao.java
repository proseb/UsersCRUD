package pl.coderslab.utils;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Arrays;

public class UserDao {
    private static final String CREATE_USER_QUERY =
            "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";

    private static final String READ_USER_QUERY =
            "SELECT * FROM users WHERE id = ?";

    private static final String UPDATE_USER_QUERY =
            "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";

    private static final String DELETE_USER_QUERY =
            "DELETE FROM users WHERE id = ?";

    private static final String FIND_ALL_USERS_QUERY =
            "SELECT * FROM users";

    public User create(User user) throws SQLException {
        Connection conn = DBUtil.getConnection();
        try {
            PreparedStatement prpStatement = conn.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            prpStatement.setString(1, user.getUserName());
            prpStatement.setString(2, user.getEmail());
            prpStatement.setString(3, hashPassword(user.getPassword()));
            prpStatement.executeUpdate();
            ResultSet resultSet = prpStatement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User read(int user_id) throws SQLException {
        Connection conn = DBUtil.getConnection();
        try {
            PreparedStatement prpStatement = conn.prepareStatement(READ_USER_QUERY);
            prpStatement.setInt(1, user_id);
            ResultSet resultSet = prpStatement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUserName(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(User user) throws SQLException {
        Connection conn = DBUtil.getConnection();
        try {
            PreparedStatement prpStatement = conn.prepareStatement(UPDATE_USER_QUERY);
            prpStatement.setString(1, user.getUserName());
            prpStatement.setString(2, user.getEmail());
            prpStatement.setString(3, this.hashPassword(user.getPassword()));
            prpStatement.setInt(4, user.getId());
            prpStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User[] findAll() throws SQLException {
        Connection conn = DBUtil.getConnection();
        User[] users = new User[0];
        try {
            PreparedStatement prpStatement = conn.prepareStatement(FIND_ALL_USERS_QUERY);
            ResultSet resultSet = prpStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUserName(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                users = Arrays.copyOf(users, users.length + 1);
                users[users.length - 1] = user;
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void delete(int userId) throws SQLException {
        Connection conn = DBUtil.getConnection();
        try {
            PreparedStatement prpStatement = conn.prepareStatement(DELETE_USER_QUERY);
            prpStatement.setInt(1, userId);
            prpStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}

