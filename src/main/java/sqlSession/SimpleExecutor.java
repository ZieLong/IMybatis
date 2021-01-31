package sqlSession;

import config.BoundSql;
import pojo.Configuration;
import pojo.MappedStatement;
import utils.GenericTokenParser;
import utils.ParameterMapping;
import utils.ParameterMappingTokenHandler;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor{
    public SimpleExecutor() {
    }

    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement statement, Object... args) throws Exception {

        //1.注册驱动，获取链接
        Connection connection = configuration.getDataSource().getConnection();

        //2.获取sql语句
        //转换sql语句 select * from user where id = ? and username = ? ,转换的过程中还需要都#{}的值进行存储
        String sql = statement.getSql();
        BoundSql boundsql =  getBoundSql(sql);

        //获取预处理对象 preparedStatement;

        PreparedStatement preparedStatement = connection.prepareStatement(boundsql.getSqlText());

        //4.设置参数

        String paramType = statement.getParameterType();
        Class<?> parameterClass = getClassType(paramType);


        List<ParameterMapping> parameterMappings = boundsql.getParameterMappingList();
        for (int i = 0; i < parameterMappings.size(); i++) {
            ParameterMapping parameterMapping = parameterMappings.get(i);
            String content = parameterMapping.getContent();

            //反射
            Field declaredField = parameterClass.getDeclaredField(content);
            //暴力访问
            declaredField.setAccessible(true);
            Object o = declaredField.get(args[0]);

            preparedStatement.setObject(i + 1, o);

        }

        //5.执行sql
        ResultSet resultSet = preparedStatement.executeQuery();

        String resultType = statement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);
        ArrayList<Object> objects = new ArrayList<>();

        //6封装返回结果集
        while (resultSet.next()) {
            Object o = resultTypeClass.newInstance();
            //元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                //获取字段的值
                Object value = resultSet.getObject(columnName);
                //使用反射或者内省,根据属性名完成封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o, value);
            }
            objects.add(o);
        }
        return (List<E>) objects;
    }


    @Override
    public Integer execute(Configuration configuration, MappedStatement statement, Object... args) throws Exception {

        //1.注册驱动，获取链接
        Connection connection = configuration.getDataSource().getConnection();
        //2.获取sql语句
        //转换sql语句 select * from user where id = ? and username = ? ,转换的过程中还需要都#{}的值进行存储
        String sql = statement.getSql();
        BoundSql boundsql =  getBoundSql(sql);

        //获取预处理对象 prearedStatement;
        PreparedStatement preparedStatement = connection.prepareStatement(boundsql.getSqlText());
        //4.设置参数
        String paramType = statement.getParameterType();
        Class<?> parameterClass = getClassType(paramType);
        List<ParameterMapping> parameterMappings = boundsql.getParameterMappingList();
        for (int i = 0; i < parameterMappings.size(); i++) {
            ParameterMapping parameterMapping = parameterMappings.get(i);
            String content = parameterMapping.getContent();

            //反射
            Field declaredField = parameterClass.getDeclaredField(content);
            //暴力访问
            declaredField.setAccessible(true);
            Object o = declaredField.get(args[0]);

            preparedStatement.setObject(i + 1, o);

        }
        //5.执行sql
        preparedStatement.execute();
        return preparedStatement.getUpdateCount();
    }


    private Class<?> getClassType(String parameterType) throws ClassNotFoundException {

        if (parameterType != null) {
            Class<?> aClass = Class.forName(parameterType);
            return aClass;
        } else {
            return null;
        }

    }

    private BoundSql getBoundSql(String sql) {

        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        //解析过后的sql
        String parseSql = genericTokenParser.parse(sql);
        //#{}里面是解析出的参数
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        BoundSql boundSql = new BoundSql(parseSql, parameterMappings);
        return boundSql;
    }
}
