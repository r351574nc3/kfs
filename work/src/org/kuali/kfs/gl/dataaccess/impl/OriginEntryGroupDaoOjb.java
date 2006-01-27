/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.dao.ojb;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.OriginEntryGroupDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author Laran Evans <lc278@cornell.edu>
 * @version $Id: OriginEntryGroupDaoOjb.java,v 1.2 2006-01-27 16:28:17 jsissom Exp $
 * 
 */

public class OriginEntryGroupDaoOjb extends PersistenceBrokerDaoSupport implements OriginEntryGroupDao {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryGroupDaoOjb.class);

	public OriginEntryGroupDaoOjb() {
		super();
	}

	/**
	 * 
	 */
	public Collection getMatchingGroups(Map searchCriteria) {
	    LOG.debug("getPendingEntries() started");
	    
	    Criteria criteria = new Criteria();
	    for(Iterator iterator = searchCriteria.keySet().iterator(); iterator.hasNext();) {
	    	String key = iterator.next().toString();
		    criteria.addEqualTo(key, searchCriteria.get(key));
	    }

	    QueryByCriteria qbc = QueryFactory.newQuery(OriginEntryGroup.class, criteria);
	    return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
	}

}
