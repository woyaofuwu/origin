/* $Id */

//集团资料查询成功后调用的方法
function selectGroupAfterAction(data) {
   //填充集团客户显示信息
   insertGroupCustInfo(data);
   //重置右侧产品显示信息
   initElement();
   //加载集团产品树
   loadGroupProductTreeOrdered($('#CUST_ID').val(),$('#GRP_USER_ID').val());

}
//集团资料查询失败后调用的方法
function selectGroupErrorAfterAction() {
	//清空填充的集团客户信息内容
    clearGroupCustInfo();
    //重置右侧产品显示信息
    initElement();
    //清空集团产品树信息
    cleanGroupProductTree();

}

//SN集团资料查询失败后调用的方法
function selectGroupBySnErrorAfterAction() {
	//清空填充的集团客户信息内容
    clearGroupCustInfo();
    //重置右侧产品显示信息
    initElement();
    //清空集团产品树信息
    cleanGroupProductTree();

}

//SN集团资料查询成功后调用的方法
function selectGroupBySnAfterAction(data)
{	
	//重置右侧产品显示信息
    initElement(); 
    //清空集团产品树信息
    cleanGroupProductTree();
    
    var productExplainInfo =  data.get("PRODUCT_DESC_INFO");
    var userInfo =  data.get("GRP_USER_INFO");
    var groupInfo =  data.get("GRP_CUST_INFO");
    
    //生成三户数据
    if(userInfo != undefined && userInfo != null){
		insertGrpUserList(userInfo);
		insertSelGroupUserInfo(userInfo);
	}
	if(groupInfo != undefined && groupInfo != null){
		insertGroupCustInfo(groupInfo);
	}
	if(productExplainInfo != undefined && productExplainInfo != null){
		insertProductExplainInfo(productExplainInfo);
	}
	
    //初始树
	initTreeByProductInfo($('#GRP_PRODUCT_NAME').val(),$('#GRP_PRODUCT_ID').val());
	//显示右侧区域
	seeCompProductRefreshPart();
		
}


function validate() {
	var grpcustid = $("#CUST_ID").val();
	var custName=$("#CUST_NAME").val();
	var userEparchyCode =$('#GRP_USER_EPARCHYCODE').val();
	
	if (custName=="") {
	   alert('请先查询集团客户资料!');
	   return false;
	}
    var selectProduct=$("#GRP_PRODUCT_ID").val();
	if (selectProduct=='') {
	   alert('请先选择需要办理的集团产品！');
	   return false;
	}
	//REQ201806080001 关于更新跨省互联网专线业务支撑实施方案的通知--界面集成及计费规则调整  add by hzl
	if(selectProduct=='9912'){
		alert('跨省互联网专线产品业务请到两级界面去办理产!');
		return false;
	 }
 	var userId=$('#GRP_USER_ID').val();
	if (userId=='') {
	   alert('请选择需要办理业务的用户！');
	   return false;
	 }
	$.beginPageLoading('业务验证中....');
    var checkParam = '&CUST_ID='+grpcustid +'&USER_ID='+userId +'&PRODUCT_ID='+selectProduct  +'&IF_BOOKING='+$("#IF_BOOKING").val()+'&EPARCHY_CODE='+userEparchyCode ;
	pageFlowRuleCheck('com.asiainfo.veris.crm.order.web.frame.csview.group.rule.DestroyGroupUserRule','checkBaseInfoRule',checkParam);
	
	return false;
}

function clickcheck()
{
	    var effectNow=$("#effectNow");
	    if(effectNow.attr("checked") )
	    {
	    	alert("您预约了集团产品月底注销！请注意！");
	    	$("#IF_BOOKING").val('true');
	    }
	    else
	    {
	    	$("#IF_BOOKING").val('false');
	    }
}

function queryProduct(node)
{
	//通过号码查询的节点不需要再重新刷新用户信息
    var nodeid = node.id;
    if(nodeid=='USER_NODE_TREE'){
    	 return true;
    }
    initElement();
    $.beginPageLoading(); 
    var ifcheck =true;
    Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.group.changeuserelement.BaseInfoHttpHandler', 'queryProductInfo','&BUSI_TYPE='+$('#BUSI_TYPE').val()+'&PRODUCT_ID='+node.value+'&CUST_ID='+$('#CUST_ID').val(),
		function(data){
			if(data == null)
				return;
			
			var x_resultcode = data.get("x_resultcode","0");
			if(x_resultcode == '-1'){
				$.endPageLoading();
				showErrorInfo(error_code,error_info);
				return;
		    }
		    
		    var productDescInfo = data.get("PRODUCT_DESC_INFO");
			var grpUserList = data.get("GRP_USER_LIST");
			var seleUserInfo = data.get("SEL_USER_INFO");
			if(productDescInfo != undefined && productDescInfo != null){
					insertProductExplainInfo(productDescInfo);
			}
			if(grpUserList != undefined && grpUserList != null){
					insertGrpUserList(grpUserList);
			}
			if(seleUserInfo != undefined && seleUserInfo != null){
					insertSelGroupUserInfo(seleUserInfo);
			}
			
		    seeCompProductRefreshPart();
		    $.endPageLoading();
	        
		 },
		function(error_code,error_info,derror){
			ifcheck= false;
	        $.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
	
	
   return ifcheck;
}

function chooseUserProducts(obj){
	if(!chooseUserProductsBase(obj)) return ;
}

function initElement(){
	hiddenCompProductRefreshPart();
	cleanGrpUserList();	
}


//隐藏产品右侧区域
function hiddenCompProductRefreshPart(){
	var obj=$('#CompProductRefreshPart'); 
  	obj.css("display","none"); 
}

//显示产品右侧区域
function seeCompProductRefreshPart(){
	var comp=$('#CompProductRefreshPart');
	comp.css("display","");
}
