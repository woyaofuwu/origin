/**
 * 比较两个dataset
 * str为需要比较的字段，以逗号分隔
 * 返回一个比较后的dataset
 * key唯一区分这个数据的,key可以是由多个字段组成的，以逗号分隔
 * str除了key以外的其他值
 */
function compareParamDataset(newDs,oldDs,key,str)
{
	var resultDs = new Wade.DatasetList();
	var ss = str.split(",");
	var keyCols = key.split(",");
	
	//newDs和oldDs比较
	for(var i=0;i<newDs.length;i++)
	{
		var newData = newDs.get(i);
		newData.put("MODIFY_TAG","ADD");//默认是新增的
		
		var isKeyFind = "false";//是否在两个数据中找到了相同的key
		for(var j=0;j<oldDs.length;j++)
		{
			var oldData = oldDs.get(j);			
			for(var k=0;k<keyCols.length;k++)
			{
				if(newData.get(keyCols[k]) == oldData.get(keyCols[k])&& newData.get("ATTR_GROUP") == oldData.get("ATTR_GROUP"))
				{
					isKeyFind = "true";
					break;//如果key中有一个字段不相等，就不用比较了
				}
				else
				{
					isKeyFind = "false";
				}
			}
			
			if(isKeyFind=="true")//如果两个参数的key相同
			{
				newData.put("MODIFY_TAG","EXIST");//置为存在状态
				var isModif = "false";//是否被修改了
				for(var k=0;k<ss.length;k++)
				{
					isModif = "false";//是否被修改了
					if(newData.get(ss[k])==oldData.get(ss[k])
						&& oldData.get("OLD_PARAM_VALUE") != null
						&& oldData.get("OLD_PARAM_VALUE") != ""){
						continue;//如果两个字段相等，继续比较下一个字段
					}
					else
					{
						// 判断是否有数据库的值，没有则不算是修改
						if(oldData.get("OLD_PARAM_VALUE") != null && oldData.get("OLD_PARAM_VALUE") != "") {
							newData.put("PARAM_OLD_VALUE", oldData.get(ss[k]));
							isModif = "true";
							break;
						}else if(newData.get("ATTR_VALUE") != null && newData.get("ATTR_VALUE") !=""){
							newData.put("MODIFY_TAG", "ADD");// 有修改，且数据库中没有，则为新增
						}
					}
				}
				if(isModif=="true")//如果被修改了，就要保存成一条删除，一条新增
				{
					oldData.put("MODIFY_TAG","DEL");//将老的数据置为删除状态
					newData.put("MODIFY_TAG","ADD");//将新数据置为新增状态
					newData.put("PARAM_OLD_VALUE",oldData.get(ss[k]));

					resultDs.add(oldData);		//将删除的老数据也保存
				}
				break;
			}			
		}	
		//排除没有变更且新值为空值的数据
		if((isKeyFind=="true" && !(newData.get("MODIFY_TAG")=="EXIST" && newData.get("ATTR_VALUE")=="")) 
			|| (newData.get("ATTR_VALUE")!=null && newData.get("ATTR_VALUE")!="")){
			resultDs.add(newData);
		}	
	}
	
	//老数据和新数据进行比较
	for(var i=0;i<oldDs.length;i++)
	{
		var isfound = "false";//是否找到了这个参数
		var oldData = oldDs.get(i);
		for(var j=0;j<newDs.length;j++)
		{
			var newData = newDs.get(j);
			if(oldData.get(key)==newData.get(key)&& oldData.get("ATTR_GROUP") == newData.get("ATTR_GROUP"))//比较老参数在新参数列表中是否有
			{
				isfound = "true";
				continue;
			}
		}
		if(isfound == "false")//如果新参数中没有，就要删除老的
		{
			oldData.put("MODIFY_TAG","DEL");
			resultDs.add(oldData);
		}
	}
	
	return resultDs;
}

var _id;


/**
 * 取消成员动态表格的编辑
 */
function cancelMemberParamEdit(){
	if(document.getElementById("ATTRIBUTE_DESC"))
	{
		document.getElementById("ATTRIBUTE_DESC").value = "";
	}
	if(document.getElementById("ATTRIBUTE_VALUE"))
	{
		document.getElementById("ATTRIBUTE_VALUE").value = "";
	}
	
	document.getElementById("bupdate").style.display="none";
	document.getElementById("bcancel").style.display="none";
}


