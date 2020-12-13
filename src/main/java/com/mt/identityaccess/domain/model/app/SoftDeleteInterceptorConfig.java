package com.mt.identityaccess.domain.model.app;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.EmptyInterceptor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * handle hibernate native load soft delete globally due to @Where & @Filter does not support inheritance.
 * this is used only for @manytomany .etc use case, which entity is loaded via hibernate api,
 * instead of criteria api provided
 * alternatively you can chose to use @Where(clause = "deleted = false") on entity class
 */
@Component
@Slf4j
public class SoftDeleteInterceptorConfig implements HibernatePropertiesCustomizer {
    private static final String DELETED_IS_FALSE = ".deleted = 0) ";
    private static final String INNER_JOIN_QUERY = "inner join";
    private static final String WHERE_KEYWORD = "where";
    private static final String SELECT_KEYWORD = "select";

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.session_factory.interceptor", hibernateInterceptor());
    }

    @Bean
    public EmptyInterceptor hibernateInterceptor() {
        return new EmptyInterceptor() {
            @Override
            public String onPrepareStatement(String sql) {
                if (sql.indexOf(SELECT_KEYWORD) == 0 && sql.contains(INNER_JOIN_QUERY) && !sql.contains(DELETED_IS_FALSE)) {
                    String[] split = sql.split(INNER_JOIN_QUERY);
                    String[] s1 = split[1].trim().split(" ");
                    String aliaName = s1[1];
                    String[] split1 = split[1].split(WHERE_KEYWORD);
                    String s = split1[0] + " and ( " + aliaName + DELETED_IS_FALSE;
                    sql = split[0] + INNER_JOIN_QUERY + s + WHERE_KEYWORD + split1[1];
                }
                System.out.println("sql " + sql);
                return sql;
            }
        };
    }
}
