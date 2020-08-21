//-----------------签约-----------------------------------
function refreshPartAtferAuth(data)
{	
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&SERIAL_NUMBER=' + serialNumber;
	param += '&USER_ID=' + data.get('USER_INFO').get('USER_ID');
	var pretag = data.get('USER_INFO').get('PREPAY_TAG');
	$.ajax.submit(null, 'loadInfo', param, 'busiInfoPart',function(){
		$('#PREPAY_TAG').val(pretag);
	},loadInfoError
	);
	
}

function loadInfoError(code,error_info){
	$.cssubmit.disabledSubmitBtn(true);
	//$.showErrorMessage("错误",info);
	MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, null);
}

function checkSignBank(){
	
	var userinfo = $.auth.getAuthData().get("CUST_INFO");
	var custName = userinfo.get("CUST_NAME");
	var psptId = userinfo.get("PSPT_ID");
	var psptTypeCode = userinfo.get("PSPT_TYPE_CODE");
	var bankNum = $('#BANK_ACCT_ID').val();
	var bankName = $('#BANK_ID').val();
	
	if(bankName == null || bankName == ""){
		alert("银行名称不能为空");
		return false;
	}
	if(bankNum == null || bankNum == ""){
		alert("银行账号不能为空");
		return false;
	}
	$.beginPageLoading("校验中...");
	$.ajax.submit(null, 'checkSignBank', '&USER_ACCOUNT='+bankNum+'&USER_NAME='+custName+'&USER_IDENT='+psptId+'&USER_IDENT_TYPE='+psptTypeCode+'&RECV_BANK='+bankName, null,function(data){
			$.endPageLoading();
			//alert(data.get(0));
			var discntStr = data.get(0,"X_RESULTCODE",""); 
			if(discntStr == '0'){
				//如果校验成功
				$('#checkBankSignFlag').val('1');
				alert('校验成功');
			}else{
				alert('校验失败');
			}
		},function(code,info){
			$.endPageLoading();
			$('#checkBankSignFlag').val('0');
			//$.cssubmit.disabledSubmitBtn(true);
			$.showErrorMessage("错误",info);
		} );
}

function changeType(){
	var flag = $('#PAY_TYPE').val();
	var preFlag = $('#PREPAY_TAG').val();
	
	if(flag == '1' && preFlag == '1'){//自动缴费
		$('#RECH_THRESHOLD').attr("disabled",null);
		$('#RECH_AMOUNT').attr("disabled",null);
		$('#RECH_THRESHOLD').val('10');
		$('#RECH_AMOUNT').val('50');
	}
	if(flag == '0' && preFlag == '1'){//主动缴费
		$('#RECH_THRESHOLD').val('');
		$('#RECH_AMOUNT').val('');
		$('#RECH_THRESHOLD').attr("disabled",true);
		$('#RECH_AMOUNT').attr("disabled",true);
	}
}

function checkBeforeSubmit(){

	if(!$.validate.confirmAll("SignContractALLPart")) return false;
	if($("#checkBankSignFlag").val()!="1"){
		alert("请校验银行账号!");
		return false;
	}
 	
 	return true; 
}


//-----------------解约-----------------------------------
function refreshPartAtferAuthCancel(data)
{	
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&SERIAL_NUMBER=' + serialNumber;
	param += '&USER_ID=' + data.get('USER_INFO').get('USER_ID');
	$.ajax.submit(null, 'loadInfo', param, 'busiInfoPart','',loadInfoError
	);
	
}

function cancelBeforeSubmit(){
	
	// 得到所有复选框
	var objs = $("*[name='imises']");
	// 保存要选中要解约的合约
	var selectValues = "";

	// 将所有选中的复选框拼成串以","分割
	for (var i = 0; i < objs.length; i++){
		var tempSign = objs[i];
		// 选中的复选框
		//alert('tempSign val--'+$(tempSign).val());
		if (tempSign.checked){
			selectValues = selectValues + "," + $(tempSign).val();
		}
	}
	//alert(selectValues.substr(1));
	
	if(selectValues == ""){
		alert("请选择需要解约的合约!");
		return false;
	}
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&SELECT_VALUES='+selectValues.substr(1);
	
	if(confirm('您确定解除选中的签约吗？')){
		$.cssubmit.addParam(param);
		return true;
	}else{
		return false;
	}
}


//----UIP查询
function queryResultsInfo()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart"))
	{
		return false;
	}

     $.beginPageLoading("数据加载中！");
	    $.ajax.submit('QueryCondPart,navt', 'queryResultsInfo', '', 'QueryListPart', function() {
	    	$.endPageLoading();
	    }, function(error_code, error_info) {
	        $.endPageLoading();
	        alert(error_info);
	    });
}
