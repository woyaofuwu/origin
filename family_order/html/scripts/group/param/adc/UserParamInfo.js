/**
 * 作用：用来初始化页面的显示,值会在productInfo.java里查出来后隐藏在各个服务的属性隐藏中
 * 		如果没有查出来的话，再调用getServiceParamsByajax查一把
 */

function loadUserParamInfo(){
	var prodcutId ="";
	var packageId ="";
	var param = "";
	var svcparamvalue ="";
	var userId ="";
	var groupId = $("#GROUP_ID").val();
	var custId = $("#CUST_ID").val();
	var urlParts = document.URL.split("?");
	var parameterParts = urlParts[1].split("&");
	var grpUserEparchyCode = $("#GRP_USER_EPARCHYCODE").val();

	try
	  {
	  	  userId=$.getSrcWindow().$("#USER_ID").val();//从父页面获取userId
	  	  if(userId==undefined)
	  	  {
	  	  	userId="";
	  	  }
	  }
	  catch(e)
	  {
		  userId="";
	  }

	try
	  {
	  	  svcparamvalue=$.getSrcWindow().selectedElements.getAttrs($("#ITEM_INDEX").val());
	  	  if(svcparamvalue.get(0,"PARAM_VERIFY_SUCC")==undefined)
	  	  {
	  	  	svcparamvalue="";
	  	  }
	  }
	  catch(e)
	  {
		  svcparamvalue="";
	  }


	for (i = 0; i < parameterParts.length; i++)
	{
		var pairParts = parameterParts[i].split("=");
		var pairName = pairParts[0];
		var pairValue = pairParts[1];

		if(pairName=="PRODUCT_ID")
		{
		      prodcutId=pairValue;
		}
		if(pairName=="PACKAGE_ID")
		{
		      packageId=pairValue;

		}
		if(pairName=="ELEMENT_ID")
		{
		      serviceId=pairValue;
		}

	}
	 //add by lijie9, 2011-5-16 for esop 接口调用参数准备
	var eos = $.DatasetList($.getSrcWindow().$("#EOS").val());//从父页面获取EOS数据

	var ibsysid = "";
	var subIbsysid = "";
	var nodeId = "";
	if(eos && eos != "" && eos != "[]")
	{
	    var eosData = eos.get(0);
		ibsysid = eosData.get("IBSYSID");
		subIbsysid = eosData.get("SUB_IBSYSID");
		nodeId = eosData.get("NODE_ID");
		if(nodeId!="bossChange"){
			$("#servpart").css("display","block");
		}
	}

   //esop end
	if(svcparamvalue==""||svcparamvalue=="[]")//没有找到已存在的参数值 采用ajax异步调用 从数据库查询
	{
	     //ESOP接入
	   	if(eos && eos != "" && eos != "[]")
	   	{
            param="&CUST_ID="+custId+"&GROUP_ID="+groupId+'&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+'&EPARCHY_CODE='+grpUserEparchyCode+'&IBSYSID='+ibsysid+'&SUB_IBSYSID='+subIbsysid+'&NODE_ID='+nodeId;
			$.ajax.submit('this', 'getServiceParamsByajax',param ,null, function(data){	putpagedata(data,serviceId);$.endPageLoading();	},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    },{async:false});
	    }
	   //非ESOP接入
	   else{

	   	   if(userId != undefined && userId != "")//userId不为空 集团产品变更
			{
				param="&CUST_ID="+custId+"&GROUP_ID="+groupId+'&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+"&USER_ID="+userId+'&EPARCHY_CODE='+grpUserEparchyCode;
			}
			else//userId为空 集团产品受理
			{
				param="&CUST_ID="+custId+"&GROUP_ID="+groupId+'&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+'&EPARCHY_CODE='+grpUserEparchyCode;
			}
			$.ajax.submit('this', 'getServiceParamsByajax',param ,null, function(data){	putpagedata(data,serviceId);$.endPageLoading();	},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    },{async:false});
		}

	}

	else //找到已存在的参数值
	{
		var datasetsize=svcparamvalue.getCount();
		if(datasetsize<=1||(eos != "" && eos != "[]")) //表示没有服务的详细参数信息(因为约定第一条记录为是否需要校验,第二条记录才是具体的参数信息)
		{
		     //ESOP接入
		   	if(eos && eos != "" && eos != "[]")
			{
		    	if(userId != undefined && userId != "")
				{
					param="&CUST_ID="+custId+"&GROUP_ID="+groupId+'&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+"&USER_ID="+userId+'&EPARCHY_CODE='+grpUserEparchyCode+'&IBSYSID='+ibsysid+'&SUB_IBSYSID='+subIbsysid+'&NODE_ID='+nodeId;
				}
				else
				{
					param="&CUST_ID="+custId+"&GROUP_ID="+groupId+'&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+'&EPARCHY_CODE='+grpUserEparchyCode+'&IBSYSID='+ibsysid+'&SUB_IBSYSID='+subIbsysid+'&NODE_ID='+nodeId;
				}

				$.ajax.submit('this', 'getServiceParamsByajax',param ,null, function(data){	putpagedata(data,serviceId);$.endPageLoading();	},
				function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
			    },{async:false});
			}
			   //非ESOP接入
		   else{
				if(userId != undefined && userId != "")
				{
					param="&CUST_ID="+custId+"&GROUP_ID="+groupId+'&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+"&USER_ID="+userId+'&EPARCHY_CODE='+grpUserEparchyCode;
				}
				else
				{
					param="&CUST_ID="+custId+"&GROUP_ID="+groupId+'&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+'&EPARCHY_CODE='+grpUserEparchyCode;
				}
				$.ajax.submit('this', 'getServiceParamsByajax', param, null, function(data){
					putpagedata(data,serviceId);
					$.endPageLoading();
				},
				function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
			    },{async:false});
			}
		}
		else
		{
			putpagedata(svcparamvalue,serviceId);
		}
	}
     setStatetype();	//根据不同的操作类型，页面输入框的可见性.
     checkmodifypriv();
     setDeliverNumDis();// 根据选择的业务属性不同,改变限制下发次数，如果业务类型是3或是4时，需要填写此字段。填写0则没有限制。
};


