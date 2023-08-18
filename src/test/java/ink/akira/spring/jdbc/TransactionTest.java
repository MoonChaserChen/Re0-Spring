package ink.akira.spring.jdbc;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionTest {
    private DataSource dataSource;

    public static final String STUB_INSERT = "insert into pet values (1, 'Tom', 18)";

    @Before
    public void before() {
        MysqlConnectionPoolDataSource mysqlDataSource = new MysqlConnectionPoolDataSource();
        mysqlDataSource.setURL("jdbc:mysql://localhost:3306/akira?useUnicode=true&characterEncoding=utf8&useSSL=false");
        mysqlDataSource.setUser("root");
        mysqlDataSource.setPassword("root@Mysql8.0");
        dataSource = mysqlDataSource;
    }

    @Test
    public void test0() throws SQLException {
        Connection con = dataSource.getConnection();
        try (PreparedStatement ps = con.prepareStatement(STUB_INSERT);) {
            con.setAutoCommit(false);

            ps.execute();
            ps.execute(); // Duplicate entry '1' for key 'pet.PRIMARY'

            con.commit();
        } catch (Exception e) {
            con.rollback();
        } finally {
            con.close();
        }
    }

    @Test
    public void test1() {
        JdbcTransactionManager txManager = new JdbcTransactionManager(dataSource);
        TransactionStatus transaction = txManager.getTransaction(TransactionDefinition.withDefaults());

        try {
            doInsert();
            doInsert();
            txManager.commit(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            txManager.rollback(transaction);
        }
    }

    @Test(expected = RuntimeException.class)
    public void test2() {
        JdbcTransactionManager txManager = new JdbcTransactionManager(dataSource);
        TransactionTemplate template = new TransactionTemplate(txManager);
        template.execute(status -> {
            doInsert();
            doInsert();
            return null;
        });
    }

    public void doInsert() {
        // 这里不能直接使用 dataSource.getConnection()，将导致多个sql使用的不是同一个 Connection，无法回滚
        // 不能关闭Connection，因为下个sql还会用到这个Connection，Spring会在commit或rollback时自动关闭。那单独使用这个方法就会导致Connection无法关闭么？而且如果使用了Mybatis等，它会自动关闭Connection吗？
        try {
            Connection con = DataSourceUtils.getConnection(dataSource);
            PreparedStatement ps = con.prepareStatement(STUB_INSERT);
            ps.execute();
        } catch (CannotGetJdbcConnectionException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
