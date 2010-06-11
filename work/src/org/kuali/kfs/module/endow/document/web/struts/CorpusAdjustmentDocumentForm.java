/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.web.struts;

import org.kuali.kfs.module.endow.document.CorpusAdjustmentDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.kfs.module.endow.EndowConstants;

public class CorpusAdjustmentDocumentForm extends EndowmentTransactionLinesDocumentFormBase {

    public CorpusAdjustmentDocumentForm() {
        super();
        this.setSourceGroupLabelName(EndowConstants.CORPUS_ADJUSTMENT_SOURCE_TRANSACTION_LINE_GROUP_LABEL_NAME);
        this.setTargetGroupLabelName(EndowConstants.CORPUS_ADJUSTMENT_TARGET_TRANSACTION_LINE_GROUP_LABEL_NAME);
        // don't show these values on the edoc.
        this.setShowIncomeTotalAmount(false);
        this.setShowIncomeTotalUnits(false);
        this.setShowPrincipalTotalUnits(false);
        this.setFeildValueToPrincipal(true);
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        //use the DataDictionaryService service to get the document type name...
        return SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(CorpusAdjustmentDocument.class);
    }

    /**
     * This method gets the Corpus Adjustment document
     * 
     * @return the CorpusAdjustmentDocument
     */
    public CorpusAdjustmentDocument getCorpusAdjustmentDocument() {
        return (CorpusAdjustmentDocument) getDocument();
    }
    
    
}
