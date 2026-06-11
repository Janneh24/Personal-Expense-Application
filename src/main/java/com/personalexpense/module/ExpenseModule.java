package com.personalexpense.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.personalexpense.repository.CategoryRepository;
import com.personalexpense.repository.ExpenseRepository;
import com.personalexpense.repository.UserRepository;
import com.personalexpense.repository.mysql.MysqlCategoryRepository;
import com.personalexpense.repository.mysql.MysqlExpenseRepository;
import com.personalexpense.repository.mysql.MysqlUserRepository;
import com.mysql.cj.jdbc.MysqlDataSource;
import javax.inject.Singleton;
import javax.sql.DataSource;

public class ExpenseModule extends AbstractModule {
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;

    public ExpenseModule(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    @Override
    protected void configure() {
        bind(ExpenseRepository.class).to(MysqlExpenseRepository.class).in(Singleton.class);
        bind(CategoryRepository.class).to(MysqlCategoryRepository.class).in(Singleton.class);
        bind(UserRepository.class).to(MysqlUserRepository.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public DataSource provideDataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName(host);
        dataSource.setPort(port);
        dataSource.setDatabaseName(database);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        return dataSource;
    }
}
