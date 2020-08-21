

//集团资料查询成功后调用的方法
function selectGroupAfterAction(data) {

   //填充集团客户显示信息
   insertGroupCustInfo(data);
   //重置产品显示信息
   initRightElement();
   //加载集团产品树
   loadGroupProductTreeOrdered($('#CUST_ID').val(),$('#GRP_USER_ID').val());

}
//集团资料查询失败后调用的方法
function selectGroupErrorAfterAction() {

	//清空填充的集团客户信息内容
    clearGroupCustInfo();
    //重置产品显示信息
    initRightElement();
    //清空集团产品树信息
    cleanGroupProductTree();

}

//通过集团号码查询集团资料成功后调用的方法
function selectGroupBySnAfterAction(data)
{	
	//重置右侧产品显示信息
    initRightElement(); 
    //清空集团产品树信息
    cleanGroupProductTree();
    //获取三户资料
    var productExplainInfo =  data.get("PRODUCT_DESC_INFO");
    var productCtrlInfo = data.get("PRODUCT_CTRL_INFO");
    var userInfo =  data.get("GRP_USER_INFO");
    var groupInfo =  data.get("GRP_CUST_INFO");
    //初始集团用户资料
    if(userInfo != undefined && userInfo != null){
		insertGrpUserList(userInfo);
		insertSelGroupUserInfo(userInfo);
	}
	//初始集团资料信息
	if(groupInfo != undefined && groupInfo != null){
		insertGroupCustInfo(groupInfo);
	}
	//初始产品描述信息
	if(productExplainInfo != undefined && productExplainInfo != null){
		insertProductExplainInfo(productExplainInfo);
		insertProductCtrlInfo(productCtrlInfo);
	}
	//初始树
	initTreeByProductInfo($('#GRP_PRODUCT_NAME').val(),$('#GRP_PRODUCT_ID').val());
	//初始付费信息
	renderPayPlanSel($('#GRP_PRODUCT_ID').val(),$('#GRP_USER_ID').val());
	//初始分散提示信息
	setDiverTipCtrMb();
	//显示右侧区域
	seeCompProductRefreshPart();
}

//SN集团资料查询失败后调用的方法
function selectGroupBySnErrorAfterAction() {
	//清空填充的集团客户信息内容
    clearGroupCustInfo();
    //重置右侧产品显示信息
    initRightElement();
    //清空集团产品树信息
    cleanGroupProductTree();
    //重置分散信息
    initDiverTip();
}

//成员号码资料查询成功后调用的方法
function selectMemberInfoAfterAction(data){
  
  if(data == null)
	return;
	
	//异地号码判断
  if(!afterCheckInfo(data)) 
  	return false;
  var memcustInfo = data.get("MEB_CUST_INFO");
  var memuserInfo = data.get("MEB_USER_INFO");
  var memuseracctdayInfo = data.get("MEB_ACCTDAY_INFO");
  
  insertMemberCustInfo(memcustInfo);
  insertMemberUserInfo(memuserInfo);
  insertUserAcctDayInfo(memuseracctdayInfo);
  setDiverTipCtrMb();

}
//成员资料查询失败后调用的方法
function selectMemberInfoAfterErrorAction() {

	clearMemberCustInfo();
	clearMemberUserInfo();
	clearUserAcctDayInfo();
	initDiverTip();
	cleanMebAuthState();
}

//USERCHECK查询失败后调用的方法
function userCheckErrAction(state,data) {
	selectMemberInfoAfterErrorAction();
	
	if(state=='USER_NOT'){
		if($('#cond_SERIAL_NUMBER').val().indexOf('0898')=='0' ){
			if(confirm("服务号码["+$('#cond_SERIAL_NUMBER').val()+"]为非移动号码，是否需要新增客户核心资料")){
				$("#GROUP_AUTH_FLAG").val("true");
				$("#MEB_SERIAL_NUMBER").val($('#cond_SERIAL_NUMBER').val());
				$("#MEB_EPARCHY_CODE").val('0898');
				$("#MEB_PRODUCT_ID").val('4444');
				$("#IF_ADD_MEB").val("true");
				return ;
			} 
		}
	}else if(state == 'USER_CUSTOM'){//网外号码
		$("#GROUP_AUTH_FLAG").val("true");
		getMemInfo();
		return;
	}else if(state == 'USER_KEY_NULL'){//用户表中没有用户密码
		return true;
	}
}

