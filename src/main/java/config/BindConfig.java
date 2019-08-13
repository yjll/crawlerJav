package config;

import com.google.inject.AbstractModule;
import org.mapstruct.factory.Mappers;

public class BindConfig extends AbstractModule{

    @Override
    protected void configure() {
        bind(ModelMapper.class).toInstance(Mappers.getMapper(ModelMapper.class));
    }
}
