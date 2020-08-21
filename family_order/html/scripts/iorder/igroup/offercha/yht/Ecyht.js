function initCrtUs() {  
	window["tableedit"] = new TableEdit("ZyhtTable",false,myOnClickMethod);
	window["tableedit2"] = new TableEdit("ByhtTable",false,myOnClickMethod2); 
	var codeCtl = getElements('Z_TAG'); 
    if(getElement('pam_Z_TAG_VAL').value=="1"){  
    	codeCtl[1].checked = true;
   	    
    }else if(getElement('pam_Z_TAG_VAL').value=="0"){  
        codeCtl[0].checked = true;
    }else{
    	getElement('pam_Z_TAG_VAL').value="0";
    	codeCtl[0].checked = true;
    	codeCtl[1].checked = false;
    }
}

function validateParamPage(methodName) {  
	if(methodName=='CrtMb'||methodName=='ChgMb'){
		var pamyht = $("#pam_zyht").val();
		var pambyht = $("#pam_byht").val();
		
	  	if ((pambyht == "" || pambyht== "[]")&&(pamyht == "" || pamyht== "[]")){
			alert ("\u8bf7\u589e\u52a0\u4e3b\u53eb\u6216\u88ab\u53eb\u4e00\u53f7\u901a\u53f7\u7801\uff01");
			return false;
		} 
		if(pamyht != "" && pamyht != "[]"){	
        	var zyhtset = $.DatasetList(pamyht);
          	var byhtset = $.DatasetList(pambyht);
          	for(var p=0;p<zyhtset.length;p++) {
            	var zyhtColumn = zyhtset.get(p);
          	 	var ifexistyht = false;
          	 	for(var o=0;o<byhtset.length;o++){
          	 		var byhtColumn	=  byhtset.get(o);
          	    	if(zyhtColumn.get('ZUSERID')==byhtColumn.get("BUSERID")){
          	        	ifexistyht = true;	
          	     	} 
          	 	}

          	 	if(ifexistyht){
          	  		alert ("\u4e3b\u53eb\u4e00\u53f7\u901a\u5173\u8054\u53f7\u7801"+zyhtColumn.get('SERIAL_NUMBER')+"\u4e0d\u80fd\u540c\u65f6\u662f\u88ab\u53eb\u4e00\u53f7\u901a\u5173\u8054\u53f7\u7801");
		           	return false;
          	 	}
          }			
		}
	}else if(methodName=='CrtUs'||methodName=='ChgUs'){
		if($("#pam_VPN_NO").val() == ""){
			MessageBox.alert("","\u0056\u0050\u004d\u004e\u7f16\u7801\u4e0d\u80fd\u4e3a\u7a7a\uff0c\u8bf7\u5148\u8ba2\u8d2d\u591a\u5a92\u4f53\u684c\u9762\u7535\u8bdd\uff0c\u83b7\u53d6\u0056\u0050\u004d\u004e\u7f16\u7801\uff01");
			return false;
		} 
	}
    return true;
}
	
function myOnClickMethod(e){ 
	if(getElement('MAIN_FLAG').value=="\u662f")
		getElement('MAIN_FLAG_CODE').checked=true;
	else 
		getElement('MAIN_FLAG_CODE').checked=false;
}
	
function changeMainflag() {   
    var main_flag = $("#MAIN_FLAG_CODE").attr("checked");
    if(main_flag == true){
    	$("#MAIN_FLAG_CODE").val("1");
       	$("#MAIN_FLAG").val("\u662f");
    }else{
       	$("#MAIN_FLAG_CODE").val("0");
       	$("#MAIN_FLAG").val("\u5426")
    } 
}	

function diffMemSn(fsn) {    
	var mensn = $("#MEMSN").val();
    if(fsn == mensn){
    	MessageBox.alert("","\u4e3b\u53f7\u4e0d\u80fd\u4f5c\u4e3a\u526f\u53f7\u6dfb\u52a0\uff01");
    	return true;
    }
    return false;
}	

