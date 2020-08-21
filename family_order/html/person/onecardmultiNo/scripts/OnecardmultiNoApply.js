/**
 * 海南省内需求 modify by pengzh 2018年1月18日10:29:50
 */
//提交前校验 必填项
function submitCheck(obj) { 
	if(!$.validate.verifyAll("followSerialNumber")) {
		return false;
	}else{
		return true;
	}
}

$(function(){
	
	//副号码类型 下拉框
	$("#SERIAL_TYPE").change(function(){
//		console.log("副号码类型 SERIAL_TYPE="+$('#SERIAL_TYPE').val());
		if($('#SERIAL_TYPE').val()=="0"){//虚拟副号
			$("#MSISDN_TYPE").val("1");
			$("#getSubTypeLi").css("display","block");
			$("#SERIALB_GET_TYPE").attr("nullable","no");
			$("#SERIAL_NUMBER_B").attr("nullable","yes");
		}else{
			$("#MSISDN_TYPE").val("");  // 清空MSISDN_TYPE
			$("#getSubTypeLi").css("display","none");
			$("#SERIALB_GET_TYPE").attr("nullable","yes");
			$("#SERIAL_NUMBER_B").attr("nullable","no")
			$("#MOSP_NUMBER_LEVEL").attr("disabled","true");
			$("#MOSP_NUMBER_LEVEL").val("");
			$("#getSerialNumber").css("display","none");
			$("#SERIAL_NUMBER_B").attr("disabled","");
		}
	});
	//副号码获取方式
	$("#SERIALB_GET_TYPE").change(function(){
//		console.log("副号码获取方式SERIALB_GET_TYPE="+$('#SERIALB_GET_TYPE').val());
		if($('#SERIAL_TYPE').val()=="0"){
			if($("#SERIALB_GET_TYPE").val() == "1"){	//1手动输入
				$("#getSerialNumber").css("display","");
				$("#SERIAL_NUMBER_B").attr("nullable","yes");
				$("#SERIAL_NUMBER_B").attr("disabled",true);
			}else{										//平台分配,已删除自动分配
				$("#SERIAL_NUMBER_B").attr("nullable","yes");
				$("#getSerialNumber").css("display","none");
				$("#SERIAL_NUMBER_B").attr("disabled",true);
			}
		}
	});
	
})
