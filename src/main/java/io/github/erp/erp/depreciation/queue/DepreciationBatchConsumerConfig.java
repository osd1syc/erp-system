package io.github.erp.erp.depreciation.queue;

/*-
 * Erp System - Mark X No 3 (Jehoiada Series) Server ver 1.7.3
 * Copyright © 2021 - 2024 Edwin Njeru and the ERP System Contributors (mailnjeru@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import io.github.erp.erp.depreciation.model.DepreciationBatchMessage;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
// import org.springframework.kafka.listener.config.ContainerProperties.ListenerMode;
// import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// import static io.github.erp.erp.depreciation.queue.DepreciationBatchProducer.DEPRECIATION_BATCH_TOPIC;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka.consumer")
public class DepreciationBatchConsumerConfig {

    @Value("${spring.kafka.depreciation-batch.topic.name}")
    private String topicName;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group.id}")
    private String groupId;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DepreciationBatchMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DepreciationBatchMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }


    @SneakyThrows
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        properties.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, String.valueOf(TimeUnit.MINUTES.toMillis(2)));
//        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, String.valueOf(TimeUnit.MINUTES.toMillis(2)));
//        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, String.valueOf(TimeUnit.MINUTES.toMillis(7)));
//        properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, String.valueOf(TimeUnit.MINUTES.toMillis(2)));
//        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, String.valueOf(TimeUnit.MINUTES.toMillis(5)));
//        properties.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, String.valueOf(TimeUnit.MINUTES.toMillis(2)));

        return properties;
    }

    // Create Kafka consumer factory
    @Bean
    public DefaultKafkaConsumerFactory<String, DepreciationBatchMessage> consumerFactory() {
        DefaultKafkaConsumerFactory<String, DepreciationBatchMessage> consumerFactory =  new DefaultKafkaConsumerFactory<>(consumerConfigs());

        // TODO Add listener
        consumerFactory.setKeyDeserializer(new StringDeserializer());
        consumerFactory.setValueDeserializer(new DepreciationBatchMessageDeserializer());
        return consumerFactory;
    }
}


