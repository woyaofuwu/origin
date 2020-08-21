var cachedCustId;

$.enterpriseLogin = (function() {
	var login, count = 0;
	if($.os && $.os.phone)
	{
		var obj = window;
		while(true)
  		{
  			var frame = obj.document.getElementById("custAuthFrame");	
   			if(frame != null &&  frame)
   			{
				login = frame.contentWindow.ecLogin;
				break;
   			}
   			obj = obj.parent;
   			count ++;
   			if (count > 2) {
				break;
			}
  		}
	}else{
		var scope = window;
		while (!(login = scope.parent.$.enterpriseLogin)) {
			scope = scope.parent;
			count++;
			if (count > 2) {
				break;
			}
		}
	}
	return login;
})();

$(function(){
	if (!ifGetEc()) {
		return;
	}	

	if(!isEsopLogin())
	{
		var custInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
		var loginCustId = custInfo.get("CUST_ID");
		if (loginCustId) {
			cachedCustId = loginCustId;
		}
		
		if($.isFunction(window["selectGroupAfterAction"]))
		{
			var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
			selectGroupAfterAction(groupInfo);
			$("#GROUP_ID_NAME").val(groupInfo.get("GROUP_ID"));
		}
	}
	else
	{
		cachedCustId = $("#CUST_ID").val();
	}

});

//是否已经进行认证
function ifGetEc()
{
	debugger;
	if(isEsopLogin())
	{
	   //esop没有登录外框，在此模拟登录对象
		var esopStr = $("#e_ESOP_INFO").text();
		if(esopStr && esopStr != "")
		{
			$.enterpriseLogin = new EnterpriseLogin("esop_group");
			var custInfo = new Wade.DataMap();
			var esopInfo = new Wade.DataMap(esopStr);
			custInfo.put("CUST_ID", esopInfo.get("CUST_ID"));
			custInfo.put("CUST_NAME", esopInfo.get("GROUP_NAME"));
			custInfo.put("GROUP_ID", esopInfo.get("GROUP_ID"));
			var groupInfo = new Wade.DataMap();
			groupInfo.put("GROUP_ID", esopInfo.get("GROUP_ID"));
			groupInfo.put("CUST_NAME", esopInfo.get("GROUP_NAME"));
			groupInfo.put("GROUP_NAME", esopInfo.get("GROUP_NAME"));
			var info = new Wade.DataMap();
			info.put("CUST_INFO", custInfo);
			info.put("GROUP_INFO", groupInfo);
			$.enterpriseLogin.setInfo(info.toString());
			return true;
		}
	}
	else if (!$.enterpriseLogin || !$.enterpriseLogin.isLogin()) 
	{
		if($("#ESOP_TAG").val() != "1"){
			MessageBox.alert("提示信息","请先在外框认证政企客户信息！", function(btn) {
				if(window.location.href.indexOf("/iorder?") > 0)
				{//互联网界面必须在外框登录集团
					closeNav();
				}
			});
		}
		return false;
	}
	return true;
}

//是否esop登陆
function isEsopLogin()
{
	if(window.location.href.indexOf("IBSYSID") > 0 || window.location.href.indexOf("ESOP_FLAT=1") > 0)
	{
		return true;
	}
	return false;
}

//页面激活时触发该方法，用于校验页面激活前后，登陆的政企客户是否发生改变
function onActive()
{
	if(!ifGetEc())
	{
		return false;
	}
	
	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
	var loginCustId = groupInfo.get("CUST_ID");
	
	if (!cachedCustId && loginCustId) {
		cachedCustId = loginCustId;
	} else if (cachedCustId && loginCustId != cachedCustId) {
		cachedCustId = loginCustId;
		$.enterpriseLogin.refreshActiveNav();
	}
}

function refreshForAutoData(data, partStr, type)
{
	
	if(type == "0")
	{
		refreshForAutoDataForList(data, partStr);
	}
	else//默认按直接按照key value处理
	{
		refreshForAutoDataForKey(data, partStr);
	}
}

function refreshForAutoDataForList(dataset, partStr)
{
	if(!(dataset instanceof  $.DatasetList))
	{
		return;
	}
	var parts= partStr.split(",");
	if(parts.length <= 0)
	{
		return;
	}
	
	for(var i = 0 ; i < dataset.length ; i ++)
	{
		var data = dataset.get(i);
		if(data instanceof  $.DataMap)
		{
			var attrValue = data.get("ATTR_VALUE");
			var attrCode = data.get("ATTR_CODE");
			var attrPre = data.get("ATTR_PRE");
			for(var i = 0 ; i < parts.length; i ++)
			{
				var part = parts[i];
				if(part != "")
				{
					var dataObj ;
					if(attrPre && attrPre != "" && attrPre != null)
					{
						dataObj= $("#"+part).find("[name='" +attrPre+"_"+attrCode+"']");
					}
					else
					{
						dataObj= $("#"+part).find("[name='"+attrCode+"']");
					}
					
					if(dataObj.length > 0)
					{
						dataObj.val(value);
					}
				}
			}
		}
	}
}

function refreshForAutoDataForKey(data, partStr)
{
	if(!(data instanceof  $.DataMap))
	{
		return;
	}
	var attrPre = data.get("PRE");
	var json = $.parseJSON(data.toString());
	$.each(json, function(name, value) {
		var parts= partStr.split(",");
		if(parts.length <= 0)
		{
			return false;
		}
		for(var i = 0 ; i < parts.length; i ++)
		{
			var part = parts[i];
			if(part != "")
			{
				var dataObj ;
				if(attrPre && attrPre != "" && attrPre != null)
				{
					dataObj= $("#"+part).find("[name='" +attrPre+"_"+name+"']");
				}
				else
				{
					dataObj = $("#"+part).find("[name='"+name+"']");
				}
				
				if(dataObj.length > 0)
				{
					dataObj.val(value);
				}
			}
		}
	});
}
