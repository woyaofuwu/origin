function queryItemInfos(){ 
	if(!$.validate.verifyAll("QueryRecordPart"))
	{
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryRecordPart','queryNonBossFeeUserItems','','QueryListPart,editPart',
			function(){ 
			  	$.endPageLoading();
			  	$("#addButton").attr("disabled",false);
				$("#editButton").attr("disabled",false);
				$("#delButton").attr("disabled",false);
				$("#retButton").attr("disabled",false);
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			});
}

function resetQry(){
	$("#cond_DATA_NAME").val('');
	$("#cond_PDATA_ID").val('');
}

//点击表格行，初始化编辑区
	function tableRowClick(){
		
		var rowData = $.table.get("paramTable").getRowData();
		$("#DATA_NAME").val(rowData.get("DATA_NAME"));
		$("#DATA_NAME_OLD").val(rowData.get("DATA_NAME_OLD"));
		$("#REMARK").val(rowData.get("REMARK"));
		$("#PARAM_NAME").val(rowData.get("PARAM_NAME_CODE")); 
		$("#PARA_CODE1").val(rowData.get("PARA_CODE1"));
		$("#PARA_CODE2").val(rowData.get("PARA_CODE2"));
		$("#PARA_CODE3").val(rowData.get("PARA_CODE3"));
		$("#PARA_CODE4").val(rowData.get("PARA_CODE4"));
		$("#START_DATE").val(rowData.get("START_DATE")); 
		$("#END_DATE").val(rowData.get("END_DATE"));
		//alert($("#VALID_FLAG").val(rowData.get("VALID_FLAG_CODE")));
	}
	 
	/** 新增一条记录，对应表格新增按钮 */
	function addItem() {
		/* 校验所有的输入框 */
		if (!checkEditPart()) {return false;}		
		//获取编辑区的数据
		var editData = $.ajax.buildJsonData("editPart");
		editData['DATA_NAME']=$("#DATA_NAME").val(); 
		editData['PARAM_NAME']=$("#PARAM_NAME")[0].options[$("#PARAM_NAME")[0].selectedIndex].text;
		editData['PARAM_NAME_CODE']=$("#PARAM_NAME").val(); 
		editData['PARA_CODE1']=$("#PARA_CODE1").val();  
		editData['PARA_CODE2']=$("#PARA_CODE2").val();  
		editData['PARA_CODE3']=$("#PARA_CODE3").val();  
		editData['PARA_CODE4']=$("#PARA_CODE4").val();  
		editData['REMARK']=$("#REMARK").val(); 
		editData['START_DATE']=$("#START_DATE").val();  
		editData['END_DATE']=$("#END_DATE").val();  
		
		/* 新增表格行 */
		$.table.get("paramTable").addRow(editData);
		$.cssubmit.disabledSubmitBtn(false);
	}
	
	/** 修改一条记录，对应表格修改按钮 */
	function editItem() {
	 	/* 校验所有的输入框 */
		if (!checkEditPart()){return false;} 
		//获取编辑区的数据
		var editData = $.ajax.buildJsonData("editPart");
		editData['DATA_NAME']=$("#DATA_NAME").val(); 
		editData['PARAM_NAME']=$("#PARAM_NAME")[0].options[$("#PARAM_NAME")[0].selectedIndex].text;
		editData['PARAM_NAME_CODE']=$("#PARAM_NAME").val(); 
		editData['PARA_CODE1']=$("#PARA_CODE1").val(); 
		editData['PARA_CODE2']=$("#PARA_CODE2").val(); 
		editData['PARA_CODE3']=$("#PARA_CODE3").val(); 
		editData['PARA_CODE4']=$("#PARA_CODE4").val(); 
		editData['REMARK']=$("#REMARK").val(); 
		editData['START_DATE']=$("#START_DATE").val();  
		editData['END_DATE']=$("#END_DATE").val();  
		editData['UPDATE_TIME']="";
		  
		if( $.table.get("paramTable").isPrimary("DATA_NAME", editData)
		 && $.table.get("paramTable").isPrimary("REMARK", editData) 
		 && $.table.get("paramTable").isPrimary("PARAM_NAME", editData)
		 && $.table.get("paramTable").isPrimary("PARA_CODE1", editData)
		 && $.table.get("paramTable").isPrimary("PARA_CODE2", editData)
		 && $.table.get("paramTable").isPrimary("PARA_CODE3", editData)
		 && $.table.get("paramTable").isPrimary("PARA_CODE4", editData)
		 && $.table.get("paramTable").isPrimary("START_DATE", editData)
		 && $.table.get("paramTable").isPrimary("END_DATE", editData)){
					alert("内容未有任何改动！");
					return false;
			 
		}
		
		$.table.get("paramTable").updateRow(editData);
		$.cssubmit.disabledSubmitBtn(false);
	}
	
	/** 删除一条记录，对应表格删除按钮 */
	function delItem() {
		var rowData = $.table.get("paramTable").getRowData();
		if (rowData.length == 0) {
			alert("请您选择记录后再进行删除操作！");
			return false;
		}
		if(confirm("您确认要删除付款单位【"+ $.table.get("paramTable").getRowData().get("DATA_NAME")+"】？")){
			$.table.get("paramTable").deleteRow();
			$.cssubmit.disabledSubmitBtn(false);
		}
		
	}
	
	function reset(){
		$("#DATA_NAME").val('');
		$("#REMARK").val('');
		$("#PARAM_NAME").val('');
		$("#PARA_CODE1").val('');
		$("#PARA_CODE2").val('');
		$("#PARA_CODE3").val('');
		$("#PARA_CODE4").val('');
		$("#START_DATE").val('');
		$("#END_DATE").val(''); 
	}
	
	function checkEditPart(){
		if(!$.validate.verifyAll("editPart")) {
			return false;
		}
		return true;
	}
	
	function submitCheck(){
		
		$.beginPageLoading("正在提交数据...");
		var submitData = $.DatasetList();
		//获取列表数据
		var listTable=$.table.get("paramTable").getTableData(null,true);
		var changeNum =0;
		//循环处理获取数据
		listTable.each(function(item,index,totalCount){
			if(item.get("tag")=="0" || item.get("tag") =="1" ||item.get("tag") =="2"){
				submitData.add(item);
			}
		});
		if(submitData.length ==0){
			alert("数据未发生变化，请勿提交！");
			$.endPageLoading();
			return false;
		}
		var param = "&ITEM_DATASET=" + submitData.toString();
		//$.showSucMessage(param,"");
		$.cssubmit.addParam(param);  
		
		return true;
	}
	