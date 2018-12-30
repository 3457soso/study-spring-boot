package _etc.factory;

import _etc.factory.ConnectionMaker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DConnectionMaker implements ConnectionMaker {
    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");

        Connection c = DriverManager.getConnection(
                "Jdbc:mysql://localhost/toby?autoReconnect=true&useSSL=false", "study", "1111");

        return c;
    }
}
