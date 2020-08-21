var test="no";
/** 读写卡接口
 * tag: 0-写卡 1-读卡
 * fct：回调函数名，读写卡完后调用
 * is3GUser：是否3G用户：0-否 1-是
 * tradeTypeCode：暂时没什么用，留作以后扩展用
 */
function invokeCard4USIM(tag, fct, is3GUser, tradeTypeCode, serialNumber, isReuse,cardSN){//这里的cardSN 相当于白卡序列号
	$.beginPageLoading();
	/*if(tradeTypeCode == '320' || tradeTypeCode == '321') {	//一卡双号
		if(serialNumber == null || serialNumber == '') {
			$.endPageLoading();
			alert('一卡双号参数错误！');
			return false;
		}
	} */
	//获取ocx版本  
	var ocxVersion = '';
	if ( test == 'yes' )
		ocxVersion = "v1.0.0";
	else
		ocxVersion = getOPSVersion4USIM(); 
	if(ocxVersion == false)
	{
		$.endPageLoading();
		alert("获取控件版本出错！请检查控件");
		return false;
	}
	var snumber ="";
	var suserid = "";
	if(tradeTypeCode=='10'){
		snumber=serialNumber;
	}else if (tradeTypeCode == '320' || tradeTypeCode == '321') {//一卡双号
		snumber=serialNumber;
		suserid = '&USER_ID='+ $.auth.getAuthData().get("USER_INFO").get("USER_ID");
	}else{
		snumber=$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
		suserid = '&USER_ID='+ $.auth.getAuthData().get("USER_INFO").get("USER_ID");
	}

	hhSubmit(null,'com.ailk.personserv.view.common.simcardmgr.SimCardBasePage4USIM','checkVersion4USIM','&OCX_VERSION=' + ocxVersion+'&SERIAL_NUMBER=' + snumber,function (data){
		$.endPageLoading();
		var ocxdata = data.get(0);
		var smsp = ocxdata.get("PARA_CODE2");
		var empCardNO = '';
		var empCardInfo ='';
		if(test=='yes'){
			empCardNO = '1811003650391902';
		}else{
			empCardInfo = getCardInfo4USIM();//例如：080AFFFFFFFFFFFFFFFFFFFF0E0A01137001080000000010|0000,需要进行iCCID和emptyCardId的区分
			if(empCardInfo == false){
				alert("获取卡片信息出错！");
				return false;
			}

			var empCardInfoArr = new Array();
			empCardInfoArr = empCardInfo.split("0E0A");
			var iccidval = empCardInfoArr[0];
			var iccidvalArr = new Array();
			iccidvalArr = iccidval.split("080A");
			if(iccidvalArr.length > 2){
				//一卡双号
			}else{
				empCardNO = iccidvalArr[1];//经过处理后得到卡号这里相当于ICCID,单卡
				empCardNO = takeBackIccid(empCardNO);
				if(empCardNO == ""){
					return false;
				}
			}
		}
//		alert(empCardNO);

		if(tag == '0'){//写卡操作
			var cardType = '1';	//卡类型：1-单号卡、2-双号卡
			var acctFlag = '0';	//0-单号卡写一个号或双号卡同时写两个号、1-双号卡写主号、2-双号卡写副号

			if(!confirm('将进行写卡操作，是否继续？')){
				return false;
			}
			var param = '&EMPTY_CARD_ID=' + cardSN + '&CARD_TYPE=' + cardType + '&IS_3G_USER=' + is3GUser 
			+'&SERIAL_NUMBER=' + snumber //serialNumber 
			+'&TRADE_TYPE_CODE=' +tradeTypeCode
			//+'&CARD_NO=' + card// add
			+'&SMSP=' +smsp //ADD
			+ '&IS_REUSE=' + (isReuse==true?1:0) + '&ACCT_FLAG=' + acctFlag;
			$.beginPageLoading();
			hhSubmit(null,"com.ailk.personserv.view.common.simcardmgr.SimCardBasePage4USIM","beforeWriteCard4USIM", param, function(data){//写卡前数据封装
				$.endPageLoading();
				var resData = data.get(0);
				var issueResultCode = resData.get("ASS_RESULT_CODE");
				var issueResultMessage = resData.get("ASS_RESULT_MESSAGE");
				var imsi1 = resData.get("IMSI");
				var encodeString = resData.get('X_CODING_STR'); 
				if(issueResultCode != "0"){
					alert("加密报文出错！"+ issueResultMessage);
					return false;
				}
				var issueData = resData.get("ISSUE_DATA");//获取实时写卡数据
				var writeRes = writeCardInfo4USIM(issueData);//获取写卡结果
				if(writeRes == false){
					alert("写卡失败！请更换新SIM卡！");
					return false;//写卡错误
				}
				empCardInfo = getCardInfo4USIM();//例如：080AFFFFFFFFFFFFFFFFFFFF0E0A01137001080000000010|0000,需要进行OCCID和cardno的区分

				if(empCardInfo == false){
					alert("获取卡片信息出错！");
					return false;
				}
				//				alert("写卡成功后的cardInfo-====="+empCardInfo);
				var empCardInfoArr = new Array();
				empCardInfoArr = empCardInfo.split("0E0A");
				var iccidval = empCardInfoArr[0];
				var iccidvalArr = new Array();
				iccidvalArr = iccidval.split("080A");
				if(iccidvalArr.length > 2){
					//一卡双号
				}else{
					empCardNO = iccidvalArr[1];//经过处理后得到卡号这里相当于ICCID,单卡
					empCardNO = takeBackIccid(empCardNO);
					if(empCardNO == ""){
						return false;
					}
				}

				$.beginPageLoading();
				var coding = '&RESULT=' + issueResultCode + '&CARDNUM=' + cardSN 
				+ '&ICCID=' + empCardNO + '&PHONENUM=' + serialNumber
				+ '&CARD_RSP='+writeRes+'&CARD_INFO='+empCardInfo
				+'&SERIAL_NUMBER=' +snumber // $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER")
				+ '&IMSI='  + imsi1; 

				hhSubmit(null,"com.ailk.personserv.view.common.simcardmgr.SimCardBasePage4USIM","afterWriteCard4USIM",coding, function(data){//写卡后数据校验
					$.endPageLoading();
					var writeResData = data.get(0);
					var resCode = writeResData.get("RESULT_CODE");//写卡校验结果编码
					var resMess = writeResData.get("RESULT_MESSAGE");//写卡校验结果信息
					if(resCode=='0'){
						writeResData.put("isReuse",isReuse);
						writeResData.put("RESULT",resCode);
//						writeResData.put("IMSI",isReuse);//imsi1
//						writeResData.put("IMSI2",isReuse);//imsi2
						if(writeResData.get('SIM_CARD_NO') == empCardNO) {
							writeResData.put('IMSI', convertCoding(encodeString, 'IMSI1'));
							writeResData.put('IMSI2', convertCoding(encodeString, 'IMSI2'));
						} else {
							writeResData.put('IMSI', convertCoding(encodeString, 'IMSI2'));
							writeResData.put('IMSI2', convertCoding(encodeString, 'IMSI1'));
						}
						//预留一卡双号
						alert('写卡成功！');
						if(fct && fct != '') {  
							if(typeof(fct) == 'string') {
								eval(fct + '(writeResData)');
							} else {
								fct(writeResData);
							}
						}
					}else{
						alert(resMess+"！，错误编码为："+resCode);
						return false;
					}
				},function(e,i)	{
					$.endPageLoading();    
					alert(e+":"+i); 
				});

			},function(e,i)	{
				$.endPageLoading();    
				alert(e+":"+i); 
			});

		}else if(tag == '1'){//读卡操作
			if(tradeTypeCode == '142'){//单卡
				if(empCardNO.substring(0, 5) != '89860'){
					$.beginPageLoading();
					hhSubmit(null,"com.ailk.personserv.view.common.simcardmgr.SimCardBasePage4USIM","checkCardType", '&EMPTY_CARD_ID='+cardSN+'&SERIAL_NUMBER='+snumber, function(data){//卡类型校验
						$.endPageLoading();    
						var cardInfo = data.get(0);
						var oldTypeCode = $("#OLD_SIM_TYPE_CODE").val();
						var oldTypeName = $("#RES_KIND_NAME").val();
						var newTypeCode = cardInfo.get("RES_KIND_CODE");
						var newTypeName = cardInfo.get("RES_KIND_NAME");
						if(oldTypeCode != newTypeCode) {
							alert(cardSN + '\nBB此卡类型[' + newTypeName + '] 原卡类型[' + oldTypeName 
									+ ']\n如果继续，请执行写卡流程！');
							cardInfo = null;
							$.endPageLoading();    
							return false;
						} else {
							alert('CC此卡是空白卡，卡号为：' + cardSN + '，请执行写卡流程！');
							$.endPageLoading();   
							return false;
						}
					},function(e,i)	{
						$.endPageLoading();    
						alert(e+":"+i); 
					});
				}
			}else{
				if(empCardNO.substring(0, 5) != '89860') {
					alert('DD此卡是空白卡，卡号为：' + cardSN + '，请执行写卡流程！');
					$.endPageLoading(); 
					return false;
				}
			}
			
			if(empCardNO.substring(0, 5) != '89860'){
				return ;
			}else{
				$.beginPageLoading();
				hhSubmit(null,"com.ailk.personserv.view.common.simcardmgr.SimCardBasePage4USIM","readCard", suserid+'&SERIAL_NUMBER='+snumber+'&TRADE_TYPE_CODE='+tradeTypeCode+'&SIM_CARD_NO='+empCardNO, function(data){//卡类型校验
					$.endPageLoading();
					alert("读卡成功！");
					var resObj = data.get(0);
//				alert(resObj);
					if(fct && fct != '') {
						if(typeof(fct) == 'string') {
							eval(fct + '(resObj)');
						} else {
							fct(resObj);
							$.endPageLoading(); 
						}
					}
					resObj = null;
					return 0;//需要验证
					
				},function(e,i)	{
					$.endPageLoading();    
					alert(e+":"+i); 
				});
			}
			
			
			//单卡多号
		}
	},function(e,i){
		$.endPageLoading();    
		alert(e+":"+i); 
	});
}

function takeBackIccid(iccid){
	var backIccid = "";
	var len = iccid.length;
	if(len != 20){
		alert("此卡序列号位数不正确！不是20位，是"+len+"位！");
		return "";
	}
	for(var a=0;a<len;a++){
		backIccid += iccid.charAt(a+1);
		backIccid += iccid.charAt(a);
		a++;
	}
	return backIccid;
}

