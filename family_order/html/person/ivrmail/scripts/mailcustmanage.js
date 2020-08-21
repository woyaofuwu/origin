//表格点击事件
function mailCustTableClick(){
	
	//获取选择行的数据
	var rowData = $.table.get("DeptTable").getRowData();
	$("#MODIFY_SERIAL_NUMBER").val(rowData.get("col_SERIAL_NUMBER"));
	$("#MODIFY_SECTION_ID").val(rowData.get("col_SECTION_ID"));
	$("#MODIFY_MONTH_LIMIT").val(rowData.get("col_MONTH_LIMIT"));
	$("#MODIFY_LM_STARTDATE").val(rowData.get("col_LM_STARTDATE"));
	$("#MODIFY_LM_ENDDATE").val(rowData.get("col_LM_ENDDATE"));
}

//查询列表
function queryList(){

	var serial_number = $("#SERIAL_NUMBER").val();//用户号码
	var section_id = $("#SECTION_ID").val();//节点
	var month_limit = $("#MONTH_LIMIT").val();//
	var send_count = $("#SEND_COUNT").val();//
	var start_date = $("#START_DATE").val();
	var end_date = $("#END_DATE").val();
	
	$.beginPageLoading("数据查询中..");

	//查询
	$.ajax.submit('MailCustManageCondPart', 'queryList', null, 'MailCustManageListPart', function(){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });

}

//修改记录
function modifymailcust(){

	var deptTable=$.table.get("DeptTable").getTableData(null,true);
	if(deptTable.length<1)
	{
		alert("表格没有数据 ,不能修改！");
		return false;
	}
	
	if( $("#MODIFY_SERIAL_NUMBER").val() == ''){
		
		alert('请点击你要修改的行！');
		return false;
	}
	
	if( $("#MODIFY_SECTION_ID").val() == ''){
		
		alert('请点击你要修改的行！');
		return false;
	}
	
	if( $("#MODIFY_MONTH_LIMIT").val() == ''){
		
		alert('请输入次数限制！');
		return false;
	}
	

	MessageBox.confirm('确定提示','确定修改此记录？',function(btn){
					if(btn == "ok"){
						domodifymailcust();
					}else{
						
					}
			}, null, null);
}

function domodifymailcust(){

	$.beginPageLoading("数据更新中..");
	
	//修改
	$.ajax.submit('ModifyMailCustPart,MailCustManageCondPart', 'modifyRecord', null, 'MailCustManageListPart', function(data){
		
		var ret = data.get('X_RESULTCODE');
		if( ret == 0 ){
			$.showSucMessage("数据更新成功",null)
		}else{
			$.showErrorMessage("数据更新失败",data.get('X_RESULTINFO'))
		}
		
		$.endPageLoading();
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

//新增记录
function addmailcust(){

	if( $("#MODIFY_SERIAL_NUMBER").val() == ''){
		
		alert('请输入手机号码！');
		return false;
	}
	
	if( $("#MODIFY_MONTH_LIMIT").val() == ''){
		
		alert('请输入次数限制！');
		return false;
	}
	
	
	var msgcontent = "确定增加此记录？";
	if( $("#MODIFY_SECTION_ID").val() == ''){
		msgcontent="请注意，新增的记录将会覆盖原有记录，确定增加该号码所有节点记录？";
	}
	
	MessageBox.confirm('确定提示',msgcontent,function(btn){
					if(btn == "ok"){
						doaddmailcust();
					}else{
						
					}
			}, null, null);
	
}

function doaddmailcust(){
	$.beginPageLoading("数据增加中..");
	
	//修改
	$.ajax.submit('ModifyMailCustPart', 'addRecord', null, null, function(data){
		var ret = data.get('X_RESULTCODE');
		if( ret == 0 ){
			$.showSucMessage("数据增加成功",null)
		}else{
			$.showErrorMessage("数据增加失败",data.get('X_RESULTINFO'))
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

//删除记录
function delmailcust(){

	var deptTable=$.table.get("DeptTable").getTableData(null,true);
	if(deptTable.length<1)
	{
		alert("表格没有数据 ,不能删除！");
		return false;
	}
	
	if( $("#MODIFY_SERIAL_NUMBER").val() == ''){
		
		alert('请点击你要删除的行！');
		return false;
	}
	
	var warningmsg = '确定删除此记录？';
	if( $("#MODIFY_SECTION_ID").val() == ''){
		warningmsg = '确定删除此号码所有节点记录？';
	}
	
	MessageBox.confirm('确定提示',warningmsg,function(btn){
					if(btn == "ok"){
						dodelmailcust();
					}else{
						
					}
			}, null, null);
}

//查询已发送次数日志
function querySendLog(){

	var serial_number = $("#SERIAL_NUMBER").val();//用户号码
	var section_id = $("#SECTION_ID").val();//节点
	var start_date = $("#START_DATE").val();
	var end_date = $("#END_DATE").val();
	
	$.beginPageLoading("数据查询中..");

	//查询
	$.ajax.submit('MailSendLogCondPart', 'querySendLog', null, 'MailSendLogListPart', function(){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });

}

//确认清空提示
function confirmDeleteAll(){
	
	var warningmsg = '确定清空所有记录？';
	
	MessageBox.confirm('',warningmsg,function(btn){
					if(btn == "ok"){
						deleteAllRecord();
					}else{
						
					}
			}, null, null);
}
//清空所有记录
function deleteAllRecord(){
	$.beginPageLoading("数据清空中..");
	$.ajax.submit(this, 'deleteAllRecord', null, null, function(data){
		var ret = data.get('X_RESULTCODE');
		if( ret == 0 ){
			$.showSucMessage("数据已全部清空",null)
		}else{
			$.showErrorMessage("数据删除失败",data.get('X_RESULTINFO'))
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


function dodelmailcust(){

	$.beginPageLoading("数据删除中..");
	
	//修改
	$.ajax.submit('ModifyMailCustPart', 'delRecord', null, null, function(data){
		var ret = data.get('X_RESULTCODE');
		if( ret == 0 ){
			$.showSucMessage("数据删除成功",null)
		}else{
			$.showErrorMessage("数据删除失败",data.get('X_RESULTINFO'))
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
    
}

