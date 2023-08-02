package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

class JDBCDemo {

    // javax.sql.DataSource: interface de JDBC (Java SE)
    static DataSource dataSource;

    @BeforeAll
    static void initDataSource() throws SQLException {
        // settings
        String url = "jdbc:mariadb://localhost:3306/dbmovie";
        String user = "movie";
        String password = "password";
        // org.mariadb.jdbc.MariaDbDataSource: concrete class implementing JDBC interface
        var localDataSource = new MariaDbDataSource();
        localDataSource.setUrl(url);
        localDataSource.setUser(user);
        localDataSource.setPassword(password);
        dataSource = localDataSource;
    }

    @Test
    void demoRead() throws SQLException {
        String query = "select id, title, year from movies where year = 1984";
        try (
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)
        ) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                short year = resultSet.getShort("year");
                System.out.println(MessageFormat.format("{0} # {1} ({2})",
                        id, title, year));
            }
        } // Auto: resultSet.close(); statement.close(); connection.close()

    }

}