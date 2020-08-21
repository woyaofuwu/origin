(function($){
    $.extend({printMgr:{
        printData:$.DatasetList(),
        elcNoteData:$.DataMap(),
        printIndex: null,		//打印索引
        params: $.DataMap(),	//[PRT_TYPE,EPARCHY_CODE,ORDER_ID]
        printEvent: null,	 	//打印事件
        printParam: $.DataMap(),	//打印个性化参数
        eDocServUrl:"/eb/NewYwblCmd",			//电子工单打印的服务地址
        edocPrintLock:false,				//电子工单打印锁
        edocPrintOcx:null,					//电子工单ActiveX组件
        finishPrinted:true,                 //是否该打的票据或者电子工单都打了
        //设定打印事件
        bindPrintEvent:function(func){
            $.printMgr.printEvent = func;
        },
        //设置特殊自定义打印参数
        addPrintParam:function(paramData){
            if(!paramData || !paramData.length){
                return;
            }
            paramData.eachKey(function(key, idx, total){
                $.printMgr.printParam.put(key, paramData.get(key));
            });
        },
        setPrintParam:function(key, value){
            if(!key || !value) return ;
            $.printMgr.printParam.put(key, value);
        },

        /**
         * 获取打印数据,主要是在业务登记完成之后调用
         * tradeData[ORDER_ID, EPARCHY_CODE, CHECK_MODE, TRADE_TYPE_CODE]
         * CHECK_MODE 认证校验方式
         * TRADE_ID 该键值自定义的打印，可能会传入，后续在一单清，票据查询，更新打印标记时候，会做特殊处理
         */
        printTrade : function(inputData){
            //如果存在打印数据，则直接打印，否则重新加载打印数据
            var infos = $.printMgr.getPrintData();
            if(infos && infos.length){

                $.printMgr.resetPrintFlag();	//重置打印轮询标记
                $.printMgr.printReceipt();		//启动缓存打印数据
                return;
            }

            //如果有自定义打印事件，则执行，否则执行默认打印
            if($.printMgr && $.printMgr.printEvent){
                $.printMgr.printEvent(inputData);
                return ;
            }
        },

        //设置保存是否打印完成
        setFinishPrinted:function(flag){
            $.printMgr.finishPrinted = flag;
        },

        //获取保存打印完成状态
        getFinishPrinted : function(){
            return $.printMgr.finishPrinted;
        },

        //设置保存打印数据
        setPrintData : function(infos){
            $.printMgr.printData = infos;
        },
        //获取打印数据
        getPrintData : function(){
            return $.printMgr.printData;
        },

        //调打印电子发票开具接口
        printReceipt4KAIJU: function(printInfo,printDoc,channl){
            if(!printDoc || printDoc.length <= 0){
                MessageBox.alert("告警提示", "系统检索不到打印数据！");
                return;
            }

            debugger;

            var userId=null;
            if($.auth && $.auth.getAuthData()){
                var user=$.auth.getAuthData().get("USER_INFO");
                if(user){
                    userId=user.get("USER_ID");
                }
            } else if ($.cssubmit && $.cssubmit.tradeData && $.cssubmit.tradeData.containsKey("USER_ID")) {
                userId=$.cssubmit.tradeData.get("USER_ID");
            }

            var eflag=printInfo.get("EFLAG");
            var postEmail=printInfo.get("POST_EMAIL");

            //PRINT_ID:打印数据源备案ID;
            var param="&ACTION=PRINT_KJ";
            param+="&APPLY_CHANNEL="+channl; //开票发起渠道：：0-营业个人业务;1-集团有ACCTID业务；2-集团无ACCTID业务；3-账务
            if('Y'!=eflag && '6' != $.printMgr.userRecptSvcConf.sendWay){
                param+="&RECEIVER_SENDWAY="+$.printMgr.userRecptSvcConf.sendWay+"&RECEIVER_MOBILE="+$.printMgr.userRecptSvcConf.receiverMobile+"&RECEIVER_EMAIL="+$.printMgr.userRecptSvcConf.receiverEmail;
            }else{
                if('6' == $.printMgr.userRecptSvcConf.sendWay)
                {
                    eflag = 'Y';
                }
                param+="&EFLAG="+eflag+"&POST_EMAIL="+postEmail;
            }
            //param+= "&EPARCHY_CODE="+$.printMgr.params.get("EPARCHY_CODE");
            param+="&USER_ID="+userId;
            param+="&TYPE="+printInfo.get("TYPE")+"&PRINT_ID="+printInfo.get("PRINT_ID")+"&NAME="+printInfo.get("NAME");
            param+="&CUST_TYPE="+printInfo.get("CUST_TYPE");
            var PRINT_DATA_SOURCE_BAK = printDoc.get("PRINT_DATA_SOURCE_BAK");
            if (PRINT_DATA_SOURCE_BAK && PRINT_DATA_SOURCE_BAK.length!=0) {
                PRINT_DATA_SOURCE_BAK.eachKey(function(key, idx, total){
                    param += "&"+key+"="+PRINT_DATA_SOURCE_BAK.get(key);
                });
            }
//			alert("开具打印数据param:"+param);
            $.beginPageLoading("加载开具打印数据。。。");
            ajaxSubmit(null, null, param, $.cssubmit.componentId,
                function(data){
                    $.endPageLoading();
                    if(data){
                        if (data.get("RESULT_CODE")=="0000") {
                            MessageBox.alert("成功提示","加载开具打印数据成功！");
                        }
                    }
                },
                function(code, info, detail){
                    $.endPageLoading();
                    MessageBox.error("错误提示","加载开具打印数据错误！", null, null, info, detail);
                },function(){
                    $.endPageLoading();
                    MessageBox.alert("告警提示","加载开具打印数据超时！");
                });
        },

        //用户的营业电子发票推送信息配置: hadGot(是否已获取)  hadTips(是否已咨询提示)  sendWay(推送方式)  receiverMobile(推送手机)  receiverEmail(推送邮箱)  isERecept(是否电子开具)
        userRecptSvcConf: {hadGot:false, hadTips:false, serviceId:null, sendWay: null, receiverMobile: null, receiverEmail: null, isERecept: null,custType:"PERSON"},

        //获取用户的开具推送信息
        getUserRecptSvcConf: function(func){
            if ($.printMgr.userRecptSvcConf.hadGot == false) {
                var userId=null;
                var eparchyCode = null;
                var tradetypeCode = null;
                var pinfos = $.printMgr.getPrintData();
                if(!pinfos || pinfos.length == 0){
                    MessageBox.alert("告警提示", "系统检索不到打印数据！");
                    return;
                }
                var pinfo = pinfos.get(0);
                if($.auth && $.auth.getAuthData()){
                    var user=$.auth.getAuthData().get("USER_INFO");
                    if(user){
                        userId=user.get("USER_ID");
                        eparchyCode=user.get("EPARCHY_CODE");
                    }
                } else if ($.cssubmit && $.cssubmit.tradeData && $.cssubmit.tradeData.containsKey("USER_ID")) {
                    userId=$.cssubmit.tradeData.get("USER_ID");
                    eparchyCode=$.cssubmit.tradeData.get("EPARCHY_CODE");
                    tradetypeCode=$.cssubmit.tradeData.get("TRADE_TYPE_CODE");
                }else if (pinfo && pinfo.containsKey("USER_ID")) {//重打从print info中取
                    userId = pinfo.get("USER_ID");
                    eparchyCode = pinfo.get("EPARCHY_CODE");
                    tradetypeCode = pinfo.get("TRADE_TYPE_CODE");
                }

                if (!userId || userId==null || userId.length==0) {
                    MessageBox.alert("告警提示","未获取到用户信息！");
                    return ;
                }
                
                if(!tradetypeCode || tradetypeCode==null || tradetypeCode.length==0)
                {
                	return ;
                }

                var param = "&ACTION=GET_ERECEPT_SEND_CONF";
                param += "&USER_ID="+userId;
                param += "&TRADE_TYPE_CODE="+tradetypeCode;

                if (!eparchyCode || eparchyCode.length<2){
                    var dataTmp = null;
                    if($.cssubmit && $.cssubmit.tradeData)
                    {
                        if($.cssubmit.tradeData instanceof $.DatasetList){
                            dataTmp = $.cssubmit.tradeData.get(0);
                        }else if($.cssubmit.tradeData instanceof $.DataMap){
                            dataTmp = $.cssubmit.tradeData;
                        }
                        if(dataTmp.containsKey("DB_SOURCE")){
                            eparchyCode = dataTmp.get("DB_SOURCE");
                        }
                    }
                }

                if (eparchyCode && eparchyCode.length>0) {
                    param += "&EPARCHY_CODE="+eparchyCode;
                }
                $.beginPageLoading("加载用户的营业电子发票推送信息数据。。。");
                ajaxSubmit(null, null, param, $.cssubmit.componentId,
                    function(data){
                        $.endPageLoading();
                        if(data){
                            $.printMgr.userRecptSvcConf.sendWay = data.get("RECEIVER_SENDWAY");
                            $.printMgr.userRecptSvcConf.receiverMobile = data.get("RECEIVER_MOBILE");
                            $.printMgr.userRecptSvcConf.receiverEmail = data.get("RECEIVER_EMAIL");
                            $.printMgr.userRecptSvcConf.hadGot = true;
                            if ($.printMgr.userRecptSvcConf.sendWay) {
                                $.printMgr.userRecptSvcConf.isERecept = "TRUE"; //海南是以用户是否存在日常营业设置来决定是否打电子发票
                            }
                        }
                    },
                    function(code, info, detail){
                        $.endPageLoading();
                        MessageBox.error("错误提示","加载用户的营业电子发票推送信息数据报错！", null, null, info, detail);
                        $.printMgr.userRecptSvcConf.hadGot = true;
                    },/*function(){
								$.endPageLoading();
								MessageBox.alert("告警提示","加载用户的营业电子发票推送信息数据超时！");
								$.printMgr.userRecptSvcConf.hadGot = true;
							}, */{
                        "async" : false  //必须第七个参数
                    });
            }
        },
        //获取集团用户的开具推送信息
        getGrpRecptSvcConf: function(custId,userId){

            if(!custId || custId == null){
                alert("集团客户标识不能为空！");
                return;
            }

            if(!userId || userId == null){
                alert("集团用户为空！无法获取对应的电子发票设置!");
                return;
            }
            var param = "&ACTION=GET_ERECEPT_GRP_CONF";
            param += '&CUST_ID='+custId;
            param += '&USER_ID='+userId;

            $.beginPageLoading("加载用户的营业电子发票推送信息数据。。。");
            ajaxSubmit(null, null, param, $.cssubmit.componentId,
                function(data){
                    $.endPageLoading();
                    if(data){
                        $.printMgr.userRecptSvcConf.sendWay = data.get("RECEIVER_SENDWAY");
                        $.printMgr.userRecptSvcConf.receiverMobile = data.get("RECEIVER_MOBILE");
                        $.printMgr.userRecptSvcConf.receiverEmail = data.get("RECEIVER_EMAIL");
                        $.printMgr.userRecptSvcConf.hadGot = true;
                        $.printMgr.userRecptSvcConf.custType = "GRP";
                        if ($.printMgr.userRecptSvcConf.sendWay) {
                            $.printMgr.userRecptSvcConf.isERecept = "TRUE"; //海南是以用户是否存在日常营业设置来决定是否打电子发票
                        }
                    }
                },
                function(code, info, detail){
                    $.endPageLoading();
                    MessageBox.error("错误提示","加载用户的营业电子发票推送信息数据报错！", null, null, info, detail);
                    $.printMgr.userRecptSvcConf.hadGot = true;
                },/*function(){
							$.endPageLoading();
							MessageBox.alert("告警提示","加载用户的营业电子发票推送信息数据超时！");
							$.printMgr.userRecptSvcConf.hadGot = true;
						}, */{
                    "async" : false  //必须第七个参数
                });
        },

        //启动打印前，咨询打印方式选择:是纸质打印还是电子打印.
        switchBeforeStartupPrint: function(printInfo, printDoc) {
            debugger;
            var printType=printInfo.get("TYPE");

            if(printType && printType.indexOf("01")> -1) {//发票打印:选择纸质打印还是电子打印
//				alert("222$.printMgr.userRecptSvcConf.isERecept:"+$.printMgr.userRecptSvcConf.isERecept);
                if ($.printMgr.userRecptSvcConf.isERecept && $.printMgr.userRecptSvcConf.isERecept!= null && $.printMgr.userRecptSvcConf.isERecept.length>0){//走电子打印接口

                    if($.printMgr.userRecptSvcConf.custType == "GRP"){//政企客户集团电子发票开具
                        var channl = "1";
                        $.printMgr.printReceipt4KAIJU(printInfo,printDoc,channl);
                    }
                    else{//个人客户电子发票开具
                        var channl = "0";
                        $.printMgr.printReceipt4KAIJU(printInfo,printDoc,channl);
                    }

                } else { //咨询是否电子打印还是纸质打印？？已经咨询过，则无需重复咨询。  //如果未配置，可能是用户默认纸质打印，也有可能是未配置。//这里不做咨询，默认为纸质打印
                    $.printMgr.startupPrint(printDoc);
                }
            } else {//非发票打印，走原逻辑
                $.printMgr.startupPrint(printDoc);
            }
        },

        //打印免填单
        printReceipt: function(){
            //start 同步 获取用户的营业电子发票推送信息-------------------------------------------------------------------------------------------
            //FIXME :在遍历打印前获取用户的营业电子发票推送信息,防止后面的代码判断条件不对。
            //FIXME :打印时才获取用户电子推送信息，是为了实时获取“业务受理后用户才设置的推送信息”，如开户后才会设置推送信息。

            //个人客户获取电子发票推送信息
            var infos = $.printMgr.getPrintData();
//			alert("printReceipt_infos="+infos);
            var eflag=infos.get(0).get("EFLAG");//跨区补卡无三户资料特殊处理
            if(eflag=='Y'){
//				alert("无三户资料特殊业务~");
                $.printMgr.userRecptSvcConf.isERecept="TRUE";
            }else{
//				alert("普通业务~");
                if("PERSON"==$.printMgr.userRecptSvcConf.custType){
                    $.printMgr.getUserRecptSvcConf();
                }else{
                    var custId=infos.get(0).get("CUST_ID");
                    var userId=infos.get(0).get("USER_ID");
                    $.printMgr.getGrpRecptSvcConf(custId,userId);
                }

            }

            //end   同步 获取用户的营业电子发票推送信息-------------------------------------------------------------------------------------------

            if(!infos || infos.length == 0){
                MessageBox.alert("告警提示", "系统检索不到打印数据！");
                return;
            }

            infos.each(function(info, idx, total){
                $.printMgr.printIndex = idx;		//记录当前打印类型

                //判断是否已经打印，如果已经打印，则不打印
                if(info.containsKey("PRINTED") && info.get("PRINTED")=="1"){
                    return true;
                }

                //当前已经确认打印或问询取消则打印下一条
                if(info.containsKey("PRT_FLAG") && info.get("PRT_FLAG") == "1"){
                    return true;
                }
                $.printMgr.setPrintInfoFlag($.printMgr.printIndex);		//设置打印标识

                var printType = info.get("TYPE");

                if(printType.indexOf("01")>-1 || printType.indexOf("02")>-1){
                    if(info.containsKey("HAS_TICKET") && info.get("HAS_TICKET")==1)
                    {
                        var feeMode=info.get("FEE_MODE");
                        var feeTypeCodes=info.get("FEE_TYPE_CODES", "");		//数据结构=|2324|1110|345|
                        if(feeMode=="0" && feeTypeCodes.indexOf("|61|")>-1){
                            MessageBox.alert("告警提示", "买断、虚拟供货终端销售，无法打印购机款发票，请另行给客户提供发票！");
                            return false;
                        }
                        //如果是存在电子发票开具配置，则直接走电子开具，无需走单联票；否则，走原始的单联票.
//						alert("$.printMgr.userRecptSvcConf.isERecept="+$.printMgr.userRecptSvcConf.isERecept);
                        if (printType.indexOf("01")>-1 && $.printMgr.userRecptSvcConf.isERecept && $.printMgr.userRecptSvcConf.isERecept.length >0) {
                            info.put("NAME","电子发票");
                            $.printMgr.printWithTip(info);
//							alert("电子票");
                        } else {
//							alert("单联票");
                            //获取员工单联票据
                            $.staffNote.getPrintTicketInfo(info);
                        }
                    }else{
                        $.printMgr.printWithTip(info);
                    }
                }else{
                    $.printMgr.printWithTip(info, true);
                }
                return false;
            });
            /*if($.cssubmit.hasPrintAll()){
                MessageBox.alert("提示","本次业务票据已经打印完毕！点击确定后刷新页面！",function(btn){
                    if(btn=="ok")
                    {
                        $.cssubmit.closeMessage(true);
                    }
                });
            }*/
        },
        getPrintInfoByIndex : function(idx){
            if(idx<0) return;
            var infos = $.printMgr.getPrintData();
            return infos.get(idx);
        },

        //更新打印标识
        setPrintInfoFlag : function(idx){
            if(idx<0) return;
            var infos = $.printMgr.getPrintData();
            var info = infos.get(idx);
            info.put("PRT_FLAG", "1");
        },
        //重置打印标识
        resetPrintFlag:function(){
            var printDatas = $.printMgr.getPrintData();
            if(!printDatas || !printDatas.length){
                return;
            }
            printDatas.each(function(item){
                item.put("PRT_FLAG", "0");
            });
        },
        //正常问询打印
        printWithTip:function(printInfo, updateTag){
//			alert("printWithTip_printInfo:"+printInfo);
            if(printInfo){
                /*MessageBox.confirm("确认提示", "是否要打印"+printInfo.get("NAME")+"?",
                    function(btn){
                        if(btn == "ok"){
                            var printDoc = printInfo.get("PRINT_DATA");
                            $.printMgr.switchBeforeStartupPrint(printInfo,printDoc);
                            if(updateTag){
                                $.printMgr.updataPrintTag();
                            }

                            if((printInfo.get("TYPE")).indexOf("03")==-1){
                                printInfo.put("PRINTED", "1");			//已经打印了
                            }
                            else{
                                printInfo.put("PRINTED_DOC", "1");
                            }


                        }
                        $.printMgr.printReceipt();
                });*/
                MessageBox.confirm("提示","即将打印"+printInfo.get("NAME")+"，请做好准备！",function(btn){
                    if(btn=="ok")
                    {
                        var printDoc = printInfo.get("PRINT_DATA");
                        $.printMgr.switchBeforeStartupPrint(printInfo,printDoc);
                        if(updateTag){
                            $.printMgr.updataPrintTag();
                        }
                        if((printInfo.get("TYPE")).indexOf("03")==-1){
                            printInfo.put("PRINTED", "1");			//已经打印了
                        }
                        else{
                            printInfo.put("PRINTED_DOC", "1");
                        }
                        $.printMgr.printReceipt();
                    }
                });
            }
        },

        //单联票据打印逻辑
        printTicket:function(ticketData){
            //如果取消则继续打印后面的免填单
            var printInfo = $.printMgr.getPrintInfoByIndex($.printMgr.printIndex);
            var printDoc = printInfo.get("PRINT_DATA");
            printInfo.put("PRINTED", "1");				//更新已经打印标记
            if(ticketData) {
                //单联票据拼接
                printDoc.put("TAX_NO", ticketData.get("TAX_NO", ""));
                printDoc.put("SECURITY_CODE", ticketData.get("SECURITY_CODE", ""));
                printDoc.put("TICKET_ID", ticketData.get("TICKET_ID", ""));
                if(ticketData.containsKey("EWM")){
                    printDoc.put("EWM", "<img width=\"100\" height=\"100\" src=\"data:image/png;base64,"+ticketData.get("EWM")+"\" border=\"0\" />");
                }
            }
            $.printMgr.startupPrint(printDoc);
            printDoc = null;
            printInfo = null;
            $.printMgr.printReceipt();
        },
        /**
         * 设置电子工单打印数据
         */
        setElcNoteData:function(notePrintData){
            var receiptData=null;
            ($.printMgr.getPrintData()).each(function(info, idx, total){
                if((info.get("TYPE")).indexOf("03")>-1){
                    receiptData=info.get("PRINT_DATA");
                    return false;
                }
            });

            //涉及到融合业务，整体打印信息需要将各业务合并，所以从打印数据中取业务受理内容
            if(receiptData && receiptData.length){
                for(var i=1; i<=5; i++){
                    notePrintData.put("RECEIPT_INFO"+i, (""+receiptData.get("RECEIPT_INFO"+i, "")).replace(/&nbsp;/g," ").replace(/<br\/>/g,"~~"));
                }
            }
            $.printMgr.elcNoteData=notePrintData;
        },
        /***
         * 获取电子工单数据
         */
        loadElecAcceptBill:function(tradeData){
            $.printMgr.resetPrintFlag();	//重置打印轮询标记

            //如果存在电子工单数据，则直接打印
            if($.printMgr.elcNoteData && $.printMgr.elcNoteData.length){
                $.printMgr.getElecAcceptBill($.printMgr.elcNoteData);
                return;
            }
        },

        getElecAcceptBill:function(notePrintData){
            //继续打印其它免填单
            ($.printMgr.getPrintData()).each(function(info, idx, total){
                if((info.get("TYPE")).indexOf("03")>-1){
                    info.put("PRT_FLAG", "1");		//设置已打印标记
                    info.put("PRINTED_DOC","2");
                    return false;
                }
            });

            var edocSecond = $("#CSSUBMIT_BUTTON").attr("edocSecond")=="true"? true : false;
            if(edocSecond){
                if($.printMgr.edocPrintLock){
                    MessageBox.alert("告警提示","正在送电子工单打印中，请耐心等待！");
                    return;
                }
                $.printMgr.edocPrintLock=true;		//设置打印锁
                $.beginPageLoading("正在打印电子工单。。。");
                //$.printMgr.printNewElecAcceptBill(notePrintData);
                if($.os.phone) {
                    window.setTimeout($.printMgr.printNewPhoneElecAcceptBill, 9);
                }
                else {
                    window.setTimeout($.printMgr.printNewElecAcceptBill, 9);
                }
            }else{
                $.printMgr.printElecAcceptBill(notePrintData);
            }

            //更新打印标记
            //$.printMgr.updataPrintTag(true);

            //继续打印票据
            $.printMgr.printReceipt();
        },

        //电子工单
        printElecAcceptBill:function(notePrintData){
            var action="http://10.199.48.97:8089/general/aip_file/seal/main.php";
            if($.printMgr.eDocServUrl){
                action=$.printMgr.eDocServUrl;
            }
            var str=[];
            str.push('<form id="NotePrintForm" name="NotePrintForm" action="'+action+'" method="post" target="_blank">');
            notePrintData.eachKey(function(key,index,totalcount){
                str.push('<input type="hidden" name="'+key+'" value="'+notePrintData.get(key)+'" />');
            });
            str.push('</form>');
            if($("#NotePrintForm") && $("#NotePrintForm").length){
                $("#NotePrintForm").remove();
            }
            $(document.body).append(str.join(""));
            $("#NotePrintForm").trigger("submit");
        },
        //新版电子工单
        printNewElecAcceptBill:function(notePrintData){
            /**if(!$("#EdocPrintOcx") || !$("#EdocPrintOcx").length){
				$(document.body).append('<object id="EdocPrintOcx" classid="clsid:5144a5b7-b206-4e79-8ee2-9df3973d6527" width="0" height="0"></object>');
			}**/
            if($.printMgr.edocPrintOcx == null){
                try{
                    //$.printMgr.edocPrintOcx = new ActiveXObject("com.neusoft.mid.mpls.ebill.bussiness.MakeBillActiveX");
                    var ocx = document.createElement("object");
                    ocx.setAttribute("id", "ocx1");
                    ocx.setAttribute("height", 0);
                    ocx.setAttribute("width", 0);
                    ocx.setAttribute("classid", "clsid:5144a5b7-b206-4e79-8ee2-9df3973d6527");
                    $.printMgr.edocPrintOcx = ocx ;
                }catch(e){
                    alert("加载电子工单打印控件错误，请确认是否已经正确安装控件!");
                    $.printMgr.edocPrintLock=false;
                    $.endPageLoading();
                    return;
                }
            }

            if(!notePrintData || !notePrintData.length){
                notePrintData = $.printMgr.elcNoteData;
            }

            var otherTradeList = notePrintData.get("OTHER_TRADE_LIST","");//LIST对象
            var tradetypecode = notePrintData.get("TRADE_TYPE_CODE", "");
            //品牌
            var brandCode= notePrintData.get("BRAND_CODE", "");
            /**
             * REQ201705270006_关于人像比对业务优化需求
             * <br/>
             * 新增业务类型：
             *   1.用户密码重置 73
             *   2.换卡(补卡) 142
             *   3.复机 310
             *	 4.有价卡销售  416
             *   5.集团成员个人开户  3008
             * @author zhuoyingzhi
             * @date 20170704
             */

            if('10' == tradetypecode || '100'== tradetypecode || '60'== tradetypecode || '40'== tradetypecode
                || '73'== tradetypecode || '142'== tradetypecode|| '310'== tradetypecode || '416'== tradetypecode
                || ('3008' == tradetypecode && 'IMSG' == brandCode))
            {


                var params = "&ACTION=GET_PIC_TAG";
                params += "&TRADE_ID="+notePrintData.get("TRADE_ID", "");
                params += "&USER_ID="+notePrintData.get("USER_ID", "");
                params += "&TRADE_TYPE_CODE="+tradetypecode;
                params += "&SERIAL_NUMBER="+notePrintData.get("SERIAL_NUMBER", "");
                ajaxSubmit(null, null, params, $.cssubmit.componentId, function(data)
                {
                    //notePrintData.put("PIC_TAG",data.get("PIC_TAG","1"));
                    notePrintData.put("FORM_PIC_TAG",data.get("FORM_PIC_TAG","1"));
                    notePrintData.put("AGENT_PIC_TAG",data.get("AGENT_PIC_TAG","1"));
                    if(data.get("PIC_TAG","") == '0'){
                        //客户
                        notePrintData.put("PIC_TAG",'0');
                    }else{
                        if(data.get("AGENT_PIC_TAG","") == '0'){
                            //经办人
                            notePrintData.put("PIC_TAG",'0');
                        }else{
                            if(data.get("FORM_PIC_TAG","1") == '0'){
                                //原客户
                                notePrintData.put("PIC_TAG",'0');
                            }else{
                                notePrintData.put("PIC_TAG",'1');
                            }
                        }
                    }

                }, function(error_code, error_info) {
                },{
                    "async" : false
                });
            }

            /**
             * REQ201805090016_在线公司电子稽核报表优化需求
             * <br/>
             * 判断是否是主产品变更
             * @author zhuoyingzhi
             * @date 20180518
             */
            var isMainProduct='1';
            var saleActionName='';
            if('110' == tradetypecode||'240' == tradetypecode){
                var params110 = "&ACTION=IS_MAIN_PRODUCT_CHANGE_OR_SALE_NAME";
                params110 += "&TRADE_ID="+notePrintData.get("TRADE_ID", "");
                params110 += "&USER_ID="+notePrintData.get("USER_ID", "");
                params110 += "&TRADE_TYPE_CODE="+tradetypecode;
                params110 += "&SERIAL_NUMBER="+notePrintData.get("SERIAL_NUMBER", "");
                ajaxSubmit(null, null, params110, $.cssubmit.componentId, function(data)
                {
                    if('110' == tradetypecode){
                        isMainProduct=data.get("IS_MAIN_PRODUCT","1");
                        //产品名称
                        notePrintData.put("PRODUCT_NAME",data.get("PRODUCT_NAME",""));
                    }
                    if('240' == tradetypecode){
                        saleActionName=data.get("SALE_ACTION_NAME","");
                    }
                }, function(error_code, error_info) {
                },{
                    "async" : false
                });
            }

            var isHyyykBatChopen="0";
            var paramHyyykbatchopen="&ACTION=IS_HYYYK_BAT_CHOPEN";
            paramHyyykbatchopen +="&USER_ID="+notePrintData.get("USER_ID", "");

            if('2016' == tradetypecode)
            {
            	ajaxSubmit(null, null, paramHyyykbatchopen, null, function(data)
                        {
                            isHyyykBatChopen=data.get("IS_HYYYK_BAT_CHOPEN","0");
                        }, function(error_code, error_info) {
                        },{
                            "async" : false
                        });
            }else
            {
            	ajaxSubmit(null, null, paramHyyykbatchopen, $.cssubmit.componentId, function(data)
                        {
                            isHyyykBatChopen=data.get("IS_HYYYK_BAT_CHOPEN","0");
                        }, function(error_code, error_info) {
                        },{
                            "async" : false
                        });
            }
            
            /*********************REQ201805090016_在线公司电子稽核报表优化需求_end*****************************************/

            var detail=notePrintData.get("RECEIPT_INFO1","");
            detail+="##"+notePrintData.get("RECEIPT_INFO2","");
            detail+="##"+notePrintData.get("RECEIPT_INFO3","");
            detail+="##"+notePrintData.get("RECEIPT_INFO4","");
            detail+="##"+notePrintData.get("RECEIPT_INFO5","");
            detail+="##"+notePrintData.get("NOTICE_CONTENT","");

            var edocXml=[];
            edocXml.push('<?xml version="1.0" encoding="utf-8" ?>');
            edocXml.push('<IN>');
            edocXml.push('	<billid>'+notePrintData.get("TRADE_ID", "")+'</billid>');
            edocXml.push('	<brand_name>'+notePrintData.get("BRAND_NAME", "")+'</brand_name>');
            edocXml.push('	<brand_code>'+notePrintData.get("BRAND_CODE", "")+'</brand_code>');
            edocXml.push('	<work_name>'+notePrintData.get("TRADE_STAFF_NAME", "")+'</work_name>');
            edocXml.push('	<work_no>'+notePrintData.get("TRADE_STAFF_ID", "")+'</work_no>');
            edocXml.push('	<org_info>'+notePrintData.get("ORG_INFO", "")+'</org_info>');
            edocXml.push('	<org_name>'+notePrintData.get("ORG_NAME", "")+'</org_name>');
            edocXml.push('	<phone>'+notePrintData.get("SERIAL_NUMBER", "")+'</phone>');
            edocXml.push('	<serv_id>'+notePrintData.get("USER_ID", "")+'</serv_id>');
            edocXml.push('	<op_time>'+notePrintData.get("ACCEPT_DATE", "")+'</op_time>');

            /*获取照片标志信息*/
            edocXml.push('	<pic_tag>'+notePrintData.get("PIC_TAG", "")+'</pic_tag>');
            edocXml.push('	<formpic_tag>'+notePrintData.get("FORM_PIC_TAG", "")+'</formpic_tag>');
            edocXml.push('	<agentpic_tag>'+notePrintData.get("AGENT_PIC_TAG", "")+'</agentpic_tag>');

            edocXml.push('	<busi_list>');
            edocXml.push('		<busi_info>');
            edocXml.push('			<op_code>'+notePrintData.get("TRADE_TYPE_CODE", "")+'</op_code>');
            /**
             * REQ201805170033_在线公司电子稽核报表优化需求
             * <br/>
             *  添加 业务名称  op_name
             * @author zhuoyingzhi
             * @date 20180614
             */
            edocXml.push('			<op_name>'+notePrintData.get("TRADE_TYPE", "")+'</op_name>');

            edocXml.push('			<sys_accept>'+notePrintData.get("TRADE_ID", "")+'</sys_accept>');
            edocXml.push('			<busi_detail>'+detail+'</busi_detail>');
            edocXml.push('		</busi_info>');

            //本次修改
            for ( var i = 0; i < otherTradeList.length; i++) {
                var detailother=otherTradeList.get(i,"RECEIPT_INFO1","");
                detailother+="##"+otherTradeList.get(i,"RECEIPT_INFO2","");
                detailother+="##"+otherTradeList.get(i,"RECEIPT_INFO3","");
                detailother+="##"+otherTradeList.get(i,"RECEIPT_INFO4","");
                detailother+="##"+otherTradeList.get(i,"RECEIPT_INFO5","");
                detailother+="##"+otherTradeList.get(i,"NOTICE_CONTENT","");

                edocXml.push('		<busi_info>');
                edocXml.push('			<op_code>'+otherTradeList.get(i,"TRADE_TYPE_CODE", "")+'</op_code>');
                edocXml.push('			<op_name>'+otherTradeList.get(i,"TRADE_TYPE", "")+'</op_name>');
                edocXml.push('			<sys_accept>'+otherTradeList.get(i,"TRADE_ID", "")+'</sys_accept>');
                edocXml.push('			<busi_detail>'+detailother+'</busi_detail>');
                edocXml.push('		</busi_info>');
            }
            //本次修改
            edocXml.push('	</busi_list>');

            edocXml.push('	<verify_mode>'+notePrintData.get("VERIFY_MODE", "")+'</verify_mode>');
            edocXml.push('	<id_card>'+notePrintData.get("ID_CARD", "")+'</id_card>');
            edocXml.push('	<cust_name>'+notePrintData.get("CUST_NAME", "")+'</cust_name>');
            edocXml.push('	<copy_flag></copy_flag>');
            edocXml.push('	<agm_flag></agm_flag>');
            /**
             * REQ201410230014电子化存储系统优化（一）
             * 2015-04-15 chenxy3
             * 获取用户的是待激活用户，则给予保存表TF_B_TRADE_CNOTE_INFO新字段RSRV_STR1，用于提示
             * 显示电子协议
             * */
            var eprintFlag=notePrintData.get("RSRV_STR1","");
            if(notePrintData.get("TRADE_TYPE_CODE", "")=="60"&&eprintFlag!=""){
                edocXml.push('	<eprint_flag>'+notePrintData.get("RSRV_STR1","")+'</eprint_flag>');
            }

            /**
             * REQ201805090016_在线公司电子稽核报表优化需求
             * @author zhuoyingzhi
             * @date 20180518
             */
            if('240'== tradetypecode){
                //营销包名称
                edocXml.push('	<marketing_info>'+saleActionName+'</marketing_info>');
            }
            if('110'== tradetypecode && isMainProduct=='0'){
                //套餐变更名称
                edocXml.push('	<mealChange_name>'+notePrintData.get("PRODUCT_NAME", "")+'</mealChange_name>');
            }

            //0表示否   1表示是
            if('1' == isHyyykBatChopen){
                //是行业应用卡
                edocXml.push('	<trade_card>'+1+'</trade_card>');
            }else{
                //非行业应用卡
                edocXml.push('	<trade_card>'+0+'</trade_card>');
            }
            /*******************REQ201805090016_在线公司电子稽核报表优化需求_end***********************************/
            //商品订购
            edocXml.push('	<batch_number>'+notePrintData.get("ORDER_ID")+'</batch_number>');
            
            edocXml.push('</IN>');

            var edocStr = edocXml.join("");
            debugger;
            if($.printMgr.edocPrintOcx && edocStr){
                $.printMgr.edocPrintOcx.MainBuildBill(edocStr);
            }
            edocStr=null;
            detail=null;
            edocXml=null;
            $.printMgr.edocPrintLock=false;		//关闭打印锁
            $.endPageLoading();
        },
        //手机端电子工单打印
        printNewPhoneElecAcceptBill:function(notePrintData){
            if(!notePrintData || !notePrintData.length){
                notePrintData = $.printMgr.elcNoteData;
            }
            var otherTradeList = notePrintData.get("OTHER_TRADE_LIST","");//LIST对象
            var tradetypecode = notePrintData.get("TRADE_TYPE_CODE", "");
            //品牌
            var brandCode= notePrintData.get("BRAND_CODE", "");
            /**
             * REQ201705270006_关于人像比对业务优化需求
             * <br/>
             * 新增业务类型：
             *   1.用户密码重置 73
             *   2.换卡(补卡) 142
             *   3.复机 310
             *
             *   5.集团成员个人开户  3008
             * @author zhuoyingzhi
             * @date 20170704
             */

            if('10' == tradetypecode || '100'== tradetypecode || '60'== tradetypecode || '40'== tradetypecode
                || '73'== tradetypecode || '142'== tradetypecode|| '310'== tradetypecode
                || ('3008' == tradetypecode && 'IMSG' == brandCode))
            {


                var params = "&ACTION=GET_PIC_TAG";
                params += "&TRADE_ID="+notePrintData.get("TRADE_ID", "");
                params += "&USER_ID="+notePrintData.get("USER_ID", "");
                params += "&TRADE_TYPE_CODE="+tradetypecode;
                params += "&SERIAL_NUMBER="+notePrintData.get("SERIAL_NUMBER", "");
                ajaxSubmit(null, null, params, $.cssubmit.componentId, function(data)
                {
                    //notePrintData.put("PIC_TAG",data.get("PIC_TAG","1"));
                    notePrintData.put("FORM_PIC_TAG",data.get("FORM_PIC_TAG","1"));
                    notePrintData.put("AGENT_PIC_TAG",data.get("AGENT_PIC_TAG","1"));
                    if(data.get("PIC_TAG","") == '0'){
                        //客户
                        notePrintData.put("PIC_TAG",'0');
                    }else{
                        if(data.get("AGENT_PIC_TAG","") == '0'){
                            //经办人
                            notePrintData.put("PIC_TAG",'0');
                        }else{
                            if(data.get("FORM_PIC_TAG","1") == '0'){
                                //原客户
                                notePrintData.put("PIC_TAG",'0');
                            }else{
                                notePrintData.put("PIC_TAG",'1');
                            }
                        }
                    }

                }, function(error_code, error_info) {
                },{
                    "async" : false
                });
            }

            /**
             * REQ201805090016_在线公司电子稽核报表优化需求
             * <br/>
             * 判断是否是主产品变更
             * @author zhuoyingzhi
             * @date 20180518
             */
            var isMainProduct='1';
            var saleActionName='';
            if('110' == tradetypecode||'240' == tradetypecode){
                var params110 = "&ACTION=IS_MAIN_PRODUCT_CHANGE_OR_SALE_NAME";
                params110 += "&TRADE_ID="+notePrintData.get("TRADE_ID", "");
                params110 += "&USER_ID="+notePrintData.get("USER_ID", "");
                params110 += "&TRADE_TYPE_CODE="+tradetypecode;
                params110 += "&SERIAL_NUMBER="+notePrintData.get("SERIAL_NUMBER", "");
                ajaxSubmit(null, null, params110, $.cssubmit.componentId, function(data)
                {
                    if('110' == tradetypecode){
                        isMainProduct=data.get("IS_MAIN_PRODUCT","1");
                        //产品名称
                        notePrintData.put("PRODUCT_NAME",data.get("PRODUCT_NAME",""));
                    }
                    if('240' == tradetypecode){
                        saleActionName=data.get("SALE_ACTION_NAME","");
                    }
                }, function(error_code, error_info) {
                },{
                    "async" : false
                });
            }

            var isHyyykBatChopen="0";
            var paramHyyykbatchopen="&ACTION=IS_HYYYK_BAT_CHOPEN";
            paramHyyykbatchopen +="&USER_ID="+notePrintData.get("USER_ID", "");

            ajaxSubmit(null, null, paramHyyykbatchopen, $.cssubmit.componentId, function(data)
            {
                isHyyykBatChopen=data.get("IS_HYYYK_BAT_CHOPEN","0");
            }, function(error_code, error_info) {
            },{
                "async" : false
            });
            /*********************REQ201805090016_在线公司电子稽核报表优化需求_end*****************************************/

            var detail=notePrintData.get("RECEIPT_INFO1","");
            detail+="##"+notePrintData.get("RECEIPT_INFO2","");
            detail+="##"+notePrintData.get("RECEIPT_INFO3","");
            detail+="##"+notePrintData.get("RECEIPT_INFO4","");
            detail+="##"+notePrintData.get("RECEIPT_INFO5","");
            detail+="##"+notePrintData.get("NOTICE_CONTENT","");

            var edocXml=[];
            edocXml.push('<?xml version="1.0" encoding="utf-8" ?>');
            edocXml.push('<IN>');
            edocXml.push('	<billid>'+notePrintData.get("TRADE_ID", "")+'</billid>');
            edocXml.push('	<brand_name>'+notePrintData.get("BRAND_NAME", "")+'</brand_name>');
            edocXml.push('	<brand_code>'+notePrintData.get("BRAND_CODE", "")+'</brand_code>');
            edocXml.push('	<work_name>'+notePrintData.get("TRADE_STAFF_NAME", "")+'</work_name>');
            edocXml.push('	<work_no>'+notePrintData.get("TRADE_STAFF_ID", "")+'</work_no>');
            edocXml.push('	<org_info>'+notePrintData.get("ORG_INFO", "")+'</org_info>');
            edocXml.push('	<org_name>'+notePrintData.get("ORG_NAME", "")+'</org_name>');
            edocXml.push('	<phone>'+notePrintData.get("SERIAL_NUMBER", "")+'</phone>');
            edocXml.push('	<serv_id>'+notePrintData.get("USER_ID", "")+'</serv_id>');
            edocXml.push('	<op_time>'+notePrintData.get("ACCEPT_DATE", "")+'</op_time>');

            /*获取照片标志信息*/
            edocXml.push('	<pic_tag>'+notePrintData.get("PIC_TAG", "")+'</pic_tag>');
            edocXml.push('	<formpic_tag>'+notePrintData.get("FORM_PIC_TAG", "")+'</formpic_tag>');
            edocXml.push('	<agentpic_tag>'+notePrintData.get("AGENT_PIC_TAG", "")+'</agentpic_tag>');

            edocXml.push('	<busi_list>');
            edocXml.push('		<busi_info>');
            edocXml.push('			<op_code>'+notePrintData.get("TRADE_TYPE_CODE", "")+'</op_code>');
            /**
             * REQ201805170033_在线公司电子稽核报表优化需求
             * <br/>
             *  添加 业务名称  op_name
             * @author zhuoyingzhi
             * @date 20180614
             */
            edocXml.push('			<op_name>'+notePrintData.get("TRADE_TYPE", "")+'</op_name>');

            edocXml.push('			<sys_accept>'+notePrintData.get("TRADE_ID", "")+'</sys_accept>');
            edocXml.push('			<busi_detail>'+detail+'</busi_detail>');
            edocXml.push('		</busi_info>');

            //本次修改
            for ( var i = 0; i < otherTradeList.length; i++) {
                var detailother=otherTradeList.get(i,"RECEIPT_INFO1","");
                detailother+="##"+otherTradeList.get(i,"RECEIPT_INFO2","");
                detailother+="##"+otherTradeList.get(i,"RECEIPT_INFO3","");
                detailother+="##"+otherTradeList.get(i,"RECEIPT_INFO4","");
                detailother+="##"+otherTradeList.get(i,"RECEIPT_INFO5","");
                detailother+="##"+otherTradeList.get(i,"NOTICE_CONTENT","");

                edocXml.push('		<busi_info>');
                edocXml.push('			<op_code>'+otherTradeList.get(i,"TRADE_TYPE_CODE", "")+'</op_code>');
                edocXml.push('			<sys_accept>'+otherTradeList.get(i,"TRADE_ID", "")+'</sys_accept>');
                edocXml.push('			<busi_detail>'+detailother+'</busi_detail>');
                edocXml.push('		</busi_info>');
            }
            //本次修改
            edocXml.push('	</busi_list>');

            edocXml.push('	<verify_mode>'+notePrintData.get("VERIFY_MODE", "")+'</verify_mode>');
            edocXml.push('	<id_card>'+notePrintData.get("ID_CARD", "")+'</id_card>');
            edocXml.push('	<cust_name>'+notePrintData.get("CUST_NAME", "")+'</cust_name>');
            edocXml.push('	<copy_flag></copy_flag>');
            edocXml.push('	<agm_flag></agm_flag>');
            /**
             * REQ201410230014电子化存储系统优化（一）
             * 2015-04-15 chenxy3
             * 获取用户的是待激活用户，则给予保存表TF_B_TRADE_CNOTE_INFO新字段RSRV_STR1，用于提示
             * 显示电子协议
             * */
            var eprintFlag=notePrintData.get("RSRV_STR1","");
            if(notePrintData.get("TRADE_TYPE_CODE", "")=="60"&&eprintFlag!=""){
                edocXml.push('	<eprint_flag>'+notePrintData.get("RSRV_STR1","")+'</eprint_flag>');
            }

            /**
             * REQ201805090016_在线公司电子稽核报表优化需求
             * @author zhuoyingzhi
             * @date 20180518
             */
            if('240'== tradetypecode){
                //营销包名称
                edocXml.push('	<marketing_info>'+saleActionName+'</marketing_info>');
            }
            if('110'== tradetypecode && isMainProduct=='0'){
                //套餐变更名称
                edocXml.push('	<mealChange_name>'+notePrintData.get("PRODUCT_NAME", "")+'</mealChange_name>');
            }

            //0表示否   1表示是
            if('1' == isHyyykBatChopen){
                //是行业应用卡
                edocXml.push('	<trade_card>'+1+'</trade_card>');
            }else{
                //非行业应用卡
                edocXml.push('	<trade_card>'+0+'</trade_card>');
            }
            /*******************REQ201805090016_在线公司电子稽核报表优化需求_end***********************************/
            edocXml.push('</IN>');

            var edocStr = edocXml.join("");
            //todo  手机端打印调试
            // needCertificatePhoto 是否需要二代证拍照 true、false(必填)
            // needPersonPhoto 是否需要人像照拍照 true、false(必填)
            // certificatePhotoTip  填写实际使用人或着经办人(必填)
            // personPhotoTip 填写实际使用人或着经办人(必填)
            // needDeleteFile 电子签名上传完图片是否删除true、false(必填)
            // signContent 受理xml内容(必填)
            // headImageName 身份证正面照图片客户端存储路径(选填takePhoto回调返回的图片路径filepath)
            // emblemImageName 身份证反面照图片客户端存储路径(选填takePhoto返回的图片路径filepath)
            // peopleImageName 人像照图片客户端存储路径(选填takePhoto返回的图片路径filepath)
            // callback回调函数(必填)
            // MessageBox.alert(edocStr);
            var needPersonPhoto;
            var certificatePhotoTip;
            var personPhotoTip;
            var peopleImageName;
            if($.MBOP.shotImageSuccess) {
                needPersonPhoto = $.MBOP.shotImageSuccess == "1" ? false:true;
                certificatePhotoTip = $.MBOP.shotImageAgent == "0"?"实际使用人":"经办人";
                personPhotoTip = $.MBOP.shotImageAgent == "0"?"实际使用人":"经办人";;
                peopleImageName = $.MBOP.shotImagePath;
            }
            else {
                needPersonPhoto = true;
                certificatePhotoTip = "实际使用人";
                personPhotoTip = "实际使用人";
                peopleImageName =  "";
            }
            var params={
                "signContent": edocStr   ,
                "needCertificatePhoto":false,//是否需要二代证拍照 true、false(必填)
                "needPersonPhoto": false,//是否需要人像照拍照 true、false(必填)
                "certificatePhotoTip":certificatePhotoTip,//(二代)填写实际使用人或着经办人(必填)
                "personPhotoTip":personPhotoTip,//(人像)填写实际使用人或着经办人(必填)
                "needDeleteFile":false,//电子签名上传完图片是否删除true、false(必填)
                // "headImageName ": "",//身份证正面照图片客户端存储路径(选填takePhoto回调返回的图片路径filepath)
                // "emblemImageName ": "",//身份证反面照图片客户端存储路径(选填takePhoto返回的图片路径filepath)
                "peopleImageName":peopleImageName,//人像照图片客户端存储路径(选填takePhoto返回的图片路径filepath)
                "callback":"$.printMgr.authRealnameProcessCallback"
            };
            var ParamMbopStr = JSON.stringify(params);
            window.MBOP.authRealnameProcess(ParamMbopStr);
            edocStr=null;
            detail=null;
            edocXml=null;
            $.printMgr.edocPrintLock=false;		//关闭打印锁
            $.endPageLoading();
        },
        //手机端回调，函数
        authRealnameProcessCallback:function(retStr) {
                var ret = new $.DataMap(retStr.toString());
                if(ret.get("exeResult") == "0") {
                    alert("业务受理流程完毕！");
                    $.printMgr.updataPrintTag();
                    $.cssubmit.closeMessage(true,true);
                }
                else {
                    alert(ret);
                    $.cssubmit.closeMessage(true);
                }
        },
        //更新打印标记
        updataPrintTag:function(edocPrint) {
            var params = "&ACTION=UPD_PRINT_TAG";
            var eparchyCode;
            //融合业务涉及多笔打印合并，更新打印标记根据订单编号批量处理
            if($.printMgr.params.containsKey("ORDER_ID")){
                params += "&ORDER_ID="+$.printMgr.params.get("ORDER_ID");
                eparchyCode=$.printMgr.params.get("EPARCHY_CODE");
            }else{
                var idx=$.printMgr.printIndex;
                var infos=$.printMgr.getPrintData();
                if(!idx) idx = infos.length-1;
                params += "&TRADE_ID="+(infos.get(idx)).get("TRADE_ID");
                eparchyCode=(infos.get(idx)).get("EPARCHY_CODE");
            }
            if(eparchyCode)	params += "&EPARCHY_CODE="+eparchyCode;
            if(edocPrint) params += "&EDOC_PRINT=true";

            ajaxSubmit(null, null, params, $.cssubmit.componentId,
                function(data){
                    //if(!edocPrint && data && data.get("PRT_TAG_RESULT"))
                    //MessageBox.success("信息提示","打印完毕!");
                },
                function(code, info, detail){
                    $.endPageLoading();
                    MessageBox.error("错误提示","更新打印标记报错！",null, null, info, detail);
                });
        },

        //启动打印
        startupPrint : function(data){
            if(!data) return;

            try{
                if (data.get("PRINT_DATA_SOURCE_BAK")) {//如果存在PRINT_DATA_SOURCE_BAK，则删除。
                    data.removeKey("PRINT_DATA_SOURCE_BAK");
                }
            }catch(e){alert("报错了");}

            //第一次进来时候，TEMP_PATH只是一个相对地址，需要加上前缀，组成全路径
            if((data.get("TEMP_PATH")).indexOf("http")== -1){
                //获取文档打印url地址
                var href = window.location.href;
                var idx = href.lastIndexOf("/?");
                if(idx == -1){
                    href = href.substring(0, href.indexOf("?"));
                    idx = href.lastIndexOf("/");
                }
                href = href.substring(0, idx);
                data.put("TEMP_PATH", href+"/"+data.get("TEMP_PATH"));
            }
            var prtDatas = $.DatasetList();
            prtDatas.add(data);
            var ocx = null;
            try{
                ocx = new ActiveXObject("Wade3Printer.Printer");
                if(ocx){
                    ocx.DoPrint(prtDatas.toString());
                }else{
                    MessageBox.alert("告警提示", "打印控件不存在！");
                }
            }catch(e){
                MessageBox.alert("告警提示","打印控件未安装或版本已升级，请到首页下载安装最新的打印控件.");
            }
        }
    },
        //员工票据
        staffNote:{
            repeatFlag: false,
            ticketParam: null,	//票据参数

            //获取单联票据
            getPrintTicketInfo:function(prtInfo){
                if(!$.staffNote.ticketParam) $.staffNote.ticketParam = $.DataMap();
                $.staffNote.ticketParam.clear();
                /**
                 * 必传字段[TRADE_ID,FEE_MODE,OTAL_FEE,EPARCHY_CODE,SERIAL_NUMBER,TRADE_TYPE_CODE]
                 * 特殊字段:NONBOSSFEE_TRADE_ID   非BOSS收款补录
                 * 		 ADVANCE_TICKET		预存款补打发票，冲红代入
                 * 		 CH_TICKET		冲红发票状态
                 */

                prtInfo.eachKey(function(key, idex, total){
                    if(key == "PRINT_DATA") return true;
                    $.staffNote.ticketParam.put(key, prtInfo.get(key));
                });

                //从打印数据里面解析调用国税接口数据
                var printData=prtInfo.get("PRINT_DATA");
                if(printData.containsKey("FEE_TYPE") && printData.containsKey("FEE")){
                    var feeList = $.DatasetList();
                    var tag="\<br\/\>";
                    var feeTypes = (printData.get("FEE_TYPE", "")).split(tag);
                    var fees = (printData.get("FEE", "")).split(tag);
                    var brandModels = (printData.get("BRAND_MODEL", "")).split(tag);
                    var units = (printData.get("UNIT", "")).split(tag);
                    var quantitys = (printData.get("QUANTITY", "")).split(tag);
                    var prices = (printData.get("PRICE", "")).split(tag);
                    if(feeTypes && feeTypes.length){
                        for(var i=0, size=feeTypes.length; i<size; i++){
                            var fee = $.DataMap();
                            //最后一可能为空
                            if(size==feeTypes.length-1 && $.trim(feeTypes[i])==""){
                                break;
                            }
                            fee.put("FEE_TYPE", feeTypes[i]);
                            var amount = 0;
                            if($.isNumeric(fees[i])){
                                amount = parseFloat(fees[i])*100;
                            }
                            fee.put("FEE", amount);

                            if(brandModels) fee.put("BRAND_MODEL", brandModels[i]);
                            if(units) fee.put("UNIT", units[i]);
                            if(quantitys) fee.put("QUANTITY", quantitys[i]);
                            if(prices){
                                var price = 0;
                                if($.isNumeric(prices[i])){
                                    price = parseFloat(prices[i])*100;
                                }
                                fee.put("PRICE", price);
                            }
                            feeList.add(fee);
                        }
                    }
                    $.staffNote.ticketParam.put("TICKET_FEE", feeList.toString());
                }
                //送国税需要
                if(printData.containsKey("CUST_NAME")){
                    $.staffNote.ticketParam.put("CUST_NAME", printData.get("CUST_NAME", ""));
                }


                var param = "&ACTION=GET_TICKET";
                param += "&FEE_MODE="+prtInfo.get("FEE_MODE");
                param += "&TRADE_TYPE_CODE="+prtInfo.get("TRADE_TYPE_CODE");
                param += "&EPARCHY_CODE="+prtInfo.get("EPARCHY_CODE");
                if(prtInfo.containsKey("USER_EPARCHY_CODE")){
                    param += "&USER_EPARCHY_CODE="+prtInfo.get("USER_EPARCHY_CODE");
                    $.staffNote.ticketParam.put("USER_EPARCHY_CODE", prtInfo.get("USER_EPARCHY_CODE"));
                }
                if(prtInfo.containsKey("ADVANCE_TICKET")){
                    param += "&ADVANCE_TICKET="+prtInfo.get("ADVANCE_TICKET");
                }

                $.beginPageLoading("加载员工票据。。。");
                ajaxSubmit(null, null, param, $.cssubmit.componentId,
                    function(data){
                        $.endPageLoading();
                        $.staffNote.createStaffNote(data, prtInfo.get("NAME"), parseFloat(prtInfo.get("TOTAL_FEE"))/100);
                    },
                    function(code, info, detail){
                        $.endPageLoading();
                        MessageBox.alert("告警提示","获取员工票据数据错误！", null, null, info, detail);
                    });
            },
            //创建单联票据窗口
            createStaffNote:function(info, name, fee){
                var cssStyle = "none";
                if(info.get("PRIV") == "true") cssStyle="";
                var msgPanel = $("#StaffNote_MSG_PANEL");
                if (!msgPanel.length) {
                    var msgArr = [];
                    msgArr.push('<div id="StaffNote_MSG_PANEL" class="c_dialog" style="display:" x-wade-uicomponent="dialog">');
                    msgArr.push('    <div class="wrapper" style="width: 30em;">');
                    msgArr.push('        <div class="header">');
                    msgArr.push('            <div class="text">票据打印设置</div>');
                    msgArr.push('        </div>');
                    msgArr.push('        <div class="content">');
                    msgArr.push('            <div class="c_msg c_tip c_tip-red">');
                    msgArr.push('                <div class="title"><span>是否打印'+name+'？</span></div>');
                    msgArr.push('            </div>');
                    msgArr.push('            <div class="c_list c_list-col-1  c_list-border c_list-line">');
                    msgArr.push('                <ul class="ul">');
                    msgArr.push('                    <li class="li">');
                    msgArr.push('                        <div class="label">台账流水</div>');
                    msgArr.push('                        <div class="value"><input type="text" id="STAFF_TRADE_ID" value="'+$.staffNote.ticketParam.get("TRADE_ID")+'" readOnly="readOnly" /></div>');
                    msgArr.push('');
                    msgArr.push('                    </li>');
                    msgArr.push('                    <li class="li">');
                    msgArr.push('                        <div class="label">票面金额(元)</div>');
                    msgArr.push('                        <div class="value"><input type="text" id="STAFF_TOTAL_FEE" value="'+fee+'" readOnly="readOnly"/></div>');
                    msgArr.push('                    </li>');
                    msgArr.push('                    <li class="li">');
                    msgArr.push('                        <div class="label">税务登记号</div>');
                    msgArr.push('                        <div class="value"><input type="text" id="STAFF_TAX_NO" value="'+info.get("TAX_NO", "")+'" /></div>');
                    msgArr.push('                    </li>');
                    msgArr.push('                    <li class="li">');
                    msgArr.push('                        <div class="label">票据编号</div>');
                    msgArr.push('                        <div class="value"><input type="text" id="STAFF_TICKET_ID" value="'+info.get("TICKET_ID", "")+'" /></div>');
                    msgArr.push('                    </li>');
                    msgArr.push('                </ul>');
                    msgArr.push('            </div>');
                    msgArr.push('            <div class="c_submit c_submit-full">');
                    msgArr.push('                <button type="button" onclick="$.staffNote.submitPrint(true)" id="TicketPrtBtn"');
                    msgArr.push('                        class="e_button-blue e_button-page"><i class="e_ico-print"></i><span>打印</span></button>');
                    msgArr.push('                <button type="button" onclick="$.staffNote.submitPrint(false)" id="TicketNonPrtBtn"');
                    msgArr.push('                        class="e_button-blue e_button-page-cancel"><i class="e_ico-cancel"></i><span>取消</span></button>');
                    msgArr.push('            </div>');
                    msgArr.push('        </div>');
                    msgArr.push('    </div>');
                    msgArr.push('</div>');

                    $(document.body).append(msgArr.join(""));
                    msgPanel = $("#StaffNote_MSG_PANEL");
                    msgArr=null;
                }
                msgPanel.css("display", "");
                msgPanel = null;

            },
            //提交单联票据打印
            submitPrint:function(flag) {
                if(!flag){
                    $("#StaffNote_MSG_PANEL").remove();
                    $.printMgr.printReceipt();
                    return;
                }
                if(!$.staffNote.chkTicketField()) return;
                if($.staffNote.repeatFlag) return;		//防止重复提交
                $.staffNote.repeatFlag = true;
                var ticketId = $.trim($("#STAFF_TICKET_ID").val());
                var taxNo = $.trim($("#STAFF_TAX_NO").val());
                var parameter = "&ACTION=CHECK_TICKET";
                parameter += "&TAX_NO=" + $("#STAFF_TAX_NO").val();
                parameter += "&TICKET_ID=" + ticketId;
                var ticketData = $.staffNote.ticketParam;
                if(ticketData && ticketData.length){
                    ticketData.eachKey(function(key, idex, total){
                        parameter += "&"+key+"="+ticketData.get(key);
                    });
                }
                $.beginPageLoading("校验票据。。。");
                ajaxSubmit(null, null, parameter, $.cssubmit.componentId,
                    function(data){
                        $.endPageLoading();
                        $.staffNote.repeatFlag = false;
                        if(!data || data.get("RESULT_CODE")!="0"){
                            MessageBox.error("错误提示","校验票据错误！");
                            return;
                        }
                        $("#StaffNote_MSG_PANEL").remove();
                        $.printMgr.printTicket(data);
                    },
                    function(code, info, detail){
                        $.endPageLoading();
                        $.staffNote.repeatFlag = false;
                        MessageBox.error("错误提示","校验票据错误！", null, null, info, detail);
                    });
            },
            //校验输入票据
            chkTicketField:function() {
                var tax = $("#STAFF_TAX_NO").val();
                if($.trim(tax) == "") {
                    alert("税务登记号不能为空！");
                    $("#STAFF_TAX_NO").focus();
                    return false;
                }
                var ticket = $("#STAFF_TICKET_ID").val();
                if($.trim(ticket) == "") {
                    alert("票据编号不能为空！");
                    $("#STAFF_TICKET_ID").focus();
                    return false;
                }
                return true;
            }
        }});

})(Wade);
