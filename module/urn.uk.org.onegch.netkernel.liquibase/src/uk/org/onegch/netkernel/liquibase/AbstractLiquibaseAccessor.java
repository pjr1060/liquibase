/*
 * Copyright (C) 2010-2011 by Chris Cormack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.org.onegch.netkernel.liquibase;

import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ResourceAccessor;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.util.RequestScopeClassLoader;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

public abstract class AbstractLiquibaseAccessor extends StandardAccessorImpl {
  protected Liquibase createLiquibase(INKFRequestContext aContext, String changelog) throws Exception {
    ClassLoader cl= new RequestScopeClassLoader(aContext.getKernelContext().getThisKernelRequest().getRequestScope());
    ResourceAccessor ra= new NetKernelResourceAccessor(aContext, cl);
    
    IHDSNode configNode= aContext.source("res:/etc/system/LiquibaseConfig.xml", IHDSNode.class);
    
    Database database = createDatabaseObject(cl,
                                             (String)configNode.getFirstValue("//jdbcDriver"),
                                             (String)configNode.getFirstValue("//jdbcConnection"),
                                             (String)configNode.getFirstValue("//user"),
                                             (String)configNode.getFirstValue("//password"),
                                             (String)configNode.getFirstValue("//changeLogTableName"),
                                             (String)configNode.getFirstValue("//changeLogLockTableName"));
    Liquibase liquibase = new Liquibase(changelog,
                                        ra,
                                        database);
    
    return liquibase;
  }
  
  private Database createDatabaseObject(ClassLoader cl,
                                        String driverClassName,
                                        String databaseUrl,
                                        String username,
                                        String password,
                                        String logTableName,
                                        String logLockTableName) throws Exception {
    
    if (driverClassName == null) {
        driverClassName = DatabaseFactory.getInstance().findDefaultDriver(databaseUrl);
    }
    
    if (driverClassName == null) {
        throw new Exception("driver not specified and no default could be found for " + databaseUrl);
    }
    
    Driver driver = (Driver) Class.forName(driverClassName, true, cl).newInstance();
    
    Properties info = new Properties();
    if (username != null) {
      info.put("user", username);
    }
    if (password != null) {
      info.put("password", password);
    }
    
    Connection connection = driver.connect(databaseUrl, info);
    
    if (connection == null) {
        throw new Exception("Connection could not be created to " +
                            databaseUrl +
                            " with driver " +
                            driver.getClass().getName() +
                            ".  Possibly the wrong driver for the given database URL");
    }
    
    Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

    database.setDatabaseChangeLogTableName(logTableName);
    database.setDatabaseChangeLogLockTableName(logLockTableName);

    return database;
  }
}
