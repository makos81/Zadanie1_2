package com.kodilla.csvconverter.zadanie2;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@EnableBatchProcessing
@Configuration
public class BatchConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    FlatFileItemReader<NamesWithBirthday> reader() {
        FlatFileItemReader<NamesWithBirthday> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("names.csv"));

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("name", "surname", "birthday");

        BeanWrapperFieldSetMapper<NamesWithBirthday> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(NamesWithBirthday.class);

        DefaultLineMapper<NamesWithBirthday> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(mapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    NamesWithBirthdayProcessor processor() {
        return new NamesWithBirthdayProcessor();
    }

    @Bean
    FlatFileItemWriter<NamesWithAge> writer() {
        BeanWrapperFieldExtractor<NamesWithAge> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[]{"name", "surname", "age"});

        DelimitedLineAggregator<NamesWithAge> aggregator = new DelimitedLineAggregator<>();
        aggregator.setDelimiter(",");
        aggregator.setFieldExtractor(extractor);

        FlatFileItemWriter<NamesWithAge> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("output.csv"));
        writer.setShouldDeleteIfExists(true);
        writer.setLineAggregator(aggregator);

        return writer;
    }

    @Bean
    Step ageCalculation(ItemReader<NamesWithBirthday> reader,
                        ItemProcessor<NamesWithBirthday, NamesWithAge> processor,
                        ItemWriter<NamesWithAge> writer) {
        return stepBuilderFactory.get("ageCalculation")
                .<NamesWithBirthday, NamesWithAge>chunk(100)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    Job calculateYearsJob(Step ageCalculation) {
        return jobBuilderFactory.get("calculateYearsJob")
                .incrementer(new RunIdIncrementer())
                .flow(ageCalculation)
                .end()
                .build();
    }
}
