function getGrpInfoBySn()
{	  
	var grpSn = $('#cond_GROUP_SERIAL_NUMBER').val();
	
	if (grpSn == ""){
        MessageBox.alert("提示信息","集团产品编码不能为空！");
		return false;
	}	
	var params = '&cond_GROUP_SERIAL_NUMBER='+grpSn;
	$.beginPageLoading();
	$.ajax.submit("","getGrpUCAInfoBySn",params,"acctInfoPart,acctListPart",
			function(data){
			    var groupInfo =  data.get("GRP_CUST_INFO");
			    
				var productDescInfo = data.get("PRODUCT_DESC_INFO");
				var acctInfos = data.get("GRP_ACCT_INFO");
				
				var productId = productDescInfo.get("PRODUCT_ID");
				var productName = productDescInfo.get("PRODUCT_NAME");
				var acctId = acctInfos.get("ACCT_ID");
				$('#SPAN_GROUPCUSTINFO_PRODUCT_ID').html(productId);
				$('#SPAN_GROUPCUSTINFO_PRODUCT_NAME').html(productName);
				$('#SPAN_GROUPCUSTINFO_ACCT_ID').html(acctId);
				
				var cust_id = groupInfo.get('CUST_ID');
				
				$('#group_CUST_ID').val(cust_id);
				$('#group_GROUP_ID').val(groupInfo.get("GROUP_ID"));
				$('#group_OLD_ACCT_ID').val(acctId);
				var userInfo = data.get("GRP_USER_INFO");
				$('#group_SERIAL_NUMBER').val(userInfo.get("SERIAL_NUMBER"));
				$('#group_USER_ID').val(userInfo.get("USER_ID"));
				queryAcctInfos(cust_id);
		   		$.endPageLoading();
		   		
			},
			function(error_code,error_info,derror){				
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });

}

function queryAcctInfos(cust_id)
{
	$.beginPageLoading();

	$.ajax.submit(this,'getAcctByCustId','&CUST_ID='+cust_id,'acctListPart',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});

}

