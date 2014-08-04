/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.module.ar.document.service.DunningLetterDistributionService;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

public class DunningLetterTemplateLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    protected KualiModuleService kualiModuleService;
    protected DunningLetterDistributionService dunningLetterDistributionService;

    /***
     * This method was overridden to remove the COPY link from the actions and to add in the REPORT link.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        final Person currentUser = GlobalVariables.getUserSession().getPerson();

        DunningLetterTemplate letterTemplate = (DunningLetterTemplate) businessObject;
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        if (StringUtils.isNotBlank(getMaintenanceDocumentTypeName()) && allowsMaintenanceEditAction(businessObject)) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        if (allowsMaintenanceNewOrCopyAction()) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
        }
        if (getDunningLetterDistributionService().isValidOrganizationForTemplate(letterTemplate, currentUser) || !letterTemplate.isAccessRestrictedInd()) {
            // can upload file and do changes.
            htmlDataList.add(getDunningLetterTemplateUploadUrl(businessObject));
        }
        if (StringUtils.isNotBlank(letterTemplate.getFilename()) && templateFileExists(letterTemplate.getFilename())) {
            htmlDataList.add(getDunningLetterTemplateDownloadUrl(businessObject));
        }
        return htmlDataList;
    }

    /**
     * This method helps in uploading the dunning letter templates.
     *
     * @param bo
     * @return
     */
    protected AnchorHtmlData getDunningLetterTemplateUploadUrl(BusinessObject bo) {
        DunningLetterTemplate letterTemplate = (DunningLetterTemplate) bo;
        Properties params = new Properties();
        params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        params.put(ArPropertyConstants.DunningLetterTemplateFields.LETTER_TEMPLATE_CODE, letterTemplate.getLetterTemplateCode());
        params.put(KFSConstants.DOC_FORM_KEY, "88888888");
        String href = UrlFactory.parameterizeUrl(getKualiConfigurationService().getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)+ "/" + ArConstants.UrlActions.ACCOUNTS_RECEIVABLE_LETTER_TEMPLATE_UPLOAD, params);
        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, ArConstants.UPLOAD_METHOD);
    }

    /**
     * Check if the template file actually exists, even though we have a fileName
     *
     * @param fileName name of template file
     * @return true if template file exists, false otherwise
     */
    protected boolean templateFileExists(String fileName) {
        ModuleConfiguration systemConfiguration = kualiModuleService.getModuleServiceByNamespaceCode(KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE).getModuleConfiguration();
        String templateFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getTemplateFileDirectories().get(KFSConstants.TEMPLATES_DIRECTORY_KEY);
        String filePath = templateFolderPath + File.separator + fileName;
        File file = new File(filePath).getAbsoluteFile();
        return file.exists() && file.isFile();
    }

    /**
     * This method helps in downloading the dunning letter templates.
     *
     * @param bo
     * @return
     */
    protected AnchorHtmlData getDunningLetterTemplateDownloadUrl(BusinessObject bo) {
        DunningLetterTemplate letterTemplate = (DunningLetterTemplate) bo;
        Properties parameters = new Properties();
        String fileName = letterTemplate.getFilename();
        if (ObjectUtils.isNotNull(fileName) && templateFileExists(fileName)) {
            parameters.put(KFSPropertyConstants.FILE_NAME, fileName);
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, ArConstants.DOWNLOAD_METHOD);
        }
        String href = UrlFactory.parameterizeUrl(getKualiConfigurationService().getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)+ "/" + ArConstants.UrlActions.ACCOUNTS_RECEIVABLE_LETTER_TEMPLATE_UPLOAD, parameters);
        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, ArConstants.DOWNLOAD_METHOD);
    }

    /**
     * Gets the kualiModuleService attribute.
     *
     * @return Returns the kualiModuleService
    */

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    /**
     * Sets the kualiModuleService attribute.
     *
     * @param kualiModuleService The kualiModuleService to set.
     */
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public DunningLetterDistributionService getDunningLetterDistributionService() {
        return dunningLetterDistributionService;
    }

    public void setDunningLetterDistributionService(DunningLetterDistributionService dunningLetterDistributionService) {
        this.dunningLetterDistributionService = dunningLetterDistributionService;
    }
}