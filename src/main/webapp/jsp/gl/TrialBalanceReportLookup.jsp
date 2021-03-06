<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<!-- ASR-1212 Trial Balance -->
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<c:set var="trialBalanceReportAttributes" value="${DataDictionary.TrialBalanceReport.attributes}" />
	
<kul:page  lookup="true" showDocumentInfo="false" 
	headerTitle="Lookup" docTitle="" headerMenuBar="${KualiForm.lookupable.htmlMenuBar}"
	transactionalDocument="false" htmlFormAction="glTrialBalance">

	<div class="headerarea-small" id="headerarea-small">
	<h1><c:out value="${KualiForm.lookupable.title}" /> <kul:help parameterName="TB_LOOKUP_HELP" altText="lookup help" /></h1>
	</div>

	<kul:enterKey methodToCall="search" />

	<html-el:hidden name="KualiForm" property="backLocation" />
	<html-el:hidden name="KualiForm" property="formKey" />
	<html-el:hidden name="KualiForm" property="lookupableImplServiceName" />
	<html-el:hidden name="KualiForm" property="businessObjectClassName" />
	<html-el:hidden name="KualiForm" property="conversionFields" />
	<html-el:hidden name="KualiForm" property="hideReturnLink" />

	<kul:errors errorTitle="Errors found in Search Criteria:" />

	<table width="100%">
		<tr>
			<td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20"
				height="20" /></td>
			<td>
			<div id="lookup" align="center"><br />
			<br />
			<table class="datatable-100" align="center" cellpadding="0"	cellspacing="0">
				<c:set var="FormName" value="KualiForm" scope="request" />
				<c:set var="FieldRows" value="${KualiForm.lookupable.rows}"	scope="request" />
				<c:set var="ActionName" value="glTrialBalance.do" scope="request" />
				<c:set var="IsLookupDisplay" value="true" scope="request" />

				<kul:rowDisplay rows="${KualiForm.lookupable.rows}" skipTheOldNewBar="true" numberOfColumns="1"/>

				<tr align=center>
					<td height="30" colspan=2 class="infoline">
					<html:image
						property="methodToCall.search" value="search"
						src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_search.gif" styleClass="tinybutton"
						alt="search" title="search" border="0" /> 
					<html:image
						property="methodToCall.clearValues" value="clearValues"
						src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_clear.gif" styleClass="tinybutton"
						alt="clear" title="clear" border="0" /> 
					<c:if test="${KualiForm.formKey!=''}">
						<a
							href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}" />'>
						<img src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" class="tinybutton"
							border="0" alt="cancel" title="cancel" /> </a>
					</c:if><!-- Optional extra button -->
					<html:image
						property="methodToCall.print" value="print"
						src="${ConfigProperties.externalizable.images.url}buttonsmall_genprintfile.gif" styleClass="tinybutton"
						alt="print" title="print" border="0" /> 
					<c:if test="${not empty KualiForm.lookupable.extraButtonSource}">
						<a
							href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&refreshCaller=org.kuali.rice.kns.lookup.KualiLookupableImpl&docFormKey=${KualiForm.formKey}" /><c:out value="${KualiForm.lookupable.extraButtonParams}" />'  title="cancel">
						<img
							src='<c:out value="${KualiForm.lookupable.extraButtonSource}" />'
							class="tinybutton"  border="0" alt="cancel"/></a>
						</c:if>
					</td>
				</tr>
			</table>
			</div>

			<br />
			<br />

		</td>
		</tr>
	</table>
	
	<table width="100%" cellspacing="0" cellpadding="0">
		<tr>
			<td><c:if test="${!empty reqSearchResultsSize}">

				<c:set var="exporting" value="${!empty param['d-16544-e']}"
					scope="request" />
				<display:table class="datatable-100" cellspacing="0" cellpadding="0"
					name="${reqSearchResults}" id="row" export="true" pagesize="100"
					defaultsort="1"
					requestURI="glTrialBalance.do?methodToCall=viewResults&reqSearchResultsSize=${reqSearchResultsSize}&searchResultKey=${searchResultKey}">

					<c:forEach items="${row.columns}" var="column">

						<c:if test="${!empty column.columnAnchor.title}">
							<c:set var="title" value="${column.columnAnchor.title}" />
						</c:if>
						<c:if test="${empty column.columnAnchor.title}">
							<c:set var="title" value="${column.propertyValue}" />
						</c:if>
						<c:choose>

							<c:when
								test="${column.formatter.implementationClass == 'org.kuali.rice.kns.web.format.CurrencyFormatter'}">

								<display:column class="numbercell" sortable="true"
									decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator"
									title="${column.columnTitle}" comparator="${column.comparator}">

									<c:choose>

										<c:when test="${column.propertyURL != \"\"}">
											<a href="<c:out value="${column.propertyURL}"/>"
												title="<c:out value="${title}" />" target="blank"><c:out
												value="${column.propertyValue}" /></a>
										</c:when>

										<c:otherwise>
											<c:out value="${column.propertyValue}" />
										</c:otherwise>
									</c:choose>
								</display:column>

							</c:when>

							<c:otherwise>

								<c:choose>

									<c:when test="${column.propertyURL != \"\"}">

										<display:column class="infocell" sortable="${column.sortable}"
											decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator"
											title="${column.columnTitle}"
											comparator="${column.comparator}">

											<a href="<c:out value="${column.columnAnchor.href}"/>"
												title="${column.columnAnchor.title}" target="blank"><c:out
												value="${column.propertyValue}" /></a>

										</display:column>

									</c:when>

									<c:otherwise>

										<display:column class="infocell" sortable="${column.sortable}"
											decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator"
											title="${column.columnTitle}"
											comparator="${column.comparator}">
												<c:out value="${column.propertyValue}" />
										</display:column>

									</c:otherwise>

								</c:choose>

							</c:otherwise>

						</c:choose>

					</c:forEach>
					<c:if test="${param['d-16544-e'] == null}">
						<logic:present name="KualiForm" property="formKey">
							<c:if
								test="${KualiForm.formKey!='' && KualiForm.hideReturnLink!=true && !KualiForm.multipleValues && param.inquiryFlag != 'true'}">
								<display:column class="infocell" property="returnUrl"
									media="html" />
							</c:if>
							<c:if
								test="${row.actionUrls!='' && KualiForm.suppressActions!=true && !KualiForm.multipleValues && KualiForm.showMaintenanceLinks}">
								<display:column class="infocell" property="actionUrls"
									title="Actions" media="html" />
							</c:if>
						</logic:present>
					</c:if>
				</display:table></td>
			</c:if>
			<td width="1%"><img
				src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif"
				alt="" height="20" width="20"></td>
		</tr>
	</table>
	<c:if test="${!empty KualiForm.message }">
 			${KualiForm.message }	
    </c:if>
</kul:page>
