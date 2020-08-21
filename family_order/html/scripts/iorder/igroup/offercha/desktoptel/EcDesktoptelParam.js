function validateParamPage(methodName) {
	if(methodName == "CrtUs" || methodName == "ChgUs") {
	    var vpnUserNum = $("#vpnUserNum").val();
		var userNum = $("#pam_BGUserNum").val();
		var fixUserNum = $("#pam_BGFixUserNum").val();
		var mobUserNum = $("#pam_BGMobileUserNum").val();
		var num=parseInt(fixUserNum)+parseInt(mobUserNum);
		if(userNum<num) {
			alert ("\u56FA\u5B9A\u96C6\u56E2\u6210\u5458\u6570\u4E0E\u79FB\u52A8\u96C6\u56E2\u6210\u5458\u6570\u4E4B\u548C\u4E0D\u80FD\u5927\u4E8E\u96C6\u56E2\u6210\u5458\u6570\uFF0C\u8BF7\u91CD\u65B0\u586B\u5199\uFF01");
			return false;
		}
		var infodata = $("#infodata").val();
		var vinfodata = $.DatasetList(infodata);
		var vpn_no = $("#pam_VPN_NO").val();
		
		if(methodName == "CrtUs") {
			if ((vinfodata.value != "[]" || vinfodata.value != "") && vinfodata.getCount()>0){
				if(vpn_no == "") {
					alert ("\u8BF7\u9009\u62E9\u0056\u0050\u004E\u7F16\u7801\uFF01");
					return false;
				}
			}
		}
		else if(methodName == "ChgUs") {
			if (vpnUserNum>0 && vpnUserNum<userNum){
			    MessageBox.alert("","\u0056\u7F51\u96C6\u56E2\u6700\u5927\u7528\u6237\u6570\u5C0F\u4E8E\u591A\u5A92\u4F53\u684C\u9762\u7535\u8BDD\u96C6\u56E2\u6210\u5458\u6570\uFF0C\u6B64\u53D8\u66F4\u4E1A\u52A1\u4E0D\u5141\u8BB8\u529E\u7406\uFF01");
			    return false;
			}
		}
	}
	else if(methodName == "CrtMb" || methodName == "ChgMb") 
	{
		var shortNumber = $("#SHORT_NUMBER_PARAM_INPUT").val();
    	var shortNumberInput = $("#pam_SHORT_CODE").val();
    	if (shortNumber){
	        if (shortNumber != shortNumberInput) {
	            MessageBox.alert("","\u8BF7\u5148\u9A8C\u8BC1\u77ED\u53F7\u7801\uFF01");
	            return false;
	        }
    	}
	    else 
	    {
	        MessageBox.alert("","\u8BF7\u5148\u9A8C\u8BC1\u77ED\u53F7\u7801\uFF01");
	        return false;
	    }
		var nocondTransfer=$("#pam_NOCOND_TRANSFER").val();
		var busyTransfer=$("#pam_BUSY_TRANSFER").val();
		var nocondTransferSn=$("#pam_NOCOND_TRANSFER_SN").val();
		var busyTransferSn=$("#pam_BUSY_TRANSFER_SN").val(); 
		if(nocondTransfer=='1'&&nocondTransferSn==''){
			MessageBox.alert("","\u8BF7\u8F93\u5165\u65E0\u6761\u4EF6\u547C\u53EB\u524D\u8F6C\u53F7\u7801");
		   	return false;
		}
		if(busyTransfer=='1'&&busyTransferSn==''){
		   	MessageBox.alert("","\u8BF7\u8F93\u5165\u9047\u5FD9\u547C\u53EB\u524D\u8F6C\u53F7\u7801");
		   	return false;
		}
		 	
		var capsType = $("#pam_HSS_CAPS_TYPE").val();
		var capsId = $("#pam_HSS_CAPS_ID").val();
		if(capsType == "0" && capsId == "") {
			alert ("\u80FD\u529B\u7C7B\u578B\u4E3A\u5FC5\u9009\u80FD\u529B\u65F6\uFF0C\u80FD\u529B\u503C\u4E0D\u80FD\u4E3A\u7A7A\uFF01");
			return false;
		}
		var fnrflag=$("#pam_CNTRX_CFNR_BSV").val();
	    var fnrsnflag=$("#pam_CNTRX_CFR_SN").val();		
		if(fnrflag=="1" && fnrsnflag=="")
		{
		  	MessageBox.alert("","\u65E0\u5E94\u7B54\u547C\u53EB\u524D\u8F6C\u53F7\u7801\u4E0D\u80FD\u4E3A\u7A7A\uFF01");
		  	return false;
		}
		
		var fnlflag=$("#pam_CNTRX_CFNL_BSV").val();
	    var fnlsnflag=$("#pam_CNTRX_CFNL_SN").val();		
		if(fnlflag=="1" && fnlsnflag=="")
		{
		  	MessageBox.alert("","\u672A\u6CE8\u518C\u547C\u53EB\u524D\u8F6C\u53F7\u7801\u4E0D\u80FD\u4E3A\u7A7A\uFF01");
		  	return false;
		}
		var alarFlag = $("#pam_MEMB_WAKE_ALARMFLAG").val();
		var number = $("#pam_MEMB_WAKE_NUMBER").val();
		var time = $("#pam_MEMB_WAKE_TIME").val();
		if(alarFlag != "" && number == "") {
			alert ("\u8BF7\u586B\u5199\u95F9\u9192\u53F7\u7801\uFF01");
			return false;
		}
		if(alarFlag != "" && time == "") {
			alert ("\u8BF7\u586B\u5199\u95F9\u9192\u65F6\u95F4\uFF01");
			return false;
		}
		
		
		var aflag=$("#pam_CNTRX_NO_DISTURB").val();
	    var bflag=$("#pam_ListProperty").val();		
		if(aflag=="1" && bflag=="")
		{
		  	MessageBox.alert("","\u8BF7\u9009\u62E9\u53F7\u7801\u5217\u8868\u5C5E\u6027\u7C7B\u578B\uFF01");
		  	return false;
		}
		if(methodName == "CrtMb"){
			var roleCodeB = $("#ROLE_CODE_B").val();
			var power = $("#pam_CNTRX_MEMB_POWER").val();
			if(roleCodeB == "2"){
				if(power == ""){
					MessageBox.alert("","\u5F53\u6210\u5458\u89D2\u8272\u4E3A\u7BA1\u7406\u5458\u65F6\uFF0C\u7BA1\u7406\u5458\u6743\u9650\u4E0D\u80FD\u4E3A\u7A7A\uFF0C\u8BF7\u9009\u62E9\u7BA1\u7406\u5458\u6743\u9650\uFF01");
					return false;
				}
				var bgAdminUserNum = $("#bgAdminUserNum").val();
		    	var adminUserNum = $("#adminUserNum").val();	
		    	if(parseInt(bgAdminUserNum)+1 > parseInt(adminUserNum)) {
		    		MessageBox.alert("","\u96C6\u56E2\u7BA1\u7406\u5458\u6570\u5DF2\u8FBE\u4E0A\u9650\uFF0C\u8BF7\u91CD\u65B0\u9009\u62E9\u96C6\u56E2\u6210\u5458\u89D2\u8272\u6216\u4FEE\u6539\u96C6\u56E2\u7BA1\u7406\u5458\u6570\uFF01");
		    		return false;
		    	}
			}
		 }
	}
	
    return true;
}