//点确定按钮返回数据到父页面隐藏字段
function setData(obj)
{

	var ret = ischinese();//中文签名特殊字符检验，邮箱域名
	if (ret == '1')
	{
	   return;
	}

	var itemPre = checkMessageAmount();//判断每月短信量和每日短信量
	if(!itemPre)
	{
	  return;
	}

	if(!$.validate.verifyAll('platsvcparamtable'))
	{
	    return;
	}

	var forbidflag =checkforbidtime();//校验允许下发时间
	if(!forbidflag)
	{
	  return;
	}
	var dealSCFlag= dealServCode();//校验服务代码
	if(!dealSCFlag)
	{
		return;
	}
	var adminFlag = checkAdminExist();/*校验管理员手机号*/
    if(!adminFlag)
	{
		return;
	}
    var canFlag = canresume();//判断用户欠费状态下,不能做恢复操作
   	if(!canFlag)
	{
		return;
	}
    var sigFlag = checkSignExist();//判断中英文签名是否存在敏感资费
    if(!sigFlag)
	{
		return;
	}

    var productId= "";
	var urlParts = document.URL.split("?");
	var parameterParts = urlParts[1].split("&");
	for (i = 0; i < parameterParts.length; i++)
	{
		var pairParts = parameterParts[i].split("=");
		var pairName = pairParts[0];
		var pairValue = pairParts[1];

		if(pairName=="PRODUCT_ID")
		{
			productId=pairValue;
		}
	}

	if (productId == "9230") {
		// 企业短彩信 根据行业类型校验服务代码
	    var servCodeFlag = checkValidServCode();
	    if (!servCodeFlag) {
	    	return;
	    }

	    // 企业短彩信 校验吉祥号码
	    var beautifualFlag = checkIsBeautifual();
	    if (!beautifualFlag) {
	    	return;
	    }
	}

   //获取提交数据
   commSubmit();
}

/**
 * 企业短彩信 根据行业类型校验服务代码
 */
function checkValidServCode() {
	var serCodeEnd = $('#pam_SVR_CODE_END').val();
	var custId = $("#CUST_ID").val();
	if(serCodeEnd == null  && serCodeEnd == "")
	{
		 alert('服务代码尾号为空');
		 $("#pam_SVR_CODE_END").select();
		 return false;
	}
	var servCodeFlag=true;
	var param ='&SVR_CODE_END='+serCodeEnd+'&CUST_ID='+custId;
	$.ajax.submit('this', 'checkValidServCode', param,null,
		function(data){
			servCodeFlag = aftercheckValidServCode(data);
		},
		function(error_code,error_info,derror){
			showDetailErrorInfo(error_code,error_info,derror);
		},
		{async:false});

	return servCodeFlag;
}

function aftercheckValidServCode(data)
{
	var flag=data.get(0,"IS_FLAG");
	if(flag=="false")
	{
		MessageBox.alert("","服务代码尾号代码不属于集团客户的行业类型，请重新输入！");
		$("#pam_SVR_CODE_END").select();//focus();
		return false;
	}
	return true;
}

/**
 * 企业短彩信 校验吉祥号码
 */
function checkIsBeautifual() {
	var serCodeEnd = $('#pam_SVR_CODE_END').val();
	var serCodeHead = $("#pam_SVR_CODE_HEAD").val();
	if(serCodeEnd == null  && serCodeEnd == "")
	{
		 alert('服务代码尾号为空');
		 $("#pam_SVR_CODE_END").select();
		 return false;
	}

	var regu = "(000|111|222|333|444|555|66|777|88|99)$";
	var reg = new RegExp(regu);
	if (!reg.test(serCodeEnd)) {
		return true;
	}

	var beautifualFlag=true;
	var param ='&SVR_CODE='+serCodeHead+serCodeEnd;
	$.ajax.submit('this', 'checkIsBeautifual', param,null,
		function(data){
			beautifualFlag = aftercheckIsBeautifual(data);
		},
		function(error_code,error_info,derror){
			showDetailErrorInfo(error_code,error_info,derror);
		},
		{async:false});

	return beautifualFlag;
}

function aftercheckIsBeautifual(data)
{
	var flag=data.get(0,"IS_FLAG");
	if(flag=="false")
	{
		MessageBox.alert("","服务代码为吉祥号码，请先进行预占！");
		$("#pam_SVR_CODE_END").select();//focus();
		return false;
	}
	return true;
}

