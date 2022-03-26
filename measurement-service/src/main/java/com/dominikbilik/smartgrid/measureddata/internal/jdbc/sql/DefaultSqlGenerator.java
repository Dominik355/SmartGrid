package com.dominikbilik.smartgrid.measureddata.internal.jdbc.sql;

import com.dominikbilik.smartgrid.measureddata.internal.jdbc.TableDescription;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.Assert;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.dominikbilik.smartgrid.measureddata.internal.StringUtils.repeat;
import static java.lang.String.format;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

public class DefaultSqlGenerator implements SqlGenerator {

    static final String
            AND = " AND ",
            OR = " OR ",
            COMMA = ", ",
            PARAM = " = ?";

    @Override
    public boolean isCompatible(DatabaseMetaData metadata) throws SQLException {
        return true;
    }

    @Override
    public String count(TableDescription table) {
        return format("SELECT count(*) FROM %s", table.getFromClause());
    }

    @Override
    public String deleteAll(TableDescription table) {
        return format("DELETE FROM %s", table.getFromClause());
    }

    @Override
    public String deleteById(TableDescription table) {
        return deleteByIds(table, 1);
    }

    @Override
    public String deleteByIds(TableDescription table, int idsCount) {
        return deleteAll(table) + " WHERE " + idsPredicate(table, idsCount);
    }

    @Override
    public String existsById(TableDescription table) {
        return format("SELECT 1 FROM %s WHERE %s", table.getFromClause(), idPredicate(table));
    }

    @Override
    public String insert(TableDescription table, Map<String, Object> columns) {
        return format("INSERT INTO  %s (%s) VALUES (%s)",
                table.getFromClause(),
                collectionToDelimitedString(columns.keySet(), COMMA),
                repeat("?", COMMA, columns.size()));
    }

    @Override
    public String selectAll(TableDescription table) {
        return format("SELECT %s FROM %s", table.getSelectClause(), table.getFromClause());
    }

    @Override
    public String selectAll(TableDescription table, Pageable page) {
        Sort sort = page.getSortOr(
                Sort.by(table.getPkColumns().stream()
                    .map(it -> new Order(Sort.Direction.ASC, it))
                    .collect(Collectors.toList())));

        return format("SELECT t2__.* FROM ( "
                        + "SELECT row_number() OVER (ORDER BY %s) AS rn__, t1__.* FROM ( %s ) t1__ "
                        + ") t2__ WHERE t2__.rn__ BETWEEN %s AND %s",
                orderByExpression(sort), selectAll(table),
                page.getOffset() + 1, page.getOffset() + page.getPageSize());
    }

    @Override
    public String selectAll(TableDescription table, Sort sort) {
        return selectAll(table) + (sort != null ? orderByClause(sort) : "");
    }

    @Override
    public String selectById(TableDescription table) {
        return selectByIds(table, 1);
    }

    @Override
    public String selectByIds(TableDescription table, int idsCount) {
        return idsCount > 0
                ? selectAll(table) + " WHERE " + idsPredicate(table, idsCount)
                : selectAll(table);
    }

    @Override
    public String update(TableDescription table, Map<String, Object> columns) {
        return format("UPDATE %s SET %s WHERE %s",
                table.getTableName(),
                formatParameters(columns.keySet(), COMMA),
                idPredicate(table));
    }

    protected String orderByClause(Sort sort) {
        return " ORDER BY " + orderByExpression(sort);
    }

    protected String orderByExpression(Sort sort) {
        StringBuilder sb = new StringBuilder();

        for (Iterator<Order> it = sort.iterator(); it.hasNext(); ) {
            Order order = it.next();
            sb.append(order.getProperty()).append(' ').append(order.getDirection());

            if (it.hasNext()) sb.append(COMMA);
        }
        return sb.toString();
    }

    protected Sort sortById(TableDescription table) {
        return Sort.by(Sort.Direction.ASC, table.getPkColumns().toArray(new String[0]));
    }

    private String idsPredicate(TableDescription table, int idsCount) {
        Assert.isTrue(idsCount > 0, "idsCount must be greater than zero");

        List<String> idColumnNames = table.getPkColumns();

        if (idsCount == 1) {
            return idPredicate(table);

        } else if (idColumnNames.size() > 1) {
            return repeat("(" + idPredicate(table) + ")", OR, idsCount);

        } else {
            return idColumnNames.get(0) + " IN (" + repeat("?", COMMA, idsCount) + ")";
        }
    }

    private String idPredicate(TableDescription table) {
        return formatParameters(table.getPkColumns(), AND);
    }

    private String formatParameters(Collection<String> columns, String delimiter) {
        return collectionToDelimitedString(columns, delimiter, "", PARAM);
    }

}
