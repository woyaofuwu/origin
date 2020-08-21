var isOK = 0;

function initPageParam_120099010032()
{
	debugger;
	if("0"!=$("#pam_MODIFY_TAG").val()||isOK==1){

	}
}

//提交
function checkSub(obj)
{
	var bizInCode = $("#pam_INDUSTRY_TYPE").val();
	if(bizInCode==""||bizInCode=="undefined"){
		$.validate.alerter.one($("#SVR_CODE_END_SECEND")[0],"请选择行业！");
		return false;
	}

	
	if(!submitOfferCha())
		return false; 

	isOK = 1;
	
	backPopup(obj);
}