/*
   1－永久白名单
   2－黑名单
   3－限制次数白名单
   4－点播业务
  如果业务类型是3或是4时，需要填写pam_BIZ_ATTR。
*/
function chargeBizAttr(){
   setDeliverNumDis();//根据选择的业务属性不同,改变限制下发次数，如果业务类型是3或是4时，需要填写此字段。填写0则没有限制。
}
/**
*@author:liaolc
 @function: 根据选择的业务属性不同,改变限制下发次数，如果业务类型是3或是4时，需要填写此字段。填写0则没有限制。
*/
function setDeliverNumDis(){
   var bizAttrValue=$("#pam_BIZ_ATTR").val();
    var operState=$("#pam_OPER_STATE").val();
   if(bizAttrValue==1||bizAttrValue==2||bizAttrValue==""||bizAttrValue==null||operState=="04"||operState=="05")
   {
       $("#pam_DELIVER_NUM").val("0");
       $("#pam_DELIVER_NUM").attr('disabled',true);
       $.Flip.get("pam_DELIVER_NUM").setDisabled(true);
   }
   else
   {
      $("#pam_DELIVER_NUM").attr('disabled',false);
      $.Flip.get("pam_DELIVER_NUM").setDisabled(false);
   }
}
/**
*服务代码处理
**/
function dealServCode()
{
 //today对EC基本接入号，业务接入号（服务代码）进行处理
  var vHead = $("#pam_SVR_CODE_HEAD").val();
  var vTail = $("#pam_SVR_CODE_END").val();
  var cLen = $("#pam_C_LENGTH").val();

  if(cLen!=""&&vTail.length!=cLen)
  {
  	alert("服务代码尾号必须为"+cLen+"位！");
	return false;
  }

  	var numberre = /[^0-9]/g;
  	var numrst = vTail.match(numberre);   // 在字符串 s 中查找匹配。
	if(numrst!=null)
	{
   		alert("服务代码必须为数字!");
   		return false;
   	}
    var userId = $.getSrcWindow().$('#USER_ID').val();
	if(userId==undefined)
	{
		userId="";
	}
    var vServCode = vHead + vTail ;
    //add by lijie9, 2011-5-16 验证端到端传过来的服务代码和填写的是否一致
    var eos = $.DatasetList($.getSrcWindow().$("#EOS").val());//从父页面获取EOS数据
	var serviceCode = $("#pam_SERVICE_CODE").val();
	if(serviceCode!=vServCode&&(eos && eos != "" && eos != "[]")&&userId=="") //产品变更(与MAS不同，MAS是服务变更）的时候不做校验，因为服务代码不能变更
	{
		alert("填写的服务代码与已审核的服务代码不一致，请核对!");
		return false;
	}

  //业务接入号被赋予 wade 控件，以便传到后台
  $('#pam_BIZ_IN_CODE').val(vServCode);

  //业务接入号填回到EC基本接入号
  $('#pam_EC_BASE_IN_CODE').val(vServCode);

  /**
  var oldBizInCode=$.getSrcWindow().$('#OLD_BIZ_IN_CODE').val();
  if(oldBizInCode!=""){
				if(vTail!=oldBizInCode){
				alert('同一集团产品下的服务代码要一致!');
				return;
			}

  }
  **/
	var groupId = $('#GROUP_ID').val();
	var bizFlag = true;
	$.ajax.submit('this', 'getDumpIdByajax', '&GROUP_ID='+groupId+'&BIZ_IN_CODE='+vServCode,null,
		function(data){
			bizFlag = checkBizInCodeByajax(data);//生成的服务代码不能使用false
		},
		function(error_code,error_info,derror){
			showDetailErrorInfo(error_code,error_info,derror);
		},
		{async:false});

	return bizFlag;
}
function chgBizCode()
{
	var bizCode=$('#pam_BIZ_CODE').val();
	var servType=$('#pam_SERVICE_TYPE').val();
	$.ajax.submit(this, 'createNewBizCode', '&BIZ_CODE='+bizCode+'&SERV_TYPE='+servType,null, 
			function(data){
				$('#pam_BIZ_CODE').val(data.get(0,"BIZ_CODE"));
			},
			function(error_code,error_info,derror){
				showDetailErrorInfo(error_code,error_info,derror);
			},
			{async:false});
}
//获取提交数据
function commSubmit()
{
	var itemIndex= "";
	var urlParts = document.URL.split("?");
	var parameterParts = urlParts[1].split("&");
	for (i = 0; i < parameterParts.length; i++)
	{
		var pairParts = parameterParts[i].split("=");
		var pairName = pairParts[0];
		var pairValue = pairParts[1];

		if(pairName=="ITEM_INDEX")
		{
		      itemIndex=pairValue;
		}
	}

	var inputElements=document.getElementsByTagName('INPUT');
	for(i=0;i<inputElements.length;i++)
	{
	  inputElements[i].disabled=false;
	}


	var selectElements=document.getElementsByTagName('SELECT');
	for(i=0;i<selectElements.length;i++)
	{
		selectElements[i].disabled=false;
	}


	$("#CANCLE_FLAG").val("false");
	var paramset = $.DatasetList();
	var svcparamdata=$.DataMap();

	var platsvcparam=$.ajax.buildJsonData("adcServicParamsForm_pam","pam");//获取表单以pam开头的数据
	var platsvcdata=$.DataMap(platsvcparam);
	svcparamdata.put("PLATSVC",platsvcdata);//PLATSVC存放页面以pam开头的数据
	svcparamdata.put("CANCLE_FLAG",$("#CANCLE_FLAG").val());
	var molist="";
	try
	{
		molist = $.table.get("MoListTable").getTableData("SEQ_ID,MO_CODE,MO_MATH,MO_TYPE,DEST_SERV_CODE,DEST_SERV_CODE_MATH",false);
	}
	catch(e)
	{
	  molist="";
	}
	if(molist != ""){
	  svcparamdata.put("MOLIST",molist);
	}
	var serviceId=$("#SERVICE_ID").val();
	svcparamdata.put("ID",serviceId);
	var paramVerifySucc=new Wade.DataMap(); //加上一个表示点过确认按钮的 DataMap
	paramVerifySucc.put("PARAM_VERIFY_SUCC","true");

	paramset.add(paramVerifySucc);
	paramset.add(svcparamdata);
    $("#adcServicParamsForm_pam").attr('disabled',false);//设置整个form元素都可见
	$.getSrcWindow().selectedElements.updateAttr(itemIndex,paramset.toString());//调置到产品组件
	//$.getSrcWindow().$("#OLD_BIZ_IN_CODE").val($('#svrCodeTail').val());//设置服务代码到父页面 保证同一个产品受理时不同服务的服务代码相同
	$.setReturnValue();
	setStatetype();
}


/*校验管理员手机号*/
function checkAdminExist()
{

	var adminSerNum = $('#pam_ADMIN_NUM').val();
	if(adminSerNum == null  && adminSerNum == "")
	{
		 alert('管理员号码为空');
		 $("#pam_ADMIN_NUM").select();
		 return false;
	}
	var adminFlag=true;
	var param ='&SERIAL_NUMBER='+adminSerNum;
	$.ajax.submit('this', 'getDumpSnByajax', param,null,
		function(data){
			adminFlag = afterCheckAdminExist(data);
		},
		function(error_code,error_info,derror){
			showDetailErrorInfo(error_code,error_info,derror);
		},
		{async:false});

	return adminFlag;
}

function afterCheckAdminExist(data)
{
	var SnFlag=data.get(0,"IS_FLAGSN");
	if(SnFlag=="true")
	{
		MessageBox.alert("","管理员号码不是有效的在网用户，请重新输入！");
		//disableEle();
		$("#pam_ADMIN_NUM").select();//focus();
		return false;
	}
	return true;
	//commSubmit();
}

//校验业务接入码
function checkBizInCodeByajax(data)
{
	//判断当前是新增还是修改页面 today
	if ($("#pam_MODIFY_TAG").val() == '0')
	{
	   var flag = data.get(0,"ISCHECKAACCESSNUMBER");
		if(flag == "false")
		{
			MessageBox.alert("","生成的服务代码不能使用，请手动输入！");
			$("#pam_SVR_CODE_END").focus();
			return false;
		}
	}
	return true;

}


/**
*table动态表
*/

function tableRowClick() {
	var rowData = $.table.get("MoListTable").getRowData();
	$("#MO_CODE").val(rowData.get("MO_CODE"));
	$("#MO_MATH").val(rowData.get("MO_MATH"));
	$("#MO_TYPE").val(rowData.get("MO_TYPE"));
	$("#DEST_SERV_CODE").val(rowData.get("DEST_SERV_CODE"));
	$("#DEST_SERV_CODE_MATH").val(rowData.get("DEST_SERV_CODE_MATH"));
}

function tableRowDBClick() {
	var rowData = $.table.get("MoListTable").getRowData();
	$("#MO_CODE").val(rowData.get("MO_CODE"));
	$("#MO_MATH").val(rowData.get("MO_MATH"));
	$("#MO_TYPE").val(rowData.get("MO_TYPE"));
	$("#DEST_SERV_CODE").val(rowData.get("DEST_SERV_CODE"));
	$("#DEST_SERV_CODE_MATH").val(rowData.get("DEST_SERV_CODE_MATH"));
}