function checkProductParamValue(paramCode){
	
	var verifyResult = true;
	
	//互联网专线 专线所在省客户编码 需分省支付时必填
	if("1112083405"==paramCode)
	{
	 var tradeId = $("#MERCH_TRADE_ID").val();
	 var inputObj = $("#input_"+paramCode);
	 var group_id=inputObj.val();
		verifyResult=verifyInterNetGroupInfo(tradeId,group_id,inputObj,paramCode);
		if(verifyResult==false){
			return false;
		}
	}

	if("1112054333"==paramCode ||"1112084333"==paramCode || "301014332"==paramCode || 
			"1113025021"==paramCode  || "1112064333"==paramCode || "1112074333"==paramCode)
	{
		var value = $("#input_"+paramCode).val();
		if(value==null||value=="")
		{
			return true;
		}
	}
	
	//专线业务、跨省互联网专线、跨省集团wlan、呼叫中心直联、跨国专线开通验收报告验证
	if("1112054333"==paramCode ||"1112084333"==paramCode || "301014332"==paramCode || 
			"1113025021"==paramCode  || "1112064333"==paramCode || "1112074333"==paramCode)
	{
		var oldValue = $("#OLDVALUE_"+paramCode).val();
		
		var newValue = $("#PARAM_VALUE_"+paramCode).val();
		var oldFileArry =oldValue.split(".");
		//获取原始文件的文件名
		var oldFileName = oldFileArry[0];

		if(oldFileName != newValue && newValue !='')
		{
			var fileAllName =$("#SIMPLEUPLOAD_SPAN_input_"+paramCode).text();
			var fileArry =fileAllName.split(".");
			var fileName = fileArry[fileArry.length - 1];
			if(!(fileName.toUpperCase()== "JPG".toUpperCase() || fileName.toUpperCase() == "BMP".toUpperCase()
					|| fileName.toUpperCase()== "JPEG".toUpperCase() || fileName.toUpperCase() == "PNG".toUpperCase()
					|| fileName.toUpperCase()== "GIF".toUpperCase() || fileName.toUpperCase() == "PDF".toUpperCase()))
			{
				alert("开通验收附件上传文件类型错误（仅支持类型：JPG、BMP、JPEG、PNG、GIF、PDF）");
				verifyResult = false;
				return false;

			}
		}else{
			
			var fileName = oldFileArry[oldFileArry.length - 1];
			if(!(fileName.toUpperCase()== "JPG".toUpperCase() || fileName.toUpperCase() == "BMP".toUpperCase()
					|| fileName.toUpperCase()== "JPEG".toUpperCase() || fileName.toUpperCase() == "PNG".toUpperCase()
					|| fileName.toUpperCase()== "GIF".toUpperCase() || fileName.toUpperCase() == "PDF".toUpperCase()))
			{
				alert("开通验收附件上传文件类型错误（仅支持类型：JPG、BMP、JPEG、PNG、GIF、PDF）");
				verifyResult = false;
				return false;

			}
		}
	}
	return verifyResult;
}



/**
验证是否选择结束

**/

function checkJs()
{

	
   //取省BBOSS反馈管理信息数据
	var bossManage = new Wade.DatasetList();
    var rowData = $.table.get("productParamTable2").getTableData(null, true);
     var bbossFlow="结束";
	 var jisflag="0";
	 var  vvv="0";
	for(var i=0;i<rowData.length;i++)
	{
   	   var tmpData = new Wade.DataMap();
		tmpData.put("PARAM_CODE", rowData.get(i).get("PARAM_CODE").substring(4));
		tmpData.put("PARAM_NAME", rowData.get(i).get("PARAM_NAME"));
		
		var bbossFlow = $("#"+"PARAM_FLAG_"+rowData.get(i).get("PARAM_CODE")).val();
		if(bbossFlow=="1") //说明是流程的选项
		{
			var vvv = rowData.get(i).get("PARAM_VALUE");
		}
	}
	
		 if( vvv.substring(vvv.lastIndexOf("_")+1)=="主办省结束流程")
		{   	
		if(confirm("确定结束流程?"))
		return true;
		else   return false;		
	   
		}
		return true;
		
}
/* *
下发管理信息非空校验
chenyi
 */
function  verifyAll(){
        var result1 =$.validate.verifyAll('orderinfotabset')
		var result2 = $.validate.verifyAll('productManageInfos');
		
	if (result1==true&&result2==true){
		return true;
	}else{
		return false;
	}
}


//管理节点校验
function bbossManageconfirm(){
	//产品属性校验
	var result1 =productParamCommit();
	
	//管理节点属性校验
	var result2 =bbossManageCommit();
	if (result1==true&&result2==true ){
		return true;
	}
	else{
	 	return false;
	}
}
//管理节点属性校验
function bbossManageCommit(){
	//基本校验，例如非空校验
	var result = $.validate.verifyAll('productManageInfos');
	if (result==false){
		return false;
	}
	else
	 return true;
}

/*
 *集团BBOSS产品参数页面提交校验统一接口
 */
