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
	MessageBox.alert(error_info);
    });
}

function a(){
	//alert(123);
}
//目前只支持一次取消一个
function beforeActionFun() {
	var param = "";
	var tabChecks = $("input[name=RADIO]");
	if(tabChecks.length>0)
	{
		for(var i =0; i<tabChecks.length;i++)
		{
			if(tabChecks[i].checked)
			{
				param+= "&ORDERNO=" + $.attr(tabChecks[i],'ORDERNO') + "&SERIAL_NUMBER_A=" + $.attr(tabChecks[i],'SERIAL_NUMBER_A') + "&SERIAL_NUMBER_B=" + $.attr(tabChecks[i],'SERIAL_NUMBER_B') + "&CATEGORY=" + $.attr(tabChecks[i],'CATEGORY');
				break;
			}
		}
	}
	if("" == param){
		alert("您没有选择任何副号码！");
		return false;
	}
    if(window.confirm('确定执行该副号码的取消操作？')){
    	$.cssubmit.addParam(param);	
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
	var ulObj = $("#authInputPart ul");
	ulObj.prepend('<li><div class="label">查询方式</div><div class="value"><span id="multiSelectedContainer"></span></div></li>');
	$.Select.after("multiSelectedContainer",{id:"NUMBERTYPE",name:"NUMBERTYPE",addDefault:false},[{TEXT:"主号码查询",VALUE:"1"},{TEXT:"副号码查询",VALUE:"0"}]);
});