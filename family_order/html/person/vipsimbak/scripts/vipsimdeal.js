function refreshPartAtferAuth(data){
	$.ajax.submit('', 'loadChildInfo',
			"&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString()+"&ACCT_INFO="+data.get("ACCT_INFO").toString(), 
			'vipInfoPart,simInfoPart', function(){$("#QRY_TAG").val("1");
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
function refreshPartAtferAuth2(data){
	$.ajax.submit('', 'loadChildInfo',
			"&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString()+"&ACCT_INFO="+data.get("ACCT_INFO").toString(), 
			'vipInfoPart,simInfoPart', function(){
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
function refreshPartAtferAuth3(data){
	$.ajax.submit('', 'loadChildInfo',
			"&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString()+"&ACCT_INFO="+data.get("ACCT_INFO").toString(), 
			'vipInfoPart,simInfoPart', function(){
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function checksimbak() {
    /**
    *输入sim卡号与客户备卡比较校验
    */
	var qry_tag =$("#QRY_TAG").val();
	if(qry_tag == null || qry_tag ==""){
		alert("请先查询服务号码！");
      	return false;
	}
    var cardNo = $('#BAK_CARD_NO').val();
    if(cardNo==null || cardNo=="")
    {
      	alert("请输入SIM卡号！");
      	return false;
    }
    
    $.ajax.submit('simBakPart,vipInfoPart', 'checkBakCard', "&CARD_NO="+cardNo+"&SERIAL_NUMBER="+$('#AUTH_SERIAL_NUMBER').val(), 'simBakPart', function(data){
    	 var feeInfo = data.get(0);
    	 if(feeInfo != null){
    		 fee = feeInfo.get('DEVICE_PRICE');
    		 	if(fee>0){
    			 var feeObj = $.DataMap();
    			 	feeObj.put("TRADE_TYPE_CODE", "146");  
    				feeObj.put("MODE","0");
    				feeObj.put("CODE","10");
    				feeObj.put("FEE",fee);
    				feeObj.put("PAY",fee);
    				$.feeMgr.clearFeeList(); 
    				$.feeMgr.insertFee(feeObj); 
    	 	}
    	 }
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
    
}
var tableRowClick = function(){

	$("#BAK_CARD_NO").val($.table.get("simbakinfo").getRowData().get("col_SIM_CARD_NO"));
	$("#NEW_IMSI").val($.table.get("simbakinfo").getRowData().get("col_NEW_IMSI"));
	$("#SEND_DATE").val($.table.get("simbakinfo").getRowData().get("col_SEND_DATE"));
	$("#OPC").val($.table.get("simbakinfo").getRowData().get("col_OPC"));
	
}

var tableRowClickCancel = function(){
	if($.table.get("simbakinfo").getRowData().get("col_ACT_TAG")=="启用"){
		alert("已经启用的备卡不能办理取消");
		return false;
	}
	
	$("#BAK_CARD_NO").val($.table.get("simbakinfo").getRowData().get("col_SIM_CARD_NO"));
	$("#NEW_IMSI").val($.table.get("simbakinfo").getRowData().get("col_NEW_IMSI"));
	$("#SEND_DATE").val($.table.get("simbakinfo").getRowData().get("col_SEND_DATE"));
	
}



function beforeReadCard(){
	var sn = $("#AUTH_SERIAL_NUMBER").val();
	$.simcard.setSerialNumber(sn);
	return true;
}


function afterReadCard(data){
	var isWrited = data.get("IS_WRITED");//用来判断卡是否被写过
	if(isWrited == "1"){
		setValue(data);
		checksimbak();
	}else{
     	var emptyCardId = data.get("EMPTY_CARD_ID");
     	var sn = $("#AUTH_SERIAL_NUMBER").val();
     	$.beginPageLoading();
    	$.ajax.submit('', 'checkEmptyCard', "&EMPTY_CARD_ID="+emptyCardId+"&SERIAL_NUMBER="+sn, '', function(data){
    		$.endPageLoading();
    	},
    	function(error_code,error_info){
    		$.endPageLoading();
    		$("#writeCardBtn").attr("disabled",true);
    		alert("请注意：写卡之后，补换卡业务也无法正常办理："+error_info);
    	});
	}
}

function afterWriteCard(data){
	if(data.get("RESULT_CODE")=="0"){
		$.simcard.readSimCard();
	}
}

function setValue(data){
	var simCardNo = data.get("ICCID");
	if(simCardNo == ""){
		simCardNo = data.get("SIM_CARD_NO"); 
	}
	$("#BAK_CARD_NO").val(simCardNo);
}

//业务提交
function onTradeSubmitApp(){
	if(!$.validate.verifyAll("simBakPart")) {
		return false;
	}
	if($('#BOOK_DATE').val()==""){
		alert("请先校验卡号");
		return false;
	}
	 return true;
}
function onTradeSubmitAct(){
	
	if(!$.validate.verifyAll("simBakPart")) {
		return false;
	}
	if($('#BAK_CARD_NO').val()=="" ||$('#SEND_DATE').val()==""){
		alert("请在SIM卡信息列表选择要激活的记录");
		return false;
	}
	 return true;
}
function onTradeSubmitCancel(){
	if(!$.validate.verifyAll("simBakPart")) {
		return false;
	}
	if($('#BAK_CARD_NO').val()=="" ||$('#SEND_DATE').val()==""){
		alert("请在SIM卡信息列表选择要取消的记录");
		return false;
	}
	 return true;
}



















