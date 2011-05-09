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

import java.util.List;

import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

public class UpdateAvailableAccessor extends AbstractLiquibaseAccessor {
  
  @Override
  public void onSource(INKFRequestContext aContext) throws Exception {
    Liquibase liquibase= createLiquibase(aContext, aContext.source("arg:changelog", String.class));
    
    if (aContext.exists("arg:parameters")) {
      IHDSNode parameters= aContext.source("arg:parameters", IHDSNode.class);
      for (IHDSNode parameter : parameters.getNodes("//parameter")) {
        liquibase.setChangeLogParameter((String)parameter.getFirstValue("name"),
                                        (String)parameter.getFirstValue("value"));
      }
    }
    
    String liquibaseContext= null;
    if (aContext.exists("arg:context")) {
      liquibaseContext= aContext.source("arg:context", String.class);
    }
    
    List<ChangeSet> changesets= liquibase.listUnrunChangeSets(liquibaseContext);
    
    aContext.createResponseFrom(changesets.size() > 0);
  }
}