function tableColumnClick() {alert(3);}
function tableAddRow(e) {$.table.get("MoListTable").addRow(e);};
function tableDeleteRow(e) {$.table.get("MoListTable").deleteRow();};
function tableCleanRow(e) {$.table.get("MoListTable").cleanRow(e);};
/**
*动态表格新增一条记录
*/
function createMoinfo()
{

	var editDate = $.ajax.buildJsonData("MoListDetail");

  	if(!$.validate.verifyAll('MoListDetail'))return false;
  	if (!$.table.get("MoListTable").isPrimary('MO_CODE,DEST_SERV_CODE', editDate)){
		editDate["MO_MATH_NAME"]=$("#MO_MATH").find("option:selected").text();
		editDate["MO_TYPE_NAME"]=$("#MO_TYPE").find("option:selected").text();
		editDate["DEST_SERV_CODE_MATH_NAME"]=$("#DEST_SERV_CODE_MATH").find("option:selected").text();
		//往表格里添加一行并将编辑区数据绑定上
		$.table.get("MoListTable").addRow(editDate,null,null,true);
		//往表格里添加一行数据后清空编辑框
		resetArea('MoListDetail',true);
	} else {
		MessageBox.alert("","添加失败！“指令内容、目的号码”已经存在相同的值！");
		resetArea('MoListDetail',true);
	}
}
/**
*动态表格更新一条记录
*/
function updateMoinfo()
{
	var editDate = $.ajax.buildJsonData("MoListDetail");
	if(!$.validate.verifyAll('MoListDetail')) return false;
	editDate["MO_MATH_NAME"]=$("#MO_MATH").find("option:selected").text();
	editDate["MO_TYPE_NAME"]=$("#MO_TYPE").find("option:selected").text();
	editDate["DEST_SERV_CODE_MATH_NAME"]=$("#DEST_SERV_CODE_MATH").find("option:selected").text();

	$.table.get("MoListTable").updateRow(editDate);
	//往表格里添加一行数据后清空编辑框
	resetArea('MoListDetail',true);
}

/**
*动态表格删除一条记录
*/
function deleteMoinfo()
{
	var tab = $.table.get("MoListTable");
	tab.deleteRow(tab.getTable().attr("selected"));
	//往表格里添加一行数据后清空编辑框
	resetArea('MoListDetail',true);
}

function getTableData() {
	//var data = $.table.get("MoListTable").getTableData(null,true);
	//$("#MoListTable tbody tr")[1].setAttribute("status", "2");
		var data1 = $.table.get("tjOtherTable").getTableData(null, true);
	MessageBox.alert("",data1);

}
//替换动态表格操作标识字段 并且设置其显示属性
function replaceTablecol(moCode,destServCode,xtag)
{
	var table=$.table.get("MoListTable").getTableData();
	table.each(function(item,idx){
		var cellmodcode = item.get("MO_CODE");
		var celldestServCode = item.get("DEST_SERV_CODE");
		if(moCode==cellmodcode&&destServCode==celldestServCode)
		{
			$("#MoListTable tbody tr")[idx].setAttribute("status", xtag);
		   if(xtag=='1')//代表删除 那么将要删除的这行隐藏掉
		   {
		   		$("#MoListTable tbody tr")[idx].style.display='none';
		   }
		}

	});
}
/**************************************************动态表格结束*********************************************************/
/**
 function setdisabledChushi()
 {
 	var inputElements=document.getElementsByTagName('INPUT');
	for(var i=0;i<inputElements.length;i++)
	{
	 	 inputElements[i].disabled=true;
	}

	var selectElements=document.getElementsByTagName('SELECT');
	for(var i=0;i<selectElements.length;i++)
	{
		selectElements[i].disabled=true;
	}

	$('#bt_ok').attr('disabled',false);
	$('#bt_canlet').attr('disabled',false);
	var aElements=document.getElementsByTagName('A');
	for(var i=0;i<aElements.length;i++)
	{
		aElements[i].style.display='none';
	}

	$('#pam_OPER_STATE').attr('disabled',false);
 }

 function setolddisabled()
 {
 	var inputElements=document.getElementsByTagName('INPUT');
	for(var i=0;i<inputElements.length;i++)
	{
	 	 inputElements[i].disabled=false;
	}

	var selectElements=document.getElementsByTagName('SELECT');
	for(var i=0;i<selectElements.length;i++)
	{
		selectElements[i].disabled=false;
	}

	$('#bt_ok').attr('disabled',false);
	$('#bt_canlet').attr('disabled',false);
	var aElements=document.getElementsByTagName('A');
	for(var i=0;i<aElements.length;i++)
	{
		aElements[i].style.display='';
	}

 	var inputElements= getChildsByRecursion("lefttabset", "INPUT", "olddisabled", "no");
	for(var i=0;i<inputElements.length;i++)
	{
	 	 inputElements[i].attr('disabled',true);
	}

	var inputElements= getChildsByRecursion("lefttabset", "SELECT", "olddisabled", "no");
	for(var i=0;i<inputElements.length;i++)
	{
	 	 inputElements[i].attr('disabled',true);
	}

	if($("#pam_MODIFY_TAG").val()!="0")
	{
		$("#pam_SVR_CODE_END").attr('disabled',true);
	}
	$('#pam_OPER_STATE').attr('disabled',true);
	$('#pam_PLAT_SYNC_STATE').attr('disabled',true);
 }
 **/
