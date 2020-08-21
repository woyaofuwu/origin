function refreshPartAtferAuth(data)
{
    $.beginPageLoading("加载中...");
	$.ajax.submit('', 'loadChildInfo',"&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), '', function(result){
		if(result.get("RESULT_CODE")!="0"){			
			MessageBox.alert("提示",result.get("RESULT_INFO"),function(){
				$("#IS_CHECK").val("1");
				$.cssubmit.resetTrade();//重新加载页面
			},null,null);			
		}else{			
			$("#CSSUBMIT_BUTTON").attr("disabled",false);   
			$("#SubmitPart").removeClass("e_dis");
			$("#IS_CHECK").val("0");
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		$("#CSSUBMIT_BUTTON").attr("disabled", true);
		$("#SubmitPart").attr("class", "e_dis");
        $.cssubmit.resetTrade();//重新加载页面
    });
}

function submitBeforeCheck(){
	//add by dengyi5
	var sn =  $("#AUTH_SERIAL_NUMBER").val();
	if(sn==undefined || sn==null || sn==""){
		MessageBox.alert("提示","请输入手机号码",function(){			
		},null,null);
		return false;
		
	}
	var isCheck =  $("#IS_CHECK").val();
	if(isCheck == 1){
		MessageBox.alert("提示","请先查询手机号码信息",function(){			
		},null,null);
		return false;
	}	
	return true;
}