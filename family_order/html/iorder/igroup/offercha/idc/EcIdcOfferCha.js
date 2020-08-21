var number = 1;
var table = new Wade.DatasetList();
var orderTable = new Wade.DatasetList();
var OLD_AttrInternet = "";
var OLD_IdcOrderData = "";
function initPageParam_110000007015(){
	var offerTpye = $("#cond_OPER_TYPE").val();
	window["IdcLineTable"] = new Wade.Table("IdcLineTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#IdcLineTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClick(IdcLineTable.getSelectedRowData());
	});
	$.each(table,function(index,data) { 
		IdcLineTable.addRow($.parseJSON(data.toString()));
	});
	OLD_AttrInternet = $("#pam_NOTIN_OLD_AttrInternet").val();
	if($("#pam_NOTIN_OLD_AttrInternet").val()!=""&&table.length==0)
	{
		$.each(new Wade.DatasetList($("#pam_NOTIN_OLD_AttrInternet").val()),function(index,data) { 
			IdcLineTable.addRow($.parseJSON(data.toString()));
		});
	}
	
	$("#IdcLineTable tbody tr").attr("class","");//去掉背景色
	
	
	//初始化IdcOrderTable
	OLD_IdcOrderData = $("#IDC_ORDER_OLD_DATA").val();
	$("#IDC_ORDER_OLD_DATA").val("");
	window["IdcOrderTable"] = new Wade.Table("IdcOrderTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#IdcOrderTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		orderTableRowClick(IdcOrderTable.getSelectedRowData());
	});
	$.each(orderTable,function(index,data) { 
		IdcOrderTable.addRow($.parseJSON(data.toString()));
	});
	if(OLD_IdcOrderData!=""&&orderTable.length==0)
	{
		$.each(new Wade.DatasetList(OLD_IdcOrderData),function(index,data) { 
			IdcOrderTable.addRow($.parseJSON(data.toString()));
		});
	}
	$("#IdcOrderTable tbody tr").attr("class","");//去掉背景色
	
	//REQ201812180018关于本省IDC流量计费产品增加按天生效时间免费月份及其他优化
	$("#pam_EFFECTIVE_DATE").attr("readonly","readonly");
	$("#pam_FREE_MONTH").attr("readonly","readonly");
	$("#pam_FREE_MONTH1").attr("readonly","readonly");
	$("#pam_FREE_MONTH2").attr("readonly","readonly");
	var WAY_TYPE =$("#pam_EFFECTIVE_WAY_TYPE").val();
	if(WAY_TYPE == 0 || WAY_TYPE ==1){
		$("#pam_EFFECTIVE_DATE").attr("disabled","ture");
	}
	
	//解决斜杠问题
	var str = "<li class=\"link required\"> <div class=\"label\">设备端口名称</div><div class=\"value\">"+
				"<input type=\"text\" name=\"IDC_DEVICE_PORT\" id=\"IDC_DEVICE_PORT\"  placeholder=\"请填写\"" +
				" value=\"\" nullable=\"no\" linkclass=\"link required\"" +
				" desc=\"设备端口名称\"></div></li>";
	$("#IdcOrderUl").append(str);
}
function initCrtUs() {
	//alert(123);
}

