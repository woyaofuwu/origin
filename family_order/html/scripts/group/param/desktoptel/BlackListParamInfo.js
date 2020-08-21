//用来初始化页面的显示
var svcparamvalue ="";	
function loadBlackListParamInfo() 
{		
	try{
	  	svcparamvalue=$.getSrcWindow().selectedElements.getAttrs($("#ITEM_INDEX").val());
	  	if(svcparamvalue.get(0,"PARAM_VERIFY_SUCC")==undefined)
	  	{
	  	  svcparamvalue="";
	  	}
	}
	catch(e)
	{
		svcparamvalue="";
	}
	 
	var userIda=$.getSrcWindow().$("#GRP_USER_ID").val();
	var userId=$.getSrcWindow().$("#MEB_USER_ID").val();
	var groupId=$.getSrcWindow().$("#GROUP_ID").val();	
	if(userIda==undefined)	userIda="";
  	if(userId==undefined) userId="";
  	if(groupId==undefined) groupId="";
	var mebEparchyCode=$.getSrcWindow().$("#MEB_EPARCHY_CODE").val();    
	var serviceId = $("#SERVICE_ID").val();
	var param ='&SERVICE_ID='+serviceId+'&MEB_USER_ID='+userId+'&MEB_EPARCHY_CODE='+mebEparchyCode;
	if(svcparamvalue==""||svcparamvalue=="[]" || svcparamvalue.getCount() <2)//没有找到已存在的参数值(第二条记录才是具体的参数信息)
	{	 
		if(userId == ""){//新增时不用查询表内数据
			return;
		}
		$.ajax.submit(this, 'refreshBWLish', param, 'ListPart', function(data){
				$.endPageLoading();		
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		 });
			
	}
	else //已经存在值 但还不确定是不是就是详细的参数信息
	{
		putpagedata(svcparamvalue);

	}		
}
function putpagedata(svcparamvalue){
	var table = $.table.get("ListArea");
	var list = svcparamvalue.get(1,"BLACK_LIST");
	for(var i=0; i<list.length; i++){
		var info = list.get(i).toString(false);
		var tableInfo=$.parseJSON(info);
		table.addRow(tableInfo);
	}
}
function tableRowClick() {
	//获取选择行的数据
	var rowData = $.table.get("ListArea").getRowData();
	$("#SERIAL_NUMBER").val(rowData.get("SERIAL_NUMBER"));  	
	$("#BIZ_CODE").val(rowData.get("BIZ_CODE")); 
}
function createBlackList() {
	if(!$.validate.verifyAll("EditPart")) {
		return false;
	}
	var editInfo = $.ajax.buildJsonData("EditPart");
	editInfo["X_TAG"] = "0";   //0新增 1删除 2修改
	var table = $.table.get("ListArea");
	
	var serialNum = $("#SERIAL_NUMBER").val();
	var bizCode = $("#BIZ_CODE").val();
	
	var ds = table.getTableData(null,true); //获得表格的数据	
	for(var i=0; i<ds.length; i++){
		var serialNumTbl = ds.get(i).get("SERIAL_NUMBER");  //表格中的服务号码
		var bizCodeTbl = ds.get(i).get("BIZ_CODE"); 
		if(serialNum == serialNumTbl && bizCode == bizCodeTbl) {
			alert('请不要重复添加！');			
			return false;
		}
	}
	table.addRow(editInfo);	
	$("#SERIAL_NUMBER").val(''); 
	$("#BIZ_CODE").val(''); 
}

function deleteBlackList() {
	var table = $.table.get("ListArea");
	table.deleteRow();
	$("#SERIAL_NUMBER").val(''); 
	$("#BIZ_CODE").val('');
}

function setDataToButton(){	
	var table = $.table.get("ListArea");
	var ds = table.getTableData(null,true); //获得表格的数据
	if("[]"==ds || ""==ds || null ==ds){
		alert("列表为空或者没有修改！");
		return false;
	}else{
		var dataset =  $.DatasetList();
		var serviceId = $("#SERVICE_ID").val();
		var paramVerifySucc= $.DataMap();
		paramVerifySucc.put("PARAM_VERIFY_SUCC","true");
		var param = $.DataMap();
		param.put("BLACK_LIST",ds);
		param.put("SERVICE_ID",serviceId);
		dataset.add(paramVerifySucc);
		dataset.add(param);
		var itemIndex= $("#ITEM_INDEX").val();
		$.getSrcWindow().selectedElements.updateAttr(itemIndex,dataset.toString());//调置到产品组件
		$.setReturnValue();
	}
}

function cancleDataToButton(){
	$.setReturnValue();
}