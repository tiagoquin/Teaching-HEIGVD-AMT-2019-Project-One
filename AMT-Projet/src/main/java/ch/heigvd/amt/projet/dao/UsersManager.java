package ch.heigvd.amt.projet.dao;

import ch.heigvd.amt.projet.model.User;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class UsersManager implements UsersManagerLocal{

    @Resource(lookup = "jdbc/amt-db")
    private DataSource dataSource;

    @Override
    public List<User> findAllUsers() {

        List<User> users = new ArrayList<>();

        try {
            Connection connection = dataSource.getConnection();

            System.out.println("Schema : " + connection.getSchema());

            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM User;");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                int id = rs.getInt("idUser");
                String username = rs.getString("username");
                String fullname = rs.getString("fullname");
                String email = rs.getString("email");
                String password = rs.getString("password");

                users.add(new User(username,fullname,email,password));
            }
            connection.close();
        }catch (SQLException ex){
            Logger.getLogger(UsersManager.class.getName()).log(Level.SEVERE,null,ex);
        }
        return users;
    }

    @Override
    public void createUser(User user) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO User(username,fullname,email,password) VALUES (?, ?, ?, ?);");
            pstmt.setString(1,user.getUsername());
            pstmt.setString(2,user.getFullname());
            pstmt.setString(3,user.getEmail());

            String hashedPassWord = hashPassword(user.getPassword());
            pstmt.setString(4,hashedPassWord);
            pstmt.executeUpdate();

            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(UsersManager.class.getName()).log(Level.SEVERE,null,ex);
        }
    }

    @Override
    public boolean checkPassword(String password, String confirmPassowrd) {
        return password.equals(confirmPassowrd);
    }

    @Override
    public boolean signIn(String username, String password) {

        Boolean check = false;
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("SELECT password FROM User WHERE username = '" + username + "'");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                String hashedPassword = rs.getString("password");
                check = verifyHashedPassword(hashedPassword,password);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public boolean isUsernameFree(String username) {
        Boolean check = true;
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM User WHERE username=?");
            pstmt.setObject(1, username);
            ResultSet rs = pstmt.executeQuery();

            check = !rs.next();


        } catch (SQLException ex) {
            Logger.getLogger(UsersManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return check;
    }

    private String hashPassword(String password) {
        String hashedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }


        return hashedPassword;
    }

    private boolean verifyHashedPassword(String hash, String attempt) {
        String generateHash = hashPassword(attempt);
        return hash.equals(generateHash);
    }


}