function createZyht() { 
	var table = $.table.get("ZyhtTable");
	var serialNumber = $("#SERIAL_NUMBER").val();
	var main_flag_code = $("#MAIN_FLAG_CODE").attr("checked");
	var meb_eparchy_code = $("#MEB_EPARCHY_CODE").val();
	var meb_serial_number = $("#MEB_SERIAL_NUMBER").val();
	var meb_user_id = $("#MEB_USER_ID").val();
	
    if (serialNumber == ""){
		alert ("\u8bf7\u8f93\u5165\u53f7\u7801\uff01");
		$("#SERIAL_NUMBER").focus();
		return false;
	}   
	if(diffMemSn(serialNumber)){
		return false;
	} 
	
	var ds = table.getTableData(null,true); //获得表格的数据		
	if(ds.length == 1){            
		alert ("\u4e3b\u53eb\u4e00\u53f7\u901a\u6700\u591a\u53ea\u80fd\u7ed1\u0031\u4e2a\u526f\u53f7\u7801\uff01");
		return false;
	}  
	if(main_flag_code){
		for(var i=0; i<ds.length; i++){
			var valueColumn = ds.get(i);
			if(valueColumn.get('MAIN_FLAG_CODE') == '1'){
				alert ("\u4e3b\u663e\u53f7\u7801\u53ea\u80fd\u6709\u4e00\u4e2a\uff01");
				return false;
			}  
		}
	}
	var param = '&SERIAL_NUMBER='+serialNumber+'&MEB_SERIAL_NUMBER='+meb_serial_number+'&MEB_EPARCHY_CODE='+meb_eparchy_code+'&MEB_USER_ID='+meb_user_id+'&METHOD_NAME=querySerialnumberInfo&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.yht.MemParamInfo';
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.group.param.ProxyParam','productParamInvoker',param, dealzyhtUserId,err);	
}

function dealzyhtUserId(data) {
	//获取编辑区的数据
	var zyhtData = $.ajax.buildJsonData("ZEditPart");
	var table = $.table.get("ZyhtTable");
	var zuser_id = data.get("ZUSER_ID");
	var serialNumber = $("#SERIAL_NUMBER").val();
	
    if (zuser_id!='' && zuser_id != null) {
        $("#ZUSERID").val(zuser_id);
    }  
    
    if(table.isPrimary('SERIAL_NUMBER', zyhtData)){
		MessageBox.alert("","关键字段[号码]已存在同样的值["+serialNumber+"]");				
		$("#SERIAL_NUMBER").focus();
		return false; 
	} 

	zyhtData["SERIAL_NUMBER"] = serialNumber;
	zyhtData["ZUSERID"] = zuser_id;   
	
	//往表格里添加一行并将编辑区数据绑定上		
	table.addRow(zyhtData);
	
	var ds = table.getTableData();
	$("#pam_zyht").val(ds.toString());		
}

function err(error_code,error_info,derror)
{
	showDetailErrorInfo(error_code,error_info,derror);
}

function ztableRowClick() {
	var rowData = $.table.get("ZyhtTable").getRowData();
	$("#SERIAL_NUMBER").val(rowData.get("SERIAL_NUMBER"));
}

function ztableRowDBClick() {
	var rowData = $.table.get("ZyhtTable").getRowData();
	$("#SERIAL_NUMBER").val(rowData.get("SERIAL_NUMBER"));
}

