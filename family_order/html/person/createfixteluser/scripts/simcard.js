/* $Id: simcard.js,v 1.8 2013/04/09 12:09:31 zhangwm Exp $ */
var test='no';
$(document).ready(function(data){

});
function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'userInfoPart', function(data){
		//新SIM卡的输入框可编辑
		if('changecard' == document.getElementById('pagecaption').value)
		{
			$("#NEW_SIM_CARD_NO").attr("disabled",null);
		} 
		else if('remotecard' == document.getElementById('pagecaption').value)
		{
			$("#reuseButton").attr("disabled",null);
			$("#chkRead").attr("disabled",null);
			$("#readAndWrite").attr("disabled",null);
		} 
		else if('remotecard2' == document.getElementById('pagecaption').value)
		{
			$("#reuseButton").attr("disabled",null);
			$("#chkRead").attr("disabled",null);
			$("#readAndWrite").attr("disabled",null);
		} 
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}
//校验新SIM卡（换卡）
function checkSIM(newSIM,getMode) 
{ 
	//校验sim卡号
	var expressionSIM = /^89860[A-Za-z0-9]{15}$/;
	if(('remotecard' == document.getElementById('pagecaption').value) || ('remotecard2' == document.getElementById('pagecaption').value) )
	{
	}
	else
		if(!expressionSIM.test(newSIM.val())) 
		{ 
			alert('SIM格式错误！'); 
			document.getElementById('NEW_IMSI').value = '';
			newSIM.focus();
			return false;
		}  
	$.beginPageLoading();
	setTimeout(function() { 
		var pp = '&NEW_SIM_TYPE_CODE='+document.getElementById('NEW_SIM_TYPE_CODE').value
		//+'&NEW_CAPACITY_TYPE='+document.getElementById('NEW_CAPACITY_TYPE').value
		+'&GET_MODE='+getMode+'&USER_ID=' +document.getElementById('USERID').value+'&NEW_SIM_CARD_NO=' + newSIM.val()
		+'&BRAND_CODE=' + document.getElementById('BRAND').value
		+'&SERIAL_NUMBER=' + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER")
		+'&PRODUCT_ID=' + document.getElementById('PRODUCTID').value; 
		$.ajax.submit('AuthPart,userInfoPart', 'verifySimCard', pp ,'', function(data)
				{ 
			var obj = data.get(0); 
			if ('GETONLY' != getMode ) {  
				if(obj.get('DOUBLE_TAG') && obj.get('DOUBLE_TAG') != '') { 
					$.ajax.submit('AuthPart,userInfoPart', 'releaseResource', '&SIM_CARD_NO=' + obj.get('SIM_CARD_NO')+'&SERIAL_NUMBER=' + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER"), null, null, null);				
					$.endPageLoading();
					alert(obj.get('SIM_CARD_NO') + '是一卡双号卡，请使用单号卡进行操作！');
					document.getElementById('NEW_IMSI').value = '';
					//document.getElementById('NEW_CAPACITY_TYPE').value = '';
					document.getElementById('NEW_SIM_TYPE_CODE').value = '';
					document.getElementById('NEW_RES_KIND_NAME').value = '';
					document.getElementById('NEW_SIM_CARD_NO').select();
					obj = null;
					return false;
				}

				document.getElementById('NEW_SIM_CARD_NO').value = obj.get('_NEW_SIM_CARD_NO'); 
				document.getElementById('NEW_IMSI').value = obj.get('_NEW_IMSI');              
				//document.getElementById('NEW_CAPACITY_TYPE').value = obj.get('_NEW_CAPACITY_TYPE'); 
				document.getElementById('NEW_SIM_TYPE_CODE').value = obj.get('_NEW_SIM_TYPE_CODE'); 
				document.getElementById('NEW_RES_KIND_NAME').value = obj.get('RES_KIND_NAME'); 
			} 
			var scoreForCardDisplay = obj.get('SCORE_CARDFEE');
			if(scoreForCardDisplay =="1")
			{
				document.getElementById('scoreForCard').style.display=""; 
				$("#scoreForCard_content").attr("checked",false);  
				document.getElementById('scoreCheckSpan').style.display="";
			} 
			else
			{
				document.getElementById('scoreForCard').style.display="none"; 
				$("#scoreForCard_content").attr("checked",false);  
				document.getElementById('scoreCheckSpan').style.display="none";
			}
			var vipCheckDisplay = obj.get('VIP_DISPLAY');
			if(vipCheckDisplay=="1")
			{
				document.getElementById('vipFreeCheck').style.display=""; 
				$("#vipFreeCheck_content").attr("checked",false); 
				$("#vipCheckSpan").text(obj.get('VIP_DISPLAY_TEXT'));                 
			}
			else
			{
				document.getElementById('vipFreeCheck').style.display="none"; 
				$("#vipFreeCheck_content").attr("checked",false); //2013-7-8 today add
			}	
			document.getElementById('initExScoreText').value=obj.get('INIT_EXSCORE');
			document.getElementById('initExCardFeeText').value=obj.get('INIT_EXCARDFEE');
			document.getElementById('USER_SCORE').value=obj.get('USER_SCORE');
			document.getElementById('cardFeeText').value = obj.get('FEE');
			document.getElementById('cardFeeTypeCode').value = obj.get('FEEITEM_CODE');

			document.getElementById('VIP_USE_NUM').value=obj.get('VIP_USE_NUM');
			document.getElementById('VIP_LEFT_NUM').value=obj.get('VIP_LEFT_NUM');
			document.getElementById('VIP_TYPE_CODE').value=obj.get('VIP_TYPE_CODE');
			document.getElementById('VIP_CLASS_ID').value=obj.get('VIP_CLASS_ID');

			if ( 'GETONLY' != getMode )                    
				document.getElementById('newCardInfo').value = obj.toString(); 
			dealFee(obj.get('FEEITEM_CODE'), obj.get('FEE')); 
			obj = null;	//清理内存 
			$.endPageLoading();
				},function(error_code,error_info){
					$.endPageLoading();
					alert(error_info); 
				});
	}, 100);
}

//费用处理（换卡、远程写卡）
function dealFee(feeitem, cardFee, isReuse) {
	if(isReuse == null || isReuse == false) {
		var obj = new Wade.DataMap();
		obj.put("TRADE_TYPE_CODE", document.getElementById("tradeTypeCode").value);
		obj.put("MODE", "0"); 
		obj.put("CODE", feeitem); 
		obj.put("FEE", cardFee);  
		//判断是否有费用项
		var feeList = $.feeMgr.getFeeList();

		if(feeList.length == 0) {	//费用列表为空
			$.feeMgr.insertFee(obj);
		} else 
		{
			$.feeMgr.clearFeeList(); 
			$.feeMgr.insertFee(obj);
		}
		//alert('费用处理后，费用数据：'+ $.feeMgr.getFeeList());
		obj = null;		//清空内存
		feeList = null;	//清空内存
	}
}

function checkBefore() 
{ 
	$.beginPageLoading(); 
	if(!$.validate.verifyAll('QueryCondPart'))
	{
		$.endPageLoading();
		return false;
	} ; 
	ajaxSubmit('QueryCondPart,AuthPart,userInfoPart', 'ckbefore', '&SERIAL_NUMBER=' + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER"), 'QueryListPart', function(data)
			{ 
		if ( ''==data.get(0,'X_RESULTINFO'))
		{
			$.endPageLoading();  
			submit() ;
			//$.cssubmit.submitTrade();
			return true ;
		}
		if (!window.confirm(data.get(0,'X_RESULTINFO'))) 
		{
			$.endPageLoading();    
			return false;
		}
		else
		{
			$.endPageLoading();  
			submit( '&M_PAY=1' );
			return true ;
		} 
			},
			function(e,i)
			{
				$.endPageLoading();    
				alert(e+":"+i); 
			});
}

//提交按钮校验（换卡）
function specCheck() {
	var newImsi = document.getElementById('NEW_IMSI').value;
	if('' == newImsi){
		alert('请输入SIM卡，敲回车校验后，再进行提交！');
		document.getElementById('NEW_SIM_CARD_NO').focus();
		return false;
	}

	//如果查询到的用户主服务状态为人工停机，则弹出“是否同时办理开机业务”对话框
	if('true' == document.getElementById('UserSvcStateTag').value){
		if(confirm('是否同时办理开机业务？')) {
			document.getElementById('OpenMobileTag').value = 'true';
		} else {
			document.getElementById('OpenMobileTag').value = 'false';
		}
	}
	//是否VIP免费换卡 
	if(document.getElementById('vipFreeCheck_content').checked ==true) //vipFreeCheck
	{
		document.getElementById('vipFreeTag').value = 'true';
	}
	else
	{
		document.getElementById('vipFreeTag').value = 'false';
	}	
	//是否积分兑换卡费
	if(document.getElementById('scoreForCard_content').checked ==true)  //scoreForCard
	{
		document.getElementById('scoreExCardFeeTag').value = 'true';
	}
	else
	{
		document.getElementById('scoreExCardFeeTag').value = 'false';
	}	

	checkBefore() ;
}

//提交按钮校验（远程写卡）
function checkRemote() {
	var simCardNo = document.getElementById('NEW_SIM_CARD_NO').value;
	var imsi = document.getElementById('NEW_IMSI').value;
	if(simCardNo == '' || imsi == '') {
		alert('请先写卡或读卡！');
		return false;
	}

	if(document.getElementById('vipFreeCheck_content').checked ==true)  
	{
		document.getElementById('vipFreeTag').value = 'true';
	}
	else
	{
		document.getElementById('vipFreeTag').value = 'false';
	}	
	//是否积分兑换卡费
	if(document.getElementById('scoreForCard_content').checked ==true)  
	{
		document.getElementById('scoreExCardFeeTag').value = 'true';
	}
	else
	{
		document.getElementById('scoreExCardFeeTag').value = 'false';
	}		

	if(!confirm('确认提交吗？')) {
		return false;
	}
	checkBefore() ;  
}

//切换读写卡（远程写卡）
function changeButton() {
	var chkRead = document.getElementById('chkRead').checked;
	if(chkRead) {
		$("#readAndWrite span:first").text("读卡");
	} else {
		$("#readAndWrite span:first").text("写卡");
	}
}

function readOrWrite() { 
	var is3GUser = '0';	//是否3G用户：0-否 1-是
	if(document.getElementById('IS_TD_USER').value == '1') {
		is3GUser = '1';
	} 
	var chkRead = document.getElementById('chkRead').checked;
	var sn = document.getElementById("AUTH_SERIAL_NUMBER").value; //today
	if(chkRead) {
		invokeCard(1, 'thenDoWhat', is3GUser, 142);
	} else {
		invokeCard(0, 'thenDoWhat', is3GUser, 142, sn ); 
	} 
}


function thenDoWhat(obj) 
{
	document.getElementById('NEW_SIM_CARD_NO').value = obj.get('SIM_CARD_NO');
	document.getElementById('NEW_IMSI').value = obj.get('IMSI');
	//document.getElementById('NEW_CAPACITY_TYPE').value = obj.get('CAPACITY_TYPE_CODE');
	document.getElementById('NEW_SIM_TYPE_CODE').value = obj.get('SIM_TYPE_CODE'); //todaydtoday从这里开始，这个值已经有了，前面传一下
	document.getElementById('NEW_RES_KIND_NAME').value = obj.get('RES_KIND_NAME'); //today 白卡写卡回写接口还要返回这个值 2013-7-5

	var emptyCardId = '';
	if ( test == 'yes' )
		emptyCardId = "123456789";
	else
		emptyCardId = getEmptyCardId(); 
	if(!emptyCardId) return false;
	document.getElementById('EMPTY_CARD_ID').value = emptyCardId;	 
	this.checkSIM($('#NEW_SIM_CARD_NO'),'GETONLY');
	if(obj.get('FEE') != '0') {
		//dealFee(obj.get('FEEITEM_CODE'), obj.get('FEE'), obj.get('isReuse')); today need open
	}	
	obj.put('_NEW_SIM_CARD_NO' ,obj.get('SIM_CARD_NO' ));
	obj.put('_NEW_IMSI',obj.get('IMSI'));
	obj.put('_NEW_SIM_TYPE_CODE',obj.get('SIM_TYPE_CODE'));
	document.getElementById('newCardInfo').value = obj.toString();   
	$("#readAndWrite").attr("disabled",true);
	$("#reuseButton").attr("disabled",true);
	obj = null;	//清理内存
}

/** 读写卡接口
 * tag: 0-写卡 1-读卡
 * fct：回调函数名，读写卡完后调用
 * is3GUser：是否3G用户：0-否 1-是
 * tradeTypeCode：暂时没什么用，留作以后扩展用
 */
function invokeCard(tag, fct, is3GUser, tradeTypeCode, serialNumber, isReuse) { 
	//USIM卡补换流程新增卡类型判断
	var cardSN = getCardSN4USIM();
	if(cardSN==false || cardSN==""){
		alert("获取空卡序列号错误！");
		return false;
	}
//	var cardSN = "1234567890123456";
	if(cardSN.length == 20){//新卡
		invokeCard4USIM(tag, fct, is3GUser, tradeTypeCode, serialNumber, isReuse,cardSN);
	}else if(cardSN.length == 16){//老卡

		$.beginPageLoading(); //2013-7-29
		if(isReuse != true) isReuse = false;	//确认为boolean类型
		if(is3GUser != '0' && is3GUser != '1') {
			$.endPageLoading();
			alert('网络制式参数错误！');
			return false;
		}
		if(tradeTypeCode == '320' || tradeTypeCode == '321') { 	//一卡双号
			if(serialNumber == null || serialNumber == '') {
				$.endPageLoading();
				alert('一卡双号参数错误！');
				return false;
			}
		} else if(tradeTypeCode == '142') 
		{ 
		}
		else
		{
			//serialNumber = ''; 
		}
		//获取ocx版本  
		var ocxVersion = '';
		if ( test == 'yes' )
			ocxVersion = "1.1.1";
		else
			ocxVersion = getOcxVersion(); 
		if(!ocxVersion) 
		{ 
			$.endPageLoading();
			return false;
		}
		var snumber ="";
		var suserid = "";
		if(tradeTypeCode=='10'){
			snumber=serialNumber;
		}else if (tradeTypeCode == '320' || tradeTypeCode == '321') { 
			snumber=serialNumber;
			suserid = '&USER_ID='+ $.auth.getAuthData().get("USER_INFO").get("USER_ID");
		}else
		{
			snumber=$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
			suserid = '&USER_ID='+ $.auth.getAuthData().get("USER_INFO").get("USER_ID");
		}
		var remote = 'com.ailk.personserv.view.simcardmgr.RemoteCard'; //QueryListPart
		//Wade.httphandler.submit('EditPart','com.ailk.quickstart.view.examples.cust.CustMgrHandler','editCust','&a=b',
		//Wade.httphandler.submit('QueryCondPart', 'com.ailk.personserv.view.simcardmgr.RemoteCard' ,'checkVersion', '&OCX_VERSION=' + ocxVersion ,function(data){
		ajaxSubmit   ('', 'checkVersion', '&OCX_VERSION=' + ocxVersion+'&SERIAL_NUMBER=' + snumber, '', function(data){ 
			//获取远程写卡参数
			var rp = data.get(0);
			//alert(rp);
			if(!isReuse) { 
				//建立与RPS系统的连接  
				if ( test != 'yes' )
					if(initWebService(rp.get('PARA_CODE20')) != 0) return false;

				//PRS用户登陆认证  
				if ( test != 'yes' )
					if(login(rp.get('PARA_CODE21'), rp.get('PARA_CODE22')) != 0) return false;
			}

			//获取空卡序列号  
			var emptyCardId = '';
			if ( test == 'yes' )
				emptyCardId = '1811003650391902';
			else 
				emptyCardId = getEmptyCardId();  
			if ( emptyCardId  == false)
			{
				$.endPageLoading();
				return false;
			} 
			//判断是否为TD白卡
			var is3GCard = '0';
			if((rp.get('PARA_CODE18') + rp.get('PARA_CODE19')).indexOf('|' + emptyCardId.substring(6, 8) + '|') >= 0) {
				is3GCard = '1';
			}

			var iccid = '';
			if ( test == 'yes' )
				iccid = "ICCID1=111111117777777777&ICCID2=898602A7184143109807";
			else
				iccid = getICCID(); 
			if(!iccid) 
			{
				$.endPageLoading();
				return false;
			}
			var simCardNo = ''; 
			if ( test == 'yes' )
				simCardNo = "89860007186813159908";  
			else
				simCardNo = convertCoding(iccid, 'ICCID1');
			if(tradeTypeCode == '320' || tradeTypeCode == '321') {
				if(iccid.indexOf('ICCID2=') < 0) {
					alert('该卡不是一卡双号的卡，请换卡！');
					return false;
				}
			}

			if(tag == '0') {	//写卡流程
				if(!isReuse) {
					//获取IMSI
					/*
				var simimsi = getIMSI();
				if(!simimsi) return false;
				var imsi1 = convertCoding(simimsi, 'IMSI1');
				if(imsi1 != '' && imsi1.length > 4 && imsi1.substring(0, 4) == '4600') {
					alert('此卡不是空白卡，SIM卡号：' + simCardNo + '，请插入空白卡！');
					return false;
				}
					 */
				}
				var cardType = '1';	//卡类型：1-单号卡、2-双号卡
				var acctFlag = '0';	//0-单号卡写一个号或双号卡同时写两个号、1-双号卡写主号、2-双号卡写副号
				if(iccid.indexOf('ICCID2=') >= 0) { 
					cardType = '2'; 
					if(tradeTypeCode == '320' || tradeTypeCode == '321') {
						if(serialNumber.indexOf('-') < 0) {
							if(confirm('一卡双号，是否将' + serialNumber + '写入主号？\n   【确定】主号  【取消】副号')) {
								acctFlag = '1';
							} else {
								acctFlag = '2';
							}
						}
					} else {
						if(!confirm('此卡是双号卡，确定继续？')) {
							$.endPageLoading();
							return false;
						}
						acctFlag = '1';
					}
				} else {
					if(!isReuse && !confirm('将进行写卡操作，是否继续？')) {
						$.endPageLoading();
						return false;
					}
				}

				rp = null;

				var coding = '&EMPTY_CARD_ID=' + emptyCardId + '&CARD_TYPE=' + cardType + '&IS_3G_USER=' + is3GUser 
				//+'&SERIAL_NUMBER=' + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER") 2013-9-21这里有一卡双号，不能直接走这个
				+'&SERIAL_NUMBER=' + snumber //serialNumber 
				+'&TRADE_TYPE_CODE=' +tradeTypeCode
				+ '&IS_REUSE=' + (isReuse==true?1:0) + '&ACCT_FLAG=' + acctFlag;
				setTimeout(function() {
					$.beginPageLoading(); //QueryCondPart,  QueryListPart
					ajaxSubmit   ('', 'beforeWriteCard', coding, '', function(data){ 
						var codingObj = data.get(0); 
						var serialNumber = codingObj.get('SERIAL_NUMBER');
						var imsi1 = codingObj.get('IMSI');  
						var encodeString = codingObj.get('X_CODING_STR'); 
						codingObj = null;

						var resultCode;
						//alert('xyz2');
						//alert('写卡串:'+ encodeString);
						if(isReuse) {
							if ( test != 'yes' )
								resultCode = rePersonalize(encodeString); //today need open
							else
								resultCode = "0";
						} else if(is3GCard == '1') {
							if ( test != 'yes' )
								resultCode = personalizeTD(encodeString); //today need open
							else
							{
								alert('is3GCard:'+is3GCard);
								alert('3G写卡串:'+ encodeString);
								resultCode = "0";
							}
						} else {
							if ( test != 'yes' )
							{ 
								resultCode = personalize(encodeString);  
							}
							else
							{
								alert('is3GCard:'+is3GCard);
								alert('2G写卡串:'+encodeString);
								resultCode = "0";
							}
						}

						simCardNo = convertCoding(encodeString, 'ICCID1');
						var simCardNo2 = ''
							if(tradeTypeCode == '320' || tradeTypeCode == '321') {
								simCardNo2 = convertCoding(encodeString, 'ICCID2');
								simCardNo = simCardNo + '-' + simCardNo2; 
								imsi1 = convertCoding(encodeString, 'IMSI1') + '-' + convertCoding(encodeString, 'IMSI2');  
							}
						/*
					var simimsi = getIMSI();
					if(!simimsi) return false;
					var imsi1 = convertCoding(simimsi, 'IMSI1');
						 */
						var coding = '&RESULT=' + resultCode + '&CARDNUM=' + emptyCardId 
						+ '&ICCID=' + simCardNo + '&PHONENUM=' + serialNumber
						+'&SERIAL_NUMBER=' +snumber // $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER")
						+ '&IMSI='  + imsi1;  

						setTimeout(function() {
							$.beginPageLoading(); //QueryCondPart,QueryListPart
							ajaxSubmit   ('', 'afterWriteCard', coding, '', function(data){
								var obj = null;
								if(resultCode == 0) {
									obj = data.get(0); 
									obj.put('isReuse', isReuse);
									obj.put('RESULT', resultCode);

									if(obj.get('SIM_CARD_NO') == simCardNo) {
										obj.put('IMSI', convertCoding(encodeString, 'IMSI1'));
										obj.put('IMSI2', convertCoding(encodeString, 'IMSI2'));
									} else {
										obj.put('IMSI', convertCoding(encodeString, 'IMSI2'));
										obj.put('IMSI2', convertCoding(encodeString, 'IMSI1'));
									}
									if(tradeTypeCode == '320' || tradeTypeCode == '321')   
									{                                                     
										obj.put('SIM_CARD_NO2',convertCoding(encodeString, 'ICCID2'));
										obj.put('IMSI2',convertCoding(encodeString, 'IMSI2'));
										obj.put('SIM_CARD_NO',convertCoding(encodeString, 'ICCID1'));              
										obj.put('IMSI',convertCoding(encodeString, 'IMSI1'));
									}
									alert('写卡成功！啦 ^o^');
									$.endPageLoading();    
									//调用回调函数
									if(fct && fct != '') {  
										if(typeof(fct) == 'string') {
											eval(fct + '(obj)');
										} else {
											fct(obj);
										}
									}
									obj = null;
								}else if(resultCode == 1) {
									$.endPageLoading();  
									alert('写卡失败，该白卡可以继续使用！');
								} else {
									$.endPageLoading(); //2013-7-29
									alert('写卡失败，该白卡不能继续使用！');
								}   
								//断开RPS系统连接 today need open
								if ( test != 'yes' )
								{
									logout();
								}
								$.endPageLoading();  	 
								return resultCode;
							},function(e,i)
							{
								$.endPageLoading();    
								alert(e+":"+i); 
							}
							);



						}, 100);
					},function(e,i)
					{
						$.endPageLoading();    
						alert(e+":"+i); 
					});
				}, 100);
			} else if(tag == '1') {//读卡流程
				//校验SIM卡
				if(simCardNo == '' || simCardNo.length < 12) {
					alert('SIM卡号码错误！[' + simCardNo + ']');
					return false;
				}
				if(tradeTypeCode == '320' || tradeTypeCode == '321') {
					var simCardNo2 = convertCoding(iccid, 'ICCID2');
					if(simCardNo.substring(0, 5) != '89860' && simCardNo2.substring(0, 5) != '89860') {
						alert('此卡是空白卡 ，卡号为：' + emptyCardId + '，请执行写卡流程！');
						$.endPageLoading(); 
						return false;
					} else if(simCardNo.substring(0, 5) == '89860' && simCardNo2.substring(0, 5) == '89860') {
						alert('读卡成功！');
						//获取IMSI
						var simimsi = '';  
						if ( test == 'yes' )
							simimsi = 'sss&IMSI1=imsi1111111111111111&IMSI2=imsi22223333333333333332';
						else
							simimsi = getIMSI();
						if(!simimsi) return false;
						var imsi1 = convertCoding(simimsi, 'IMSI1');
						var imsi2 = convertCoding(simimsi, 'IMSI2');

						var obj = new Wade.DataMap();
						obj.put('SIM_CARD_NO', simCardNo);
						obj.put('SIM_CARD_NO2', simCardNo2);
						obj.put('IMSI', imsi1);
						obj.put('IMSI2', imsi2);
						obj.put('EMPTY_CARD_ID', emptyCardId); 

						//调用回调函数
						if(fct && fct != '') {
							if(typeof(fct) == 'string') {
								eval(fct + '(obj)');
							} else {
								fct(obj);
							}
						}

						//断开RPS系统连接
						if ( test != 'yes' )
						{
							logout(); 
						}

						obj = null;
						return 0;
					} else {
						alert('该卡只写入一个SIM卡号，不能用于一卡双号读卡流程！');
						$.endPageLoading();   
						return false;
					}
				} else if(tradeTypeCode == '142') {
					if(simCardNo.substring(0, 5) != '89860') {
						setTimeout(function() {
							$.beginPageLoading();//QueryCondPart,QueryListPart
							ajaxSubmit   ('', 'checkCardType', '&EMPTY_CARD_ID=' + emptyCardId+'&SERIAL_NUMBER=' + snumber//$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER")
									, '', function(data){ 
								var emptycard = data.get(0);
								var oldTypeCode = document.getElementById('OLD_SIM_TYPE_CODE').value;
								var oldTypeName = document.getElementById('RES_KIND_NAME').value;
								var newTypeCode = emptycard.get('RES_KIND_CODE');
								var newTypeName = emptycard.get('RES_KIND_NAME');//today 2013-7-9资源接口需支持此字段，支持后，改回去 RES_KIND_NAME');
								//alert(oldTypeCode + " new:" +newTypeCode);
								if(oldTypeCode != newTypeCode) {
									alert(emptyCardId + '\nBB此卡类型[' + newTypeName + '] 原卡类型[' + oldTypeName 
											+ ']\n如果继续，请执行写卡流程！');
									emptycard = null;
									$.endPageLoading();    
									return false;
								} else {
									alert('CC此卡是空白卡，卡号为：' + emptyCardId + '，请执行写卡流程！');
									$.endPageLoading(); 
									return false;
								}
							},
							function(e,i)
							{
								$.endPageLoading();    
								alert(e+":"+i); 
							});
						}, 100);
						return false;
					}
				} else {
					if(simCardNo.substring(0, 5) != '89860') {
						alert('DD此卡是空白卡，卡号为：' + emptyCardId + '，请执行写卡流程！');
						$.endPageLoading(); 
						return false;
					}
				}
				setTimeout(function() {//today 这个分支明显没有测试到位 2013-06-24
					//校验并预占SIM卡
					$.beginPageLoading(); //QueryCondPart
					ajaxSubmit('', 'readCard',suserid+'&SERIAL_NUMBER='+snumber //document.getElementById("AUTH_SERIAL_NUMBER").value+
							+'&TRADE_TYPE_CODE=' +tradeTypeCode+
							'&SIM_CARD_NO=' + simCardNo, null, function(data) {
						alert('读卡成功！');
						var obj = data.get(0);

						//断开RPS系统连接
						if ( test != 'yes' )
							logout(); //today need open

						//调用回调函数
						if(fct && fct != '') {
							if(typeof(fct) == 'string') {
								eval(fct + '(obj)');
							} else {
								fct(obj);
								$.endPageLoading(); 
							}
						}

						obj = null;
						return 0;
					},
					function(e,i)
					{
						$.endPageLoading();    
						alert(e+":"+i); 
					});
				}, 100);
			} else {
				alert('参数错误！');
			}

		},
		function(e,i)
		{
			$.endPageLoading();    
			alert(e+":"+i); 
		});



	}



}

function cancelTrade(tradeTypeCode) {
	//释放临时选占资源
	releaseResource(); 
}

//取消业务或关闭页面时释放资源
function releaseResource() {
	if(document.getElementById('NEW_SIM_CARD_NO') == null || document.getElementById('NEW_SIM_CARD_NO').value == '') {
		return;
	} 
	var emptyCoding = '&EMPTY_CARD_ID=';
	if(document.getElementById('EMPTY_CARD_ID') != null && document.getElementById('EMPTY_CARD_ID').value != '') {
		emptyCoding += document.getElementById('EMPTY_CARD_ID').value;
	}
	var simCoding = '&SIM_CARD_NO=';
	if(document.getElementById('NEW_SIM_CARD_NO') != null && document.getElementById('NEW_SIM_CARD_NO').value != '') {
		simCoding += document.getElementById('NEW_SIM_CARD_NO').value;
	} 
	ajaxSubmit('QueryCondPart', 'releaseResource', emptyCoding + simCoding+'&SERIAL_NUMBER=' + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER"), 'QueryListPart', function(data)
			{
		$.endPageLoading();    
		window.location.reload();  
			},
			function(e,i)
			{
				$.endPageLoading();    
				alert(e+":"+i); 
			}); 
}

/** 重个人化接口
 * fct：回调函数名，重个人化完后调用。
 * is3GUser：是否3G用户：0-否 1-是
 * tradeTypeCode：暂时没什么用，留作以后扩展用。
 */
function reuseCard(fct, is3GUser, tradeTypeCode) {
	$.beginPageLoading("校验写卡参数");  
	if(is3GUser != '1') {
		is3GUser = '0';
	} 
	var reuse = 'person.simcardmgr.RemoteCard'; 
	var ocxVersion = getOcxVersion(); 

	//校验ocx版本(dbOcxVersion为空则不校验)  2013-10-22
	ajaxSubmit   ('', 'checkVersion', '&OCX_VERSION=' + ocxVersion+'&SERIAL_NUMBER=' + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER"), 'QueryListPart', function(data){ 
		//ajaxSubmit   ('QueryCondPart', 'checkVersion', '&OCX_VERSION=' + ocxVersion+'&SERIAL_NUMBER=' + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER"), 'QueryListPart', function(data){ 
		//获取远程写卡参数 
		$.endPageLoading(); 
		var rp = data.get(0);

		var emptyCardId = '';
		var simCardNo   = ''; 
		if(initWebService(rp.get('PARA_CODE20')) != 0) return false;

		//PRS用户登陆认证
		if ( test != 'yes' )
		{
			if(login(rp.get('PARA_CODE21'), rp.get('PARA_CODE22')) != 0) return false;
		}
		//获取空卡ID
		if ( test == 'yes' )
			emptyCardId ='11111';
		else
			emptyCardId = getEmptyCardId();
		if(!emptyCardId) return false;

		//获取SIM卡序列号
		var iccid ='';
		if ( test == 'yes' )
			iccid = "ICCID1=898600D8188099717100&ICCID2=898602A7184143109807";
		else
			iccid = getICCID();
		if(!iccid) return false;

		var simCardNo = convertCoding(iccid, 'ICCID1');

		if((simCardNo + '00000').substring(0, 5) != '89860') {
			alert('此卡是空白卡，不能进行SIM卡重个人化！');
			//断开RPS系统连接
			if ( test != 'yes' )
				logout();
			$.endPageLoading();  
			return false;
		}

		if(!confirm('暂不支持TD卡，确定进行SIM卡重个人化？')) {
			//断开RPS系统连接
			if ( test != 'yes' )
				logout();
			$.endPageLoading();  
			return false;
		}

		var eraseFlag = true;
		if(confirm('是否保留SIM卡内的通讯录和短信息？\n   【确定】保留  【取消】清除')) {
			eraseFlag = false;
		}
		$.beginPageLoading();
		setTimeout(function() {
			$.beginPageLoading();
			ajaxSubmit   ('QueryCondPart', 'check4ReuseCard', '&EMPTY_CARD_ID=' + emptyCardId + '&SIM_CARD_NO=' + simCardNo+'&SERIAL_NUMBER=' + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER"), 
					'QueryListPart',  function() { 

				//清除卡内个人信息
				if(eraseFlag) {
					if(clearSPBADNSMS(true, true, true) != 0) {
						if(!confirm('清除卡内个人信息失败，是否继续？')) {
							//断开RPS系统连接
							if ( test != 'yes' )
								logout();
							$.endPageLoading();   
							return;
						}
					}
				} 
				invokeCard(0, fct, is3GUser, tradeTypeCode, document.getElementById("AUTH_SERIAL_NUMBER").value, true);
			},
			function(e,i)
			{
				$.endPageLoading();    
				alert(e+":"+i); 
			});  
		}, 100);
	},function(e,i)
	{
		$.endPageLoading();    
		alert(e+":"+i); 
	});
}