function init(){
	var methodname =$("#METHOD_NAME").val();
	var vpnno =$("#pam_VPN_NO");
	if (methodname == "ChgUs") {
		vpnno.attr("disabled","true");
		vpnno.attr("nullable","no");
	}
	if(methodname == "CrtMb" || methodname == "ChgMb"){
		var tableedit = $.table.get("ZyhtTable");
		var shortCode =$("#pam_SHORT_CODE").val();
		if(shortCode != "") {
			$("#validButton").css("display","none");
		 	var methodName =  $("#METHOD_NAME").val();
		 	if(methodName == "CrtMb") {
		 		var shortNumberInput = $("#pam_SHORT_CODE").val();
		 	 	var shortNumber =$("#pam_OLD_SHORT_CODE").val();
			    if (shortNumber != "" && shortNumber != null)	{
			        if (shortNumber != shortNumberInput) {
			            var del = new Wade.DataMap();
			            del.put("RES_TYPE_CODE","S");
			            del.put("RES_CODE",shortNumber);
			            deleteRes(del);
			        }
			    }
			    var obj =new Wade.DataMap();
			    obj.put("RES_TYPE_CODE","S");
			    obj.put("RES_CODE",shortCode);
			    obj.put("CHECKED","true");
			    obj.put("DISABLED","true");
			    insertRes(obj);
			    $("#SHORT_NUMBER_PARAM_INPUT").val(shortCode);
		 	}
		
		}
		alamflag();
		checkTransferNumber();
		onchangefnrbsvflag();
		onchagefnlflag();
		checkTransferNumberBusy();
		onchagedisturbflag();
		checkShortInfos();
	}
}
//往动态表格里面加入一条数�?
function addShortInfos(){
	var editData = $.ajax.buildJsonData("shortInfo");
	var tableedit = $.table.get("ZyhtTable");
	var shortNum = $("#SHORT_NUMBER").val(); 
   	var serialNumber = $("#SHORT_SN").val(); 
	if(!$.validate.verifyAll("shortInfo")) {
		return false;
	}
	if(tableedit.isPrimary('SHORT_NUMBER', editData)){
		MessageBox.alert("","产品参数[缩位号码]重复,请重新输入！");				
		$("#SHORT_NUMBER").focus();
		return false; 
	} 
	if(tableedit.isPrimary('SHORT_SN', editData)) {
		MessageBox.alert("","产品参数[手机号码]重复,请重新输入！");
		$("#SHORT_SN").focus();
		return false; 	
	}
	
	//往表格里添加一行并将编辑区数据绑定�?	
	tableedit.addRow(editData);
	
	var tableInfos = tableedit.getTableData("X_TAG,SHORT_NUMBER,SHORT_SN");
	$("#pam_SHORTS").val(tableInfos.toString());
}

