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
		var zip=$('#'+zipID);
		if(zip){if(!cb.checked){
			zip.css('display','none');
		}else{
			zip.css('display','block');
		}}
	}
}
function productInfoNextCheck(){ 
	if(!comparaPayPlans()){return false;}
}
