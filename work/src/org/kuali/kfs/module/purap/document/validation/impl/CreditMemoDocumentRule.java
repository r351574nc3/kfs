/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.rules;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.CREDIT_MEMO_TYPES;
import org.kuali.module.purap.bo.CreditMemoItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.rule.CalculateAccountsPayableRule;
import org.kuali.module.purap.rule.ContinueAccountsPayableRule;
import org.kuali.module.purap.rule.PreCalculateAccountsPayableRule;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.util.VendorUtils;


/**
 * Business rules for the Credit Memo Document.
 */
public class CreditMemoDocumentRule extends AccountsPayableDocumentRuleBase implements ContinueAccountsPayableRule, CalculateAccountsPayableRule, PreCalculateAccountsPayableRule {

    /**
     * Validation that occurs on Route of the document.
     * 
     * @param purapDocument - Credit Memo Document Instance
     * @return boolean - true if validation was ok, false if there were errors
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);

        CreditMemoDocument cmDocument = (CreditMemoDocument) purapDocument;

        valid = processItemValidation(cmDocument);

        valid = valid && validateTotalMatchesVendorAmount(cmDocument);
        valid = valid && validateTotalOverZero(cmDocument);

        return valid;
    }

    /**
     * Validation that occurs when the continue action is selected from the initial screen.
     * 
     * @param document - Credit Memo Document Instance
     * @return boolean - true if validation was ok, false if there were errors
     */
    public boolean processContinueAccountsPayableBusinessRules(AccountsPayableDocument document) {
        boolean valid = true;

        CreditMemoDocument cmDocument = (CreditMemoDocument) document;
        valid = validateInitTabRequiredFields(cmDocument);

        if (valid) {
            valid = validatInitTabReferenceNumbers(cmDocument);
        }

        if (valid && StringUtils.equals(cmDocument.getCreditMemoType(), CREDIT_MEMO_TYPES.TYPE_PO)) {
            valid = checkPurchaseOrderForInvoicedItems(cmDocument);
        }

        return valid;
    }

    /**
     * Validates extended price field and cm totals after a calculation has been performed.
     * 
     * @see org.kuali.module.purap.rule.CalculateAccountsPayableRule#processCalculateAccountsPayableBusinessRules(org.kuali.module.purap.document.AccountsPayableDocument)
     */
    public boolean processCalculateAccountsPayableBusinessRules(AccountsPayableDocument document) {
        boolean valid = true;
        CreditMemoDocument cmDocument = (CreditMemoDocument) document;

        // flag line just gives warnings
        flagLineItemTotals(cmDocument.getItems());

        valid = validateTotalMatchesVendorAmount(cmDocument);
        valid = valid && validateTotalOverZero(cmDocument);

        return valid;
    }


    /**
     * Validates item fields are valid for the calculation process.
     * 
     * @see org.kuali.module.purap.rule.PreCalculateAccountsPayableRule#processPreCalculateAccountsPayableBusinessRules(org.kuali.module.purap.document.AccountsPayableDocument)
     */
    public boolean processPreCalculateAccountsPayableBusinessRules(AccountsPayableDocument document) {
        boolean valid = true;
        CreditMemoDocument cmDocument = (CreditMemoDocument) document;

        valid = processItemValidation(cmDocument);

        return valid;
    }

    /**
     * Validates the necessary fields on the init tab were given and credit memo date is valid. (NOTE: formats for cm date and
     * number already performed by pojo conversion)
     * 
     * @param cmDocument - credit memo document which contains the fields that need checked
     * @return boolean - true if validation was ok, false if there were errors
     */
    protected boolean validateInitTabRequiredFields(CreditMemoDocument cmDocument) {
        boolean valid = true;

        valid = validateRequiredField(cmDocument, PurapPropertyConstants.CREDIT_MEMO_NUMBER);
        valid = valid && validateRequiredField(cmDocument, PurapPropertyConstants.CREDIT_MEMO_AMOUNT);
        boolean creditMemoDateExist = validateRequiredField(cmDocument, PurapPropertyConstants.CREDIT_MEMO_DATE);

        if (creditMemoDateExist) {
            if (SpringServiceLocator.getPaymentRequestService().isInvoiceDateAfterToday(cmDocument.getCreditMemoDate())) {
                String label = SpringServiceLocator.getDataDictionaryService().getAttributeErrorLabel(CreditMemoDocument.class, PurapPropertyConstants.CREDIT_MEMO_DATE);
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_DATE, PurapKeyConstants.ERROR_INVALID_INVOICE_DATE, label);
                valid = false;
            }
        }

