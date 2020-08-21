function changebook()
{
	var bookTradeCode = $("#BOOK_TYPE_CODE").val();
	var param = "&BOOK_TYPE_CODE=" + bookTradeCode;
	ajaxSubmit('', 'getBookInfo', param, 'svcItemPart,inModePart', function(data){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			MessageBox.error("错误",error_info);
		}
		 );
}

function queryCustInfo()
{
	var serialNumber = $("#SERIAL_NUMBER").val();
	$.beginPageLoading("信息查询中..");
	ajaxSubmit('', 'getCustInfo', '&SERIAL_NUMBER=' + serialNumber, 'QueryCondPart,custInfo,hiddenPart', function(data) {
			$("#CSSUBMIT_BUTTON").attr("disabled",false).removeClass("e_dis");
			$.endPageLoading();
			
		},
		function(error_code,error_info){
			$.endPageLoading();
			var alertInfo = data.get(0).get("alertInfo");
			if(alertInfo != '')
			{
				MessageBox.error("错误",alertInfo);
			}
			MessageBox.error("错误",error_info);
		}
		);
}


/*function onTradeSubmit()
{	
	if(!verifyAll('QueryCondPart'))
   	{
	   return false;
	}
	
	var productId = $("#PRODUCT_ID").val();
	var serviceId = $("#SERVICE_ID").val();
	var inModeCode = $("#IN_MODE_CODE").val();
	var departCode = $("#DEPART_CODE").val();
	
	
	return true;
}*/

/** submit depts */
function submitDepts(obj) {
	
	/* 将指定列拼成串，后台将串解析生成数据  确定提交吗？ */	
//	alert($.table.get("paramTable").getTableData().toString());
	/** 提交操作，用AJAX局部刷新提交 */
	$.beginPageLoading("正在提交数据...");
	$.ajax.submit('QueryCondPart,bookInfoPart,hiddenPart', 'createApplyServ', '', '', function(data){
		$.endPageLoading();
		var title = "业务受理成功";
		if(data){
			var result = data.get(0).get("IS_SUCCESS");
			if(result != 'TRUE'){
				title = "业务受理失败";
				MessageBox.error(title,title,null,null,null);
			}else{
				MessageBox.success(title,title,null,null,null);
			}
		}
		
		//reset();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}