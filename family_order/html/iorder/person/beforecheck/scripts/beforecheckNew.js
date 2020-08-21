function checkDone(){
	 
	var logObj=$("#QueryListPart").find("input[name=CHECKBOX_LIST]:checked");
	var checkNum=$("#CHECK_COUNT").val();
//	if(!logObj || !logObj.length){
//		alert("请勾选已核验的项！");
//		return;
//	} 
	if(logObj.length!=checkNum){
		MessageBox.alert("提示", "请确认所有检查项再办理业务！");
		return;
	}else{
		$("#CHECK_CODE").val("1");
		setPopupReturnValue(this,{'CHECK_CODE_OK':$("#CHECK_CODE").val()});
		hidePopup(this);
//	$.closePopupPage(true,null,null,null,null,true);
	}
}

function backPopuPage(){
	var check_code = $("#CHECK_CODE").val();
	if(check_code == "1")
		{
			hidePopup(this);
		}
	if(check_code == "-1"){
		MessageBox.alert("提示","请确认所有检查项再办理业务！");
		return;
	}
}