function tableAddRow(e) {$.table.get("ZyhtTable").addRow(e);};
function tableDeleteRow(e) {$.table.get("ZyhtTable").deleteRow();};


/**
*动态表格删除一条数�?
*/
function delShortInfos(){
	var tableedit = $.table.get("ZyhtTable");
	if(!$.validate.verifyAll('ZyhtTable')) return false; 
	tableedit.deleteRow();
    var tableInfos = tableedit.getRowData("X_TAG,SHORT_NUMBER,SHORT_SN");
	$("#pam_SHORTS").val(tableInfos.toString()); 
}

/**
*
*/
function updateShortInfos(){
	if (!tableedit.verifyTable()) return false;
	if (!tableedit.checkRow('SHORT_SN', true)) return false;
	if (!tableedit.checkRow('SHORT_NUMBER', true)) return false;
	tableedit.updateRow();
    var tableInfos = $.table.get("ZyhtTable").getRowData("X_TAG,SHORT_NUMBER,SHORT_SN");
	$("#pam_SHORTS").val(tableInfos.toString());
}

function alamflag() {
	var alam =$("#pam_AlarmCall").val();
	if (alam=="1") {
		$("#alamflagDep").css("display","");
		$("#wakeNumberDep").css("display","");
		$("#wakeTiemDep").css("display","");
	}
	else{
		$("#alamflagDep").css("display","none");
		$("#wakeNumberDep").css("display","none");
		$("#wakeTiemDep").css("display","none");
		$("#pam_MEMB_WAKE_ALARMFLAG").val("");
		$("#pam_MEMB_WAKE_NUMBER").val("");
		$("#pam_MEMB_WAKE_TIME").val("");
	}
}

function onchangefnrbsvflag(){
   var flag = $("#pam_CNTRX_CFNR_BSV").val();
   if(flag=="1")
   {
      $("#dialnumber").css("display","");
   }
   else
   {  
      $("#dialnumber").css("display","none");
      $("#pam_CNTRX_CFR_SN").val("");
   }
}

function onchagefnlflag(){
    var flag = $("#pam_CNTRX_CFNL_BSV").val();
    if(flag=="1")
    {
       $("#idcallnumber").css("display","");
    }
    else
    {
        $("#idcallnumber").css("display","none");
        $("#pam_CNTRX_CFNL_SN").val("");
    }
}

function onchagedisturbflag(){
   var flag = $("#pam_CNTRX_NO_DISTURB").val();
    if(flag=="1")
    {
        $("#idcalltype").css("display","");
        $("#tNoDisturbInfo").css("display","");
    }
    else
    {
       $("#idcalltype").css("display","none");
        $("#tNoDisturbInfo").css("display","none");
         $("#pam_CNTRX_MEBSN_1").val("");
         $("#pam_CNTRX_MEBSN_2").val("");
         $("#pam_CNTRX_MEBSN_3").val("");
         $("#pam_CNTRX_MEBSN_4").val("");
         $("#pam_CNTRX_MEBSN_5").val("");
         $("#pam_CNTRX_MEBSN_6").val("");
         $("#pam_CNTRX_MEBSN_7").val("");
         $("#pam_CNTRX_MEBSN_8").val("");
         $("#pam_CNTRX_MEBSN_9").val("");
         $("#pam_CNTRX_MEBSN_10").val("");
    }
}


