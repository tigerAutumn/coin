package com.qkwl.service.otc.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;


@Component("flexibleTransMgr")
public class DrdsFlexibleTransaction extends DataSourceTransactionManager {
	
	public DrdsFlexibleTransaction(@Qualifier("otcDateSource")DataSource dataSource) {
        super(dataSource);
    }
	
    @Override
    protected void prepareTransactionalConnection(Connection con, TransactionDefinition definition) throws SQLException {
    	DatabaseMetaData dmd = con.getMetaData();
    	if (dmd.getURL().contains("drds") ) {
    		try (Statement stmt = con.createStatement()) {
            	logger.info("drds_transaction_policy = 'FLEXIBLE'");
                stmt.executeUpdate("SET drds_transaction_policy = 'FLEXIBLE'");
            }
    	}
        
    }
}
