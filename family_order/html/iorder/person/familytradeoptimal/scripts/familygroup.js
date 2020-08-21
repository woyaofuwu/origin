$(document).ready(function(){
	initValue();
});

function initValue()
{
	var tag =$("#HID_TAG").val();
	if(tag ==1){
    	$("#FamilyInfopart").css("display", "");
    	$("#LableInfopart").css("display", "");

	}else if(tag==2){
		$("#FamilyInfopart").css("display", "none");
    	$("#LableInfopart").css("display", "");
	}

}


/**
 * boss 查询接口
 */
function bossgroupquery(){
	var business = $("#BUSINESS_TYPE").val();
	var bizverson =$("#BIZ_VERSION").val();
	var productofferingid =$("#PRODUCT_OFFERING_ID").val();
	var custphone = $("#CUSTOMER_PHONE").val();
	var memberNumber =  $("#MEM_NUMBER").val();
	if(business == ''){
		MessageBox.alert("信息提示","业务类型为空！");
		return false;
	}
	if(bizverson==''){
		MessageBox.alert("信息提示","业务版本为空！");
		return false;
	}
	if(productofferingid ==''&& custphone== ''&& memberNumber==''){
		MessageBox.alert("信息提示","业务订购实例ID、客户标识、成员号码至少有一个是必填！");
		return false;
	}
	//将页面变更所需的字段存在隐藏域里，群组信息变更时的必填入参
	$("#HID_BUSINESS_TYPE").val(business);
	$("#HID_MEM_NUMBER").val(memberNumber);

	var param = '&BUSINESS_TYPE='+$("#BUSINESS_TYPE").val()+'&PRODUCT_OFFERING_ID='+$("#PRODUCT_OFFERING_ID").val()+'&PRODUCT_CODE='+$("#PRODUCT_CODE").val()+'&CUSTOMER_PHONE='+$("#CUSTOMER_PHONE").val()+'&MEM_TYPE='+$("#MEM_TYPE").val()
	+'&MEM_AREA_CODE='+$("#MEM_AREA_CODE").val()+'&MEM_NUMBER='+$("#MEM_NUMBER").val()+'&BIZ_VERSION='+$("#BIZ_VERSION").val();	
	$.beginPageLoading("查询中......");
	$.ajax.submit('', 'bossGroupInfo', param, 'BossQueryInfoPart,ShowResultpart', function(data) {
		var retCode = data.get("RSP_CODE");
		var retDesc = data.get("RSP_DESC");
		$.endPageLoading();
		if(retCode!="00"){
			$.endPageLoading();
			MessageBox.alert("提示信息",retDesc);
		}
	},function(error_code,error_info,derror){
		$.endPageLoading();
		MessageBox.error(error_code,error_info,derror);
	});
}

