package baunetzwerk.cloud.database;

public class SQLSelectBuilder {

    private final String table;
    private String[] columns;
    private String[] whereColumns;
    private Object[] whereResults;

    private String[] orderBy;
    private boolean isOrderByAsc = false;
    private int limitStart = -1;
    private int limitEnd = -1;
    private String[] groupBy;

    public SQLSelectBuilder(String table) {
        this.table = table;
    }

    public SQLSelectBuilder select(String... columns) {
        this.columns = columns;
        return this;
    }

    public SQLSelectBuilder where(String columns, Object results) {
        this.whereColumns = new String[]{columns};
        this.whereResults = new Object[]{results};
        return this;
    }

    public SQLSelectBuilder where(String[] columns, Object[] results) {
        this.whereColumns = columns;
        this.whereResults = results;
        return this;
    }

    public SQLSelectBuilder groupBy(String... columns) {
        this.groupBy = columns;
        return this;
    }

    public SQLSelectBuilder orderByAsc(String column, String... columns) {
        this.orderBy = new String[1 + columns.length];

        orderBy[0] = column;
        System.arraycopy(columns, 0, orderBy, 1, columns.length);

        this.isOrderByAsc = true;
        return this;
    }

    public SQLSelectBuilder orderByDesc(String column, String... columns) {
        this.orderBy = new String[1 + columns.length];
        orderBy[0] = column;
        System.arraycopy(columns, 0, orderBy, 1, columns.length);
        this.isOrderByAsc = false;
        return this;
    }

    public SQLSelectBuilder limit(int limit) {
        this.limitStart = 0;
        this.limitEnd = limit;
        return this;
    }

    public SQLSelectBuilder limit(int limitStart, int limitEnd) {
        this.limitStart = limitStart;
        this.limitEnd = limitEnd;
        return this;
    }

    public String asSQL() {
        StringBuilder builder = new StringBuilder("SELECT ");
        int size = 0;

        // selected Columns
        if (columns == null) {
            builder.append("* ");
        } else {
            for (int i = 0; i < columns.length; i++) {
                builder.append(columns[i]);
                if (i + 1 < columns.length) {
                    builder.append(", ");
                } else {
                    builder.append(" ");
                }
            }
        }

        // Table
        builder.append("FROM ").
                append(table).
                append(" ");

        // Where
        if (whereColumns != null) {
            builder.append("WHERE ");
            for (int i = 0; i < whereColumns.length; i++) {
                builder.append(whereColumns[i]).append("=?");
                if (i + 1 < whereColumns.length) {
                    builder.append("AND ");
                } else {
                    builder.append(" ");
                }
            }
        }

        // Group By
        if (groupBy != null) {
            builder.append("GROUP BY ");
            for (int i = 0; i < groupBy.length; i++) {
                builder.append(groupBy[i]);
                if (i + 1 < groupBy.length) {
                    builder.append(", ");
                } else {
                    builder.append(" ");
                }
            }
        }

        // Order By
        if (orderBy != null) {
            builder.append("ORDER BY ");

            for (int i = 0; i < orderBy.length; i++) {
                builder.append(orderBy[i]);
                if (i + 1 < orderBy.length) {
                    builder.append(", ");
                } else {
                    builder.append(" ");
                }
            }

            if (isOrderByAsc) {
                builder.append("ASC ");
            } else {
                builder.append("DESC ");
            }
        }

        // Limit
        if (limitStart != -1) {
            builder.append("LIMIT ").append(limitStart).append(", ").append(limitEnd);
        }

        return builder.toString();
    }

    public Object[] getWhereResults() {
        return whereResults;
    }
}
