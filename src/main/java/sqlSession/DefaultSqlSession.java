package sqlSession;

import contains.DmlEnum;
import pojo.Configuration;
import pojo.MappedStatement;

import javax.jws.Oneway;
import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }


    @Override
    public <E> List<E> selectList(String statementId, Object... args) throws Exception {

        //将要完成对simpleExecutor的调用
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        List<Object> list = simpleExecutor.query(configuration, mappedStatement, args);

        return (List<E>) list;
    }

    @Override
    public <T> T selectOne(String statementId, Object... args) throws Exception {
        List<Object> objects = selectList(statementId, args);
        if (objects.size() == 1) {
            return (T) objects.get(0);
        } else {
            throw new RuntimeException("查询结果为空或返回结果多余1个");
        }
    }

    @Override
    public <T> T insert(String statementId, Object... args) throws SQLException, Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        Object res = simpleExecutor.execute(configuration, mappedStatement,args);
        return (T) res;
    }

    @Override
    public <T> T update(String statementId, Object... args) throws SQLException, Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        Object res = simpleExecutor.execute(configuration, mappedStatement,args);
        return (T) res;
    }


    @Override
    public <T> T delete(String statementId, Object... args) throws SQLException, Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        Object res = simpleExecutor.execute(configuration, mappedStatement,args);
        return (T) res;
    }


    @Override
    public <T> T getMapper(Class<?> mapperClass) {

        //使用jdk动态代理为Dao接口生成代理对象，并返回
        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //底层都还是执行JDBC代码,//根据不同情况来调用不同的方法
                //准备参数 1.statementId: sql语句的唯一标识：namespace.id

                //获取方法明
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();

                String statementId = className + "." + methodName;
                //获取被调用方法的返回值类型
                Type genericReturnType = method.getGenericReturnType();
                //判断是否进行了泛型参数泛型化
                Object o = null;

                switch (methodName) {
                    case "delete":
                        o =  delete(statementId, args);
                        break;
                    case "selectOne":
                        o =  selectOne(statementId, args);
                        break;
                    case "update":
                        o =  update(statementId,args);
                        break;
                    case "insert":
                        o =  insert(statementId, args);
                        break;
                    case "selectList":
                        o =  selectList(statementId, args);
                        break;
                }
                return o;
            }
        });
        return (T) proxyInstance;
    }


}
