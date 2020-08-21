function init(){
	//初始付费计划
	renderGrpMemPayPlanSel($('#GRP_PRODUCT_ID').val(),$('#GRP_USER_ID').val(),$('#MEB_USER_ID').val(),$('#MEB_EPARCHY_CODE').val()); 
} 
function productInfoNextCheck(){ 
	var newTypeCode = $("#PAY_PLAN_SEL_PLAN_TYPE").val();
	var oldTypeCode = $("#OLD_PLAN_TYPE_CODE").val();
	if(oldTypeCode == newTypeCode){
		alert("未对付费计划进行修改，业务不能继续！");
		$("#grpPayRels").val('');
		return false;
	}
	return true;
}
/* $Id */

function showFeeMessage() {
	var payfee_type = $("#pam_PAY_FEE_MODE_CODE").val();
	var feemsgpart = $("#FeeMSGPart");
	if (payfee_type == "1") {
		hidden(feemsgpart, true);
	} 
	else {
		hidden(feemsgpart, false);
	}
};
function operationZip(checkBoxID,zipID){
//	var cb=document.getElementById(checkBoxID);
	var cb=$('#'+checkBoxID);
	if(cb){
//		var zip=document.getElementById(zipID);
		var cb=$('#'+zipID);
		if(zip){if(!cb.checked){
			zip.css('display','none');
		}else{
			zip.css('display','block');
		}}
	}
}