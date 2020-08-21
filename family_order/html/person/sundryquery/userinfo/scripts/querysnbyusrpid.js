//用户资料模糊查询
function querySnByUsrpid(){
	//查询条件校验
	if(!$.validate.verifyAll("QuerySnByUsrpidPart")) {
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	//用户资料模糊查询
	$.ajax.submit('QuerySnByUsrpidPart', 'querySnByUsrpid', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function querySnByUsrpidMod()
{
	//查询条件校验
	if(!$.validate.verifyAll("QuerySnByUsrpidPart")) 
	{
		return false;
	}
	beginPageLoading("正进行金库认证..");
	$.treasury.auth("CUST_4A_querySnByUsrpidMod",function(ret)
	{
		endPageLoading();
		if(true === ret)
		{  
			var param ="&cond_X_DATA_NOT_FUZZY=true";
			//$.beginPageLoading("正在查询数据...");
			//用户资料模糊查询
			$.ajax.submit('QuerySnByUsrpidPart', 'querySnByUsrpid', param, 'QueryListPart', function(data)
			{
				//$.endPageLoading();
			},
			function(error_code,error_info){
				//$.endPageLoading();
				alert(error_info);
				return false;
		    });
		}
		else
		{
			alert("认证失败");
			return false;
		}
	}); 
}

function userquerymodechg11()
{	
	var querymode = $("#cond_QUERY_MODE").val();
	/**query by cust_name*/
	
	if(querymode=='1' || querymode=='2')
	{
		$("#cond_PSPT_TYPE_CODE").val("");
		$("#cond_PSPT_ID").val("");
		$("#cond_SERIAL_NUMBER").val("");
		$("#cond_PSPT_TYPE_CODE").attr("nullable","yes");
		$("#pspttypecode").css("display", "none");
		$("#psptid").css("display", "none");
		$("#sn").css("display", "none");
		$("#custname").css("display", "");
		$("#cond_CUST_NAME").attr("nullable","no");
		$("#cond_PSPT_ID").attr("nullable","yes");
		$("#cond_SERIAL_NUMBER").attr("nullable","yes");
	}
	/**query by pspt_id*/
	else if(querymode=='3' || querymode=='4')
	{
		$("#cond_CUST_NAME").val("");
		$("#cond_SERIAL_NUMBER").val("");
		$("#custname").css("display", "none");
		$("#sn").css("display", "none");
		$("#pspttypecode").css("display", "");
		$("#psptid").css("display", "");
		$("#cond_CUST_NAME").attr("nullable","yes");
		$("#cond_PSPT_TYPE_CODE").attr("nullable","no");
		$("#cond_PSPT_ID").attr("nullable","no");
		$("#cond_SERIAL_NUMBER").attr("nullable","yes");
	}
	
}


function userquerymodechg()
{
	var querymode = $("#cond_QUERY_MODE").val();
	if(querymode=='1')
	{
		$("#pspttypecode").css("display", "none");
		$("#psptid").css("display", "none");
		$("#sn").css("display", "");
		$("#custname").css("display", "none");
		$("#cond_CUST_NAME").attr("nullable","yes");
		$("#cond_PSPT_ID").attr("nullable","yes");
		$("#cond_SERIAL_NUMBER").val("");
		$("#cond_SERIAL_NUMBER").attr("nullable","no");
		$("#cond_SERIAL_NUMBER").css("display", "");
		
	}
	/**query by cust_name*/
	else if(querymode=='2' || querymode=='3')
	{
		
		$("#pspttypecode").css("display", "none");
		$("#psptid").css("display", "none");
		$("#sn").css("display", "none");
		$("#custname").css("display", "");
		$("#cond_CUST_NAME").attr("nullable","no");
		$("#cond_PSPT_ID").attr("nullable","yes");
		$("#cond_SERIAL_NUMBER").val("");
		$("#cond_SERIAL_NUMBER").attr("nullable","yes");
		$("#cond_SERIAL_NUMBER").css("display", "none");
	}
	/**query by pspt_id*/
	else if(querymode=='4' || querymode=='5')
	{
		
		$("#pspttypecode").css("display", "");
		$("#psptid").css("display", "");
		$("#sn").css("display", "none");
		$("#custname").css("display", "none");
		$("#cond_CUST_NAME").attr("nullable","yes");
		$("#cond_PSPT_ID").attr("nullable","no");
		$("#cond_SERIAL_NUMBER").val("");
		$("#cond_SERIAL_NUMBER").attr("nullable","yes");
		$("#cond_SERIAL_NUMBER").css("display", "none");
	}
		/*
	REQ201512300001 客户资料关联查询优化--业务挑刺问题
	*/
	else if(querymode=='7' ){
		$("#cond_PSPT_TYPE_CODE").val("");
		$("#cond_CUST_NAME").val("");
		$("#cond_PSPT_ID").val("");
		$("#cond_SERIAL_NUMBER").val("");
		
		$("#custname").css("display", "");
		$("#psptid").css("display", "");
		$("#pspttypecode").css("display", "none"); 
		$("#sn").css("display", "none");
		
		$("#cond_CUST_NAME").attr("nullable","no");
		$("#cond_PSPT_ID").attr("nullable","no");
		$("#cond_PSPT_TYPE_CODE").attr("nullable","yes");
		$("#cond_SERIAL_NUMBER").attr("nullable","yes");
	}
}


function exportBeforeAction(domid) {
	
	var QUERY_MODE = $("#cond_QUERY_MODE").val();
	var SERIAL_NUMBER = $("#cond_SERIAL_NUMBER").val();
	var CUST_NAME = $("#cond_CUST_NAME").val();
	var PSPT_TYPE_CODE = $("#cond_PSPT_TYPE_CODE").val();
	var PSPT_ID = $("#cond_PSPT_ID").val();
	var REMOVE_TAG = $("#cond_REMOVE_TAG").val();
	
	var params = ""; 
	params += "&QUERY_MODE="+QUERY_MODE+"&SERIAL_NUMBER="+SERIAL_NUMBER
					+"&CUST_NAME="+CUST_NAME+"&PSPT_TYPE_CODE="+PSPT_TYPE_CODE
					+"&PSPT_ID="+PSPT_ID;
	
	$.Export.get(domid).setParams(params);
	return true;
}



//oper: 取消：cancel；终止：terminate；状态修改中的 确定：loading；导出完成后的确定：ok；导出失败时的确定：fail；
function exportAction(oper, domid) {
	if (oper == "cancel") {
		alert("点击[取消]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
	} else if (oper == "terminate") {
		alert("点击[终止]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
	} else if (oper == "loading") {
		alert("点击[加载]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
	} else if (oper == "ok") {
		alert("成功时点击[确定]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
	} else if (oper == "fail") {
		alert("失败时点击[确定]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
	} 
	return true;
}

//tab
function myTabSwitchAction(ptitle, title) {
	if(title=='宽带业务')
	{
		 $.ajax.submit('QueryCondPart', 'qryTHCustomerContactInfo', '', 'refreshArea2', function(data){
				redirect(trade_type);
				$.endPageLoading();
			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
		  });
	}
	return true;
}

//下拉框
function broadquerymodechg()
{
	var broadquerymode = $("#broad_QUERY_MODE").val();
	if(broadquerymode=='1')
	{
		$("#broadpspttypecode").css("display", "");
		$("#broadpsptid").css("display", "");
		$("#broadcustname").css("display", "none");
		$("#broad_CUST_NAME").attr("nullable","yes");
		$("#broad_PSPT_ID").attr("nullable","no");
	}else if(broadquerymode=='2' ){
		$("#broad_PSPT_TYPE_CODE").val("");
		$("#broad_CUST_NAME").val("");
		$("#broad_PSPT_ID").val("");
		
		$("#broadcustname").css("display", "");
		$("#broadpsptid").css("display", "");
		$("#broadpspttypecode").css("display", "none"); 
		
		$("#broad_CUST_NAME").attr("nullable","no");
		$("#broad_PSPT_ID").attr("nullable","no");
		$("#broad_PSPT_TYPE_CODE").attr("nullable","yes");
	}
}


//宽带业务查询
function broadQuerySnByUsrpid(){
	//查询条件校验
	if(!$.validate.verifyAll("BroadQuerySnByUsrpidPart")) {
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	//用户资料模糊查询
	$.ajax.submit('BroadQuerySnByUsrpidPart', 'broadQuerySnByUsrpid', null, 'BroadQueryListPart', function(data){
		var counts = data.get("counts","");
		if('0' != counts){
			$("#broadcon").css("display", "none");
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