//解析传入的串，并返回字段paraName的值
function convertCoding(codingStr, paraName) {
	var arrayReuse = new Array();
	arrayReuse = codingStr.split('&');
	for(var i = 0; i < arrayReuse.length; i++) {
		if(arrayReuse[i].indexOf('=') >= 0) {
			if(paraName == arrayReuse[i].substring(0, arrayReuse[i].indexOf('='))) {
				var paraValue = arrayReuse[i].substr(arrayReuse[i].indexOf('=') + 1);
				arrayReuse = null;
				return paraValue;
			}
		}
	}
	arrayReuse = null;
	return null;
}

/**
 * VIP免费赠送选择框点击事件
 */                       
function vipFreeCheckClick()
{ 
	var vipFreeCheck = document.getElementById("vipFreeCheck_content"); 
	var scoreForCard = document.getElementById("scoreForCard_content"); 

	var cardFeeTypeCode = document.getElementById("cardFeeTypeCode").value;
	var cardFee = document.getElementById("cardFeeText").value; 

	if(vipFreeCheck.checked == true)
	{
		if(scoreForCard.checked == true)
		{   
			if( scoreForCard.style.display=="")
			{ 
				$("#scoreForCard_content").attr("checked",false);
			}
		}
		dealFee(cardFeeTypeCode, "0"); 

	}
	else
	{
		dealFee(cardFeeTypeCode, cardFee); 
	}	

}

