/*
 * Copyright 2007-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.integration.cg;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Methods which allow core KFS modules to interact with the ContractsAndGrants module.
 */
public interface ContractsAndGrantsModuleRetrieveService {


    /**
     * This method would return list of business object - in this case Awards for CG Invoice On Demand functionality in AR.
     * 
     * @param fieldValues
     * @param unbounded
     * @return
     */
    public List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded);


    /**
     * This method retrieves awards for Invoice On Demand in AR.
     * 
     * @param lookupResultsSequenceNumber
     * @param personId
     * @return
     */
    public Collection<ContractsAndGrantsBillingAward> getAwardsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId);

    /**
     * Determines whether the CG and Billing Enhancements are on from the system parameters
     * 
     * @return true if parameter ENABLE_CG_BILLING_ENHANCEMENTS_IND is set to "Y"; otherwise false.
     */
    public boolean isContractsGrantsBillingEnhancementsActive();

    /**
     * This method checks the Contract Control account set for Award Account based on award's invoicing option.
     * 
     * @return errorString
     */
    public List<String> hasValidContractControlAccounts(Long proposalNumber);
}