function createData() {
    //服务器型号
    var serverType = $("#pam_NOTIN_SERVER_TYPE").val();
    //IP地址
    var ip = $("#pam_NOTIN_IP_ADDRESS").val();
    //IP地址类型
    var ipType= $("#pam_TYPE_OF_IP_ADDRESS").val();
    //空间租用费
    var spacePrice = $("#pam_NOTIN_SPACE_PRICE").val();
    //端口通信费
    var portPrice = $("#pam_NOTIN_PORT_PRICE").val();
    //免费IP端口
    var freeIp = $("#pam_NOTIN_IP_PORT").val();
    
    //var freeIpName = $("#pam_NOTIN_IP_PORT option:selected").text();
    var freeIpName = $("#pam_NOTIN_IP_PORT").text();
    
    var maxnumberline = $("#pam_NOTIN_MAX_NUMBER_LINE").val();

    //机柜服务电费（元）
    var cabinetSvcEcIncome = $("#pam_CABINET_SVC_EC_INCOME").val();

    //IDC增值服务费（元）
    var idcValAddedSvcIncome = $("#pam_IDC_VAL_ADDED_SVC_INCOME").val();
    
    var numA = maxnumberline.substring(maxnumberline.length-3);
	var numB = Number(numA) + Number(number);
 
	if(Number(numB) >= 1000){
		//alert("\u5df2\u7ecf\u8d85\u8fc7\u4e13\u7ebf\u5e8f\u5217\u6761\u65701000\u6761");
		$.validate.alerter.one($("#IdcLineTable")[0], "已经超过专线序列条数1000条");
		return false;
	}
	var indexA = "0000" + numB;
	var indexB = indexA.substring(indexA.length-3);
	var lineInstance = maxnumberline.substring(0,maxnumberline.length-3) + indexB;
	
	
	var thisDate = new Date();
    var year = thisDate.getYear().toString();  
    var month = thisDate.getMonth()+1;
    if (month <= 9) {
		month = "0" + month;
	}
	var day = thisDate.getDate();
	if (day <= 9) {
		day = "0" + day;
	}
	var hours = thisDate.getHours();
	if (hours <= 9) {
		hours = "0" + hours;
	}
	var minutes = thisDate.getMinutes();
	if (minutes <= 9) {
		minutes = "0" + minutes;
	}
	var seconds = thisDate.getSeconds();
	if (seconds <= 9) {
		seconds = "0" + seconds;
	}
	
	var today = year + month + day + hours + minutes + seconds;
	
    

    //服务器型号
    if (serverType == ""){
		//alert ("请填写服务器型号！");
    	$.validate.alerter.one($("#pam_NOTIN_SERVER_TYPE")[0], "请填写服务器型号！");
		return false;
	}
	//IP地址
    if (ip == ""){
		//alert ("请填写IP地址！");
    	$.validate.alerter.one($("#pam_NOTIN_IP_ADDRESS")[0], "请填写IP地址！");
		return false;
	}

    //IP地址类型
    if (ipType == "") {
        $.validate.alerter.one($("#pam_TYPE_OF_IP_ADDRESS")[0], "请选择IP地址类型！");
        return false;
    }
	
	if(!$.verifylib.checkIp(ip)){
	   //alert("IP地址格式不正确请重新输入！");
		$.validate.alerter.one($("#pam_NOTIN_IP_ADDRESS")[0], "IP地址格式不正确请重新输入！");
	   return false;
	}
	
	//空间租用费
	if (spacePrice == ""){
		//alert ("请填写空间租用费！");
		$.validate.alerter.one($("#pam_NOTIN_SPACE_PRICE")[0], "请填写空间租用费！");
		return false;
	}else{
		var flag = checkNum(spacePrice);
		if(!flag){
			//alert("空间租用费必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_SPACE_PRICE")[0], "空间租用费必须是数字");
			return false;
		}else if(!checkDecimalPoint(spacePrice))
		{   //只允许精确到小数点后2位
			$.validate.alerter.one($("#pam_NOTIN_SPACE_PRICE")[0], "空间租用费只允许精确到小数点后2位！");
			return false;
		}
	}
	
	//端口通信费
	if (portPrice == ""){
		//alert ("请填写端口通信费！");
		$.validate.alerter.one($("#pam_NOTIN_PORT_PRICE")[0], "请填写端口通信费！");
		return false;
	}else{
		var flag = checkNum(portPrice);
		if(!flag){
			//alert("端口通信费必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_PORT_PRICE")[0], "端口通信费必须是数字");
			return false;
		}else if(!checkDecimalPoint(portPrice))
		{   //只允许精确到小数点后2位
			$.validate.alerter.one($("#pam_NOTIN_PORT_PRICE")[0], "端口通信费只允许精确到小数点后2位！");
			return false;
		}
	}
	//免费IP端口
    if (freeIp == "" || freeIp == null){
		//alert ("请选择免费IP端口！");
		$.validate.alerter.one($("#pam_NOTIN_IP_PORT")[0], "请选择免费IP端口！");
		return false;
	}

    //机柜服务电费（元）
	if(!checkNum(cabinetSvcEcIncome))
	{
		//alert('机柜服务电费必须填入整数！');
		$.validate.alerter.one($("#pam_CABINET_SVC_EC_INCOME")[0], "机柜服务电费必须填入数字！");
		return false;
	}else if(!checkDecimalPoint(cabinetSvcEcIncome))
	{   //只允许精确到小数点后2位
		$.validate.alerter.one($("#pam_CABINET_SVC_EC_INCOME")[0], "机柜服务电费只允许精确到小数点后2位！");
		return false;
	}
    
    //IDC增值服务费（元）
	if(!checkNum(idcValAddedSvcIncome))
	{
		//alert('IDC增值服务费必须填入整数！');
		$.validate.alerter.one($("#pam_IDC_VAL_ADDED_SVC_INCOME")[0], "IDC增值服务费必须填入数字！");
		return false;
	}else if(!checkDecimalPoint(idcValAddedSvcIncome))
	{   //只允许精确到小数点后2位
		$.validate.alerter.one($("#pam_IDC_VAL_ADDED_SVC_INCOME")[0], "IDC增值服务费只允许精确到小数点后2位！");
		return false;
	}

    if (cabinetSvcEcIncome == "" || idcValAddedSvcIncome == ""){
		//alert ("未输入机柜服务电费或IDC增值服务费，默认0（元）！");
    	$.validate.alerter.one($("#pam_IDC_VAL_ADDED_SVC_INCOME")[0], "未输入机柜服务电费或IDC增值服务费，默认0（元）！");

	    if (cabinetSvcEcIncome == ""){
	        $("#pam_CABINET_SVC_EC_INCOME").val(0);
		}

	    if (idcValAddedSvcIncome == ""){
	        $("#pam_IDC_VAL_ADDED_SVC_INCOME").val(0);
		}
	}
	
	$("#pam_NOTIN_OPER_TAG").val(today);
	$("#pam_NOTIN_IP_PORT").val(freeIp);
	$("#pam_NOTIN_IP_PORT_NAME").val(freeIpName);
	$("#pam_NOTIN_LINE_INSTANCENUMBER").val(lineInstance);
	
	var visplineData = $.ajax.buildJsonData("idcLinePart");
	
	//$.table.get("IdcLineTable").addRow(visplineData);
	var data = $.DataMap(visplineData);
	data.put("pam_NOTIN_IP_PORT_NAME",$("#pam_NOTIN_IP_PORT").text());
	
	IdcLineTable.addRow($.parseJSON(data.toString()));
	table.add(data);
	
	$("#pam_NOTIN_SERVER_TYPE").val("");
	$("#pam_NOTIN_IP_ADDRESS").val("");
    $("#pam_TYPE_OF_IP_ADDRESS").val("");
	$("#pam_NOTIN_SPACE_PRICE").val("");
	$("#pam_NOTIN_PORT_PRICE").val("");
	$("#pam_NOTIN_IP_PORT").val("");
	$("#pam_CABINET_SVC_EC_INCOME").val("");
	$("#pam_IDC_VAL_ADDED_SVC_INCOME").val("");
	
    calCabinetSvcEcIncome(cabinetSvcEcIncome);
    calIdcValAddedSvcIncome(idcValAddedSvcIncome);
    calDepositIncome();
    calAccessIncome();
	
    calIncome();
    
	number++;	
	
	$("#IdcLineTable tbody tr").attr("class","");//去掉背景色
}

function deleteData(){
	//$.table.get("IdcLineTable").deleteRow();
	table.remove(IdcLineTable.getData());
	IdcLineTable.deleteRow(IdcLineTable.selected);
	
    calCabinetSvcEcIncome();
    calIdcValAddedSvcIncome();
    
    calDepositIncome();
    calAccessIncome();
	
    calIncome();
}