//从url取关键值,并根据关键值从父页面取值
function putpagedata(dataset,serviceId)
{
	  //如果曾经点过确认按钮，就会总是曾经点过，想抵赖是不行的
	  var paramVerifySucc=dataset.get(0); //如果曾经点过确认按钮，就会总是曾经点过，想抵赖是不行的
	  $("#PARAM_VERIFY_SUCC").val(paramVerifySucc.get("PARAM_VERIFY_SUCC",""));//设置参数是否已经校验成功的值到页面
	  var paramMap=dataset.get(1);            //改为从1取，因为 dataset的0已存放了一个表示是否点过确认按钮的状态
	  var platsvcdata=paramMap.get('PLATSVC');//得到的platsvc表数据IData结构
	  platsvcdata.eachKey(                    //将platsvcdata参数 填充到adcServicParamsForm

	 	 function(key)
	 	 {
	     	try
	     	{
	     		var tempElement = $("#"+key);
	     		if(tempElement != "undefined" && tempElement != ""){
	     			tempElement.val(platsvcdata.get(key,""));
	     		}
	     	}
	     	catch(e)
	     	{
	     	}
	     }
	    );

	  var molistdataset=paramMap.get('MOLIST');//得到业务上行指令数据 IDataset结构
	  if(molistdataset)
	  {
	  	var molistcount=molistdataset.getCount();
	  	for(j=0;j<molistcount;j++)
	 	 {
	  		 var modata=molistdataset.get(j);
	   		 modata.eachKey(
	 			 function(mokey)
	 	 		 {
	     			try
	     			{

	     				$("#"+mokey).val(modata.get(mokey,""));
	     			}
	     			catch(ee)
	     			{
	     			}
	     		}
	    	);
	   	    if(modata.get("tag","")==""|| modata.get("tag","")=="2")//直接从数据库查得的记录
	   	 	{
	  			createMoinfo();
	  			replaceTablecol(modata.get("MO_CODE"),modata.get("DEST_SERV_CODE"),'2');
	  	 	}
	  	 	else if( modata.get("tag","")=="0")//新增的记录
	  	 	{
	  	 		createMoinfo();//往表新增这条记录
	  	 	}
	  	 	else if( modata.get("tag","")=="1")//删除的记录
	  		{
	  	 		createMoinfo();//往表新增这条记录
	  			replaceTablecol(modata.get("MO_CODE"),modata.get("DEST_SERV_CODE"),'1'); //修改这条记录的操作标识为删除,同时隐藏这条记录
	  	 	}
	  	}
	  }

	dealOperStateoptions();

	//只有新增时，才从库取配置

	//生成服务代码尾数
	var vModiState = $("#pam_MODIFY_TAG").val();
	if (vModiState == '0'&&$("#PARAM_VERIFY_SUCC").val()=="false")
	{
	   //getBizCodeTail();//只有新增用户时才产生服务代码尾数(或产生 或从该用户的其他服务继承)

	    //begin: add by lijie9 for esop,2011-07-08, 如果已经有参照的服务代码，则取其尾
	    if ($("#pam_SERVICE_CODE").val() && $("#pam_SVR_CODE_HEAD").val())
	    {
	    	var vSrvCode = $("#pam_SERVICE_CODE").val();
	    	var vpart=$("#pam_SVR_CODE_HEAD").val();
	    	var lall=vSrvCode.length;
			var lpart=vpart.length;

	    	if (vSrvCode.substring(0,lpart) == vpart)
	    	{
	    		$("#pam_SVR_CODE_END").val(vSrvCode.substring(lpart,lall)) ;
	    	}
	    }
	    //end: add by lijie9 for esop,2011-07-08, 如果已经有参照的服务代码，则取其尾
	}
	else
	{
		var vall=$("#pam_BIZ_IN_CODE").val();
		var vpart=$("#pam_SVR_CODE_HEAD").val();
		var lall=vall.length;
		var lpart=vpart.length;
		var vtail=vall.substring(lpart,lall);
		$("#pam_SVR_CODE_END").val(vtail) ;
	}


	//如果企业邮箱产品　则显示邮箱域名要素
	var productid = $.getSrcWindow().$('#PRODUCT_ID').val();
	if(productid=="10005801")
	{
	    $("#ec_mail_tr").css("display","block");
	}
	else
	{
		$("#ec_mail_tr").css("display","none");
	}
	if(productid=="10009150")
	{
		$("#ec_sp_code").css("display","block");
		$('#pam_SP_CODE').attr('nullable',"no");

	}
	else
	{
		$("#ec_sp_code").css("display","none");
	}
	//如果是集团通讯录
	if(productid=="10009805")
	{
		$("#tongxunlu").css("display","block");

	}
	else
	{
		$("#tongxunlu").css("display","none");
	}

	// 全网彩信页面的提示语BUG修改
	var bizTypeCode=$("#pam_BIZ_TYPE_CODE").val();
    if (bizTypeCode == "001") {
  	  $("#max_item_day_text").text("每天最大短信数：");
  	  $("#max_item_mon_text").text("每月最大短信数：");
  	  $("#is_text_ecgn_text").text("短信正文签名：");
    } else if (bizTypeCode == "002") {
  	  $("#max_item_day_text").text("每天最大彩信数：");
  	  $("#max_item_mon_text").text("每月最大彩信数：");
  	  $("#is_text_ecgn_text").text("彩信正文签名：");
    }

     //关于BOSS/ESOP集团短彩信端口新增端口分类的需求
	  var isDisplay = $("#pam_IS_SPEC_AREA_DISPLAY").val();
	  if (isDisplay == '1') {
	  	$("#spec_area").css("display","block");
	  	$('#pam_SERVICE_TYPE').attr('nullable',"no");
	  	$('#pam_WHITE_TOWCHECK').attr('nullable',"no");
	  	$('#pam_SMS_TEMPALTE').attr('nullable',"no");
	  	$('#pam_PORT_TYPE').attr('nullable',"no");
	  } else {
	  	$("#spec_area").css("display","none");
	  }
}

function dealBizCodetailDisable() //判断是用户开户 还是用户变更界面 如果是变更界面 限制服务代码不能修改
{
     var modifypageflag=false;
	 var selectElements=window.parent.document.getElementById('SELECTED_ELEMENTS').value;
	 var selectElementset = new Wade.DatasetList(selectElements);

	 for(var i=0;i<selectElementset.getCount();i++)//分理出产品变更哪些服务做了暂停恢复操作,存在stopsvcstr里面 和哪些产品元素做了修改存储在svcDataset里面
  	 {
  	    if(modifypageflag)
   	    {
   	    	break;
   	    }
  	 	var elementData=selectElementset.get(i);
  	 	var elementsDataset = elementData.get("ELEMENTS"); // 取元素
  	 	var productMode=elementData.get("PRODUCT_MODE");
  	 	for(var j=0;j<elementsDataset.getCount();j++)
   	 	{

   	 		var packageData = elementsDataset.get(j); // 取每个元素

   	 		var elementType = packageData.get("ELEMENT_TYPE_CODE", "");
   	    	var elementstate=packageData.get("STATE");
   	    	if(elementstate!="ADD")
   	    	{
   	    	  modifypageflag=true;
   	    	  $("#pam_SVR_CODE_END").attr('disabled',true);
   	    	  break;
   	    	}
   	    }
   	  }
}

function dealOperStateoptions()
{
 	var modifytag=$('#pam_MODIFY_TAG').val();
 	if("0"==modifytag)//新增时
 	{
	  $('#pam_OPER_STATE').html("");
 	  $('#pam_OPER_STATE option').length = 0;  //清空操作状态选项
 	  jsAddItemToSelect($("#pam_OPER_STATE"), '新增', '01');
 	  $('#pam_OPER_STATE').val('01');
 	  $('#pam_OPER_STATE').attr('disabled',true);

	  var rsrvTag2="1";
	  var classId = $("#CLASS_ID").val();

	  if (classId=="5"|| classId=="6" || classId=="A1" || classId=="A2")
	  {
		 rsrvTag2="3";
	  }
	  if (classId=="7"|| classId=="8" || classId=="B1" || classId=="B2")
	  {
		 rsrvTag2="2";
	  }
	  $('#pam_RSRV_TAG2').val(rsrvTag2);//客户等级
 	}
 	else if("2"==modifytag)//对原有记录进行修改时
 	{
 	  $('#pam_OPER_STATE').html("");
 	  $('#pam_OPER_STATE options').length = 0;  //清空操作状态选项
 	  var platsyncState=$("#pam_PLAT_SYNC_STATE").val();
 	  if(platsyncState == '')
 	  {
 	    platsyncState="1";
 	  }

 	  if(platsyncState=="P")//暂停
 	  {
 	   	$('#pam_OPER_STATE').html("");
 	    $('#pam_OPER_STATE options').length = 0;  //清空操作状态选项
 	  	jsAddItemToSelect($("#pam_OPER_STATE"), '恢复', '05');
 	  }
 	  if(platsyncState=="1")//正常在用
 	  {
 	   	$('#pam_OPER_STATE').html("");
 	    $('#pam_OPER_STATE options').length = 0;  //清空操作状态选项
 	    jsAddItemToSelect($('#pam_OPER_STATE'), '暂停', '04');
 	    jsAddItemToSelect($('#pam_OPER_STATE'), '变更', '08');
 	  }

 	  //变更时不能改服务代码
 	   $('#pam_SVR_CODE_END').attr('disabled',true) //变更时不能改服务代码
 	}

}