        return valid;

    }

    /**
     * Validates only one of preq, po, or vendor number was given. Then validates the existence of that number.
     * 
     * @param cmDocument - credit memo document which contains init reference numbers
     * @return boolean - true if validation was ok, false if there were errors
     */
    protected boolean validatInitTabReferenceNumbers(CreditMemoDocument cmDocument) {
        boolean valid = true;

        if (!(ObjectUtils.isNotNull(cmDocument.getPaymentRequestIdentifier()) ^ StringUtils.isNotEmpty(cmDocument.getVendorNumber()) ^ ObjectUtils.isNotNull(cmDocument.getPurchaseOrderIdentifier())) || (ObjectUtils.isNotNull(cmDocument.getPaymentRequestIdentifier()) && StringUtils.isNotEmpty(cmDocument.getVendorNumber()) && ObjectUtils.isNotNull(cmDocument.getPurchaseOrderIdentifier()))) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_INIT_REQUIRED_FIELDS, PurapKeyConstants.ERROR_CREDIT_MEMO_REQUIRED_FIELDS);
            return false;
        }

        // Make sure PREQ is valid if entered
        Integer preqNumber = cmDocument.getPaymentRequestIdentifier();
        if (ObjectUtils.isNotNull(preqNumber)) {
            PaymentRequestDocument preq = SpringServiceLocator.getPaymentRequestService().getPaymentRequestById(preqNumber);
            if (ObjectUtils.isNull(preq)) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_PAYMENT_REQUEST_ID, PurapKeyConstants.ERROR_CREDIT_MEMO_PAYMENT_REQEUEST_INVALID, preqNumber.toString());
                valid = false;
            }
            else if ((PurapConstants.PaymentRequestStatuses.IN_PROCESS.equals(preq.getStatus().getStatusCode())) || (PurapConstants.PaymentRequestStatuses.CANCELLED_POST_APPROVE.equals(preq.getStatus().getStatusCode()) || (PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS.equals(preq.getStatus().getStatusCode())))) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_PAYMENT_REQUEST_ID, PurapKeyConstants.ERROR_CREDIT_MEMO_PAYMENT_REQEUEST_INVALID_SATATUS, preqNumber.toString());
                valid = false;
            }
        }

        // Make sure PO # is valid if entered
        Integer purchaseOrderID = cmDocument.getPurchaseOrderIdentifier();
        if (ObjectUtils.isNotNull(purchaseOrderID)) {
            PurchaseOrderDocument purchaseOrder = SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(purchaseOrderID);
            if (ObjectUtils.isNull(purchaseOrder)) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_CREDIT_MEMO_PURCHASE_ORDER_INVALID, purchaseOrderID.toString());
                valid = false;
            }
            else if (!StringUtils.equals(purchaseOrder.getStatusCode(), PurapConstants.PurchaseOrderStatuses.OPEN)) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_CREDIT_MEMO_PURCAHSE_ORDER_INVALID_STATUS, purchaseOrderID.toString());
                valid = false;
            }
        }

        // Make sure vendorNumber is valid if entered
        String vendorNumber = cmDocument.getVendorNumber();
        if (StringUtils.isNotEmpty(vendorNumber)) {
            VendorDetail vendor = SpringServiceLocator.getVendorService().getVendorDetail(VendorUtils.getVendorHeaderId(vendorNumber), VendorUtils.getVendorDetailId(vendorNumber));
            if (ObjectUtils.isNull(vendor)) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_VENDOR_NUMBER, PurapKeyConstants.ERROR_CREDIT_MEMO_VENDOR_NUMBER_INVALID, vendorNumber);
                valid = false;
            }
        }

        return valid;
    }


    /**
     * Validates item lines for the document. Checks numeric fields to verify they are positive and compares with source quantity
     * and price.
     * 
     * @param cmDocument - credit memo document which contains the po reference
     * @return boolean - true if validation was ok, false if there were errors
     */
    @Override
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;

        CreditMemoDocument cmDocument = (CreditMemoDocument) purapDocument;

        List itemList = cmDocument.getItems();
        for (int i = 0; i < itemList.size(); i++) {
            CreditMemoItem item = (CreditMemoItem) itemList.get(i);
            String errorKeyPrefix = PropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + Integer.toString(i) + "].";

            valid = validateItemQuantity(cmDocument, item, errorKeyPrefix + PurapPropertyConstants.QUANTITY);
            valid = valid && validateItemUnitPrice(cmDocument, item, errorKeyPrefix + PurapPropertyConstants.ITEM_UNIT_PRICE);
            valid = valid && validateItemExtendedPrice(cmDocument, item, errorKeyPrefix + PurapPropertyConstants.EXTENDED_PRICE);
        }

        return valid;
    }


    /**
     * Validates the credit memo quantity for an item line.
     * 
     * @param cmDocument - credit memo document
     * @param item - credit memo item
     * @param errorKey - key to associate any generated errors with
     * @return boolean - true if quantity is valid, false if invalid
     */
    public boolean validateItemQuantity(CreditMemoDocument cmDocument, CreditMemoItem item, String errorKey) {
        boolean valid = true;

        if (item.getItemQuantity() != null) {
            if (item.getItemQuantity().isNegative()) {
                String label = SpringServiceLocator.getDataDictionaryService().getAttributeErrorLabel(CreditMemoItem.class, PurapPropertyConstants.QUANTITY);
                GlobalVariables.getErrorMap().putError(errorKey, PurapKeyConstants.ERROR_CREDIT_MEMO_ITEM_AMOUNT_NONPOSITIVE, label);
                valid = false;
            }

            // check cm quantity is not greater than invoiced quantity
            KualiDecimal invoicedQuantity = getSourceTotalInvoiceQuantity(cmDocument, item);
            if (item.getItemQuantity().isGreaterThan(invoicedQuantity)) {
                GlobalVariables.getErrorMap().putError(errorKey, PurapKeyConstants.ERROR_CREDIT_MEMO_ITEM_QUANTITY_TOOMUCH);
                valid = false;
            }
        }
        else {
            // check if quantity should be required
            KualiDecimal invoicedQuantity = getSourceTotalInvoiceQuantity(cmDocument, item);
            if (item.getItemType().isQuantityBasedGeneralLedgerIndicator() && invoicedQuantity.isGreaterThan(KualiDecimal.ZERO) && item.getExtendedPrice().isGreaterThan(KualiDecimal.ZERO)) {
                String label = SpringServiceLocator.getDataDictionaryService().getAttributeErrorLabel(CreditMemoItem.class, PurapPropertyConstants.QUANTITY);
                GlobalVariables.getErrorMap().putError(errorKey, KeyConstants.ERROR_REQUIRED, label);
                valid = false;
            }
        }

        return valid;
    }


    /**
     * Validates the credit memo unit price for an item line.
     * 
     * @param cmDocument - credit memo document
     * @param item - credit memo item
     * @param errorKey - key to associate any generated errors with
     * @return boolean - true if quantity is valid, false if invalid
     */
    public boolean validateItemUnitPrice(CreditMemoDocument cmDocument, CreditMemoItem item, String errorKey) {
        boolean valid = true;

        if (item.getItemUnitPrice() != null) {
            // verify unit price is not negative
            if (item.getItemUnitPrice().signum() == -1) {
                String label = SpringServiceLocator.getDataDictionaryService().getAttributeErrorLabel(CreditMemoItem.class, PurapPropertyConstants.ITEM_UNIT_PRICE);
                GlobalVariables.getErrorMap().putError(errorKey, PurapKeyConstants.ERROR_CREDIT_MEMO_ITEM_AMOUNT_NONPOSITIVE, label);
                valid = false;
            }

            // if item type is misc check description was given
            if (StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CODE, item.getItemTypeCode()) && StringUtils.isBlank(item.getItemDescription())) {
                GlobalVariables.getErrorMap().putError(errorKey, PurapKeyConstants.ERROR_CREDIT_MEMO_ITEM_MISCDESCRIPTION);
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Validates the credit memo extended price for an item line.
     * 
     * @param cmDocument - credit memo document
     * @param item - credit memo item
     * @param errorKey - key to associate any generated errors with
     * @return boolean - true if quantity is valid, false if invalid
     */
    public boolean validateItemExtendedPrice(CreditMemoDocument cmDocument, CreditMemoItem item, String errorKey) {
        boolean valid = true;

        if (item.getExtendedPrice() != null) {
            if (item.getExtendedPrice().isNegative()) {
                String label = SpringServiceLocator.getDataDictionaryService().getAttributeErrorLabel(CreditMemoItem.class, PurapPropertyConstants.EXTENDED_PRICE);
                GlobalVariables.getErrorMap().putError(errorKey, PurapKeyConstants.ERROR_CREDIT_MEMO_ITEM_AMOUNT_NONPOSITIVE, label);
                valid = false;
            }

            // check cm extended price is not greater than total invoiced amount
            KualiDecimal invoicedAmount = null;
            if (StringUtils.equals(PurapConstants.CREDIT_MEMO_TYPES.TYPE_PO, cmDocument.getCreditMemoType())) {
                invoicedAmount = item.getPoExtendedPrice();
            }
            else {
                invoicedAmount = item.getPreqExtendedPrice();
            }

            if (item.getExtendedPrice().isGreaterThan(invoicedAmount)) {
                GlobalVariables.getErrorMap().putError(errorKey, PurapKeyConstants.ERROR_CREDIT_MEMO_ITEM_EXTENDEDPRICE_TOOMUCH);
                valid = false;
            }

        }

        return valid;
    }


    /**
     * Returns the total invoiced quantity for the item line based on the type of credit memo.
     * 
     * @param cmDocument - credit memo document
     * @param item - credit memo item line to return total invoice quantity
     * @return KualiDecimal - total invoiced quantity
     */
    private KualiDecimal getSourceTotalInvoiceQuantity(CreditMemoDocument cmDocument, CreditMemoItem item) {
        KualiDecimal invoicedQuantity = null;

        if (StringUtils.equals(PurapConstants.CREDIT_MEMO_TYPES.TYPE_PO, cmDocument.getCreditMemoType())) {
            invoicedQuantity = item.getPoInvoicedTotalQuantity();
        }
        else {
            invoicedQuantity = item.getPreqInvoicedTotalQuantity();
        }

        return invoicedQuantity;
    }

    /**
     * Verifies the purchase order for the credit memo has at least one invoiced item. If no invoiced items are found, a credit memo
     * cannot be processed against the document.
     * 
     * @param cmDocument - credit memo document which contains the po reference
     * @return boolean - true if validation was ok, false if there were errors
     */
    protected boolean checkPurchaseOrderForInvoicedItems(CreditMemoDocument cmDocument) {
        boolean hasInvoicedItems = true;

        PurchaseOrderDocument poDocument = SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(cmDocument.getPurchaseOrderIdentifier());
        List<PurchaseOrderItem> invoicedItems = SpringServiceLocator.getCreditMemoService().getPOInvoicedItems(poDocument);

        if (invoicedItems == null || invoicedItems.isEmpty()) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_CREDIT_MEMO_PURCAHSE_ORDER_NOITEMS);
            hasInvoicedItems = false;
        }

        return hasInvoicedItems;
    }

    /**
     * Helper method to perform required field checks add error messages if the validation fails. Adds an error required to
     * GlobalVariables.errorMap using the given fieldName as the error key and retrieving the error label from the data dictionary
     * for the error required message param.
     * 
     * @param businessObject - Business object to check for value
     * @param fieldName - Name of the property in the business object
     */
    private boolean validateRequiredField(BusinessObject businessObject, String fieldName) {
        boolean valid = true;

        Object fieldValue = ObjectUtils.getPropertyValue(businessObject, fieldName);
        if (fieldValue == null || (fieldValue instanceof String && StringUtils.isBlank(fieldName))) {
            String label = SpringServiceLocator.getDataDictionaryService().getAttributeErrorLabel(businessObject.getClass(), fieldName);
            GlobalVariables.getErrorMap().putError(fieldName, KFSKeyConstants.ERROR_REQUIRED, label);
            valid = false;
        }

        return valid;
    }

    /**
     * Validates the credit memo total matches the vendor credit memo amount
     * 
     * @param cmDocument - credit memo document
     * @return boolean - true if amounts match, false if they do not match
     */
    private boolean validateTotalMatchesVendorAmount(CreditMemoDocument cmDocument) {
        boolean valid = true;

        if (cmDocument.getGrandTotal().compareTo(cmDocument.getCreditMemoAmount()) != 0) {
            GlobalVariables.getErrorMap().putError(PropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM, PurapKeyConstants.ERROR_CREDIT_MEMO_INVOICE_AMOUNT_NONMATCH);
            valid = false;
        }

        return valid;
    }

    /**
     * Validates the credit memo total is over zero.
     * 
     * @param cmDocument - credit memo document
     * @return boolean - true if amount is over zero, false if not
     */
    private boolean validateTotalOverZero(CreditMemoDocument cmDocument) {
        boolean valid = true;

        if (!cmDocument.getGrandTotal().isPositive()) {
            GlobalVariables.getErrorMap().putError(PropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM, PurapKeyConstants.ERROR_CREDIT_MEMO_TOTAL_ZERO);
            valid = false;
        }

        return valid;
    }

    /**
     * Compares the extended price of each item to the calculated price and if different adds a warning message.
     * 
     * @param itemList - list of items to check
     */
    private void flagLineItemTotals(List<PurchasingApItem> itemList) {
        for (int i = 0; i < itemList.size(); i++) {
            CreditMemoItem item = (CreditMemoItem) itemList.get(i);
            if (item.getItemQuantity() != null && item.calculateExtendedPrice().compareTo(item.getExtendedPrice()) != 0) {
                String errorKey = PropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + Integer.toString(i) + "]." + PurapPropertyConstants.EXTENDED_PRICE;
                GlobalVariables.getErrorMap().putError(errorKey, PurapKeyConstants.ERROR_PAYMENT_REQUEST_ITEM_TOTAL_NOT_EQUAL);
            }
        }
    }
}
