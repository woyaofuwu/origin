function refreshPartAtferAuth(data){
	$.beginPageLoading("信息加载中.....");
	$.ajax.submit('', 'loadChildInfo',
			"&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString()+"&ACCT_INFO="+data.get("ACCT_INFO").toString(), 
			'vipInfoPart,lastServerInfos,payInfoPart,commInfoPart,simBakPart,AirportVipServerPart', 
			function(){
				$("#SubmitPart").addClass("e_dis");
				$("#CSSUBMIT_BUTTON").attr("disabled",true);
				$.endPageLoading();
			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
  			});
}

function queryAirportCancel(data){
	var queryType = $("#cond_QUERY_VALUE").val();
	if(queryType == ""){
		alert("请选择查询类型！");
		return false;
	}
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	if(serialNumber == ""){
		alert("请输入号码！");
		return false;
	}
	var isFree = $("#cond_IS_FREE").val();
	var startDate = $("#cond_START_DATE").val();
	var endData = $("#cond_END_DATE").val();
	var isReturn = $("#cond_IS_RETURN").val();
	
	if(!comparaDate(startDate,endData))
	{
		alert("结束时间必须大于开始时间！");
		$("#cond_START_DATE").val("");
		$("#cond_END_DATE").val("");
		return false;
	}
	
	var param = "&QUERY_TYPE="+queryType+
	"&SERIAL_NUMBER="+serialNumber+
	"&START_DATE="+startDate+
	"&END_DATE="+endData+
	"&IS_TRUE="+isReturn+
	"&IS_FREE="+isFree;

	$.beginPageLoading("信息加载中.....");
	$.ajax.submit('', 'queryAirportCancel',param , 
			'showCancelInfos', 
			function(){
				$.endPageLoading();
			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
  			});
}

function comparaDate(sysDate,selDate)
{
	var sysDateArray = sysDate.split("-");
	var selDateArray = selDate.split("-");
	
	if(selDateArray[0]<sysDateArray[0])//比较年份
	{
		return false;
	}else if (selDateArray[0] == sysDateArray[0])
	{
		if(selDateArray[1]<sysDateArray[1])//比较月份
		{
			return false;
		}else if (selDateArray[1] == sysDateArray[1])
		{
			if(selDateArray[2]<=sysDateArray[2])//比较日期
			{
				return false;
			}
		}
	}
	return true;
}

function submitButton(data){
	var data = $.table.get("sceneTable").getCheckedRowDatas();//获取选择中的数据
	if(data == null || data.length == 0) {
		alert('请选择待处理信息后，再继续办理业务!');
		return false;
	}
	var tableData  =  $.table.get("sceneTable").getTableData(null,true);
	var checkData = data.get(0);
	for(var i=0;i<tableData.length;i++){
		var tmp = tableData.get(i);
		var tradeId = tmp.get("TRADE_ID");
		var dataTradeId = checkData.get("TRADE_ID");
		if(tradeId > dataTradeId){
			alert("指定返销的不是此用户同种业务的最后一笔，不能隔笔返销");
			return false;
		}
	}
	
	var param = "&TABLE_DATA="+data;

	$.beginPageLoading("预登记加载中.....");
	$.ajax.submit('', 'submitButton',param , 
			'showCancelInfos', 
			function(){
				$.showSucMessage("预登记成功！","","")
				$.endPageLoading();
			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
  			});
}

/**
 * 校验VIP用户身份是否合法，主要是校验VIP卡号或者身份证
 */