//查账户详情
function queryAcctDetailInfoByAcctId(el){
	cleanAcctDescInfo();
	var acctId = $(el).attr("acctId");
	if(acctId == '')
	{	
		return false;
	}
	
	$.beginPageLoading();
	
	$.ajax.submit(this,'getAcctByActId','&ACCT_ID=' + acctId, 'AcctDetailPart',function(data){
		$.endPageLoading();
		showPopup('myPopup','acct-popup-param_item');

	},function(error_code,error_info,derror){
		
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	

}

//编辑账户信息时查账户详情
function queryEditAcctDetailInfoByAcctId(el){
	cleanAcctDescInfo();
	var acctId = $(el).attr("updateAcctId");
	if(acctId == '')
	{	
		return false;
	}
	
	$.beginPageLoading();
	
	$.ajax.submit(this,'queryAcctInfo','&ACCT_ID=' + acctId, 'EditAcctPart',function(data){
		$.endPageLoading();
		showPopup('myPopup','acct-popup-edit_item');
		var acctType = data.get("PAY_MODE_CODE");
		if(acctType == "0"){
			$("#li_CONTRACT_NO").css("display", "none");
			$("#li_SUPER_BANK").css("display", "none");
			$("#li_BANK_NAME").css("display", "none");
			$("#li_BANK_ACCT_NO").css("display", "none");
			$("#li_CONTACT_PHONE").css("display", "none");
			$("#li_ACCT_CONTACT").css("display", "none");
			$("#li_CONSIGN_AMOUNT").css("display", "none");
		}
	},function(error_code,error_info,derror){
		
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	

}
//清空账户详情
function cleanAcctDescInfo() {
	
	$('#acct_PAY_NAME').val('');
	$('#acct_PAY_MODE_NAME').val('');
	$('#acct_SUPER_BANK_NAME').val('');
	$('#acct_BANK_NAME').val('');
	$('#acct_CONTRACT_NO').val('');
	$('#acct_BANK_ACCT_NO').val('');
	$('#acct_ALLNEW_BALANCE').val('');
	$('#acct_OPEN_DATE').val('');

	
}
function queryBankInfo() {

	var superBank = $("#cond_SUPER_BANK_CODE").val();	
	$.ajax.submit(this,'queryBankInfo','&SUPER_BANK=' + superBank, 'BANK_PART',function(data){
		$.endPageLoading();		
	},function(error_code,error_info,derror){
		
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}

function queryBankInfoForAdd() {

	var superBank = $("#add_SUPER_BANK_CODE").val();	
	$.ajax.submit(this,'queryBankInfo','&SUPER_BANK=' + superBank, 'BANK_PART_ADD',function(data){
		$.endPageLoading();		
	},function(error_code,error_info,derror){
		
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}
function afterChangeAcctType() {

	var acctType = $("#cond_PAY_MODE_CODE").val();	
	if(acctType == "0"){
		$("#li_CONTRACT_NO").css("display", "none");
		$("#li_SUPER_BANK").css("display", "none");
		$("#li_BANK_NAME").css("display", "none");
		$("#li_BANK_ACCT_NO").css("display", "none");
		$("#li_CONTACT_PHONE").css("display", "none");
		$("#li_ACCT_CONTACT").css("display", "none");
		$("#li_CONSIGN_AMOUNT").css("display", "none");

	}else if (acctType == "1" || acctType == "2" || acctType == "3"){
		
		$("#li_CONTRACT_NO").css("display", "");
		$("#li_SUPER_BANK").css("display", "");
		$("#li_SUPER_BANK").addClass("required");
		$("#li_BANK_NAME").css("display", "");
		$("#li_BANK_NAME").addClass("required");
		$("#li_BANK_ACCT_NO").css("display", "");
		$("#li_BANK_ACCT_NO").addClass("required");
		$("#li_CONTACT_PHONE").css("display", "");
		$("#li_ACCT_CONTACT").css("display", "");
		$("#li_CONSIGN_AMOUNT").css("display", "");
	}
	
}

function afterChangeAcctTypeForAdd() {

	var acctType = $("#add_PAY_MODE_CODE").val();	
	if(acctType == "0"){
		$("#li_CONTRACT_NO_ADD").css("display", "none");
		$("#li_SUPER_BANK_ADD").css("display", "none");
		$("#li_BANK_NAME_ADD").css("display", "none");
		$("#li_BANK_ACCT_NO_ADD").css("display", "none");
		$("#li_CONTACT_PHONE_ADD").css("display", "none");
		$("#li_ACCT_CONTACT_ADD").css("display", "none");		
		$("#li_CONSIGN_AMOUNT_ADD").css("display", "none");

	}else if (acctType == "1" || acctType == "2" || acctType == "3"){
		
		$("#li_CONTRACT_NO_ADD").css("display", "");
		$("#li_SUPER_BANK_ADD").css("display", "");
		$("#li_SUPER_BANK_ADD").addClass("required");

		$("#li_BANK_NAME_ADD").css("display", "");
		$("#li_BANK_NAME_ADD").addClass("required");

		$("#li_BANK_ACCT_NO_ADD").css("display", "");
		$("#li_BANK_ACCT_NO_ADD").addClass("required");

		$("#li_CONTACT_PHONE_ADD").css("display", "");
		$("#li_ACCT_CONTACT_ADD").css("display", "");
		$("#li_CONSIGN_AMOUNT_ADD").css("display", "");
	}
	
}
function checkAcct(el){
	
	$(el).siblings().removeClass('checked');
	$(el).addClass("checked");
	var selAcctId = $(el).attr("selAcctId");
	$("#group_SEL_ACCT_ID").val(selAcctId);
}

//编辑账户信息提交
function submitAcct(obj){ 
    var consignAmount = $("#cond_CONSIGN_AMOUNT").val();
	if(!checkMoney(consignAmount)){
		MessageBox.alert("提示信息","请输入正确的金额格式！");
		$("#cond_CONSIGN_AMOUNT").val('');
		return false;
	}
		
	 if($("#cond_BANK_CODE").val() == '' && $("#cond_PAY_MODE_CODE").val() != '0')
	 {
		MessageBox.alert("提示信息","银行名称不能为空!");
   		return false;
	 }
	 
	 if($("#cond_BANK_ACCT_NO").val() == '' && $("#cond_PAY_MODE_CODE").val() != '0')
	 {
		MessageBox.alert("提示信息","银行账号不能为空！");
   		return false;
	 }
	 
	 var custId = $("#group_CUST_ID").val();
	 var grpSn = $("#group_SERIAL_NUMBER").val();
	 var productId = $("#GROUP_PRODUCT_ID").val();
	 
	 
	 var param  = "state="+obj;
		 param += "&productId="+productId;
		 param += "&CUST_ID_HIDE="+custId;
		 param += "&GrpSn="+grpSn;
		$.beginPageLoading();
		$.ajax.submit('EditAcctPart','onSubmitBaseTrade', param,'EditAcctPart',
				function(data)
			    { 
			        $.endPageLoading(); 
			        var tradeData;
			    	if(data instanceof $.DatasetList){
			    		tradeData = data.get(0);
			    	}else if(data instanceof $.DataMap){
			    		tradeData = data;
			    	}
			        var x_resultcode = tradeData.get("x_resultcode","0");
			        if(x_resultcode == '-1'){
			      	  ifcheck= false;
			      	MessageBox.alert(data.get("x_resultinfo"));
			      		return;
			        }
			        if(tradeData.get("HAS_PAYMODE_CHGPRIV", false)){
			        	MessageBox.alert("提示信息","您已特殊办理银行托收业务，为避免停机给您造成不便，请您务必及时缴清往月欠费。");
			        }
			        
					var obj = JSON.parse(data);
					MessageBox.success("业务受理成功!", "订单号: "+obj[0].ORDER_ID,function(btn){
						if("ok" == btn){
							var custId = $("#group_CUST_ID").val();
							queryAcctInfos(custId);
						}
					});
					$.endPageLoading();
				},
				function(error_code,error_info,derror)
				{
					$.endPageLoading(); 
					showDetailErrorInfo(error_code,error_info,derror);	
			    }
			 );   
	
} 
//删除当前账户
function deleteAcctInfo(){
	
	
	
}


//拆分提交
function onSubmitBaseTradeCheck()
{
	var curAcctId = $('#SPAN_GROUPCUSTINFO_ACCT_ID').html();
	var selAcctId = $('#group_SEL_ACCT_ID').val();
	var groupId = $('#group_GROUP_ID').val();
	var custId = $('#group_CUST_ID').val();
	var grpSn = $('#group_SERIAL_NUMBER').val();
	var oldAcctId = $('#group_OLD_ACCT_ID').val();
	var userId = $('#group_USER_ID').val();

	if(selAcctId == '')
	{
		MessageBox.alert("提示信息","请选择帐户!");
		return false;
	}
	
	if(curAcctId == selAcctId)
	{		
		MessageBox.alert("提示信息","您选择的帐户与您当前帐户相同，请重新选择!!");
		return false ;
	}
	
	var groupSn = $('#cond_GROUP_SERIAL_NUMBER').val();
MessageBox.confirm("提示信息", "账户拆分后代付费关系不会变更，代付用户费用仍会通过原账户收取" +
		"，如需办理代付关系变更请在账户拆分后另行处理。", function(btn) {
		if ("ok" == btn) {
	MessageBox.confirm("提示信息", "账户拆分为下月生效!", function(btn){
		if("ok" == btn){
			var msg = "您确定要将集团产品【" + groupSn + "】拆分到账户【" + selAcctId + "】上吗？";
			MessageBox.confirm("提示信息", msg, function(btn){
				if("ok" == btn){
					$.beginPageLoading();

					var param = "&SERIAL_NUMBER=" + groupSn ;
					param += "&NEW_ACCT_ID=" + selAcctId;
					param += "&OLD_ACCT_ID=" + curAcctId;
					param += "&CUST_ID=" + custId ;
					param += "&USER_ID=" + userId;
					param += "&GROUP_ID=" + groupId ;

					
					$.ajax.submit(this,'onSubmit',param,'GroupInfoPart,tabPart',function(data){
						var obj = JSON.parse(data);
						MessageBox.success("业务受理成功!", "订单号: "+obj[0].ORDER_ID,function(btn){
							if("ok" == btn){
								window.location.reload();
							}
						});
						$.endPageLoading();
					},function(error_code,error_info,derror){
						$.endPageLoading();
						showDetailErrorInfo(error_code,error_info,derror);
					});
				}
			});
		}
	});
		}
	});
	
}

//弹出新增账户界面
function beforeCreateAcct(){
	var custId = $("#group_CUST_ID").val();
	if(custId==""||custId==undefined){
		MessageBox.alert("提示信息", "未获取到集团客户资料！");
		return false;
	}
	$.ajax.submit(this,'initAdd','CUST_ID='+custId, 'AddAcctPart',function(data){
		$.endPageLoading();
		var custName = data.get("CUST_NAME");
		var payModCode = $("#add_PAY_MODE_CODE").val();
		$("#add_PAY_NAME").val(custName);
		if(payModCode=="1" || payModCode=="2" || payModCode=="3"){
			
			$("#li_SUPER_BANK_ADD").addClass("required");
			$("#li_BANK_NAME_ADD").addClass("required");
			$("#li_BANK_ACCT_NO_ADD").addClass("required");
			
		}
		showPopup('myPopup','acct-popup-add_item');

	},function(error_code,error_info,derror){
		
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

//新增账户
function createAcct(obj){
    var consignAmount = $("#add_CONSIGN_AMOUNT").val();
	if(!checkMoney(consignAmount)){
		MessageBox.alert("提示信息","请输入正确的金额格式！");
		$("#cond_CONSIGN_AMOUNT").val('');
		return false;
	}
		
	 if($("#add_BANK_CODE").val() == '' && $("#add_PAY_MODE_CODE").val() != '0')
	 {
		MessageBox.alert("提示信息","银行名称不能为空");
   		return false;
	 }
	 
	 var custId = $("#group_CUST_ID").val();
	 var grpSn = $("#group_SERIAL_NUMBER").val();
	 var productId = $("#GROUP_PRODUCT_ID").val();
	 
	 
	 var param  = "state="+obj;
		 param += "&productId="+productId;
		 param += "&CUST_ID_HIDE="+custId;
		 param += "&GrpSn="+grpSn;
		$.beginPageLoading();
		$.ajax.submit('AddAcctPart','onSubmitBaseTradeForAdd', param,'AddAcctPart',
				function(data)
			    { 
			        $.endPageLoading(); 
			        var tradeData;
			    	if(data instanceof $.DatasetList){
			    		tradeData = data.get(0);
			    	}else if(data instanceof $.DataMap){
			    		tradeData = data;
			    	}
			        var x_resultcode = tradeData.get("x_resultcode","0");
			        if(x_resultcode == '-1'){
			      	  ifcheck= false;
			      	MessageBox.alert(data.get("x_resultinfo"));
			      		return;
			        }
			        if(tradeData.get("HAS_PAYMODE_CHGPRIV", false)){
			        	MessageBox.alert("提示信息","您已特殊办理银行托收业务，为避免停机给您造成不便，请您务必及时缴清往月欠费。");
			        }
			        
					var obj = JSON.parse(data);
					MessageBox.success("业务受理成功!", "订单号: "+obj[0].ORDER_ID,function(btn){
						if("ok" == btn){
							backPopup("myPopup","acct-popup-add_item");
						}
					});
					$.endPageLoading();
				},
				function(error_code,error_info,derror)
				{
					$.endPageLoading(); 
					showDetailErrorInfo(error_code,error_info,derror);	
			    }
			 );  
}
/**
用途：检查输入字符串是否符合金额格式 
格式定义为整数
*/
function checkMoney(obj){
	var regu = "^[0-9]*(\.[0-9]{1,2})?$"; 
	var reg = new RegExp(regu); 
	
	if (reg.test(obj)) { 
		return true; 
	} else { 
		return false; 
	} 
}

//刷新账户信息
function refreshAcctInfo(){
	
	$.beginPageLoading();

	var cust_id = $("#group_CUST_ID").val();
	$.beginPageLoading();

	$.ajax.submit(this,'getAcctByCustId','&CUST_ID='+cust_id,'acctListPart',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}


