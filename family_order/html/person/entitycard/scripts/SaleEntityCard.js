function refreshPartAtferAuth(data) {
	
}
// 输入起始卡号的同时，赋值给截止卡号 
function startCardKeyUp() {
	var start = $("#START_CARD_NO");
	var end = $("#END_CARD_NO");
	end.val(start.val());
}
//点击“加入”时作验证 
function addValueCard(str, objTable) {
	
	$.beginPageLoading("正在校验数据");
	
	var start = $("#START_CARD_NO");
	var end = $("#END_CARD_NO");
	var vstart = start.val();
	var vend = end.val();
	
	var discount = $("#DISCOUNT");
	var saleprice = $("#SALEPRICE");
	var radio = $("#baseinfo_radio_b");
	
	if(vstart == "") {
		alert("状态:请检查卡号！");
		start.focus();
		$.endPageLoading();
		return false;
	}
	if(vend == "") {
		alert("状态:请检查卡号！");
		end.focus();
		$.endPageLoading();
		return false;
	}
	

	// 保证在同一号段，即从第一位到倒数第五位相同
	if (vstart.substring(0, vstart.length - 4) != vend.substring(0,
			vstart.length - 4)) {
		alert("状态:起始卡号" + vstart.substring(0, vstart.length - 4) + "和截止卡号"
				+ vend.substring(0, vstart.length - 4) + "必须在同一号段！");
		end.focus();
		$.endPageLoading();
		return false;
	}
	if (vstart > vend) {
		alert("状态:请将数字较大的卡号置于截止卡号！");
		end.focus();
		$.endPageLoading();
		return false;
	}
	// 一次不能查询超过300条数据
	if (Number(vend.substring(vend.length - 4, vend.length))
			- Number(vstart.substring(vstart.length - 4, vstart.length)) > 300) {
		alert("状态:数量量过大将导致超时，请缩小数据段！");
		$.endPageLoading();
		end.focus();
		return false;
	}
	
	if(radio.attr("checked")) {
		if(discount.val() == "" && saleprice.val() == "") {
			alert("状态:请输入【折扣率】或【销售价格】！");
			discount.focus();
			$.endPageLoading();
			return false;
		}
		if(discount.val() != "" && saleprice.val() != "") {
			alert("状态:【折扣率】和【销售价格】不可以同时填写！");
			discount.focus();
			$.endPageLoading();
			return false;
		}
		if(!(discount.val()=="" ? false : checkFloat(discount, "【折扣率】")) 
					&& !(saleprice.val()=="" ? false : checkFloat(saleprice, "【销售价格】"))) {
			$.endPageLoading();
			return false;
		}
		if(parseFloat(discount.val()) > 10 || parseFloat(discount.val()) < 0) {
			alert("状态:【折扣率】输入不正确！");
			$.endPageLoading();
			return false;
		}
	}

	if(objTable.rows.length > 0) {
		for(var i = 1; i < objTable.rows.length; i++) {
				if(vstart >= objTable.rows[i].cells[1].innerHTML 
					&& vstart <= objTable.rows[i].cells[2].innerHTML) {
					alert("状态:输入卡号与已有号段中号码重复！");
					$.endPageLoading();
					return false;
				}
				if(vend >= objTable.rows[i].cells[1].innerHTML 
					&& vend <= objTable.rows[i].cells[2].innerHTML) {
					alert("状态:输入卡号与已有号段中号码重复！");
					$.endPageLoading();
					return false;
				}
			}

		var param ="&X_CODING_STR="+codingTable2(objTable);
	}
	$.ajax.submit('QueryCondPart', 'addClick', param, 'BasicInfosPart,SaleInfosPart',
			function(data){
				afterAdd(data);
				$.cssubmit.disabledSubmitBtn(false);
				$.endPageLoading();
			}, 
			function(error_code,error_info)
			{
				alert(error_info);
				$.endPageLoading();
	});
}
//将TABLE2中数据拼串
function codingTable2(objTable) {

	var idata = null;
	var dataset = new Wade.DatasetList();
	for(var i = 1; i < objTable.rows.length; i++) {
		idata = new $.DataMap();
		idata.put("startCardNo", objTable.rows[i].cells[1].innerHTML);
		idata.put("endCardNo", objTable.rows[i].cells[2].innerHTML);
		idata.put("simPrice", objTable.rows[i].cells[3].innerHTML);
		idata.put("singlePrice", objTable.rows[i].cells[4].innerHTML);
		idata.put("totalPrice", objTable.rows[i].cells[5].innerHTML);
		idata.put("rowCount", objTable.rows[i].cells[6].innerHTML);
		idata.put("valueCode", objTable.rows[i].cells[7].innerHTML);
		idata.put("advise_price", objTable.rows[i].cells[8].innerHTML);
		idata.put("devicePrice", objTable.rows[i].cells[9].innerHTML);
		idata.put("resKindCode", objTable.rows[i].cells[10].innerHTML);
		idata.put("resTypeCode", objTable.rows[i].cells[11].innerHTML);
		idata.put("activeFlag", objTable.rows[i].cells[12].innerHTML);
		idata.put("RES_KIND_CODE", objTable.rows[i].cells[10].innerHTML);
		
		dataset.add(idata);
	}
	idata = null;
	return dataset.toString();

}
//加入后处理费用
function afterAdd(data) {

	var fee1 = getTable2Fee();

	if($.feeMgr.getTotalFee() == 0) {
			
		insertFee("0","22",fee1);
	 } else {
		modFee("0","22",fee1);
	  }
	//$.cssubmit.disabledSubmitBtn(false);
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
//验证浮点型数据 (整数部分一位，小数部分无或一位、两位) 
function checkFloat(obj, desc) {
	var expression = /^\d+(?:\.\d{1,2})?$/;
	if(!expression.exec(obj.val())) {
		alert("状态:"+desc + "格式错误！");
		obj.focus();
		return false;
	}
	return true;
}

function checkRadio(){
	var saleTypeRadio = $("input[name='SALE_TYPE_RADIO']:checked").val();
	if(saleTypeRadio =="1"){
		$("#DISCOUNT_AREA").css("display","none");
		$("#DISCOUNT").val("");
		$("#SALEPRICE").val("");
	}else{
		$("#DISCOUNT_AREA").css("display","");
	}
}
//根据页面radio选项决定是否显示折扣率、销售价格
function checkRadio() {
	var radio = document.getElementsByName("baseinfo_radio"); 
	var discount1 = document.getElementById("DISCOUNT_AREA");
	var CHG_TAG = false;	// 解决：点击已经被选中的radio时触发也会清空列表
	if(radio[1].checked && radio[1].attributes["oldvalue"].nodeValue != "1") {
		radio[1].attributes["oldvalue"].nodeValue;
		discount1.style.display = '';
		radio[0].attributes["oldvalue"].nodeValue = "0";
		radio[1].attributes["oldvalue"].nodeValue = "1";
		CHG_TAG = true;
	} else if(radio[0].checked && radio[0].attributes["oldvalue"].nodeValue != "1") {
		discount1.style.display = 'none'; 
		radio[0].attributes["oldvalue"].nodeValue = "1";
		radio[1].attributes["oldvalue"].nodeValue = "0";
		CHG_TAG = true;
	}
	if(CHG_TAG) {
		cleanTable()
	}
}
function cleanTable(){
	// 禁止提交
	$.cssubmit.disabledSubmitBtn(true);
	// 清空表格
		var table1 = document.getElementById("table1");
		// 有价卡换卡没有table1
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
		// 清除购卡费
		modFee("0","20",0);
}
function activeRow(rowIndex){
	$.beginPageLoading("正在激活所选实体卡");
	if(window.confirm("提示信息:确定激活第" + rowIndex + "行数据吗？")){ 
		
		var table2 = document.getElementById("table2");
		
		var startCardNo = table2.rows[rowIndex].cells[1].innerHTML;
		var endCardNo = table2.rows[rowIndex].cells[2].innerHTML;
		var activeFlag = table2.rows[rowIndex].cells[12].innerHTML;
		var singlePrice = table2.rows[rowIndex].cells[4].innerHTML;
		
		if(activeFlag=="1"){
			alert("实体卡已激活，无需再激活！");
			$.endPageLoading();
			return false;
		}
		
		var param ='&START_CARD_NO='+startCardNo+'&END_CARD_NO='+endCardNo+'&ACTIVE_FLAG='+activeFlag+'&SINGLE_PRICE='+singlePrice;
		
		$.ajax.submit('', 'activeClick', param, '',
				function(data){
					if(data.get("activeFlag") && "1"==data.get("activeFlag")){
						table2.rows[rowIndex].cells[12].innerHTML = "1";
						alert("实体卡激活成功！");
					}else{
						alert("实体卡激活失败！");
					}
					
					$.endPageLoading();
				}, 
				function(error_code,error_info)
				{
					$.endPageLoading();
					$.MessageBox.error(error_code,error_info);
		});

	}
}
function deleteRow1(rowIndex) {
	$("#IS_DEL").val("true");
	if(window.confirm("提示信息:确定要删除该条记录？")){ 
		if($("#IS_DEL").val()=="true"){
			$("#IS_DEL").val("false");
			deleteRow(rowIndex);
		}
	};
}
//双击TABLE2中某一行进行删除
function deleteRow(rowIndex) {

    var table2 = document.getElementById("table2");
    var fee2 = getTable2Fee();
    var fee = parseFloat(table2.rows[rowIndex].cells[5].innerHTML) * 100;

    // 删除记录
	table2.deleteRow(rowIndex);
  	delFee("0","22",fee);
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

function submitCheck(){
	$.beginPageLoading("正在校验数据");
	var tableObj =document.getElementById('table2');
	
	if(tableObj.rows.length < 2) {
		alert("状态:没有可以销售的卡！");
		$.endPageLoading();
		return false;
	}
	var param="&X_CODING_STR="+codingTable2(tableObj);
	
	$.cssubmit.addParam(param);
	var saleCount = 0;	//操作卡数量
	for(var i = 1; i < tableObj.rows.length; i++) {
		if("0" == tableObj.rows[i].cells[12].innerHTML){
			alert("必须先激活后才能销售！");
			$.endPageLoading();
			return false;
		}
		saleCount = saleCount + Number(tableObj.rows[i].cells[6].innerHTML);
	}
	
	
	alert("状态:总共销售了" + saleCount + "张卡");
	return true;
	
}

