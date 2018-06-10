package config;

import com.google.inject.AbstractModule;

public class BindConfig extends AbstractModule{

    @Override
    protected void configure() {
        bind(ModelMapper.class).toInstance(ModelMapper.INSTANCE);
    }
}
