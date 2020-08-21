/*$Id:$*/
//面初始化

function myTabSwitchAction(ptitle, title) {
   
	return true;
}


function showDIV(op){
	if(op==11){
		$("#only").css('display','none');
		$("#many").css('display','block'); 
	}
	if(op==12){
		$("#only").css('display','block');
		$("#many").css('display','none'); 
	}
	if(op==21){
		$("#only").css('display','none');
		$("#many").css('display','block'); 
	}
	if(op==22){
		$("#only").css('display','block');
		$("#many").css('display','none'); 
	}
	if(op==31){
		$("#only").css('display','none');
		$("#many").css('display','block'); 
	}
	if(op==32){
		$("#only").css('display','block');
		$("#many").css('display','none'); 
	}
}


function queryInfo()
{   
	$.beginPageLoading("数据正在导入...");
	$.ajax.submit("many", "batPccInfo", null, "BatSubmitPart", 
			function(data){
	          $.endPageLoading();
	          $.showSucMessage(data.get("result"), '', '');
	          $("#FILE_PATH").val("");
			}, 
			function(error_code, error_info){
				$.endPageLoading();
				alert(error_info);
	});	
}

function addBaseInfo(tmp)
{   
	var flowId=$("#info_BASS_FLOW_ID").val();
	var operation=$("#info_OPERATION_TYPE").val();
	var usrIdentifier=$("#info_USR_IDENTIFIER").val();
	
	if(tmp==2){
		var serviceCode=$("#info_SERVICE_CODE").val();
		if(serviceCode==""){
			alert("业务策略代码不能为空!");
			return;
		}
	}
	
	if(tmp==3){
		var sessionCode=$("#info_USR_SESSION_POLICY_CODE").val();
		if(sessionCode==""){
			alert("用户策略代码不能为空!");
			return;
		}
	}
	
	if(flowId==""){
		alert("经分流水号不能为空!");
		return;
	}
	if(flowId==""){
		alert("操作类型不能为空!");
		return;
	}
	if(usrIdentifier==""){
		alert("手机号码不能为空!");
		return;
	}
	$.beginPageLoading("数据正在入库...");
	$.ajax.submit("only", "addBaseInfo", null, "QueryPart", 
			function(data){
	          $.endPageLoading();
	          //alert(data.get("result"));
	          $.showSucMessage(data.get("result"), '', '');
			}, 
			function(error_code, error_info){
				$.endPageLoading();
				alert(error_info);
	});	
}
