//通过集团号码查询集团资料成功后调用的方法
function selectGroupBySnAfterAction(data)
{
    var groupInfo =  data.get("GRP_CUST_INFO");
  
	//初始集团资料信息
	if(groupInfo != undefined && groupInfo != null){
		insertGroupCustInfo(groupInfo);
	}
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
	queryAcctInfos(cust_id,acctId,userInfo.get("SERIAL_NUMBER"));
}

 //SN集团资料查询失败后调用的方法
function selectGroupBySnErrorAfterAction() {
	//清空填充的集团客户信息内容
    clearGroupCustInfo();
}

function queryAcctInfos(cust_id,acctId,serialNumber)
{
	$.ajax.submit(this,'queryAcctInfos','&CUST_ID='+cust_id + '&ACCT_ID=' + acctId + '&SERIAL_NUMBER=' + serialNumber ,'acctInfoPart',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}


function onSubmitBaseTradeCheck()
{
	var curAcctId = $('#SPAN_GROUPCUSTINFO_ACCT_ID').html();
	var selAcctId = $('#acct_ACCT_ID').val();
	if(selAcctId == '')
	{
		alert('请选择帐户!');
		return false;
	}
	
	if(curAcctId == selAcctId)
	{
		alert('您选择的帐户与您当前帐户相同，请重新选择!');
		return false ;
	}
	
	var groupSn = $('#cond_GROUP_SERIAL_NUMBER').val();
	
	/*
	MessageBox.confirm("确认信息!","您确定要将集团产品【" + groupSn + "】拆分到账户【" + selAcctId + "】上吗？",function(btn){
						if(btn == "ok") {
							var param = "&SERVER_NUMBER=" + groupSn ;
							param += "&ACCT_ID=" + selAcctId;
							param += "&OLD_ACCT_ID=" + curAcctId;
							param += "&CUST_ID=" + $('#SPAN_GROUPCUSTINFO_CUST_ID').val() ;
							param += "&GROUP_ID=" + $('#SPAN_GROUPCUSTINFO_GROUP_ID').val() ;
							
							$.ajax.submit(this,'onSubmitBaseTrade',param,'GroupInfoPart,tabPart',function(data){
								$.endPageLoading();
							},function(error_code,error_info,derror){
								$.endPageLoading();
								showDetailErrorInfo(error_code,error_info,derror);
							});
						}
						else
						{
							return false;
						}
					});
	*/
	alert('提示：账户拆分为下月生效!');
	var msg = "您确定要将集团产品【" + groupSn + "】拆分到账户【" + selAcctId + "】上吗？";
	if(window.confirm(msg))
	{
		return true;
	}
	return false;
}