function productParamCommit(){

	//基本校验，例如非空校验
	var result = $.validate.verifyAll('orderinfotabset');
	if (result==false){
		return false;
	}
	
	// ICB参数必填校验，适用于场景，勾选了ICB参数，但没点开填值的情况
	var verifyICB = checkICBIsNotEmpty();
	if (verifyICB == false) {
		return false;
	}
	
	//获取所有对象
	var attrParams=$('[id =PRODUCT_PARAM_CODE]');
	
	
	//返回值定义
	var verifyResult = true;
	attrParams.each(function(){
   	   //获取输入框对象
   	    var paramCode = $.attr(this,"value");
		var inputObj = $("#input_"+paramCode);
		
		//某些属性需要进一步验证
		verifyResult =checkProductParamValue(paramCode);
		if(verifyResult==false){
			return false;
		}
		
		//互联网专线 专线所在省客户编码 需分省支付时必填
		if("1112083405"==paramCode || "1112053405"==paramCode || "1112053409"==paramCode)
		{
		 var tradeId = $("#MERCH_TRADE_ID").val();
    	 var group_id=inputObj.val();
			verifyResult=verifyInterNetGroupInfo(tradeId,group_id,inputObj,paramCode);
			if(verifyResult==false){
				return false;
			}
		}else{
		//获取调用的js方法名
		var methodName =inputObj.attr('commitMethodName');
		//判断方法名是否存在，如果不存在则直接返回
		if(methodName!=null && methodName != "undefined"){
			//获取产品的既有值
			var oldValue =$('#OLDVALUE_'+paramCode).val();
			//获取当前属性的属性值
			var attrValue = inputObj.val();
			verifyResult = window[methodName](inputObj,paramCode,oldValue,attrValue);
			if(verifyResult==false){
				return false;
			}
		}	
		}	
    });
	return verifyResult;
}

function  verifyInterNetGroupInfo(tradeId,group_id,inputObj,paramCode){
	
	var result=false;
	ajaxSubmit('orderinfotabset', 'verifyInterNetGroupInfo','&MERCH_TRADE_ID='+tradeId+'&GROUP_ID='+group_id, '',function success(data){
					inputObj.val(data.get("MP_GROUP_CUST_CODE"));
					//获取调用的js方法名
					var methodName =inputObj.attr('commitMethodName');
					//判断方法名是否存在，如果不存在则直接返回
					if(methodName!=null && methodName != "undefined"){
						//获取产品的既有值
					var oldValue =$('#OLDVALUE_'+paramCode).val();
					//获取当前属性的属性值
					var attrValue = inputObj.val();
					var verifyResult = window[methodName](inputObj,paramCode,oldValue,attrValue);
					if(verifyResult==false){
						result=false;
						}
					}	;
				result=  true;
			},
	function(e,i){
			$.showErrorMessage("专线所在省客户编码对应集团公司EC编码不存在");
			result= false;
	    },
	    {async:false}
	);
			
		return result;	
		
}

/**
 * 弹出页面使用
 * 将BBOSS产品的资费信息和产品参数信息放到指定的控件中
 * modify by weixb3 2013-4-22
 */
function setProductParamsTrade()
{
	
	$.beginPageLoading("业务受理中...");

	var manageInfo=new Wade.DataMap($("#MANAGE_INFO_HIDDEN").val());
	//manageInfo.put("flag",true);//管理借点标识，拼参数时使用
	var bossManage = new Wade.DatasetList();
	
	//取出商品合同附件信息
	var bbossUploadId = $("#uploadtest").val();
	
	//取省BBOSS反馈管理信息数据
	var rowData = $.table.get("productParamTable2").getTableData(null, true);
	for (var i = 0; i < rowData.length; i++)
	{
		var tmpData = new Wade.DataMap();
		tmpData.put("PARAM_CODE", rowData.get(i).get("PARAM_CODE").substring(4));
		tmpData.put("PARAM_NAME", rowData.get(i).get("PARAM_NAME"));
		
		var bbossFlow = $("#"+"PARAM_FLAG_"+rowData.get(i).get("PARAM_CODE")).val();
		if(bbossFlow=="1") //说明是流程的选项
		{
			var vv = rowData.get(i).get("PARAM_VALUE");
			var s = rowData.get(i).get("PARAM_CODE").substring(4);
			manageInfo.put("BbossFlow", $('#PARAM_VALUE_'+rowData.get(i).get("PARAM_CODE")).val());
			tmpData.put("PARAM_VALUE",vv.substring(vv.lastIndexOf("_")+1));
		}
		else   
		{
			tmpData.put("PARAM_VALUE", rowData.get(i).get("PARAM_VALUE"));
		}
		bossManage.add(tmpData);
	}
	manageInfo.put("MANAGE_INFO",bossManage);
	manageInfo.put("UPLOAD_ID",bbossUploadId);
	
	setAllProductParamInfo(manageInfo);
	var submitData = selectedElements.getSubmitData();
	var PRODUCTS_ELEMENT =  new Wade.DataMap(); 
	PRODUCTS_ELEMENT.put($("#PRODUCT_ID").val(),submitData);
	manageInfo.put("PRODUCTS_ELEMENT",PRODUCTS_ELEMENT);
	
	
	$("#MANAGE_INFO_HIDDEN").val(manageInfo.toString());
	
    var trade_id = $("#TRADE_ID").val();
    var bboss_user_id = $("#BBOSS_USER_ID").val();
    var flowPoint = $("#FLOWPOINT").val();
    var product_number = $("#PRODUCT_ID").val();
	var merch_trade_id = $("#MERCH_TRADE_ID").val();
	var orderId = $("#ORDER_ID").val();
	var eparchy_code =$("#GRP_USER_EPARCHYCODE").val();
	//关闭popup
	ajaxSubmit(this, 'serverBbossFlow', '&FLOWPOINT='+flowPoint+'&MANAGE_INFO_HIDDEN='+manageInfo.toString()+'&PRODUCT_NUMBER='+product_number+'&BBOSS_USER_ID='+bboss_user_id+'&TRADE_ID='+trade_id+'&MERCH_TRADE_ID='+merch_trade_id+'&ORDER_ID='+orderId+'&GRP_USER_EPARCHYCODE='+eparchy_code, '',function success(){
		alert("crm侧数据处理成功，已发送服务开通侧，选择【确定】跳转到当前页面");
		setPopupReturnValue('','');
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
			showDetailErrorInfo(error_code,error_info,derror);
			$.endPageLoading();
	    });

	
}

