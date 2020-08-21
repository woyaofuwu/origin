function refreshPartAtferAuth(data) {
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="
			+ (data.get("USER_INFO")).toString(),
			'EditPart,filterSnPart',
			function(data) {
				$("#TipBar").css("display","");
				$("#tipSpan").empty();
				if(data.get("PHONE_SIZE") ==0){
					//$("#OPEN_SMS").attr("disabled",true);
					$("#tipSpan").append("提醒：该用户未开通来电拒接！");
				}else{
					$("#tipSpan").append("提醒：该用户已经开通来电拒接！");
				}
		
			$.endPageLoading();
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
			});
}

//点击表格行，初始化编辑区
function tableRowClick(){
	var rowData = $.table.get("phoneTable").getRowData();
	$("#REJECT_SN").val(rowData.get("REJECT_SN"));
	$("#REMARK").val(rowData.get("REMARK"));
	
}

function createPhone(obj){
	/* 校验所有的输入框 */
	if (!checkEditPart()) return false;	
	
	var jwtongTag=$("#JWTONG_TAG").val();

	if(jwtongTag=="1")
	{
		alert("该用户为监务通客户，暂时不能申请呼入限制业务！");
		return;	
	}	
	if($("#AUTH_SERIAL_NUMBER").val()==$("#REJECT_SN").val())
	{
		alert("不能设置本身号码为拒接号码!");
		return;	
	}
	if(($("#PHONE_SIZE").val()=="1") && ($("#PRE_PHONE_SIZE").val()=="0"))
	{
		alert("用户办理来电拒接开户时只能新增一个号码!");
		return;	
	}
	if((parseInt($("#PHONE_SIZE").val())+1)> 10)
	{
		alert("一个用户最多只能设置10个拒接号码,您已经达到最大限度!");
		return;	
	}
	
	var phoneTable = $.table.get("phoneTable").getTableData(null, true);
	
	var editData = $.ajax.buildJsonData("editPart");
	editData['REJECT_SN']=$("#REJECT_SN").val();
	editData['REMARK']=$("#REMARK").val();
	
	if($.table.get("phoneTable").isPrimary("REJECT_SN", editData)){
		alert("该号码已经存在,请重新输入！");
		return false;
	}
	/* 新增表格行 */
	$.table.get("phoneTable").addRow(editData,null,null,null,true);
	
	$("#bcreate").attr("disabled",false);	
	$("#bupdate").attr("disabled",false);	
	$("#bdelete").attr("disabled",true);	
	$("#bdeleteall").attr("disabled",true);
	
	$("#bdelete").attr("className","e_dis");	
	$("#bdeleteall").attr("className","e_dis");

	$("#PHONE_SIZE").val(phoneTable.length+1);
}

function updatePhone(obj){
	
		var rowData = $.table.get("phoneTable").getRowData();
		if (rowData.length == 0) {
			alert("请您选择记录后再进行修改操作！");
			return false;
		}
		
		if(rowData.get("START_DATE") != ""){
			alert("只能对本次业务新增号码进行修改，请重新选择！");	
			return false;
		}
		
		if($("#AUTH_SERIAL_NUMBER").val()==$("#REJECT_SN").val())
		{
			alert("不能设置本身号码为拒接号码!");
			return;	
		}
		 /* 校验所有的输入框 */
		if (!checkEditPart()) return false;	
		
		var editData = $.ajax.buildJsonData("editPart");
		editData['REJECT_SN']=$("#REJECT_SN").val();
		editData['REMARK']=$("#REMARK").val();
		
		if($.table.get("phoneTable").isPrimary("REJECT_SN", editData)){
			if($("#REJECT_SN").val()==rowData.get("REJECT_SN")){
				
			}else{
				alert("该号码已经存在,请重新输入！");
				return false;
			}
		}		
		
		$.table.get("phoneTable").updateRow(editData,null,null,true);
}
function operationSMS(obj){//开通短信提醒功能
	
	if(obj.checked){
		obj.value="1";
	}
	else{			
		obj.value="0";
	}
}
function deletePhone(obj){
	
	var rowData = $.table.get("phoneTable").getRowData();
	if (rowData.length == 0) {
		alert("请您选择记录后再进行删除操作！");
		return false;
	}
	
	$.table.get("phoneTable").deleteRow();
	
	var phoneTable = $.table.get("phoneTable").getTableData(null, true);

	var size =0;
	phoneTable.each(function(item,index,totalCount){

		if(item.get("tag") !="1"){
			size++;
		}
	});
	
	$("#PHONE_SIZE").val(size);
	
	$("#bcreate").attr("disabled",true);	
	$("#bupdate").attr("disabled",true);	
	$("#bdelete").attr("disabled",false);	
	$("#bdeleteall").attr("disabled",true);

	$("#bcreate").attr("className","e_dis");	
	$("#bupdate").attr("className","e_dis");	

	$("#bdeleteall").attr("className","e_dis");
}

function deleteAllPhone(obj){


	var length = $.table.get("phoneTable").getTableData(null, true).length;
	
	for(var i=0;i<length;i++){
		$.table.fn.selectedRow('phoneTable', $("#phoneTable")[0].rows[i + 1]);
		$.table.get("phoneTable").deleteRow();
	}
	$("#PHONE_SIZE").val(0);
	$("#OPER_TYPE").val("2");
	$("#bcreate").attr("disabled",true);	
	$("#bupdate").attr("disabled",true);	
	$("#bdelete").attr("disabled",true);	
	$("#bdeleteall").attr("disabled",true);

	$("#bcreate").attr("className","e_dis");	
	$("#bupdate").attr("className","e_dis");	
	$("#bdelete").attr("className","e_dis");	
	$("#bdeleteall").attr("className","e_dis");

}
function checkEditPart(){
	if(!$.validate.verifyAll("EditPart")) {
		return false;
	}
	return true;
}


function submitTrade(){ 
	
	var submitData = $.table.get("phoneTable").getTableData(null, false);
	var size =0;
	var operType ="-1";
	submitData.each(function(item,index,totalCount){
		if(item.get("tag") =="0")
		{
			size++; $("#OPER_TYPE").val("0"); 
		}else if(item.get("tag")=="1"){
			size++;  $("#OPER_TYPE").val("1");
		}
	});
	if(size<1){
		alert("没有数据可以提交！");
		return false;
	}
	if(parseInt($("#PRE_PHONE_SIZE").val())<=size && $("#OPER_TYPE").val()=="1"){$("#OPER_TYPE").val("2");}
	var param = "&SN_DATASET=" + submitData.toString();
	$.cssubmit.addParam(param);
	return true;
}














