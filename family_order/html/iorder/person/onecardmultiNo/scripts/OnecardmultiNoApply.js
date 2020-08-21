$(function(){
	
	//副号码类型 下拉框
	$("#SERIAL_TYPE").change(function(){
//		console.log("副号码类型 SERIAL_TYPE="+$('#SERIAL_TYPE').val());
		if($('#SERIAL_TYPE').val()=="0"){//虚拟副号
			$("#MSISDN_TYPE").val("1");
			$("#getSubTypeLi").css("display","");
			$("#SERIALB_GET_TYPE").attr("nullable","no");
			$("#SERIAL_NUMBER_B").attr("nullable","yes");
			$("#SERIAL_NUMBER_B").attr("disabled","true");
			$("#getSerialNumber").css("display","");
			$("#SERIAL_NUMBER_B").val("");
		}else{
			$("#MSISDN_TYPE").val("");  // 清空MSISDN_TYPE
			$("#getSubTypeLi").css("display","none");
			$("#SERIALB_GET_TYPE").attr("nullable","yes");
			$("#SERIAL_NUMBER_B").attr("nullable","no");
			$("#MOSP_NUMBER_LEVEL").attr("disabled","true");
			$("#MOSP_NUMBER_LEVEL").val("");
			$("#getSerialNumber").css("display","none");
//			$("#getSerialNumber").attr("disabled",true);
			$("#SERIAL_NUMBER_B").attr("disabled","true");
			$("#SERIAL_NUMBER_B").val("");
		}
	});
	//副号码获取方式
	$("#SERIALB_GET_TYPE").change(function(){
//		console.log("副号码获取方式SERIALB_GET_TYPE="+$('#SERIALB_GET_TYPE').val());
		if($('#SERIAL_TYPE').val()=="0"){
			if($("#SERIALB_GET_TYPE").val() == "1"){	//1手动输入
//				$("#getSerialNumber").attr("disabled",false);
				$("#SERIAL_NUMBER_B").attr("nullable","yes");
				$("#SERIAL_NUMBER_B").attr("disabled",true);
				$("#getSerialNumber").css("display","");
				$("#SERIAL_NUMBER_B").val("");
			}else{										//平台分配,已删除自动分配
				$("#SERIAL_NUMBER_B").attr("nullable","yes");
//				$("#getSerialNumber").attr("disabled",true);
				$("#getSerialNumber").css("display","none");
				$("#SERIAL_NUMBER_B").attr("disabled",true);
				$("#SERIAL_NUMBER_B").val("");
			}
		}
	});
	
})

function submitCheck(obj) {
	//alert("=======PIC_ID======"+$("#PIC_ID").val());
	if("ERROR" == $("#PIC_ID").val())
	{
		alert("人像比对信息错误，请重新拍摄！");
        return false;
	}
    if(!$.validate.verifyAll("followSerialNumber")) {
        return false;
    }else{
        return true;
    }
}

function releaseUnuseNumber(){
	$('#getSerialNumber').attr('disabled','true');
	$.ajax.submit('', 'releaseUnuseNumber','&FOLLOW_MSISDN_S='+$('#FOLLOW_MSISDN_S').val()+'&SERIAL_NUMBER_B='+$('#SERIAL_NUMBER_B').val(), '',function(data) {	
	}, function(error_code, error_info) 
	{
		$.MessageBox.error(error_code, error_info);
	});
}