function updateData() {
	 //服务器型号
    var serverType = $("#pam_NOTIN_SERVER_TYPE").val();
    //IP地址
    var ip = $("#pam_NOTIN_IP_ADDRESS").val();
    //IP地址类型
    var ipType = $("#pam_TYPE_OF_IP_ADDRESS").val();
    //空间租用费
    var spacePrice = $("#pam_NOTIN_SPACE_PRICE").val();
    //端口通信费
    var portPrice = $("#pam_NOTIN_PORT_PRICE").val();
    //免费IP端口
    var freeIp = $("#pam_NOTIN_IP_PORT").val();
    
    //var freeIpName = $("#pam_NOTIN_IP_PORT option:selected").text();
    var freeIpName = $("#pam_NOTIN_IP_PORT").text();
    //专线实例号
    var lineInstance = $("#pam_NOTIN_LINE_INSTANCENUMBER").val();

    //机柜服务电费（元）
    var cabinetSvcEcIncome = $("#pam_CABINET_SVC_EC_INCOME").val();

    //IDC增值服务费（元）
    var idcValAddedSvcIncome = $("#pam_IDC_VAL_ADDED_SVC_INCOME").val();
    

	//服务器型号
    if (serverType == ""){
		//alert ("请填写服务器型号！");
    	$.validate.alerter.one($("#pam_NOTIN_SERVER_TYPE")[0], "请填写服务器型号！");
		return false;
	}
	
	//IP地址
    if (ip == ""){
		//alert ("请填写IP地址！");
    	$.validate.alerter.one($("#pam_NOTIN_IP_ADDRESS")[0], "请填写IP地址！");
		return false;
	}

    //IP地址类型
    if (ipType == "") {
        $.validate.alerter.one($("#pam_TYPE_OF_IP_ADDRESS")[0], "请选择IP地址类型！");
        return false;
    }
    
	if(!$.verifylib.checkIp(ip)){
	   //alert("IP地址格式不正确请重新输入！");
		$.validate.alerter.one($("#pam_NOTIN_IP_ADDRESS")[0], "IP地址格式不正确请重新输入！");
	   return false;
	}
	
	
	//空间租用费
	if (spacePrice == ""){
		//alert ("请填写空间租用费！");
		$.validate.alerter.one($("#pam_NOTIN_SPACE_PRICE")[0], "请填写空间租用费！");
		return false;
	}else{
		var flag = checkNum(spacePrice);
		if(!flag){
			//alert("空间租用费必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_SPACE_PRICE")[0], "空间租用费必须是数字");
			return false;
		}else if(!checkDecimalPoint(spacePrice))
		{
			$.validate.alerter.one($("#pam_NOTIN_SPACE_PRICE")[0], "空间租用费只允许精确到小数点后2位！");
			return false;
		}
	}
	
	//端口通信费
	if (portPrice == ""){
		//alert ("请填写端口通信费！");
		$.validate.alerter.one($("#pam_NOTIN_PORT_PRICE")[0], "请填写端口通信费！");
		return false;
	}else{
		var flag = checkNum(portPrice);
		if(!flag){
			//alert("端口通信费必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_PORT_PRICE")[0], "端口通信费必须是数字");
			return false;
		}else if(!checkDecimalPoint(portPrice))
		{
			$.validate.alerter.one($("#pam_NOTIN_PORT_PRICE")[0], "端口通信费只允许精确到小数点后2位！");
			return false;
		}
	}
	
	//免费IP端口
    if (freeIp == "" || freeIp == null){
		//alert ("请选择免费IP端口！");
    	$.validate.alerter.one($("#pam_NOTIN_IP_PORT")[0], "请选择免费IP端口！");
		return false;
	}
	
	$("#pam_NOTIN_IP_PORT").val(freeIp);
	$("#pam_NOTIN_IP_PORT_NAME").val(freeIpName);
    
    //机柜服务电费（元）
	if(!checkNum(cabinetSvcEcIncome))
	{
		//alert('机柜服务电费必须填入整数！');
		$.validate.alerter.one($("#pam_CABINET_SVC_EC_INCOME")[0], "机柜服务电费必须填入数字！");
		return false;
	}else if(!checkDecimalPoint(cabinetSvcEcIncome))
	{
		$.validate.alerter.one($("#pam_CABINET_SVC_EC_INCOME")[0], "机柜服务电费只允许精确到小数点后2位！");
		return false;
	}
    
    //IDC增值服务费（元）
	if(!checkNum(idcValAddedSvcIncome))
	{
		//alert('IDC增值服务费必须填入整数！');
		$.validate.alerter.one($("#pam_IDC_VAL_ADDED_SVC_INCOME")[0], "IDC增值服务费必须填入数字！");
		return false;
	}else if(!checkDecimalPoint(idcValAddedSvcIncome))
	{   //只允许精确到小数点后2位
		$.validate.alerter.one($("#pam_IDC_VAL_ADDED_SVC_INCOME")[0], "IDC增值服务费只允许精确到小数点后2位！");
		return false;
	}

    if (cabinetSvcEcIncome == "" || idcValAddedSvcIncome == ""){
		//alert ("未输入机柜服务电费或IDC增值服务费，默认0（元）！");
    	$.validate.alerter.one($("#pam_IDC_VAL_ADDED_SVC_INCOME")[0], "未输入机柜服务电费或IDC增值服务费，默认0（元）！");

	    if (cabinetSvcEcIncome == ""){
	        $("#pam_CABINET_SVC_EC_INCOME").val(0);
		}

	    if (idcValAddedSvcIncome == ""){
	        $("#pam_IDC_VAL_ADDED_SVC_INCOME").val(0);
		}
	}
    
    
    
	 
	//获取编辑区的数据
	var visplineData = $.ajax.buildJsonData("idcLinePart");
	
	var checkedData =IdcLineTable.getData().get(0);
	if(checkedData.get("pam_NOTIN_SERVER_TYPE")==visplineData.pam_NOTIN_SERVER_TYPE&&
		checkedData.get("pam_NOTIN_IP_ADDRESS")==visplineData.pam_NOTIN_IP_ADDRESS&&
        checkedData.get("pam_TYPE_OF_IP_ADDRESS")==visplineData.pam_TYPE_OF_IP_ADDRESS&&
		checkedData.get("pam_NOTIN_SPACE_PRICE")==visplineData.pam_NOTIN_SPACE_PRICE&&
		checkedData.get("pam_NOTIN_PORT_PRICE")==visplineData.pam_NOTIN_PORT_PRICE&&
		checkedData.get("pam_NOTIN_IP_PORT")==visplineData.pam_NOTIN_IP_PORT&&
		checkedData.get("pam_CABINET_SVC_EC_INCOME")==visplineData.pam_CABINET_SVC_EC_INCOME&&
		checkedData.get("pam_IDC_VAL_ADDED_SVC_INCOME")==visplineData.pam_IDC_VAL_ADDED_SVC_INCOME
	){
		$.validate.alerter.one($("#pam_NOTIN_SERVER_TYPE")[0], "请修改！");
		return false;
	}
	
	
	
	//往表格里添加一行并将编辑区数据绑定上
	//$.table.get("IdcLineTable").updateRow(visplineData);
	var data = $.DataMap(visplineData);
	data.put("pam_NOTIN_IP_PORT_NAME",$("#pam_NOTIN_IP_PORT").text());
	IdcLineTable.updateRow($.parseJSON(data.toString()),IdcLineTable.selected);
	
	$("#pam_NOTIN_SERVER_TYPE").val("");
	$("#pam_NOTIN_IP_ADDRESS").val("");
	$("#pam_TYPE_OF_IP_ADDRESS").val("");
	$("#pam_NOTIN_SPACE_PRICE").val("");
	$("#pam_NOTIN_PORT_PRICE").val("");
	$("#pam_NOTIN_IP_PORT").val("");
	$("#pam_CABINET_SVC_EC_INCOME").val("");
	$("#pam_IDC_VAL_ADDED_SVC_INCOME").val("");
	
    calCabinetSvcEcIncome();
    calIdcValAddedSvcIncome();
    
    calDepositIncome();
    calAccessIncome();
	
    calIncome();
}

function tableRowClick(data){
	//获取选择行的数据
	 //var rowData = $.table.get("IdcLineTable").getRowData();
	 $("#pam_NOTIN_OPER_TAG").val(data.get("pam_NOTIN_OPER_TAG"));
	 $("#pam_NOTIN_SERVER_TYPE").val(data.get("pam_NOTIN_SERVER_TYPE"));
	 $("#pam_NOTIN_IP_ADDRESS").val(data.get("pam_NOTIN_IP_ADDRESS"));
	 $("#pam_TYPE_OF_IP_ADDRESS").val(data.get("pam_TYPE_OF_IP_ADDRESS"));
	 $("#pam_NOTIN_SPACE_PRICE").val(data.get("pam_NOTIN_SPACE_PRICE"));
	 $("#pam_NOTIN_PORT_PRICE").val(data.get("pam_NOTIN_PORT_PRICE"));
	 $("#pam_NOTIN_IP_PORT").val(data.get("pam_NOTIN_IP_PORT"));
	 $("#pam_NOTIN_LINE_INSTANCENUMBER").val(data.get("pam_NOTIN_LINE_INSTANCENUMBER"));
	 $("#pam_CABINET_SVC_EC_INCOME").val(data.get("pam_CABINET_SVC_EC_INCOME"));
	 $("#pam_IDC_VAL_ADDED_SVC_INCOME").val(data.get("pam_IDC_VAL_ADDED_SVC_INCOME"));
}


