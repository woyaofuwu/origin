//查询集团客户信息
function getGroupInfo() {	
	//EC编码校验
	if(!$.validate.verifyAll("ImsUserPart")) {
		return false;
	}
	
	//EC编码查询	
	$.beginPageLoading("数据查询中...");
	$.ajax.submit('ImsUserPart', 'getGroupBaseInfo', '', 'GroupInfoPart,ImsUserPart2', function(data){
		$.endPageLoading();
		var resultcode = data.get('X_RESULTCODE','0');
   		if(resultcode!='0'){   	
   			$.showWarnMessage(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));
   		}
	},
	function(error_code,error_info,derror) {
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function checkBlackWhite(){
	var userTypeCode = $("#USER_TYPE_CODE").val();
	var serialNumber = $("#cond_SERIAL_NUMBER");
	if(null == userTypeCode || ""== userTypeCode) {
		alert("黑白名单属性不能为空，请选择黑白名单属性之后再进行操作！");
		serialNumber.val("");
		return false;
	}else{
		return true;
	}
}

function changeblackwhite() {	
	checkBlackWhite();
	var	userId = $("#IMS_USER_ID").val();	
	if(null!= userId && "" != userId) {			
		$.beginPageLoading("数据查询中...");
	    $.ajax.submit('ImsInfoPart,MemInfo', 'refreshListArea', null, 'BlackWhitePart', 
		function(data){
			$.endPageLoading();		
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
	}
	getBizcode();
}

function queryblackwhite() {	
	if(!checkBlackWhite()){
		$("#IMS_USER_ID").val("");
		return false;
	}
	var	userId = $("#IMS_USER_ID").val();	
	$("#GRP_USER_ID").val(userId);				  

	$.beginPageLoading("数据查询中...");
    $.ajax.submit('ImsInfoPart,MemInfo', 'refreshListArea', null, 'BlackWhitePart', 
	function(data){
		$.endPageLoading();		
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });

	getBizcode();
}

function getOpenBizBw(){
	var utypecode = "";	
	var userId = $("#M_USER_ID").val();	 
	var userTypeCode = $("#USER_TYPE_CODE").val();
	var eparchyCode = $("#EPARCHY_CODE").val();
	
	if(userTypeCode=="IB0"){
		utypecode = "PersonalCallerBlack";
	}else if(userTypeCode=="IB1"){
		utypecode = "PersonalCalleeBlack";
	}else if(userTypeCode=="IW0"){
		utypecode = "PersonalCallerWhite";
	}else if(userTypeCode=="IW1"){
		utypecode = "PersonalCalleeWhite";
	}	
	$.ajax.submit('ImsInfoPart', 'chkMebBwOpen', '&USER_ID='+userId+'&USER_TYPE_CODE1='+utypecode+'&EPARCHY_CODE='+eparchyCode, null, 
	function(data){
		$.endPageLoading();
		var message = data.get('ERROR_MESSAGE','0');
		var result = data.get('RESULT',true);
		if (result=='false'){
			MessageBox.alert("系统提示",message,function(btn){
				$("#USER_TYPE_CODE").val('');   
				$("#USER_TYPE_CODE").focus();
			});	
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function getBizcode(){ 
	var tmp = $("#USER_TYPE_CODE").val();	 
    var code = $("#BIZ_CODE").val(tmp.substr(2, 1));
	if(code=='0'){
		$("#BIZ_CODE_NAME").val('主叫');	 
	}else{
		$("#BIZ_CODE_NAME").val('被叫');	 
	}
}

function codeToname(){
	var code = $("#BIZ_CODE").val();	
	if(code=='0'){
		$("#BIZ_CODE_NAME").val('主叫');	 
	}else{
		$("#BIZ_CODE_NAME").val('被叫');	 
	}
}

/**************************************************动态表格开始*********************************************************/
function tableRowClick() {
	//获取选择行的数据
	var rowData = $.table.get("DeptTable").getRowData();
}
function tableColumnClick() {alert(3);}
function tableAddRow(e) {$.table.get("DeptTable").addRow(e);};
function tableDeleteRow(e) {$.table.get("DeptTable").deleteRow();};
function tableCleanRow(e) {$.table.get("DeptTable").cleanRow(e);};

function createDept() {   
	//获取编辑区的数据
	var custEdit = $.ajax.buildJsonData("EditPart");
	var table = $.table.get("DeptTable");
	var serialNumber = $("#SERIAL_NUMBER").val();	 //编辑区 输入的服务号码
	if(!$.validate.verifyAll("ImsInfoPart")) {
		return false;
	}
	if(!$.validate.verifyAll("EditPart")) {
		return false;
	}
	
	var code = $("#BIZ_CODE").val();	
	if(code=='0'){
		$("#BIZ_CODE_NAME").val('主叫');	 
	}else{
		$("#BIZ_CODE_NAME").val('被叫');	 
	}
	var codeName = $("#BIZ_CODE_NAME").val();	
	custEdit["BIZ_CODE_NAME"] = codeName;
	custEdit["X_TAG"] = "0";   //0新增 1删除 2修改
	
	var ds = table.getTableData(null,true); //获得表格的数据	
	for(var i=0; i<ds.length; i++){
		var sn = ds.get(i).get("SERIAL_NUMBER");  //表格中的服务号码
		var biz_code = ds.get(i).get("BIZ_CODE"); 
		if(serialNumber == sn && biz_code == code) {
			alert('关键字段['+'服务号码,呼叫属性'+']已存在同样的值\r\n['+serialNumber+','+codeName+'],请不要重复添加！');			
			return false;
			continue;
		}
	}	
	table.addRow(custEdit);	
	$("#SERIAL_NUMBER").val('');   
}

function deleteDept() {	
	MessageBox.confirm("提示信息","确定删除该条数据吗?",function(btn)
			{
				if(btn=='ok')
				{
					//获取编辑区的数据
					var custEdit = $.ajax.buildJsonData("EditPart");
					var table = $.table.get("DeptTable");
					//custEdit["X_TAG"] = "1";   //0新增 1删除 2修改
					table.deleteRow();	
				}
			});
}

function getTableData() {
	var data = $.table.get("DeptTable").getTableData();
}
/**************************************************动态表格结束*********************************************************/


function onSubmitBaseTradeCheck(){	
	var bwList = $.table.get("DeptTable").getTableData();	
	if (bwList.length == '' || bwList == null ){
		alert("请先选择对表格进行操作后提交！");
		return false;
	}	
	//设置表格数据
	$.cssubmit.setParam("BW_LISTS", bwList);  
	
	return true;                                                                                 
}