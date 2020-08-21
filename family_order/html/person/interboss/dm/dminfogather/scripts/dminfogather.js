//Auth组件查询后调用方法
function refreshPartAtferAuth(data)
{
	var user_info = data.get("USER_INFO").toString();
	
	var param = "&USER_INFO="+user_info;
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('AuthPart', 'loadChildInfo', param, 'tradeTypePart,hiddenPart', function(){
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

function sendHttpRequest()
{
	
	var flag = false;
	var params="";
	var prex = "apprs";
	var count = 0;
	var fid ="";
	var deptTable=$.table.get("DeptTable").getTableData(null,true);
	for(var i=0;i<deptTable.length;i++){
		
		var icheckname = i+prex;
		var ocheck = $("#"+icheckname);
		
		if(ocheck && ocheck.attr("checked") == true){
			 fid += ocheck.attr("funcid")+",";
			 count+=1;
		}
		else{
			continue;
		}
	}
	if(count == 0){
		alert('您没有选择任何业务功能');
		return false;
	}
	
	params ="&FUNCIDS="+fid.substring(0,fid.length-1)+"&PHONE="+$("#AUTH_SERIAL_NUMBER").val();
	
	//$.cssubmit.addParam(params);
	
	$("#CSSUBMIT_BUTTON").attr("disabled",true);
	$("#CSSUBMIT_BUTTON").attr("className","e_button-page-ok e_dis");
	//校验需要同步
	//$.beginPageLoading("数据处理中...");
	var rs1 = $.ajax.check(null, 'sendHttpGather', params, null, null);
	//$.endPageLoading();
    var data = new $.DataMap(rs1.rscode()+"");
    if(data && data.length>0){
    	$("#comminfo_OPERATEID").val(data.get("OPERATEID"));
    	$("#comminfo_FUNCTIONID").val(data.get("FUNCTIONID"));
    	alert("参数采集命令发送成功，开始记录日志!"); 
    	flag = true;
    }
    else{
    	alert("参数采集命令发送失败！");
    	flag = false;
    	$("#CSSUBMIT_BUTTON").attr("disabled",false);
	    $("#CSSUBMIT_BUTTON").attr("className","e_button-page-ok");
    }
    return flag;
	
}