/**
 * 积分兑换卡费CHECK点击事件
 */
function scoreForCardCheck()
{ 
	var vipFreeCheck = document.getElementById("vipFreeCheck_content"); 
	var scoreForCard = document.getElementById("scoreForCard_content"); 
	var cardFeeTypeCode =document. getElementById("cardFeeTypeCode").value;
	var strCardFee = document.getElementById("cardFeeText").value;
	if(scoreForCard.checked ==true)
	{
		if( vipFreeCheck.checked == true)
		{ 
			if( vipFreeCheck.style.display=="" )
			{ 
				$("#vipFreeCheck_content").attr("checked",false);
				dealFee(cardFeeTypeCode, strCardFee);  
			}	
		}
		document.getElementById("DEDUCE_SCORE").value = document.getElementById("initExScoreText").value;
		document.getElementById("DEDUCE_FEE").value = document.getElementById("initExScoreText").value/document.getElementById("initExCardFeeText").value;
		showLayer('pwdModify');
	}
	else
	{
		document.getElementById("DEDUCE_SCORE").value = "0";
		document.getElementById("DEDUCE_FEE").value = "0";
		dealFee(cardFeeTypeCode, strCardFee);  
		hideLayer('pwdModify');
	}	

}
function showLayer(optionID) { 
	document.getElementById(optionID).style.display = "block";
}
//隐藏修改密码框
function hideLayer(optionID) {
	$("#pwdModify").css("display","none"); 
}