function getNewData(){
	var data = $.table.get("IdcLineTable").getTableData(null, true);
	alert("获取全表数据:"+data);
}

function getOldData(){
	var oldPamAttr = $("#pam_NOTIN_OLD_AttrInternet").val();
	alert(oldPamAttr.toString());
}



function validateParamPage(methodName) {
  	//var pamAttr = $.table.get("IdcLineTable").getTableData(null, true);
	var pamAttr = IdcLineTable.getData();
	var oldPamAttr = $("#pam_NOTIN_OLD_AttrInternet").val();
	oldPamAttr = OLD_AttrInternet;
	if(methodName=="CrtUs"){
		if (pamAttr == "" || pamAttr == "[]"){
			//alert ("\u8BF7\u589E\u52A0\u4E13\u7EBF\uFF01");"请增加专线！"
			$.validate.alerter.one($("#pam_NOTIN_SERVER_TYPE")[0], "请增加专线！");
			return false;
		}
	}
	
	var attrList = new Wade.DatasetList();
	var attrList = compareDataset(pamAttr,oldPamAttr);
	
	$("#pam_NOTIN_AttrInternet").val(attrList.toString());
	$("#pam_NOTIN_SERVER_TYPE").attr("nullable", "yes");
	$("#pam_NOTIN_IP_ADDRESS").attr("nullable", "yes");
	$("#pam_NOTIN_SPACE_PRICE").attr("nullable", "yes");
	$("#pam_NOTIN_PORT_PRICE").attr("nullable", "yes");
	$("#pam_NOTIN_IP_PORT").attr("nullable", "yes");
	$("#pam_CABINET_SVC_EC_INCOME").attr("nullable", "yes");
	$("#pam_IDC_VAL_ADDED_SVC_INCOME").attr("nullable", "yes");
	$("#pam_TYPE_OF_IP_ADDRESS").attr("nullable", "yes");
	
    
    //---add by chenzg@20180621--begin--REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求----
	var pamOrder = IdcOrderTable.getData();
	var str1 = $("input:hidden[name=pp_PRICE_PLAN_ID][value=130084012041]").val();
	var str2 = $("input:hidden[name=pp_PRICE_PLAN_ID][value=130084012040]").val();
	if (str1 == "130084012041" && str2 =="130084012040"){
		alert("IDC峰值计费套餐、IDC95法则计费套餐。2个套餐互斥，只能办理其中一个");
		return false;
	}else{
		if(str1 == "130084012041" || str2 =="130084012040"){
			//判断是否新订购指定套餐,新订购套餐支持修改失效日期
			var state = false;
			var strText1= $("span[id^='expire_"+str1+"']").next().text();
			var strText2= $("span[id^='expire_"+str2+"']").next().text();
			if(strText1 == "[修改]" || strText2 == "[修改]"){
				state = true;
			}
			
		    if ((pamOrder == "" || pamOrder == "[]") && state){
				$.validate.alerter.one($("#IDC_DEVICE_NAME")[0], "您订购了IDC相关套餐，请填写产品参数中的订购信息[设备名称,设备IP,设备端口名称等相关信息]！");
				return false;
			}
		}else{
			if (!(pamOrder == "" || pamOrder == "[]")){
				alert("您未订购或退订IDC相关套餐，请全部删除产品参数中的订购信息表格记录[设备名称,设备IP,设备端口名称等相关信息]！");
				return false;
			}
		}
	}
	var oldOrderList = OLD_IdcOrderData;
	var orderList = new Wade.DatasetList();
	orderList = compareOrderDataset(pamOrder,oldOrderList);
	$("#pam_IDC_ORDER_DATA").val(orderList.toString());
	
	
	$("#IDC_DEVICE_NAME").attr("nullable", "yes");
    $("#IDC_DEVICE_IP").attr("nullable", "yes");
    $("#IDC_DEVICE_PORT").attr("nullable", "yes");
    //REQ201812180018关于本省IDC流量计费产品增加按天生效时间免费月份及其他优化
    $("#pam_EFFECTIVE_WAY_TYPE").attr("nullable", "yes");
    $("#pam_EFFECTIVE_DATE").attr("nullable", "yes");
    $("#pam_FREE_MONTH").attr("nullable", "yes");
    $("#pam_FREE_MONTH1").attr("nullable", "yes");
    $("#pam_FREE_MONTH2").attr("nullable", "yes");
    //---add by chenzg@20180621--end----REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求----
    
    
    return true;
}
function compareDataset(strNewValue,strOldValue){
	if (strOldValue == ""){
        strOldValue="[]";
	}
    if (strNewValue == ""){
        strNewValue == "[]";
    }
    
    var newValueSet = strNewValue;
    var oldValueSet = new Wade.DatasetList(strOldValue);
    var resultSet = new Wade.DatasetList();
    
    
    for (var i=0;i<newValueSet.length;i++){
    	var isfound = "false";
    	var newValueColumn = newValueSet.get(i);
	    for (var j=0;j<oldValueSet.length;j++) {
	        var oldValueColumn = oldValueSet.get(j);
	        if(oldValueColumn.get('pam_NOTIN_OPER_TAG') == newValueColumn.get('pam_NOTIN_OPER_TAG')){
	        	/*if(newValueColumn.get('tag') == "2"){
	        		var isfound = "true";
					resultSet.add(newValueColumn);	        	
	        	}*/
	        	if(oldValueColumn.get('pam_NOTIN_SERVER_TYPE') == newValueColumn.get('pam_NOTIN_SERVER_TYPE')&&
        			oldValueColumn.get('pam_NOTIN_IP_ADDRESS') == newValueColumn.get('pam_NOTIN_IP_ADDRESS')&&
                    oldValueColumn.get('pam_TYPE_OF_IP_ADDRESS') == newValueColumn.get('pam_TYPE_OF_IP_ADDRESS')&&
        			oldValueColumn.get('pam_NOTIN_SPACE_PRICE') == newValueColumn.get('pam_NOTIN_SPACE_PRICE')&&
        			oldValueColumn.get('pam_NOTIN_PORT_PRICE') == newValueColumn.get('pam_NOTIN_PORT_PRICE')&&
        			oldValueColumn.get('pam_NOTIN_IP_PORT') == newValueColumn.get('pam_NOTIN_IP_PORT')&&
        			oldValueColumn.get('pam_CABINET_SVC_EC_INCOME') == newValueColumn.get('pam_CABINET_SVC_EC_INCOME')&&
        			oldValueColumn.get('pam_IDC_VAL_ADDED_SVC_INCOME') == newValueColumn.get('pam_IDC_VAL_ADDED_SVC_INCOME')){
	        		
	        		newValueColumn.put("tag","");
	        	}else{
	        		newValueColumn.put("tag","2");
	        	}
	        	var isfound = "true";
				resultSet.add(newValueColumn);
	        }
	    }
	    if(isfound == "false"){
       		resultSet.add(newValueColumn);
	    }
	}
	
	
	for (var i=0;i<oldValueSet.length;i++) {
        var oldValueColumn = oldValueSet.get(i);
        var isfound = "false";
        for (var j=0;j<newValueSet.length;j++) {
            var newValueColumn = newValueSet.get(j);
            if(oldValueColumn.get('pam_NOTIN_OPER_TAG') == newValueColumn.get('pam_NOTIN_OPER_TAG'))
            {
            	isfound = "true";
            	break;
            }
        }
        if (isfound == "false") {
            oldValueColumn.put("tag","1");
            resultSet.add(oldValueColumn);
        }
   }
   
   return resultSet.toString();
}

