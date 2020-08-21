 

 
function submitBeforeCheck(){
	//alert("aaa");
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	var resCode = $("#cond_TERMINAL_NUMBER").val();
	if(serialNumber==""){
		alert("手机号不能为空");
		return false;
	}
	if(resCode==""){
		alert("终端串号不能为空");
		return false;
	}
	
	if(!confirm("确定提交吗?")){ 
		return false;
	}
 
	return true; 
}

 