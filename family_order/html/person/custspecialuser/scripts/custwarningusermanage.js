
//选择类型事件
function onchangetype(){

}

function stateDefault(){
	$("#label_flag").text('标志位：');
	document.getElementById("flag").style.display="none"
	
	$("#lable_reserve1").text('保留字段1：');
	document.getElementById("reserve1").style.display="none"
	
	$("#lable_reserve2").text('保留字段2：');
	document.getElementById("reserve2").style.display="none"
	
	$("#label_modifyflag").text('标志位：');
	document.getElementById("modifyflag").style.display="none"
	
	$("#lable_modifyreserve1").text('保留字段1：');
	document.getElementById("modifyreserve1").style.display="none"
	
	$("#lable_modifyreserve2").text('保留字段2：');
	document.getElementById("modifyreserve2").style.display="none"
	
	$("#id_col_FLAG").text('标志位');
	$("#id_col_RESERVE1").text('保留字段1');
	$("#id_col_RESERVE2").text('保留字段2');
	
	
}

//选择警示性客户类型
function stateWarnningUser(){
	$("#label_flag").text('操作类型：');
	document.getElementById("flag").style.display=""
	
	$("#lable_reserve1").text('投诉类型：');
	document.getElementById("reserve1").style.display=""
	
	$("#lable_reserve2").text('投诉次数：');
	document.getElementById("reserve2").style.display=""
	
	$("#label_modifyflag").text('操作类型：');
	document.getElementById("modifyflag").style.display=""
	
	$("#label_modifyreserve1").text('投诉类型：');
	document.getElementById("modifyreserve1").style.display=""
	
	$("#label_modifyreserve2").text('投诉次数：');
	document.getElementById("modifyreserve2").style.display=""
	
	$("#id_col_FLAG").text('操作类型');
	$("#id_col_RESERVE1").text('投诉类型');
	$("#id_col_RESERVE2").text('投诉次数');
	
}

//表格点击事件
function CustSpecialUserTableClick(){
	
	//获取选择行的数据
	var rowData = $.table.get("DeptTable").getRowData();
	$("#MODIFY_SERIAL_NUMBER").val(rowData.get("col_SERIAL_NUMBER"));
	$("#MODIFY_USER_NAME").val(rowData.get("col_USER_NAME"));
	$("#MODIFY_TYPE").val(rowData.get("col_TYPE"));
	$("#MODIFY_FLAG").val(rowData.get("col_FLAG"));
	$("#MODIFY_RESERVE1").val(rowData.get("col_RESERVE1"));
	$("#MODIFY_RESERVE2").val(rowData.get("col_RESERVE2"));
	$("#MODIFY_START_DATE").val(rowData.get("col_START_DATE"));
	$("#MODIFY_END_DATE").val(rowData.get("col_END_DATE"));
}

//查询列表
function queryList(){

	var serial_number = $("#SERIAL_NUMBER").val();//用户号码
	
	var type = $("#TYPE").val();//类型
	if( type == '' ){
		alert("请选择类型");
		return false;
	}
	
	var flag = $("#FLAG").val();//类型
	var desc = $("#label_flag").text()+flag+" 非法输入，只能输入数字";
	if( false == $.verifylib.checkInteger(flag, desc) ){
		alert(desc);
		return false;
	}
	
	$.beginPageLoading("数据查询中..");

	//查询
	$.ajax.submit('CustSpecialUserManageCondPart', 'queryList', null, 'CustSpecialUserManageListPart', function(){
		$.endPageLoading();
		if( $("#TYPE").val() == '1'){//警示性客户
			stateWarnningUser()
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });

}

//修改记录
function modifyspecialuser(){

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
	
	if( $("#MODIFY_TYPE").val() == ''){
		
		alert('请点击你要修改的行！');
		return false;
	}
	if( $("#MODIFY_START_DATE").val() == ''){
		
		alert('请选择起始时间！');
		return false;
	}
	if( $("#MODIFY_END_DATE").val() == ''){
		
		alert('请选择结束时间！');
		return false;
	}
	
	var flag = $("#MODIFY_FLAG").val();//类型
	var desc = $("#label_modifyflag").text()+flag+" 非法输入，只能输入数字";
	if( false == $.verifylib.checkInteger(flag, desc) ){
		alert(desc);
		return false;
	}
	

	MessageBox.confirm('确定提示','确定修改此记录？',function(btn){
					if(btn == "ok"){
						domodifyspecialuser();
					}else{
						
					}
			}, null, null);
}

function domodifyspecialuser(){

	$.beginPageLoading("数据更新中..");
	
	//修改
	$.ajax.submit('ModifyCustSpecialUserPart,CustSpecialUserManageCondPart', 'modifyRecord', null, 'CustSpecialUserManageListPart', function(data){
		
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
function addspecialuser(){

	if( $("#MODIFY_SERIAL_NUMBER").val() == ''){
		
		alert('请输入手机号码！');
		return false;
	}
	
	if( $("#MODIFY_TYPE").val() == ''){
		
		alert('请选择类型！');
		return false;
	}
	
	if( $("#MODIFY_START_DATE").val() == ''){
		
		alert('请选择起始时间！');
		return false;
	}
	if( $("#MODIFY_END_DATE").val() == ''){
		
		alert('请选择结束时间！');
		return false;
	}
	
	var flag = $("#MODIFY_FLAG").val();//类型
	var desc = $("#label_modifyflag").text()+flag+" 非法输入，只能输入数字";
	if( false == $.verifylib.checkInteger(flag, desc) ){
		alert(desc);
		return false;
	}
	
	
	var msgcontent = "确定增加此记录？";
	
	
	MessageBox.confirm('确定提示',msgcontent,function(btn){
					if(btn == "ok"){
						doaddspecialuser();
					}else{
						
					}
			}, null, null);
	
}

function doaddspecialuser(){
	$.beginPageLoading("数据增加中..");
	
	//修改
	$.ajax.submit('ModifyCustSpecialUserPart', 'addRecord', null, null, function(data){
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
function delspecialuser(){

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
	
	if( $("#MODIFY_TYPE").val() == ''){
		
		alert('请点击你要删除的行！');
		return false;
	}
	
	var warningmsg = '确定删除此记录？';

	
	MessageBox.confirm('确定提示',warningmsg,function(btn){
					if(btn == "ok"){
						dodelspecialuser();
					}else{
						
					}
			}, null, null);
}


function dodelspecialuser(){

	$.beginPageLoading("数据删除中..");
	
	//修改
	$.ajax.submit('ModifyCustSpecialUserPart', 'delRecord', null, null, function(data){
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