function calIncome(){
//	var depositIncome = parseInt($("#pam_N_DEPOSIT_INCOME").val());
//	var accessIncome = parseInt($("#pam_N_ACCESS_INCOME").val());
//	var otherIncome = parseInt($("#pam_N_OTHER_INCOME").val());
	
	
	
	var depositIncome = $("#pam_N_DEPOSIT_INCOME").val();
	var accessIncome = $("#pam_N_ACCESS_INCOME").val();
	var otherIncome = $("#pam_N_OTHER_INCOME").val();
	var nCabinetSvcEcIncome = $("#pam_N_CABINET_SVC_EC_INCOME").val();
	var nIdcValAddedSvcIncome = $("#pam_N_IDC_VAL_ADDED_SVC_INCOME").val();
	
//	var desc;
//	alert($.verifylib.checkInteger(depositIncome)+'---'+desc+'---'+!$.verifylib.checkInteger(depositIncome,desc));
	/*if(!$.verifylib.checkInteger(depositIncome))
	{
		//alert('主机托管总收入必须填入整数！');
		$.validate.alerter.one($("#pam_N_DEPOSIT_INCOME")[0], "主机托管总收入必须填入整数！");
		return false;
	}
	if(!$.verifylib.checkInteger(accessIncome))
	{
		//alert('互联网接入总收入必须填入整数！');
		$.validate.alerter.one($("#pam_N_ACCESS_INCOME")[0], "互联网接入总收入必须填入整数！");
		return false;
	}*/
	


	
	if(depositIncome == '' || isNaN(depositIncome))
	{
//		$("#pam_N_DEPOSIT_INCOME").val(0);
		depositIncome = parseFloat(0);
	}
//	else if( !$.isNumber(depositIncome))
//	{
//		alert("主机托管总收入必须填入整数！");
//		$("#pam_N_DEPOSIT_INCOME").focus()
//		return false;
//	}
	
	if(accessIncome == ''|| isNaN(accessIncome))
	{
//		$("#pam_N_ACCESS_INCOME").val(0);
		accessIncome = parseFloat(0);
	}
//	else if( !$.isNumber(accessIncome))
//	{
//		alert("互联网接入总收入必须填入数字！");
//		$("#pam_N_ACCESS_INCOM").focus()
//		return false;
//	}
	
	if(otherIncome == ''|| otherIncome == null )
	{
//		$("#pam_N_OTHER_INCOME").val(0);
		otherIncome = parseFloat(0);
	}else if(!checkNum(otherIncome))
	{
		//alert('其它总收入必须填入整数！');
		$.validate.alerter.one($("#pam_N_OTHER_INCOME")[0], "其它总收入必须填入数字！");
		return false;
	}else if(!checkDecimalPoint(otherIncome))
	{   //只允许精确到小数点后2位
		$.validate.alerter.one($("#pam_N_OTHER_INCOME")[0], "其它总收入只允许精确到小数点后2位！");
		return false;
	}
//	else if( !$.isNumber(otherIncome))
//	{
//		alert("其它总收入必须填入数字！");
//		$("#pam_N_OTHER_INCOME").focus()
//		return false;
//	}
	
	if(nCabinetSvcEcIncome == ''|| isNaN(nCabinetSvcEcIncome))
	{
		nCabinetSvcEcIncome = parseFloat(0);
	}
	
	if(nIdcValAddedSvcIncome == ''|| isNaN(nIdcValAddedSvcIncome))
	{
		nIdcValAddedSvcIncome = parseFloat(0);
	}
	
	
	var pamNIDCINCOME = parseFloat(depositIncome)+parseFloat(accessIncome)+parseFloat(otherIncome)+parseFloat(nCabinetSvcEcIncome)+parseFloat(nIdcValAddedSvcIncome)


	$("#pam_N_IDC_INCOME").val($.format.number(pamNIDCINCOME,"0.00")); // TODO 机柜服务总电费 和 IDC总增值服务费 是否计入 IDC总收入 
}

function calSize(obj){
	
	if(!checkPInteger(obj))
		return false;
	var size = $("#pam_N_DEVICE_SIZE").val();
	if(size != null && size != '')
	{
		val= $.format.number(size/42,"0.0000");
		$("#pam_N_DEPOSIT_SUM").val(val);
	}
	
}

//主机托管机架个数为集团客户托管主机尺寸/42
function changeSumForSize(obj){
	
	if(!checkPInteger(obj))
		return false;
	var size = $("#pam_N_DEVICE_SIZE").val();
	if(size != null && size != '')
	{
		val= $.format.number(size/42,"0");
		$("#pam_N_DEPOSIT_SUM").val(val);
	}
	
}

function checkPInteger(object){
	if(!$.verifylib.checkPInteger($(object).val())|| object.value=='0')
	{
		//alert($(object).attr('desc')+'必须为正整数！');
		$.validate.alerter.one($(object)[0], $(object).attr('desc')+'必须为正整数！');
		$(object).focus();
		return false;
	}
	else
		return true;
}

// 机柜服务总电费（元）计算
function calCabinetSvcEcIncome(){
	var cabinetSvcEcIncomeTotal = 0;
	//var idcLineTableData = $.table.get("IdcLineTable").getTableData(null,true);
	var idcLineTableData = IdcLineTable.getData(true);
	
	for ( var i = 0; i < idcLineTableData.length; i++) {
		var cabinetSvcEcIncome = 0;
		var idcLineColumnValue = idcLineTableData.get(i);

		cabinetSvcEcIncome = idcLineColumnValue.get("pam_CABINET_SVC_EC_INCOME");
		
		if ("1" == idcLineColumnValue.get("tag")) { // tag = 1 - 删除
			cabinetSvcEcIncome = 0;
		}

		cabinetSvcEcIncomeTotal = parseFloat(cabinetSvcEcIncomeTotal) + parseFloat(cabinetSvcEcIncome);
	}


	$("#pam_N_CABINET_SVC_EC_INCOME").val($.format.number(cabinetSvcEcIncomeTotal,"0.00"));
}

