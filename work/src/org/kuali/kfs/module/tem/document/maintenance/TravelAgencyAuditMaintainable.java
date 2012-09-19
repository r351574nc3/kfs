/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.maintenance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.batch.service.AgencyDataImportService;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.service.CreditCardAgencyService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.document.MaintenanceDocument;

/**
 * Maintainable instance for the travel agency audit maintenance document
 *  
 */
public class TravelAgencyAuditMaintainable extends FinancialSystemMaintainable {
    
    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.kns.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);
        if (documentHeader.getWorkflowDocument().stateIsFinal()){
            AgencyStagingData agencyStaging  = (AgencyStagingData) getBusinessObject();
            
            //get the updated AgencyStagingData from DB
            AgencyStagingData updateAgencyStaging = getBusinessObjectService().findBySinglePrimaryKey(AgencyStagingData.class, agencyStaging.getId());
            updateCreditCardAgency(updateAgencyStaging);    
            //after fixing the agency audit record, attempt to move agency data to historical table
            AgencyDataImportService importService = SpringContext.getBean(AgencyDataImportService.class);
            importService.processAgencyStagingExpense(updateAgencyStaging, new GeneralLedgerPendingEntrySequenceHelper());
            
            //save the agency staging record after it is processed and moved to history
            getBusinessObjectService().save(updateAgencyStaging);
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterEdit(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterPost(document, parameters);
        AgencyStagingData agencyStaging = (AgencyStagingData) super.getBusinessObject();
        updateCreditCardAgency(agencyStaging);
    }
    
    /**
     * 
     * @param agencyStaging
     */
    private void updateCreditCardAgency(AgencyStagingData agencyStaging){
        //update the agency name base on code if provided
        CreditCardAgencyService creditCardAgencyService = SpringContext.getBean(CreditCardAgencyService.class);
        CreditCardAgency agency = SpringContext.getBean(CreditCardAgencyService.class).getCreditCardAgencyByCode(agencyStaging.getCreditCardOrAgencyCode());
        if (agency != null){
            agencyStaging.setCreditCardAgency(agency);
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#populateBusinessObject(java.util.Map, org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Map populateBusinessObject(Map<String, String> fieldValues, MaintenanceDocument maintenanceDocument, String methodToCall) {
        //populate maintenanceDocument with boNotes from this maintainable.
        List boNotes = maintenanceDocument.getBoNotes();
        if(boNotes == null){
            boNotes = new ArrayList();
        }else{
            boNotes.clear();
        }
        
        if(!getBusinessObject().getBoNotes().isEmpty()){
            boNotes.addAll(getBusinessObject().getBoNotes());
        }
        
        AgencyStagingData agencyStaging = (AgencyStagingData) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        updateCreditCardAgency(agencyStaging);
        return super.populateBusinessObject(fieldValues, maintenanceDocument, methodToCall);
    }

	/**
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
	 */
	@Override
	public void saveBusinessObject() {
	    AgencyStagingData agencyStaging = (AgencyStagingData) getBusinessObject();
	    //since it is fixed an submitted, changing the status to OK
	    agencyStaging.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        super.saveBusinessObject();
	}  

	/**
	 * 
	 * This method trims the descriptionText to 40 characters.
	 * @param descriptionText
	 * @return
	 */
	protected String trimDescription(String descriptionText) {
        return StringUtils.substring(descriptionText, 0, 39);
	}

}
