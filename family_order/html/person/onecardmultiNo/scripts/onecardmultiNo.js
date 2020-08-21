function tradeActionFun(data)
{
	//alert("进入tradeActionFun");
	var user_info = data.get("USER_INFO").toString();
	var param = "&USER_INFO="+user_info;
	
	//alert(param);
	$.ajax.submit('AuthPart', 'qryOtherInfos',param, 'QueryListPart', function(returnData){
	},	
	function(error_code,error_info){
		$("#CSSUBMIT_BUTTON").attr("disabled", true);
		$("#SubmitPart").attr("class", "e_dis");
		$.endPageLoading();
		alert(error_info);
    });
}

function a(){
	//alert(123);
}
//目前只支持一次取消一个
function beforeActionFun() {
	var num = getCheckedBoxNum('ORDERNO');
    if('0'==num){
      	alert("请选择副号码提交取消操作！");
		return false;
    }
    if(window.confirm('确定执行该副号码的取消操作？')){
		return true;
	}
	return false;
}

function selectFollowPhone(){
	var followType = $("SERIAL_TYPE").val();
	if(followType == "0"){
		$("#serialNumberB").attr("disabled",true);
		$("#serialNumberB").attr("nullable","yes");
	}else{
		$("#serialNumberB").attr("disabled",false);
		$("#serialNumberB").attr("nullable","no");
	}
}
$(document).ready(function(){
	var ulObj = $("div .c_form .ul");
	ulObj.first().append('<li class="li"><span class="e_select">'
					+'<span><span>'
					+'<select name="NUMBERTYPE" id="NUMBERTYPE" >'
					+'<option value="1" title="主号码查询">主号码查询</option>'
					+'<option value="0" title="副号码查询">副号码查询</option>'
					+'</select></span></span></span>'
					+'</li>');
});