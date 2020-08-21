/**
 * 当客户标志类型变为手机号时，自动填写客户标示号码为手机号码
 */
function changeIDValue()
{
	var routeType  =  $("#cond_ROUTE").val();
	var idType     =  $("#cond_IDTYPE").val();
	var phoneNum =  $("#cond_SERIAL_NUMBER").val();
	if("01"==routeType && "01"==idType)  
	{   
		$("#cond_IDVALUE").val( phoneNum); 
	}
}

/**
 * 当客户标示号码为手机号码时，同时将手机号码修改成客户标示号码相同
 */
function changeMobileNumValue()
{
	var routeType  =  $("#cond_ROUTE").val();
	var type = $("#cond_IDTYPE").val();
	if (type == "01" && routeType == "01"){
		var tel = $("#cond_IDVALUE").val();
		$('#cond_SERIAL_NUMBER').val(tel);
	}
}

/**
 *  校验手机支付充值
 */
  function iBossAccountPayAuthCheck()
  {
	var routeType = $("#cond_ROUTE").val();
	var provCode = $("#cond_PROVINCE_CODE").val();
	if("02" == routeType)
	{
		if("" == provCode)
		{
			MessageBox.alert("提示","按号码归属省路由，号码归属省不能为空");
			return false;
		}
	}
	
	var verifyType = $("#cond_VERIFY_TYPE").val();
	  if(verifyType == "0")
	  {
		  if($("#cond_CARD_TYPE").val() == "" ||  $("#cond_IDCARDNUM").val() == "")
		{
			  MessageBox.alert("提示","证件校验方式，证件号码和类型不能为空");
			  return false ;
		}
	  }else
	  {
		  if($("#cond_USER_PASSWD").val() == "")
			{
			   MessageBox.alert("提示","密码校验方式，客服密码不能为空");
				  return false;
			}
	  }
	 
	
	  if(!$.validate.verifyAll("routePart")) {
			return false;
		}
	  $.beginPageLoading("正在查询数据...");
		
		ajaxSubmit('routePart,custInfoPart', 'IBossAccountPayCheck', null, 'AcceptPart', function(data){
			if(data.get('ALERT_INFO') != '')
			{
				MessageBox.alert("提示",data.get('ALERT_INFO'));
			}
			if(data.get('SUCCESS_FLAG') == 'true')
			{
				$.cssubmit.disabledSubmitBtn(false);
			}
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			MessageBox.error("错误",error_info);
		}
		 );
  }
  
  
  /*
    手机支付一级boss充值    
  */
  function iBossAccountPayCheck()
  {
	  
	  if(!$.validate.verifyAll('AcceptPart')){
			return false;
		}

		/*if( !isNaN($('#cond_AMOUNT').val()) && !isNaN($('#cond_RSP_MAX_CASH').val())){
			if(parseInt($("#cond_AMOUNT").val()) > parseInt($("#cond_RSP_MAX_CASH").val())){
			 	alert("充值金额不能大于最大可充值金额:"+parseInt($("#cond_RSP_MAX_CASH").val())+"元");
			 	return false;
			}
		}*/
	  
	  $.cssubmit.bindCallBackEvent(acctPayCallBack);		//设置提交业务后回调事件
		return true;
  }
  
  /*
  function afterSubmit(data){
		$.showSucMessage("业务受理成功","工单流水号"+data.get("TRADE_ID"));
	}*/
  
  /**
     检验方式变化时
  **/
  function checkVerify()
  {
	  var verifyType = $("#cond_VERIFY_TYPE").val();
	  if(verifyType == "0")
	  {
		  $("#cardTypeSpan").css("display","");
		  $("#cardNumSpan").css("display","");
		  $("#passwordSpan").css("display","none");
	  }
	  if(verifyType == "1")
	  {
		  $("#cardTypeSpan").css("display","none");
		  $("#cardNumSpan").css("display","none");
		  $("#passwordSpan").css("display","");
	  }
		  
	  return ;
  }
 
  

function acctPayCallBack(data){
	var content;
	if (data &&data.get("PRINT_FLAG")=="" ){
		content = "客户订单标识："+data.get("ORDER_ID")+"<br/>点【确定】继续业务受理。";
	}else{
		alert(data.get("PRINT_FLAG"));
		return;
	}
	$.cssubmit.showMessage("success", "业务受理成功", content, true);
	
	$.printMgr.bindPrintEvent(acctPayTrade);	
}

function acctPayTrade(datas){

	var eparchyCode = "898";
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	var custName = $("#cond_SERIAL_NUMBER").val();
	var params = "&SERIAL_NUMBER="+serialNumber;
	params += "&CUST_NAME="+custName;
	params += "&AMOUNT="+$("#cond_AMOUNT").val();
	params += "&TRADE_ID="+datas.get("TRADE_ID");
	$.beginPageLoading("加载打印数据。。。");
	$.ajax.submit(null, "loadPrintData", params, null, 
		function(data){
			$.endPageLoading();
			//设置打印数据
			$.printMgr.setPrintData(data.get("PRINT_DATA"));
			$.printMgr.params.put("PRT_TYPE", "0");
			$.printMgr.params.put("EPARCHY_CODE", eparchyCode);
			//启动打印
			$.printMgr.printReceipt();
		},
		function(errorcode, errorinfo){
			$.endPageLoading();
			MessageBox.alert("信息提示",errorinfo);
		},function(){
			$.endPageLoading();
			alert("加载打印数据超时");
	});	
}