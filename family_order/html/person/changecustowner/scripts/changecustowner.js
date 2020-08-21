$(function(){
	$.acctInfo.pullWidget(3);//隐藏第三列 用户账期
	//$.acctInfo.pushWidget(-1,"ACCT_CONTRACT_NO_BODY");
});

/**
 * 认证之后刷新业务区域
 * @param data
 * @return
 */
function refreshPartAtferAuth(data)
{
	//针对单方过户当中的要求
	var checkMode=data.get("CHECK_MODE");
	if(checkMode=="2"||checkMode=="6"){
		$("#SPECIAL_CHANGE_CUST").attr("checked",true);
		$("#SPECIAL_CHANGE_CUST").attr("disabled",true);
	}else{
		$("#SPECIAL_CHANGE_CUST").attr("checked",false);
		$("#SPECIAL_CHANGE_CUST").attr("disabled",false);
	}
	
	$.acctInfo.cacheWidget={};
	var userInfo =getDataByKeys(data.get("USER_INFO"),"USER_TYPE_CODE,IN_DATE,BRAND_CODE,BRAND_NAME,PRODUCT_ID,PRODUCT_NAME,USER_ID,EPARCHY_CODE,SERIAL_NUMBER");
	var custInfo =getDataByKeys(data.get("CUST_INFO"),"PSPT_TYPE_CODE,PSPT_ID,CUST_NAME");
	var param="&USER_INFO="+userInfo.toString()+"&CUST_INFO="+custInfo.toString()+"&ACCT_INFO="+data.get("ACCT_INFO").toString();
	$.ajax.submit('', 'queryUserBuisInfo', param, 'OldInfoPart,UserDiscntInfoPart,UserSvcInfoPart,HiddenPart,newCustPart,newAcctPart,HiddenPart', function(callBackData){
		$.acctInfo.delWidget(3);////隐藏第三列 用户账期
		$.custInfo.setRelatePayName(false);//解除填写客户名字时自动 填充账户名称
		alert("办理过户业务后，原户主将无法打印月结发票，请告知客户并明确原户主是否需要打印月结发票！");
		$("#NEW_PASSWD").val('');//清空新密码
		$("#REMARK").val('');
		$.custInfo.setSerialNumber(data.get("USER_INFO").get('SERIAL_NUMBER'));
		$("#PAY_MODE_CODE").unbind("change");//解除组件中绑定的事件
		$("#PAY_MODE_CODE").bind("change",onChangePayMode);
		$.acctInfo.addWidget(-1,"ACCT_CONTRACT_NO_BODY");
		
		//如果用户是宽带用户，需要提示进行处理
		var isWidnetUser=callBackData.get('IS_WIDENET_USER');
		if(isWidnetUser=="1"){
			var widenetDeal=null;
			var widenetDeal=confirm("用户存在宽带业务，【OK】是办理宽带过户，【Cancel】或者\"关闭提示框\"是办理宽带取消！");
			
			var IMSTag = callBackData.get('IMS_TAG');
			
			if(widenetDeal){
				if(IMSTag == '1')
				{
					var IMSDeal=confirm("用户家庭IMS固话可随手机号码过户，是否对家庭IMS固话过户，【OK】是，【Cancel】或者\"关闭提示框\"否！");
					if(IMSDeal)
					{
						$("#DESTORY_IMS_TAG").val("2");	//IMS过户标识
					}else
					{
						$("#DESTORY_IMS_TAG").val("1");	//IMS拆机标识
					} 
				}
				$("#WIDENET_DEAL").val("1");	//过户宽带
				widenetDeal="1";
			}else{
				if(IMSTag == '1')
				{
					alert("用户存在IMS固话，家庭IMS固话不具备过户条件，需拆机！");
				}
				$("#WIDENET_DEAL").val("0");	//取消宽带
				widenetDeal="0";
			}
			
			$.beginPageLoading("核对宽带办理规则...");
			var checkParam="&WIDENET_DEAL="+widenetDeal+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
			$.ajax.submit('', 'checkChangeWidenetUser', checkParam, null, function(){
				$.endPageLoading();
				$.cssubmit.disabledButton(false,$("#CSSUBMIT_BUTTON"));
			},function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
				$.cssubmit.disabledButton(true,$("#CSSUBMIT_BUTTON"));
		    });
		}
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


function getDataByKeys(orgData,keys)
{
	var retData=new Wade.DataMap();
	var keyArray = keys.split(",");
	for(i=0;i<keyArray.length;i++)
	{
		retData.put(keyArray[i],orgData.get(keyArray[i]));
	}
	return retData;
}

function onChangePayMode(){
	
	var iUserPrepayTag = $.auth.getAuthData().get('USER_INFO').get('PREPAY_TAG');
	var payModeCode = $("#PAY_MODE_CODE").val();
	 if("1" == iUserPrepayTag && "0"!= payModeCode){
		 alert("预付费用户不能过到非现金帐户！");
		 $("#PAY_MODE_CODE").val('0');
		 return false;
	  }
	 
	
	if(payModeCode!='' && payModeCode!='0' && payModeCode!='5'){
		//分散帐期添加校验，如果帐期不为自然月，提示信息
		var acctDay = $.auth.getAuthData().get('ACCTDAY_INFO').get('ACCT_DAY');
		if(acctDay != 1){
			var flag = window.confirm('该用户的帐期日为'+acctDay+'号；托收后将变更为自然月[1号]，是否继续？');
			if(!flag){
				$('#PAY_MODE_CODE').val("0");
				return false;
			}
		}
	}
	//继续执行组件中的绑定事件
	$.acctInfo.events.onChangePayModeCode();
}


//点击“密码设置”按钮组件时执行
function beforeEvent(){
   $("#NEW_PASSWD").val('');//清空新密码
   var t_psptId = $('#PSPT_ID').val();//取页面中的新证件号
   if(t_psptId==""){
	   alert("请先设置证件号码！");
	   $('#PSPT_ID').focus();
	   return false;
   }
   var t_serialNumber =  $.auth.getAuthData().get('USER_INFO').get('SERIAL_NUMBER');
   var t_userId =  $.auth.getAuthData().get('USER_INFO').get('USER_ID');
   
   $.password.setPasswordAttr({
      psptId:t_psptId, 
      serialNumber:t_serialNumber,
      userId:t_userId     
   });
   return true;
}
//密码设置完成后的回调方法
function afterEvent(data){
	$("#NEW_PASSWD").val(data.get("NEW_PASSWORD"));
}

/**
 * 提交时 检查
 * @return
 */
function submitBeforeCheck()
{
	//人像比对
	var AGENT_PIC_ID=$("#AGENT_PIC_ID").val();
	if(AGENT_PIC_ID ==''){
		$("#AGENT_PIC_ID").val("AGENT_PIC_ID_value");
	}
	
	if(!$.validate.verifyAll()) return false;
	//AGENT_PIC_ID_value这是界面默认的值,
	//当没有对经办人人像摄像是，就把AGENT_PIC_ID指控
	var AGENT_PIC_ID_111=$("#AGENT_PIC_ID").val();
	if(AGENT_PIC_ID_111 =='AGENT_PIC_ID_value'){
		$("#AGENT_PIC_ID").val("");
	}
	
	var cmpTag = "1";
	$.ajax.submit(null,'isCmpPic','','',
			function(data){ 
				var flag=data.get("CMPTAG");
				if(flag=="0"){ 
					cmpTag = "0";
					//alert("提交到后台：cmpTag:"+cmpTag);
				}
			  	$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
	},{
		"async" : false
	});
	//alert("后台执行完：cmpTag:"+cmpTag);
	//alert("获取界面的值AGENT_PIC_ID:"+$("#AGENT_PIC_ID").val());
	
	if(cmpTag == "0"){		
		var picid = $("#PIC_ID").val();
		if(null != picid && picid == "ERROR"){
			MessageBox.error("告警提示","客户"+$("#PIC_STREAM").val(),null, null, null, null);
			return false;
		}
		var formpicid = $("#FORM_PIC_ID").val();
		if(null != formpicid && formpicid == "ERROR"){
			MessageBox.error("告警提示","原客户"+$("#FORM_PIC_STREAM").val(),null, null, null, null);
			return false;
		}
		var agentpicid = $("#AGENT_PIC_ID").val();
		if(null != agentpicid && agentpicid == "ERROR"){
			MessageBox.error("告警提示","经办人"+$("#AGENT_PIC_STREAM").val(),null, null, null, null);
			return false;
		}
		var oldTypeCode = $("#OLD_PSPT_TYPE_CODE_NUM").val();
		//var oldTypeCode =$('#OLD_PSPT_TYPE_CODE').attr('value');
		//alert("oldTypeCode:"+oldTypeCode);
		var oldtag =(oldTypeCode=="D" || oldTypeCode=="E" || oldTypeCode=="G" || oldTypeCode=="L" || oldTypeCode=="M")?true:false;
		var spectag = $("#SPECIAL_CHANGE_CUST").attr("checked");
		var pspTypecode = $("#PSPT_TYPE_CODE").val();
		//新户主人像比对标志
		var pspshot = ((pspTypecode == "0" ||pspTypecode == "1"|| pspTypecode == "2") && picid == "")?true:false;
		//经办人证件类型
		var angeTypecode = $("#AGENT_PSPT_TYPE_CODE").val();
		var angentshot = ((angeTypecode =="0"|| angeTypecode =="1") && agentpicid == "")?true:false;		
		//alert("agentpicid:"+agentpicid+"oldTypeCode:"+oldTypeCode+",oldtag:"+oldtag+",spectag:"+spectag+",pspTypecode:"+pspTypecode+",pspTag:"+pspTag+",pspshot:"+pspshot+",angentshot:"+angentshot+",angeTypecode:"+angeTypecode);
		//标识经办人与原客户是否一致
		var isAgentInfoAndpsptInfoSame=false;
		if(spectag){//单方过户
			
			/**
			 * REQ201705270006_关于人像比对业务优化需求
			 * <br/>
			 * 个人开户：用户个人身份证证件开户，判断户主或者经办人人像比对通过即可。 
			 * @author zhuoyingzhi
			 * @date 20170621
			 * 只要是 单方过户，新户主必须进行人像比对通过后才能办理业务。
			 */
/*			if(oldTypeCode == "0" ||oldTypeCode == "1"){//原户主是个人证件
				if(formpicid == "" && pspshot){
					MessageBox.error("告警提示","请进行原客户或客户摄像",null, null, null, null);
					return false;
				}
			}else if(pspshot){
				MessageBox.error("告警提示","请进行客户摄像",null, null, null, null);
				return false;
			}*/
			if(pspshot){
				//客户未摄像
				if(pspTypecode == "2"){
					//户口本
					//客户证件号码
					var custPsptId = $("#PSPT_ID").val();	
					
					if(checkCustAge(custPsptId,pspTypecode) ){
						//11岁（含）至120岁（不含）之间的用户必须通过验证才可以办理（同身份证一致）;
						MessageBox.error("告警提示","请进行客户摄像!",null, null, null, null);
						return false;
					}
				}else{
					MessageBox.error("告警提示","请进行客户摄像",null, null, null, null);
					return false;
				}
				/**************************************/

			}
			
		}else{//非单方过户
			if((oldTypeCode == "0" || oldTypeCode == "1") && formpicid == ""){
				MessageBox.error("告警提示","请进行原客户摄像",null, null, null, null);
				return false;
			}
			if(pspshot){
				/**
				 * REQ201705270006_关于人像比对业务优化需求
				 * <br/>
				 * 非单方过户新增规则，若录入经办人与原户主一致（证件号和姓名一致），原户主通过人像比对即可办理。 
				 * @author zhuoyingzhi
				 * @date 20170623
				 */
				//经办人名称
				var agentCustName=$("#AGENT_CUST_NAME").val();
				//经办人证件号码
				var agentPsptId=$("#AGENT_PSPT_ID").val();
				
				//原客户名称
				var oldCustNameValue=$("#OLD_CUST_NAME_VALUE").val();
				//原客户证件号码
				var oldPsptIdNum=$("#OLD_PSPT_ID_NUM").val();
				
				if(agentCustName == oldCustNameValue && agentPsptId == oldPsptIdNum){
					//经办人与原户主一致（证件号和姓名一致）
					//原户主人像比对已经在前面判断
					isAgentInfoAndpsptInfoSame=true;
				}else{
					/**
					 * REQ201707060020_关于年龄外经办人限制变更的优化
					 * @author  zhuoyingzhi
					 * @date 20170805
					 */
					if(pspTypecode == "2"){
						//户口本
						//客户证件号码
						var custPsptId = $("#PSPT_ID").val();	
						
						if(checkCustAge(custPsptId,pspTypecode) ){
							//11岁（含）至120岁（不含）之间的用户必须通过验证才可以办理（同身份证一致）;
							MessageBox.error("告警提示","请进行客户摄像!",null, null, null, null);
							return false;
						}
					}else {
						MessageBox.error("告警提示","请进行客户摄像",null, null, null, null);
						return false;
					}
				}
			}
		}
		if(angentshot&&!isAgentInfoAndpsptInfoSame){//客户是单位证件				
			MessageBox.error("告警提示","请进行经办人摄像",null, null, null, null);
			return false;
		}
	}	
	/**
	 * 验证 使用人数量限制
	 */
    //证件类型选择集团证件（单位证件、营业执照、事业单位法人证书、社会团体法人登记证书、组织机构代码证）的，	 	 
    var objValue = $("#PSPT_TYPE_CODE").val();	 	 
    if(objValue=="D" || objValue=="E" || objValue=="G" || objValue=="L" || objValue=="M") 	 
    {	 	
	    	//使用人姓名
	        var custName = $("#USE").val();
	        //使用人证件号
	        var psptId = $("#USE_PSPT_ID").val();
	        if(custName == "" || psptId == ""){
	            return false;
	        }
	        var param = "&USE="+custName+"&USE_PSPT_ID="+psptId;
	        $.beginPageLoading("使用人证件信息数量校验......");
	    	$.ajax.submit('', 'checkRealNameLimitByUsePspt', param, '',submitBeforeCheckRealNameLimitByPspt,
	    	function(error_code,error_info,derror){
	    		alert("error_info:"+error_info);
	    		$.endPageLoading();
	    		return false;
	        });	 	 
    }else{
    	return submitBeforeCheckInfo();
    }
	
}
/**
 * 
 * 提交时验证  使用人证件号码数限制校验
 * @param data
 * @returns {Boolean}
 */
function  submitBeforeCheckRealNameLimitByPspt(data){
    $.endPageLoading();
	if(data.get("CODE") == "1"){
		//验证未通过
		$("#USE_PSPT_ID").attr("value", "");
		alert(data.get("MSG"));
		return false;
   }else{
	   return submitBeforeCheckInfo();
   }
	
}

/**
 * 提交时验证  正常的规则
 * @returns {Boolean}
 */
function submitBeforeCheckInfo(){
	var newPwd = $("#NEW_PASSWD").val();
	if(newPwd==""){
		if(confirm("是否修改密码？")){
			alert('请点击【密码设置】按钮,修改用户密码!');
			return false;
		}
	}
	
	var oldIsRealName = $.auth.getAuthData().get('CUST_INFO').get('IS_REAL_NAME');
	if(oldIsRealName=="1" || oldIsRealName=="2")
	{
		var reg =/(^[0]{1,5}$|^[1]{1,5}$)/;
		if($("#PSPT_TYPE_CODE").val() == "Z"
				|| $("#CUST_NAME").val()=="海南通"
				|| reg.test($("#PSPT_ID").val()))
		{
    		if(confirm("如果选择其它证件类型，系统将把你置为非实名制客户。是否继续?"))
    		{
    			$("#IS_REAL_NAME").val("0");
    			return true;
    		}else
    		{
    			$("#IS_REAL_NAME").val("");
    			return false;
    		}
		}else
		{
			var param = "&CUST_NAME="+$("#CUST_NAME").val()+"&PSPT_ID="+$("#PSPT_ID").val()+"&USER_EPARCHY_CODE="+$.auth.getAuthData().get('USER_INFO').get('EPARCHY_CODE');
			$.ajax.submit('', 'checkPsptRealNameLimit', param, '', function(data){
				var limitCnt=parseInt(data.get(0).get("LIMIT_COUNT"),10);
				var result=parseInt(data.get(0).get("RESULT"),10);
				/**
				 * REQ201611180016 关于特殊调整我公司营业执照开户使用人不限制5户的需求 chenxy3 2016-12-14
				 * 增加测试用户--使用人信息
				 * */
				if($("#PSPT_TYPE_CODE").val()=="E" && $("#PSPT_ID").val()=="91460000710920952X" && $("#CUST_NAME").val()=="中国移动通信集团海南有限公司"){
					result="1";
				}
				if(result<=0)
				{
					alert("证件号码【"+$("#PSPT_ID").val()+"】实名制开户的数量已达到最大值【"+limitCnt+"】个，请更换其它证件！");
				}else
				{
					$("#IS_REAL_NAME").val(oldIsRealName);
					$.cssubmit.submitTrade();//主动提交
				}
			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
		    });
			return false;//返回false是不需要cssubmit组件再提交了。
		}
	}else
	{
		$("#IS_REAL_NAME").val(oldIsRealName);
		$.cssubmit.submitTrade();//主动提交
	}
	return true;
}
function verifyenterprise(){
	$.custInfo.verifyEnterpriseCard(); 
} 
function verifyorg(){
	$.custInfo.verifyOrgCard(); 
}
/**
 * REQ201707060020_关于年龄外经办人限制变更的优化
 * <br/>
 * 判断是否在
 * 11岁（含）至120岁（不含）之间的
 * @param idCard
 * @param psptTypeCode
 * @date 20170804
 */
function checkCustAge(idCard,psptTypeCode){

	//根据身份证  获取周岁(已有的方法)
	var cust_age = this.jsGetAge(idCard);
	
	if(11 <=cust_age && cust_age < 120 ){
		//11岁（含）至120岁（不含）
		return true;
	}else{
		return false;
	}
}
/**
 * REQ201707060020_关于年龄外经办人限制变更的优化
 * <br/>
 * @param idCard
 * @returns
 * @author zhuoyingzhi
 * @date 20170804
 */
function jsGetAge(idCard){				 
    var returnAge;
	var bstr = idCard.substring(6,14);		 
    var birthYear = bstr.substring(0,4);
    var birthMonth = bstr.substring(4,6);
    var birthDay = bstr.substring(6,8);
    
    var d = new Date();
    var nowYear = d.getFullYear();
    var nowMonth = d.getMonth() + 1;
    var nowDay = d.getDate();
    
    if(nowYear == birthYear)
    {
        returnAge = 0;//同年 则为0岁
    }
    else
    {
        var ageDiff = nowYear - birthYear ; //年之差
        if(ageDiff > 0)
        {
            var monthDiff = nowMonth - birthMonth;//月之差
            if(monthDiff <= 0)
            {
                returnAge = ageDiff - 1;
            }
            else
            {
                returnAge = ageDiff ;
            }
        }
        else
        {
            returnAge = -1;//返回-1 表示出生日期输入错误 晚于今天
        }
    }
    return returnAge;//返回周岁年龄		    
}