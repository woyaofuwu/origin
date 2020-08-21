function selectPackage(){
	$("#cond_SALE_GOODS").empty();
	$("#cond_SALE_GOODS").append("<option value=''>--请选择--</option>");
	var product_id=$("#cond_SALE_PRODUCT").val();
	if(product_id==null||product_id==""){
		return;
	}
	var params="&SELECTTYPE=PAKG&PRODUCT_ID="+product_id;
	
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('','getPackages',params,'PackagePart,EditPackagePart',
			function(data){
			  	$.endPageLoading();
			  	
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			});
}

function selectEditPackage(){
	 
	var product_id=$("#SALE_PRODUCT").val(); 
	if(product_id==null||product_id==""){
		return;
	}
	var params="&SELECTTYPE=PAKG&PRODUCT_ID="+product_id;
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('','getPackages',params,'EditPackagePart',
			function(data){  
				$("#SALE_PACKAGE").attr("disabled",false);
			  	$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			});
}

function selectGoods(){
	var product_id=$("#cond_SALE_PRODUCT").val();
	var package_id=$("#cond_SALE_PACKAGE").val();
	if(product_id==null||product_id==""||package_id==null||package_id==""){
		return;
	}
	var params="&SELECTTYPE=GOODS&PRODUCT_ID="+product_id+"&PACKAGE_ID="+package_id;
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('','getGoods',params,'GoodsPart',
			function(data){  
			  	$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			});
}

function queryInfos(){
	resetEdit();
	if(!$.validate.verifyAll("QueryRecordPart")) {
		return false; 
	}
	var product_id=$("#cond_SALE_PRODUCT").val();
	var package_id=$("#cond_SALE_PACKAGE").val();
	var goods_id=$("#cond_SALE_GOODS").val();
	var params="&SELECTTYPE=LISTS&PRODUCT_ID="+product_id+"&PACKAGE_ID="+package_id+"&GOODS_ID="+goods_id+"&VILID_TAG="+$("#cond_VILID_TAG").val();
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('','queryGoodsLists',params,'QueryListPart',
			function(data){ 
				$("#SALE_PRODUCT").attr("disabled",false);
				$("#SALE_PACKAGE").attr("disabled",false);
				$("#RES_NAME").attr("disabled",false);
				setButton(false);
			  	$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			});
}
//编辑区：通过选择包获取的隐藏GOODS_ID
function selectEditGOODS(){
	var package_id=$("#SALE_PACKAGE").val(); 
	if(package_id==null||package_id==""){
		return;
	}
	var params="&SELECTTYPE=EDITGOODS&PACKAGE_ID="+package_id;
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('','getGoodsElem',params,'',
			function(data){
				var rtnType=data.get("RTN_TYPE"); 
				if(rtnType=="1"){
					$("#GOODS_ID").val(data.get("GOODS_ID"));
				}else{
					alert(data.get("RTN_COMM"));
					$("#SALE_PACKAGE").val("");
					$("#SALE_PACKAGE").attr("disabled",false);
				}				
			  	$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			});
}

//点击表格行，初始化编辑区
function tableRowClick(){  
	resetEdit();
	var rowData = $.table.get("paramTable").getRowData();
	
	$("#SALE_PRODUCT").val(rowData.get("SALE_PRODUCT"));
	$("#GOODS_ID").val(rowData.get("SALE_GOODS")); 
	$("#RES_NAME").val(rowData.get("SALE_GOODS_NAME"));
	var product_id=$("#SALE_PRODUCT").val(); 
	var params="&SELECTTYPE=PAKG&PRODUCT_ID="+product_id;
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('','getPackages',params,'EditPackagePart',
			function(data){  
				$("#SALE_PACKAGE").attr("disabled",false);
				$("#SALE_PACKAGE").val(rowData.get("SALE_PACKAGE"));  
			  	$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			});
	
	$("#GOODS_PROPERTY").val(rowData.get("GOODS_PROPERTY")); 
	$("#PURCHASE_TYPE").val(rowData.get("PURCHASE_TYPE_ID")); 
	$("#ACCOUNT_ID").val(rowData.get("ACCOUNT_ID")); 
	$("#CITY_CODE").val(rowData.get("CITY_CODE")); 
	$("#RES_TYPE_CODE").val(rowData.get("RES_TYPE_CODE")); 
	$("#RES_ID").val(rowData.get("RES_ID"));  
}

