package mesos.am30.db;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

import java.util.*;

/**
 * DB Connection handler.
 * <br>This Class handles the connection to the Database, in this Project case mySQL.
 *
 * @author LoreDN - Lorenzo Di Napoli
 * @version 1.0
 * @since 1.0
 */
class DBConnection {
    private static boolean initialized = false;
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    /**
     * Get the DB connection.
     * <br>This static method works as a getter for the DB connection (used by JDBC).
     * <br>If the DB connection has not been initialized yet, it does so; then it returns the Driver connection.
     *
     * @return DB Driver connection
     * @throws IOException The .env file cannot be read correctly
     * @throws SQLException The DB connection cannot be established correctly
     */
    public static synchronized Connection getConnection() throws IOException, SQLException {
        if (!initialized) {
            DBConnection.getFromEnv();
            initialized = true;
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // set the DB from the .env file
    private static  synchronized void getFromEnv() throws IOException {
        Properties env = new Properties();
        env.load(new FileReader("db/.env"));
        URL = "jdbc:mysql://localhost:3306/" + env.getProperty("MYSQL_DATABASE");
        USER = env.getProperty("MYSQL_USER");
        PASSWORD = env.getProperty("MYSQL_PASSWORD");
    }

    // Test configuration
    static void configureForTesting(String url, String user, String password) {
        URL = url;
        USER = user;
        PASSWORD = password;
        initialized = true;
    }

    // Test setter for the attribute initialized
    public static void setInitialized(boolean initialized) {
        DBConnection.initialized = initialized;
    }

    // Test getter for the attribute URL
    static String getURL() {
        return URL;
    }

    // Test getter for the attribute USER
    static String getUSER() {
        return USER;
    }

    // Test getter for the attribute PASSWORD
    static String getPASSWORD() {
        return PASSWORD;
    }

    /**
     * Get the Results from a SQL Query.
     * <br>This method executes a Query, get its Results, and then converts them as easy to read data in Java.
     *
     * @param query Query (parametric) to be executed.
     * @return Results of the Query (maybe null).
     * @throws SQLException The DB connection cannot be established.
     */
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

    /**
     * Update the Schema.
     * <br>This method executes a DML Query and updates the Schema.
     *
     * @param query Query (parametric) to be executed.
     * @throws SQLException The DB connection cannot be established.
     */
    public static synchronized void updateSchema(PreparedStatement query, Object... parameters) throws SQLException {
        DBConnection.setQuery(query, parameters);
        query.executeUpdate();
    }

    // Set a parametric Query
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