function checkShortInfos(){
	var obj = $("#pam_SHORT_DIAL").attr("checked"); 
	if(obj){
        $("#shortInfo").css("display","");
        $("#dynamShortSpan").css("display","");
        $("#pam_SHORT_DIAL").val("1");
  	}else{
        $("#shortInfo").css("display","none");
       	$("#dynamShortSpan").css("display","none");
       	$("#pam_SHORT_DIAL").val("0");
 	}
}

function validateShortNum(obj) 
{
    var shortCode = $("#pam_SHORT_CODE").val();
    var shortCodeTmp = $.trim(shortCode); //去掉空格
    var user_id = $("#GRP_USER_ID").val();
    var meb_cust_id = $("#MEB_CUST_ID").val();
    var meb_eparchy_code = $("#MEB_EPARCHY_CODE").val();
    if(shortCode == "") 
    {
    	MessageBox.alert("",'短号码不能为空！');
    	$("#pam_SHORT_CODE").focus();
    	return false;
    }
    if(shortCode != shortCodeTmp)
    {
    	MessageBox.alert("",'短号['+shortCode+']含有空格，请去掉!');
    	return false;
    }
    
	var shortNumber = $("#SHORT_NUMBER_PARAM_INPUT").val();
	if ($.validate.verifyField(obj))
    {
    	if(shortNumber == shortCode )
    	{
			MessageBox.alert("","短号未修�?无需校验!");
			$("#pam_SHORT_CODE").focus();
			return false;
		}
		
		var param = '&SHORT_CODE='+obj.val()+'&USER_ID='+user_id+'&MEB_EPARCHY_CODE='+meb_eparchy_code+'&MEB_CUST_ID='+meb_cust_id+'&METHOD_NAME=validchk&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.desktoptel.MebParamInfo';					   
        Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.group.param.ProxyParam','productParamInvoker',param, afterValidateShortNum,err);           																																						   
	}

}

function afterValidateShortNum(data) 
{
	var shortCode = data.get("SHORT_CODE");
	var result = data.get("RESULT");
	var error_msg = data.get('ERROR_MESSAGE');
    if (result == "false") 
    {
		MessageBox.alert("",error_msg);
        $("#pam_SHORT_CODE").val("");
        $("#pam_SHORT_CODE").focus();
        return false;
    }
    else 
    {
        displayShort(shortCode);
    }
}

function err(error_code,error_info)
{
	MessageBox.alert("",error_info);
}
    
function displayShort(shortCode) 
{
	var shortNumber = $("#SHORT_NUMBER_PARAM_INPUT").val();
    var shortNumberInput = $("#pam_SHORT_CODE").val();
    if (shortNumber != "" && shortNumber != null)	
    {
        if (shortNumber != shortNumberInput) 
        {
            var del = $.DataMap();
            del.put("RES_TYPE_CODE","S");
            del.put("RES_CODE",shortNumber);
            deleteRes(del);
        }
        else
        {
			return true;
		}
    }
    var obj = $.DataMap();
    obj.put("RES_TYPE_CODE","S");
    obj.put("RES_CODE",shortCode);
    obj.put("CHECKED","true");
    obj.put("DISABLED","true");
    insertRes(obj);
    $("#SHORT_NUMBER_PARAM_INPUT").val(shortCode);
    MessageBox.alert("","\u9a8c\u8bc1\u901a\u8fc7\uff0c\u5f55\u5165\u7684\u77ed\u53f7\u7801\u53ef\u4ee5\u4f7f\u7528\uff01");
    return true;
}
function checkTransferNumber(){
	var flag = $("#pam_NOCOND_TRANSFER").val();
	if(flag == "1"){      
		$("#NOCOND_TRANSFER").css("display","");       
  	}else{
		$("#NOCOND_TRANSFER").css("display","none");  
		$("#pam_NOCOND_TRANSFER_SN").val(""); 
  	}
}
function checkTransferNumberBusy(){
	var flag = $("#pam_BUSY_TRANSFER").val();
	if(flag == "1"){     
		$("#BUSY_TRANSFER").css("display","");       
  	}else{
		$("#BUSY_TRANSFER").css("display","none");  
		$("#pam_BUSY_TRANSFER_SN").val("");  
  	}
}