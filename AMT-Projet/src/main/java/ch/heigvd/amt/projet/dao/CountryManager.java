package ch.heigvd.amt.projet.dao;

import ch.heigvd.amt.projet.model.Country;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CountryManager implements CountryManagerLocal {

    @Resource(lookup = "jdbc/amt-db")
    private DataSource dataSource;

    @Override
    public List<Country> findAllCountries() {
        List<Country> users = new ArrayList<>();

        try {
            Connection connection = dataSource.getConnection();

            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM Country;");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                String name = rs.getString("name");
                users.add(Country.builder().name(name).build());
            }
            connection.close();
        }catch (SQLException ex){
            Logger.getLogger(UsersManager.class.getName()).log(Level.SEVERE,null,ex);
        }
        return users;
    }
}