function updateZyht() { 
	//获取编辑区的数据
	var table = $.table.get("ZyhtTable");
	var serialNumber = $("#SERIAL_NUMBER").val();
	var main_flag_code = $("#MAIN_FLAG_CODE").attr("checked");
	var meb_eparchy_code = $("#MEB_EPARCHY_CODE").val();
	var meb_serial_number = $("#MEB_SERIAL_NUMBER").val();
	var meb_user_id = $("#MEB_USER_ID").val();
		
    if (serialNumber == ""){
		alert ("\u8bf7\u8f93\u5165\u53f7\u7801\uff01");
		return false;
	}
	if(diffMemSn(serialNumber)){
		return false;
	}
	var ds = table.getTableData(null,true);  //获得表格的数据		
	
	if(main_flag_code){
		for(var i=0; i<ds.length; i++){
			var valueColumn = ds.get(i);
			if(valueColumn.get('MAIN_FLAG_CODE') == '1'){
				alert ("\u4e3b\u663e\u53f7\u7801\u53ea\u80fd\u6709\u4e00\u4e2a\uff01");
				return false;
			}  
		}
	}
	var param = '&SERIAL_NUMBER='+serialNumber+'&MEB_SERIAL_NUMBER='+meb_serial_number+'&MEB_EPARCHY_CODE='+meb_eparchy_code+'&MEB_USER_ID='+meb_user_id+'&METHOD_NAME=querySerialnumberInfo&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.yht.MemParamInfo';
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.group.param.ProxyParam','productParamInvoker',param, updatezyhtUserId,err);	
}

function updatezyhtUserId(data) { 
	changeMainflag(); 
	
	//获取编辑区的数据
	var zyhtData = $.ajax.buildJsonData("ZEditPart");
	var table = $.table.get("ZyhtTable");
	var serialNumber = $("#SERIAL_NUMBER").val();
	
	var zuser_id = data.get("ZUSER_ID");
    if (zuser_id!='' && zuser_id != null) {
        $("#ZUSERID").val(zuser_id);
    }
    
	if(table.isPrimary('SERIAL_NUMBER', zyhtData)){
		MessageBox.alert("","关键字段[号码]已存在同样的值["+serialNumber+"]");				
		$("#SERIAL_NUMBER").focus();
		return false; 
	} 
	
	zyhtData["SERIAL_NUMBER"] = serialNumber;
	zyhtData["ZUSERID"] = zuser_id;   
	
	//往表格里添加一行并将编辑区数据绑定上		
	table.updateRow(zyhtData);
	
	var ds = table.getTableData();
	$("#pam_zyht").val(ds.toString());	  
}

function deleteZyht() {
	var table = $.table.get("ZyhtTable");
	if(!$.validate.verifyAll('ZyhtTable')) return false; 
	table.deleteRow();
    var ds = table.getTableData();
	$("#pam_zyht").val(''+ds.toString()); 
}

function myOnClickMethod2(e){ 
	 
	if(getElement('pam_Z_TAG_VAL').value=="0"){
		getElements('Z_TAG')[0].checked=true;
	}else{
		getElements('Z_TAG')[1].checked=true;
	}
}

function changeZflag() {   
   	var codeCtl = $("#Z_TAG").attr("checked");
    if(codeCtl){  
    	$("#pam_Z_TAG_VAL").val("1");
    	$("#pam_Z_TAG_NAME").val("\u987a\u632f");
    }else{   
       	$("#pam_Z_TAG_VAL").val("0");
    	$("#pam_Z_TAG_NAME").val("\u540c\u632f");
    }     
}	

function createByht() {  	
	var table = $.table.get("ByhtTable");
	var meb_eparchy_code = $("#MEB_EPARCHY_CODE").val();
	var meb_serial_number = $("#MEB_SERIAL_NUMBER").val();
	var serialNumber = $("#BSERIAL_NUMBER").val();
    if (serialNumber == ""){
		alert ("\u8bf7\u8f93\u5165\u53f7\u7801\uff01");
		return false;
	} 
	if(diffMemSn(serialNumber)){
		return false;
	}
	var ds = table.getTableData(null,true); //获得表格的数据		
	if(ds.length == 3){
		alert ("\u88ab\u53eb\u4e00\u53f7\u901a\u6700\u591a\u53ea\u80fd\u7ed1\u0033\u4e2a\u526f\u53f7\u7801\uff01\u000d\u000a");
		return false;
	}
	 
	var param = '&BSERIAL_NUMBER='+serialNumber+'&MEB_SERIAL_NUMBER='+meb_serial_number+'&MEB_EPARCHY_CODE='+meb_eparchy_code+'&METHOD_NAME=queryBSerialnumberInfo&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.yht.MemParamInfo';
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.group.param.ProxyParam','productParamInvoker',param, dealbyhtUserId,err);	
}

