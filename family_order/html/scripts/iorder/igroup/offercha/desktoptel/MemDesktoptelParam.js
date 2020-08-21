var table = new Wade.DatasetList();

var initFlag = 0;

function initPageParam_110000222201(){
	$(".top").attr("hidden","true");
	if(initFlag!=0){
		var mapList = $.DatasetList($.parseJSON($("#cha_110000222201").val()));
		mapList.each(function(item){
			var key = item.get("ATTR_CODE");
			if("SHORT_DIAL" == key){
				if($("#pam_"+key).val(item.get("ATTR_VALUE"))==1){
					$("input[type='checkbox']").attr('checked','true');
				}else{
					$("input[type='checkbox']").attr('checked','false');
				}
			}else{
				$("#pam_"+key).val(item.get("ATTR_VALUE"));
			}
		});
	}else{
		initFlag = 1;
	}
	//保存表格数据
	$.each(table, function(index, data) {
		ZyhtTable.addRow($.parseJSON(data.toString()));
	});
	
	$("#ZyhtTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClick(ZyhtTable.getSelectedRowData());
	});
	
	alamflag();
	checkTransferNumber();
	onchangefnrbsvflag();
	onchagefnlflag();
	checkTransferNumberBusy();
	onchagedisturbflag();
	checkShortInfos();
}

function tableRowClick(data) {
	//获取选择行的数据
	 $("#pam_SHORT_NUMBER").val(data.get("pam_SHORT_NUMBER"));
	 $("#pam_SHORT_SN").val(data.get("pam_SHORT_SN"));
}

//往动态表格里面加入一条数�?
function addShortInfos(){
	var editData = $.ajax.buildJsonData("shortInfo");
	if(!$.validate.verifyAll("shortInfo")) {
		return false;
	}
	table.add(ZyhtTable.getData);
	var shortNumber =  ZyhtTable.getData(true,'pam_SHORT_NUMBER');
	var shortSn =  ZyhtTable.getData(true,'pam_SHORT_SN');
	var flag = 0;
	shortNumber.each(function(item,index,totalcount){
		if($("#pam_SHORT_NUMBER").val()==shortNumber.get(index,'pam_SHORT_NUMBER')){
			flag = 1;
			$.validate.alerter.one($("#pam_SHORT_NUMBER")[0], "产品参数[缩位号码]重复,请重新输入！");
			return false; 
		}
	});
	shortNumber.each(function(item,index,totalcount){
		if($("#pam_SHORT_SN").val()==shortSn.get(index,'pam_SHORT_SN')){
			flag = 1;
			$.validate.alerter.one($("#pam_SHORT_SN")[0], "产品参数[手机号码]重复,请重新输入！");
			return false; 
		}
	});
	
	//往表格里添加一行并将编辑区数据绑定�?	
	if (flag==0){
		var data = $.DataMap(editData);
		ZyhtTable.addRow($.parseJSON(data.toString()));
		setPamShorts();
		delAll(editData);
	}
}

/*添加数据到表格后，重置编辑区*/
function delAll(data){
	var idata = $.DataMap(data);
	idata.eachKey(function(item,index,totalcount){
		$("#"+item).val('');
	});
}

function setPamShorts() {
	var data = ZyhtTable.getData();
	$("#pam_SHORTS").val(data.toString());	
}

function tableAddRow(e) {$.table.get("ZyhtTable").addRow(e);};
function tableDeleteRow(e) {$.table.get("ZyhtTable").deleteRow();};


/**
*动态表格删除一条数�?
*/
function delShortInfos(){
	table.remove(ZyhtTable.getData());
	ZyhtTable.deleteRow(ZyhtTable.selected);
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
	var obj =$("input[type='checkbox']").attr('checked');
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
    var user_id = $("#cond_EC_USER_ID").val();
    var meb_cust_id = $("#cond_CUST_ID").val();
    var meb_eparchy_code = $("#cond_USER_EPARCHY_CODE").val();
    if(shortCode == "") 
    {
    	$.validate.alerter.one($("#pam_SHORT_CODE")[0], "短号码不能为空！");
    	$("#pam_SHORT_CODE").focus();
    	return false;
    }
    if(shortCode != shortCodeTmp)
    {
    	$.validate.alerter.one($("#pam_SHORT_CODE")[0], '短号['+shortCode+']含有空格，请去掉!');
    	return false;
    }
    
	var shortNumber = $("#SHORT_NUMBER_PARAM_INPUT").val();
	if ($.validate.verifyField(obj))
    {
    	if(shortNumber == shortCode )
    	{
    		$.validate.alerter.one($("#pam_SHORT_CODE")[0], "短号未修改无需校验!");
			return false;
		}
		
		var param = '&SHORT_CODE='+obj.val()+'&USER_ID='+user_id+'&MEB_EPARCHY_CODE='+meb_eparchy_code+'&MEB_CUST_ID='+meb_cust_id;					   
        $.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.param.ProxyParam", "Deskvalidchk",param,  afterValidateShortNum, err);
        
    }

}

function afterValidateShortNum(data) 
{
	var shortCode = data.get("SHORT_CODE");
	var result = data.get("AJAX_DATA").get("RESULT");
	var error_msg = data.get("AJAX_DATA").get('ERROR_MESSAGE');
    if (result == "false") 
    {
    	$.validate.alerter.one($("#pam_SHORT_CODE")[0], error_msg);
        $("#pam_SHORT_CODE").val("");
        return false;
    }
    else 
    {
        displayShort(shortCode);
    }
}

function err(error_code,error_info)
{
	$.validate.alerter.one($("#pam_SHORT_CODE")[0], error_info);
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
//            deleteRes(del);
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
//    insertRes(obj);
    $("#SHORT_NUMBER_PARAM_INPUT").val(shortCode);
    $.validate.alerter.one($("#pam_SHORT_CODE")[0], "短号码可以使用！\n",'green');
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

function checkSub(obj){
	$("#pam_SHORT_NUMBER").attr("nullable","yes");
	$("#pam_SHORT_SN").attr("nullable","yes");
	
	table = null;
	table = ZyhtTable.getData();
	
	$("#pam_SHORTS").val(table.toString());
	
	
	submitOfferCha();
	var result = submitOfferCha();
	if(result==true){
		backPopup(obj);
	}
	try {
		//backPopup(obj);
	} catch (msg) {
			$.error(msg.message);
	}

	
}