function querySaleActive() {
	var serialNumber = $("#cond_GROUP_SERIAL_NUMBER").val();
	if(serialNumber == null || serialNumber == "") 
	{
		return;
	}
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("QueryCondPart", "getSaleActiveInfo", "", "RefreshPart,ServerPart,DiscntPart", function(data){
		$.endPageLoading();
	},
		function(error_code, error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		}
	);
}

/**
 * Ajax查询活动的服务资费信息
 */
function querySaleParams(obj) 
{
	if(obj.checked) 
	{
		var endDateName = obj.getAttribute("end_date_name");
		$("#" + endDateName).attr("disabled", false);
		
		var user_id = obj.getAttribute("user_id");
		$("#USER_ID").val(user_id);
		
		var params = "&CAMPN_ID="+obj.getAttribute("campn_id")+"&USER_ID="+user_id+"&RELATION_TRADE_ID="+obj.getAttribute("relation_trade_id");
		
		$.beginPageLoading("数据查询中......");
		$.ajax.submit("", "querySaleParams", params, "ServerPart,DiscntPart", function(data){
			$.endPageLoading();
		},
			function(error_code, error_info, derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			}
		);
	}
	
}

/**
 * 活动终止页面激活优惠结束时间
 */
function activeDiscntEndDate(obj) 
{
	var endDateName = obj.getAttribute("end_date_name");
	if(obj.checked) 
	{
		$("#" + endDateName).attr("disabled", false);
	}
	else 
	{
		$("#" + endDateName).attr("disabled", true);
	}
}

/**
 * 活动终止页面提交按钮
 */
function submitSaleParams() 
{
	var discnts = document.getElementsByName("discnts");
	var end_time = ""; //优惠最晚结束时间(取所有优惠结束的最晚时间)
	for(var i = 0, size = discnts.length; i < size; i++) 
	{
		var discnt = discnts[i];
//		if(discnt.checked) 
//		{
			if(end_time != "") 
			{
				var el_end_time = $("#" + discnt.getAttribute("end_date_name")).val();
				if(dateCompare(el_end_time, end_time) > 0) 
				{
					end_time = el_end_time;
				}
			}
			else
			{
				end_time = $("#" + discnt.getAttribute("end_date_name")).val();
			}
//		}
	}
	
	var actives = document.getElementsByName("actives");
	if(end_time != "") 
	{
		for(var j = 0, size = actives.length; j < size; j++) 
		{
			var active = actives[j];
			if(active.checked) 
			{
				var active_end_time = $("#" + active.getAttribute("end_date_name")).val();
				//同步元素最晚结束时间到活动结束时间
				if(dateCompare(active_end_time, end_time) > 0)
				{
					$("#" + active.getAttribute("end_date_name")).val(end_time);
				}
				break;
			}
		}
	}
	
	$("#SALEACTIVE_STR").val(decodeSaleActives());
	$("#DISCNT_STR").val(decodeSaleDiscnts());
	if($("#SALEACTIVE_STR").val() == "") 
	{
		$.showWarnMessage("数据提交失败", "请选择需要终止的营销活动！", null);
		return false;
	}
	return true;
}

/**
 * 拼活动串
 * PARTITION_ID, USER_ID, PRODUCT_ID, PACKAGE_ID, CAMPN_ID, START_DATE, RELATION_TRADE_ID
 */
function decodeSaleActives() 
{
	var actives = document.getElementsByName("actives");
	var dataset = $.DatasetList();
	var activeStr = "";
	for(var j = 0, size = actives.length; j < size; j++) 
	{
		var active = actives[j];
		if(active.checked) 
		{
			activeStr = "@" + "USER_ID=" + active.getAttribute("user_id") 
				+ ",PARTITION_ID=" + active.getAttribute("partition_id") 
				+ ",CAMPN_ID=" + active.getAttribute("campn_id") 
				+ ",PRODUCT_ID=" + active.getAttribute("product_id") 
				+ ",PACKAGE_ID=" + active.getAttribute("package_id") 
				+ ",RELATION_TRADE_ID=" + active.getAttribute("relation_trade_id") 
				+ ",START_DATE=" + active.getAttribute("start_date") 
				+ ",END_DATE=" + $("#" + active.getAttribute("end_date_name")).val();
				
		}
	}
	return activeStr != "" ? activeStr.substring(1) : "";
}

/**
 * 拼优惠串
 * PARTITION_ID, USER_ID, USER_ID_A, PRODUCT_ID, PACKAGE_ID, CAMPN_ID, START_DATE, RELATION_TRADE_ID
 */
function decodeSaleDiscnts() 
{
	var discnts = document.getElementsByName("discnts");
	var dataset = $.DatasetList();
	var discntStr = "";
	for(var i = 0, size = discnts.length; i < size; i++) 
	{
		var discnt = discnts[i];
		if(discnt.checked) 
		{
			discntStr = "@" + "USER_ID=" + discnt.getAttribute("user_id") 
				+ ",USER_ID_A=" + discnt.getAttribute("user_id_a") 
				+ ",PARTITION_ID=" + discnt.getAttribute("partition_id") 
				+ ",PRODUCT_ID=" + discnt.getAttribute("product_id") 
				+ ",PACKAGE_ID=" + discnt.getAttribute("package_id") 
				+ ",DISCNT_CODE=" + discnt.getAttribute("discnt_code") 
				+ ",START_DATE=" + discnt.getAttribute("start_date") 
				+ ",END_DATE=" + $("#" + discnt.getAttribute("end_date_name")).val(); 

		}
	}
	return discntStr != "" ? discntStr.substring(1) : "";
}

/**
 * 比较时间大小
 * @param date_str1
 * @param date_str2
 * @returns 0：表示date_str1=date_str2
 *          1：表示date_str1>date_str2
 *         -1：表示date_str1<date_str2
 */
function dateCompare(date_str1, date_str2) 
{
	if(date_str1 == "") 
	{
		date_str1 = "1970-01-01 00:00:00";
	}
	if(date_str2 == "") 
	{
		date_str2 = "1970-01-01 00:00:00";
	}
	
	if(date_str1 == date_str2) return 0;
	
	var date_arr1 = date_str1.split(" ");
	if(date_arr1.length != 2) 
	{
		date_arr1[1] = "00:00:00";
	}
	var date1 = date_arr1[0].split("-");
	var time1 = date_arr1[1].split(":");
	var dateTime1 = new Date(date1[0], date1[1], date1[2], time1[0], time1[1], time1[2]);
	
	var date_arr2 = date_str2.split(" ");
	if(date_arr2.length != 2) 
	{
		date_arr2[1] = "00:00:00";
	}
	var date2 = date_arr2[0].split("-");
	var time2 = date_arr2[1].split(":");
	var dateTime2 = new Date(date2[0], date2[1], date2[2], time2[0], time2[1], time2[2]);
	
	if(dateTime1.getTime() > dateTime2.getTime()) 
	{
		return 1;
	}
	else
	{
		return -1;
	}
		
}

