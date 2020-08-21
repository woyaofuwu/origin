function initPageParam_110000007120() {
	/*debugger;
	var  html='<div class="c_popupGroup" level="3"><div id="staffPopupItem" class="c_popupItem">111</div></div>';
	$("#popup .c_popupWrapper").append(html);
	showPopup('popup','staffPopupItem');*/
	
	var html="<input type='text' id='POP_pam_CUST_MANAGER' name='POP_pam_CUST_MANAGER' jwcid='@TextField' value='' readOnly = 'true'/>";
	$(html).insertBefore($("#pam_CUST_MANAGER"));
	$("#pam_CUST_MANAGER").css("display", "none");
}
//提交
function checkSub(obj)
{
	var offerTpye = $("#cond_OPER_TYPE").val();
	if(!validateParamPage(offerTpye))
		return false;
	if(!submitOfferCha())
		return false; 
	backPopup(obj);
}
function validateParamPage(methodName) {
	if($("#pam_CUST_MANAGER").val()==""||$("#pam_CUST_MANAGER").val()==null){
		$.validate.alerter.one($("#pam_CUST_MANAGER")[0], "客户经理不能为空！");
		return false;
	}
    return true;
}

function openStaffPopup(fieldName)
{
	debugger;
	$("#staffSelFrame").contents().find("#field_name").val(fieldName);
	showPopup('staffPicker','staffPickerHome');
}

function setMangeId()
{
	debugger;
	var maageid= $("#pam_CUST_MANAGER").val();
	
}