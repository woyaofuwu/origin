var number = 1;
function initCrtUs() {
	//alert(123);
}

function createData() {
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
    
    var freeIpName = $("#pam_NOTIN_IP_PORT option:selected").text();
    
    
    var maxnumberline = $("#pam_NOTIN_MAX_NUMBER_LINE").val();

    //机柜服务电费（元）
    var cabinetSvcEcIncome = $("#pam_CABINET_SVC_EC_INCOME").val();

    //IDC增值服务费（元）
    var idcValAddedSvcIncome = $("#pam_IDC_VAL_ADDED_SVC_INCOME").val();
    
    var numA = maxnumberline.substring(maxnumberline.length-3);
	var numB = Number(numA) + Number(number);
 
	if(Number(numB) >= 1000){
		alert("\u5df2\u7ecf\u8d85\u8fc7\u4e13\u7ebf\u5e8f\u5217\u6761\u65701000\u6761");
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
		alert ("请填写服务器型号！");
		return false;
	}
	//IP地址
    if (ip == ""){
		alert ("请填写IP地址！");
		return false;
	}

	//IP地址类型
    if (ipType == "") {
        alert("请选择IP地址类型！");
        return false;
    }
	
	if(!$.verifylib.checkIp(ip)){
	   alert("IP地址格式不正确请重新输入！");
	   return false;
	}
	
	//空间租用费
	if (spacePrice == ""){
		alert ("请填写空间租用费！");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(spacePrice);
		if(!flag){
			alert("空间租用费必须是整数");
			return false;
		}
	}
	
	//端口通信费
	if (portPrice == ""){
		alert ("请填写端口通信费！");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(portPrice);
		if(!flag){
			alert("端口通信费必须是整数");
			return false;
		}
	}
	//免费IP端口
    if (freeIp == "" || freeIp == null){
		alert ("请选择免费IP端口！");
		return false;
	}

    //机柜服务电费（元）
	if(!$.verifylib.checkPInteger(cabinetSvcEcIncome))
	{
		alert('机柜服务电费必须填入整数！');
		return false;
	}
    
    //IDC增值服务费（元）
	if(!$.verifylib.checkPInteger(idcValAddedSvcIncome))
	{
		alert('IDC增值服务费必须填入整数！');
		return false;
	}

    if (cabinetSvcEcIncome == "" || idcValAddedSvcIncome == ""){
		alert ("未输入机柜服务电费或IDC增值服务费，默认0（元）！");

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
	$.table.get("IdcLineTable").addRow(visplineData);
	
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
	
    calIncome();
    
	number++;	
}

function deleteData(){
	$.table.get("IdcLineTable").deleteRow();
	
    calCabinetSvcEcIncome();
    calIdcValAddedSvcIncome();
	
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
    
    var freeIpName = $("#pam_NOTIN_IP_PORT option:selected").text();
    //专线实例号
    var lineInstance = $("#pam_NOTIN_LINE_INSTANCENUMBER").val();

    //机柜服务电费（元）
    var cabinetSvcEcIncome = $("#pam_CABINET_SVC_EC_INCOME").val();

    //IDC增值服务费（元）
    var idcValAddedSvcIncome = $("#pam_IDC_VAL_ADDED_SVC_INCOME").val();
    

	//服务器型号
    if (serverType == ""){
		alert ("请填写服务器型号！");
		return false;
	}
	
	//IP地址
    if (ip == ""){
		alert ("请填写IP地址！");
		return false;
	}

    //IP地址类型
    if (ipType == "") {
        alert("请选择IP地址类型！");
        return false;
    }
	
	if(!$.verifylib.checkIp(ip)){
	   alert("IP地址格式不正确请重新输入！");
	   return false;
	}
	
	
	//空间租用费
	if (spacePrice == ""){
		alert ("请填写空间租用费！");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(spacePrice);
		if(!flag){
			alert("空间租用费必须是整数");
			return false;
		}
	}
	
	//端口通信费
	if (portPrice == ""){
		alert ("请填写端口通信费！");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(portPrice);
		if(!flag){
			alert("端口通信费必须是整数");
			return false;
		}
	}
	
	//免费IP端口
    if (freeIp == "" || freeIp == null){
		alert ("请选择免费IP端口！");
		return false;
	}
	
	$("#pam_NOTIN_IP_PORT").val(freeIp);
	$("#pam_NOTIN_IP_PORT_NAME").val(freeIpName);
    
    //机柜服务电费（元）
	if(!$.verifylib.checkPInteger(cabinetSvcEcIncome))
	{
		alert('机柜服务电费必须填入整数！');
		return false;
	}
    
    //IDC增值服务费（元）
	if(!$.verifylib.checkPInteger(idcValAddedSvcIncome))
	{
		alert('IDC增值服务费必须填入整数！');
		return false;
	}

    if (cabinetSvcEcIncome == "" || idcValAddedSvcIncome == ""){
		alert ("未输入机柜服务电费或IDC增值服务费，默认0（元）！");

	    if (cabinetSvcEcIncome == ""){
	        $("#pam_CABINET_SVC_EC_INCOME").val(0);
		}

	    if (idcValAddedSvcIncome == ""){
	        $("#pam_IDC_VAL_ADDED_SVC_INCOME").val(0);
		}
	}
	 
	//获取编辑区的数据
	var visplineData = $.ajax.buildJsonData("idcLinePart");
	//往表格里添加一行并将编辑区数据绑定上
	$.table.get("IdcLineTable").updateRow(visplineData);
	
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
	
    calIncome();
}

function tableRowClick(){
	//获取选择行的数据
	 var rowData = $.table.get("IdcLineTable").getRowData();
	 $("#pam_NOTIN_OPER_TAG").val(rowData.get("pam_NOTIN_OPER_TAG"));
	 $("#pam_NOTIN_SERVER_TYPE").val(rowData.get("pam_NOTIN_SERVER_TYPE"));
	 $("#pam_NOTIN_IP_ADDRESS").val(rowData.get("pam_NOTIN_IP_ADDRESS"));
	 $("#pam_NOTIN_SPACE_PRICE").val(rowData.get("pam_NOTIN_SPACE_PRICE"));
	 $("#pam_NOTIN_PORT_PRICE").val(rowData.get("pam_NOTIN_PORT_PRICE"));
	 $("#pam_NOTIN_IP_PORT").val(rowData.get("pam_NOTIN_IP_PORT"));
	 $("#pam_NOTIN_LINE_INSTANCENUMBER").val(rowData.get("pam_NOTIN_LINE_INSTANCENUMBER"));
	 $("#pam_CABINET_SVC_EC_INCOME").val(rowData.get("pam_CABINET_SVC_EC_INCOME"));
	 $("#pam_IDC_VAL_ADDED_SVC_INCOME").val(rowData.get("pam_IDC_VAL_ADDED_SVC_INCOME"));
	 $("#pam_TYPE_OF_IP_ADDRESS").val(rowData.get("pam_TYPE_OF_IP_ADDRESS"));
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
  	var pamAttr = $.table.get("IdcLineTable").getTableData(null, true);
	var oldPamAttr = $("#pam_NOTIN_OLD_AttrInternet").val();
	if (pamAttr == "" || pamAttr == "[]"){
		alert ("\u8BF7\u589E\u52A0\u4E13\u7EBF\uFF01");
		return false;
	}
	
	var attrList = new Wade.DatasetList();
	var attrList = compareDataset(pamAttr,oldPamAttr);
	
	$("#pam_NOTIN_AttrInternet").val(attrList);
	
	//---add by chenzg@20180621--begin--REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求----
	/*判断是否订购了：84012040,IDC峰值计费套餐，84012041,IDC95法则计费套餐
	 * 是则需要判断是否填写订购信息
	 * */
	var length = selectedElements.selectedEls.length;
	var isAdd = false;	//有新增的IDC套餐
	var isDel = false;	//有退订的IDC套餐
	var isExist = false;	//是否存在IDC套餐
	//alert(selectedElements.selectedEls);
	for(var i=0;i<length;i++){
		var selectEle = selectedElements.selectedEls.get(i);
		var elementType = selectEle.get("ELEMENT_TYPE_CODE");
		var elementId = selectEle.get("ELEMENT_ID");
		var state = selectEle.get("MODIFY_TAG");
		if(elementType=="D"&&(elementId=="84012040" || elementId=="84012041")){
			if(state=="0"){
				isAdd = true;
			}else if(state=="1"){
				isDel = true;
			}else if(state=="exist"){
				isExist = true;
			}
		}
	}
	//alert("isAdd="+isAdd+",isDel="+isDel+",isExist="+isExist);
	var orderData = $.table.get("IdcOrderTable").getTableData(null, true);
	var orderDataLst = $.DatasetList();
	//找出增删改的数据
	if(orderData!=null && orderData.length>0){
		orderData.each(function(item,index,totalcount){
			var tag = item.get("tag", "");
			if(tag=="0" || tag=="1" || tag=="2"){
				orderDataLst.add(item);
			}
		});
	}
	
	$("#pam_IDC_ORDER_DATA").val(orderDataLst.toString());
	//alert($("#pam_IDC_ORDER_DATA").val());
	//未填写产品订购信息
	if(orderDataLst==null || orderDataLst.length == 0){
		//原来没有，现在新增IDC套餐
		if(isAdd&&!isExist){
			alert("您订购了IDC相关套餐，请填写产品参数中的订购信息[设备名称,设备IP,设备端口名称]！");
			return false;
		}
		//退订原IDC套餐,当次没有新增IDC套餐,也没有已经存在的IDC套餐
		else if(isDel&&!isAdd&&!isExist){
			alert("您退订了IDC相关套餐，请删除产品参数中的订购信息[设备名称,设备IP,设备端口名称]！");
			return false;
		}
	}
	//填写产品订购信息
	else{
		//填写了订购信息，但是没有新增、删除及存在的IDC套餐
		if(!isAdd&&!isExist&&!isDel){
			alert("您未订购或退订IDC相关套餐，无需填写产品参数中的订购信息[设备名称,设备IP,设备端口名称]！");
			return false;
		}
		if(isDel&&!isAdd&&!isExist){
			if(orderData.length!=orderDataLst.length){
				alert("您退订了IDC相关套餐，请全部删除产品参数中的订购信息[设备名称,设备IP,设备端口名称]！");
				return false;
			}
		}
	}
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
	        	/**
	        	if(newValueColumn.get('tag') == ""){
	        		oldValueColumn.put("STATE","EXIST");
	        	}
	        	if(newValueColumn.get('tag') == "1"){
	        		oldValueColumn.put("STATE","DEL");
	        	}
	        	*/
	        	if(newValueColumn.get('tag') == "2"){
	        		var isfound = "true";
					resultSet.add(newValueColumn);	        	
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
	if(!$.verifylib.checkInteger(depositIncome))
	{
		alert('主机托管总收入必须填入整数！');
		return false;
	}
	if(!$.verifylib.checkInteger(accessIncome))
	{
		alert('互联网接入总收入必须填入整数！');
		return false;
	}
	if(!$.verifylib.checkInteger(otherIncome))
	{
		alert('其它总收入必须填入整数！');
		return false;
	}
	if(!$.verifylib.checkInteger(nCabinetSvcEcIncome))
	{
		alert('机柜服务总电费必须填入整数！');
		return false;
	}
	if(!$.verifylib.checkInteger(nIdcValAddedSvcIncome))
	{
		alert('IDC总增值服务费必须填入整数！');
		return false;
	}
	
	if(depositIncome == '' || isNaN(depositIncome))
	{
//		$("#pam_N_DEPOSIT_INCOME").val(0);
		depositIncome = parseInt(0);
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
		accessIncome = parseInt(0);
	}
//	else if( !$.isNumber(accessIncome))
//	{
//		alert("互联网接入总收入必须填入数字！");
//		$("#pam_N_ACCESS_INCOM").focus()
//		return false;
//	}
	
	if(otherIncome == ''|| isNaN(otherIncome))
	{
//		$("#pam_N_OTHER_INCOME").val(0);
		otherIncome = parseInt(0);
	}
//	else if( !$.isNumber(otherIncome))
//	{
//		alert("其它总收入必须填入数字！");
//		$("#pam_N_OTHER_INCOME").focus()
//		return false;
//	}
	
	if(nCabinetSvcEcIncome == ''|| isNaN(nCabinetSvcEcIncome))
	{
		nCabinetSvcEcIncome = parseInt(0);
	}
	
	if(nIdcValAddedSvcIncome == ''|| isNaN(nIdcValAddedSvcIncome))
	{
		nIdcValAddedSvcIncome = parseInt(0);
	}
	
	$("#pam_N_IDC_INCOME").val(parseInt(depositIncome)+parseInt(accessIncome)+parseInt(otherIncome)+parseInt(nCabinetSvcEcIncome)+parseInt(nIdcValAddedSvcIncome)); // TODO 机柜服务总电费 和 IDC总增值服务费 是否计入 IDC总收入 
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

function checkPInteger(object){
	if(!$.verifylib.checkPInteger($(object).val())|| object.value=='0')
	{
		alert($(object).attr('desc')+'必须为正整数！');
		$(object).focus();
		return false;
	}
	else
		return true;
}

// 机柜服务总电费（元）计算
function calCabinetSvcEcIncome(){
	var cabinetSvcEcIncomeTotal = 0;
	var idcLineTableData = $.table.get("IdcLineTable").getTableData(null,true);
	
	for ( var i = 0; i < idcLineTableData.length; i++) {
		var cabinetSvcEcIncome = 0;
		var idcLineColumnValue = idcLineTableData.get(i);

		cabinetSvcEcIncome = idcLineColumnValue.get("pam_CABINET_SVC_EC_INCOME");
		
		if ("1" == idcLineColumnValue.get("tag")) { // tag = 1 - 删除
			cabinetSvcEcIncome = 0;
		}

		cabinetSvcEcIncomeTotal = parseInt(cabinetSvcEcIncomeTotal) + parseInt(cabinetSvcEcIncome);
	}
	
	$("#pam_N_CABINET_SVC_EC_INCOME").val(parseInt(cabinetSvcEcIncomeTotal));
}

//IDC总增值服务费（元）计算
function calIdcValAddedSvcIncome(){

	var idcValAddedSvcIncomeTotal = 0;
	var idcLineTableData = $.table.get("IdcLineTable").getTableData(null,true);
	
	for ( var i = 0; i < idcLineTableData.length; i++) {
		var idcValAddedSvcIncome = 0;
		var idcLineColumnValue = idcLineTableData.get(i);
		
		idcValAddedSvcIncome = idcLineColumnValue.get("pam_IDC_VAL_ADDED_SVC_INCOME");
		
		if ("1" == idcLineColumnValue.get("tag")) { // tag = 1 - 删除
			idcValAddedSvcIncome = 0;
		}

		idcValAddedSvcIncomeTotal = parseInt(idcValAddedSvcIncomeTotal) + parseInt(idcValAddedSvcIncome);
	}
	
	$("#pam_N_IDC_VAL_ADDED_SVC_INCOME").val(parseInt(idcValAddedSvcIncomeTotal));
}

/**
 * add by chenzg@20180621 REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求
 * 校验：84012040 IDC峰值计费套餐，84012041 IDC95法则计费套餐 的属性输入
 * @param index
 * @return
 * @date 2018-06-13
 * @author chenzg
 */
function checkDiscntAttr(itemIndex){
	var tempElement = selectedElements.selectedEls.get(itemIndex);
	var discntCode = tempElement.get("ELEMENT_ID");	//优惠编码
	var dkType = $("#61301").val();				//带宽种类
	var dkNum = $("#61302").val();				//带宽条数
	var monFee = $("#61303").val();				//单价(元/月/带宽)
	var maxPercent = $("#61304").val();			//上浮比例(百分比)
	var minPercent = $("#61305").val();			//保底比例(百分比)
	//带宽种类
	if(!$.verifylib.checkPInteger(dkType)){
		alert("带宽种类请填写大于0的数字,填写说明:xxMb则填写xx,xxGb则填写xx乘于1024的值！");
		return false;
	}
	//带宽条数
	if(!$.verifylib.checkPInteger(dkNum)){
		alert("带宽条数请填写>=0的整数！");
		return false;
	}
	//单价(元/月/带宽)
	if(!$.verifylib.checkPInteger(monFee)){
		alert("单价(元/月/带宽)请填写>=0的整数！");
		return false;
	}
	//上浮比例(百分比)
	if(!$.verifylib.checkPInteger(maxPercent)){
		alert("上浮比例(百分比)请填写>20的整数！");
		return false;
	}else{
		if(!(maxPercent>20 && maxPercent<=100)){
			alert("上浮比例(百分比)请填写>20的整数！");
			return false;
		}
	}
	//保底比例(百分比)
	if(!$.verifylib.checkPInteger(minPercent)){
		alert("保底比例(百分比)请填写>=0的整数！");
		return false;
	}else{
		if(!(minPercent>=0 && minPercent<=100)){
			alert("保底比例(百分比)必须在[0-100]之间！");
			return false;
		}
	}
	
	selectedElements.confirmAttr(itemIndex);
	return true;
}
/**
 * 订购信息表格行数据点击处理
 * @return
 * add by chenzg@20180621 REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求
 */
function orderTableRowClick(){
	//获取选择行的数据
	 var rowData = $.table.get("IdcOrderTable").getRowData();
	 $("#IDC_DEVICE_NAME").val(rowData.get("IDC_DEVICE_NAME"));
	 $("#IDC_DEVICE_IP").val(rowData.get("IDC_DEVICE_IP"));
	 $("#IDC_DEVICE_PORT").val(rowData.get("IDC_DEVICE_PORT"));
	 $("#IDC_DEVICE_INSTID").val(rowData.get("IDC_DEVICE_INSTID"));
}
/**
 * 新增订购信息行数据
 * @return
 * add by chenzg@20180621 REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求
 */
function createOrderData() {
    var deviceName = $("#IDC_DEVICE_NAME").val(); 	//设备名称
    var deviceIp = $("#IDC_DEVICE_IP").val();		//IP地址
    var devicePort = $("#IDC_DEVICE_PORT").val();	//端口

    //设备名称
    if (deviceName == ""){
		alert ("请填写设备名称！");
		return false;
	}
	//IP地址
    if (deviceIp == ""){
		alert ("请填写设备IP地址！");
		return false;
	}
	if(!$.verifylib.checkIp(deviceIp)){
	   alert("设备IP地址格式不正确请重新输入！");
	   return false;
	}
	
	//设备端口名称
    if (devicePort == "" || devicePort == null){
		alert ("请填写设备端口名称！");
		return false;
	}
	
	var orderData = $.ajax.buildJsonData("IdcOrderPart");
	$.table.get("IdcOrderTable").addRow(orderData);
	$("#IDC_DEVICE_NAME").val('');
	$("#IDC_DEVICE_IP").val('');
	$("#IDC_DEVICE_PORT").val('');
	$("#IDC_DEVICE_INSTID").val('');
}
/**
 * 删除订购信息表格行数据
 * @return
 * add by chenzg@20180621 REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求
 */
function deleteOrderData(){
	$.table.get("IdcOrderTable").deleteRow();
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

    //设备名称
    if (deviceName == ""){
		alert ("请填写设备名称！");
		return false;
	}
	//IP地址
    if (deviceIp == ""){
		alert ("请填写设备IP地址！");
		return false;
	}
	if(!$.verifylib.checkIp(deviceIp)){
	   alert("设备IP地址格式不正确请重新输入！");
	   return false;
	}
	
	//设备端口名称
    if (devicePort == "" || devicePort == null){
		alert ("请填写设备端口名称！");
		return false;
	}
	
	var orderData = $.ajax.buildJsonData("IdcOrderPart");
	$.table.get("IdcOrderTable").updateRow(orderData);
	$("#IDC_DEVICE_NAME").val('');
	$("#IDC_DEVICE_IP").val('');
	$("#IDC_DEVICE_PORT").val('');
	$("#IDC_DEVICE_INSTID").val('');
}
