package config;

import org.mybatis.guice.XMLMyBatisModule;

/**
 * @author youjun
 * @date 2019/12/5 18:08
 */
public class MybatisConfig extends XMLMyBatisModule {
    @Override
    protected void initialize() {
//        install(JdbcHelper.SQLITE_FILE);

//        bindDataSourceProviderType(PooledDataSourceProvider.class);
//        bindTransactionFactoryType(JdbcTransactionFactory.class);
//        addMapperClass(VideoInfoMapper.class);
//
//        Names.bindProperties(binder(), PropertyUtil.getConfig("db.properties"));
    }
}
