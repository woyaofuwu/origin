/** 
 * */
function refreshPartAtferAuth(data)
{  
	$.beginPageLoading("正在查询数据...");
	var userInfo = data.get("USER_INFO");
	$.ajax.submit('', 'checkFTTHBusi', "&USER_ID="+userInfo.get("USER_ID")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER"), '', function(rtnData) { 
				$.endPageLoading();  	
				if(rtnData!=null&&rtnData.length > 0){
					//alert(rtnData.get("RTNMSG"));	
					MessageBox.error("错误提示", rtnData.get("RTNMSG"), $.auth.reflushPage, null, null, null);
					return false;
				} 
				$("#EditPart").removeClass("e_dis");
				$("#EditPart").attr("disabled",false);
				$("#addbtn").attr("disabled",false);
				$("#delbtn").attr("disabled",false);
				$("#CUST_ID").val(userInfo.get("CUST_ID"));
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
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
	if($.table.get("memberTable").isPrimary("KD_NUMBER", editData)){
		alert("该宽带号码已经存在列表中,请重新输入！");
		$("#KD_NUMBER").val("");
		$("#KD_NUMBER").focus();
		return false;
	} 
	$.beginPageLoading("校验宽带号码...");
	$.ajax.submit('', 'checkKDNumber', "&KD_NUMBER="+kd_number+"&CUSTID_GROUP="+cust_id, '', function(rtnData) { 
		$.endPageLoading();  	
		if(rtnData!=null&&rtnData.length > 0){
			var checkResz=rtnData.get("RTNCODE");
			if(checkResz=="1"){
				/* 新增表格行 */
				editData['CUST_NAME']=rtnData.get("CUST_NAME"); 
				editData['UPDATE_TIME']=rtnData.get("UPDATE_TIME"); 
				editData['KD_USERID']=rtnData.get("KD_USERID"); 
				editData['KD_TRADE_ID']=rtnData.get("KD_TRADE_ID"); 
				$.table.get("memberTable").addRow(editData);
				$("#KD_NUMBER").val("");
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

function onTradeSubmit()
{
	var serialNum=$("#AUTH_SERIAL_NUMBER").val();
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