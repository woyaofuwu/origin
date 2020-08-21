/* $Id */
// 资源号码校验
function checkResSn(ressn,restypecode,restypename){
	
	if(ressn.length == 0){
        alert("资源号码不能为空！");
        return false;
    }
	if(ressn.length < 5){    
        alert("资源号码位数不能小于5位！");
        return false;
    }
	if(restypecode=="0" && ressn.length<11){
		alert("请输入正确的手机号码！");
		return false;
    }
	if(restypecode=="1" && ressn.length<20){
		alert("请检查输入SIM卡号是否相同或错误！");
		return false;
    }
	var productID = $("#PRODUCT_ID").val();
	
	$.beginPageLoading("资源校验中...");
	
    $.ajax.submit(null, "checkResourceInfo", "&RES_TYPE_CODE="+restypecode + "&RES_TYPE="+restypename + "&RES_VALUE="+ressn + "&PRODUCT_ID="+productID, "RES_DEPART",
	    function(data){
	       $.endPageLoading();
	       afterCheckResSn(ressn,restypecode,restypename,data);
	    },
		function(error_code, error_info, derror){
		   $.endPageLoading();
		   showDetailErrorInfo(error_code, error_info, derror);
		}
    );	
	   
}

// 资源号码校验回调函数
function afterCheckResSn(ressn,restypecode,restypename,data){
		
   var resultcode = data.get("X_RESULTCODE");
   var resultInfo = data.get("X_RESULTINFO");
   
   if (resultcode == "0"){
	   $("#SERIAL_NUMBER").val(ressn);	 
	   $("#checkSuccess").html("成功");
	   $("#checkSuccess").attr("style","color:green;");
	   $("#modifyTag").html("可修改");
   } else {
	   $("#SERIAL_NUMBER").val("");
	   $("#resourcesn").attr("disabled",true);
	   $("#checkSuccess").attr("style","color:red;");
	   $("#checkSuccess").html("失败"); 
	   MessageBox.alert("提示",resultInfo);
	   $("#resourcesn").attr("disabled",false);
   }  
   
}

// 点击下一步的校验
function nextCheck(){
	
	//校验必选包
	if (!selectedElements.checkForcePackage()) {
		return false;
	}
	
	var restype = $("#resourcesn").attr("restype");
	var ressn = $("#resourcesn").val();
	var serialNumber = $("#SERIAL_NUMBER").val();
	
	if(ressn.length==0){
		alert("请在资源信息区域输入["+restype+"]，回车进行校验！");
		return false;
	}
	if(serialNumber.length==0){
		alert("请敲回车进行资源号码校验！");
		return false;
	}
	
	if(!$.validate.verifyAll("productInfoPart"))
		return false;
	
	$("#SELECTED_ELEMENTS").val(selectedElements.getSubmitData());
	
	return true;
	
}


$(document).ready(function(){
	
	var tag = $("#TAG").val();
	if (tag == "1"){
		$("#memRes input:first").attr("disabled",true);		
		$("#checkSuccess").html("成功");
		$("#modifyTag").html("不可修改");
	} else {
		$("#memRes input:first").attr("disabled",false);
		$("#modifyTag").html("可修改");
	}
	   	
});
