
/**
 * 
 * @returns {Boolean}
 */
function terminalMaintainInfoQry(){
	
	if (!$.validate.verifyAll()){
		return false;
	}
	
	//用户手机号码
	var cond_MobilePhone=$('#cond_MobilePhone').val();
	//手机串号
	var cond_IMEI=$('#cond_IMEI').val();
	//受理网点
	var cond_strSiteCode=$('#cond_strSiteCode').val();
	//受理开户时间
	var cond_strApplyBeginDate=$('#cond_strApplyBeginDate').val();
	//受理结束时间
	var cond_strApplyEndDate=$('#cond_strApplyEndDate').val();
	if(cond_MobilePhone == '' 
		&&cond_IMEI=='' && cond_strSiteCode ==''
		&&cond_strApplyBeginDate ==''&&cond_strApplyBeginDate==''&&cond_strApplyEndDate == ''){
		MessageBox.alert("提示","查询条件至少选择一项");
		
		return false;
	}
	
	
	
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryCondPart', 'terminalMaintainInfoQry', null, 'QueryDataPart', function(data){
		$.endPageLoading();
		if(data.get("DATA_COUNT")=="0"){
			alert("查询无数据！");
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}