/**
 * 弹出页面使用
 * 将BBOSS产品的资费信息和产品参数信息放到指定的控件中 ,用在发送受理报文
 * modify by weixb3 2013-4-25
 */
function setProductParamsTradeAll(method)
{
	$.beginPageLoading("业务受理中...");
	
//	校验资费包
	var Flag = checkFeePackage();
	if(false == Flag){
		return;
	}
	
	var manageInfo=new Wade.DataMap($("#MANAGE_INFO_HIDDEN").val());
	
	//取省BBOSS反馈管理信息数据
	var bossManage = new Wade.DatasetList();
	
	setAllElementInfo(manageInfo);
	
	setAllProductParamInfo(manageInfo);
	
	var trade_id = $("#TRADE_ID").val();
    var bboss_user_id = $("#BBOSS_USER_ID").val();
    var product_id = $("#PRODUCT_ID").val();
    var mproduct_id = $("#MPRODUCT_ID").val();
    var product_offer_id = $("#PRODUCT_OFFER_ID").val();
    try{
	    //获取合同附件信息
	    var strAttInfos = getFrame("frameProductOrder").window.$.table.get("dataList").getTableData("X_TAG,ATT_TYPE_CODE,ATT_CODE,CONT_NAME,ATT_NAME_filename");
	    for(var i=0;i<strAttInfos.length;i++){
	    	strAttInfos.get(i).put("ATT_NAME",strAttInfos.get(i).get("ATT_NAME_filename"));
		}
	    getFrame("frameProductOrder").window.$("#ATT_INFOS").val(strAttInfos)
	    var attrInfoList = new Wade.DatasetList(getFrame("frameProductOrder").window.$("#ATT_INFOS").val());
	    //获取审批人信息
	    var strAuditorInfos = getFrame("frameProductOrder").window.$.table.get("dataList2").getTableData("X_TAG,AUDITOR,AUDITOR_TIME,AUDITOR_DESC");
	    getFrame("frameProductOrder").window.$("#AUDITOR_INFOS").val(strAuditorInfos);
	    var auditorInfoList = new Wade.DatasetList(getFrame("frameProductOrder").window.$("#AUDITOR_INFOS").val());
	    //获取联系人信息
	    var strContactorInfos = getFrame("frameProductOrder").window.$.table.get("dataList3").getTableData("X_TAG,CONTACTOR_TYPE_CODE,CONTACTOR_NAME,CONTACTOR_PHONE,STAFF_NUMBER");
	    getFrame("frameProductOrder").window.$("#CONTACTOR_INFOS").val(strContactorInfos);
	  	var contactorInfoList = new Wade.DatasetList(getFrame("frameProductOrder").window.$("#CONTACTOR_INFOS").val());
	  	var tag1 = 0;
			var tag2 = 0
			for(var i=0;i<contactorInfoList.length;i++){
				var data = contactorInfoList.get(i);
				
				if(data.get("CONTACTOR_TYPE_CODE")==2){
					tag1 = 1;
				}
				if(data.get("CONTACTOR_TYPE_CODE")==5){
					tag2 = 1;
				}
			}
	  	
  	}catch(e){
  		attrInfoList = "";;
  		auditorInfoList ="";;
  		contactorInfoList ="";;
  	}
  		if (undefined == strAttInfos || '' == strAttInfos || 0 == strAttInfos.length)
  	{
  		alert('请上传合同附件');
  		$.endPageLoading();
  		return;
  	}
  	if (undefined == contactorInfoList || '' == contactorInfoList || 0 == contactorInfoList.length)
  	{
  		alert('请填写联系人信息');
  		$.endPageLoading();
  		return;
  	}else if(tag1 == 0){
			//联系人信息表格，客户经理信息需要新增记录
			alert("\u8054\u7cfb\u4eba\u4fe1\u606f\u8868\u683c\uff0c\u5ba2\u6237\u7ecf\u7406\u4fe1\u606f\u9700\u8981\u65b0\u589e\u8bb0\u5f55");
			$.endPageLoading();
			return false;
		}else if(tag2 == 0){
			//联系人信息表格，订单提交人员信息需要新增记录
			alert("\u8054\u7cfb\u4eba\u4fe1\u606f\u8868\u683c\uff0c\u8ba2\u5355\u63d0\u4ea4\u4eba\u5458\u4fe1\u606f\u9700\u8981\u65b0\u589e\u8bb0\u5f55");
			$.endPageLoading();
			return false;
		}
  	
    if (undefined == product_offer_id)
    {
    	product_offer_id='';
    }
    var product_order_id = $("#PRODUCT_ORDER_ID").val();
    if (undefined == product_order_id)
    {
    	product_order_id='';
    }
    var merch_spec_code = $("#MERCH_SPEC_CODE").val();
    if (undefined == merch_spec_code)
    {
    	merch_spec_code='';
    }
    var product_spec_code = $("#PRODUCT_SPEC_CODE").val();
    if (undefined == product_spec_code)
    {
    	product_spec_code='';
    }
	//add chenyi submit传值太大 需放到域中传值
	//$("#MANAGE_INFO_HIDDEN").val(manageInfo.toString());
	//关闭popup，显示提示信息
	ajaxSubmit(this, 'serverBbossFlow', '&PRODUCT_SPEC_CODE='+product_spec_code+'&MERCH_SPEC_CODE='+merch_spec_code+'&PRODUCT_ORDER_ID='+product_order_id+'&PRODUCT_OFFER_ID='+product_offer_id+'&MANAGE_INFO_HIDDEN='+manageInfo.toString()+'&PRODUCT_ID='+product_id+'&MPRODUCT_ID='+mproduct_id+'&BBOSS_USER_ID='+bboss_user_id+'&TRADE_ID='+trade_id+
		'&ATT_INFOS='+attrInfoList+"&AUDITOR_INFOS="+auditorInfoList+"&CONTACTOR_INFOS="+contactorInfoList, '',function success(){
		alert("受理数据保存成功!");
		$.endPageLoading();
		setPopupReturnValue('','');
		
	},
	function(error_code,error_info,derror){
			showDetailErrorInfo(error_code,error_info,derror);
			$.endPageLoading();
	    });
	
}


