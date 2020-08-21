var clickedPrmRuleRowObj = null;


/** 查询规则列表 */
function qryPrmRuleList(){
	//查询条件校验
	if(!$.validate.verifyAll("QryPrmRuleCondPart")) {
		return false;
	}
	
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QryPrmRuleCondPart,QryPrmRuleNav', 'qryPrmRuleList', null, 'QryPrmRuleListPart,QryPrmRecListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	});	
	
	//第二部分和第三部分隐藏
	$("#ParaRecordListDivPart").css("display","none");
	$("#ParaSetDivPart").css("display","none");
}

/** 触发点击规则 */
function clickPrmRuleRow(obj){	
	//首先默认第二部分、第三部分隐藏(因为第三部分操作后需要触发此事件、或者触发此事件失败，都必须隐藏的区域)
	$("#ParaRecordListDivPart").css("display","none");
	$("#ParaSetDivPart").css("display","none");
	
	clickedPrmRuleRowObj = obj;
	var cells =obj.cells;
	$("#cond_RULE_ID").val(cells[0].innerHTML);
	$("#cond_RULE_NAME").val(cells[1].innerHTML);	
	$("#cond_TABLE_NAME2").val(cells[2].innerHTML);
	$("#cond_RIGHT_CODE").val(cells[3].innerHTML);
	
	var param="&cond_RULE_ID="+cells[0].innerHTML+"&cond_TABLE_NAME2="+cells[2].innerHTML+"&cond_RIGHT_CODE="+cells[3].innerHTML;
	
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QryPrmRecCondPart,QryPrmRecListNav', 'qryPrmRecList', null, 'QryPrmRecListPart,ParaSetPart', function(data){
		$.endPageLoading();
		
		//第二部分显示、第三部分隐藏
		$("#ParaRecordListDivPart").css("display","");
		$("#ParaSetDivPart").css("display","none");
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	});
}

/** 触发点击更新或者复制参数列 */
function clickPrmRowUpdate(obj, tag){
	var obj=obj.parentNode.parentNode;
	
	//显示第三部分
	$("#ParaSetDivPart").css("display","");
	
	$(obj).find("input").each(function(){
		var columnName = $(this).attr("columnName");
		var value = $(this).attr("value");
		$("#col_"+columnName).val(value);
		$("#old_OLD_"+columnName).val(value); //备份更新前的数据
	}); 
	
	if (tag == '1') { //如果复制
		$("#col_RECORDROWID").val("");
	}
}

/** 触发点击新增参数记录 */
function clickPrmRowInsert(obj){
	if (clickedPrmRuleRowObj == null) {
		alert("你没有选择配置规则行，请先选择!");
	} else {
		//显示第三部分
		$("#ParaSetDivPart").css("display","");
	}
}

/** 全选 */
function clickChcbox(checked){
	$("#paraRecordTable input:checkbox").attr("checked", checked);
}

/** 触发点击 删除 */
function clickPrmRowDelete(obj){
	if ($("input[name='chcbox']:checked").size() <= 0) {
  		alert("你没有选择删除对象，请先选择!");
  		return;
 	}
	
	if (!confirm("删除操作将数据彻底删除，您确定要删除吗?")) {
		return;
	}
 	
 	var TABLE_NAME = $("#cond_TABLE_NAME2").val();
 	var delRowIds ="";

	$("input[name='chcbox']:checked").each(function(){
		var val = $(this).val();
		delRowIds = delRowIds + "," + val;
	});

	if(delRowIds.length>1){
		delRowIds = delRowIds.substring(1);
	}
	var param="&TABLE_NAME="+TABLE_NAME+"&DEL_ROWIDS="+delRowIds;
	$.ajax.submit('', 'deleteParaMaintain', param, 'QryPrmRecListPart', function(data){
		MessageBox.alert("提示","参数删除成功！");
		clickPrmRuleRow(clickedPrmRuleRowObj);
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	});
}

/** 显示固定列 */
function clickFixedHide(){
	var obj=  $("#paraRecordTable");
 	$(obj).find("th[columnIsFixed='1']").each(function(){
			$(this).css("display","none");
	}); 
	$(obj).find("td[columnIsFixed='1']").each(function(){
			$(this).css("display","none");
	});
	
	var obj=  $("#paraSetTable");
 	$(obj).find("th[columnIsFixed='1']").each(function(){
			$(this).css("display","none");

	}); 
	$(obj).find("td[columnIsFixed='1']").each(function(){
			$(this).css("display","none");
	});
}

