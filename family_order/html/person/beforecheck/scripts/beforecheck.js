function checkDone(){
	 
	var logObj=$("#QueryListPart").find("input[name=CHECKBOX_LIST]:checked");
	var checkNum=$("#CHECK_COUNT").val();
//	if(!logObj || !logObj.length){
//		alert("请勾选已核验的项！");
//		return;
//	} 
	if(logObj.length!=checkNum){
		alert("请确认所有检查项再办理业务！");
		return;
	}else{
	$.closePopupPage(true,null,null,null,null,true);
	}
}