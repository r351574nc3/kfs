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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.document.validation.MaintenanceRuleTestBase;

/**
 * This class tests the business rules for the OrganizationAccountingDefault Maint. Doc.
 */
@ConfigureContext(session = khuntley)
public class OrganizationAccountingDefaultRuleTest extends MaintenanceRuleTestBase {
    
    OrganizationAccountingDefault organizationAccountingDefault;
    
    private static String EXPENSE_OBJECT_CODE = "3310";
    private static String INCOME_OBJECT_CODE = "0776";
    private static String CHART_CODE = "IU";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        organizationAccountingDefault = new OrganizationAccountingDefault();
        organizationAccountingDefault.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
        organizationAccountingDefault.setChartOfAccountsCode(CHART_CODE);
    }
    
    /**
     * This method tests if the isWriteOffObjectValidExpense rule returns true when write off object is set to an expense object code
     */
    /*
    public void testIsWriteOffObjectValidExpense_True(){
        
        organizationAccountingDefault.setWriteoffFinancialObjectCode(EXPENSE_OBJECT_CODE);
        organizationAccountingDefault.refreshReferenceObject("writeoffObject");
        OrganizationAccountingDefaultRule rule = (OrganizationAccountingDefaultRule) setupMaintDocRule(newMaintDoc(organizationAccountingDefault), OrganizationAccountingDefaultRule.class);
        
        boolean result = rule.isWriteOffObjectValidExpense(organizationAccountingDefault);
        assertEquals( "When write off object code is " + EXPENSE_OBJECT_CODE + ", isWriteOffObjectValidExpense should return true. ", true, result );
    }*/
    
    /**
     * This method tests if the isWriteOffObjectValidExpense rule returns false when write off object is set to an income object code
     */
    /*
    public void testIsWriteOffObjectValidExpense_False(){
        organizationAccountingDefault.setWriteoffFinancialObjectCode(INCOME_OBJECT_CODE);
        organizationAccountingDefault.refreshReferenceObject("writeoffObject");
        OrganizationAccountingDefaultRule rule = (OrganizationAccountingDefaultRule) setupMaintDocRule(newMaintDoc(organizationAccountingDefault), OrganizationAccountingDefaultRule.class);
        
        boolean result = rule.isWriteOffObjectValidExpense(organizationAccountingDefault);
        assertEquals( "When write off object code is " + INCOME_OBJECT_CODE + ", isWriteOffObjectValidExpense should return false. ", false, result );
        
    }*/
    
    /**
     * This method tests if the isLateChargeObjectValidIncome rule returns true when late charge object is set to an income object code
     */
    public void testIsLateChargeObjectValidIncome_True(){
        organizationAccountingDefault.setOrganizationLateChargeObjectCode(INCOME_OBJECT_CODE);
        organizationAccountingDefault.refreshReferenceObject("organizationLateChargeObject");
        OrganizationAccountingDefaultRule rule = (OrganizationAccountingDefaultRule) setupMaintDocRule(newMaintDoc(organizationAccountingDefault), OrganizationAccountingDefaultRule.class);
        
        boolean result = rule.isLateChargeObjectValidIncome(organizationAccountingDefault);
        assertEquals( "When late charge object code is " + INCOME_OBJECT_CODE + ", isLateChargeObjectValidIncome should return true. ", true, result );        
    }

    /**
     * This method tests if the isLateChargeObjectValidIncome rule returns false when late charge object is set to an expense object code
     */
    public void testIsLateChargeObjectValidIncome_False(){
        organizationAccountingDefault.setOrganizationLateChargeObjectCode(EXPENSE_OBJECT_CODE);
        organizationAccountingDefault.refreshReferenceObject("organizationLateChargeObject");
        OrganizationAccountingDefaultRule rule = (OrganizationAccountingDefaultRule) setupMaintDocRule(newMaintDoc(organizationAccountingDefault), OrganizationAccountingDefaultRule.class);
        
        boolean result = rule.isLateChargeObjectValidIncome(organizationAccountingDefault);
        assertEquals( "When late charge object code is " + EXPENSE_OBJECT_CODE + ", isLateChargeObjectValidIncome should return false. ", false, result );  
        
    }
    
    /**
     * This method tests if the isDefaultInvoiceFinancialObjectValidIncome rule returns true when default invoice financial object is set to an income object code
     */
    public void testIsDefaultInvoiceFinancialObjectValidIncome_True(){
        organizationAccountingDefault.setDefaultInvoiceFinancialObjectCode(INCOME_OBJECT_CODE);
        organizationAccountingDefault.setDefaultInvoiceChartOfAccountsCode(CHART_CODE);
        organizationAccountingDefault.refreshReferenceObject("defaultInvoiceFinancialObject");
        OrganizationAccountingDefaultRule rule = (OrganizationAccountingDefaultRule) setupMaintDocRule(newMaintDoc(organizationAccountingDefault), OrganizationAccountingDefaultRule.class);
        
        boolean result = rule.isDefaultInvoiceFinancialObjectValidIncome(organizationAccountingDefault);
        assertEquals( "When default invoice financial  is " + INCOME_OBJECT_CODE + ", isDefaultInvoiceFinancialObjectValidIncome should return true. ", true, result );        
        
    }
    
    /**
     * This method tests if the isDefaultInvoiceFinancialObjectValidIncome rule returns false when default invoice financial object is set to an expense object code
     */
    public void testIsDefaultInvoiceFinancialObjectValidIncome_False(){
        organizationAccountingDefault.setDefaultInvoiceFinancialObjectCode(EXPENSE_OBJECT_CODE);
        organizationAccountingDefault.setDefaultInvoiceChartOfAccountsCode(CHART_CODE);
        organizationAccountingDefault.refreshReferenceObject("defaultInvoiceFinancialObject");
        OrganizationAccountingDefaultRule rule = (OrganizationAccountingDefaultRule) setupMaintDocRule(newMaintDoc(organizationAccountingDefault), OrganizationAccountingDefaultRule.class);
        
        boolean result = rule.isDefaultInvoiceFinancialObjectValidIncome(organizationAccountingDefault);
        assertEquals( "When default invoice financial object code is " + EXPENSE_OBJECT_CODE + ", isDefaultInvoiceFinancialObjectValidIncome should return false. ", false, result );  
    }

}

