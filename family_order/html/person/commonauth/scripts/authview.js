function changeCheckMode(type){
	 if(type==0){
		 $("#checkMode0").css("display","");
		$("#checkMode1").css("display","none");
		$("#PSPT_TYPE_CODE").val('');
		$("#PSPT_ID").val('');
		$("#SERIAL_NUMBER").val('');
		$("#SERIAL_NUMBER1").val('');
	 }else if (type==2){
		//固话号码+服务密码
		$("#checkMode0").css("display","none");
		$("#checkMode1").css("display","");
		$("#cttId").css("display","");
		$("#wideId").css("display","none");
		$("#PSPT_TYPE_CODE").val('');
		$("#PSPT_ID").val('');
		$("#SERIAL_NUMBER").val('');
		$("#SERIAL_NUMBER1").val('');
	}
	else if (type==3){
		//宽带帐号+服务密码
		$("#checkMode0").css("display","none");
		$("#checkMode1").css("display","");
		$("#cttId").css("display","none");
		$("#wideId").css("display","");
		$("#PSPT_TYPE_CODE").val('');
		$("#PSPT_ID").val('');
		$("#SERIAL_NUMBER").val('');
		$("#SERIAL_NUMBER1").val('');
	}
}

var sn="";
/**
 * 形成标签页
*/
function checkCond(obj) {
	var checkmode = $("#CHECK_MODE").val();
	var psptType = $("#PSPT_TYPE_CODE").val();
	var psptId = $("#PSPT_ID").val();

	if (checkmode == "0"){	
		if (psptType == "") {
			alert("请选择证件类型!");
			return;
		}
		if (psptType == "0") {
			if (!$.verifylib.checkPspt(psptId)) {
				alert("请输入正确的身份证号码！");
				return false;
			}
		}
	}
	var serialNumber = "";
	if (checkmode == "2"){	
		serialNumber = $("#SERIAL_NUMBER").val();
	}
	if (checkmode == "3"){	
		serialNumber = $("#SERIAL_NUMBER1").val();
	}
	var param = "&PSPT_TYPE_CODE="+psptType+"&PSPT_ID="+psptId+"&SERIAL_NUMBER="+serialNumber+"&CHECK_MODE="+checkmode;
	$.ajax.submit('', 'getUsers', param, 'ResultDataPart,custTable', function(data){
		if(data.get('ALERT_INFO') && data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		var tableData =getAllTableData("userTable");
		if(tableData){
			for( var i = 0; i < tableData.length ; i++){
				if(tableData.get(i)){
				    if(tableData.get(i).get("tabCheck") == "true" ){
					    sn = tableData.get(i).get("SERIAL_NUMBER");
					    //alert(sn+" done!")
				$.auth.escapeAuth(sn);
				$("#cancelButton").css('display','');		 	
						break;
				    }else{
				    	//alert(" not done!")
				    }
				}
			}		 
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
	return true;
}

function save2Cookie(obj){
	sn=$(obj).attr("serialNumber");
	custid=$(obj).attr("cust_id");
	userid=$(obj).attr("user_id");
	cus_enc=$(obj).attr("cus_enc");
	if (sn =="" ) return ;
	var param = "&SERIAL_NUMBER="+serialNumber+"&CUST_ID="+custid+"&USER_ID="+userid+"&CUST_ID_USER_ID_SN_ENC="+cus_enc;
	
	$.beginPageLoading("认证中。。。");
	$.ajax.submit('', 'changeAuthUser', param,null, function(data){
		if(data.get('ALERT_INFO') && data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}

		$.auth.escapeAuth(sn);
		//$("#cancelButton").css('display','');		 	
				
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
	
}
function cancelAuth(){
	
	if(sn == "") return;
	var param = "&SERIAL_NUMBER="+sn;
	$.beginPageLoading("取消认证中。。。");
	$.ajax.submit('', 'cancelAuthUser', param,null, function(data){

		 $.auth.cancelEscapeAuth(sn);
		//$("#cancelButton").css('display','');		 	
				
		$.endPageLoading();
		if(data.get('ALERT_INFO') && data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
function onClose(){
	 //$.auth.cancelEscapeAuth(sn);
	cancelAuth();
	 
}