function UpdateInfo()
{

	var productCode = mebData.getAttribute("PRODUCT_CODE");
	var productOfferingId = mebData.getAttribute("PRODUCT_OFFERING_ID");
	var poidCode = mebData.getAttribute("POID_CODE");
	var poidLable =mebData.getAttribute("POID_LABLE");
	var customerPhone = mebData.getAttribute("CUSTOMER_PHONE");
	var business = $("#HID_BUSINESS_TYPE").val();
	var membernumber = 	$("#HID_MEM_NUMBER").val();

	var param = '&PRODUCT_OFFERING_ID='+productOfferingId;
	param += '&PRODUCT_CODE='+productCode;
	param += '&POID_CODE='+poidCode;
	param += '&POID_LABLE='+poidLable;
	param += '&CUSTOMER_PHONE='+customerPhone;
	param += '&BUSINESS_TYPE='+business;
	param += '&MEM_NUMBER='+membernumber;

	popupDialog('群组信息变更', 'familytradeoptimal.UpdateGroupInfo', 'init', param, null, '60', '35', true, null, null);


}
function onTradeSubmit()
{
	var poidCode = $("#POID_CODE").val();
	var poidLable = $("#POID_LABLE").val();
	var memLable = $("#MEM_LABLE").val();
	var oprNumber = $("#HID_MAIN_NUMBER").val();//操作号码
	var memPhone = $("#HID_MEM_NUMBER").val();//副号
	var tag = $("#HID_TAG").val();//标识  1 --主号变更标识  2-- 副号变更标识
	if(tag=='1'){//主号码标识变更  可以变更poidcode poidlable 主号码 memlable
		if(poidCode!=""){
			if(poidCode.length == 2){
				if(poidCode <1 || poidCode >99){
					MessageBox.alert("提示信息","群组编码只能在01-99之间！");
					return false;
				}
			}else{
				MessageBox.alert("提示信息","群组编码只能是两位！");
				return false;
			}
		}
		if(poidCode =="" && poidLable =="" && memLable==""){
			MessageBox.alert("提示信息","群组信息变更至少选择一项变更！");
			return false;
		}
		var param = '&PRODUCT_OFFERING_ID='+$("#HID_PRODUCT_OFFERING_ID").val()+'&PRODUCT_CODE='+$("#HID_PRODUCT_CODE").val()+'&CUSTOMER_PHONE='+$("#HID_CUSTOMER_PHONE").val()+'&OPR_NUMBER='+$("#HID_MAIN_NUMBER").val()+'&POID_CODE='+$("#POID_CODE").val()+'&POID_LABLE='+$("#POID_LABLE").val()+'&MEM_LABLE='+$("#MEM_LABLE").val()+'&MEM_NUMBER='+$("#HID_CUSTOMER_PHONE").val();	
		$.beginPageLoading("变更中，请稍后......");
		$.ajax.submit('', 'updateGroup', param, 'FamilyInfopart', function(data) {	
			$("#HID_TAG").val("1");
			initValue();
			var retCode = data.get("RSP_CODE");
			var retDesc = data.get("RSP_DESC");
			$.endPageLoading();
			if(retCode!="00"){
				$.endPageLoading();
				MessageBox.alert("提示信息",retDesc);
			}else{
				$.endPageLoading();
				MessageBox.success("提示信息","群组信息变更成功!");
			}
		},function(error_code,error_info,derror){
			$.endPageLoading();
			MessageBox.error(error_code,error_info,derror);
		});
	}else{
		if(memLable==""){
			MessageBox.alert("提示信息","变更成员标签时，标签不能为空！！");
			return false;
		}
		var param = '&PRODUCT_OFFERING_ID='+$("#HID_PRODUCT_OFFERING_ID").val()+'&PRODUCT_CODE='+$("#HID_PRODUCT_CODE").val()+'&CUSTOMER_PHONE='+$("#HID_CUSTOMER_PHONE").val()+
		'&MEM_NUMBER='+$("#HID_MEM_NUMBER").val()+'&OPR_NUMBER='+$("#HID_MAIN_NUMBER").val()+'&POID_CODE='+$("#POID_CODE").val()+'&POID_LABLE='+$("#POID_LABLE").val()+'&MEM_LABLE='+$("#MEM_LABLE").val();	
		$.beginPageLoading("变更中，请稍后......");
		$.ajax.submit('', 'updateGroup', param, 'FamilyInfopart', function(data) {	
			$("#HID_TAG").val("2");
			initValue();
			var retCode = data.get("RSP_CODE");
			var retDesc = data.get("RSP_DESC");
			$.endPageLoading();
			if(retCode!="00"){
				$.endPageLoading();
				MessageBox.alert("提示信息",retDesc);
			}else{
				$.endPageLoading();
				MessageBox.success("提示信息","群组信息变更成功!");
			}
		},function(error_code,error_info,derror){
			$.endPageLoading();
			MessageBox.error(error_code,error_info,derror);
		});
	}
}



//点击table中的内容获取成员信息
function clickRow()
{	
	var rowData = $.table.get("viceInfoTable").getRowData();
	$('#MEB_SERIAL_NUMBER').val(rowData.get('SERIAL_NUMBER_B'));

}
