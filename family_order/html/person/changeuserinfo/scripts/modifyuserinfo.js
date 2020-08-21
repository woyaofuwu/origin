function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString(), 'ModifyUserInfoPart,UseInfoPart', function(data){
		$("#USER_TYPE_CODE").attr("disabled", false);
		$("#ADD_BTN").attr("disabled", false);
		$("#DEL_BTN").attr("disabled", false);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
		$.auth.reflushPage();
    });
	
}

//新增担保人信息
function addAssure()
{
	 $("#ASSURE_PSPT_TYPE_CODE").attr("disabled", false);
	 $("#ASSURE_PSPT_ID").attr("disabled", false);
	 $("#ASSURE_NAME").attr("disabled", false);
	 $("#ASSURE_TYPE_CODE").attr("disabled", false);
	 $("#ASSURE_DATE").attr("disabled", false);
	 
	 $("#ASSURE_PSPT_TYPE_CODE").val("");
	 $("#ASSURE_PSPT_ID").val("");
	 $("#ASSURE_NAME").val("");
	 $("#ASSURE_TYPE_CODE").val("");
	 $("#ASSURE_DATE").val("");
	 
	 $("#ASSURE_PSPT_ID").attr("nullable", "no");
	 $("#ASSURE_PSPT_TYPE_CODE").attr("nullable", "no");
	 $("#ASSURE_NAME").attr("nullable", "no");
}

//删除担保人信息 
function deleteAssure()
{
	 $("#ASSURE_PSPT_TYPE_CODE").attr("disabled", true);
	 $("#ASSURE_PSPT_ID").attr("disabled", true);
	 $("#ASSURE_NAME").attr("disabled", true);
	 $("#ASSURE_TYPE_CODE").attr("disabled", true);
	 $("#ASSURE_DATE").attr("disabled", true);
	 
	 $("#ASSURE_PSPT_TYPE_CODE").val("");
	 $("#ASSURE_PSPT_ID").val("");
	 $("#ASSURE_NAME").val("");
	 $("#ASSURE_TYPE_CODE").val("");
	 $("#ASSURE_DATE").val("");
	 
	 $("#ASSURE_PSPT_ID").attr("nullable", "yes");
	 $("#ASSURE_PSPT_TYPE_CODE").attr("nullable", "yes");
	 $("#ASSURE_NAME").attr("nullable", "yes");
}

//担保证件类型 
function changeTypeCode()
{
     $("#ASSURE_PSPT_ID").val("");
	 $("#ASSURE_NAME").val("");
	 $("#ASSURE_TYPE_CODE").val("");
	 $("#ASSURE_DATE").val("");
	 
	var assurePsptTypeCode = $("#ASSURE_PSPT_TYPE_CODE").val();
	//如果是身份证，则做身份证格式校验
	if(assurePsptTypeCode=="0" || assurePsptTypeCode=="1") {
		$("#ASSURE_PSPT_ID").attr("datatype", "pspt");
	}
	else {
		$("#ASSURE_PSPT_ID").attr("datatype", "text");
	}
}

//证件号码校验
function ckeckPsptNum()
{
   if(!$.validate.verifyField($("#ASSURE_PSPT_ID")[0])) {
   		$("#ASSURE_PSPT_ID").val("");
		return false;
	}
	else {
		//黑名单校验
		$.ajax.submit('AssurePart,AuthPart', 'isBlackUser', null, null, function(data) {
			var data0 = data.get(0);
			showMessage(data0);
		},
		function(error_code,error_info){
			$.endPageLoading();
			$("#ASSURE_PSPT_ID").val("");
			alert(error_info);
			
	    });
	}
}

function showMessage(data0){
    
    var isBlack = data0.get("IS_BLACK_USER");
    if(isBlack=="true")
    {
      $("#ASSURE_PSPT_ID").val("");
      alert("该担保人用户为黑名单客户!");     
    }else
    {  
      var assureName=data0.get("ASSURE_NAME");
      var psptUseCount= data0.get("PSPT_COUNT");
      
      if (assureName!="")
      {
    	  $("#ASSURE_NAME").val(assureName);
		  $("#ASSURE_NAME").attr("disabled", true);

      }else
      {
         if (psptUseCount=="0")
         {
        	 $("#ASSURE_NAME").val('');
   		   	 $("#ASSURE_NAME").attr("disabled", false);
         }else
         {            
            if(confirm('是否选择同客户？'))
            {
                var param = '&multi=false&PSPT_TYPE_CODE='+ $("#ASSURE_PSPT_TYPE_CODE").val()+'&PSPT_ID='+$("#ASSURE_PSPT_ID").val();
    	        popupPage('custlist.CustListPage', 'queryCustList', param, '客户信息列表', '640', '280');
            }else
            {
            	$("#ASSURE_PSPT_TYPE_CODE").val('');
            	$("#ASSURE_PSPT_ID").val('');
            }
            
         }
         $("#ASSURE_NAME").attr("nullable", "no");
        
      }
    }
    
   
}

//提交校验
function submitCheck(obj)
{  
	var assurePsptId= $("#ASSURE_PSPT_ID").val();
	if(assurePsptId.indexOf('*')>=0)//被模糊化了 则设置证件号码为text类型
	{
		$("#ASSURE_PSPT_ID").attr("datatype","text");
	}else{
		var psptTypeCode = $("#ASSURE_PSPT_TYPE_CODE").val();		
		if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode=="2") {
			$("#ASSURE_PSPT_ID").attr("datatype","pspt");
		}else
		{
			$("#ASSURE_PSPT_ID").attr("datatype","text");
		}
	}
	
   if(!$.validate.verifyField($("#USER_TYPE_CODE")) || !$.validate.verifyField($("#ASSURE_PSPT_TYPE_CODE")) 
        || !$.validate.verifyField($("#ASSURE_PSPT_ID"))  || !$.validate.verifyField($("#ASSURE_NAME"))) {
        return false;
   }
   
   var newUserTypeCode = $("#USER_TYPE_CODE").val();
   var newPsptType =  $("#ASSURE_PSPT_TYPE_CODE").val();
   var newPsptId =  $("#ASSURE_PSPT_ID").val();
   var newAssuerName =  $("#ASSURE_NAME").val();
   var newAssuerType =  $("#ASSURE_TYPE_CODE").val();
   var newAssuerDate =  $("#ASSURE_DATE").val();
   
   var oldUserTypeCode = $("#OLD_USER_TYPE_CODE").val();
   var oldPsptType =  $("#OLD_ASSURE_PSPT_TYPE_CODE").val();
   var oldPsptId =  $("#OLD_ASSURE_PSPT_ID").val();
   var oldAssuerName =  $("#OLD_ASSURE_NAME").val();
   var oldAssuerType =  $("#OLD_ASSURE_TYPE_CODE").val();
   var oldAssuerDate =  $("#OLD_ASSURE_DATE").val();
   
   if(oldAssuerDate.length<=10){
	   oldAssuerDate=oldAssuerDate+" 00:00:00";
   }
   if(newUserTypeCode==oldUserTypeCode && newPsptType==oldPsptType && newPsptId==oldPsptId && newAssuerName==oldAssuerName
		   && newAssuerType==oldAssuerType && newAssuerDate==oldAssuerDate){
	   alert("没有修改任何资料，不需要提交！");
	   return false;
   }
	return true;	  
}



