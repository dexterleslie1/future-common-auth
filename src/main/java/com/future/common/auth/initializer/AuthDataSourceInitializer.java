package com.future.common.auth.initializer;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.CompositeDatabasePopulator;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 * auth 的 DataSourceInitializer
 */
public class AuthDataSourceInitializer extends DataSourceInitializer {
    public AuthDataSourceInitializer(DataSource dataSource) {
        this.setDataSource(dataSource);

        // 初始化数据库DatabasePopulator
        // 组合populator，用于组合普通脚本和存储过程populator
        CompositeDatabasePopulator compositeDatabasePopulator = new CompositeDatabasePopulator();
        // 普通脚本populator
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        List<String> sqlFileList = Arrays.asList("auth-init-db.sql");
        if (sqlFileList != null && sqlFileList.size() != 0) {
            for (String filePath : sqlFileList) {
                ClassPathResource resource = new ClassPathResource(filePath);
                databasePopulator.addScript(resource);
            }
        }
        compositeDatabasePopulator.addPopulators(databasePopulator);

        // 存储过程populator
        databasePopulator = new ResourceDatabasePopulator();
        sqlFileList = Arrays.asList();
        if (sqlFileList != null && sqlFileList.size() != 0) {
            for (String filePath : sqlFileList) {
                ClassPathResource resource = new ClassPathResource(filePath);
                databasePopulator.addScript(resource);
            }
        }
        // 设置存储过程sql文件内的语句结束符号为$$
        databasePopulator.setSeparator("$$");
        compositeDatabasePopulator.addPopulators(databasePopulator);

        this.setDatabasePopulator(compositeDatabasePopulator);

        // 清除数据库DatabasePopulator
        ResourceDatabasePopulator databaseCleaner = new ResourceDatabasePopulator();
        sqlFileList = Arrays.asList();
        if (sqlFileList != null && sqlFileList.size() != 0) {
            for (String filePath : sqlFileList) {
                ClassPathResource resource = new ClassPathResource(filePath);
                databasePopulator.addScript(resource);
            }
        }
        //在DataSourceInitializer中，setDatabaseCleaner方法的作用主要是用于在数据源（DataSource）销毁时执行一个清理脚本，确保数据库被清理并处于一个已知的状态，为其他使用者或者后续的测试留下干净的数据库环境。
        //具体来说，setDatabaseCleaner方法允许你设置一个DatabasePopulator对象，该对象会在DataSourceInitializer的destroy方法被调用时执行。这个DatabasePopulator可以配置为执行一个或多个SQL脚本，这些脚本用于清理或重置数据库。
        //在Spring框架中，DataSourceInitializer实现了InitializingBean和DisposableBean接口，因此它可以在Spring容器初始化数据源时执行初始化脚本，并在Spring容器销毁数据源时执行清理脚本。
        this.setDatabaseCleaner(databaseCleaner);
    }
}