/**
 * 积分兑换卡费页面关闭事件
 */
function scoreForCardClose()
{
	document.getElementById("DEDUCE_SCORE").value = "0";
	document.getElementById("DEDUCE_FEE").value = "0"; 
	$("#scoreForCard_content").attr("checked",false); 
}

/**
 * 积分兑换卡费确定事件
 */
function scoreForCardConfirm()
{
	var strDeduceScore = document.getElementById("DEDUCE_SCORE").value;
	var strUserScore = document.getElementById("USER_SCORE").value;
	var strDeduceCardFee = document.getElementById("DEDUCE_FEE").value;
	var strCardFee = document.getElementById("cardFeeText").value;

	if(parseInt(strDeduceScore) > parseInt(strUserScore))
	{
		alert("用户当前积分不够");
		return;
	} 

	if(parseInt(strDeduceCardFee) > parseInt(strCardFee))
	{
		alert("扣减费用超过卡费！");
		return;
	} 
	var cardFee = parseInt(strCardFee) - (parseInt(strDeduceCardFee)*100);
	var cardFeeTypeCode = document.getElementById("cardFeeTypeCode").value;
	dealFee(cardFeeTypeCode, cardFee);  
	hideLayer('pwdModify');   
}

/**
 * 抵扣积分框离开事件
 */