function canresume()
{
	var canFlag = true;
	var userId=$.getSrcWindow().$("#USER_ID").val();
	var operstate=$('#pam_OPER_STATE').val();
	if(operstate=="05")
	{
		$.ajax.submit('this', 'grpUserOweFeeByajax', '&USER_ID='+userId,null,
		function(data){
			canFlag =  aftercanresume(data);
		},
		function(error_code,error_info,derror){
			showDetailErrorInfo(error_code,error_info,derror);
		},
		{async:false});

	}
	return canFlag;
}
function aftercanresume(owefeeset)
{
	var owefee=parseInt(owefeeset.get(0,"OWE_FEE"));//实时欠费信息
	var createvalue=parseInt(owefeeset.get(0,"CREDIT_VALUE"));//用户信用度
	//var rst=owefee +createvalue*100;
	//alert('实时欠费的费用为:'+owefee+"用户信用度为:"+createvalue+" 最后值:"+rst);
	if( owefee +createvalue*100<0)
	{
		alert('集团产品已经欠费,不能执行[恢复]操作！请先缴清费用后再执行[恢复]操作!');
		return false;
	}
	return true;
	//checkSignExist();//判断中英文签名是否存在敏感资费
}


function checkforbidtime()
{
	forbidstarttimea=$("#pam_FORBID_START_TIME_A").val();
	forbidendtimea=$("#pam_FORBID_END_TIME_A").val();
	forbidstarttimeb=$("#pam_FORBID_START_TIME_B").val();
	forbidendtimeb=$("#pam_FORBID_END_TIME_B").val();
	forbidstarttimec=$("#pam_FORBID_START_TIME_C").val();
	forbidendtimec=$("#pam_FORBID_END_TIME_C").val();
	forbidstarttimed=$("#pam_FORBID_START_TIME_D").val();
	forbidendtimed=$("#pam_FORBID_END_TIME_D").val();

	if((forbidstarttimea==""&&forbidendtimea!="")||(forbidstarttimea!=""&&forbidendtimea==""))
	{
		alert("不允许下发开始时间1和不允许下发结束时间1 必须配对 \n可以都为空,或都不为空!");
		return false;
	}
	if((forbidstarttimeb==""&&forbidendtimeb!="")||(forbidstarttimeb!=""&&forbidendtimeb==""))
	{
		alert("不允许下发开始时间2和不允许下发结束时间2 必须配对 \n可以都为空,或都不为空!");
		return false;
	}
	if((forbidstarttimec==""&&forbidendtimec!="")||(forbidstarttimec!=""&&forbidendtimec==""))
	{
		alert("不允许下发开始时间3和不允许下发结束时间3 必须配对 \n可以都为空,或都不为空!");
		return false;
	}
	if((forbidstarttimed==""&&forbidendtimed!="")||(forbidstarttimed!=""&&forbidendtimed==""))
	{
		alert("不允许下发开始时间4和不允许下发结束时间4 必须配对 \n可以都为空,或都不为空!");
		return false;
	}
	return true;
}


//点取消按钮返回原值数据到父页面隐藏字段
function setCancleData(obj)
{
	var serviceId="";
	var itemIndex= "";
	var urlParts = document.URL.split("?");
	var parameterParts = urlParts[1].split("&");
	var svcparamvalue="";
	try
	  {
	  	  svcparamvalue=$.getSrcWindow().selectedElements.getAttrs($("#ITEM_INDEX").val());
	  	  if(svcparamvalue.get(0,"PARAM_VERIFY_SUCC")==undefined)
	  	  {
	  	  	svcparamvalue="";
	  	  }
	  }
	  catch(e)
	  {
		  svcparamvalue="";
	  }
	for (i = 0; i < parameterParts.length; i++)
	{
		var pairParts = parameterParts[i].split("=");
		var pairName = pairParts[0];
		var pairValue = pairParts[1];
		if(pairName=="ELEMENT_ID")
		{
		      serviceId=pairValue;
		}
		if(pairName=="ITEM_INDEX")
		{
		      itemIndex=pairValue;
		}
	}

	if(svcparamvalue==""||svcparamvalue=="[]")//请求url内没有找到已存在的参数值
	{
		var dataset = $.DatasetList();
		var paramVerifySuccMap=$.DataMap();
		paramVerifySuccMap.put("PARAM_VERIFY_SUCC","false");
		dataset.add(paramVerifySuccMap);
		svcparamvalue=dataset.toString();
	}
	$.getSrcWindow().selectedElements.updateAttr(itemIndex,svcparamvalue.toString());
	$.setReturnValue();
}


//当计费类型变更时，做的判断
function chargeTypeChanged()
{
	var bizAttr=$('#pam_BIZ_ATTR').val();
	var billType = $('#pam_BILLING_TYPE').val();
	if(bizAttr != '0')
	{
		if (billType!='00')
		{
			alert('业务属性为黑白名单时，计费类型必须为免费，且单价须为0！');
			$('#pam_BILLING_TYPE').val('00');
			$('#pam_PRICE').val('0');
		}
	}
	if(bizAttr == '0')
	{
		if (billType=='00')
		{
			alert('业务属性为订购关系时，计费类型不能是免费！');
			$('#pam_BILLING_TYPE').val('01');
		}
	}
}



/**
function dealBizCodeTail(data)
{
	var strSvcCodeTail = data.get(0,"strSvcCodeTail");
	//add if condition by lijie9 for esop, 2011-07-18, 只有当服务代码尾本来为空时，才去做这个替换操作，以防因ajax延迟覆盖掉端到端带入的服务代码
	if (!$("#pam_SVR_CODE_END").val())
	{
		$("#pam_SVR_CODE_END").val(strSvcCodeTail) ;
	}
	//setMoAccessNum();
}
**/