function afterMessage()
{
	//MessageBox.success("提示","操作成功！","");
	
}

/**
 * 点下一步的时候，进行验证
 */
function validateParamForNext(method)
{
	 if(method=="CrtUs"||method=="ChgUs")
	 {
		 if(getElementValue("operType")=="")
		 {
			 alert("请选择商品操作类型！");
			 return false;
		 }
	 }
	 return setCheckedProducts(method);
}

/**
 * 产品包元素数量验证
 * @returns 验证结果
 * @author chenkh
 */
function checkFeePackage() {
	var productId = $("#PRODUCT_ID").val();
	var packageInfoList;
	// 取得服务包元素
	Wade.httphandler.submit('',
			'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
			'qryPackageInfo', '&PRODUCT_ID=' + productId, function(d) {
				packageInfoList = d.map.result;
			}, function(e, i) {
				$.showErrorMessage("操作失败");
				result = false;
			}, {
				async : false
			});

	var productElements = selectedElements.selectedEls;
	// 循环判断包中元素是否满足规则
	for ( var i = 0; i < packageInfoList.length; i++) {
		var packageId = packageInfoList.get(i, "PACKAGE_ID");
		var element_min = packageInfoList.get(i, "MIN_NUMBER");
		var element_max = packageInfoList.get(i, "MAX_NUMBER");
		var forceTag = packageInfoList.get(i, "FORCE_TAG");
		var num = 0;
		if (forceTag != "1") {
			continue;
		}
		num = selectedElementNumber(packageId, productElements);
		// 判断最小元素个数
		if (-1 != element_min && num < element_min) {
			$.endPageLoading();
			alert(packageInfoList.get(i, "PACKAGE_NAME") + "包中最少需要" + element_min
					+ "个元素");

			return false;
		}
		// 判断最大元素个数
		if (-1 != element_max && num > element_max) {
			$.endPageLoading();
			alert(packageInfoList.get(i, "PACKAGE_NAME") + "包中最多有" + element_max
					+ "个元素");

			return false;
		}
	}

	return true;
}

function selectedElementNumber(pid, elements) {
	var num = 0;
	elements.each(function(item, index, totalcount) {
		var packageId = item.get("PACKAGE_ID");
		var modifyTag = item.get("MODIFY_TAG");
		if (packageId == pid && (modifyTag == "0" || modifyTag == "exist"))
			num++;
	});
	return num;
}

/** chenyi
 * 弹出产品参数页面
 */
