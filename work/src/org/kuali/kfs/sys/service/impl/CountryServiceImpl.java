/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Country;
import org.kuali.kfs.sys.service.CountryService;
import org.kuali.rice.kns.service.BusinessObjectService;

public class CountryServiceImpl implements CountryService {
    private static Logger LOG = Logger.getLogger(CountryServiceImpl.class);

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.sys.service.CountryService#getByPrimaryId(java.lang.String)
     */
    public Country getByPrimaryId(String postalCountryCode) {
        if (StringUtils.isBlank(postalCountryCode)) {
            LOG.info("The postalCountryCode cannot be empty String.");
            return null;
        }

        Map<String, String> postalCountryMap = new HashMap<String, String>();
        postalCountryMap.put(KFSPropertyConstants.POSTAL_COUNTRY_CODE, postalCountryCode);

        return (Country) businessObjectService.findByPrimaryKey(Country.class, postalCountryMap);
    }

    /**
     * @see org.kuali.kfs.sys.service.CountryService#getByPrimaryIdIfNecessary(java.lang.String,
     *      org.kuali.kfs.sys.businessobject.Country)
     */
    public Country getByPrimaryIdIfNecessary(String postalCountryCode, Country existingCountry) {
        if (existingCountry != null) {
            if (StringUtils.equals(postalCountryCode, existingCountry.getPostalCountryCode())) {
                return existingCountry;
            }
        }

        return this.getByPrimaryId(postalCountryCode);
    }

    /**
     * @see org.kuali.kfs.sys.service.CountryService#findAllCountries()
     */
    public List<Country> findAllCountries() {
        return (List<Country>) businessObjectService.findAll(Country.class);
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
