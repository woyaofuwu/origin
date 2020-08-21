(function($){ 
	$.extend({simcardPhone:{
		clazz:"com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.SimCardHandler",
		mode:null,					//写卡模式(手机只能为1)
		serialNumber: null,			//单卡号码(取PC端)
		beforeWriteSVC:null,		//写卡前服务
		checkWriteSVC:null,			//写卡后确认服务（预制卡需要调用）
		afterWriteSVC:null,			//写卡后服务
		netTypeCode:null,          //区分资源库
		params:null,                //自定义入参(取PC端)
        simICCID:null,		//新大陆读卡设备读取
        iccSerial:null,		//新大陆读卡设备读取
        cardInfo:null,      //新大陆读卡设备读取
        simATR:null,		//新大陆读卡设备读取
        exeResult:null,		//新大陆读卡设备读取
        errorDesc:null,		//新大陆读卡设备读取
        resIMSIParam:null,		//从资源获取，由于新大陆写卡设备都是用的异步回掉函数，需要在写卡前做存储用
        phoneNumParam:null,		//从资源获取，由于新大陆写卡设备都是用的异步回掉函数，需要在写卡前做存储用
        tradeIdParam:null,		//从资源获取，由于新大陆写卡设备都是用的异步回掉函数，需要在写卡前做存储用
        iccidParam:null,		//从资源获取，由于新大陆写卡设备都是用的异步回掉函数，需要在写卡前做存储用
        emptyCardIdParam:null,		//从资源获取，由于新大陆写卡设备都是用的异步回掉函数，需要在写卡前做存储用
        phoneNum:1,		//手机版只能为1
        isNew:'1',//手机版只支持新卡
		init:function(){
			$.simcardPhone.mode = $("#_CardCmpParam").attr("mode");
			//mode(1-2),读卡写卡[1：单卡，2：双卡]（手机版本只支持单卡）
			if($.simcardPhone.mode==1 || $.simcardPhone.mode==2){
				$.simcardPhone.beforeWriteSVC = $("#_CardCmpParam").attr("beforeWriteSVC");
				$.simcardPhone.checkWriteSVC = $("#_CardCmpParam").attr("checkWriteSVC");
				$.simcardPhone.afterWriteSVC = $("#_CardCmpParam").attr("afterWriteSVC");
				$("#readCardBtn").bind("click", $.simcardPhone.events.onClickReadCardBtnPhone);
                $("#writeCardBtn").bind("click", $.simcardPhone.events.onClickWriteCardBtnPhone);
				$("#writeCardBtn").attr("disabled",true);
			}
		},
        //读卡操作第一步
        readCardPhoneFirst:function(){
		    //alert('readCardPhoneFirst');
            var phoneNum= $.simcardPhone.phoneNum;
            var simICCID = $.simcardPhone.simICCID;
            var iccSerial = $.simcardPhone.iccSerial;
            //如果读卡的时候是空白卡，需要执行写卡流程
            if(simICCID.substring(0,5) != "89860"){
                MessageBox.alert("空白卡,卡号为："+iccSerial+"，请执行写卡流程！");
                $("#writeCardBtn").attr("disabled",false);
                var readAfterAction = $("#_CardCmpParam").attr("readAfterAction");
                if(readAfterAction && readAfterAction != ""){
                    var m=$.DataMap();
                    m.put("PHONE_NUM", phoneNum);
                    m.put("EMPTY_CARD_ID", iccSerial);
                    m.put("IS_WRITED", "0");		//是否被写过0为没写过
                    (new Function("var data=arguments[0];"+ readAfterAction + ";"))(m);
                }
                return;
            }
            //如果读卡的时候是非空白卡，需要执行校验流程
            var param="&EPARCHY_CODE="+$("#_CardCmpParam").attr("eparchyCode");
            param+="&SIM_CARD_NO="+simICCID;
            param+="&SERIAL_NUMBER="+$.simcard.getSerialNumber();
            param+="&NET_TYPE_CODE="+$.simcard.netTypeCode;
            $.beginPageLoading("校验SIM卡。。。");
            $.httphandler.get($.simcardPhone.clazz, "checkSimCard", param,
                function(data){
                    $.endPageLoading();
                    $.simcard.afterReadCardAction(phoneNum, simICCID, iccSerial, data);
                },function(code, info, detail){
                    $.endPageLoading();
                    MessageBox.error("错误提示","校验SIM卡错误！", null, null, info, detail);
                });
        },
        //写卡操作第一步
        writeCardPhoneFirst:function(){
            //alert('writeCardPhoneFirst');
            var flag=true;
            var strICCID = $.simcardPhone.simICCID;
            if(strICCID.substring(0,5) == "89860" ){
                alert("非空白卡，不能进行写卡，请插入白卡！");
                return;
            }
            if(strICCID != 'FFFFFFFFFFFFFFFFFFFF') {
                alert("当前只支持白卡写卡操作，请确认当前卡是否白卡！");
                return;
            }
            var writeBeforeAction = $("#_CardCmpParam").attr("writeBeforeAction");
            if(writeBeforeAction && writeBeforeAction != ""){
                flag=(new Function("return "+writeBeforeAction + ";"))();
            }
            if(!flag) return;
            $.simcardPhone.writeCardPhoneSecond();
        },
        //写卡操作第二步
        writeCardPhoneSecond:function(){
			//alert('writeCardPhoneSecond');
            var eparchyCode = $("#_CardCmpParam").attr("eparchyCode");
            MessageBox.confirm("确认提示","接下来将进行写卡操作，是否继续？", function(btn){
                if(btn != "ok") {
                    return;
                }
                if("0898" != eparchyCode){
                    MessageBox.alert("未知的接入方式！");
                    return false;
                }
                var sn = $.simcard.getSerialNumber();
                var phoneNum=$.simcardPhone.phoneNum;
                var simICCID=$.simcardPhone.simICCID;
                var iccSerial=$.simcardPhone.iccSerial;
                var param = "&EPARCHY_CODE="+eparchyCode;
                param += "&PHONE_NUM="+phoneNum;
                param += "&EMPTY_CARD_ID="+iccSerial;
                param += "&IS_NEW="+$.simcardPhone.isNew;
                param += "&MODE="+$.simcardPhone.mode;
                param += "&SERIAL_NUMBER="+sn;
                if($.simcardPhone.mode == 2){
                    MessageBox.alert("该卡为单号卡，请到换卡界面写卡！");
                    return false;
                }
                if(!sn){
                    MessageBox.alert("手机号码不能为空！");
                    return false;
                }
                if( simICCID.substring(0,5) == "89860" ){
                    MessageBox.alert("非空白卡，不能进行写卡，请插入白卡！");
                    return false;
                }
                //设置自定义写卡前服务
                if($.simcardPhone.beforeWriteSVC){
                    param +="&BEFORE_WRITE_SVC="+$.simcardPhone.beforeWriteSVC;
                }
                //自定义参数
                if($.simcard.params){
                    param +="&PARAMS="+$.simcard.params;
                }
                $.beginPageLoading("开始写卡。。。");
                //MessageBox.alert("开始写卡参数："+param);
                $.httphandler.get($.simcardPhone.clazz, "beforeWriteCard", param,
                    function(data){
                        $.endPageLoading();
                        if(!data || !data.length){
                            $.endPageLoading();
                            MessageBox.alert("写卡前校验错误,不能获取写卡参数串！");
                            return;
                        }
                        $.simcardPhone.writeCardPhoneThree(data, phoneNum, iccSerial);
                    },function(code, info, detail){
                        $.endPageLoading();
                        MessageBox.error("错误提示","写卡前校验报错！", null, null, info, detail);
                    });
                $.endPageLoading();
            });
        },
        //写卡操作，对于预制卡需要多一步确认操作，同时IMSI需要从后台返回，控件暂时不支持，预制卡的IMSI则可以直接读取（手机版）
        writeCardPhoneThree:function(data, phoneNum, emptyCardId) {
            //alert('writeCardPhoneThree');
            $.simcardPhone.resIMSIParam = data.get("IMSI");
            $.simcardPhone.phoneNumParam = phoneNum;
            $.simcardPhone.tradeIdParam = data.get("TRADE_ID");
			$.simcardPhone.iccidParam = data.get("SIM_CARD_NO");
			$.simcardPhone.emptyCardIdParam = emptyCardId;
            var strEncode = data.get("ENCODE_STR");//"720700037D0101700000681106000505B000F274D8A2A60B071B65FB024D1A153C281C071468A804043D8B9C2FF96760AA7CA21315375AEDBC4AA0DA81374481ACFFFE47B58CF9A43F59E8250F18F9620830304234D37ED46879DAE8F18A8D484B0D8DE54D0517D650B9192C6ED83FFBAE802C";
            //MessageBox.alert('写入的simData信息' + strEncode);
            try {
                window.MBOP.writeCard(strEncode, "$.simcardPhone.writeCardPhoneThird");
            } catch (e) {
                alert('写卡设备设备出现错误!');
            }

        },
        writeCardPhoneThird:function(writeCardResult) {
            //MessageBox.alert('进入写卡回调函数writeCardResultPhone----');
            MessageBox.alert("写卡结果:"+writeCardResult);
            //如果写卡失败，则直接返回
            var writeResultJson = $.parseJSON(writeCardResult);
            var writeResultMap  = new $.DataMap(writeResultJson);
            if(writeResultMap.get("exeResult") == "1") {
                alert(writeResultMap.get("exeResult"));
                return;
            }
            var imsi = $.simcardPhone.resIMSIParam;
            var cardInfo = $.simcardPhone.cardInfo;
            var param ="&CARD_INFO="+cardInfo+"&RESULT_INFO="+writeCardResult+"&TRADE_ID="+$.simcardPhone.tradeIdParam;
            //设置自定义写卡后确认服务
            if($.simcardPhone.checkWriteSVC){
                param +="&CHECK_WRITE_SVC="+$.simcardPhone.checkWriteSVC;
            }
            $.beginPageLoading("写卡确认前校验。。。");
            $.httphandler.get($.simcardPhone.clazz, "checkWriteCard", param, function(data){
            	$.endPageLoading();
                $.simcardPhone.writeCardSuccessFirst("0", $.simcardPhone.phoneNumParam, $.simcardPhone.iccidParam, $.simcardPhone.emptyCardIdParam, imsi);
            },function(code, info, detail){
                $.endPageLoading();
                MessageBox.error("错误提示","写卡校验报错！", null, null, info, detail);
            });

        },
        writeCardSuccessFirst:function(resultCode, phoneNum, iccid, emptyCardId, imsi){
            //alert('writeCardSuccessFirst');
			var data=$.DataMap();
			var param = "&RESULT_CODE="+resultCode;
			param += "&EMPTY_CARD_ID="+emptyCardId;
			param += "&EPARCHY_CODE="+$("#_CardCmpParam").attr("eparchyCode");
			param += "&SERIAL_NUMBER="+$.simcard.getSerialNumber();
			param += "&PHONE_NUM="+phoneNum;
			data.put("EMPTY_CARD_ID", emptyCardId);
			param += "&SIM_CARD_NO="+iccid;
			param += "&IMSI="+imsi;
			data.put("SIM_CARD_NO", iccid);
			data.put("IMSI", imsi);
			data.put("RESULT_CODE", resultCode);
			//设置自定义写卡后服务
			if($.simcard.afterWriteSVC){
				param +="&AFTER_WRITE_SVC="+$.simcard.afterWriteSVC;
			}
			$.beginPageLoading("写卡确认。。。");
			$.httphandler.get($.simcardPhone.clazz, "afterWriteCard", param,function(){
				$.endPageLoading();
				var writeAfterAction = $("#_CardCmpParam").attr("writeAfterAction");
				if(writeAfterAction && writeAfterAction != ""){
					data.put("WRITE_TAG","0");//白卡/成卡标记
					(new Function("var data=arguments[0];"+ writeAfterAction + ";"))(data);
				}
			},function(code, info, detail){
				$.endPageLoading();
				MessageBox.error("错误提示","写卡错误！", null, null, info, detail);
			});
		},
		events:{
            //点击读卡
            onClickReadCardBtnPhone:function(){
                //alert("onClickReadCardBtnPhone");
                var flag=true;
                var readBeforeAction = $("#_CardCmpParam").attr("readBeforeAction");
                if(readBeforeAction && readBeforeAction != ""){
                    flag=(new Function("return "+readBeforeAction + ";"))();
                }
                if(!flag) return;
                $.simcardPhone.events.readOrWriteChangePhone('read');

            },
            //点击写卡
            onClickWriteCardBtnPhone:function(){
                //alert("onClickWriteCardBtnPhone");
                $.simcardPhone.events.readOrWriteChangePhone('write');

            },
            //调用新大陆外驱动设备
            readOrWriteChangePhone:function(tag){
                try {
                    if(tag == 'read') {
                        try {
                            alert("开始读卡。。。。。");
                            window.MBOP.simGetSimInfo("$.simcardPhone.events.readCardCallBack");
                        } catch (e) {
                            alert("读卡设备出现错误！");
                        }
                    }
                    else {
                        alert("写卡前读取卡信息。。。。。");
                        try {
                            window.MBOP.simGetSimInfo("$.simcardPhone.events.writeCardCallBack");
                        } catch (e) {
                            alert("写卡设备出现错误！");
                        }
                    }
                } catch (e) {
                    alert("控件报错！");
                }
            },
            //读卡回掉
            readCardCallBack:function(ret){
                $.simcardPhone.events.clearSIMInfoPhone();
                //alert("读卡回调信息"+ret);
                var simInfoJson = $.parseJSON(ret);
                var simInfo  = new $.DataMap(simInfoJson);
                $.simcardPhone.simICCID = simInfo.get("simICCID");
                $.simcardPhone.iccSerial = simInfo.get("iccSerial");
                $.simcardPhone.simATR = simInfo.get("simATR");
                $.simcardPhone.exeResult = simInfo.get("exeResult");
                $.simcardPhone.errorDesc = simInfo.get("errorDesc");
                $.simcardPhone.readCardPhoneFirst();
            },
            //写卡回掉
            writeCardCallBack:function(ret){
                $.simcardPhone.events.clearSIMInfoPhone();
                //alert("写卡前读取卡信息回调信息"+ret);
                var simInfoJson = $.parseJSON(ret);
                var simInfo  = new $.DataMap(simInfoJson);
                $.simcardPhone.simICCID = simInfo.get("simICCID");
                $.simcardPhone.iccSerial = simInfo.get("iccSerial");
                $.simcardPhone.simATR = simInfo.get("simATR");
                $.simcardPhone.exeResult = simInfo.get("exeResult");
                $.simcardPhone.errorDesc = simInfo.get("errorDesc");
                $.simcardPhone.writeCardPhoneFirst();
            },
            //重置读卡数据
            clearSIMInfoPhone:function(){
                $.simcardPhone.simICCID = null;
                $.simcardPhone.iccSerial = null;
                $.simcardPhone.cardInfo = null;
                $.simcardPhone.simATR = null;
                $.simcardPhone.exeResult = null;
                $.simcardPhone.errorDesc = null;
            }
		}
	}});
})(Wade);