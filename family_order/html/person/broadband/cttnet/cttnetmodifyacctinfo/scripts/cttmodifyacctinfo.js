function refreshPartAtferAuth(data)
{
	ajaxModiAcctNameNew();
	
	var param =  "&ACCT_ID="+data.get("ACCT_INFO").get("ACCT_ID")
					+"&ACCT_INFO="+data.get("ACCT_INFO").toString()
					+"&USER_ID="+data.get("USER_INFO").get("USER_ID")
					+"&EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE");
	$.ajax.submit('', 'loadChildInfo',param, 'AcctInfoPart', function(data){
		chgAcctInfoInit();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}


/*初始化时，对帐户类型的处理*/
function chgAcctInfoInit()
{
	var acctinfo_PAY_MODE_CODE = $("#acctinfo_PAY_MODE_CODE").val();
	
	/*如果是现金*/
	if(acctinfo_PAY_MODE_CODE == '0')
	{
		$("#acctinfo_PAY_NAME").attr('disabled',false);
		$("#acctinfo_PAY_MODE_CODE").attr('disabled',false);
		$("#REMARK").attr('disabled',false);
		$("#acctinfo_RSRV_STR6").attr('disabled',true);
//		$("#acctInfo_IS_RECFEE").attr('disabled',false);
		$("#acctinfo_RSRV_STR3").attr('disabled',false);

		$("#comminfo_SUPERBANK_CODE").attr('nullable','yes');
		$("#acctinfo_BANK_CODE").attr('nullable','yes');
		$("#acctinfo_BANK_ACCT_NO").attr('nullable','yes');
	}
	/*如果是非现金*/
	else if(acctinfo_PAY_MODE_CODE != '0')
	{
		$("#comminfo_SUPERBANK_CODE").attr('disabled',false);
		$("#acctinfo_BANK_ACCT_NO").attr('disabled',false);
		$("#acctinfo_BANK_CODE").attr('disabled',false);
		$("#acctinfo_CONTRACT_NO").attr('disabled',false);
		$("#acctinfo_PAY_NAME").attr('disabled',false);
		$("#acctinfo_PAY_MODE_CODE").attr('disabled',false);
		$("#acctinfo_RSRV_STR6").attr('disabled',false);
//		$("#acctInfo_IS_RECFEE").attr('disabled',false);
		$("#acctinfo_RSRV_STR3").attr('disabled',false);

		$("#bankFld").attr('disabled',false);
		$("#REMARK").attr('disabled',false);
		
		$("#bankAcctNo").attr("className","e_required");
		$("#brand").attr("className","e_required");
		$("#superBank").attr("className","e_required");
		$("#bankAcctNo").attr("claclassNamess","e_required");
		
		$("#comminfo_SUPERBANK_CODE").attr('nullable','no');
		$("#acctinfo_BANK_CODE").attr('nullable','no');
		$("#acctinfo_BANK_ACCT_NO").attr('nullable','no');
	}else{
		$("#acctinfo_CONTRACT_NO").attr('disabled',true);
		$("#comminfo_SUPERBANK_CODE").attr('disabled',true);
		$("acctinfo_BANK_ACCT_NO").attr('disabled',true);
		$("#acctinfo_BANK_CODE").attr('disabled',true);
//		$("#acctInfo_IS_RECFEE").attr('disabled',false);
		$("#acctinfo_RSRV_STR3").attr('disabled',false);
	}
}

//检查帐户类别是不是选择托收，如果是则激活上级银行、银行名称、银行帐号组件
function checkPaymode() {
	var acctinfo_PAY_MODE_CODE = $("#acctinfo_PAY_MODE_CODE");
	var comminfo_SUPERBANK_CODE = $("#comminfo_SUPERBANK_CODE");
	var acctinfo_BANK_CODE = $("#acctinfo_BANK_CODE");
	var acctinfo_BANK_ACCT_NO = $("#acctinfo_BANK_ACCT_NO");
	var acctinfo_CONTRACT_NO = $("#acctinfo_CONTRACT_NO");
	var acctinfo_RSRV_STR6 = $("#acctinfo_RSRV_STR6");
	
	var acctDayTag = $("#ACCT_DAY_TAG").val();
	var nextAcctDay = $("#NEXT_ACCT_DAY").val();
	var bookAcctDay = $("#BOOK_ACCT_DAY").val();
	
	if (acctinfo_PAY_MODE_CODE != null && acctinfo_PAY_MODE_CODE.val() != '0') {
	// 分散账期
		if(acctDayTag != '9') {
			if (acctDayTag == '0') {
				if (nextAcctDay == '0') {
					if (confirm('尊敬的客户，您当前用户账期为非自然月账期，办理托收业务需要更改为自然月账期，下账期生效，生效时间为在' + bookAcctDay + '，是否继续办理该业务？')) {
					} else {
						acctinfo_PAY_MODE_CODE.val( '0');
						$('#CSSUBMIT_BUTTON').attr('disabled',true);
					}
				} else {
	
					if (confirm('该用户存在预约用户账期，账期生效时间为' + bookAcctDay + '，账期生效后才能办理该业务！')) {
						acctinfo_PAY_MODE_CODE.val( '0');
						$('#CSSUBMIT_BUTTON').attr('disabled',true);
					} else {
						acctinfo_PAY_MODE_CODE.val( '0');
						$('#CSSUBMIT_BUTTON').attr('disabled',true);
					}
				}
			} else {
				if (nextAcctDay != '0' && nextAcctDay != '1') {
					alert('尊敬的客户，您当前用户账期为自然月账期，该用户存在预约用户账期，账期生效时间为' + bookAcctDay + '，账期生效后才能办理该业务！');
					acctinfo_PAY_MODE_CODE.val( '0');
					$('#CSSUBMIT_BUTTON').attr('disabled',true);
				}
			}
		}
	// 激活组件
	comminfo_SUPERBANK_CODE.attr('disabled',false);
	acctinfo_BANK_ACCT_NO.attr('disabled',false);
	acctinfo_BANK_CODE.attr('disabled',false);
	acctinfo_CONTRACT_NO.attr('disabled',false);
	acctinfo_RSRV_STR6.attr('disabled',false);
	
	$("#bankFld").attr('disabled',false);

	$("#bankAcctNo").attr("className","e_required");
	$("#brand").attr("className","e_required");
	$("#superBank").attr("className","e_required");
	$("#bankAcctNo").attr("claclassNamess","e_required");
	
	$("#comminfo_SUPERBANK_CODE").attr('nullable','no');
	$("#acctinfo_BANK_CODE").attr('nullable','no');
	$("#acctinfo_BANK_ACCT_NO").attr('nullable','no');
}
	
	if(acctinfo_PAY_MODE_CODE != null && (acctinfo_PAY_MODE_CODE.val() == '0' ||acctinfo_PAY_MODE_CODE.val() == '')) {
		// 设置组件不可用
		$('#CSSUBMIT_BUTTON').attr('disabled',false);
		var cb=$("#acctinfo_BANK_CODE");

		$("#comminfo_SUPERBANK_CODE").val('');
		$("#acctinfo_BANK_ACCT_NO").val('') ;
		$("#POP_acctinfo_BANK_CODE").val('') ;
		$("#acctinfo_BANK_CODE").val('') ;
		$("#acctinfo_RSRV_STR6").val('') ;
		$("#acctinfo_CONTRACT_NO").val('') ;

		comminfo_SUPERBANK_CODE.attr('disabled',true);
		acctinfo_BANK_CODE.attr('disabled',true);
		acctinfo_BANK_ACCT_NO.attr('disabled',true);
		acctinfo_CONTRACT_NO.attr('disabled',true);
		acctinfo_RSRV_STR6.attr('disabled',true);
		$("#bankFld").attr('disabled',true);
		
		$("#bankAcctNo").attr("className","");
		$("#brand").attr("className","");
		$("#superBank").attr("className","");
		$("#bankAcctNo").attr("claclassNamess","");
		
		$("#comminfo_SUPERBANK_CODE").attr('nullable','yes');
		$("#acctinfo_BANK_CODE").attr('nullable','yes');
		$("#acctinfo_BANK_ACCT_NO").attr('nullable','yes');
	}
	
}

function checkBank(){
	var  PAY_MODE_CODE = $("#acctinfo_PAY_MODE_CODE").val();
	var  SUPERBANK_CODE = $("#comminfo_SUPERBANK_CODE").val();
	if(PAY_MODE_CODE == "0"){
		return false;	
	}
	if(PAY_MODE_CODE == null || PAY_MODE_CODE == ""){
		alert("请选择账户类别！");
		return false;	
	}
	if(SUPERBANK_CODE == null || SUPERBANK_CODE == ""){
		alert("上级银行不能为空！");
		return false;	
	}
}
function checkSuperBank(){
		$("#POP_acctinfo_BANK_CODE").val('') ;
}



//业务提交
function onTradeSubmit(){
	//提交前校验
	if(checkBeforeSubmit()) {//必填项校验
		var param ="&USER_ID="+ $.auth.getAuthData().get('USER_INFO').get("USER_ID")+"&EPARCHY_CODE="+$.auth.getAuthData().get('USER_INFO').get("EPARCHY_CODE");
		////验证银行账号的唯一性以及判断是否是统付用户
		$.ajax.submit('AcctInfoPart', 'checkUserSpecialepay', param, '', function(data){
			if(data.get('X_RESULT_CODE')!='' && data.get('X_RESULT_CODE')=='1'){
				if(!confirm(data.get("X_RESULTINFO"))){
					return false;
				}
			}
			checkBusiness();//校验邮编和邮寄地址
			$.cssubmit.submitTrade();//主动执行提交事件
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
			return false;
	    });
	}
	return false;
}

//提交校验
function checkBeforeSubmit()
{  
   if(!$.validate.verifyField($("#acctinfo_PAY_NAME")[0]) || !$.validate.verifyField($("#acctinfo_PAY_MODE_CODE")[0]) 
        || !$.validate.verifyField($("#comminfo_SUPERBANK_CODE")[0]) || !$.validate.verifyField($("#acctinfo_BANK_ACCT_NO")[0])
//        || !$.validate.verifyField($("#acctInfo_IS_RECFEE")[0])
        )
     {
        return false;
      }

   if($("#acctinfo_BANK_CODE").val() == '' && $("#acctinfo_PAY_MODE_CODE").val() != '0')
   {
   		alert('银行名称不能为空');
   		return false;
   }

   $.cssubmit.addParam("&SERIAL_NUMBER="+$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER"));
   return true;
}

function checkBusiness()
{
	var postAddr = $('#RSRV_STR3').val();
	var postCode = $('#RSRV_STR10').val();
	var payModeCode = $('#acctinfo_PAY_MODE_CODE').val();
	var str = '';
	if(payModeCode != "0" || payModeCode != "5")
	{
		if(postAddr == "" || postCode == "")
		{
			str = '请到批量修改银行资料界面,修改邮编和邮寄地址！'
		}
		else
		{
			str = '请核查邮编和邮寄地址,如果不对,请到批量修改银行资料界面修改！';
		}
		alert(str);
	}
	
}

/**
 * @deprecated
 * REQ201710090024-关于铁通迁移固话用户办理界面优化及套餐开发的需求-NGBOSS-铁通模块-个人业务-其他业务-帐户资料变更（铁通），
 * 请业务支撑部收回是否收改号费选择，下线收费功能 @auth zhaohj3 @date 2017-2-6 8:50:22
 */
function ajaxModiAcctName()
{
	var isRecFee = $('#acctInfo_IS_RECFEE').val();
	var payName = $('#acctinfo_PAY_NAME').val();
	var oldPayName = $('#acctinfo_OLD_PAY_NAME').val();
	var feeMode = $('#FEE_MODE').val();
	var fee = $('#FEE').val()
	var feeTypeCode = $('#FEE_TYPE_CODE').val();
	var tradeTypeCode = "9728";
	if(payName != oldPayName && feeMode == '')
	{
		if(isRecFee == '1')
		{
			$.ajax.submit('', 'ajaxModiAcctName', '', '', function(data){
				if(data.length>0)
				{
					var payName = $('#acctinfo_PAY_NAME').val();
					var oldPayName = $('#acctinfo_OLD_PAY_NAME').val();
					
					fee = data.get(0).get("FEE");
					feeMode = data.get(0).get("FEE_MODE");
					feeTypeCode = data.get(0).get("FEE_TYPE_CODE");
					
					var str1 = '';
					str1 = str1 + '您修改了账户名称，并选择了收取改名费，将收取[' + (parseFloat(fee)/100).toFixed(2) + ']元手续费' + "\n";
					str1 = str1 + '原账户名称：' + oldPayName + "\n";
					str1 = str1 + '新账户名称：' + payName + "\n";
					alert(str1);
					
					var obj =new Wade.DataMap();
					obj.put("TRADE_TYPE_CODE",tradeTypeCode)
					obj.put("MODE",feeMode);
					obj.put("CODE", feeTypeCode);
					obj.put("FEE", parseInt(fee));
					$.feeMgr.removeFee(tradeTypeCode, feeMode, feeTypeCode); 
					$.feeMgr.insertFee(obj);

					$('#FEE_MODE').val(feeMode);
					$('#FEE_TYPE_CODE').val(feeTypeCode);
					$('#FEE').val(fee);
				}
				$.endPageLoading();
			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
		    });
		}
	}
	else if(payName != oldPayName && feeMode != '')
	{
		var obj =new Wade.DataMap();
		obj.put("TRADE_TYPE_CODE",tradeTypeCode)
		obj.put("MODE",feeMode);
		obj.put("CODE", feeTypeCode);
		obj.put("FEE", fee);
		if(isRecFee == '1')
		{
			$.feeMgr.removeFee(tradeTypeCode, feeMode, feeTypeCode); 
			$.feeMgr.insertFee(obj);
		}
		else
		{
			$.feeMgr.removeFee(tradeTypeCode, feeMode, feeTypeCode); 
		}
	}
	else if(payName == oldPayName && feeMode != '')
	{
		var obj =new Wade.DataMap();
		obj.put("TRADE_TYPE_CODE",tradeTypeCode)
		obj.put("MODE",feeMode);
		obj.put("CODE", feeTypeCode);
		obj.put("FEE", parseInt(fee));
		$.feeMgr.removeFee(tradeTypeCode, feeMode, feeTypeCode); 
	}
}

/**
 * REQ201710090024-关于铁通迁移固话用户办理界面优化及套餐开发的需求-NGBOSS-铁通模块-个人业务-其他业务-帐户资料变更（铁通），
 * 请业务支撑部收回是否收改号费选择，下线收费功能 @auth zhaohj3 @date 2017-2-6 8:50:22
 */
function ajaxModiAcctNameNew()
{
	var payName = $('#acctinfo_PAY_NAME').val();
	var oldPayName = $('#acctinfo_OLD_PAY_NAME').val();
	var feeMode = $('#FEE_MODE').val();
	var fee = $('#FEE').val()
	var feeTypeCode = $('#FEE_TYPE_CODE').val();
	var tradeTypeCode = "9728";
	if(payName != oldPayName && feeMode != '')
	{
		var obj =new Wade.DataMap();
		obj.put("TRADE_TYPE_CODE",tradeTypeCode)
		obj.put("MODE",feeMode);
		obj.put("CODE", feeTypeCode);
		obj.put("FEE", fee);
		$.feeMgr.removeFee(tradeTypeCode, feeMode, feeTypeCode); 
	}
	else if(payName == oldPayName && feeMode != '')
	{
		var obj =new Wade.DataMap();
		obj.put("TRADE_TYPE_CODE",tradeTypeCode)
		obj.put("MODE",feeMode);
		obj.put("CODE", feeTypeCode);
		obj.put("FEE", parseInt(fee));
		$.feeMgr.removeFee(tradeTypeCode, feeMode, feeTypeCode); 
	}
}