function authUserInfo(obj){
	var authType = $("#auth_AUTH_TYPE").val();
	if(authType == ""){
		alert("证件类型不能为空!");
		return false;
	}
	var authValue = $("#auth_AUTH_VALUE").val();
	if(authValue == ""){
		alert("号码不能为空！");
		return false;
	}
	var serialNumber = $("#USER_SERIAL_NUMBER").val();
	if(serialNumber == ""){
		alert("手机号码不能为空！"+serialNumber);
		return false;
	}
	
	var vipCardNo = $("#vipInfo_VIP_CARD").val();
	var psptId =$("#PSPT_ID").val();
	
	if(authType == "0"){
		
		$.beginPageLoading("信息加载中.....");
	$.ajax.submit('', 'authVipUserInfo',
			"&SERIAL_NUMBER="+serialNumber+"&PSPT_ID="+authValue, 
			'', 
			function(data){
				var result = data.get(0);
				if("0"==result.get("RESULT_CODE")){
					alert("正确！");
					$("#SubmitPart").removeClass("e_dis");
					$("#CSSUBMIT_BUTTON").attr("disabled",false);
				}else{
					alert("错误！");
					$("#auth_AUTH_VALUE").val("");
					$.endPageLoading();
					return false;
				}
				$.endPageLoading();
			},
			function(error_code,error_info){
				
  			});
	/**
		if(authValue == psptId){
			alert("正确！");
			$("#SubmitPart").removeClass("e_dis");
			$("#CSSUBMIT_BUTTON").attr("disabled",false);
		}else{
			alert("错误！");
			$("#auth_AUTH_VALUE").val("");
			return false;
		}
	**/	
	}
	if(authType == "1"){
		if(authValue == vipCardNo){
			alert("正确！");
			$("#SubmitPart").removeClass("e_dis");
			$("#CSSUBMIT_BUTTON").attr("disabled",false);
		}else{
			alert("错误！");
			$("#auth_AUTH_VALUE").val("");
			return false;
		}
	}
	
	/**
	$.beginPageLoading("信息加载中.....");
	$.ajax.submit('psptCheckPart', 'authVipUserInfo', '&AUTH_TYPE='+authType+"&AUTH_VALUE="+authValue+"&VIP_CARD_NO="+vipCardNo+"&PSPT_ID="+psptId, '', 
	function(){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    }); */
}

function dealAuthTypeChange(obj){

	var authType =$("#auth_AUTH_TYPE").val();
	
	if(authType == ""){
		alert("证件类型不能为空!");
		return false;
	}
	if(authType == "0"){
		$("#psptIdAuthButton").css("display","");
	}
	if(authType == "1"){
		$("#psptIdAuthButton").css("display","none");
	}
}

/** 随员数量检查 */
function checkfollownum(){
	var follownum = $("#commInfo_FOLLOW_NUMBER").val();
	if(!$.isNumeric(follownum) ){
	   	alert("随行人数中请输入数字！");
	   	$("#comminfo_FOLLOW_NUM").val("0");
	   	return false;
	}
	if (follownum > 1) {
		alert("最多带一个随员,请重填！");
		$("#comminfo_FOLLOW_NUM").val("0");
		return false;
	}
}

function onTradeSubmitApp() {
	if(!$.validate.verifyAll("commInfoPart")) {
			return false;
	}
	if(""==$("#commInfo_AIRDROME_ID").val()){
	   	alert("请选择机场名称！");
	   	return false;
	}
	if(""==$("#commInfo_PLANE_LINE").val()){
	   	alert("请输入航班编号！");
	   	return false;
	}
	if(""==$("#commInfo_SERVICE_CONTENT").val()){
	   	alert("请输入服务内容！");
	   	return false;
	}
	if(""==$("#commInfo_SERVICE_TYPE").val()){
	   	alert("请选择服务类型！");
	   	return false;
	}
	
	checkfollownum();
	
	$.printMgr.bindPrintEvent(print);
	return true;

}

function print(dataset){
	
	var authData = $.auth.getAuthData();
	var eparchyCode = authData.get("USER_INFO").get("EPARCHY_CODE");
	var serialNumber = authData.get("USER_INFO").get("SERIAL_NUMBER");
	var brandCode = authData.get("USER_INFO").get("BRAND_CODE");
	var psptId = authData.get("CUST_INFO").get("PSPT_ID");
	var custName = authData.get("CUST_INFO").get("CUST_NAME");
	var obj = $("input[name=REPRINT_TRADE]:checked");
	var reprintTrade = obj.parent().siblings(); 
	var flag ="1";
	var params = "&PRINT_FLAG="+flag;
	params += "&TRADE_ID="+dataset.get("TRADE_ID");
	params += "&SERIAL_NUMBER="+serialNumber;
	params += "&PSPT_ID="+psptId;
	params += "&BRAND_CODE="+brandCode;
	params += "&CUST_NAME="+custName;
	
	$.beginPageLoading("加载打印数据。。。");
	$.ajax.submit(null, "loadPrintData", params, null, 
		function(data){
			$.endPageLoading();
			
			//设置打印数据
			$.printMgr.params.put("ORDER_ID", dataset.get("ORDER_ID"));
			$.printMgr.params.put("EPARCHY_CODE", dataset.get("EPARCHY_CODE"));
			$.printMgr.setPrintData(data);
			//$.printMgr.setPrintData(data.get(0));
			
			//启动打印
			$.printMgr.printReceipt();
		},
		function(errorcode, errorinfo, detail){
			$.endPageLoading();
			MessageBox.error("错误提示", "加载打印数据报错！",null,null,errorinfo, detail);
		},function(){
			$.endPageLoading();
			MessageBox.alert("告警提示", "加载打印数据超时");
	});		
}
