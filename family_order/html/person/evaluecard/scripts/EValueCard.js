$(document).ready(function(){
});


function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'freshPart,QueryCondPart', function(data){
		$("#QueryCondPart").removeClass("e_dis");
		$("#SubmitPart").removeClass("e_dis");
	},
	function(error_code,error_info){
		$.endPageLoading(); 
		alert(error_info);
    });
}

//批量销售页面
function refreshPartAtferAuth2(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'freshPart,QueryCondPart', function(data){
		 disabledArea('QueryBatchSellReqPart', false);
		 disabledArea('BatchSellReqALLPart', false);
		 $("#QueryBatchSellReqPart").removeClass("e_dis");
		 $("#BatchSellReqALLPart").removeClass("e_dis");

		 var operRadio = document.getElementById("cond_OPERTYPE1");
		 operRadio.checked=true;
		 initDisplayDiv();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

//提交校验
function checkBeforeSubmit()
{  
	if (!$.validate.verifyAll())
    {
		return false;
	}
	if($("#BUY_COUNT").val() >10){
		MessageBox.alert("提示","购卡数量最多购买10张！");
		return false;
	}
	return true;
}

function checkSubmit(){

	var param = "";
	var objTable = document.getElementById("table1");
	if(objTable.rows.length <= 1){
		MessageBox.alert("提示","没有可以返销的记录不能提交！");
		return false;
	}
	var qryType = $("#QRY_TYPE").val();

	if(qryType == 1){
		param = "&QRY_FLAG=1&TRANSACTION_ID="+objTable.rows[1].cells[0].innerHTML;
	}else
		param = "&QRY_FLAG=2&CARD_NO="+objTable.rows[1].cells[1].innerHTML;
	
	$.cssubmit.addParam(param);
	
	return true;
}


//设置预存费用组件
function insertFee()
{
	$.feeMgr.removeFee("5333", "0", "20");//清空费用列表
	
	var buyCount= $("#BUY_COUNT").val();
	var money = $("#CARD_MONEY").val();
	if(buyCount != "" && money != ""){
		var feeData = new $.DataMap();
		feeData.put("TRADE_TYPE_CODE", "5333");
		feeData.put("MODE", "0");
		feeData.put("CODE", "20");
		feeData.put("FEE",  buyCount*money*100);
		$.feeMgr.insertFee(feeData);
	}
}

function queryBatchReqInfo(){
	
	var transId = $("#cond_TRANSACTION_ID").val();
	var startData = $("#cond_START_DATE").val();
	var endData = $("#cond_END_DATE").val();
 	if (!$.validate.verifyAll("QueryCondPart")){
		return false; 
	}
 	var day=31;
	//校验起始日期范围
	if(dateDiff(startData, endData)>day){
		alert("开始时间和结束时间跨度不能超过"+day+"天");
		return false;
	}
	
	$.beginPageLoading();
//	$.message.showSucTradeMessage(data);
	$.ajax.submit('QueryCondPart','queryBatchReqInfo', null,'refreshtable',function(data)
	{ 	
		$.endPageLoading();	  		
	},function(errorcode,errorinfo){
		alert(errorinfo);
		$.endPageLoading();
	}); 
}

function dateDiff(start,end){
	var day = 0;
	var startDt = new Date(Date.parse(start.replace(/-/g,   "/")));
	var endDt = new Date(Date.parse(end.replace(/-/g,   "/")));
	day = (endDt.getTime()-startDt.getTime()) / (1000*60*60*24) ;
	return day;
}

function createSellReq() {	
	
	var flag ="0";
	var channelType = $("#CHANNEL_TYPE").val();
	var publicKeyID = $("#PUBLIC_KEY_ID").val();
	var cardType = $("#CARD_TYPE").val();
	var cardMoney = $("#CARD_MONEY").val();
	var buyCount = $("#BUY_COUNT").val();
	var cardBusiprop = $("#CARD_BUSIPROP").val();//卡业务属性	

	if(channelType=="")
	{
		alert("请选择受理渠道！");
		return false;
	}
	
	if(publicKeyID=="")
	{
		alert("请输入公钥ID！");
		return false;
	}
	
	if(cardType=="")
	{
		alert("请选择卡类型！");
		return false;
	}
	
	if(cardMoney=="")
	{
		alert("请输入卡面值！");
		return false;
	}
	if(!$.isNumeric(cardMoney)){
		alert("卡面值输入不合法！");
		return false;
    }
	
	if(!$.isNumeric(buyCount)){
		alert("购卡数量输入不合法！");
		return false;
	}
	
	if(buyCount<=10){
		alert("购卡数量必须大于10张！");		
		return false;
	}
	if(buyCount>1000000){
		alert("购卡数量不能大于一百万！");		
		return false;
	}	
	
	//获取编辑区的数据
    var custEdit = $.ajax.buildJsonData("EditPart");
	var deptTable=$.table.get("SellReqTable").getTableData(null,true);	

	if(flag == "0"){
		//新增成员
		custEdit["CHANNEL_TYPE_TEXT"] = $("#CHANNEL_TYPE")[0].options($("#CHANNEL_TYPE")[0].selectedIndex).text;//受理渠道    文本
		custEdit["CHANNEL_TYPE"] = $("#CHANNEL_TYPE")[0].options($("#CHANNEL_TYPE")[0].selectedIndex).value;//受理渠道    值 
		custEdit["CARD_TYPE_TEXT"] = $("#CARD_TYPE")[0].options($("#CARD_TYPE")[0].selectedIndex).text;//卡类型   文本
		custEdit["CARD_TYPE"] = $("#CARD_TYPE")[0].options($("#CARD_TYPE")[0].selectedIndex).value;//卡类型   值
		custEdit["PUBLIC_KEY_ID"] = publicKeyID;//公钥ID）
		custEdit["CARD_MONEY"] = Math.round(parseFloat($("#CARD_MONEY").val())*100)/100;//现缴履约金金额（元）
		custEdit["BUY_COUNT"] = buyCount;
		custEdit["CARD_BUSIPROP"] = cardBusiprop;
		
	    $.table.get("SellReqTable").addRow(custEdit);
	}
	//alert("添加"+$.feeMgr.getFeeList());
	cleanSellReq();
}

function tableRowClick() {
//获取选择行的数据
	delAble =true;
}

function deleteSellReq(){	
	var rowData = $.table.get("SellReqTable").getRowData();
	if(rowData.length == 0)
	{
		alert("请您选择记录后再进行删除操作！");
		return false;
	}
	$.table.get("SellReqTable").deleteRow();	

cleanSellReq();


}

function  cleanSellReq(){
	
	$("#BUY_COUNT").val("");
	$("#CARD_MONEY").val("");	
}

function submitSellReqs(obj) {
	
	var add=false;//收取押金标志
	var upd=false;//清退押金标志
	var addPay =0;	
	var updPay =0;	
	var info = "";
	
	var totalFee = 0;
	var sellReqTable=$.table.get("SellReqTable").getTableData(null,true);
	//if(sellReqTable=="[]"||sellReqTable==null){
	//	MessageBox.alert("提示信息","请点击新增后再进行提交！");
	//	return false;
	//}
	for(var i=0;i<sellReqTable.length;i++){
		pay = parseFloat(sellReqTable.get([i],"PAY_MONEY"));
		$.feeMgr.removeFee("5333", "0", "20");//清空费用列表
		
		var buyCount= parseFloat(sellReqTable.get([i],"BUY_COUNT"));
		var money = parseFloat(sellReqTable.get([i],"CARD_MONEY"));
		
		if(buyCount != "" && money != ""){
			totalFee = totalFee + buyCount*money*100;
		}
	}
	var feeData = new $.DataMap();
	feeData.put("TRADE_TYPE_CODE", "5333");
	feeData.put("MODE", "0");
	feeData.put("CODE", "20");
	feeData.put("FEE",  totalFee);
	$.feeMgr.insertFee(feeData);

	$("#X_BATSELLREQ_STR").val(sellReqTable); 
	$.cssubmit.bindCallBackEvent(displayTrans);
	return true;
}

function displayTrans(data) {
	var isPrint = false;			//是否打印业务受理单		
	var isTicketPrint = false;		//是否打印票据
	var isPrivPrint = true;			//权限打印标识(根据打印权限控制或者接入方式判断)
	var title = "业务受理成功";

	var tradeData;
	if(data instanceof $.DatasetList){
		tradeData = data.get(0);
	}else if(data instanceof $.DataMap){
		tradeData = data;
	}	
	if(tradeData && tradeData.containsKey("TRADE_ID")){
		content = "业务流水号：" + tradeData.get("TRADE_ID") + "<br/>点【确定】查询业务受理进度。";
		$("#cond_TRANSACTION_ID").val(tradeData.get("BatchID"));
	}

	$.cssubmit.showMessage("success", title, content, isPrivPrint && isTicketPrint);
}

function toQueryTrans() {
	var queryRadio = document.getElementById("cond_OPERTYPE0");
	queryRadio.checked=true;
	initDisplayDiv();
}

function initDisplayDiv() {
	var checkRadio = $("input[name='cond_OPERTYPE'][type='radio']:checked");
	var checkRadioValue = checkRadio.val();
	if(checkRadioValue=='0'){
		$('#BatchSellReqALLPart').css('display','none');
		$('#QueryBatchSellReqPart').css('display','');
	}else{
		$('#BatchSellReqALLPart').css('display','');
		$('#QueryBatchSellReqPart').css('display','none');
	}
}

function downLoadFile(rowIndex) {
    var selTable = $.table.get("QueryListTable");
    var rowValue = selTable.getRowData(null,rowIndex);
    var fileid = rowValue.get("FILE_ID");
    var filename = rowValue.get("FILE_NAME");
    window.location.href="attach?action=download&realName="+filename+"&fileId="+fileid;
}

function queryCardInfo(){
	
	var cardNo = $("#COND_NO").val();
	var transId = $("#COND_ID").val();
	var qryFlag = document.getElementById("qryFlag");

	if(qryFlag.checked && transId == "" ){
		MessageBox.alert("提示","交易流水不能为空！");
		return false;
	}
	if(!qryFlag.checked && cardNo == "" ){
		MessageBox.alert("提示","有价卡卡号不能为空！");
		return false;
	}
	if(qryFlag.checked){
		$("#QRY_TYPE").val("1");
	}else
		$("#QRY_TYPE").val("2");
	
	$.beginPageLoading();
//	$.message.showSucTradeMessage(data);
	$.ajax.submit('QueryCondPart,AuthPart','queryCardInfo', "&CARD_NO="+cardNo+"&TRANSACTION_ID="+transId+"&QRY_FLAG="+$("#QRY_TYPE").val(),'BasicInfosPart',function(data)
	{ 
		
		var flag = data.get("IS_SUCCESS");
		if(flag == '0'){
			$("#SubmitPart").removeClass("e_dis");
			$("#CSSUBMIT_BUTTON").attr("disabled",null);
		}
		if(flag == '1'){
			MessageBox.alert("提示","没有可返销的记录！");
		}
		
		$.feeMgr.removeFee("5333", "0", "20");//清空费用列表
		var feeData = new $.DataMap();
		feeData.put("TRADE_TYPE_CODE", "5333");
		feeData.put("MODE", "0");
		feeData.put("CODE", "20");
		feeData.put("FEE",  data.get("TOTAL_FEE"));
		$.feeMgr.insertFee(feeData);
		//alert(feeData);
		$.endPageLoading(); 
		return ;
		  		
	},function(errorcode,errorinfo){
		alert(errorinfo);
		$.endPageLoading();
	}); 
}

function queryEValueCardInfoAndPayInfo(){
	
	var cardNo = $("#CARD_NO").val();
	if(cardNo == "" ){
		MessageBox.alert("提示","有价卡卡号不能为空！");
		return false;
	}
	$.beginPageLoading();
	$.ajax.submit('NormalQueryPart','queryCardInfoAndPayInfo','','QueryCardInfoPart,QueryPayedInfoPart',function(data)
	{ 
		var flag = data.get("IS_SUCCESS");
		var resultinfo = data.get("RESULT_INFO");
		if(flag == '0'){
			MessageBox.alert("提示","查询卡信息成功!");
		}else if(flag == '1'){
			MessageBox.alert("提示","查询失败！"+resultinfo);
		}
		$.endPageLoading(); 
		return ;
		  		
	},function(errorcode,errorinfo){
		alert(errorinfo);
		$.endPageLoading();
	}); 
}

//电子卡锁定、解锁、延期操作
function checkAndSubmit()
{
	//查询条件校验
	if(!$.validate.verifyAll("NormalSubmitPart"))
	{
		return false;
	}
	
	var begin = $("#BEGIN_CARDNO").val();
	var end = $("#END_CARDNO").val();
    if(end - begin > 10000){
    	MessageBox.alert("提示","单次电子卡操作的卡数量不得超过10000张！");
    	return false;
    }	
    
	$.beginPageLoading("业务受理中...");
	
	$.ajax.submit('NormalSubmitPart', 'lockOrUnlockEValueCard', null, 'NormalSubmitPart', function(data)
	{
		var flag = data.get("IS_SUCCESS");
		var resultinfo = data.get("RESULT_INFO");
		if(flag == '0'){
			MessageBox.alert("提示","操作成功!"+resultinfo);
		}else if(flag == '1'){
			MessageBox.alert("提示","操作失败！"+resultinfo);
		}else if(flag == '2'){
			MessageBox.alert("提示","操作失败！");
		}
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

//安全密钥上行更新
function UpdatePublicKey()
{
	//校验
	if(!$.validate.verifyAll("NormalSubmitPart"))
	{
		return false;
	}
	
	$.beginPageLoading("业务受理中...");
	
	$.ajax.submit('NormalSubmitPart', 'upRsaPublicKey', null, '', function(data)
	{
		var flag = data.get("IS_SUCCESS");
		if(flag == '0'){
			qryRsaPublicKey();
			resetInfo();
			MessageBox.alert("提示","更新成功！");
		}
		if(flag == '1'){
			MessageBox.alert("提示","更新失败！");
		}
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

//安全密钥省公司更新
function updateProvince()
{
	$.ajax.submit('', 'upRsaPublicKey', "&DONE_CODE=1", '', function(data)
	{
		var flag = data.get("IS_SUCCESS");
		if(flag == '0'){
			qryRsaPublicKey();
			MessageBox.alert("提示","更新成功！");
		}
		if(flag == '1'){
			MessageBox.alert("提示","更新失败！");
		}
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

//查询安全密钥列表
function qryRsaPublicKey()
{
	//校验
	if(!$.validate.verifyAll("QueryPart"))
	{
		return false;
	}
	
	$.beginPageLoading("业务受理中...");
	
	$.ajax.submit('QueryPart', 'qryRsaPublicKey', null, 'QueryListPart', function(data)
	{
		if(data.getCount() < 1)
		{
			 MessageBox.alert("提示信息","查询无数据！");
		}
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

//重置
function resetInfo(){
	$("#edit_SERIAL_NUMBER").val("");
	$("#edit_REMARK").val("");
	$("#edit_SECR_PASSWD").val("");
	$("#edit_PASSWD_ID").val("");
}

function addRsainfo(){
	$("#contentPart").css("display","");
	resetInfo();
}

function editRsaInfo(){
	var rowData = $.table.get("infosTable").getRowData();
	if(rowData.length == 0)
	{
		MessageBox.alert("提示信息", '单击列表选中要修改的数据，然后点击"修改"。');
		return false;
	}
	$("#contentPart").css("display","");
	resetInfo();
	//设置编辑界面的值
	$("#edit_PASSWD_ID").val(rowData.get("PASSWD_ID"));
	$("#edit_SERIAL_NUMBER").val(rowData.get("SERIAL_NUMBER"));
	$("#edit_REMARK").val(rowData.get("RSRV_STR1"));
	$("#edit_SECR_PASSWD").val(rowData.get("SECR_PASSWD"));
}

function deletRsaInfo(obj){
	var rowData = $.table.get("infosTable").getRowData();
	if(rowData.length == 0)
	{
		MessageBox.alert("提示信息", '单击列表选中要修改的数据，然后点击"删除"。');
		return false;
	}
    var pwdId = rowData.get("PASSWD_ID");
	MessageBox.confirm("确认信息",
			"确认要删除选中的安全密钥信息吗吗？",
			function(btn){
			    if(btn=="ok"){
			    	ajaxSubmit("","upRsaPublicKey","SECRPASSWD_ID="+pwdId+"&OPER_TYPE=3",null, function(data){
			    		$.endPageLoading();
			    		
			    		qryRsaPublicKey();

			    		MessageBox.alert("提示","安全密钥信息删除成功！");
			    	},
			    	function(error_code,error_info)
			    	{
			    		$.endPageLoading();
			    		alert(error_info);
			        });
			     }
			}
	);
}