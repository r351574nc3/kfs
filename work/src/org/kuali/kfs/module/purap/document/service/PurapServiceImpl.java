/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/document/service/PurapServiceImpl.java,v $
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.purap.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.ContractManager;
import org.kuali.module.purap.bo.StatusHistory;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.service.PurapService;

public class PurapServiceImpl implements PurapService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapServiceImpl.class);

    private BusinessObjectService businessObjectService;
    protected BusinessObjectService getBOService() {
        if( ObjectUtils.isNull( this.businessObjectService ) ) {
            this.businessObjectService = SpringServiceLocator.getBusinessObjectService();
        }
        return this.businessObjectService;
    }
    public void setBusinessObjectService(BusinessObjectService boService) {
        this.businessObjectService = boService;    
    }

    /**
     * This method updates the status and status history for a purap document.
     *
    */
    public boolean updateStatusAndStatusHistory(PurchasingAccountsPayableDocument document,String statusToSet) {
        LOG.debug("updateStatusAndStatusHistory(): entered method.");
        
        boolean success = false;
        
        if ( ObjectUtils.isNull(document) || ObjectUtils.isNull(statusToSet) ) {
            return success;
        }

        success = this.updateStatus(document, statusToSet);

// TODO: make this work.
//        success = this.updateStatusHistory(document, statusToSet);

        LOG.debug("updateStatusAndStatusHistory(): leaving method.");
        return success;
    }

    /**
     * This method updates the status for a purap document.
     *
    */
    public boolean updateStatus(PurchasingAccountsPayableDocument document,String statusToSet) {
        LOG.debug("updateStatus(): entered method.");
        
        boolean success = false;
        
        if ( ObjectUtils.isNull(document) || ObjectUtils.isNull(statusToSet) ) {
            return success;
        }

        document.setStatusCode(statusToSet);
        
        success = true;
        
        LOG.debug("updateStatus(): leaving method.");
        return success;
    }

    /**
     * This method updates the status history for a purap document.
     *
    */
    public boolean updateStatusHistory(PurchasingAccountsPayableDocument document,String statusToSet) {
        LOG.debug("updateStatusHistory(): entered method.");
        
        boolean success = false;
        
        if ( ObjectUtils.isNull(document) || ObjectUtils.isNull(statusToSet) ) {
            return success;
        }

        StatusHistory statusHistory = new StatusHistory();
        statusHistory.setOldStatusCode(document.getStatusCode());
        statusHistory.setNewStatusCode(statusToSet);
        // TODO: add note, what other fields need to be filled?
        
        document.getStatusHistories().add(statusHistory);

        success = true;
        
        LOG.debug("updateStatusHistory(): leaving method.");
        return success;
    }

}
