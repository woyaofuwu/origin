//表格点击事件
function mailCustTableClick(){
	//获取选择行的数据
	var rowData = $.table.get("DeptTable").getRowData();
	$("#MODIFY_SERIAL_NUMBER").val(rowData.get("col_SERIAL_NUMBER"));
}

//查询列表
function queryList(){
	var serial_number = $("#SERIAL_NUMBER").val();//用户号码
	var start_date = $("#START_DATE").val();
	var end_date = $("#END_DATE").val();
	$.beginPageLoading("数据查询中..");
	//查询
	$.ajax.submit('MailCustShieldCondPart', 'queryList', null, 'MailCustShieldListPart', function(){
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
	var msgcontent = "确定增加此记录？";
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
	$.ajax.submit('ModifyMailCustShieldPart', 'addRecord', null, null, function(data){
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
	MessageBox.confirm('确定提示',warningmsg,function(btn){
					if(btn == "ok"){
						dodelmailcust();
					}else{
						
					}
			}, null, null);
}

function dodelmailcust(){
	$.beginPageLoading("数据删除中..");
	//删除
	$.ajax.submit('ModifyMailCustShieldPart', 'delRecord', null, null, function(data){
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

