package sqlSession;

import pojo.Configuration;
import pojo.MappedStatement;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface Executor {

    public <E> List<E> query(Configuration configuration, MappedStatement statement, Object... args) throws Exception;

    public Integer execute(Configuration configuration, MappedStatement statement, Object... args) throws Exception;

}