function validate() {
	var custid=$("#CUST_ID").val();	   
	if (custid=="") {
	   alert('尚未查询客户资料，请输入集团编码按回车查询!');
	   return false;
	}
	
	var meb_userid=$("#MEB_USER_ID").val(); 
	var addMeb = $("#IF_ADD_MEB").val();
	if (meb_userid == "" && addMeb != 'true') {
	   alert('请先输入成员服务号码查询资料后，再进行此操作！');
	   return false;
	}
	
	var productId=$("#GRP_PRODUCT_ID").val();
	if (productId=='') {
	   alert('请在集团产品树上选择需要办理的集团产品后，再进行此操作！');
	   return false;
	}
	
	var grpuserid=$("#GRP_USER_ID").val();
	if (grpuserid=='') {
	   alert('请选择需要办理业务的集团用户后，再进行此操作！');
	   return false;
	}
	
	if(!checkPayPlan()){
		return false;
	}
	
	//判断是否新增成员时选择了新增网外号码的三户资料
	if(!judgeAddMebUca()){
		return false;
	}
	
	var authTag = $("#GROUP_AUTH_FLAG").val();
	if(authTag!= 'true'){
		alert('号码未验证，请验证！');
		groupAuthStart();
		return false;
	}
	
	if(!judeAcctDays($('#USER_ACCTDAY_DISTRIBUTION').val(),$('#PRODUCT_NATURETAG').val(),$('#PRODUCT_IMMEDI_TAG').val(),$('#USER_ACCTDAY_FIRST_DAY_NEXTACCT').val(),$('#PAY_PLAN_SEL_PLAN_TYPE').val())){
		return false;
	}
	$.beginPageLoading('业务验证中....');
    var checkParam ='&CUST_ID='+custid+'&PRODUCT_ID='+productId+'&USER_ID='+grpuserid +'&USER_ID_B='+meb_userid+'&SERIAL_NUMBER='+$("#MEB_SERIAL_NUMBER").val()+'&BRAND_CODE_B='+$("#MEB_BRAND_CODE").val() +'&EPARCHY_CODE_B='+$("#MEB_EPARCHY_CODE").val() +'&IF_ADD_MEB='+$("#IF_ADD_MEB").val()+'&IF_BOOKING='+$("#IF_BOOKING").val()  ;
	pageFlowRuleCheck('com.asiainfo.veris.crm.order.web.frame.csview.group.rule.CreateGroupMemberRule','checkBaseInfoRule',checkParam);
	return false;   
}


function queryProduct(node)
{
	//通过号码查询的节点不需要再重新刷新用户信息
    var nodeid = node.id;
    if(nodeid=='USER_NODE_TREE'){
    	 return true;
    }
    initRightElement();
    $.beginPageLoading(); 
    var ifcheck =true;
    Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupmember.BaseInfoHttpHandler', 'queryProductInfo','&PRODUCT_ID='+node.value+'&CUST_ID='+$('#CUST_ID').val(),
	function(data){
		if(data == null)
			return;
		
		var x_resultcode = data.get("x_resultcode","0");
		if(x_resultcode == '-1'){
			$.endPageLoading();
			showErrorInfo(error_code,error_info);
			return;
	    }
	    $('#GRP_PRODUCT_ID').val(node.value);
	    
	    var productDescInfo = data.get("PRODUCT_DESC_INFO");
	    var productCtrlInfo = data.get("PRODUCT_CTRL_INFO")
		var grpUserList = data.get("GRP_USER_LIST");
		var seleUserInfo = data.get("SEL_USER_INFO");
		if(productDescInfo != undefined && productDescInfo != null){
				insertProductCtrlInfo(productCtrlInfo);
				if(!judgeAddMebUca()) {
					$.endPageLoading();
					return ;
				}
				insertProductExplainInfo(productDescInfo);
		}
		if(grpUserList != undefined && grpUserList != null){
				insertGrpUserList(grpUserList);
		}
		if(seleUserInfo != undefined && seleUserInfo != null){
				insertSelGroupUserInfo(seleUserInfo);
		}
		
		if($('#GRP_USER_ID').val() != null && $('#GRP_USER_ID').val() != ''){
	    	renderPayPlanSel($('#GRP_PRODUCT_ID').val(),$('#GRP_USER_ID').val());
	    }
	    setDiverTipCtrMb();
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

//判断 异地号码的后续处理
function afterCheckInfo(data){
  var result = data.get("OUT_PHONE","false");;
  if(result == "true"){
  	if(!(confirm("请注意：该号码是异地号码，是否继续成员新增?"))){
  	    //选择取消则退出办理
	    $('#cond_SERIAL_NUMBER').val();
	    selectMemberInfoAfterErrorAction();
	    return false;
	}
  }
  return true;
}

function chooseUserProducts(obj){
	if(!chooseUserProductsBase(obj)) return ;
	renderPayPlanSel($('#GRP_PRODUCT_ID').val(),$('#GRP_USER_ID').val());
	setDiverTipCtrMb();
}

//重置下右侧的产品控制区域（集团用户列表+付费信息+分散信息）
function initRightElement(){
	hiddenCompProductRefreshPart();
	cleanGrpUserList();
	cleanPayPlanSelPart();
	initDiverTip();
	cleanProductExplainInfo();
	cleanProductCtrlInfo(); 	
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

//成员新增时使用的分散提示信息
function setDiverTipCtrMb(){
	setDiverTip($('#USER_ACCTDAY_DISTRIBUTION').val(),$('#PRODUCT_NATURETAG').val(),$('#PRODUCT_IMMEDI_TAG').val(),$('#USER_ACCTDAY_FIRST_DAY_NEXTACCT').val(),$('#PAY_PLAN_SEL_PLAN_TYPE').val(),$('#PRODUCTCTRL_BOOKING_FLAG').val());
}

function selectPayPlanSelAfterAction(){
	setDiverTipCtrMb();
}

//判断产品是否支持成员新增时同时新增三户资料
function judgeAddMebUca(){
	var ifAddMeb =ifAddMebUca();//新增三户资料
	var AddMebUcaFlag  =getAddMebUcaFlag();//是否支持新增三户资料
	if(ifAddMeb=='true' && AddMebUcaFlag != 'true'){
		alert('产品不支持成员订购时新增客户核心资料！请先在集团成员用户开户界面补录号码的三户资料');
		return false;
	}
	return true;
}