//IDC总增值服务费（元）计算
function calIdcValAddedSvcIncome(){

	var idcValAddedSvcIncomeTotal = 0;
	//var idcLineTableData = $.table.get("IdcLineTable").getTableData(null,true);
	var idcLineTableData = IdcLineTable.getData(true);
	
	for ( var i = 0; i < idcLineTableData.length; i++) {
		var idcValAddedSvcIncome = 0;
		var idcLineColumnValue = idcLineTableData.get(i);
		
		idcValAddedSvcIncome = idcLineColumnValue.get("pam_IDC_VAL_ADDED_SVC_INCOME");
		
		if ("1" == idcLineColumnValue.get("tag")) { // tag = 1 - 删除
			idcValAddedSvcIncome = 0;
		}

		idcValAddedSvcIncomeTotal = parseFloat(idcValAddedSvcIncomeTotal) + parseFloat(idcValAddedSvcIncome);
	}


	$("#pam_N_IDC_VAL_ADDED_SVC_INCOME").val($.format.number(idcValAddedSvcIncomeTotal,"0.00"));
}

//主机托管总收入（元）计算
function calDepositIncome(){
	var calDepositIncomeTotal = 0;
	//var idcLineTableData = $.table.get("IdcLineTable").getTableData(null,true);
	var idcLineTableData = IdcLineTable.getData(true);
	
	for ( var i = 0; i < idcLineTableData.length; i++) {
		var calDepositIncome = 0;
		var idcLineColumnValue = idcLineTableData.get(i);

		calDepositIncome = idcLineColumnValue.get("pam_NOTIN_SPACE_PRICE");
		
		if ("1" == idcLineColumnValue.get("tag")) { // tag = 1 - 删除
			calDepositIncome = 0;
		}

		calDepositIncomeTotal = parseFloat(calDepositIncomeTotal) + parseFloat(calDepositIncome);
	}


	$("#pam_N_DEPOSIT_INCOME").val($.format.number(calDepositIncomeTotal,"0.00"));
}

//互联网接入总收入（元）计算
function calAccessIncome(){
	var calAccessIncomeTotal = 0;
	//var idcLineTableData = $.table.get("IdcLineTable").getTableData(null,true);
	
	var idcLineTableData = IdcLineTable.getData(true);
	
	for ( var i = 0; i < idcLineTableData.length; i++) {
		var calAccessIncome = 0;
		var idcLineColumnValue = idcLineTableData.get(i);

		calAccessIncome = idcLineColumnValue.get("pam_NOTIN_PORT_PRICE");
		
		if ("1" == idcLineColumnValue.get("tag")) { // tag = 1 - 删除
			calAccessIncome = 0;
		}

		calAccessIncomeTotal = parseFloat(calAccessIncomeTotal)+parseFloat(calAccessIncome);
	
	}
	   

	$("#pam_N_ACCESS_INCOME").val($.format.number(calAccessIncomeTotal,"0.00"));
}

//提交
function checkSub(obj)
{
	var offerTpye = $("#cond_OPER_TYPE").val();
	if(!validateParamPage(offerTpye))
		return false; 
	if(!submitOfferCha())
		return false; 
	table = null;
	table = IdcLineTable.getData(true);
	orderTable = null;
	orderTable = IdcOrderTable.getData(true);
	backPopup(obj);
	$("#IDC_ORDER_OLD_DATA").val("");
	$("#pam_IDC_ORDER_DATA").val("");
}


//生效方式下拉框
function changeWay1()
{
  var date = new Date();
  var time = '';
  date.setTime(date.getTime());
  var effectiveWay = $("#pam_EFFECTIVE_WAY_TYPE").val();
  if(effectiveWay=='0')
  {	
	  //如果是最后一天不能选择次月
	  date.setTime(date.getTime()+24*60*60*1000);
	  if(date.getDate()=='1'){
		  $("#pam_EFFECTIVE_WAY_TYPE").val("");
		  $("#pam_EFFECTIVE_DATE").val("");
		  alert("月末最后一天不能选择次月生效");
		  return;
	  }
	  date.setTime(date.getTime()-24*60*60*1000);
	  if(date.getMonth()+2 > '12'){
		  time = (date.getFullYear()+1)+"-01-01";
	  }else{
		  time = date.getFullYear()+"-" + PrefixInteger((date.getMonth()+2),2) + "-01";
	  }
	  $("#pam_EFFECTIVE_DATE").attr("disabled","ture");
	  $("#pam_EFFECTIVE_DATE").val(time+" 00:00:00");
  }else if(effectiveWay=='1'){
	  date.setTime(date.getTime()+24*60*60*1000);
	  time = date.getFullYear()+"-" + PrefixInteger((date.getMonth()+1),2) + "-" + PrefixInteger(date.getDate(),2);
	  $("#pam_EFFECTIVE_DATE").attr("disabled","ture");
	  $("#pam_EFFECTIVE_DATE").val(time+" 00:00:00");
  }else if(effectiveWay=='2'){
	  $("#pam_EFFECTIVE_DATE").attr("disabled","");
	  $("#pam_EFFECTIVE_DATE").val("");
  }
}

function changeMonth(){
	var freeMonth = $("#pam_FREE_MONTH").val();
	var date = new Date();
	date.setTime(date.getTime());
	var month = date.getFullYear() + "-" + PrefixInteger((date.getMonth()+1),2);
	if(freeMonth == month){
		$("#pam_FREE_MONTH").val("");
		alert("免费月份不能选择当前月。");
	}
	
	//免费月份只能选择当前日期往后延期一年，且不可重复；
	var arrdate = freeMonth.split("-");
	var dataA = new Date(arrdate[0],arrdate[1],"01");
	var dataB = new Date(date.getFullYear()+1,PrefixInteger((date.getMonth()+1),2),"01");
	var dataC = new Date(date.getFullYear(),PrefixInteger((date.getMonth()+1),2),"01");
	if(dataA.getTime()>dataB.getTime() || dataA.getTime()<=dataC.getTime()){
		$("#pam_FREE_MONTH").val("");
		alert("免费月份只能选择当前日期往后延期一年内。");
	}
	var freeMonth1 = $("#pam_FREE_MONTH1").val();
	var freeMonth2 = $("#pam_FREE_MONTH2").val();
	if(freeMonth1!="" && freeMonth1!="undefined" && freeMonth==freeMonth1){
		$("#pam_FREE_MONTH").val("");
		alert("免费月份不可重复选择。");
	}else if(freeMonth2!="" && freeMonth2!="undefined" && freeMonth==freeMonth2){
		$("#pam_FREE_MONTH").val("");
		alert("免费月份不可重复选择。");
	}
}