function getBizCodeTail()
{
/**
	 var oldcodetail=$.getSrcWindow().$('#OLD_BIZ_IN_CODE').val();
	 if(oldcodetail==undefined)
	 {
	 	oldcodetail=""
	 }
	 if(oldcodetail!="")
	 {
	 	$('#pam_SVR_CODE_END').val(oldcodetail);
	 	//setMoAccessNum();
	 	return;
	 }

  var oldServCodeFlag=false;//是否有其他业务已经填写过业务接入号
  if(oldServCodeFlag)//如果存在其他业务已经存在(同一次操作内,包括新增的) 确定了业务接入号,则直接使用该同用户的其他业务的业务接入号做为此业务的业务接入号
  {
		var vall=oldServCode;
		var vpart=$("#pam_SVR_CODE_HEAD").val();
		var lall=vall.length;
		var lpart=vpart.length;
		var vtail=vall.substring(lpart,lall);
		$("#pam_SVR_CODE_END").val(vtail) ;
		$("#pam_SVR_CODE_END").attr('disabled',false);
		//setMoAccessNum();
		return;
  }

  else//否则到后台 通过ajax直接从服务器取值
  {
	var param ='&C_LENGTH='+$('#pam_C_LENGTH').val();
  	$.ajax.submit('this', 'getBizCodeTail',param ,null, function(data){
  		dealBizCodeTail(data);
  		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
  }
  **/
}

/**
 * 作用：下拉列表给值
 */
function jsAddItemToSelect(objSelect, objItemText, objItemValue) {
    //判断是否存在
    if (jsSelectIsExitItem(objSelect, objItemValue)) {
        MessageBox.alert("","该Item的Value值已经存在");
    } else {
       objSelect.append("<option selected value=\"" +objItemValue+"\">" +objItemText+ "</option>");
    }
}

 function jsSelectIsExitItem(objSelect, objItemValue) {
    var isExit=false;
    objSelect.children("option").each(function(){
         if(this.value==objItemValue)
            return !isExit;
    });
   return isExit;
}

function isCharsInBag (s, bag)
{
	var i,c;
	for (var i = 0; i < s.length; i++)
	{
		c = s.charAt(i);//字符串s中的字符
		if (bag.indexOf(c) > -1)
		return c;
	}
	return "";
}

