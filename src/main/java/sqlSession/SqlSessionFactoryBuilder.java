package sqlSession;

import config.XMLConfigBuilder;
import pojo.Configuration;

import java.io.InputStream;

public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream in) throws Exception {
        //第一:使用dom4j解析配置文件，将内容封装到configuration中

       XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parseConfig(in);

        //第二：创建sqlSessionFactory对象:工厂类：生产sqlSession:会话对象

        DefaultSqlSessionFactory defaultSqlSessionFactory = new DefaultSqlSessionFactory(configuration);
        return defaultSqlSessionFactory;

    }

}
