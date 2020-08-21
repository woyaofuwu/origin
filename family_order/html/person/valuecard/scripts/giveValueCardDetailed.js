
/**
 * 
 * @returns {Boolean}
 */
function queryValueCardInfo(){
	
	if (!$.validate.verifyAll()){
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryCondPart', 'queryValueCardInfo', null, 'QueryDataPart', function(data){
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

function textToUpperCase(obj){
	var v = obj.value;
	obj.value=v.toUpperCase();
}

/**
 * 每输入一个开始号码，自动填充终止号码
 */
function autoCopyNum(startId, endId){
	var s = $('#'+startId);
	var e = $('#'+endId);
	e.val(s.val());
}
function onblurCopyNum(startId, endId){
	var s = $('#'+startId);
	var e = $('#'+endId);
	if(s.val()!="" && e.val()==""){
		e.val(s.val());
	}
	if(e.val()!="" && s.val()==""){
		s.val(e.val());
	}
}

function checkStartDate(){
	var a = $("#cond_START_DATE").val();
	var b = $("#cond_END_DATE").val();
	if(b=="")return;
	var c = a.slice(5,7);
	var d = b.slice(5,7);
	var time = b.substring(0,8) + '01';
	if(a > b){
		alert('请输入正确的销售起始时间！');
		$("#cond_START_DATE").val(time);
	}
	if(c != d){
		alert('此查询只支持一个月内查询！');
		$("#cond_START_DATE").val(time);
	}
}

function getEndDate(){
	var time = $("#cond_END_DATE").val();
	var time_start = time.substring(0,8) + '01';
	$("#cond_START_DATE").val(time_start);	
}

/**
 * 提交业务
 * @returns {Boolean}
 */
function onNotPhoneSubmit(){
	var  tableData=$.table.get("QueryListTable").getTableData();
	if(tableData == null||tableData == ''){
		alert("没有数据要提交");
		return false;
	}
	
	var data = $.table.get("QueryListTable").getCheckedRowDatas();//获取选择中的数据
	if(data == null || data.length == 0) {
		alert('至少选择一项');
		return false;
	}
	//获取客户号码
    var custPhone=$("#custPhone").val();
    if(custPhone == ''){
    	alert("客户号码:不能为空");
    	return false;
    }
    
	var param = "&NOTPHONE_TABLE=" + data + "&custPhone=" + custPhone;
	$.beginPageLoading("业务处理中..");
     $.ajax.submit('QueryDataPart', 'submitValueCardInfo', param, null, function(data){
		$.endPageLoading();
		var count=data.get(0).get("count");
	   if(count > 0){
		   $.showSucMessage("补录"+count+"条成功");
		   resetPage('QueryDataPart');
	   }
	},
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}
/**
 * 导入
 * @returns {Boolean}
 */
function importCardData(){

	if($("#cond_STICK_LIST").val()==""){
		alert('上传文件不能为空！');
		return false;
	}
	$.beginPageLoading("努力导入中...");
	
	$.ajax.submit('SubmitImportValueCardinfo','importValueCardNotPhoneinfo','','',function(data){
		$.endPageLoading();
		var count=data.get(0).get("count");
		   if(count > 0){
			   $.showSucMessage("导入"+count+"条成功");
			   resetPage('SubmitImportValueCardinfo');
		   }
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
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
