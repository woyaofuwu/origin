

/**
 * 
 * @returns {Boolean}
 */
function queryTestCardUserInfo(){
	
	if (!$.validate.verifyAll()){
		return false;
	}
	
	//清空测试号码
	$("#SERIAL_NUMBER").val("");
	//清空测试卡类型
	$("#RSRV_VALUE").val("");
	
	$("#CSSUBMIT_BUTTON").attr("disabled", true).addClass("e_dis");
	
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryCondPart', 'queryTestCardUserinfo', null, 'QueryDataPart', function(data){
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

//点击表格行，初始化编辑区
function tableRowClick(){
	var rowData = $.table.get("QueryListTable").getRowData();
	for(var i = 0;i< rowData.keys.length;i++){
		var colId = rowData.keys[i];
		var jqObj = $('#'+colId);
		if(jqObj!=null){
			jqObj.val(rowData.map[colId]);
		}
	}
	$("#CSSUBMIT_BUTTON").attr("disabled", false).removeClass("e_dis");
}

/**
 * 提交业务
 * @returns {Boolean}
 */
function onTradeSubmit(){
	//获取测试号码
    var SERIAL_NUMBER=$("#SERIAL_NUMBER").val();
    if(SERIAL_NUMBER == ''){
    	alert("测试号码:不能为空");
    	return false;
    }
   var RSRV_VALUE=$("#RSRV_VALUE").val();
   if(RSRV_VALUE == ''){
	   	alert("测试卡类型:不能为空");
		return false;
   }
   var INST_ID=$("#INST_ID").val();
   var PARTITION_ID=$("#PARTITION_ID").val();
   
	var param = '&SERIAL_NUMBER='+SERIAL_NUMBER;//手机号码
	param += '&RSRV_VALUE='+RSRV_VALUE;//测试卡类型
	param += '&INST_ID='+INST_ID;
	param += '&PARTITION_ID='+PARTITION_ID;
	$.cssubmit.addParam(param);
	return true;
}


/**
 * 初始化页面参数
 * */
function resetPage(jwcidMark){
	$.beginPageLoading("努力刷新中...");
	$.ajax.submit('','','',jwcidMark,function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}