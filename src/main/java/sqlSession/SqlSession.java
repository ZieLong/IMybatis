package sqlSession;

import java.sql.SQLException;
import java.util.List;

public interface SqlSession {

    //查询所有
    public <E> List<E> selectList(String statementId, Object... args) throws SQLException, ClassNotFoundException, Exception;


    //根据条件查询单个
    public <T> T selectOne(String statementId, Object... args) throws SQLException, Exception;


    public <T> T insert(String statementId, Object... args) throws SQLException, Exception;
    public <T> T delete(String statementId, Object... args) throws SQLException, Exception;
    public <T> T update(String statementId, Object... args) throws SQLException, Exception;
    //为dao接口生成代理实现类

    public <T> T getMapper(Class<?> mapperClass);

}
