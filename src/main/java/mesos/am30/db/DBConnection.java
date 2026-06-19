package mesos.am30.db;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

import java.util.*;

// DB Connection handler.
class DBConnection {
    private static boolean initialized = false;
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    // static getter for the DB connection.
    public static synchronized Connection getConnection() throws IOException, SQLException {
        if (!initialized) {
            DBConnection.getFromEnv();
            initialized = true;
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // set the DB from the .env file
    private static synchronized void getFromEnv() throws IOException {
        Properties env = new Properties();
        env.load(new FileReader("db/.env"));
        URL = "jdbc:mysql://localhost:3306/" + env.getProperty("MYSQL_DATABASE");
        USER = env.getProperty("MYSQL_USER");
        PASSWORD = env.getProperty("MYSQL_PASSWORD");
    }

    // test configuration
    static void configureForTesting(String url, String user, String password) {
        URL = url;
        USER = user;
        PASSWORD = password;
        initialized = true;
    }

    // test setter for the attribute initialized
    public static void setInitialized(boolean initialized) {
        DBConnection.initialized = initialized;
    }

    // test getter for the attribute URL
    static String getURL() {
        return URL;
    }

    // test getter for the attribute USER
    static String getUSER() {
        return USER;
    }

    // test getter for the attribute PASSWORD
    static String getPASSWORD() {
        return PASSWORD;
    }

    // get the Results from a SQL Query
    public static synchronized List<Map<String, String>> getResults(PreparedStatement query, Object... parameters) throws SQLException {
        DBConnection.setQuery(query, parameters);
        try (ResultSet result = query.executeQuery()) {
            ResultSetMetaData meta = result.getMetaData();
            int columnCount = meta.getColumnCount();

            // Convert the rows
            List<Map<String, String>> rows = new ArrayList<>();
            while (result.next()) {
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnLabel(i), result.getString(i));
                }
                rows.add(row);
            }
            return rows;
        }
    }

    // update the Schema
    public static synchronized void updateSchema(PreparedStatement query, Object... parameters) throws SQLException {
        DBConnection.setQuery(query, parameters);
        query.executeUpdate();
    }

    // set a parametric Query
    private static synchronized void setQuery(PreparedStatement statement, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            switch (parameters[i]) {
                case Integer p  -> statement.setInt(i + 1, p);
                case String p   -> statement.setString(i + 1, p);
                case Double p   -> statement.setDouble(i + 1, p);
                case null       -> statement.setNull(i + 1, Types.NULL);
                default         -> statement.setObject(i + 1, parameters[i]);
            }
        }
    }
}