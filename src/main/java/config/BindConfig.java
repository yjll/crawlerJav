package config;

import com.google.inject.AbstractModule;
import org.mapstruct.factory.Mappers;

public class BindConfig extends AbstractModule{

    @Override
    protected void configure() {
        bind(ModelMapstruct.class).toInstance(Mappers.getMapper(ModelMapstruct.class));
    }
}
