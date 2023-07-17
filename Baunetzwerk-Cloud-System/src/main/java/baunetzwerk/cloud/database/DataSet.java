package baunetzwerk.cloud.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DataSet implements AutoCloseable {

    private final ResultSet resultSet;

    public DataSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    // Close

    @Override
    public void close() throws SQLException {
        resultSet.close();
    }


    // next

    public boolean next() throws SQLException {
        return resultSet.next();
    }

    // getter

    public long getLong(String column) throws SQLException {
        return resultSet.getLong(column);
    }

    public int getInt(String column) throws SQLException {
        return resultSet.getInt(column);
    }

    public short getShort(String column) throws SQLException {
        return resultSet.getShort(column);
    }

    public byte getByte(String column) throws SQLException {
        return resultSet.getByte(column);
    }

    public double getDouble(String column) throws SQLException {
        return resultSet.getDouble(column);
    }

    public float getFloat(String column) throws SQLException {
        return resultSet.getFloat(column);
    }

    public String getString(String column) throws SQLException {
        return resultSet.getString(column);
    }

    public boolean getBoolean(String column) throws SQLException {
        return resultSet.getBoolean(column);
    }

    public UUID getUUID(String column) throws SQLException {
        return UUID.fromString(resultSet.getString(column));
    }

    public <T extends Enum<T>> T getEnum(String column, Class<T> enumClazz) throws SQLException {
        return (T) Enum.valueOf(enumClazz, resultSet.getString(column));
    }

}
