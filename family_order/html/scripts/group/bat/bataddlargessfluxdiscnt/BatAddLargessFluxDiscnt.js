// 设置返回值
function setReturnData(){

	var grpSn = $("#cond_groupSerialNumbers").val();
	if(grpSn == "" || grpSn == null){
		alert("请输入集团产品编码后，点击'查询'按钮查询或者回车查询!");
		return false;
	}
	
	if(!$.validate.verifyAll("groupPart")) return false;

	if(!$.validate.verifyAll("feePart")) return false;

	if(!$.validate.verifyAll("discntPart")) return false;
	
	
	
	// 设置返回值
	var valueData = $.DataMap();
	
	var discntCode = $("#DISCNT_CODE").val();
	
	valueData.put("DISCNT_CODE", discntCode);
	
	var grpNum = $("#cond_groupSerialNumbers").val();
	
	valueData.put("GRP_SERIAL_NUMBER", grpNum);
	
	if($("input[name='pam_BindTeam']:checked")){
		var check = $("input[name='pam_BindTeam']:checked").val();
		valueData.put("BindTeam", check);
	}
 	
 	var limitFee = $("#cond_limitFee").val();
 	
 	if(limitFee == "" || limitFee == null){
 		alert("请输入分配的流量!");
 		return false;
 	}
 	
 	if(limitFee <= 0){
 		alert("分配的流量不能等于或小于0!请重新输入!");
 		return false;
 	}
 	
 	valueData.put("LimitFee", limitFee);
 	
 	parent.$('#POP_CODING_STR').val("优惠编码：" + discntCode);
	parent.$('#CODING_STR').val(valueData);
 	
	parent.hiddenPopupPageGrp();
}

function qrySerialNumber(){

	var grpSn = $("#cond_groupSerialNumber").val();
	if(grpSn == "" || grpSn == null){
		alert("请输入集团产品编码后，点击'查询'按钮查询或者回车查询!");
		return false;
	}
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("groupPart", "getNumberInfo", null, null, 
		function(data){
			$.endPageLoading();
			var grpSn = $("#cond_groupSerialNumber").val();
			if(grpSn != "" && grpSn != null){
				$("#cond_groupSerialNumbers").val(grpSn);
			}
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

 function changeNumber() 
{
	var grpNum = $("#cond_groupSerialNumbers").val();
	if(grpNum != null && grpNum != ""){
		 $("#cond_groupSerialNumbers").val("");
	}
}
