// 查询集团用户信息
function qryGrpUser(){

	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryproductForm", "qryGrpUser", null, 'acctArea', 
		function(data){
			$("#cond_USER_ID_A").val(data.get("cond_USER_ID_A"));
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}


function onSubmitBaseTradeCheck(){
	
	var serialNum=$("#cond_SERIAL_NUMBER").val();
	var submitData = $.DatasetList();
	var listTable=$.table.get("memberTable").getTableData(null,true);
	var changeNum =0;
	listTable.each(function(item,index,totalCount){ 
		if(item.get("tag")=="0"){  
			submitData.add(item); 
		}
	});
	if(submitData.length ==0){
		alert("数据未发生变化，请勿提交！");
		$.endPageLoading();
		return false;
	}  
	var param = "&SERIAL_NUMBER="+serialNum+"&FTTH_DATASET="+submitData.toString();
	param=param.replace(/%/g,"%25");
	
	$.cssubmit.addParam(param); 
	
	return true;
}

/**
 * 校验终端
 */
function checkTerminal(){
	var resID = $("#RES_ID").val();
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	if(resID == ""){
		alert("请输入终端串码");
		return false;
	}
	$.beginPageLoading("终端校验中......");
	$.ajax.submit(null, 'checkTerminal', "&RES_ID="+resID + "&SERIAL_NUMBER=" + serialNumber, 'terminalPart',
			function(data) {
				$("#EditPart").removeClass("e_dis");
				$("#EditPart").attr("disabled",false);
				$("#addbtn").attr("disabled",false);
				$("#delbtn").attr("disabled",false);
				$.endPageLoading();
				debugger;
				$("#RES_FEE").val("0.0");
				
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
}


/** 新增一条记录，对应表格新增按钮 */
function addItem() {
	/* 校验所有的输入框 */ 	
	var kd_number=$("#KD_NUMBER").val();
	
	if(kd_number==""||kd_number==null){
		alert("请先录入宽带号码！"); 
		$("#KD_NUMBER").focus();
		return false;
	}
	var cust_id=$("#CUST_ID").val();
	//获取编辑区的数据
	var editData = $.ajax.buildJsonData("editPart");
	editData['KD_NUMBER']=kd_number;  
	if($.table.get("memberTable").isPrimary("KD_RES_ID", editData)){
		alert("该宽带号码已经存在列表中,请重新输入！");
		$("#KD_NUMBER").val("");
		$("#KD_NUMBER").focus();
		return false;
	} 
	$.beginPageLoading("校验宽带号码...");
	$.ajax.submit('', 'qryUserWidenet', "&KD_NUMBER="+kd_number+"&CUSTID_GROUP="+cust_id, '', function(rtnData) { 
		$.endPageLoading();  	
		if(rtnData!=null&&rtnData.length > 0){
			var checkResz=rtnData.get("RTNCODE");
			if(checkResz=="1"){
				/* 新增表格行 */
				editData['KD_NUMBER']=rtnData.get("KD_NUMBER"); 
				editData['KD_ADDR']=rtnData.get("KD_ADDR"); 
				editData['KD_PHONE']=rtnData.get("KD_PHONE"); 
				editData['CUST_NAME']=rtnData.get("CUST_NAME"); 
				editData['KD_USERID']=rtnData.get("KD_USERID");
				editData['KD_RES_ID']=$("#RES_ID").val();
				editData['KD_RES_BRAND_NAME']=$("#RES_BRAND_NAME").val();
				editData['KD_RES_KIND_NAME']=$("#RES_KIND_NAME").val();
				editData['KD_RES_STATE_NAME']=$("#RES_STATE_NAME").val();
				editData['KD_RES_SUPPLY_COOPID']=$("#RES_SUPPLY_COOPID").val();
				
				editData['KD_RES_TYPE_CODE']=$("#RES_TYPE_CODE").val();
				editData['KD_RES_BRAND_CODE']=$("#RES_BRAND_CODE").val();
				editData['KD_RES_KIND_CODE']=$("#RES_KIND_CODE").val();
				editData['KD_RES_STATE_CODE']=$("#RES_STATE_CODE").val();
				editData['KD_DEVICE_COST']=$("#DEVICE_COST").val();
				
				editData['KD_RES_FEE']=$("#RES_FEE").val();
				editData['KD_ARTIFICIAL_SERVICES']=$("#Artificial_services").val();
				editData['KD_REMARK']=$("#REMARK").val();
				
				$.table.get("memberTable").addRow(editData);
				$("#KD_NUMBER").val("");
				$("#RES_ID").val("");
				$("#RES_BRAND_NAME").val("");
				$("#RES_KIND_NAME").val("");
				$("#RES_STATE_NAME").val("");
				$("#RES_SUPPLY_COOPID").val("");
				$("#RES_FEE").val("");
				$("#KD_REMARK").val("");
				$("#addbtn").attr("disabled",true);

			}else{				
				alert(rtnData.get("RTNMSG"));
				$("#KD_NUMBER").val("");
				return false;
			} 
		}
	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
	});  
	
}
/** 删除一条记录，对应表格删除按钮 */
function delItem() {
	var rowData = $.table.get("memberTable").getRowData();
	if (rowData.length == 0) {
		alert("请您选择记录后再进行删除操作！");
		return false;
	}
	if(confirm("您确认要删除宽带号码【"+ $.table.get("memberTable").getRowData().get("KD_NUMBER")+"】？")){
		$.table.get("memberTable").deleteRow();
	}
	
}


function selectActive(obj){
		$("#RES_ID").val($(obj).parent().parent().find("#RES_ID_NUM").val());
	}



//-------------------------------------------
function init() {
	var custName = $('#cond_SERIAL_NUMBER').val();
	if(custName ==''){
		$('#bsubmit').attr('disabled','true');	
	}
	else {
		$('#bsubmit').attr('disabled','false');
	}
	
}


	 