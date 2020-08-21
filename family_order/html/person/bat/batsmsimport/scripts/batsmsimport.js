function onchangetype(){

	var type = $("#TYPE").val();
	//alert(type);
	
	if( type == 1 ){
		document.getElementById("groupnum").style.display=""
	}else{
		document.getElementById("groupnum").style.display="none"
	}
	
}


function checkImport(){

	var sendobject = $("#SEND_OBJECT").val();
	if( null == sendobject || "" == sendobject ){
		alert("请输入短信下发端口");
		return false;
	}
	if( sendobject.length > 18 ){
		alert("短信下发端口最大18位");
		return false;
	}
	
	var desc = "短信下发端口 输入非法，只能输入数字";
	if( false == $.verifylib.checkInteger(sendobject, desc) ){
		alert(desc);
		return false;
	}

	var type = $("#TYPE").val();
	if( type == 1 ){
		var group_num = $("#GROUP_NUM").val();
		if( group_num == null || ""== group_num){
			alert("请输入GROUP_NUM");
			return false;
		}
	}
	
/*	var content = $("#NOTICE_CONTENT").val();
	if( null == content || "" == content ){
		alert("请输入短信内容");
		return false;
	}*/
	
	
	return true;
}