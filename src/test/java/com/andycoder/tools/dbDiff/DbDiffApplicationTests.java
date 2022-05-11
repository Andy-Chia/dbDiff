package com.andycoder.tools.dbDiff;

import com.andycoder.tools.dbDiff.utils.GsonUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class DbDiffApplicationTests {


    private Logger logger = LoggerFactory.getLogger(DbDiffApplicationTests.class);

    // 标准数据库配置
    private static Map<String, Object> standardDBConfig = new HashMap<>();
    // 对比数据库配置。
    private static Map<String, Object> diffDBConfig = new HashMap<>();

    static {
        standardDBConfig.put("ip",".mysql.rds.aliyuncs.com");
        standardDBConfig.put("port","3306");
        standardDBConfig.put("dbtype","mysql");
        standardDBConfig.put("username","");
        standardDBConfig.put("password","!");
        standardDBConfig.put("database","standarddb");

        diffDBConfig.put("ip",".mysql.rds.aliyuncs.com");
        diffDBConfig.put("port","3306");
        diffDBConfig.put("dbtype","mysql");
        diffDBConfig.put("username","");
        diffDBConfig.put("password","!");
        diffDBConfig.put("database","diff");
    }

    @Test
    void diff() {

        List<Map<String, Object>> standardDBTables = getDBTables(standardDBConfig);
        List<Map<String, Object>> diffDBTables = getDBTables(diffDBConfig);
        for (Map<String, Object> map : standardDBTables) {
            logger.info("table info {}",GsonUtil.string(map));
            String tableName = MapUtils.getString(map, "TABLE_NAME");

            List<Map<String, Object>> maps = qryFields(standardDBConfig, tableName);


            logger.info(GsonUtil.string(maps));
            List<Map<String, Object>> maps1 = qryIndexs(standardDBConfig, tableName);
            logger.info(GsonUtil.string(maps1));
        }

        for (Map<String, Object> map : diffDBTables) {
            logger.info("table info {}",GsonUtil.string(map));
            String tableName = MapUtils.getString(map, "TABLE_NAME");

            List<Map<String, Object>> maps = qryFields(diffDBConfig, tableName);
            logger.info(GsonUtil.string(maps));
            List<Map<String, Object>> maps1 = qryIndexs(diffDBConfig, tableName);
            logger.info(GsonUtil.string(maps1));
        }


    }

    /**
     * 获取索引
     *
     * @param dbconfig
     * @return
     */
    public List<Map<String, Object>> qryIndexs(Map<String, Object> dbconfig, String tableName) {

        JdbcTemplate jdbcTemplate = getJdbcTemplate(dbconfig);
        String type = MapUtils.getString(dbconfig, "dbtype");
        String sql = getQryIndexSql(type, tableName);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }


    /**
     * 获取字段
     *
     * @param dbconfig
     * @return
     */
    public List<Map<String, Object>> qryFields(Map<String, Object> dbconfig, String tableName) {

        JdbcTemplate jdbcTemplate = getJdbcTemplate(dbconfig);
        String type = MapUtils.getString(dbconfig, "dbtype");
        String sql = getQryFieldsSql(type, tableName);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }

    /**
     * 获取表。
     *
     * @param dbconfig
     * @return
     */
    private List<Map<String, Object>> getDBTables(Map<String, Object> dbconfig) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(dbconfig);
        String type = MapUtils.getString(dbconfig, "dbtype");
        String sql = getQryTablesSql(type);
        List<Map<String, Object>> dbTableList = null;
        if ("mysql".equals(type)) {
            dbTableList = jdbcTemplate.queryForList(sql, dbconfig.get("database") + "%");
        } else if ("oracle".equals(type)) {
            dbTableList = jdbcTemplate.queryForList(sql);
        }

        logger.info("{} get table size {}", dbconfig.get("database"), dbTableList.size());
        return dbTableList;

    }


    private JdbcTemplate getJdbcTemplate(Map<String, Object> params) {
        String type = MapUtils.getString(params, "dbtype");
        String driverClassName = "";
        switch (type) {
            case "mysql":
                driverClassName = "com.mysql.jdbc.Driver";
                break;
            case "oracle":
                driverClassName = "oracle.jdbc.driver.OracleDriver";
                break;
        }
        String ip = MapUtils.getString(params, "ip");
        String port = MapUtils.getString(params, "port");
        String userName = MapUtils.getString(params, "username");
        String password = MapUtils.getString(params, "password");
        String dbName = MapUtils.getString(params, "database");
        String param = MapUtils.getString(params, "param");
        String url = "";
        switch (type) {
            case "mysql":
                url = "jdbc:mysql://" + ip + ":" + port + "/" + dbName + "?" + param;
                break;
            case "oracle":
                url = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + dbName + (StringUtils.isEmpty(param) ? "" : ("?" + param));
                break;
        }
        JdbcTemplate jdbcTemplate = null;
        DataSource dataSource = DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(userName)
                .password(password)
                .build();
        jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate;
    }

    private String getQryTablesSql(String dbType) {
        String sql = "";
        switch (dbType) {
            case "mysql":
                sql = "SELECT * FROM INFORMATION_SCHEMA.`TABLES` WHERE TABLE_SCHEMA like ? ORDER BY create_time DESC";
                break;
            case "oracle":
                sql = "select * from user_tab_comments where TABLE_NAME not like '%$%'";
                break;
        }
        return sql;
    }

    private String getQryFieldsSql(String dbType, String tableName) {
        String sql = "";
        switch (dbType) {
            case "mysql":
                sql = "show full columns from " + tableName;
                break;
            case "oracle":
                sql = "select COLUMN_NAME as \"Field\",DATA_TYPE||'('||CHAR_COL_DECL_LENGTH||')' as \"Type\",NULLABLE as \"Null\",DATA_DEFAULT as \"Default\" from user_tab_columns where TABLE_NAME ='" + tableName + "'";
                break;
        }
        return sql;
    }


    private String getQryIndexSql(String dbType, String tableName) {
        String sql = "";
        switch (dbType) {
            case "mysql":
                sql = "show index from " + tableName;
                break;
            case "oracle":
                sql = " select * from user_indexes where table='" + tableName + "'";
                break;
        }
        return sql;
    }
}
