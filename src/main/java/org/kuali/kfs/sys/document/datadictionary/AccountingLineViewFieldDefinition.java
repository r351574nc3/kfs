/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.document.datadictionary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.DynamicNameLabelGenerator;
import org.kuali.kfs.sys.document.web.AccountingLineViewField;
import org.kuali.kfs.sys.document.web.AccountingLineViewOverrideField;
import org.kuali.kfs.sys.document.web.TableJoining;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.FieldBridge;

/**
 * Data dictionary definition of a field to be rendered as part of an accounting line view.
 */
public class AccountingLineViewFieldDefinition extends MaintainableFieldDefinition implements AccountingLineViewRenderableElementDefinition {
    private String dynamicLabelProperty;
    private boolean useShortLabel = false;
    private boolean hidden = false;
    private List<AccountingLineViewOverrideFieldDefinition> overrideFields;
    private String dynamicNameLabelGeneratorBeanName;
    private int overrideColSpan = -1;
    private Class<? extends AccountingLineViewField> accountingLineViewFieldClass = org.kuali.kfs.sys.document.web.AccountingLineViewField.class;
    private String overrideLookupParameters;
    protected boolean explodable;

    private DynamicNameLabelGenerator dynamicNameLabelGenerator;

    public boolean isExplodable() {
        return this.explodable;
    }

    public void setExplodable(boolean explodable) {
        this.explodable = explodable;
    }

    /**
     * Gets the dynamicLabelProperty attribute.
     * @return Returns the dynamicLabelProperty.
     */
    public String getDynamicLabelProperty() {
        return dynamicLabelProperty;
    }

    /**
     * Sets the dynamicLabelProperty attribute value.
     * @param dynamicLabelProperty The dynamicLabelProperty to set.
     */
    public void setDynamicLabelProperty(String dynamicLabelProperty) {
        this.dynamicLabelProperty = dynamicLabelProperty;
    }

    /**
     * Gets the useShortLabel attribute.
     * @return Returns the useShortLabel.
     */
    public boolean shouldUseShortLabel() {
        return useShortLabel;
    }

    /**
     * Sets the useShortLabel attribute value.
     * @param useShortLabel The useShortLabel to set.
     */
    public void setUseShortLabel(boolean useShortLabel) {
        this.useShortLabel = useShortLabel;
    }

    /**
     * Gets the hidden attribute.
     * @return Returns the hidden.
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Sets the hidden attribute value.
     * @param hidden The hidden to set.
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Gets the overrideFields attribute.
     * @return Returns the overrideFields.
     */
    public List<AccountingLineViewOverrideFieldDefinition> getOverrideFields() {
        return overrideFields;
    }

    /**
     * Sets the overrideFields attribute value.
     * @param overrideFields The overrideFields to set.
     */
    public void setOverrideFields(List<AccountingLineViewOverrideFieldDefinition> overrideFields) {
        this.overrideFields = overrideFields;
    }

    /**
     * Gets the dynamicNameLabelGeneratorBeanName attribute.
     * @return Returns the dynamicNameLabelGeneratorBeanName.
     */
    public String getDynamicNameLabelGeneratorBeanName() {
        return dynamicNameLabelGeneratorBeanName;
    }

    /**
     * Sets the dynamicNameLabelGeneratorBeanName attribute value.
     * @param dynamicNameLabelGeneratorBeanName The dynamicNameLabelGeneratorBeanName to set.
     */
    public void setDynamicNameLabelGeneratorBeanName(String dynamicNameLabelGeneratorBeanName) {
        this.dynamicNameLabelGeneratorBeanName = dynamicNameLabelGeneratorBeanName;
    }

    /**
     * Gets the overrideColSpan attribute.
     * @return Returns the overrideColSpan.
     */
    public int getOverrideColSpan() {
        return overrideColSpan;
    }

    /**
     * Sets the overrideColSpan attribute value.
     * @param overrideColSpan The overrideColSpan to set.
     */
    public void setOverrideColSpan(int overrideColSpan) {
        this.overrideColSpan = overrideColSpan;
    }

    /**
     * Gets the accountingLineViewFieldClass attribute.
     * @return Returns the accountingLineViewFieldClass.
     */
    public Class<? extends AccountingLineViewField> getAccountingLineViewFieldClass() {
        return accountingLineViewFieldClass;
    }

