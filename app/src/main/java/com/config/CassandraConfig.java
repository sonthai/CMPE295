package com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.DropKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableCassandraRepositories(basePackages = "com.api.database.repository")
public class CassandraConfig extends AbstractCassandraConfiguration {
    public static final String KEYSPACE = "app_keyspace";
    @Override
    protected String getKeyspaceName() {
        return KEYSPACE;
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        CreateKeyspaceSpecification specification = CreateKeyspaceSpecification.createKeyspace(KEYSPACE)
                                                    .ifNotExists()
                                                    .with(KeyspaceOption.DURABLE_WRITES)
                                                    .withSimpleReplication();

        return Arrays.asList(specification);
    }

    //@Override
    //protected List<DropKeyspaceSpecification> getKeyspaceDrops() {
      //  return Arrays.asList(DropKeyspaceSpecification.dropKeyspace(KEYSPACE));
    //}


    @Override
    public String[] getEntityBasePackages() {
        return new String[]{"com.api.database.domain"};
    }

    //@Override
    //protected List<String> getStartupScripts() {
        //String insertions = "INSERT INTO app_keyspace.classifier (product_id, id, color_id, style_id, brand_id, upper_id, lower_id, gender_id) "
          //      + " VALUES ('1',0,'1','2','3','2','3','1')";
        //return Collections.singletonList(insertions);
        //return Collections.singletonList("CREATE TABLE IF NOT EXISTS myKeySpace.test(id UUID PRIMARY KEY, greeting text, occurrence timestamp) WITH default_time_to_live = 600;");
    //}
}