function popProductParamPage(e)
{
	//$.beginPageLoading();
	var method = $(e).attr('method');
	
	var productId = $(e).attr('productId');//产品id
	var userid = $(e).attr('userid');//子用户id
	var tradeid = $(e).attr('tradeid');//产品trade_id
	var flowInfo = $(e).attr('flowInfo');//BBOSS_4000
	var myFlag = $(e).attr('myFlag');//0
	var productName = $(e).attr('productName');
	var orderid = $(e).attr('orderid');
	var eparchy_code = $(e).attr('eparchy_code');
	var productOfferId = $(e).attr('productOfferId');
	var productOrderId = $(e).attr('productOrderId');
	var merchSpecCode = $(e).attr('merchSpecCode');
	var productSpecCode = $(e).attr('productSpecCode');

	var pospecnumber = $(e).attr('pospecnumber');
	
	if(method=="CtrOpe" || method=="Ctr")
	{  
		var ibsysid=$("#IBSYSID").val();
		var nodeid=$("#NODE_ID").val();
		var bpmtempleid=$("#BPM_TEMPLET_ID").val();
		var maintempleid=$("#MAIN_TEMPLET_ID").val();
		var workid=$("#WORK_ID").val();
		if(null==ibsysid){
			ibsysid="";
		}
	}
	
	//组件参数 BUSI_TYPE
	var busi_type;
	
	if(method=="CtrOpe")
	{  
		popupPage('csserv.group.param.bboss.bbossManageInfo.BbossManageOper', 'init', '&IS_EXIST=true&PRODUCT_ID='+productId+'&BBOSS_USER_ID='+userid+'&ORDER_ID='+orderid+'&GRP_USER_EPARCHYCODE='+eparchy_code+'&TRADE_ID='+tradeid+'&FLOWINFO='+flowInfo+"&MYFLAG="+myFlag+"&ibsysid="+ibsysid+"&workid="+workid+"&nodeid="+nodeid+"&bpmtempleid="+bpmtempleid+"&maintempleid="+maintempleid,productName+'产品信息','1100','650', 'product_pop');
	}
	if(method=="HasSendOpe")  //调一样的，给用户显示看
	{
		popupPage('csserv.group.param.bboss.bbossManageInfo.BbossManageOper', 'init', '&IS_EXIST=true&PRODUCT_ID='+productId+'&BBOSS_USER_ID='+userid+'&ORDER_ID='+orderid+'&GRP_USER_EPARCHYCODE='+eparchy_code+'&TRADE_ID='+tradeid+'&FLOWINFO='+flowInfo+"&MYFLAG="+myFlag,productName+'产品信息','750','650', 'product_pop');
	}
	if(method=="Ctr") //查看预受理信息已归档的数据
	{
		if(e.ibsysid!=null&&e.ibsysid!="")
		{
			popupPage('csserv.group.param.bboss.bbossManageInfo.BbossManageCreate', 'init', '&refresh=true&IS_EXIST=true&PRODUCT_ID='+productId+'&BBOSS_USER_ID='+userid+'&ORDER_ID='+orderid+'&GRP_USER_EPARCHYCODE='+eparchy_code+'&TRADE_ID='+tradeid+'&FLOWINFO='+flowInfo+"&MYFLAG="+myFlag+"&PRODUCT_OFFER_ID="+productOfferId+"&PRODUCT_ORDER_ID="+productOrderId+"&MERCH_SPEC_CODE="+merchSpecCode+"&PRODUCT_SPEC_CODE="+productSpecCode+"&ibsysid="+ibsysid+"&workid="+workid+"&nodeid="+nodeid+"&bpmtempleid="+bpmtempleid+"&maintempleid="+maintempleid,productName+'产品信息', '1100', '650', 'product_pop');
		}else{
			busi_type = 'CrtUs'
		popupPage('csserv.group.param.bboss.bbossManageInfo.BbossManageCreate', 'init', '&IS_EXIST=true&PRODUCT_ID='+pospecnumber+"&PRODUCT_ID2="+productId+"&BUSI_TYPE="+busi_type+"&PRODUCT_OPER_TYPE=1"+'&BBOSS_USER_ID='+userid+'&ORDER_ID='+orderid+'&GRP_USER_EPARCHYCODE='+eparchy_code+'&TRADE_ID='+tradeid+'&FLOWINFO='+flowInfo+"&MYFLAG="+myFlag+"&PRODUCT_OFFER_ID="+productOfferId+"&PRODUCT_ORDER_ID="+productOrderId+"&MERCH_SPEC_CODE="+merchSpecCode+"&PRODUCT_SPEC_CODE="+productSpecCode,productName+'产品信息', '1100', '650', 'product_pop');
		
		}
		
	}  

}


function afterSendData()
{
	
	var result = this.ajaxDataset.get(0, "RESULT");
	if(result=="1")
	{
	alert("发送成功");
	}
	if(result=="2")
	{
    alert("取消受理成功");
	}

}



 /**
  * 产品参数变化时，影响依赖他的其他参数
  * @param e
  * @return
  */
