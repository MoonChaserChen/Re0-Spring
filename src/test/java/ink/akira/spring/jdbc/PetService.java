package ink.akira.spring.jdbc;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PetService {
    public static final String STUB_INSERT = "insert into pet values (1, 'Tom', 18)";

    private DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 基于XML的声明式事务
     */
    public void stubInsert() {
        // 这里不能直接使用 dataSource.getConnection()，将导致多个sql使用的不是同一个 Connection，无法回滚
        // 不能关闭Connection，因为下个sql还会用到这个Connection，Spring会在commit或rollback时自动关闭。那单独使用这个方法就会导致Connection无法关闭么？而且如果使用了Mybatis等，它会自动关闭Connection吗？
        try {
            Connection con = DataSourceUtils.getConnection(dataSource);
            PreparedStatement ps = con.prepareStatement(STUB_INSERT);
            ps.execute();
            ps.execute(); // Duplicate entry '1' for key 'pet.PRIMARY'
        } catch (CannotGetJdbcConnectionException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 基于注解的声明式事务
     */
    @Transactional
    public void doInsert() {
        // 这里不能直接使用 dataSource.getConnection()，将导致多个sql使用的不是同一个 Connection，无法回滚
        // 不能关闭Connection，因为下个sql还会用到这个Connection，Spring会在commit或rollback时自动关闭。那单独使用这个方法就会导致Connection无法关闭么？而且如果使用了Mybatis等，它会自动关闭Connection吗？
        try {
            Connection con = DataSourceUtils.getConnection(dataSource);
            PreparedStatement ps = con.prepareStatement(STUB_INSERT);
            ps.execute();
            ps.execute(); // Duplicate entry '1' for key 'pet.PRIMARY'
        } catch (CannotGetJdbcConnectionException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
