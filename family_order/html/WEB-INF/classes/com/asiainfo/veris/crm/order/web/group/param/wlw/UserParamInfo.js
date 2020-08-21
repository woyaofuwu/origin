/**$Id: UserParamInfo.js,v 1.1 2013/05/22 14:30:09 wangyf6 Exp $*/
/**
 * ���ã�������ʼ��ҳ�����ʾ,ֵ����productInfo.java�������������ڸ������������������
 * 		���û�в�����Ļ����ٵ���getServiceParamsByajax��һ��
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
	if(svcparamvalue==""||svcparamvalue=="[]")//û���ҵ��Ѵ��ڵĲ���ֵ ����ajax�첽���� �����ݿ��ѯ
	{
			ajaxGet(this,'getServiceParamsByajax','SERVICE_ID='+serviceId,null,null,true,function(){putpagedataByajax(serviceId);});
	}
	else 
	{
		var dataset = new Wade.DatasetList(svcparamvalue);
		var datasetsize=dataset.getCount();
		if(datasetsize<=1) //��ʾû�з������ϸ������Ϣ(��ΪԼ����һ����¼Ϊ�Ƿ���ҪУ��)
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
 * ���ã����URL��û�д�������AJAXˢ�º�Ĵ���
 */
function putpagedataByajax(serviceId)
{
	var serverParamdataset=this.ajaxDataset;
	putpagedata(serverParamdataset,serviceId);
}

/**
*���ã��Զ����ɷ��������չ��
*/
function createBizCodeExtend(){
	ajaxGet(this,'createBizCodeExtend','',null,null,true,afterCreateBizCodeExtend);
}

function afterCreateBizCodeExtend(){
	  var extendCode = this.ajaxDataset.get(0,"EXTEND_CODE");
	  if (extendCode == "" || extendCode == null){
	    alert("�Զ����ɴ������ֶ�����!");
	  }else{
	     var accessMode = getElementValue("pam_ACCESS_MODE");
	     getElement("pam_SVRCODETAIL").value = extendCode.substring(4);
	     checkAccessNumber();
	  }
	}


/**У���������Ƿ����*/
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

/**У��������AJAXˢ�º�Ĵ���*/
function afterCheckAccessNumber(accessNumber){
	var flag = this.ajaxDataset.get(0,"ISCHECKAACCESSNUMBER");
	if(flag == "true"){
		alert("����������ʹ�ã�");
		getElement('pam_BIZ_IN_CODE').value=accessNumber.replace(/(^\s*)|(\s*$)/g, "");
	}else{
		alert("�������["+accessNumber+"]����ʹ�ã����������ɣ�");
		getElement('pam_BIZ_IN_CODE').value="";
	}
}

/**
 * ���ã���urlȡ�ؼ�ֵ,�����ݹؼ�ֵ�Ӹ�ҳ��ȡֵ
 */
function putpagedata(dataset,serviceId)
{
	var paramVerifySucc=dataset.get(0); //����������ȷ�ϰ�ť���ͻ��������������������ǲ��е�
	  getElement("PARAM_VERIFY_SUCC").value=paramVerifySucc.get("PARAM_VERIFY_SUCC","");//���ò����Ƿ��Ѿ�У��ɹ���ֵ��ҳ��

	  var paramMap=dataset.get(1);            //��Ϊ��1ȡ����Ϊ dataset��0�Ѵ����һ����ʾ�Ƿ���ȷ�ϰ�ť��״̬
	  var platsvcdata=paramMap.get('PLATSVC');//�õ���platsvc������IData�ṹ
	  platsvcdata.eachKey(                    //��platsvcdata���� ��䵽adcServicParamsForm
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
 * ���ã����URL��û�д�������AJAXˢ�º�Ĵ���
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
	
	if(svcparamvalue==""||svcparamvalue=="[]")   //û���ҵ��Ѵ��ڵĲ���ֵ ����ajax�첽���� �����ݿ��ѯ
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
	var platsvcdata = paramMap.get('PLATSVC');//�õ���platsvc������IData�ṹ
	var paramVerifySucc = dataset.get(0);
	getElement("PARAM_VERIFY_SUCC").value = paramVerifySucc.get(
			"PARAM_VERIFY_SUCC", "");//���ò����Ƿ��Ѿ�У��ɹ���ֵ��ҳ��
	//��platsvcdata���� ��䵽PlatSvcParamForm
	platsvcdata.eachKey(function(key) {
		try {
			getElement(key).value = platsvcdata.get(key, "");
		} catch (e) {
		}
	});
}

//------------------------------ȷ��ʱ��--------------------------------
function setData(obj) {

	checkSignExist();
	if (!verifyAll(getElement('platsvcparamtable'))) {
		//		disableEle();
		return;
	}
	commSubmit(); //����ֵ��IData
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
			alert("�Բ�������ǩ�����������ַ�[" + paramMap.get("PARAM_NAME") + "]������������");
			getElement("pam_TEXT_ECGN_ZH").select();
			return;
		}

		var hasEn = paramMap.get("HAS_EN");
		if (hasEn == "true") {
			alert("�Բ���Ӣ��ǩ�����������ַ�[" + paramMap.get("PARAM_NAME") + "]������������");
			getElement("pam_TEXT_ECGN_EN").select();
			return;
		}
	}
}

/*У����Ӣ��ǩ���Ƿ��������ַ�*/
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