/** 隐藏固定列 */
function clickFixedShow(){
	var obj=  $("#paraRecordTable");
 	$(obj).find("th[columnIsFixed='1']").each(function(){
			$(this).css("display","");
	}); 
	$(obj).find("td[columnIsFixed='1']").each(function(){
			$(this).css("display","");
	});
	
	var obj=  $("#paraSetTable");
 	$(obj).find("th[columnIsFixed='1']").each(function(){
			$(this).css("display","");

	}); 
	$(obj).find("td[columnIsFixed='1']").each(function(){
			$(this).css("display","");
	});
}

/** 提交新增或者更新 */
function saveOrUpdateParaMaintain() {
	var TABLE_NAME = $("#TABLE_NAME").val();
	var col_RECORDROWID = $("#col_RECORDROWID").val();
	var col_tr=  $("#col_tr");
	
	$.ajax.submit('ParaSetPart', 'saveOrUpdateParaMaintain', null, 'QryPrmRecListPart', function(data){
		MessageBox.alert("提示","参数配置成功！");
		clickPrmRuleRow(clickedPrmRuleRowObj);
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	});	
}

/** 模板导出 *//*
function exportTemplate() {
	//var TABLE_NAME = $("#cond_TABLE_NAME2").val();
	//var RULE_NAME = $("#cond_RULE_ID").val();
	//var RULE_COLUMNS = $("#RULE_COLUMNS").val();
	//var ss=new Wade.DatasetList(RULE_COLUMNS);
	popupPage ('parametermaintainance.ParaMaintain','getExl','', '目标客户群');
	
	//$.redirect.toPage(null,"parametermaintainance.ParaMaintain","getExl",'');
	
	alert(1);
	var TABLE_NAME = $("#cond_TABLE_NAME2").val();
	alert(TABLE_NAME);
	var param="&TABLE_NAME="+TABLE_NAME;
	
	$.ajax.submit(null, 'initXml', param, null, function(data){
		window.open("attach?action=downloadweb&filePath=template/person/parammaintance/"+TABLE_NAME+".xls");
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误","没有相关模板存在！");
	});	
	return false;
}*/

//同步导出
function exportTemplate() {
	var TABLE_NAME = $("#cond_TABLE_NAME2").val();
	var RULE_ID = $("#cond_RULE_ID").val();
	var RULE_NAME = $("#cond_RULE_NAME").val();
	
	var param="&TABLE_NAME="+TABLE_NAME+"&RULE_ID="+RULE_ID+"&RULE_NAME="+RULE_NAME;
	
	$.beginPageLoading("导出中...");	
	$.ajax.submit('ParaRecordListDivPart','exportExcel',param,'',function(data){
		if (data && data.get("url")) {
			window.open(data.get("url"),"_self");
		}
		$.endPageLoading();
	},function(e,i){
		$.showErrorMessage("导出失败");
		$.endPageLoading();
	});
}

/**
 * 导入数据
 * *//*
function importData(){
	if($("#cond_STICK_LIST").val()==""){
		alert('上传文件不能为空！');
		return false;
	}
	$.beginPageLoading("努力导入中...");
	$.ajax.submit(null,'importData','','',function(data){
		alert('导入成功！');
		resetPage();
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
	
}*/

/**
 * 导入数据
 * */
function ajaxUpload() {
	var ruleId = $("#cond_RULE_ID").val();
	var ruleName = $("#cond_RULE_NAME").val();
	var tableName = $("#cond_TABLE_NAME2").val();
	
	var param="&TABLE_NAME="+tableName+"&RULE_ID="+ruleId+"&RULE_NAME="+ruleName;
	
	var FILE_PATH = $("#FILE_PATH").val();
	if (FILE_PATH =="") {
		return ;
	}
	
	$.beginPageLoading("努力导入中...");
	$.ajax.submit('ParaRecordListDivPart', 'uploadData', param, '', function(data){
		if (data.get("RESULT_CODE")=="0") {
			MessageBox.alert("提示","导入成功！");
		} else {
			if (data && data.get("DOWNLOAD_URL")) {
				MessageBox.error("错误","导入成功"+data.get("SUC_SIZE")+"条，导入失败"+data.get("ERR_SIZE")+"条，导入失败数据请见附件！");
				window.open(data.get("DOWNLOAD_URL"),"_self");
			}
		}		
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	});
}
