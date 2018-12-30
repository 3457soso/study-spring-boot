package users.sql.sqlmapping;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public interface SqlMapConfig {
    Resource getSqlMapResource();
}
