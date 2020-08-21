var hk_count = 0;
function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString(),'', function(data){
		$("#QueryCondPart").removeClass("e_dis")
		//查询后解开卡号输入的限制
		
		$("#addButtom").attr("disabled",null);
		$("#START_CARD_NO").val("");
		$("#END_CARD_NO").val("");
		$("#START_CARD_NO").focus();
		cleanTable();
		
		//设置提示信息
		if(data.get("HINT_INFO")!=null&&data.get("HINT_INFO")!=""){
			$("#ALERT_INFO").text(data.get("HINT_INFO"));
			$("#ALERT_INFO_DIV").css("display","block");
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
function startCardKeyUp() {
	if($("#RES_KIND_CODE").val()!="C"){
		$("#END_CARD_NO").val($("#START_CARD_NO").val());
	}
}
function textToUpperCase(){
	  if(event.keyCode>=65 && event.keyCode<=90 || event.type=="change"){
	    event.srcElement.value = event.srcElement.value.toUpperCase();
	  }
	}
function cleanTable(){
	//禁止提交
	$("#SubmitPart").addClass("e_dis");
	$("#CSSUBMIT_BUTTON").attr("disabled","true");
	//清空表格
		var table1 = document.getElementById("table1");
		//有价卡换卡没有table1
		if(table1!=null){
			var length1 = table1.rows.length - 1;
			for(var i = 0; i < length1; i++) {
				table1.deleteRow(1);
			}
		}
		var table2 = document.getElementById("table2");
		var length2 = table2.rows.length - 1;
		for(var i = 0; i < length2; i++) {
			table2.deleteRow(1);
		}
		//清除购卡费
		modFee("0","20",0);
}
//选择连号
function changeMethod1(){
	$("#numberInput").css("display","block");
	$("#fileInput").css("display","none");
	
	$("#START_CARD_NO").val("");
	$("#END_CARD_NO").val("");
	$("#STAFF_ID").val("");
	$("#REMARK").val("");
	$("#import_STAFF_ID").val("");
	$("#DEAL_METHOD").val("1");	
}
//选择文件导入
function changeMethod2(){	
	$("#numberInput").css("display","none");
	$("#fileInput").css("display","block");
	
	$("#START_CARD_NO").val("");
	$("#END_CARD_NO").val("");
	$("#STAFF_ID").val("");
	$("#REMARK").val("");
	$("#import_STAFF_ID").val("");
	$("#import_CARD_LIST").val("");
	$("#DEAL_METHOD").val("2");
}
//导入文件
function importfile(){
	
	if(!$.validate.verifyAll("fileInput")) {
		return false;
	}
	if($("#FILE_FIELD1").val()==""){
	   alert("请先导入文件")
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	//
	$.ajax.submit('fileInput', 'importFile', null, '', function(data){
		$("#import_CARD_LIST").val(data.get("TEXT_CONTENT"));
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
//点击“加入”时作验证 
function addValueCardGive(objTable) {
	
	var method = $("#DEAL_METHOD").val();
	
	if(method == "1"){
		if(!$.validate.verifyAll("numberInput")) {
			return false;
		}
		
		var start = $("#START_CARD_NO");
		var end = $("#END_CARD_NO");
		var vstart = start.val();
		var vend = end.val();
		
		if(vstart == "") {
			alert("状态:请检查卡号！");
			start.focus();
			return false;
		}
		if(vend == "") {
			alert("状态:请检查卡号！");
			end.focus();
			return false;
		}
		
		var citycode = $("#CITY_CODE");
		var staffid = $("#STAFF_ID");
		var vcitycode = citycode.val();
		var vstaffid = staffid.val();
		if(vcitycode == "") {
			alert("状态:请检查业务区！");
			citycode.focus();
			return false;
		}
		if(vstaffid == "") {
			alert("状态:请检查工号！");
			staffid.focus();
			return false;
		}	
	}
	
	if(method == "2"){
		if(!$.validate.verifyAll("fileInput")) {
			return false;
		}
		
	    var cardlist = $("#import_CARD_LIST");
		var citycode = $("#import_CITY_CODE");
		var staffid = $("#import_STAFF_ID");
		var vcitycode = citycode.val();
		var vstaffid = staffid.val();
		var vcardlist = cardlist.val();
		if(vcardlist == "") {
			alert("状态:请检查卡列表！");
			cardlist.focus();
			return false;
		}
		if(vcitycode == "") {
			alert("状态:请检查业务区！");
			citycode.focus();
			return false;
		}
		if(vstaffid == "") {
			alert("状态:请检查工号！");
			staffid.focus();
			return false;
		}	
	}
	

	if (objTable.rows.length > 0) {

		for ( var i = 1; i < objTable.rows.length; i++) {
			if (vstart >= objTable.rows[i].cells[1].innerHTML
					&& vstart <= objTable.rows[i].cells[2].innerHTML) {
				MessageBox.alert("状态", "输入卡号与已有号段中号码重复！");
				return false;
			}
			if (vend >= objTable.rows[i].cells[1].innerHTML
					&& vend <= objTable.rows[i].cells[2].innerHTML) {
				MessageBox.alert("状态", "输入卡号与已有号段中号码重复！");
				return false;
			}
		}
	}
	
	var listener ="addClick";
	if(method == "2"){
		listener ="importClick";
	}
	var param = "";
	param += "&table2=" + coding(objTable);
	$.beginPageLoading();
	$.ajax.submit('QueryCondPart,AuthPart',listener,param,'BasicInfosPart,SaleInfosPart',function(data)
	{ 
		
		$.cssubmit.disabledSubmitBtn(false);
		
	 /* var info = data.get(0, "alertInfo");  
	  if(info != null) 
	  {
		    MessageBox.alert("状态",info);
	  }*/
	  addFee(data);
	  $.endPageLoading(); //today 费用组件的处理待续，先返回
	  		
	},function(errorcode,errorinfo){
		alert(errorinfo);
		$.endPageLoading();
	}); 
		
}
//加入后处理费用
function addFee(data,str) {
	var fee1 =0 - getTable2Fee();

	if(hk_count==0){
		insertFee("0","20",fee1);
		hk_count=1;
	}else{
		var obj = new Wade.DataMap();
		modFee("0","20",fee1);
	}
	
	var trade = $.feeMgr.getFeeTrade() ;

}
//获取TABLE2中总费用
function getTable2Fee() {
	var objTable = document.getElementById("table2");
	var feeTotal = 0.0;
	for(var i = 1; i < objTable.rows.length; i++) {
		feeTotal = feeTotal + parseFloat(objTable.rows[i].cells[5].innerHTML) * 100;
	}
	return feeTotal;
}
//将TABLE2中数据拼串 已改造为
function coding(objTable) {
	var xCodingStr = "";
	var idata = null;
	var dataset = new Wade.DatasetList();

	for(var i = 1; i < objTable.rows.length; i++) {
		idata = new Wade.DataMap();
		idata.put("cardType", objTable.rows[i].cells[0].innerHTML);
		idata.put("startCardNo", objTable.rows[i].cells[1].innerHTML);
		idata.put("endCardNo", objTable.rows[i].cells[2].innerHTML);
		idata.put("simPrice", objTable.rows[i].cells[3].innerHTML);
		idata.put("singlePrice", objTable.rows[i].cells[4].innerHTML);
		idata.put("totalPrice", objTable.rows[i].cells[5].innerHTML);
		idata.put("rowCount", objTable.rows[i].cells[6].innerHTML);
		idata.put("valueCode", objTable.rows[i].cells[7].innerHTML);
		idata.put("advise_price", objTable.rows[i].cells[8].innerHTML);
		idata.put("activateInfo", new Wade.DataMap(objTable.rows[i].cells[9].innerHTML));
		idata.put("devicePrice", objTable.rows[i].cells[10].innerHTML);
		idata.put("RES_KIND_CODE", objTable.rows[i].cells[11].innerHTML);
		
		dataset.add(idata);
	}
	idata = null;
	xCodingStr = dataset.toString();
	dataset = null;
	return xCodingStr;
}

//增加一笔费用
function insertFee(mode,code,fee){
	
	var feeObj = new Wade.DataMap(); 
	feeObj.put("MODE",mode);
	feeObj.put("CODE",code);
	feeObj.put("FEE",fee);
	feeObj.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());

	$.feeMgr.insertFee(feeObj);
}
//删除一笔费用
function delFee(mode,code,fee){
	var feeObj = new Wade.DataMap(); 
	feeObj.put("MODE",mode);
	feeObj.put("CODE",code);
	feeObj.put("FEE",fee);
	feeObj.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
	$.feeMgr.deleteFee(feeObj);
}
//修改费用
function modFee(mode,code,fee){
	var feeObj = new Wade.DataMap(); 
	feeObj.put("MODE",mode);
	feeObj.put("CODE",code);
	feeObj.put("FEE",fee);
	feeObj.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
	$.feeMgr.modFee(feeObj);
}

function checkBeforeSubmit(){
	
	var queryMethod = $("#DEAL_METHOD").val();
	
	if( queryMethod == "2")
	{
		var objTable1 = document.getElementById('table1');
		var saleCount = objTable1.rows.length -1 ;
		alert("状态:总共返销了" + saleCount + "张卡");
		return true ;
	}
	
	var objTable2 = document.getElementById('table2');
	
	if(objTable2.rows.length < 2) {
		alert("状态:没有可以返销的卡！");
		return false;
	}
	
	var saleCount = 0;	//操作卡数量
	for(var i = 1; i < objTable2.rows.length; i++) {
		
		saleCount = saleCount + Number(objTable2.rows[i].cells[6].innerHTML);
		
	}
	alert("总共返销了" + saleCount + "张卡");
	
	var param = "";
	param += "&X_CODING_STR=" + coding(objTable2);
	
	$.cssubmit.addParam(param);
	
	return true;
}

function deleteRow1(rowIndex, str) {
	$("#IS_DEL").val("true");
	var result = window.confirm("提示信息:确定要删除该条记录？");
	if(result){ 
		if($("#IS_DEL").val()=="true"){
			$("#IS_DEL").val("false");
			deleteRow(rowIndex, str);
		}
	};
}

// 双击TABLE2中某一行进行删除
function deleteRow(rowIndex, str) {
	
    var table2 = document.getElementById("table2");
    var fee2 = getTable2Fee();
    var fee = parseFloat(table2.rows[rowIndex].cells[5].innerHTML) * 100;
    if(fee2==fee){
		hk_count=0
	}
    // 删除记录
	table2.deleteRow(rowIndex);
	if(table2.rows.length < 2) {
    	$("#X_CODING_STR").val("");
   	}
	delFee("0","20",fee);
}