//选择了资源的礼品后，需要将该资源附带的几项内容赋值：物料编码、礼品属性、采购类型、礼品数量、账户别名
function selectRes(){
	
}

function setButton(type){
	if(type==true){
		$("#addButton").attr("disabled",true);
		$("#editButton").attr("disabled",true);
		$("#delButton").attr("disabled",true);
		$("#retButton").attr("disabled",true);
	}else{
		$("#addButton").attr("disabled",false);
		$("#editButton").attr("disabled",false);
		$("#delButton").attr("disabled",false);
		$("#retButton").attr("disabled",false);
	}
}

function resetQry(){
	$("#cond_SALE_PRODUCT").val("");
	$("#cond_SALE_PACKAGE").empty();
	$("#cond_SALE_PACKAGE").append("<option value=''>--请选择--</option>");
	$("#cond_SALE_GOODS").empty();
	$("#cond_SALE_GOODS").append("<option value=''>--请选择--</option>");
}

function resetEdit(){
	$("#SALE_PRODUCT").val("");
	$("#SALE_PACKAGE").val(""); 
	$("#GOODS_ID").val(""); 
	$("#SALE_RESOURCE").val("");
	$("#GOODS_PROPERTY").val("");
	$("#RES_ID").val("");
	$("#PURCHASE_TYPE").val("");
	$("#CITY_CODE").val(""); 
}

/** 新增一条记录，对应表格新增按钮 */
function addItem() {
	if (!$.validate.verifyAll("editPart")) return false;
	//获取编辑区的数据
	var editData = $.ajax.buildJsonData("editPart");
	
	editData['SALE_PRODUCT']=$("#SALE_PRODUCT").val(); 
	var saleProd=$("#SALE_PRODUCT")[0].options[$("#SALE_PRODUCT")[0].selectedIndex].text;
	editData['SALE_PRODUCT_NAME']=saleProd.substring(saleProd.indexOf("|")+1);
	
	editData['SALE_PACKAGE']=$("#SALE_PACKAGE").val(); 	
	var salePack=$("#SALE_PACKAGE")[0].options[$("#SALE_PACKAGE")[0].selectedIndex].text; 
	editData['SALE_PACKAGE_NAME']=salePack.substring(salePack.indexOf("|")+1);
	
	editData['SALE_GOODS']=$("#GOODS_ID").val(); 
	editData['SALE_GOODS_NAME']=$("#RES_NAME").val(); 
	editData['ACCOUNT_ID']=$("#ACCOUNT_ID").val();
	editData['ACCOUNT_NAME']=$("#ACCOUNT_ID")[0].options[$("#ACCOUNT_ID")[0].selectedIndex].text; 
	editData['CITY_CODE']=$("#CITY_CODE").val();
	editData['CITY_CODE_NAME']=$("#CITY_CODE")[0].options[$("#CITY_CODE")[0].selectedIndex].text; 
	editData['RES_ID']=$("#RES_ID").val(); 
	editData['GOODS_PROPERTY']=$("#GOODS_PROPERTY").val();
	editData['PURCHASE_TYPE_ID']=$("#PURCHASE_TYPE").val();
	editData['PURCHASE_TYPE']=$("#PURCHASE_TYPE")[0].options[$("#PURCHASE_TYPE")[0].selectedIndex].text;
//	这段代码并非判断一行中5个主要字段是否存在，而是分别判断每个字段在列表中是否唯一。
//	因此屏蔽
//	if($.table.get("paramTable").isPrimary("SALE_PRODUCT", editData)
//			&&$.table.get("paramTable").isPrimary("SALE_PACKAGE", editData)
//			&&$.table.get("paramTable").isPrimary("SALE_GOODS", editData)
//			&&$.table.get("paramTable").isPrimary("RES_ID", editData)
//			&&$.table.get("paramTable").isPrimary("CITY_CODE", editData)){
//		alert("该营销活动对应的礼品在列表中已经存在！");
//		return false;
//	}
	
    var params="&SELECTTYPE=CHECKTAB";
	//校验是否该配置已经存在TD_B_SALE_GOODS_EXT表
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('editPart','checkIfExist',params,'',
			function(data){  
				var rtnType=data.get("RTN_TYPE"); 
				if(rtnType=="0"){
					/* 新增表格行 */
					$.table.get("paramTable").addRow(editData);
					$.cssubmit.disabledSubmitBtn(false);
				}else{
					//报错提示
					alert(data.get("RTN_COMM")); 
				}		 
			  	$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			}); 
}

