/**
 * 作用：用来初始化页面的显示
 */
function init()
{

}

//点击下一步的校验
function validateParamPage()
{
    var famSn = $("#pam_NOTIN_FAM_SN").val();
    var grpUserId = $("#GRP_USER_ID").val();
    var groupId = $("#GROUP_ID").val();
    var mainNum = $("#MEB_SERIAL_NUMBER").val();

	var submitData = selectedElements.getSubmitData();
	var falg = false;
	var checkExists = false;

	for (var i = 0; i < submitData.length; i++)
	{
		if(submitData.get(i, "MODIFY_TAG")=="0" && submitData.get(i, "ELEMENT_ID")=="32000001"&& submitData.get(i, "ELEMENT_TYPE_CODE")=="D" )
	 	 {
	 		falg = true;
	 		break;
	 	 }

	}
	if(falg==true)
	{
	    if(""==famSn||null==famSn)
	    {
	    	alert("请添写家长号码！");
			return false;
	    }
		var isXxt = validateFamSn(famSn,grpUserId);//true 订购过和校园10套餐，false 没有订购过
		if(false==isXxt||"false"==isXxt)
		{
			alert("要订购学护卡15元包,家长号：【"+ famSn +"】必须先订购和校园10元套餐或者8元套餐 ！");
			return false;
		}
	}

	// 100036
	// 订购学护卡服务时先发起正向学护卡成员业务办理校验接口
	for (var i = 0; i < submitData.length; i++)
	{
		if(submitData.get(i, "MODIFY_TAG")=="0" && submitData.get(i, "ELEMENT_ID")=="574401"&& submitData.get(i, "ELEMENT_TYPE_CODE")=="S" )
	 	 {
			checkExists = true;
	 		break;
	 	 }

		if (submitData.get(i, "ELEMENT_ID")=="574401" && null!=famSn && ""!=famSn) {
			alert("校星卡服务不能填写家长号码！");
			return false;
		}

	}
//	if (checkExists==true) {
//		if(""==famSn||null==famSn)
//	    {
//	    	alert("家长号码不能为空，请填写！");
//			return false;
//	    }
//
//		var isExistsFamsn = validateFamSnIsExists(mainNum, submitData, groupId);
//
//		if(false==isExistsFamsn||"false"==isExistsFamsn)
//		{
//			alert("该号码尚未在学护卡平台录入资料，不能订购该业务 ！");
//			return false;
//		}
//	}

	return true;
}

function validateFamSn(famSn,grpUserId)
{
        var isXxt = false;
		var param = '&FAM_SN='+famSn+'&METHOD_NAME=getFamSnXxtDisByajax&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.adc.xfk.MebParamInfo';
		Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',param,
	   	function(data){
	   		if(data != null){
				isXxt = getFamSnXxtDisByajax(data);//true 订购过和校园10套餐，false 没有订购过
			}
	    },
	    function(error_code,error_info,derror){
	   		showDetailErrorInfo(error_code,error_info,derror);
	    },
	    {async:false});

	    return isXxt;

}

//校验资费不允许在产品信息页面取消
function getFamSnXxtDisByajax(data)
{
	var isXxt = data.get("isXxtDisFlag");
    return isXxt
}

function validateFamSnIsExists(famSn,submitData, groupId)
{
        var isExists = false;
		var param = '&GROUP_ID='+groupId+'&FAM_SN='+famSn+'&SELECTED_ELEMENT='+submitData+'&METHOD_NAME=QryXfkNumIsExists&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.adc.xfk.MebParamInfo';
		Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',param,
	   	function(data){
	   		if(data != null){
	   			isExists = getFamSnIsExistsByajax(data);//true 订购过和校园10套餐，false 没有订购过
			}
	    },
	    function(error_code,error_info,derror){
	   		showDetailErrorInfo(error_code,error_info,derror);
	    },
	    {async:false});

	    return isExists;

}

//校验资费不允许在产品信息页面取消
function getFamSnIsExistsByajax(data)
{
	var isExists = data.get("isExistsFlag");
    return isExists
}