function changeMonth1(){
	var freeMonth = $("#pam_FREE_MONTH1").val();
	var date = new Date();
	date.setTime(date.getTime());
	var month = date.getFullYear() + "-" + PrefixInteger((date.getMonth()+1),2);
	if(freeMonth == month){
		$("#pam_FREE_MONTH1").val("");
		alert("免费月份不能选择当前月。");
	}
	
	//免费月份只能选择当前日期往后延期一年，且不可重复；
	var arrdate = freeMonth.split("-");
	var dataA = new Date(arrdate[0],arrdate[1],"01");
	var dataB = new Date(date.getFullYear()+1,PrefixInteger((date.getMonth()+1),2),"01");
	var dataC = new Date(date.getFullYear(),PrefixInteger((date.getMonth()+1),2),"01");
	if(dataA.getTime()>dataB.getTime() || dataA.getTime()<=dataC.getTime()){
		$("#pam_FREE_MONTH1").val("");
		alert("免费月份只能选择当前日期往后延期一年内。");
	}
	var freeMonth1 = $("#pam_FREE_MONTH").val();
	var freeMonth2 = $("#pam_FREE_MONTH2").val();
	if(freeMonth1!="" && freeMonth1!="undefined" && freeMonth==freeMonth1){
		$("#pam_FREE_MONTH1").val("");
		alert("免费月份不可重复选择。");
	}else if(freeMonth2!="" && freeMonth2!="undefined" && freeMonth==freeMonth2){
		$("#pam_FREE_MONTH1").val("");
		alert("免费月份不可重复选择。");
	}
}

function changeMonth2(){
	var freeMonth = $("#pam_FREE_MONTH2").val();
	var date = new Date();
	date.setTime(date.getTime());
	var month = date.getFullYear() + "-" + PrefixInteger((date.getMonth()+1),2);
	if(freeMonth == month){
		$("#pam_FREE_MONTH2").val("");
		alert("免费月份不能选择当前月。");
	}
	
	//免费月份只能选择当前日期往后延期一年，且不可重复；
	var arrdate = freeMonth.split("-");
	var dataA = new Date(arrdate[0],arrdate[1],"01");
	var dataB = new Date(date.getFullYear()+1,PrefixInteger((date.getMonth()+1),2),"01");
	var dataC = new Date(date.getFullYear(),PrefixInteger((date.getMonth()+1),2),"01");
	if(dataA.getTime()>dataB.getTime() || dataA.getTime()<=dataC.getTime()){
		$("#pam_FREE_MONTH2").val("");
		alert("免费月份只能选择当前日期往后延期一年内。");
	}
	var freeMonth1 = $("#pam_FREE_MONTH1").val();
	var freeMonth2 = $("#pam_FREE_MONTH").val();
	if(freeMonth1!="" && freeMonth1!="undefined" && freeMonth==freeMonth1){
		$("#pam_FREE_MONTH2").val("");
		alert("免费月份不可重复选择。");
	}else if(freeMonth2!="" && freeMonth2!="undefined" && freeMonth==freeMonth2){
		$("#pam_FREE_MONTH2").val("");
		alert("免费月份不可重复选择。");
	}
}

function getZero(){
	var date = $("#pam_EFFECTIVE_DATE").val();
	if(date.length > 11){
		$("#pam_EFFECTIVE_DATE").val(date.substring(0,11) + "00:00:00");
		//指定生效日期只能选择在当前日期之后
		var sysdate = new Date();
		sysdate.setTime(sysdate.getTime());
		var dateA = new Date(date.substring(0,4),date.substring(5,7),date.substring(8,10));
		var dataB = new Date(sysdate.getFullYear(),PrefixInteger((sysdate.getMonth()+1),2),PrefixInteger(sysdate.getDate(),2));
		if(dataB.getTime()>=dateA.getTime()){
			$("#pam_EFFECTIVE_DATE").val("");
			alert("指定生效日期只能选择在当前日期之后。");
		}
	}
}

function PrefixInteger(num, length) {
	return (Array(length).join('0') + num).slice(-length);
}

/**
 * 订购信息表格行数据点击处理
 * @return
 * add by chenhh6@20190221 REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求
 */
function orderTableRowClick(rowData){
	//获取选择行的数据
	 $("#IDC_DEVICE_NAME").val(rowData.get("IDC_DEVICE_NAME"));
	 $("#IDC_DEVICE_IP").val(rowData.get("IDC_DEVICE_IP"));
	 $("#IDC_DEVICE_PORT").val(rowData.get("IDC_DEVICE_PORT"));
	 $("#IDC_DEVICE_INSTID").val(rowData.get("IDC_DEVICE_INSTID"));
	 $("#pam_EFFECTIVE_WAY_TYPE").val(rowData.get("pam_EFFECTIVE_WAY_TYPE"));
	 $("#pam_EFFECTIVE_DATE").val(rowData.get("pam_EFFECTIVE_DATE"));
	 $("#pam_FREE_MONTH").val(rowData.get("pam_FREE_MONTH"));
	 $("#pam_FREE_MONTH1").val(rowData.get("pam_FREE_MONTH1"));
	 $("#pam_FREE_MONTH2").val(rowData.get("pam_FREE_MONTH2"));
}
/**
 * 新增订购信息行数据
 * @return
 * add by chenhh6@20190221 REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求
 */
function createOrderData() {
    var deviceName = $("#IDC_DEVICE_NAME").val(); 	//设备名称
    var deviceIp = $("#IDC_DEVICE_IP").val();		//IP地址
    var devicePort = $("#IDC_DEVICE_PORT").val();	//端口
    var wayType= $("#pam_EFFECTIVE_WAY_TYPE").val();//生效方式
    var effectiveDate= $("#pam_EFFECTIVE_DATE").val();//生效时间

    //设备名称
    if (deviceName == ""){
    	$.validate.alerter.one($("#IDC_DEVICE_NAME")[0], "请填写设备名称！");
		return false;
	}
	//IP地址
    if (deviceIp == ""){
    	$.validate.alerter.one($("#IDC_DEVICE_IP")[0], "请填写设备IP地址！");
		return false;
	}
	if(!$.verifylib.checkIp(deviceIp)){
	   $.validate.alerter.one($("#IDC_DEVICE_IP")[0], "设备IP地址格式不正确请重新输入！");
	   return false;
	}
	
	//设备端口名称
    if (devicePort == "" || devicePort == null){
    	$.validate.alerter.one($("#IDC_DEVICE_PORT")[0], "请填写设备端口名称！");
		return false;
	}
    
    //生效方式
    if (wayType == "") {
        $.validate.alerter.one($("#pam_EFFECTIVE_WAY_TYPE")[0], "请选择生效方式！");
        return false;
    }
    //指定生效时间
    if (effectiveDate == "") {
    	$.validate.alerter.one($("#pam_EFFECTIVE_DATE")[0], "请选择生效时间！");
    	return false;
    }
	
	var orderData = $.ajax.buildJsonData("IdcOrderPart");
	
	var data = $.DataMap(orderData);
	IdcOrderTable.addRow($.parseJSON(data.toString()));
	orderTable.add(data);
	$("#IdcOrderTable tbody tr").attr("class","");//去掉背景色
	
	$("#IDC_DEVICE_NAME").val('');
	$("#IDC_DEVICE_IP").val('');
	$("#IDC_DEVICE_PORT").val('');
	$("#IDC_DEVICE_INSTID").val('');
	$("#pam_EFFECTIVE_WAY_TYPE").val("");
    $("#pam_EFFECTIVE_DATE").val("");
    $("#pam_FREE_MONTH").val("");
    $("#pam_FREE_MONTH1").val("");
    $("#pam_FREE_MONTH2").val("");
	
}

