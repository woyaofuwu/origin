$(function(){
	var inModeCode = $("#SERIAL_NUMBER").attr("inModeCode");
	if(inModeCode == 1 && typeof(eval(window.top.getCustorInfo))=="function"){
		var sn = window.top.getCustorInfo();
		$("#SERIAL_NUMBER").val(sn);
		$("#QUERY_BTN").click();
	}
})
function sel_bef_chkSearchForm(){
	var serialNum = $("#SERIAL_NUMBER").val();
	if(!$.verifylib.checkMbphone(serialNum)){
		MessageBox.error("错误提示","用户号码格式不正确！");
		return;
	}
	var param = '&SERIAL_NUMBER='+ serialNum;
	$.ajax.submit(null, 'queryInfo', param, 'QueryListPart', loadSuccess,
	function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","查询用户信息错误！", null, null, info, detail);
	});	
	
}

function loadSuccess(custInfo){
	//客户信息 
	if(top.showCustInfo && typeof(top.showCustInfo)=="function"){ 
		var param = "&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
				param += "&TRADE_TYPE_CODE=2101"; 
		$.ajax.submit("", "getHintInfo", param,"",function(data){ 
			if(!data || (data && data.get("RESULT_CODE")!="0")){ 
				top.clearCustInfo(); 
				return ; 
			} 
			var map=$.DataMap(); 
			var custName = custInfo.get('CUST_NAME');
			var custNameFazzy = custName.substring(0,1);
			for(var i=0;i<custName.length-1;i++)
			{
				custNameFazzy += "*"; 
			}
			map.put("CUST_NAME", custNameFazzy);
			map.put("PRODCUT_NAME", data.get("PRODCUT_NAME")); 
			map.put("HINT_INFO", data.get("HINT_INFO1", "")+data.get("HINT_INFO2", "")); 
			top.showCustInfo(map.toString()); 
		},function(code, info, detail){ 
		$.endPageLoading(); 
		MessageBox.error("错误提示","加载客户信息错误！", null, null, info, detail); 
		}); 
	}
	
	//营销推荐信息
	if(top.triggerPushInfos && typeof(top.triggerPushInfos)=="function") {
		var sn = $("#SERIAL_NUMBER").val();
		var param = "&HINT_INFO="+"{SERIAL_NUMBER:'"+sn+"',OPEN_DATE:'2011-01-01'}"
		$.ajax.submit("", "checkPushInfo", param,"",function(resultData){
			if(!resultData || (resultData && resultData.get("PUSH_FLAG")!="1")){
				top.$.sidebar.hideSide(true);
				return;
			}
			top.triggerPushInfos(param,"baseinfo");
			
		},function(code, info, detail){
			$.endPageLoading();
			MessageBox.error("错误提示","加载新业务推荐信息错误！", null, null, info, detail);
		});
	}
}