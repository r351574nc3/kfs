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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceTemplate;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
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

/**
 * Helper service class for Invoice Template lookup
 */
public class InvoiceTemplateLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    protected KualiModuleService kualiModuleService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected FinancialSystemUserService financialSystemUserService;

    /***
     * This method was overridden to remove the COPY link from the actions and to add in the REPORT link.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        InvoiceTemplate invoiceTemplate = (InvoiceTemplate) businessObject;
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        boolean isValid = false;
        final Person currentUser = GlobalVariables.getUserSession().getPerson();

        // check for KFS-SYS User Role's membership
        final ChartOrgHolder chartOrg = getFinancialSystemUserService().getPrimaryOrganization(currentUser, ArConstants.AR_NAMESPACE_CODE);

        if (ObjectUtils.isNotNull(invoiceTemplate.getBillByChartOfAccountCode()) && ObjectUtils.isNotNull(invoiceTemplate.getBilledByOrganizationCode())) {
            if (StringUtils.equals(invoiceTemplate.getBillByChartOfAccountCode(), chartOrg.getChartOfAccountsCode()) && StringUtils.equals(invoiceTemplate.getBilledByOrganizationCode(), chartOrg.getOrganizationCode())) {
                isValid = true;
            }
        }

        if (isValid) {
            if (StringUtils.isNotBlank(getMaintenanceDocumentTypeName()) && allowsMaintenanceEditAction(businessObject)) {
                htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
            }
            if (allowsMaintenanceNewOrCopyAction()) {
                htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
            }
            if (getContractsGrantsInvoiceDocumentService().isTemplateValidForUser(invoiceTemplate, currentUser)) {
                htmlDataList.add(getInvoiceTemplateUploadUrl(businessObject));
            }
            if (StringUtils.isNotBlank(invoiceTemplate.getFilename()) && templateFileExists(invoiceTemplate.getFilename())) {
                htmlDataList.add(getInvoiceTemplateDownloadUrl(businessObject));
            }
        }
        return htmlDataList;
    }

    /**
     * This method helps in uploading the invoice templates.
     *
     * @param bo
     * @return
     */
    protected AnchorHtmlData getInvoiceTemplateUploadUrl(BusinessObject bo) {
        InvoiceTemplate invoiceTemplate = (InvoiceTemplate) bo;
        String href = "../arAccountsReceivableInvoiceTemplateUpload.do" + "?&methodToCall=start&invoiceTemplateCode=" + invoiceTemplate.getInvoiceTemplateCode() + "&docFormKey=88888888";
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
     * This method helps in downloading the invoice templates.
     *
     * @param bo
     * @return
     */
    protected AnchorHtmlData getInvoiceTemplateDownloadUrl(BusinessObject bo) {
        InvoiceTemplate invoiceTemplate = (InvoiceTemplate) bo;
        Properties parameters = new Properties();
        String fileName = invoiceTemplate.getFilename();
        if (ObjectUtils.isNotNull(fileName) && templateFileExists(fileName)) {
            parameters.put("fileName", fileName);
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "download");
        }
        String href = UrlFactory.parameterizeUrl("../arAccountsReceivableInvoiceTemplateUpload.do", parameters);
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

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    public FinancialSystemUserService getFinancialSystemUserService() {
        return financialSystemUserService;
    }

    public void setFinancialSystemUserService(FinancialSystemUserService financialSystemUserService) {
        this.financialSystemUserService = financialSystemUserService;
    }
}
