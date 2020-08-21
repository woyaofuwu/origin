
function refreshPartAtferAuth(data)
{
	var param = "&ROUTE_EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE")+"&USER_ID="+data.get("USER_INFO").get("USER_ID")
	 +"&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val()+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
    $.beginPageLoading("宽带资料查询。。。");
	$.ajax.submit('', 'loadChildInfo', param, 'wideInfoPart,newSerialNumberPart', function(data){
//		var fee1=data.get("FEE1");
//		if(fee1!=null && fee1>0){
//			var m1=$.DataMap();
//			m1.put("MODE", data.get("MODE1"));
//			m1.put("CODE", data.get("CODE1"));
//			m1.put("FEE", fee1);
//			$.feeMgr.insertFee(m1);//光猫押金
//		} 
//		
//		var fee2=data.get("FEE2");
//		if(fee2!=null && fee2>0){
//			var m2=$.DataMap();
//			m2.put("MODE", data.get("MODE2"));
//			m2.put("CODE", data.get("CODE2"));
//			m2.put("FEE", fee2);
//			$.feeMgr.insertFee(m2);//营销活动预存款剩余
//		} 
		$.endPageLoading();
	},
	function(error_code, error_info,detail) { 
    $.endPageLoading();
    MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
    });
}

function qureyMphone(){  
	var inputSn= $("#SERIAL_NUMBER_A").val(); 
	var oldSerialNum=$("#AUTH_SERIAL_NUMBER").val(); 
	
	var isCheck=$("#IS_CHECK");
	var serialnumber = $("#SERIAL_NUMBER_PRE").val();
	if(serialnumber!=inputSn)
	{
		  $("#IS_CHECK").val("");
	 }
	if(oldSerialNum==null||oldSerialNum=="")
	{
	  alert("请输入需要过户的原号码点击【查询】!");
	  $("#AUTH_SERIAL_NUMBER").focus();
	  return false;
	}
	if(inputSn==null||inputSn=="")
	{
	  alert("请输入变更的服务号码!");
	  return false;
	}
	if(inputSn==oldSerialNum){
		alert("不允许过户给自己！");
		$("#IS_CHECK").val("0");
		$("#CHECK_DESC").val("不允许过户给自己！");
		return false;
	}
	var remainFee=$("#ACTIVE_REMAIN_FEE").val();
	var modemFee=$("#MODEM_FEE").val();
	var inparms='&SERIAL_NUMBER_A='+trim(inputSn) ; 
	if(modemFee!=""&&modemFee!=null){
		inparms=inparms+"&MODEM_FEE="+modemFee;
	}
	$.beginPageLoading("变更号码验证中。。。");
	$.ajax.submit(this, "qureyMphone", inparms, '',function(data){
		 $.endPageLoading();
			var  result = data.get(0);
			$("#IS_CHECK").val(result.get("IS_CHECK"));
			$("#IS_CHECK_DEC").val(result.get("IS_CHECK_DEC"));
			$("#CHECK_DESC").val(result.get("CHECK_DESC")); 
			$("#SERIAL_NUMBER_PRE").val(result.get("SERIAL_NUMBER_PRE"));
			$("#ACCT_ID_CHANGE").val(result.get("ACCT_ID_CHANGE"));
			$("#CUST_ID_NEW").val(result.get("CUST_ID_NEW"));
			$("#USER_ID_NEW").val(result.get("USER_ID_NEW"));
			MessageBox.alert("信息提示","变更号码验证完成，请查看结果!",null,null,null);
	},
	function(error_code, error_info,detail) { 
		 $.endPageLoading();
			MessageBox.error("错误提示", error_info,'newSerialNumberPart', null, null, detail);
	});
}

function onTradeSubmit(){
	if(!verifyAll('newSerialNumberPart'))
	   {
		   return false;
	   }
	var isCheck=$("#IS_CHECK").val();
	if(isCheck!="1"){
		alert("新号码校验未通过，无法办理宽带过户。原因："+$("#CHECK_DESC").val());
		return false;
	}
	if(!window.confirm("系统将提交宽带过户受理数据信息，你确认要继续吗？")){
		return false;
	}
	return true;
}