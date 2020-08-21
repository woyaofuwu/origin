//证件号码查询 
function queryCustNumber(){
	//查询条件校验
	if(!$.validate.verifyAll("psptPart")) {
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('psptPart', 'queryCustNumber', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function queryCustNumberMod(){
	//查询条件校验
	if(!$.validate.verifyAll("psptPart")) {
		return false;
	}
	beginPageLoading("正进行金库认证..");
	$.treasury.auth("CUST_4A_querySnByUsrpidMod",function(ret)
	{
		endPageLoading();
		if(true === ret)
		{  
			var param ="&X_DATA_NOT_FUZZY=true";
			//$.beginPageLoading("正在查询数据...");
			$.ajax.submit('psptPart', 'queryCustNumber', param, 'QueryListPart', function(data){
				//$.endPageLoading();
			},
			function(error_code,error_info){
				//$.endPageLoading();
				alert(error_info);
		    });
		}
		else
		{
			alert("认证失败");
			return false;
		}
	});
}

/**
 * 扫描读取身份证信息
 */
function clickScanPspt(){
    getMsgByEForm("PSPT_ID",null,null,null,null,null,null,null);
}