function productParamValueOnchange(e)
{
	//从数据库中取出每个控件的PARA_CODE6,和当前控件的PARA_CODE1,和当前控件的值进行比较,如果相等，就必须填
	
	var paraCode6 = e.paraCode + "=" +e.value;
	for(var i=1;i<productParamTable.rows.length;i++)
	{
		var tableRow = productParamTable.rows[i];
		//对PARA_CODE5=1和PARA_CODE6符合条件的表格进行操作
		if(tableRow.cells[2].innerHTML=="1"&&tableRow.cells[3].innerHTML.indexOf(e.paraCode+"=")!=-1)
		{
			var relationParaObj = getElement("input_"+tableRow.cells[1].innerHTML);//关联的输入控件
			if(tableRow.cells[3].innerHTML==paraCode6)
			{
				tableRow.style.display="block";
				relationParaObj.setAttribute('nullable', 'no');
			}
			else
			{
				tableRow.style.display="none";
				relationParaObj.setAttribute('nullable', '');
				tableRow.cells[7].innerHTML="";
			}
		}
	}
}



/*
 * 弹出参数组的页面
 */
function popParamGroup(e)
{
	popupDialog('group.param.bboss.createuser.ParamGroup', 'init', '&PRODUCT_ID='+e.productId+'&PARA_CODE='+e.paraCode+'&PARA_VALUE='+e.value+'&BBOSS_USER_ID='+e.userId+'&refresh=true','','750','650');
	
}


//控制参数框保持在可视区域内
function controlProductTreeChg()
{
	showLayer("paramOpr");
	var paramOpr = System.get("paramOpr");
	paramOpr.position("absolute");
	paramOpr.setTop(window.event.y+50);
}


//控制参数框保持在可视区域内 影藏
function controlProductTreehHide()
{
	var paramOpr = document.getElementById("paramOpr");
	hideLayer('paramOpr');
}




function changeManageValue(tag)
{
	 if(verifyField(tag) && checkStartWith(tag))
	 {
		 var class_type = tag.type;
		 if('text' != class_type)
		 {
		 	tag.parentNode.parentNode.parentNode.parentNode.cells[3].innerText = tag.value;
		 }
		 else 
		 {
		 	tag.parentNode.parentNode.parentNode.cells[3].innerText = tag.value;
		 }
	 }
	 else 
	 {
	 	tag.value = "";
	 }
}


function changeMemberValue(tag)
{
	 if(verifyField(tag))
	 {
		 var class_type = tag.type;
		 if('text' != class_type)
		 {
		 	tag.parentNode.parentNode.parentNode.parentNode.cells[5].innerText = tag.value;
		 }
		 else 
		 {
		 tag.parentNode.parentNode.parentNode.cells[5].innerText = tag.value;
		 }
	 }
	 else 
	 {
	 	tag.value = "";
	 }
}




function changeValue(tag)
{	
	var class_type = tag.type;
	if('text' != class_type)
	{
		tag.parentNode.parentNode.parentNode.parentNode.parentNode.cells[3].innerText = tag.value;
	}
	else 
	{
		tag.parentNode.parentNode.parentNode.parentNode.cells[3].innerText = tag.value;
	}
}

