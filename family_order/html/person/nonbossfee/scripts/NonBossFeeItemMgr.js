function queryItemInfos(){
	
	if(!$.validate.verifyAll("QueryRecordPart"))
	{
		return false;
	}
	reset();
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryRecordPart','queryNonBossFeeItems','','QueryListPart',
			function(data){ 
				$.cssubmit.disabledSubmitBtn(false);
				$("#addButton").attr("disabled",false);
				$("#editButton").attr("disabled",false);
				$("#delButton").attr("disabled",false);
				$("#retButton").attr("disabled",false);
			  	$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			});
}

//点击表格行，初始化编辑区
	function tableRowClick(){ 
		reset();
		var rowData = $.table.get("paramTable").getRowData();
		$("#DATA_ID").val(rowData.get("DATA_ID"));
		$("#DATA_ID_OLD").val(rowData.get("DATA_ID_OLD"));
		$("#DATA_NAME").val(rowData.get("DATA_NAME"));
		$("#PARAM_NAME").val(rowData.get("PARAM_CODE"));
		$("#PARA_CODE1").val(rowData.get("PARA_CODE1"));
		var tax= rowData.get("PARA_CODE2");
		$("#PARA_CODE2").val(tax.replace("%",""));
		$("#PARA_CODE3").val(rowData.get("PARA_CODE3"));
		$("#PARA_CODE4").val(rowData.get("PARA_CODE4"));
		$("#PARA_CODE5").val(rowData.get("PARA_CODE5"));
		$("#PARA_CODE6").val(rowData.get("PARA_CODE6"));
		$("#PARA_CODE7").val(rowData.get("PARA_CODE7"));
		$("#PARA_CODE8").val(rowData.get("PARA_CODE8"));
		$("#PARA_CODE9").val(rowData.get("PARA_CODE9"));
		$("#START_DATE").val(rowData.get("START_DATE"));
		$("#END_DATE").val(rowData.get("END_DATE"));
		$("#REMARK").val(rowData.get("REMARK"));
		$("#PARA_CODE10").val(rowData.get("PARA_CODE10"));
	}
	
	/** 新增一条记录，对应表格新增按钮 */
	function addItem() {
		/* 校验所有的输入框 */
		if (!checkEditPart()) return false;		
		//获取编辑区的数据
		var editData = $.ajax.buildJsonData("editPart");
		
		editData['DATA_ID']=$("#DATA_ID").val();
		editData['DATA_ID_OLD']="";
		editData['DATA_NAME']=$("#DATA_NAME").val(); 
		if($("#PARAM_NAME").val()!=null&&$("#PARAM_NAME").val()!=""){
			editData['PARAM_CODE']=$("#PARAM_NAME").val();
			var param_name=$("#PARAM_NAME")[0].options[$("#PARAM_NAME")[0].selectedIndex].text;
			param_name=param_name.substring(param_name.indexOf("|")+1);
			editData['PARAM_NAME']=param_name;
		}else{
			editData['PARAM_NAME']=$("#PARAM_NAME_OTHER").val();
		}
		
		if($("#PARA_CODE1").val()!=null&&$("#PARA_CODE1").val()!=""){
			editData['PARA_CODE1']=$("#PARA_CODE1").val();
			var para_code1=$("#PARA_CODE1")[0].options[$("#PARA_CODE1")[0].selectedIndex].text;
			para_code1=para_code1.substring(para_code1.indexOf("|")+1);
			editData['PARA_CODE1_NAME']=para_code1;
		}else{
			editData['PARA_CODE1_NAME']=$("#PARA_CODE1_OTHER").val();
		}
		
		if($("#PARA_CODE3").val()!=null&&$("#PARA_CODE3").val()!=""){
			editData['PARA_CODE3']=$("#PARA_CODE3").val();
			var para_code3=$("#PARA_CODE3")[0].options[$("#PARA_CODE3")[0].selectedIndex].text;
			para_code3=para_code3.substring(para_code3.indexOf("|")+1);
			editData['PARA_CODE3_NAME']=para_code3;
		}else{
			editData['PARA_CODE3_NAME']=$("#PARA_CODE3_OTHER").val();
		}
		 
		if($("#PARA_CODE10").val()!=null&&$("#PARA_CODE10").val()!=""){
			editData['PARA_CODE10']=$("#PARA_CODE10").val();
			var para_code10=$("#PARA_CODE10")[0].options[$("#PARA_CODE10")[0].selectedIndex].text;
			para_code10=para_code10.substring(para_code10.indexOf("|")+1);
			editData['PARA_CODE10_NAME']=para_code10;
		}else{
			editData['PARA_CODE10_NAME']=$("#PARA_CODE10_OTHER").val();
		}
		
		
		editData['PARA_CODE2']=$("#PARA_CODE2").val();
		editData['PARA_CODE4']=$("#PARA_CODE4").val(); 
		editData['PARA_CODE5']=$("#PARA_CODE5").val();
		editData['PARA_CODE6']=$("#PARA_CODE6").val(); 
		editData['PARA_CODE7']=$("#PARA_CODE7").val();
		editData['PARA_CODE8']=$("#PARA_CODE8").val(); 
		editData['PARA_CODE9']=$("#PARA_CODE9").val();
		editData['START_DATE']=$("#START_DATE").val(); 
		editData['END_DATE']=$("#END_DATE").val();       
		editData['REMARK']=$("#REMARK").val();  
		
		if($.table.get("paramTable").isPrimary("DATA_NAME", editData)){
			alert("该费用项已经存在,请重新输入！");
			return false;
		}else if($.table.get("paramTable").isPrimary("DATA_ID", editData)){
			alert("该费用项编码已经存在,请重新输入！");
			return false;
		}
		/* 新增表格行 */
		$.table.get("paramTable").addRow(editData);
	}
	
	/** 修改一条记录，对应表格修改按钮 */
	function editItem() {
		/* 校验所有的输入框 */
		if (!checkEditPart()) return false;		
		//获取编辑区的数据
		var editData = $.ajax.buildJsonData("editPart");
		
		editData['DATA_ID']=$("#DATA_ID").val();
		editData['DATA_NAME']=$("#DATA_NAME").val(); 
		if($("#PARAM_NAME").val()!=null&&$("#PARAM_NAME").val()!=""){
			editData['PARAM_CODE']=$("#PARAM_NAME").val();
			var param_name=$("#PARAM_NAME")[0].options[$("#PARAM_NAME")[0].selectedIndex].text;
			param_name=param_name.substring(param_name.indexOf("|")+1);
			editData['PARAM_NAME']=param_name;
		}else{
			editData['PARAM_CODE']="";
			editData['PARAM_NAME']=$("#PARAM_NAME_OTHER").val();
		}
		
		if($("#PARA_CODE1").val()!=null&&$("#PARA_CODE1").val()!=""){
			editData['PARA_CODE1']=$("#PARA_CODE1").val();
			var para_code1=$("#PARA_CODE1")[0].options[$("#PARA_CODE1")[0].selectedIndex].text;
			para_code1=para_code1.substring(para_code1.indexOf("|")+1);
			editData['PARA_CODE1_NAME']=para_code1;
		}else{
			editData['PARA_CODE1']="";
			editData['PARA_CODE1_NAME']=$("#PARA_CODE1_OTHER").val();
		}
		
		if($("#PARA_CODE3").val()!=null&&$("#PARA_CODE3").val()!=""){
			editData['PARA_CODE3']=$("#PARA_CODE3").val();
			var para_code3=$("#PARA_CODE3")[0].options[$("#PARA_CODE3")[0].selectedIndex].text;
			para_code3=para_code3.substring(para_code3.indexOf("|")+1);
			editData['PARA_CODE3_NAME']=para_code3;
		}else{
			editData['PARA_CODE3']="";
			editData['PARA_CODE3_NAME']=$("#PARA_CODE3_OTHER").val();
		}
		
		if($("#PARA_CODE10").val()!=null&&$("#PARA_CODE10").val()!=""){
			editData['PARA_CODE10']=$("#PARA_CODE10").val();
			var para_code10=$("#PARA_CODE10")[0].options[$("#PARA_CODE10")[0].selectedIndex].text;
			para_code10=para_code10.substring(para_code10.indexOf("|")+1);
			editData['PARA_CODE10_NAME']=para_code10;
		}else{
			editData['PARA_CODE10']="";
			editData['PARA_CODE10_NAME']=$("#PARA_CODE10_OTHER").val();
		}
		 
		editData['PARA_CODE2']=$("#PARA_CODE2").val();
		editData['PARA_CODE4']=$("#PARA_CODE4").val(); 
		editData['PARA_CODE5']=$("#PARA_CODE5").val();
		editData['PARA_CODE6']=$("#PARA_CODE6").val(); 
		editData['PARA_CODE7']=$("#PARA_CODE7").val();
		editData['PARA_CODE8']=$("#PARA_CODE8").val(); 
		editData['PARA_CODE9']=$("#PARA_CODE9").val();
		editData['START_DATE']=$("#START_DATE").val(); 
		editData['END_DATE']=$("#END_DATE").val();       
		editData['REMARK']=$("#REMARK").val();  
		editData['UPDATE_TIME']="";  
		
		if($.table.get("paramTable").isPrimary("DATA_ID", editData)
			&&$.table.get("paramTable").isPrimary("DATA_NAME", editData)	
			&&$.table.get("paramTable").isPrimary("PARAM_CODE", editData)	
			&&$.table.get("paramTable").isPrimary("PARAM_NAME", editData)	
			&&$.table.get("paramTable").isPrimary("PARA_CODE1", editData)	
			&&$.table.get("paramTable").isPrimary("PARA_CODE1_NAME", editData)	
			&&$.table.get("paramTable").isPrimary("PARA_CODE2", editData)	
			&&$.table.get("paramTable").isPrimary("PARA_CODE3", editData)	
			&&$.table.get("paramTable").isPrimary("PARA_CODE3_NAME", editData)	
			&&$.table.get("paramTable").isPrimary("PARA_CODE4", editData)	
			&&$.table.get("paramTable").isPrimary("PARA_CODE5", editData)	
			&&$.table.get("paramTable").isPrimary("PARA_CODE6", editData)	
			&&$.table.get("paramTable").isPrimary("PARA_CODE7", editData)	
			&&$.table.get("paramTable").isPrimary("PARA_CODE8", editData)	
			&&$.table.get("paramTable").isPrimary("PARA_CODE9", editData)
			&&$.table.get("paramTable").isPrimary("PARA_CODE10", editData)
			&&$.table.get("paramTable").isPrimary("REMARK", editData)	
			&&$.table.get("paramTable").isPrimary("START_DATE", editData)	
			&&$.table.get("paramTable").isPrimary("END_DATE", editData)
		){
			alert("没有数据项发生变化！");
			return false;
		}
		
		$.table.get("paramTable").updateRow(editData);
	}
	
	/** 删除一条记录，对应表格删除按钮 */
	function delItem() {
		var rowData = $.table.get("paramTable").getRowData();
		if (rowData.length == 0) {
			alert("请您选择记录后再进行删除操作！");
			return false;
		}
		if(confirm("您确认要删除费用项目【"+ $.table.get("paramTable").getRowData().get("DATA_NAME")+"】？")){
			$.table.get("paramTable").deleteRow();
		}
		
	}
	
	function reset(){
		$("#DATA_ID").val('');
		$("#DATA_ID_OLD").val('');
		$("#DATA_NAME").val('');
		$("#PARAM_NAME").val('');
		$("#PARAM_NAME_OTHER").val('');
		turnBackList("income");
		$("#PARA_CODE1").val('');
		$("#PARA_CODE1_OTHER").val('');
		$("#PARA_CODE2").val('');
		turnBackList("tax");
		$("#PARA_CODE3").val('');
		$("#PARA_CODE3_OTHER").val('');
		turnBackList("invoice");
		$("#PARA_CODE5").val('');
		$("#PARA_CODE6").val('');
		$("#PARA_CODE7").val('');
		$("#PARA_CODE8").val('');
		$("#PARA_CODE9").val('');
		$("#PARA_CODE4").val('');
		$("#START_DATE").val('');
		$("#END_DATE").val('');
		$("#REMARK").val('');
	}
	
	function checkEditPart(){
		
		if(!$.validate.verifyAll("editPart")) {
			return false;
			
		}else{
			if(($("#PARAM_NAME").val()==null||$("#PARAM_NAME").val()=="")&&($("#PARAM_NAME_OTHER").val()==null||$("#PARAM_NAME_OTHER").val()=="") ){
				alert( "【收入类别】不能为空，请输入！" ); 
				$("#PARAM_NAME").focus();
				return false;
			} 
			if(($("#PARA_CODE3").val()==null||$("#PARA_CODE3").val()=="")&&($("#PARA_CODE3_OTHER").val()==null||$("#PARA_CODE3_OTHER").val()=="")){
				alert( "【发票项目】不能为空，请输入！" ); 
				$("#PARA_CODE3").focus();
				return false;
			} 
			if(($("#PARA_CODE1").val()==null||$("#PARA_CODE1").val()=="")&&($("#PARA_CODE1_OTHER").val()==null||$("#PARA_CODE1_OTHER").val()=="")){
				alert( "【应税类型】不能为空，请输入！" ); 
				$("#PARA_CODE1").focus();
				return false;
			}  
		}
		return true;
	}
	
	function submitCheck(){
		
		$.beginPageLoading("正在提交数据...");
		var submitData = $.DatasetList();
		var listTable=$.table.get("paramTable").getTableData(null,true);
		var changeNum =0;
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
		var param = "&ITEM_DATASET="+submitData.toString();
		param=param.replace(/%/g,"%25");
		
		$.cssubmit.addParam(param); 
		
		return true;
	}
	
	
	function selectOther(vtype){
		 
		if(vtype!=""&&vtype=="income"){
			$("#incomeList").css("display","none");
			$("#incomeLiText").css("display","");
			$("#PARAM_NAME").val("");
		}
		if(vtype!=""&&vtype=="tax"){
			$("#taxList").css("display","none");
			$("#taxLiText").css("display","");
			$("#PARA_CODE1").val("");
		} 
		if(vtype!=""&&vtype=="invoice"){
			$("#invoiceList").css("display","none");
			$("#invoiceLiText").css("display","");
			$("#PARA_CODE3").val("");
		}
	}
	
	function turnBackList(backtype){ 
		if(backtype=="income"){
			$("#incomeList").css("display","");
			$("#incomeLiText").css("display","none");
			$("#PARAM_NAME_OTHER").val("");
		} 
		if(backtype=="tax"){
			$("#taxList").css("display","");
			$("#taxLiText").css("display","none");
			$("#PARA_CODE1_OTHER").val("");
		} 
		if(backtype=="invoice"){
			$("#invoiceList").css("display","");
			$("#invoiceLiText").css("display","none");
			$("#PARA_CODE3_OTHER").val("");
		} 
	}
	
	function resetQry(){
		$("#cond_DATA_NAME").attr("disabled",false);
		$("#cond_PARAM_NAME").attr("disabled",false);
		$("#cond_PARA_CODE3").attr("disabled",false);
		$("#cond_DATA_NAME").val("");
		$("#cond_PARAM_NAME").val("");
		$("#cond_PARA_CODE3").val("");
		$("#cond_VALID_FLAG").val("1");
	}
	function validChange(){ 
		if($("#cond_VALID_FLAG").val()=="0"){
			$("#cond_DATA_NAME").val("");
			$("#cond_DATA_NAME").attr("disabled",true);
			$("#cond_PARAM_NAME").val("");
			$("#cond_PARAM_NAME").attr("disabled",true);
			$("#cond_PARA_CODE3").val(""); 
			$("#cond_PARA_CODE3").attr("disabled",true);
		}else{
			resetQry();
		}
	}