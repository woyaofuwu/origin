/**$Id: UserParamInfo.js,v 1.1 2013/05/22 14:30:09 wangyf6 Exp $*/
/**
 * 作用：用来初始化页面的显示,值会在productInfo.java里查出来后隐藏在各个服务的属性隐藏中
 * 		如果没有查出来的话，再调用getServiceParamsByajax查一把
 */
function initUserParamInfo() {
	var serviceId="";	
	var urlParts = document.URL.split("?"); 
	var parameterParts = urlParts[1].split("&"); 
	var svcparamvalue="";
	for (i = 0; i < parameterParts.length; i++) 
	{ 
		var pairParts = parameterParts[i].split("="); 
		var pairName = pairParts[0]; 
		var pairValue = pairParts[1]; 
		
		if(pairName=="HIDDEN_NAME")
		{
		  try
		  {
		  	  svcparamvalue=window.parent.document.getElementById(pairValue).value;
		  }
		  catch(e)
		  {
			  svcparamvalue="";
		  }
		}
		if(pairName=="SERVICE_ID")
		{
		      serviceId=pairValue;
		}
	} 
	if(svcparamvalue==""||svcparamvalue=="[]")//没有找到已存在的参数值 采用ajax异步调用 从数据库查询
	{
			ajaxGet(this,'getServiceParamsByajax','SERVICE_ID='+serviceId,null,null,true,function(){putpagedataByajax(serviceId);});
	}
	else 
	{
		var dataset = new Wade.DatasetList(svcparamvalue);
		var datasetsize=dataset.getCount();
		if(datasetsize<=1) //表示没有服务的详细参数信息(因为约定第一条记录为是否需要校验)
		{
			ajaxGet(this,'getServiceParamsByajax','SERVICE_ID='+serviceId,null,null,true,function(){putpagedataByajax(serviceId);});
		}
		else
		{
			putpagedata(dataset,serviceId);
		}
	}
}

/**
 * 作用：如果URL中没有带参数，AJAX刷新后的处理
 */
function putpagedataByajax(serviceId)
{
	var serverParamdataset=this.ajaxDataset;
	putpagedata(serverParamdataset,serviceId);
}

/**
*作用：自动生成服务代码扩展码
*/
function createBizCodeExtend(){
	ajaxGet(this,'createBizCodeExtend','',null,null,true,afterCreateBizCodeExtend);
}

function afterCreateBizCodeExtend(){
	  var extendCode = this.ajaxDataset.get(0,"EXTEND_CODE");
	  if (extendCode == "" || extendCode == null){
	    alert("自动生成错误，请手动输入!");
	  }else{
	     var accessMode = getElementValue("pam_ACCESS_MODE");
	     getElement("pam_SVRCODETAIL").value = extendCode.substring(4);
	     checkAccessNumber();
	  }
	}


/**校验服务代码是否可用*/
function checkAccessNumber(){
	if (!verifyField(getElement("pam_SVRCODETAIL"))) {
		return;
	}
    var bizInCode = getElementValue("pam_SI_BASE_IN_CODE");
    var svcCode = getElementValue("pam_SVRCODETAIL");
    var length = svcCode.length;
    var accessMode = getElementValue("pam_ACCESS_MODE");

   var accessNumber = bizInCode+svcCode;
   var tradeData=window.parent.document.getElementById('tradeData').value;
   var tradeStr=new Wade.DataMap(tradeData);
   var custInfo=tradeStr.get("CUST_INFO");
   var groupId = custInfo.get("GROUP_ID","");
   ajaxGet(this,'getDumpIdByajax','&GROUP_ID='+groupId+'&ACCESSNUMBER='+accessNumber,null,null,true,function() {afterCheckAccessNumber(accessNumber);});
}

/**校验服务代码AJAX刷新后的处理*/
function afterCheckAccessNumber(accessNumber){
	var flag = this.ajaxDataset.get(0,"ISCHECKAACCESSNUMBER");
	if(flag == "true"){
		alert("服务代码可以使用！");
		getElement('pam_BIZ_IN_CODE').value=accessNumber.replace(/(^\s*)|(\s*$)/g, "");
	}else{
		alert("服务代码["+accessNumber+"]不能使用，请重新生成！");
		getElement('pam_BIZ_IN_CODE').value="";
	}
}

