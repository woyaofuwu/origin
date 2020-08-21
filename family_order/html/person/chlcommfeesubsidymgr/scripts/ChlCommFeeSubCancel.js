$(document).ready(function(){
	cancelInit(); 
});

function refreshPartAtferAuth(data){
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="
			+ (data.get("USER_INFO")).toString() + "&CUST_INFO="
			+ (data.get("CUST_INFO")).toString() + "&VIP_INFO="+data.get("VIP_INFO"),
			'busiInfoPart,busiInfoPart2', function() {
				cancelInit();
				$.endPageLoading();
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
			});
	
}
/**
 * 删除页面初始化,如果默认值为否，则禁止修改
 */
function cancelInit(){
	var phoneSub = $("#chlInfo_PHONE_SUB");
	var newTrade = $("#chlInfo_NEW_TRADE");
	if(phoneSub.val() == "0"){
		phoneSub.attr("disabled",true);
	}
	if(newTrade.val() == "0"){
		newTrade.attr("disabled",true);
	}
}
//根据客户类型获取用户其他信息
function ajaxGetOtherInfo(){
	var chlType = $('#chlInfo_CHL_TYPE');
	var chlCode = $('#chlInfo_CHL_CODE');
	var chlName = $('#chlInfo_CHL_NAME');
	var chlLevel = $('#chlInfo_CHL_LEVEL');
	var staffId = $('#chlInfo_STAFF_ID');
	var para_code2 = $('#chlInfo_PARA_CODE2');
	var custType = $('#chlInfo_CUST_TYPE');
	
	var staffIdHead = $('#staffIdHead');
	var phoneSub = $("#chlInfo_PHONE_SUB");
	var before = "";
	var now = "";
	if(custType.val() == ""){//如果是从有值onchange到空,则返回
		return;
	}	

	if(para_code2.val() != "" && custType.val() != para_code2.val()){
			  if(para_code2.val() == "0"){
	        	 	before="负责人";
		      }else if(para_code2.val() == "1"){
		        	before="店员" ;
		      }else if(para_code2.val() == "2"){
		        	before="无卡店员" ;
		      }
		      else if(para_code2.val() == "3"){
		        	before="店员(高级及以上级别)" ;
		      }
		      if(custType.val() == "0"){
		        	now="负责人";
		      }else if(custType.val() == "1"){
		        	now="店员" ;
		      }else if(custType.val() == "2"){
		        	now="无卡店员" ;
	      	  }else if(custType.val() == "3"){
		        	now="店员(高级及以上级别)" ;
	      	  }
		      alert("您已经办理过"+before+",不能选择"+now+"继续办理！");
		      custType.val(para_code2.val());
	}
	if(custType.val() == "0"){
		staffId.attr("disabled",true);
		staffId.attr("nullable","yes");
  	}else{		
		staffId.disabled = "";
		staffId.attr("nullable","no");
  	}
  	if(custType.val() == "2"){//无卡店员不能办理话音补贴
  		phoneSub.val("0");
  		phoneSub.attr("disabled",true);
  	}else{
  		phoneSub.val("1");
  		phoneSub.disabled = "";
  	}
  	dealChlCodeOrStaffHead();
};

function dealChlCodeOrStaffHead(){
	var chlCodeHead = $("#chlCodeHead");
	var staffIdHead = $("#staffIdHead");
	var custType = $("#chlInfo_CUST_TYPE");
	var chlCode = $("#chlInfo_CHL_CODE");
	var chlName = $("#chlInfo_CHL_NAME");
	var chlLevel = $("#chlInfo_CHL_LEVEL");
	var staffId = $("#chlInfo_STAFF_ID");
	if(custType.val() == "1"||custType.val() == "3"){//店员,店员(高级及以上级别)
		staffIdHead.attr("className","e_required");
		staffId.attr("nullable","no");
		chlCodeHead.attr("className","");
		chlCode.attr("nullable","yes");
	}else{//责任人和无卡店员
		chlCodeHead.attr("className","e_required");
		chlCode.attr("nullable","no");
		staffIdHead.attr("className","");
		staffId.attr("nullable","yes");
		staffId.attr("disabled",true);
	}
}

//根据渠道类型获取渠道编号
function ajaxGetChlCode(){
	var chlCode = $('#chlInfo_CHL_CODE');
	var chlName = $('#chlInfo_CHL_NAME');
	var chlLevel = $('#chlInfo_CHL_LEVEL');
	var staffId = $('#chlInfo_STAFF_ID');
	var	custType = $('#chlInfo_CUST_TYPE');
	var chlType = $("#chlInfo_CHL_TYPE");
	var para_code2 = $("#chlInfo_PARA_CODE2").val();//客户类型
	var para_code3 = $("#chlInfo_PARA_CODE3").val();//渠道类型
	var chlTypeValue = "";

	if(custType.val() == "0" && chlType.val() =="999"){
		alert("如果客户类型为负责人，不能选择非签约网点！");
		chlType.val("");
		return;		
	}

	if(para_code2 != ""){//如果不为空，后台应该已经将渠道编码查出来了
		return;
	}
	if(para_code3 != ""){
		chlTypeValue = para_code3;
	}else if(chlType.val() != ""){
		chlTypeValue = chlType.val();
	}else{
		return;
	}
	alert(chlTypeValue);
	$.ajax.submit('', 'queryChlCode', "&CHL_TYPE="+ chlTypeValue,'chlCode', 
			function() {
				dealChlCodeOrStaffHead();
			}, function(error_code, error_info) {
				$.endPageLoading();
				$.MessageBox.error(error_code,error_info);
			});
};

//根据渠道编码，设置渠道名称和渠道星级的值
function ajaxGetchlName(){
	var chlCode = $('#chlInfo_CHL_CODE').val();
	var para_code4 = $('#chlInfo_PARA_CODE4').val();//渠道编码
	var chlCodeValue = "";
	if(para_code4 != ""){
		chlCodeValue = para_code4;
	}else if(chlCode != ""){
		chlCodeValue = chlCode;
	}else{
		return;
	}
	$.ajax.submit('', 'queryChlName', "&CHL_CODE="+ chlCodeValue,'chlName,chlLevel', 
			function() {
		
			}, function(error_code, error_info) {
				$.endPageLoading();
				$.MessageBox.error(error_code,error_info);
			});
};

function beforeCommitCheck(){
	var phoneSub = $('#chlInfo_PHONE_SUB');
	var newTrade = $('#chlInfo_NEW_TRADE');
	
	if(phoneSub.val() == "0" && newTrade.val() == "0"){
		alert("请选择相应的补贴优惠");
		return false;
	}
	return true;   
	
}