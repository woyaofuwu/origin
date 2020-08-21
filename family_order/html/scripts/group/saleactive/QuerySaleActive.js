//查询所有用户
function queryAllUser()
{
	clearUserInfoPart();
	
	var serialNumber = $("#cond_GROUP_SERIAL_NUMBER").val();
	var staff_id = $("#cond_STAFF_ID").val();
	var depart_id = $("#cond_DEPART_ID").val();
	var capmn_type = $("#cond_CAMPN_TYPE").val();
	var start_date = $("#cond_START_DATE").val();
	var end_date = $("#cond_END_DATE").val();
	
//	if(serialNumber.length == 8)
//	{
//		serialNumber = "898" + serialNumber;
//	}
	var condstring = '&SERIAL_NUMBER='+serialNumber+'&STAFF_ID='+staff_id+'&DEPART_ID='+depart_id+'&CAMPN_TYPE='+capmn_type+'&START_DATE='+start_date+'&END_DATE='+end_date;
	
	if((serialNumber==null||serialNumber=='')
			&&(staff_id==null||staff_id=='')
				&&(depart_id==null||depart_id=='')
					&&(capmn_type==null||capmn_type=='')
						&&(start_date==null||start_date=='')
							&&(end_date==null||end_date==''))
	{
		MessageBox.confirm("集团营销查询验证", "确认不输入任何查询条件？将查询出所有集团营销活动办理记录",
				function(btn){
					if(btn == "ok") {
						doQueryUser();
					}
				} , "", false);
	}
	else 
	{
		doQueryUser();
	}
}

function doQueryUser() 
{
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("QueryCondPart", "getSaleActiveInfo", "", "UserInfoPart,RefreshPart,mytab", function(data){
		$.endPageLoading();
	},
		function(error_code, error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		}
	);
}

//查询用户信息、营销包优惠信息、营销包实物信息、话费返还信息
function showOtherInfo()
{
	var tableObj = $.table.get("saleactiveTable").getRowData();
	var packageId = tableObj.get("PACKAGE_ID");
	var userId = tableObj.get("USER_ID");
	var relationTradeId = tableObj.get("RELATION_TRADE_ID");

	var condstring = "&USER_ID="+userId+ "&PACKAGE_ID="+packageId+"&RELATION_TRADE_ID="+relationTradeId;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("", "queryUserOtherInfo", condstring, "UserInfoPart,mytab", function(data){
		$.endPageLoading();
	},
		function(error_code, error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		}
	);
}

//清除用户信息区域数据
function clearUserInfoPart() 
{
	$("#userinfo_SERIAL_NUMBER").val("");
	$("#userinfo_CUST_NAME").val("");
	$("#userinfo_CUST_TYPE").val("");
	$("#userinfo_PSPT_TYPE").val("");
	$("#userinfo_REMOVE_TAG").val("");
	$("#userinfo_OPEN_DATE").val("");	
}

