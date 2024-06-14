package com.user.batch.job.grade;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class GradeUpgradeConfig {

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

}
