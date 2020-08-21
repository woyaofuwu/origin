function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString(), 'ScorePlanPart', function(){
			
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, null);
    });
}

/** check serialNumber */

//业务提交
function onTradeSubmit()
{
	if (!$.validate.verifyAll()){
		return false;
	}
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	if(confirm('您确定订购此积分计划？')){
		$.cssubmit.addParam(param);
		return true;
	}else{
		return false;
	}
}

function checkPlan(obj){
	var planId = $("#PLAN_ID").val();
	
	//alert(isNull(planId));
	if(obj.value == planId && !isNull(planId)){
		alert("用户已经订购该积分计划，请重新选择！");
		obj.value = "";
		obj.focus();
	}	
}

	function isNull(str){
			if(str==undefined || str==null || str=="") {
				return true;
			}
			return false;
	}