function ischinese()
{

	var zh = $("#pam_TEXT_ECGN_ZH").val();
	var en = $("#pam_TEXT_ECGN_EN").val();
	var em = $("#pam_RSRV_STR2").val();//邮箱域名

	var re = /[&\*\+\.\[\]%\$\^\?\{}\|\\#@!~]/g;
	var rn = /[^A-Za-z0-9_\s]/g;
	//var rm = /\w+\.\w+$/g;

	var r = zh.match(re);
	var e = en.match(rn);
	//var m = em.match(rm);
	if(r!=null)
	{
		alert("中文签名,不允许包含除中文 字母 数字外的特殊字符,请检查输入!");
		return '1';
	}
	if(e!=null)
	{
		alert("英文签名,不允许包含除字母 数字外的特殊字符,请检查输入!");
		return '1';
	}

	var l = zh.length;
	if(l>18||l<2)
	{
	    alert("中文签名，最多输入2-18个汉字之间!\n");
	    $("#pam_TEXT_ECGN_ZH").select();
	   	return '1';
	}
	var len = en.length;
	if(len>36||len<4) {
		MessageBox.alert("","英文签名，最多必须输入4-36个字符之间!\n");
		disableEle();
		$("#pam_TEXT_ECGN_EN").select();
		return '1';
	}
	/**
	if(m!=null&&(em=!null||em!=""))
	{
		alert("邮箱域名格式不正确,请检查输入!");
		return '1';
	}
	**/

}

function disableEle()
{
  //20090527重设为默认的 disabled
	$("#pam_PLAT_SYNC_STATE").attr('disabled',true);
	$("#pam_SIBASE_INCODE_A").attr('disabled',true);
	$("#pam_BIZ_CODE").attr('disabled',true);
	$("#pam_BIZ_NAME").attr('disabled',true);
	$("#pam_SVR_CODE_HEAD").attr('disabled',true);
	$("#pam_BILLING_TYPE").attr('disabled',true);
	$("#pam_PRICE").attr('disabled',true);
	$("#pam_BIZ_STATUS").attr('disabled',true);
	$("#pam_BIZ_ATTR").attr('disabled',true);
	$("#pam_PRE_CHARGE").attr('disabled',true);
	//$("#pam_IS_TEXT_ECGN").attr('disabled',true);
}

/*校验中英文签名是否有敏感字符*/
function checkSignExist(){
    var signFlag = true;
    var productid=$.getSrcWindow().$("#PRODUCT_ID").val();
	var param ='&TEXT_ECGN_ZH='+$('#pam_TEXT_ECGN_ZH').val()+'&TEXT_ECGN_EN='+$('#pam_TEXT_ECGN_EN').val()+'&PRODUCT_ID='+productid;
	$.ajax.submit('this', 'getSensitiveTextByajax', param,null,
	function(data){
		 signFlag = afterCheckSignExist(data);
	},
	function(error_code,error_info,derror){
		showDetailErrorInfo(error_code,error_info,derror);
	},
	{async:false});

	return signFlag;
}

/*校验是否有权限修改*/
function checkmodifypriv(){
	var hasmodifyprv=$("#HASMODIFYPRV").val();
	if(hasmodifyprv=="true")
		{
		  $('#pam_RSRV_TAG2').attr('disabled',false);//客户等级
		
		}
	return;
}

function afterCheckSignExist(data){
	var inProduct=data.get(0,'IN_PRODUCT');
	if(inProduct=="true"){
		var hasZh = data.get(0,"HAS_ZH");
		if(hasZh=="true"){
			alert("对不起，中文签名含有敏感字符["+data.get(0,"PARAM_NAME")+"]，请重新输入");
			$("#pam_TEXT_ECGN_ZH").select();
			return false;
		}

		var hasEn = data.get(0,"HAS_EN");
		if(hasEn=="true"){
			alert("对不起，英文签名含有敏感字符["+data.get(0,"PARAM_NAME")+"]，请重新输入");
			$("#pam_TEXT_ECGN_EN").select();
			return false;
		}
	}
	return true;
	//checkAdminExist();
}

//设置短信上行访问代码
function setMoAccessNum()
{
	var biztype=$("#pam_BIZ_TYPE_CODE").val();
	if(biztype=="001"&&$("#pam_MO_ACCESS_NUM").val())
	{
		$("#pam_MO_ACCESS_NUM").val("");
	}
	else if((biztype=="002"||biztype=="003")&&$("#pam_MO_ACCESS_NUM"))
	{
		$("#pam_MO_ACCESS_NUM").val($("#pam_SVR_CODE_HEAD").val()+$("#pam_SVR_CODE_END").val());
	}
}

//设置短信上行访问代码样式
function setMoAccessNumClass()
{
	var biztype=$("#pam_BIZ_TYPE_CODE").value;
	if(biztype=="001"&&$("#pam_MO_ACCESS_NUM"))
	{
		$("#pam_MO_ACCESS_NUM_SPAN").className="";
	}
	else if((biztype=="002"||biztype=="003")&&$("#pam_MO_ACCESS_NUM"))
	{
		$("#pam_MO_ACCESS_NUM_SPAN").className="e_required";
	}
}


function smsLimitCounCheck(element)
{
    var str=$(element).val();
	if(null==str||str==''||str=='0')
	{
	  $(element).val('1');
	}
	else
	{
	  $(element).val(str);
	}

	var max="100000";
	var rsrvTag2=$('#pam_RSRV_TAG2').val();
	var errinfo="";
	if (parseInt(str)<=parseInt('0'))
	{
		alert("最大短信数不能小于或等于0!");
		element.focus;
		return '1';

	}

	if(rsrvTag2=="1")//普通级：不能超过10万
	{
	   max="100000";
	   errinfo="普通级：最大短信数不能超过10万!";
	}
    if (rsrvTag2=="2") //银牌级：不能超过100万
    {
	   max="1000000";
	   errinfo="银牌级：最大短信数不能超过100万!";
    }
	if (rsrvTag2=="3") //金牌级：不能超过500万
	{
		 max="5000000";
		 errinfo="金牌级：最大短信数不能超过500万!";
	}
	if (rsrvTag2=="4") //VIP级：不能超过10亿
	{
		 max="1000000000";
		 errinfo="VIP级：最大短信数不能超过10亿!";
	}

	if (parseInt(str)>parseInt(max))
	{
		alert(errinfo);
		element.focus;
		return '1';

	}
}
/**
 * 作用：根据不同的操作类型，页面输入框的可见性.
 *  	08-变更 04- 暂停 05-恢复
 */
function setStatetype(){
 var operState=$("#pam_OPER_STATE").val();
  if (operState == "08")
  {
	  tableDisabled("platsvcparamtable",false);
	  tableDisabled("MoListDetail",false);
	  $("#pam_PLAT_SYNC_STATE").attr('disabled',true);
	  $("#pam_BIZ_CODE").attr('disabled',true);
	  $("#pam_BIZ_NAME").attr('disabled',true);
	  $("#pam_BIZ_IN_CODE").attr('disabled',true);
	  $("#pam_BIZ_ATTR").attr('disabled',true);
	  $("#pam_BILLING_TYPE").attr('disabled',true);
	  $("#pam_PRICE").attr('disabled',true);
	  $("#pam_ACCESS_MODE").attr('disabled',true);
	  $("#pam_BIZ_STATUS").attr('disabled',true);
	  $("#pam_BIZ_TYPE_CODE").attr('disabled',true);
	  $("#pam_RSRV_TAG2").attr('disabled',true);
	  showBizInCode();
  }
  if (operState == "04")
  {
	  tableDisabled("adcServicParamsForm_pam",true);
	  $("#pam_OPER_STATE").attr('disabled',false);
	  $.Flip.get("pam_DELIVER_NUM").setDisabled(true);
	  $.Flip.get("pam_MAX_ITEM_PRE_DAY").setDisabled(true);
	  $.Flip.get("pam_MAX_ITEM_PRE_MON").setDisabled(true);
	  showBizInCode();
  }
  if (operState == "05")
  {
      tableDisabled("adcServicParamsForm_pam",true);
      $("#pam_OPER_STATE").attr('disabled',false);
      $.Flip.get("pam_DELIVER_NUM").setDisabled(true);
	  $.Flip.get("pam_MAX_ITEM_PRE_DAY").setDisabled(true);
	  $.Flip.get("pam_MAX_ITEM_PRE_MON").setDisabled(true);
	  showBizInCode();
  }
  if (operState == "01")
  {
 	  tableDisabled("platsvcparamtable",false);
	  tableDisabled("MoListDetail",false);
	  $("#pam_PRE_CHARGE").attr('disabled',true);//预付费标记
	  $("#pam_BIZ_CODE").attr('disabled',true);
	  $("#pam_BIZ_NAME").attr('disabled',true);
	  $("#pam_SVR_CODE_HEAD").attr('disabled',true);
	  //$("#pam_BIZ_ATTR").attr('disabled',true);
	  $("#pam_BILLING_TYPE").attr('disabled',true);
	  $("#pam_PRICE").attr('disabled',true);
	  $("#pam_ACCESS_MODE").attr('disabled',true);
	  $("#pam_BIZ_STATUS").attr('disabled',true);
	  $("#pam_BIZ_TYPE_CODE").attr('disabled',true);
	  $("#pam_RSRV_TAG2").attr('disabled',true);
	  $("#pam_PLAT_SYNC_STATE").attr('disabled',true);
	  if(!$("#pam_BILLING_TYPE").val())
	  {
	  	$("#pam_BILLING_TYPE").val("00");//没值的情况默认免费
	  }
   }
}
/**
 * 作用：控制TABLE里的值是否可填
 * @param tableName
 * @param flag
 */
function tableDisabled(tableName, flag){

$("#" + tableName + " input").each(function(){
    this.disabled=flag;
});

$("#" + tableName + " SELECT").each(function(){
     this.disabled=flag;
});
}

/**
*作用：控制服务代码的显示，及校验标示的去必填
*/
function showBizInCode(){
	$("#SI_BASE_IN_CODE").css("display","none");
	$("#BIZ_IN_CODE").css("display","block");
	$("#pam_SVR_CODE_HEAD").attr('nullable','yes');
	$("#pam_SVR_CODE_END").attr('nullable','yes');


}

/**
*作用：校验日下发短信量和月下发短信量
*/
function checkMessageAmount(){

	var dayValue = $("#pam_MAX_ITEM_PRE_DAY").val();
	var monValue = $("#pam_MAX_ITEM_PRE_MON").val();
	if (Number(dayValue) > Number(monValue)){
		MessageBox.alert("","您输入的每月短信数不能小于每日短信数，请重新输入！");
		return false;
	}
	var max="100000";
	var rsrvTag2=$('#pam_RSRV_TAG2').val();
	var errinfo="";
	if (monValue<="0")
	{
		alert("最大短信数不能小于或等于0!");
		$("#pam_MAX_ITEM_PRE_MON").focus;
		return false;

	}

	if(rsrvTag2=="1")//普通级：不能超过10万
	{
	   max="100000";
	   errinfo="普通级：最大短信数不能超过10万!";
	}
    if (rsrvTag2=="2") //银牌级：不能超过100万
    {
	   max="1000000";
	   errinfo="银牌级：最大短信数不能超过100万!";
    }
	if (rsrvTag2=="3") //金牌级：不能超过500万
	{
		 max="5000000";
		 errinfo="金牌级：最大短信数不能超过500万!";
	}
	if (rsrvTag2=="4") //VIP级：不能超过10亿
	{
		 max="1000000000";
		 errinfo="VIP级：最大短信数不能超过10亿!";
	}

	if (parseInt(monValue)>parseInt(max))
	{
		alert(errinfo);
		$("#pam_MAX_ITEM_PRE_MON").focus;
		return false;

	}
	return true;

}