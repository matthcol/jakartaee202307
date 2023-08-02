package org.example;

import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;

class JDBCDemo {

	@Test
    void aTest(){
        // javax.sql.DataSource: interface de JDBC (Java SE)
        // org.mariadb.jdbc.MariaDbDataSource: concrete class implementing JDBC interface
        DataSource dataSource = new MariaDbDataSource();
    }

}