/**
 * 作用：从url取关键值,并根据关键值从父页面取值
 */
function putpagedata(dataset,serviceId)
{
	var paramVerifySucc=dataset.get(0); //如果曾经点过确认按钮，就会总是曾经点过，想抵赖是不行的
	  getElement("PARAM_VERIFY_SUCC").value=paramVerifySucc.get("PARAM_VERIFY_SUCC","");//设置参数是否已经校验成功的值到页面

	  var paramMap=dataset.get(1);            //改为从1取，因为 dataset的0已存放了一个表示是否点过确认按钮的状态
	  var platsvcdata=paramMap.get('PLATSVC');//得到的platsvc表数据IData结构
	  platsvcdata.eachKey(                    //将platsvcdata参数 填充到adcServicParamsForm
	 	 function(key)
	 	 {
	     	try
	     	{
	     		var tempElement = getElement(key);
	     		if(tempElement != "undefined" && tempElement != ""){
	     			tempElement.value=platsvcdata.get(key,"");
	     		}
	     	}
	     	catch(e)
	     	{
	     	}
	     }
	    );
}

/**
 * 作用：如果URL中没有带参数，AJAX刷新后的处理
 */
function putpagedataByajax(serviceId) {
	var serverParamdataset = this.ajaxDataset;
	putpagedata(serverParamdataset, serviceId);
}

function setCancleData(obj) {
	var serviceId="";	
	var urlParts = document.URL.split("?"); 
	var parameterParts = urlParts[1].split("&"); 
	var svcparamvalue="";
	for (i = 0; i < parameterParts.length; i++) 
	{ 
		var pairParts = parameterParts[i].split("="); 
		var pairName = pairParts[0]; 
		var pairValue = pairParts[1]; 
		if(pairName=="HIDDEN_NAME")
		{
		  try
		  {
		  	svcparamvalue=window.parent.document.getElementById(pairValue).value;  
		  }
		  catch(e)
		  {
		   svcparamvalue="";
		  }
		}
		if(pairName=="SERVICE_ID")
		{
		  serviceId=pairValue;
		}
	}
	
	if(svcparamvalue==""||svcparamvalue=="[]")   //没有找到已存在的参数值 采用ajax异步调用 从数据库查询
	{
		var dataset = new Wade.DatasetList();
		var paramVerifySuccMap=new Wade.DataMap();
		paramVerifySuccMap.put("PARAM_VERIFY_SUCC","false");
		dataset.add(paramVerifySuccMap);
		svcparamvalue=dataset.toString();
	}
	
	var hiddenName=getElement('HIDDEN_NAME').value;
	var buttenName=getElement('POPUP_BTN_NAME').value;
	setReturnValue(buttenName,buttenName,[hiddenName],[svcparamvalue]);
}

function putpagedata(dataset) {
	var paramMap = dataset.get(1);
	var platsvcdata = paramMap.get('PLATSVC');//得到的platsvc表数据IData结构
	var paramVerifySucc = dataset.get(0);
	getElement("PARAM_VERIFY_SUCC").value = paramVerifySucc.get(
			"PARAM_VERIFY_SUCC", "");//设置参数是否已经校验成功的值到页面
	//将platsvcdata参数 填充到PlatSvcParamForm
	platsvcdata.eachKey(function(key) {
		try {
			getElement(key).value = platsvcdata.get(key, "");
		} catch (e) {
		}
	});
}

//------------------------------确定时用--------------------------------
function setData(obj) {

	checkSignExist();
	if (!verifyAll(getElement('platsvcparamtable'))) {
		//		disableEle();
		return;
	}
	commSubmit(); //设置值到IData
}

function changeButton(){
	
	if (getElementValue("pam_BIZ_SERV_MODE")=='02'){
		getElement("pam_BIZ_SERV_CODE").setAttribute("nullable","no");
		getElement("serv_code_class").setAttribute("className","e_required");
	}else{
		getElement("pam_BIZ_SERV_CODE").setAttribute("nullable","yes");
		getElement("serv_code_class").setAttribute("className","");
	}
	
}

