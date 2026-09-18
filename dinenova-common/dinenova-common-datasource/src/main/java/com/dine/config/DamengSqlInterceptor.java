package com.dine.config;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
@Component
@ConditionalOnProperty(name = "spring.datasource.driver-class-name", havingValue = "dm.jdbc.driver.DmDriver")
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class DamengSqlInterceptor implements Interceptor {

    private static final Pattern LIMIT_N = Pattern.compile("(?i)\\bLIMIT\\s+(\\d+)\\b");

    /** SQL keywords and common functions that must not be quoted. */
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "SELECT", "FROM", "WHERE", "AND", "OR", "NOT", "IN", "ON", "AS", "LEFT", "RIGHT", "INNER",
            "OUTER", "FULL", "CROSS", "JOIN", "ORDER", "BY", "GROUP", "HAVING", "LIMIT", "OFFSET",
            "UNION", "ALL", "DISTINCT", "INSERT", "INTO", "VALUES", "UPDATE", "SET", "DELETE", "CREATE",
            "TABLE", "INDEX", "VIEW", "DROP", "ALTER", "ADD", "NULL", "IS", "LIKE", "BETWEEN", "EXISTS",
            "CASE", "WHEN", "THEN", "ELSE", "END", "ASC", "DESC", "TRUE", "FALSE", "WITH", "OVER",
            "PARTITION", "ROW", "ROWS", "FETCH", "FIRST", "NEXT", "ONLY", "TOP", "DUAL", "USING",
            "NATURAL", "INTERSECT", "EXCEPT", "MINUS", "RETURNING", "FOR", "OF", "NOWAIT", "WAIT",
            "COUNT", "SUM", "AVG", "MAX", "MIN", "CAST", "CONVERT", "COALESCE", "NVL", "NVL2",
            "IFNULL", "NULLIF", "CONCAT", "CONCAT_WS", "SUBSTRING", "SUBSTR", "TRIM", "LTRIM", "RTRIM",
            "UPPER", "LOWER", "LENGTH", "REPLACE", "INSTR", "ROUND", "FLOOR", "CEIL", "CEILING", "ABS",
            "MOD", "POWER", "SQRT", "NOW", "SYSDATE", "CURRENT_DATE", "CURRENT_TIMESTAMP", "CURRENT_TIME",
            "DATE", "TIME", "TIMESTAMP", "YEAR", "MONTH", "DAY", "HOUR", "MINUTE", "SECOND",
            "TO_CHAR", "TO_DATE", "TO_NUMBER", "TO_TIMESTAMP", "EXTRACT", "DECODE", "GREATEST", "LEAST",
            "OVERLAPS", "SOME", "ANY", "BOTH", "LEADING", "TRAILING", "CHARACTER", "VARYING",
            "DECIMAL", "NUMERIC", "INTEGER", "INT", "BIGINT", "SMALLINT", "FLOAT", "DOUBLE", "REAL",
            "BOOLEAN", "BLOB", "CLOB", "TEXT", "VARCHAR", "VARCHAR2", "CHAR", "NCHAR", "NVARCHAR",
            "PRIMARY", "KEY", "FOREIGN", "REFERENCES", "CONSTRAINT", "UNIQUE", "CHECK", "DEFAULT",
            "SCHEMA", "DATABASE", "USER", "SESSION", "START", "CONNECT", "RECURSIVE", "MATERIALIZED",
            "TOTAL", "ROWNUM", "ROWID", "LEVEL", "PRIOR", "SIBLINGS", "SYS_GUID", "NEXTVAL", "CURRVAL",
            "IF", "WHILE", "LOOP", "BEGIN", "DECLARE", "EXCEPTION", "RETURN", "FUNCTION", "PROCEDURE",
            "PACKAGE", "TRIGGER", "SEQUENCE", "COMMENT", "GRANT", "REVOKE", "COMMIT", "ROLLBACK",
            "SAVEPOINT", "LOCK", "UNLOCK", "EXPLAIN", "ANALYZE", "SHOW", "DESCRIBE", "USE",
            "IGNORE", "DUPLICATE", "INTERVAL",
            "GROUP_CONCAT", "SEPARATOR", "LISTAGG", "WITHIN", "DISTINCTROW",
            "DATE_FORMAT", "STR_TO_DATE", "FROM_UNIXTIME", "UNIX_TIMESTAMP", "FIND_IN_SET",
            "UUID", "UUID_SHORT"
    ));

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(unwrapProxy(statementHandler));
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        if (boundSql == null) {
            return invocation.proceed();
        }
        String sql = boundSql.getSql();
        if (sql == null || sql.isEmpty()) {
            return invocation.proceed();
        }
        String converted = convertSql(sql);
        if (!converted.equals(sql)) {
            metaObject.setValue("delegate.boundSql.sql", converted);
        }
        return invocation.proceed();
    }

    /**
     * Convert MySQL-oriented SQL fragments to Dameng-compatible form, then quote identifiers.
     */
    static String convertSql(String sql) {
        String s = sql.replace('`', '"');
        s = convertGroupConcat(s);
        s = convertLimit(s);
        return quoteIdentifiers(s);
    }

    /** GROUP_CONCAT(expr [ORDER BY ...] SEPARATOR 'x') -> LISTAGG(expr, 'x') [WITHIN GROUP (ORDER BY ...)] */
    static String convertGroupConcat(String sql) {
        StringBuilder out = new StringBuilder(sql.length() + 64);
        int i = 0;
        int n = sql.length();
        while (i < n) {
            char c = sql.charAt(i);
            if (c == '\'' || c == '"') {
                int start = i;
                i = skipQuoted(sql, i, n, c);
                out.append(sql, start, i);
                continue;
            }
            if (matchWordIgnoreCase(sql, i, n, "GROUP_CONCAT")) {
                int nameEnd = i + "GROUP_CONCAT".length();
                int j = nameEnd;
                while (j < n && Character.isWhitespace(sql.charAt(j))) {
                    j++;
                }
                if (j < n && sql.charAt(j) == '(') {
                    int close = findMatchingParen(sql, j);
                    if (close > j) {
                        String inner = sql.substring(j + 1, close);
                        out.append(rewriteGroupConcatArgs(inner));
                        i = close + 1;
                        continue;
                    }
                }
            }
            out.append(c);
            i++;
        }
        return out.toString();
    }

    private static String rewriteGroupConcatArgs(String inner) {
        String expr = inner;
        String orderBy = null;
        String separator = "','";

        int orderPos = findTopLevelKeyword(inner, "ORDER");
        int sepPos = findTopLevelKeyword(inner, "SEPARATOR");

        if (orderPos >= 0 && (sepPos < 0 || orderPos < sepPos)) {
            expr = inner.substring(0, orderPos);
            int byPos = orderPos + "ORDER".length();
            while (byPos < inner.length() && Character.isWhitespace(inner.charAt(byPos))) {
                byPos++;
            }
            if (!matchWordIgnoreCase(inner, byPos, inner.length(), "BY")) {
                // malformed, keep original via LISTAGG of whole inner
                return "LISTAGG(" + inner.trim() + ", ',')";
            }
            byPos += 2;
            int orderEnd = sepPos >= 0 ? sepPos : inner.length();
            orderBy = inner.substring(byPos, orderEnd).trim();
        } else if (sepPos >= 0) {
            expr = inner.substring(0, sepPos);
        }

        if (sepPos >= 0) {
            separator = inner.substring(sepPos + "SEPARATOR".length()).trim();
            if (separator.isEmpty()) {
                separator = "','";
            }
        }

        StringBuilder rewritten = new StringBuilder();
        rewritten.append("LISTAGG(").append(expr.trim()).append(", ").append(separator).append(")");
        if (orderBy != null && !orderBy.isEmpty()) {
            rewritten.append(" WITHIN GROUP (ORDER BY ").append(orderBy).append(")");
        }
        return rewritten.toString();
    }

    /** LIMIT n -> FETCH FIRST n ROWS ONLY (Dameng / SQL standard). */
    static String convertLimit(String sql) {
        Matcher matcher = LIMIT_N.matcher(sql);
        StringBuffer sb = new StringBuffer(sql.length() + 32);
        while (matcher.find()) {
            matcher.appendReplacement(sb, "FETCH FIRST " + matcher.group(1) + " ROWS ONLY");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static int findTopLevelKeyword(String sql, String keyword) {
        int i = 0;
        int n = sql.length();
        int depth = 0;
        while (i < n) {
            char c = sql.charAt(i);
            if (c == '\'' || c == '"') {
                i = skipQuoted(sql, i, n, c);
                continue;
            }
            if (c == '(') {
                depth++;
                i++;
                continue;
            }
            if (c == ')') {
                depth--;
                i++;
                continue;
            }
            if (depth == 0 && matchWordIgnoreCase(sql, i, n, keyword)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private static boolean matchWordIgnoreCase(String sql, int i, int n, String word) {
        int len = word.length();
        if (i + len > n) {
            return false;
        }
        if (i > 0 && isIdentPart(sql.charAt(i - 1))) {
            return false;
        }
        if (i + len < n && isIdentPart(sql.charAt(i + len))) {
            return false;
        }
        return sql.regionMatches(true, i, word, 0, len);
    }

    private static int findMatchingParen(String sql, int openIdx) {
        int depth = 0;
        int n = sql.length();
        for (int i = openIdx; i < n; i++) {
            char c = sql.charAt(i);
            if (c == '\'' || c == '"') {
                i = skipQuoted(sql, i, n, c) - 1;
                continue;
            }
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static int skipQuoted(String sql, int i, int n, char quote) {
        i++;
        while (i < n) {
            char ch = sql.charAt(i);
            i++;
            if (ch == quote) {
                if (i < n && sql.charAt(i) == quote) {
                    i++;
                } else {
                    break;
                }
            } else if (ch == '\\' && i < n) {
                i++;
            }
        }
        return i;
    }

    private static String quoteIdentifiers(String sql) {
        StringBuilder result = new StringBuilder(sql.length() + 64);
        int i = 0;
        int n = sql.length();
        while (i < n) {
            char c = sql.charAt(i);
            if (c == '\'' || c == '"') {
                i = appendQuotedLiteral(sql, i, n, c, result);
            } else if (Character.isDigit(c) || (c == '.' && i + 1 < n && Character.isDigit(sql.charAt(i + 1)))) {
                i = appendNumber(sql, i, n, result);
            } else if (isIdentStart(c)) {
                int start = i;
                i++;
                while (i < n && isIdentPart(sql.charAt(i))) {
                    i++;
                }
                String ident = sql.substring(start, i);
                if (KEYWORDS.contains(ident.toUpperCase(Locale.ROOT))) {
                    result.append(ident);
                } else {
                    result.append('"').append(ident).append('"');
                }
            } else {
                result.append(c);
                i++;
            }
        }
        return result.toString();
    }

    private static int appendNumber(String sql, int i, int n, StringBuilder result) {
        while (i < n && (Character.isDigit(sql.charAt(i)) || sql.charAt(i) == '.')) {
            result.append(sql.charAt(i));
            i++;
        }
        if (i < n && (sql.charAt(i) == 'e' || sql.charAt(i) == 'E')) {
            int j = i + 1;
            if (j < n && (sql.charAt(j) == '+' || sql.charAt(j) == '-')) {
                j++;
            }
            if (j < n && Character.isDigit(sql.charAt(j))) {
                while (i < j) {
                    result.append(sql.charAt(i));
                    i++;
                }
                while (i < n && Character.isDigit(sql.charAt(i))) {
                    result.append(sql.charAt(i));
                    i++;
                }
            }
        }
        return i;
    }

    private static int appendQuotedLiteral(String sql, int i, int n, char quote, StringBuilder result) {
        result.append(quote);
        i++;
        while (i < n) {
            char ch = sql.charAt(i);
            result.append(ch);
            i++;
            if (ch == quote) {
                if (i < n && sql.charAt(i) == quote) {
                    result.append(quote);
                    i++;
                } else {
                    break;
                }
            } else if (ch == '\\' && i < n) {
                result.append(sql.charAt(i));
                i++;
            }
        }
        return i;
    }

    private static boolean isIdentStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private static boolean isIdentPart(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private static Object unwrapProxy(Object target) {
        MetaObject metaObject = SystemMetaObject.forObject(target);
        while (metaObject.hasGetter("h")) {
            Object h = metaObject.getValue("h");
            metaObject = SystemMetaObject.forObject(h);
            if (metaObject.hasGetter("target")) {
                target = metaObject.getValue("target");
                metaObject = SystemMetaObject.forObject(target);
            } else {
                break;
            }
        }
        return target;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // no-op
    }
}