/** 修改一条记录，对应表格新增按钮 */
function editItem() { 
//	/* 校验所有的输入框 */
	if (!$.validate.verifyAll("editPart")) return false;		
	//获取编辑区的数据
	var editData = $.ajax.buildJsonData("editPart");
	
	editData['SALE_PRODUCT']=$("#SALE_PRODUCT").val(); 
	var saleProd=$("#SALE_PRODUCT")[0].options[$("#SALE_PRODUCT")[0].selectedIndex].text;
	editData['SALE_PRODUCT_NAME']=saleProd.substring(saleProd.indexOf("|")+1);
	
	editData['SALE_PACKAGE']=$("#SALE_PACKAGE").val(); 	
	var salePack=$("#SALE_PACKAGE")[0].options[$("#SALE_PACKAGE")[0].selectedIndex].text; 
	editData['SALE_PACKAGE_NAME']=salePack.substring(salePack.indexOf("|")+1);
	
	editData['SALE_GOODS']=$("#GOODS_ID").val(); 
	editData['SALE_GOODS_NAME']=$("#RES_NAME").val(); 
	editData['ACCOUNT_ID']=$("#ACCOUNT_ID").val();
	editData['ACCOUNT_NAME']=$("#ACCOUNT_ID")[0].options[$("#ACCOUNT_ID")[0].selectedIndex].text; 
	editData['CITY_CODE']=$("#CITY_CODE").val();
	editData['CITY_CODE_NAME']=$("#CITY_CODE")[0].options[$("#CITY_CODE")[0].selectedIndex].text; 
	editData['RES_ID']=$("#RES_ID").val(); 
	editData['GOODS_PROPERTY']=$("#GOODS_PROPERTY").val();
	editData['PURCHASE_TYPE_ID']=$("#PURCHASE_TYPE").val();
	editData['PURCHASE_TYPE']=$("#PURCHASE_TYPE")[0].options[$("#PURCHASE_TYPE")[0].selectedIndex].text;
//	这段代码并非判断一行中5个主要字段是否存在，而是分别判断每个字段在列表中是否唯一。
//	因此屏蔽
//	if($.table.get("paramTable").isPrimary("SALE_PRODUCT", editData)
//			&&$.table.get("paramTable").isPrimary("SALE_PACKAGE", editData)
//			&&$.table.get("paramTable").isPrimary("SALE_GOODS", editData)
//			&&$.table.get("paramTable").isPrimary("RES_ID", editData)
//			&&$.table.get("paramTable").isPrimary("CITY_CODE", editData)){
//		alert("该营销活动对应的礼品在列表中已经存在！");
//		return false;
//	}
	var params="&SELECTTYPE=CHECKTAB&EDITDATA=1";
	//校验是否该配置已经存在TD_B_SALE_GOODS_EXT表
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('editPart','checkIfExist',params,'',
			function(data){  
				var rtnType=data.get("RTN_TYPE"); 
				if(rtnType=="0"){
					/* 修改表格行 */
					$.table.get("paramTable").updateRow(editData);
					$.cssubmit.disabledSubmitBtn(false);
				}else{
					//报错提示
					alert(data.get("RTN_COMM")); 
				}		 
			  	$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			});  
}

/** 删除一条记录，对应表格删除按钮 */
function delItem() {
	var rowData = $.table.get("paramTable").getRowData();
	if (rowData.length == 0) {
		alert("请您选择记录后再进行删除操作！");
		return false;
	}
	if(confirm("您确认要删除项目【"+ $.table.get("paramTable").getRowData().get("SALE_GOODS_NAME")+"】？")){
		$.table.get("paramTable").deleteRow();
		$.cssubmit.disabledSubmitBtn(false);
	} 
}

/**
 * 提交数据
 * */
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
		alert("数据未发生变化，无法提交！");
		$.endPageLoading();
		return false;
	}  
	var param = "&SELECTTYPE=SUBMIT&ITEM_DATASET="+submitData.toString();
	param=param.replace(/%/g,"%25");  
	$.cssubmit.addParam(param); 
	return true;
}