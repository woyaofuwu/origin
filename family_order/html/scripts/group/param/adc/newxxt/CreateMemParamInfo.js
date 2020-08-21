/**
 * 作用：用来初始化页面的显示
 */
function init()
{

}

function setMemParamInfo(e)
{
	var tempElements = selectedElements.selectedEls;
	//alert("======tempElements=>>>>>"+tempElements);

	//pam_NOTIN_STU_PARAM_LIST+i 把页面上的KEY 传给子页面用，子页面通过$.getSrcWindow().$("#"+pam_NOTIN_STU_PARAM_LISTi).val()取父页面的值
	var stuParamkey = $(e).attr("paramlistname");

	var stuParamStr = $('#'+stuParamkey).val();
	var stuParamList = $.DatasetList(stuParamStr);

	var rowindex = $(e).attr("rowindex");
	var mainSn = $("#pam_NOTIN_SERIAL_NUMBER").val();
	var outsn = $('#'+'pam_NOTIN_OUT_SN'+rowindex).val();
	var groupId= $("#pam_GROUP_ID").val();
	var ecUserId=$("#pam_GRP_USER_ID").val();

	// 校验家长号码
	var isMod = g_IsMobileNumber(outsn);
	if (isMod == false) {
		alert($('#'+'pam_NOTIN_OUT_SN'+rowindex).attr('desc') + "格式不正确，请输入正确的手机号码");
		return ;
	}

	var  paramset = $.DatasetList();

	for (var i = 0; i < tempElements.length; i++)
	{
		var disdata = $.DataMap();

	    if(tempElements.get(i, "ELEMENT_TYPE_CODE")=="D")
	    {
	    	disdata.put("ELEMENT_ID",tempElements.get(i, "ELEMENT_ID"));
	    	disdata.put("MODIFY_TAG",tempElements.get(i, "MODIFY_TAG"));
	    	disdata.put("ELEMENT_TYPE_CODE","D");

	    	paramset.add(disdata);
	    }

	}

	if (!paramset)
	{
	    alert("请先在产品信息页面，选择和校园学生优惠套餐！");
		return ;
	}
	//var param = '&DISCNTSET='+paramset+'&STUPARAMLIST='+encodeURIComponent(stuParamList)+'&stuParamkey='+stuParamkey;
	var param = '&DISCNTSET='+paramset+'&stuParamkey='+stuParamkey+"&NOTIN_SERIAL_NUMBER="+mainSn+"&NOTIN_OUT_SN="+outsn+"&GRP_USER_ID="+ecUserId+"&GROUP_ID="+groupId;

	$.popupPage('group.param.adc.newxxt.CreateParamInfo','initParamInfo', param, '和校园成员参数信息','800','600');
}


//控制Checkbo选中时 字段不能为空
function validateCheckbox(e)
{
	if($(e).attr('checked'))
	{
		$('#'+'pam_NOTIN_OUT_SN'+$(e).attr('indexvalue')).attr('nullable','no')
		$('#'+'pam_NOTIN_STU_PARAM_LIST'+$(e).attr('indexvalue')).attr('nullable','no')
	}
	else
	{
		$('#'+'pam_NOTIN_OUT_SN'+$(e).attr('indexvalue')).attr('nullable','yes')
		$('#'+'pam_NOTIN_STU_PARAM_LIST'+$(e).attr('indexvalue')).attr('nullable','yes')
	}
}

//点击下一步的校验
function validateParamPage()
{
	//校验，资费不允许在产品信息页面取消
	if(!validateDiscntPage())
	{
		alert("资费还有学生关联，请通过和校园成员参数页面退订资费！");
		return false;
	}

	//校验异网号码的操作
	var mainSn = $("#pam_NOTIN_SERIAL_NUMBER").val();
	var checkBoxList = $("#stuParamlistPart input:checked");
	var count = 0 ;
	checkBoxList.each(function()
	{
		var checkflag = $(this).val();

		if("on"==checkflag)
		{
			var stuDisParam = $('#'+'pam_NOTIN_STU_PARAM_LIST'+$(this).attr('indexvalue')).val();
			if(stuDisParam==""||stuDisParam==null||stuDisParam=='[]'||stuDisParam=={})
			{
				alert("至少添加一个学生参数信息！");
				return false;
			}

			// 校验家长号码
			var isMod = g_IsMobileNumber($('#'+'pam_NOTIN_OUT_SN'+$(this).attr('indexvalue')).val());
			if (isMod == false) {
				alert($('#'+'pam_NOTIN_OUT_SN'+$(this).attr('indexvalue')).attr('desc') + "格式不正确，请输入正确的手机号码");
				return false;
			}
		  count++;
		}

	});
	if(count>1)
	{
		alert("一次只能选择一个异网号操作！");
		return false;
	}
	if(count<1)
	{
		alert("请选择一条异网号操作！");
		return false;
	}
	return true;
}

//校验资费不允许在产品信息页面取消
function validateDiscntPage()
{
	var submitData = selectedElements.getSubmitData();
	for (var i = 0; i < submitData.length; i++)
	{
		if(submitData.get(i, "MODIFY_TAG")=="1" && submitData.get(i, "ELEMENT_ID")!="5911" && submitData.get(i, "ELEMENT_ID")!="5912")
	 	 {
	 		return false;
	 		break;
	 	 }

	}
	return true;
}

/*
 * 校验是否为手机号码
 */
function g_IsMobileNumber(s) {
	if (s == null || s == '') {
		return false;
	}
	if (s.length != 11 || !g_IsDigit(s)) {
		return false;
	}

	if (!checkMobileNumber(s)) {
		return false;
	}

	return true;
}

function checkMobileNumber(data) {
	var flag = false;
	var param = '&SERIAL_NUMBER='+data+'&METHOD_NAME=checkMobileNumber&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.adc.newxxt.CreateMemParamInfo';
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',param,
   	function(data){
   		if(data != null){
   			var rspflag = data.get("IS_FLAG");
   			if (rspflag == "true") {
   				flag = true;
   			} else {
   				flag = false;
   			}

		}
    },
    function(error_code,error_info,derror){
   		showDetailErrorInfo(error_code,error_info,derror);
    },
    {async:false});

    return flag;
}

/*
 * 校验是否为数字类型
 */
function g_IsDigit(s) {
	if (s == null) {
		return false;
	}
	if (s == '') {
		return true;
	}
	s = '' + s;
	if (s.substring(0, 1) == '-' && s.length > 1) {
		s = s.substring(1, s.length);
	}
	var patrn = /^[0-9]*$/;
	if (!patrn.exec(s)) {
		return false;
	}
	return true
}
