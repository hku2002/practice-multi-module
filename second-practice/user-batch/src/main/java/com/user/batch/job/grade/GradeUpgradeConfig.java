package com.user.batch.job.grade;


import com.user.entity.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class GradeUpgradeConfig {

    public final DataSource dataSource;

    @Bean
    public Job userUpgradeJob(JobRepository jobRepository, Step userUpgradeStep) {
        return new JobBuilder("userUpgradeJob", jobRepository)
                .start(userUpgradeStep)
                .build();
    }

    @Bean
    @JobScope
    public Step userUpgradeStep(JobRepository jobRepository
            , PlatformTransactionManager transactionManager) {
        return new StepBuilder("userUpgradeStep", jobRepository)
                .chunk(10, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<User> reader() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("baseOrderCount", 10);

        return new JdbcPagingItemReaderBuilder<User>()
                .pageSize(10)
                .fetchSize(10)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(User.class))
                .queryProvider(customQueryProvider())
                .parameterValues(parameters)
                .name("JdbcPagingItemReader")
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<User, User> processor(){
        return user -> {
            user.gradeUp();
            return user;
        };
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<User> writer(){
        return new JdbcBatchItemWriterBuilder<User>()
                .dataSource(dataSource)
                .sql("UPDATE user SET grade = 'LV2' WHERE id = :id")
                .beanMapped()
                .build();
    }

    private PagingQueryProvider customQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean queryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();

        queryProviderFactoryBean.setDataSource(dataSource);

        queryProviderFactoryBean.setSelectClause("SELECT id, grade ");
        queryProviderFactoryBean.setFromClause("FROM user ");
        queryProviderFactoryBean.setWhereClause("WHERE totalOrderCount >= :baseOrderCount AND grade != 'LV2'");

        Map<String,Order> sortKey = new HashMap<>();
        sortKey.put("id", Order.ASCENDING);

        queryProviderFactoryBean.setSortKeys(sortKey);

        return queryProviderFactoryBean.getObject();

    }

}