/**
 *@description 拼装产品属性信息
 *@auhtor weixb3
 *@date 2013-09-14
 */
 function setAllProductParamInfo(manageInfo){
 	//1- 获取所有参数对象
	var attrParams=$('[id =PRODUCT_PARAM_CODE]');
	
	var flag=manageInfo.get("flag");
	//2- 定义新参数集
	var newParamObj=new Wade.DatasetList();
	
	//3- 循环参数对象
	attrParams.each(function(){
		//3-1 获取属性编号
		var paramCode = $.attr(this,"value");
		
		//3-2 循环新参数集，查看当前属性编号是否存在于新参数集中
		var isFind = false;
		for(var i=0;i<newParamObj.length;i++)
		{		
			var tmpData = newParamObj.get(i);
			if(tmpData.get("ATTR_CODE")==paramCode)//有重复的
			{
				isFind =true;
				break;			
			}					
		}
		
		//3-3 如果新参数集中没有，则需要将该属性编号对应的所有属性添加进产品参数集(注意属性组情况)
		if(isFind==false){
			var paramValueId = "input_"+paramCode;
			var groupAttrId = "GROUPATTR_FLAG_"+paramCode;
			var paramValues = $('[id='+paramValueId+']');	
			var attrGroupFlags = $('[id='+groupAttrId+']');
			paramValues.each(function(index,item,totalcount){
				//新产品参数对象
				var newParamData = new Wade.DataMap();
				newParamData.put("ATTR_CODE",paramCode);
				newParamData.put("ATTR_NAME",$("#PARAM_NAME_"+paramCode).text());
				newParamData.put("ATTR_GROUP",$.attr(attrGroupFlags.get(index),"value"));
				//拼写属性的前后缀
				var front_part=$("#FRONT_PART_"+paramCode).text();
				var after_part=$("#AFTER_PART_"+paramCode).text()
				var attr_value=$.attr(paramValues.get(index),"value")
			   	attr_value=front_part+attr_value+after_part;
		    	newParamData.put("ATTR_VALUE",attr_value);
				newParamObj.add(newParamData);
			});			
		}
    });												
	
	
	//4- 获取所有的老参数对象
	var old=$("#OLD_PRODUCT_PARAMS").val().toString();
	var oldDataset = new Wade.DatasetList(old);
	//因为升级后集团受理与变更的参数页面合并了，而OLD_PRODUCT_PARAMS元素仅供变更使用，因此受理时赋空值
	if(null==$("#GRP_USER_ID").val() || ""==$("#GRP_USER_ID").val()){
		oldDataset =new Wade.DatasetList("");
	}
	if(null!=$("#BBOSS_USER_ID").val() || ""!=$("#BBOSS_USER_ID").val()){
		var oldDataset = new Wade.DatasetList(old);
	}
	
	//5- 定义老参数集
	var oldParamObj=new Wade.DatasetList();//老产品参数对象集合
	
	//6- 循环老参数对象，将老参数添加至老参数集	
	for(var i=0;i<oldDataset.length;i++)
	{
		var oldParamData = new Wade.DataMap();
		oldParamData.put("ATTR_CODE",oldDataset.get(i,"ATTR_CODE"));
		oldParamData.put("ATTR_NAME",oldDataset.get(i,"ATTR_NAME"));
		oldParamData.put("ATTR_VALUE",oldDataset.get(i,"ATTR_VALUE"));
		if (oldDataset.get(i, "ATTR_GROUP") != undefined) {
			oldParamData.put("ATTR_GROUP", oldDataset.get(i, "ATTR_GROUP"));
		} else {
			oldParamData.put("ATTR_GROUP", "");
		}
		oldParamData.put("OLD_PARAM_VALUE", oldDataset.get(i, "OLD_PARAM_VALUE"));
		oldParamObj.add(oldParamData);
	}
	
	
	
	//7- 比较新老参数集,确定参数状态
	var tmpDs = compareParamDataset(newParamObj,oldParamObj,"ATTR_CODE","ATTR_VALUE"); 
	
	//8- 将产品参数对象保存进商产品信息中(manageInfo)
	var PRODUCT_PARAM = new Wade.DataMap();
   	PRODUCT_PARAM.add($("#PRODUCT_ID").val(),tmpDs);
	if(manageInfo.get("PRODUCT_PARAM")!=null){
		PRODUCT_PARAM =manageInfo.get("PRODUCT_PARAM");
	}
	manageInfo.put("PRODUCT_PARAM",PRODUCT_PARAM);
 }
 
 
 /*
  * ICB参数必填校验
  * liuxx3 2014 09-17
  */
 function checkICBIsNotEmpty() {

 	// ICB参数值不为空
 	var isNotEmpty = true;

 	// 获取产品包元素以及元素参数值（PRODUCT_ID、PACKAGE_ID、ELEMENT_ID、ATTR_PARAM）
 	var productElements = selectedElements.selectedEls;

 	// 循环资费元素
 	productElements.each(function(item, index, totalcount) {
 		// 过滤勾选的值，剔除无关的数据
 		if (item.get("MODIFY_TAG") == "0" || item.get("MODIFY_TAG") == "1"
 				|| item.get("MODIFY_TAG") == "2") {

 			// 获取ICB资费参数
 			var icbParam = item.get("ATTR_PARAM");
 			// 获取元素名称
 			var eleName = item.get("ELEMENT_NAME");
 			// ATTR_PARAM不为NULL，则为带ICB的资费；
 			if (icbParam != "" && icbParam != null) {

 				var icbParamValue = "";
 				for (var i = 0; i < icbParam.length; i++) {

 					// ICB资费如果没有填任何资费参数，则提示
 					icbParamValue += icbParam.get(i, "ATTR_VALUE");
 				}

 				if (icbParamValue == "") {

 					alert(eleName + "的资费参数不能全为空！");
 					isNotEmpty = false;
 				}
 			}
 		}
 	});

 	return isNotEmpty;
 }
/*
 *@description 设置产品的资费与服务信息
 *@author weixb3
 *@date 2013-10-03
 */
function setAllElementInfo(manageInfo)
{
	//1- 将产品资费与服务的提交信息添加至商产品信息中
	var productElements = selectedElements.getSubmitData();
	var PRODUCTS_ELEMENT =  new Wade.DataMap(); 
	PRODUCTS_ELEMENT.put($("#PRODUCT_ID").val(),productElements);
	manageInfo.put("PRODUCTS_ELEMENT",PRODUCTS_ELEMENT);
}

function showTip(e)
{
	$.showToolTip($(e).attr('ID'),$(e).attr('DESC'),null,true,200);
}
function hideTip(e)
{
	$.hiddenToolTip($(e).attr('id'));
}