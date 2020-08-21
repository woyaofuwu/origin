function initProductInfo() {
	var effectNow = $("#EFFECT_NOW").attr("checked");
	if (effectNow == true) {
		selectedElements.isEffectNow = effectNow;
	}
}

function refreshProductInfoArea(data) {
	insertGroupCustInfo(data);
	var custId = $('#CUST_ID').val();
	$.beginPageLoading('正在查询集团已订购的产品列表...');
	$.ajax.submit('CondGroupPart','queryGroupOrderProduct','&CUST_ID='+custId,'productInfoArea,ProductPackagePart',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}


function refreshProduct(){
	var productVal = $("#userProductInfo").val();
	if("" == productVal || null == productVal || undefined == productVal) {
		alert('请选择集团产品!');
		return false;
	}else {
		var productNameStr = $("#userProductInfo :selected").text();
		var productNameArry = productNameStr.split("|");
		setHiddenData(productNameArry);
		
		var productId = $("#PRODUCT_ID").val();
		if("10009805" == productId)
		{
			$("#LI_IS_SEC").attr("style", "");
			
		}
		else
		{
			$("#LI_IS_SEC").attr("style", "display:none");
			
		}
		var userId = $("#GRP_USER_ID").val();
		var groupId = $('#POP_cond_GROUP_ID').val();
		$.beginPageLoading('正在查询产品包元素中...');
		$.ajax.submit(this,'refreshProduct','&PRODUCT_ID='+productId+'&USER_ID='+userId+'&GROUP_ID='+groupId,'ProductPackagePart,GroupUserPart',function(data){
			$.endPageLoading();
		},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
	}
}


function setHiddenData(Arry) {
	$("#PRODUCT_ID").val(Arry[0]);
	$("#GRP_PRODUCT_NAME").val(Arry[1]);
	$("#GRP_SN").val(Arry[2]);
	$("#GRP_USER_ID").val(Arry[3]);
}

function pkgListAfterSelectAction(package) {
	var selfParam =  "&GRP_USER_ID=" + $('#GRP_USER_ID').val() + "&PRODUCT_ID=" + $('#PRODUCT_ID').val();
	var eparchyCode;
	pkgElementList.renderComponent(package,eparchyCode,selfParam);
}
/**产品拼串*/
function setData(obj){
	if(!$.validate.verifyAll("scrollPart")) {
		return false;
	}
		
	var tempElements = selectedElements.getSubmitData();
	
	var svcElements = selectedElements.selectedEls;

	for (var i=0;i<svcElements.length ;i++ )
   {
  	 if(svcElements.get(i,"ATTR_PARAM_TYPE")=='9'&&svcElements.get(i,"ATTR_PARAM").get(0,"PARAM_VERIFY_SUCC")=='false')
	 {
		alert("(集团服务开通)缺少服务参数，请补全相关服务参数信息！"); 
		return false ; 
      }
   } 
	$("#SELECTED_ELEMENTS").val(tempElements);
	
	var grpProductId = $("#PRODUCT_ID").val();
	var flag = true;
	$.ajax.submit('GroupUserPart', 'checkMustDiscnt','&GRPPRODUCTID='+grpProductId, null,function(data){
			$.endPageLoading();
			verifyBySelectDiscnt(data);
		},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		flag = false;
		},{async:false});
	return flag;	
}



function verifyBySelectDiscnt(data) {
	var checkresult = data.get('checkresult');
	if(""==checkresult || null==checkresult) {
		commSubmit();
	}else {
		$.showWarnMessage('未选择必选包元素!',checkresult);
		return false;
	}
}

function commSubmit() {
	var group_id = $("#GROUP_ID").val();
	var effectNow = $("#userInfoEffectNow").val();
	var productName = $("#GRP_PRODUCT_NAME").val();
	var grpProductId = $("#PRODUCT_ID").val();
	var grpUserID = $("#GRP_USER_ID").val();
	var elements = $("#SELECTED_ELEMENTS").val();
	var cust_id = $("#CUST_ID").val();
	var oper_code = $("#OPERCODE").val();
	var grp_sn = $('#GRP_SN').val();
	var is_sec = $('#IS_SEC').val();
	
	if ( elements == "" ) 
		return false;    
	var idata = $.DatasetList(elements);

	var info = $.DataMap();
	
	info.put("SELECTED_ELEMENTS", idata);
	
	info.put("GROUP_ID", group_id);
	info.put("PRODUCT_ID",grpProductId);
	info.put("USER_ID", grpUserID);
	info.put("CUST_ID", cust_id);
	info.put("OPER_CODE", oper_code);
	info.put('GRP_SERIAL_NUMBER', grp_sn);
	if("10009805"==grpProductId)
	{
		info.put('PAGE_SELECTED_TC', is_sec);
	}
	
	//$.setReturnValue({'POP_CODING_STR':productName},false);
	//$.setReturnValue({'CODING_STR':info},true);
	
	parent.$('#POP_CODING_STR').val(productName);
	parent.$('#CODING_STR').val(info);
 	
	parent.hiddenPopupPageGrp();
}

function errorAction() {
	clearGroupCustInfo();
}

function addChoiceArea (str) {
	$('#'+str).click();
}

function getOtherParam()
{
    var grpUserId=$("#GRP_USER_ID").val();
    var grpProductId=$("#PRODUCT_ID").val();
    var mebProductId=$("#MEB_PRODUCT_ID").val();
    
    var param="&GRP_USER_ID="+grpUserId+"&GRP_PRODUCT_ID="+grpProductId+"&PRODUCT_ID="+mebProductId;
    return param;
} 

/**
 * 设置ADCMAS弹出的服务参数页面URL值
 * 
 */
(function(){$.extend(SelectedElements.prototype,{
	buildPopupAttrParam: function(data){
	        var eparchyCode=$("#MEB_EPARCHY_CODE").val();
	        var param="&MEB_EPARCHY_CODE="+eparchyCode;
	        return param;
	       }
	});
})();
