function submitCheck(obj) { 
	var serialNumber = $("#SERIAL_NUMBER").val();
	var staffId = $("#STAFF_ID").val();
	var psptId = $("#PSPT_ID").val();
	var psptType = $("#PSPT_TYPE_CODE").val();
	if(""==serialNumber){
		alert('服务号码不能为空！');
		return false;
	}
	if(""==staffId){
		alert('请输入工单编号！');
		return false;
	}
	if(""==serialNumber){
		alert('证件号码不能为空！');
		return false;
	}
	//这里进行证件类型校验，就不用在后台校验，后续修改只需要修改js就可以，不需重启服务。
	if("012AIHONP".indexOf(psptType) != -1){
		 $("#IS_UNIT_PSPT_TYPE").val('0');
	}else if("MEDGL".indexOf(psptType) != -1){
		$("#IS_UNIT_PSPT_TYPE").val('1');
	}else{
		alert('证件类型暂未定义！');
		return false;
	}
	return true;
}


function getSubSer(){
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('AuthPart,followSerialNumber','querySubSerialNumber', null, 'serialB', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
/**
 * 客户扫描读取身份证信息
 * @author zhuoyingzhi
 * @date 20170906
 */
function clickScanPspt(){
	
	getMsgByEForm("custInfo_PSPT_ID",null,null,null,null,null,null,null);
	//this.verifyIdCardName(fieldName);
}