function commSubmit() {
	var svcparamdata = new Wade.DataMap();
	var paramset = new Wade.DatasetList();

	var paramVerifySucc = new Wade.DataMap();
	paramVerifySucc.put("PARAM_VERIFY_SUCC", "true");
	paramset.add(paramVerifySucc);
	
	svcparamdata.put("INDUSTRY_TYPE", getElementValue("pam_INDUSTRY_TYPE"));
	svcparamdata.put("BIZ_SERV_CODE", getElementValue("pam_BIZ_SERV_CODE"));
	svcparamdata.put("AUTH_MODE", getElementValue("pam_AUTH_MODE"));
	svcparamdata.put("BASE_SERV_CODE_PROP",
			getElementValue("pam_BASE_SERV_CODE_PROP"));
	svcparamdata.put("BIZ_ATTR_TYPE", getElementValue("pam_BIZ_ATTR_TYPE"));
	svcparamdata.put("BIZ_SERV_MODE", getElementValue("pam_BIZ_SERV_MODE"));
	svcparamdata.put("DEFAULT_SIGNLANG",
			getElementValue("pam_DEFAULT_SIGNLANG"));
	svcparamdata.put("IS_TEXT_SIGN", getElementValue("pam_IS_TEXT_SIGN"));
	svcparamdata.put("TEXT_SIGN_ZH", getElementValue("pam_TEXT_SIGN_ZH"));
	svcparamdata.put("TEXT_SIGN_EN", getElementValue("pam_TEXT_SIGN_EN"));
	svcparamdata.put("BIZ_IN_CODE", getElementValue("pam_BIZ_IN_CODE"));
	svcparamdata.put("SVRCODETAIL", getElementValue("pam_SVRCODETAIL"));
	var serviceId = getElement("SERVICE_ID").value;
	
	svcparamdata.put("SERVICE_ID", serviceId);
	var params = new Wade.DatasetList();

	svcparamdata.eachKey(function(key) {
		try {
			var tmp = new Wade.DataMap();
			tmp.put("ATTR_CODE", key);
			tmp.put("ATTR_VALUE", svcparamdata.get(key, ""));
			paramset.add(tmp);
		} catch (e) {

		}
	});

	//	paramset.add(params);
	var hiddenName = getElement('HIDDEN_NAME').value;
	var buttenName = getElement('POPUP_BTN_NAME').value;
	setReturnValue(buttenName, buttenName, [ hiddenName ], [ paramset ]);

}

function afterCheckSignExist() {
	var resultdataset = this.ajaxDataset;
	var paramMap = resultdataset.get(0);
	var inProduct = paramMap.get('IN_PRODUCT');
	if (inProduct == "true") {
		var hasZh = paramMap.get("HAS_ZH");
		if (hasZh == "true") {
			alert("对不起，中文签名含有敏感字符[" + paramMap.get("PARAM_NAME") + "]，请重新输入");
			getElement("pam_TEXT_ECGN_ZH").select();
			return;
		}

		var hasEn = paramMap.get("HAS_EN");
		if (hasEn == "true") {
			alert("对不起，英文签名含有敏感字符[" + paramMap.get("PARAM_NAME") + "]，请重新输入");
			getElement("pam_TEXT_ECGN_EN").select();
			return;
		}
	}
}

/*校验中英文签名是否有敏感字符*/
function checkSignExist() {
	if(window.parent.document.getElementById('PRODUCT_ID') ==null)
	{
		return;
	}
	var productid = window.parent.document.getElementById('PRODUCT_ID').value;
	ajaxGet(this, 'getSensitiveTextByajax',
			'TEXT_ECGN_ZH=' + getElement('pam_TEXT_SIGN_ZH').value
					+ '&TEXT_ECGN_EN=' + getElement('pam_TEXT_SIGN_EN').value
					+ '&PRODUCT_ID=' + productid, null, null, true,
			afterCheckSignExist);
}