    /**
     * Sets the accountingLineViewFieldClass attribute value.
     * @param accountingLineViewFieldClass The accountingLineViewFieldClass to set.
     */
    public void setAccountingLineViewFieldClass(Class<? extends AccountingLineViewField> accountingLineViewFieldClass) {
        if (accountingLineViewFieldClass != null) {
            this.accountingLineViewFieldClass = accountingLineViewFieldClass;
        }
    }

    /**
     * Returns the dynamicNameLabelGenerator for this field definition, if it has one
     * @return an implementation of DynamicNameLabelGenerator to use for this field
     */
    public DynamicNameLabelGenerator getDynamicNameLabelGenerator() {
        if (!StringUtils.isBlank(dynamicNameLabelGeneratorBeanName) && dynamicNameLabelGenerator == null) {
            dynamicNameLabelGenerator = SpringContext.getBean(DynamicNameLabelGenerator.class,dynamicNameLabelGeneratorBeanName);
        }
        return dynamicNameLabelGenerator;
    }

    /**
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewRenderableElementDefinition#createLayoutElement()
     */
    @Override
    public TableJoining createLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        AccountingLineViewField layoutElement = getNewAccountingLineViewField();
        layoutElement.setDefinition(this);
        layoutElement.setField(getKNSFieldForDefinition(accountingLineClass));
        layoutElement.setOverrideFields(getFieldsForOverrideFields(layoutElement, accountingLineClass));
        layoutElement.setExplodable(this.isExplodable());
        return layoutElement;
    }

    /**
     * Creates a new instance of the accounting line view field class this definition uses
     * @return a new AccountingLineViewField instance or child class instance
     */
    protected AccountingLineViewField getNewAccountingLineViewField() {
        AccountingLineViewField layoutElement = null;
        try {
            layoutElement = (AccountingLineViewField)getAccountingLineViewFieldClass().newInstance();
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Could not instantiate instance of class "+getAccountingLineViewFieldClass().getName(), ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("IllegalAccessException while attempting to instantiate "+getAccountingLineViewFieldClass().getName(), iae);
        }
        return layoutElement;
    }

    /**
     * Creates a KNS Field for an AccountingLineViewField definition
     * @param accountingLineClass the class of the accounting line used by this definition
     * @return a properly initialized KNS field
     */
    public Field getKNSFieldForDefinition(Class<? extends AccountingLine> accountingLineClass) {
        Field realField = FieldUtils.getPropertyField(accountingLineClass, getName(), false);
        FieldBridge.setupField(realField, this, null);
        if (isHidden()) {
            realField.setFieldType(Field.HIDDEN);
        }
        if (shouldUseShortLabel()) {
            org.kuali.rice.krad.datadictionary.BusinessObjectEntry boEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(accountingLineClass.getName());
            realField.setFieldLabel(boEntry.getAttributeDefinition(getName()).getShortLabel());
        }
        return realField;
    }

    /**
     * For each defined override field within this definition, creates a Field and puts them together as a List
     * @param parentField the AccountingLineViewField which will own all of the override fields
     * @param accountingLineClass the class of accounting lines which will be rendered
     * @return a List of override fields, or if no override fields were defined, an empty List
     */
    protected List<AccountingLineViewOverrideField> getFieldsForOverrideFields(AccountingLineViewField parentField, Class<? extends AccountingLine> accountingLineClass) {
        List<AccountingLineViewOverrideField> fields = new ArrayList<AccountingLineViewOverrideField>();
        if (getOverrideFields() != null && getOverrideFields().size() > 0) {
            for (AccountingLineViewOverrideFieldDefinition overrideFieldDefinition : getOverrideFields()) {
                fields.add(overrideFieldDefinition.getOverrideFieldForDefinition(parentField, accountingLineClass));
            }
        }
        return fields;
    }

    /**
     * Gets the overrideLookupParameters attribute.
     * @return Returns the overrideLookupParameters.
     */
    public String getOverrideLookupParameters() {
        return overrideLookupParameters;
    }

    /**
     * Sets the overrideLookupParameters attribute value.
     * @param overrideLookupParameters The overrideLookupParameters to set.
     */
    public void setOverrideLookupParameters(String overrideLookupParameters) {
        this.overrideLookupParameters = overrideLookupParameters;
    }
}