/**
 * 删除订购信息表格行数据
 * @return
 * add by chenhh6@20190221 REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求
 */
function deleteOrderData(){
	orderTable.remove(IdcOrderTable.getData());
	IdcOrderTable.deleteRow(IdcOrderTable.selected);
	$("#addBtnOrder").attr("disabled","ture");
	alert("由于删除掉相关设备信息，在本工单完工前将禁止新增设备信息。");
}

/**
 * 修改订购信息表格行数据
 * @return
 * add by chenzg@20180621 REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求
 */
function updateOrderData() {
	var deviceName = $("#IDC_DEVICE_NAME").val(); 	//设备名称
    var deviceIp = $("#IDC_DEVICE_IP").val();		//IP地址
    var devicePort = $("#IDC_DEVICE_PORT").val();	//端口
    var wayType= $("#pam_EFFECTIVE_WAY_TYPE").val();//生效方式
    var effectiveDate= $("#pam_EFFECTIVE_DATE").val();//生效时间

  //设备名称
    if (deviceName == ""){
    	$.validate.alerter.one($("#IDC_DEVICE_NAME")[0], "请填写设备名称！");
		return false;
	}
	//IP地址
    if (deviceIp == ""){
    	$.validate.alerter.one($("#IDC_DEVICE_IP")[0], "请填写设备IP地址！");
		return false;
	}
	if(!$.verifylib.checkIp(deviceIp)){
	   $.validate.alerter.one($("#IDC_DEVICE_IP")[0], "设备IP地址格式不正确请重新输入！");
	   return false;
	}
	
	//设备端口名称
    if (devicePort == "" || devicePort == null){
    	$.validate.alerter.one($("#IDC_DEVICE_PORT")[0], "请填写设备端口名称！");
		return false;
	}
    
    //生效方式
    if (wayType == "") {
        $.validate.alerter.one($("#pam_EFFECTIVE_WAY_TYPE")[0], "请选择生效方式！");
        return false;
    }
    //指定生效时间
    if (effectiveDate == "") {
    	$.validate.alerter.one($("#pam_EFFECTIVE_DATE")[0], "请选择生效时间！");
    	return false;
    }
    
    var orderData = $.ajax.buildJsonData("IdcOrderPart");
	
    //不允许修改设备和端口信息
	var checkedData =IdcOrderTable.getData().get(IdcOrderTable.selected);
	if(checkedData.get("IDC_DEVICE_NAME")!=orderData.IDC_DEVICE_NAME){
		$.validate.alerter.one($("#IDC_DEVICE_NAME")[0], "不允许修改设备信息！");
		return false;
	}
	if(checkedData.get("IDC_DEVICE_IP")!=orderData.IDC_DEVICE_IP){
		$.validate.alerter.one($("#IDC_DEVICE_IP")[0], "不允许修改IP信息！");
		return false;
	}
	if(checkedData.get("IDC_DEVICE_PORT")!=orderData.IDC_DEVICE_PORT){
		$.validate.alerter.one($("#IDC_DEVICE_PORT")[0], "不允许修改端口信息！");
		return false;
	}
    
	var data = $.DataMap(orderData);
	IdcOrderTable.updateRow($.parseJSON(data.toString()),IdcOrderTable.selected);
	$("#IdcOrderTable tbody tr").attr("class","");//去掉背景色
	
	$("#IDC_DEVICE_NAME").val('');
	$("#IDC_DEVICE_IP").val('');
	$("#IDC_DEVICE_PORT").val('');
	$("#IDC_DEVICE_INSTID").val('');
	$("#pam_EFFECTIVE_WAY_TYPE").val("");
    $("#pam_EFFECTIVE_DATE").val("");
    $("#pam_FREE_MONTH").val("");
    $("#pam_FREE_MONTH1").val("");
    $("#pam_FREE_MONTH2").val("");
    
}

/**
 * 处理订购信息
 * @param newdate
 * @param olddate
 */
function compareOrderDataset(strNewValue,strOldValue){
	if (strOldValue == ""){
        strOldValue="[]";
	}
    if (strNewValue == ""){
        strNewValue == "[]";
    }
    var newValueSet = strNewValue;
    var oldValueSet = new Wade.DatasetList(strOldValue);
    var resultSet = new Wade.DatasetList();
    
    for (var i=0;i<newValueSet.length;i++){
    	var isfound = "false";
    	var newValueColumn = newValueSet.get(i);
	    for (var j=0;j<oldValueSet.length;j++) {
	        var oldValueColumn = oldValueSet.get(j);
	        if(oldValueColumn.get('IDC_DEVICE_INSTID') == newValueColumn.get('IDC_DEVICE_INSTID')){
	        	//解决tag=0
	        	if(oldValueColumn.get('IDC_DEVICE_NAME') == newValueColumn.get('IDC_DEVICE_NAME')&&
	        			oldValueColumn.get('IDC_DEVICE_IP') == newValueColumn.get('IDC_DEVICE_IP')&&
	        			oldValueColumn.get('IDC_DEVICE_PORT') == newValueColumn.get('IDC_DEVICE_PORT')&&
	        			oldValueColumn.get('pam_EFFECTIVE_WAY_TYPE') == newValueColumn.get('pam_EFFECTIVE_WAY_TYPE')&&
	        			oldValueColumn.get('pam_EFFECTIVE_DATE') == newValueColumn.get('pam_EFFECTIVE_DATE')&&
	        			oldValueColumn.get('pam_FREE_MONTH') == newValueColumn.get('pam_FREE_MONTH')&&
	        			oldValueColumn.get('pam_FREE_MONTH1') == newValueColumn.get('pam_FREE_MONTH1')&&
	        			oldValueColumn.get('pam_FREE_MONTH2') == newValueColumn.get('pam_FREE_MONTH2')
	        		){
	        		isfound = "true";
	        	}else{
	        		newValueColumn.put("tag","2");
	        	}
	        }
	    }
	    if(isfound == "false"){
       		resultSet.add(newValueColumn);
	    }
	}
	
	
	for (var i=0;i<oldValueSet.length;i++) {
        var oldValueColumn = oldValueSet.get(i);
        isfound = "false";
        for (var j=0;j<newValueSet.length;j++) {
            var newValueColumn = newValueSet.get(j);
            if(oldValueColumn.get('IDC_DEVICE_INSTID') == newValueColumn.get('IDC_DEVICE_INSTID'))
            {
            	isfound = "true";
            	break;
            }
        }
        if (isfound == "false") {
            oldValueColumn.put("tag","1");
            resultSet.add(oldValueColumn);
        }
   }
   return resultSet.toString();
}

//校验小数点只能保留后两位
function checkDecimalPoint(num){
	var point = /^\d+(\.\d{1,2})?$/;
	if(!point.test(num)){
		return false;
	}else{
		return true;
	}
}

function checkNum(num){
	var reg = /^[0-9]+.?[0-9]*$/;
	if(!reg.test(num)){
		return false;
	}else{
		return true;
	}
}