package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.text.MessageFormat;
import java.util.List;

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
                // TODO: var movie = Movie.builder()
                //      .id(id)
                //      .title(title)
                //      .year(year)
                //      .build();
                //  + add to a collection result
                System.out.println(MessageFormat.format("{0} # {1} ({2})",
                        id, title, year));
            }
        } // Auto: resultSet.close(); statement.close(); connection.close()
    }

    @Test
    void demoReadWithParameter() throws SQLException {
        String query = "select id, title, year from movies where year = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            var years = List.of((short) 1982, (short) 1984, (short) 1992);
            for (short yearQuery : years) {
                System.out.println(MessageFormat.format("*** Movies of year: {0} ***", yearQuery));
                statement.setShort(1, yearQuery);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String title = resultSet.getString("title");
                        short year = resultSet.getShort("year");
                        // TODO: var movie = Movie.builder()
                        //      .id(id)
                        //      .title(title)
                        //      .year(year)
                        //      .build();
                        //  + add to a collection result
                        System.out.println(MessageFormat.format("{0} # {1} ({2})",
                                id, title, year));
                    }
                } // // Auto: resultSet.close();
                System.out.println();
            }
        } // Auto: statement.close(); connection.close()
    }

}