function dealbyhtUserId(data) {
	//获取编辑区的数据
	var byhtData = $.ajax.buildJsonData("BEditPart");
	var table = $.table.get("ByhtTable");
	var serialNumber = $("#BSERIAL_NUMBER").val();
	
	var buser_id = data.get("BUSER_ID");
    if (buser_id!='' && buser_id != null) {
        $("#BUSERID").val(buser_id);
    }
    
   // var buser_id2 = $("#BUSERID").val();
	
	if(table.isPrimary('BSERIAL_NUMBER', byhtData)){
		MessageBox.alert("","关键字段[号码]已存在同样的值["+serialNumber+"]");				
		$("#BSERIAL_NUMBER").focus();
		return false; 
	} 
	
	byhtData["BSERIAL_NUMBER"] = serialNumber;
	byhtData["BUSERID"] = buser_id;   
	
	//往表格里添加一行并将编辑区数据绑定上		
	table.addRow(byhtData);
	
	var ds = table.getTableData();
	$("#pam_byht").val(ds.toString());	  
}

function btableRowClick() {
	var rowData = $.table.get("ByhtTable").getRowData();
	$("#BSERIAL_NUMBER").val('');
}

function btableRowDBClick() {
	var rowData = $.table.get("ByhtTable").getRowData();
	$("#BSERIAL_NUMBER").val('');
}

function updateByht() {  
	var meb_eparchy_code = $("#MEB_EPARCHY_CODE").val();
	var meb_serial_number = $("#MEB_SERIAL_NUMBER").val();
    var serialNumber = $("#BSERIAL_NUMBER").val(); 
    if (serialNumber == ""){
		alert ("\u8bf7\u8f93\u5165\u53f7\u7801\uff01");
		return false;
	}  
	if(diffMemSn(serialNumber)){
		return false;
	}
	var param = '&BSERIAL_NUMBER='+serialNumber+'&MEB_SERIAL_NUMBER='+meb_serial_number+'&MEB_EPARCHY_CODE='+meb_eparchy_code+'&METHOD_NAME=queryBSerialnumberInfo&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.yht.MemParamInfo';
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.group.param.ProxyParam','productParamInvoker',param, updatebyhtUserId,err);	
}

function updatebyhtUserId(data) {	
	//获取编辑区的数据
	var byhtData = $.ajax.buildJsonData("BEditPart");
	var table = $.table.get("ByhtTable");
	var serialNumber = $("#BSERIAL_NUMBER").val();
	
	var buser_id = data.get("BUSER_ID");
    if (buser_id!='' && buser_id != null) {
        $("#BUSERID").val(buser_id);
    }
    
	if(table.isPrimary('BSERIAL_NUMBER', byhtData)){
		MessageBox.alert("","关键字段[号码]已存在同样的值["+serialNumber+"]");				
		$("#BSERIAL_NUMBER").focus();
		return false; 
	} 
	
	byhtData["BSERIAL_NUMBER"] = serialNumber;
	byhtData["BUSERID"] = buser_id;   
	
	//往表格里添加一行并将编辑区数据绑定上		
	table.updateRow(byhtData);
	
	var ds = table.getTableData();
	$("#pam_byht").val(ds.toString());	  
}

function deleteByht() {
	var table = $.table.get("ByhtTable");
	if(!$.validate.verifyAll('ByhtTable')) return false; 
	table.deleteRow();
    var ds = table.getTableData();
	$("#pam_byht").val(''+ds.toString()); 
}