function deduceScoreOnBlur()
{
	var strDeduceScore = document.getElementById("DEDUCE_SCORE").value;
	var strInitDeduceCardFee = document.getElementById("initExCardFeeText").value;
	if(strDeduceScore % strInitDeduceCardFee != 0)
	{
		alert("填写积分应是"+strInitDeduceCardFee+"的倍数");
		return;
	}
	var strDeduceCardFee = strDeduceScore / strInitDeduceCardFee;

	document.getElementById("DEDUCE_FEE").value = strDeduceCardFee;
	return;
}
//切换读写卡（远程写卡）
function change141Button() {
	var chkRead = document.getElementById('chkRead').checked;
	if(chkRead) {
		document.getElementById('readAndWrite').value='读卡';
		//document.getElementById('readAndWrite').children[1].innerHTML='读卡';
		$("#readAndWrite span:first").text("读卡");
		document.getElementById('chkRead').value='true';
	} else {
		//document.getElementById('readAndWrite').value='写卡';
		//document.getElementById('readAndWrite').children[1].innerHTML='写卡';
		$("#readAndWrite span:first").text("写卡");
		document.getElementById('chkRead').value='false';
	}
}
function submit ( p )
{   
	$.cssubmit.submitTrade('userInfoPart_newSim,QueryCondPart,AuthPart,inputPWDInfoPart,userInfoPart,chPWDInfoPart', null, p+'&SERIAL_NUMBER=' + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER") , null, null, null); 
	return  false; 
}