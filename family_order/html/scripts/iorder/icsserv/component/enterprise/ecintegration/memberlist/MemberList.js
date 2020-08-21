$(function() {
    $("#MEBLIST_FILE").afterAction(function(e, file) {
        //		alert(file.name + "|" + file.fileId); // 从 file 对象中获取文件名和上传后的文件 id
        $("#MEMBER_FILE_INFO_TMP").attr("FILE_ID", file.fileId);
        $("#MEMBER_FILE_INFO_TMP").attr("FILE_NAME", file.name);
    });
});

function initMemberListByOperType() {
    //如果是删除成员，则新增操作隐藏
    if ($("#M_CHILDOFFER_OPER_TYPE").val() == "DelMeb") {
        $("#SINGLE_ADD").css("display", "none");
        $("#BATCH_ADD_1").css("display", "none");
        $("#BATCH_ADD_2").css("display", "none");
        $("#importMebButton").css("display", "none");
    }
}

function showAddMemberByType() {
    if ($("#ADD_MEB_TYPE").val() == "0") { //批量新增
        $("#SINGLE_ADD").css("display", "none");
        $("#BATCH_ADD_1").css("display", "");
        $("#BATCH_ADD_2").css("display", "");
    } else if ($("#ADD_MEB_TYPE").val() == "1") { //单个新增
        $("#SINGLE_ADD").css("display", "");
        $("#BATCH_ADD_1").css("display", "none");
        $("#BATCH_ADD_2").css("display", "none");
    }
}

//批量号码新增
function addMemberBatch() {
    var meblistFile = $("#MEBLIST_FILE").val();
    if (!meblistFile) {
        MessageBox.alert("提示信息", "请先导入文件再执行导入！");
        return false;
    }

    var impXmlPath = $("#IMPORT_XML_PATH").val();
    var expXmlPath = $("#EXPORT_XML_PATH").val();
    var chkSvcName = $("#CHECK_SVC_NAME").val();
    var ecOfferCode = $("#EC_OFFER_CODE").val();

    var custId = "";
    if(!$.os.phone){
        custId = $.enterpriseLogin.getInfo().get("CUST_INFO").get("CUST_ID");
    }else{
        custId = new Wade.DataMap($("#CUST_INFO").text()).get("CUST_ID");
    }

    var param = "&MEBLIST_FILE=" + meblistFile + "&IMPORT_XML_PATH=" + impXmlPath + "&EXPORT_XML_PATH=" + expXmlPath +
         "&CHECK_SVC_NAME=" + chkSvcName + "&EC_OFFER_CODE=" + ecOfferCode + "&ACTION=excuteImport" +"&EC_CUST_ID="+custId;
    
    if ($("#ESP_EC_USER_ID").val() && $("#ESP_BPM_TEMPLET_ID").val()){
        var ecUserId = $("#ESP_EC_USER_ID").val();
    	var bpmTempletId = $("#ESP_BPM_TEMPLET_ID").val();
    	param +="&EC_USER_ID=" + ecUserId +"&BPM_TEMPLET_ID=" + bpmTempletId;
    }
    
    $.beginPageLoading("文件导入中，请稍后...");
    $.ajax.submit("", "", param, "MEBLIST_PART", function(data) {
            $.endPageLoading();
            var totalNum = data.get("TOTAL_NUM");
            var failNum = data.get("FAIL_NUM");
            var succNum = data.get("SUCCESS_NUM");
            var tip = "您本次共导入数据" + totalNum + "条，其中成功" + succNum + "条，失败" + failNum + "条！";

            if (succNum > 0) {
                var memFileData = new Wade.DataMap();
                memFileData.put("FILE_ID", $("#MEMBER_FILE_INFO_TMP").attr("FILE_ID"));
                memFileData.put("FILE_NAME", $("#MEMBER_FILE_INFO_TMP").attr("FILE_NAME"));
                $("#MEMBER_FILE_INFO").text(memFileData.toString());

                var existSn = "";
                var succList = data.get("SUCC_LIST");
                for (var i = 0, size = succList.length; i < size; i++) {
                    var succData = succList.get(i);
                    if (ecOfferCode == 380700) {
                        //和商务TV批量成员新增
                        var sn = succData.get("DEV_MAC_NUMBER");
                        if ($("#MEB_LIST_UL").find("li[id=MEB_LIST_" + sn + "]").length > 0) {
                            existSn = existSn + sn + ",";
                            continue;
                        }
                        drawTVMemberListHtml(succData);
                    } else if (ecOfferCode == 380300) {
                        //云WiFi批量成员新增
                        var sn = succData.get("MAC_NUMBER");
                        if ($("#MEB_LIST_UL").find("li[id=MEB_LIST_" + sn + "]").length > 0) {
                            existSn = existSn + sn + ",";
                            continue;
                        }
                        drawWifiMemberListHtml(succData);
                    } else {
                        var sn = succData.get("SERIAL_NUMBER");
                        if ($("#MEB_LIST_UL").find("li[id=MEB_LIST_" + sn + "]").length > 0) {
                            existSn = existSn + sn + ",";
                            continue;
                        }
                        drawMemberListHtml(succData);
                    }
                }
                if (existSn.length > 0) {
                    MessageBox.alert("提示信息", "以下号码在列表中已存在[" + sn + "]，已在导入时剔除！");
                }
            }
            if (failNum > 0) {
                tip = tip + "请点击[下载]按钮下载失败数据。";
                var failUrl = data.get("FAILD_FILE_URL");
                MessageBox.alert("提示信息", tip, function(btn) {
                    if (btn == "ext1") {
                        window.location.href = failUrl;
                    }
                }, { "ext1": "下载" });

                $("#MEBLIST_failFileLi").css("display", "");
                $("#MEBLIST_failFile").attr("href", failUrl);
            } else {
                $("#MEBLIST_failFileLi").css("display", "none");
                $("#MEBLIST_failFile").attr("href", "");
                MessageBox.success("成功信息", tip);
            }

        },
        function(error_code, error_info, derror) {
            $.endPageLoading();
            showDetailErrorInfo(error_code, error_info, derror);
        });
}

//单个号码新增
function addMemberSingle() {
    if (!$.validate.verifyAll("SINGLE_ADD")) {
        return false;
    }

    //云WiFi处理
    var offerCode = $("#EC_OFFER_CODE").val();

    // 校验提交参数
    if (!checkMemberSingle(offerCode)) {
        return false;
    }

    var memberInfo = new Wade.DataMap();
    $("#SINGLE_ADD").find(":input").each(function() {
        memberInfo.put($(this).attr("NAME"), $(this).val());
    });

    var impXmlPath = $("#IMPORT_XML_PATH").val();
    var expXmlPath = $("#EXPORT_XML_PATH").val();
    var chkSvcName = $("#CHECK_SVC_NAME").val();
    var ecOfferCode = $("#EC_OFFER_CODE").val();

    var custId = "";
    if(!$.os.phone){
    	custId = $.enterpriseLogin.getInfo().get("CUST_INFO").get("CUST_ID");
    }else{
    	custId = new Wade.DataMap($("#CUST_INFO").text()).get("CUST_ID");
    }

	var ecUserId = $("#ESP_EC_USER_ID").val();
	var bpmTempletId = $("#ESP_BPM_TEMPLET_ID").val();

    var param = "&MEMBER_INFO=" + memberInfo.toString() + "&IMPORT_XML_PATH=" + impXmlPath +
    	"&BPM_TEMPLET_ID=" + bpmTempletId +"&EXPORT_XML_PATH=" + expXmlPath + "&CHECK_SVC_NAME=" + chkSvcName + "&EC_OFFER_CODE=" + ecOfferCode +
        "&EC_USER_ID=" + ecUserId +"&EC_CUST_ID=" + custId + "&ACTION=addMemberSingle";
    $.beginPageLoading("成员新增校验中，请稍后...");
    $.ajax.submit("", "", param, "MEBLIST_PART", function(data) {
            $.endPageLoading();

            //		var checkResult = data.get("IMPORT_RESULT");
            if (data && data.get("IMPORT_RESULT") == "true") {

                if (offerCode == "380300") {
                    drawWifiMemberListHtml(data);
                } else {
                    if (data.get("IMPORT_FLAG") == "false") {
                        /*
                                                                     *  融合v网添加的固话成员号码，依赖 多媒体桌面电话的成员号码；
                                                                     *  判断该号码有没有 添加至 多媒体桌面电话的成员列表里  CHILD_OFFER_ID_110000002222
                         */
                        var vpmOfferData = new Wade.DataMap($("#CHILD_OFFER_DATA_110000002222").text());
                        if (vpmOfferData && vpmOfferData.get("MEB_LIST")) {
                            var vpmMebList = vpmOfferData.get("MEB_LIST");
                            var vpmflag = false;
                            for (var i = 0; i < vpmMebList.length; i++) {
                                var sn222 = vpmMebList.get(i).get("SERIAL_NUMBER");
                                if (sn222 == data.get("SERIAL_NUMBER")) {
                                    vpmflag = true;
                                    break;
                                }
                            }
                            if (!vpmflag) {
                                MessageBox.alert("提示信息", data.get("IMPORT_ERROR"));
                            } else {
                                drawMemberListHtml(data);
                            }
                        } else {
                            MessageBox.alert("提示信息", data.get("IMPORT_ERROR"));
                        }

                    } else {
                        drawMemberListHtml(data);
                    }
                }
            } else if (data && data.get("IMPORT_RESULT") == "false") {
                MessageBox.alert("提示信息", data.get("IMPORT_ERROR"));
            }

        },
        function(error_code, error_info, derror) {
            $.endPageLoading();
            showDetailErrorInfo(error_code, error_info, derror);
        });

}
//和商务TV单个成员新增
function addTVMemberSingle() {
	if (!$.validate.verifyAll("SINGLE_ADD")) {
		return false;
	}
	var sn = $("#DEV_MAC_NUMBER").val();
	if ($("#MEB_LIST_UL").find("li[id=MEB_LIST_" + sn + "]").length > 0) {
		MessageBox.alert("提示信息", "成员号码列表中已经存在相同的号码[" + sn + "]，请勿重复添加！");
		return false;
	}
	var bpmTempletId = $("#ESP_BPM_TEMPLET_ID").val();
	var serialNumber = $("#ESP_SERIAL_NUMBER").val();
	var ecUserId = $("#ESP_EC_USER_ID").val();
	var memberInfo = new Wade.DataMap();
	$("#SINGLE_ADD").find(":input").each(function() {
		memberInfo.put($(this).attr("NAME"), $(this).val());
	});
	var impXmlPath = $("#IMPORT_XML_PATH").val();
	var expXmlPath = $("#EXPORT_XML_PATH").val();
	var chkSvcName = $("#CHECK_SVC_NAME").val();
	var ecOfferCode = $("#EC_OFFER_CODE").val();
	var param = "&MEMBER_INFO=" + memberInfo.toString() + "&IMPORT_XML_PATH=" + impXmlPath + "&EXPORT_XML_PATH=" + expXmlPath
			+ "&BPM_TEMPLET_ID=" + bpmTempletId + "&SERIAL_NUMBER=" + serialNumber + "&EC_USER_ID=" + ecUserId + "&CHECK_SVC_NAME="
			+ chkSvcName + "&EC_OFFER_CODE=" + ecOfferCode + "&ACTION=addTVMemberSingle";
	$.beginPageLoading("成员新增校验中，请稍后...");
	$.ajax.submit("", "", param, "MEBLIST_PART", function(data) {
		$.endPageLoading();
		if (data && data.get("IMPORT_RESULT") == "true") {
			drawTVMemberListHtml(data);
		} else if (data && data.get("IMPORT_RESULT") == "false") {
			MessageBox.alert("提示信息", data.get("IMPORT_ERROR"));
		}
	}, function(error_code, error_info, derror) {
		$.endPageLoading();
		showDetailErrorInfo(error_code, error_info, derror);
	});
}

//删除号码
function deleteMemberSn(el) {
	debugger;
    var delMebList = new Wade.DatasetList($("#SUCCESS_MEMBERDEL_LIST").text());
    var sn = $(el).closest("li").attr("id").substring(9);
    MessageBox.confirm("提示信息", "是否确定删除该【 " + sn + "】成员号码？", function(btn) {
        if ("ok" == btn) {
            var operType = $("#oattr_OPER_TYPE").val();  // 操作类型
            
        	if (operType && "DelMeb" == operType) {
        		//变更删除成员
        		var ecOfferCode = $("#EC_OFFER_CODE").val();
        		var ecUserId = $("#ESP_EC_USER_ID").val();
        		var param =  "&SERIAL_NUMBER=" + sn + "&EC_USER_ID=" + ecUserId + "&EC_OFFER_CODE=" + ecOfferCode + "&ACTION=deleteMemberSn";
        		
        		$.ajax.submit("", "", param, "MEBLIST_PART", function(data) {
        			if ("true" == data.get("CHECK_RESULT")) {
        				$(el).closest("li").remove();
        				
        				var mebList = new Wade.DatasetList($("#SUCCESS_MEMBER_LIST").text());
        				for (var i = 0, size = mebList.length; i < size; i++) {
        					var mebSn = mebList.get(i);
        					if (mebSn.get("SERIAL_NUMBER") == sn) {
        						if (mebSn.get("OPER_CODE") == "0") {
        							mebList.remove(mebSn);
        						} else if ( "3"==mebSn.get("OPER_CODE")) {
        							delMebList.add(mebSn);
        							mebSn.put("OPER_CODE", "1"); //1-删除
        						}
        						break;
        					}
        				}
        				$("#SUCCESS_MEMBER_LIST").text(mebList.toString());
        				$("#SUCCESS_DELMBR_LIST").text(delMebList.toString());
        				$("#SUCCESS_MEMBERDEL_LIST").text(delMebList.toString());
        			} else {
        				data.get("CHECK_ERROR");
        				MessageBox.error("错误信息", data.get("CHECK_ERROR"));
        			}
        			
        		}, function(error_code, error_info, derror) {
        			$.endPageLoading();
        			showDetailErrorInfo(error_code, error_info, derror);
        		});
        	} else {
        		//开通删除成员
        		$(el).closest("li").remove();
				
				var mebList = new Wade.DatasetList($("#SUCCESS_MEMBER_LIST").text());
				for (var i = 0, size = mebList.length; i < size; i++) {
					var mebSn = mebList.get(i);
					if (mebSn.get("SERIAL_NUMBER") == sn) {
						if (mebSn.get("OPER_CODE") == "0") {
							mebList.remove(mebSn);
						} else if (mebSn.get("OPER_CODE") == "3") {
							delMebList.add(mebSn);
							mebSn.put("OPER_CODE", "1"); //1-删除
						}
						break;
					}
				}
				$("#SUCCESS_MEMBER_LIST").text(mebList.toString());
				$("#SUCCESS_DELMBR_LIST").text(delMebList.toString());
				$("#SUCCESS_MEMBERDEL_LIST").text(delMebList.toString());
        	}
            
        }
    });

}

//删除云WiFi成员
function deleteWifiMemberSn(el) {
     
    var sn = $(el).closest("li").attr("id").substring(9);
    var delMebList = new Wade.DatasetList($("#SUCCESS_MEMBERDEL_LIST").text());
    MessageBox.confirm("提示信息", "是否确定删除该【 " + sn + "】成员号码？", function(btn) {
        if ("ok" == btn) {
            $(el).closest("li").remove();
            var mebList = new Wade.DatasetList($("#SUCCESS_MEMBER_LIST").text());
            for (var i = 0, size = mebList.length; i < size; i++) {
                var mebSn = mebList.get(i);
                if (mebSn.get("MAC_NUMBER") == sn) {
                    delMebList.add(mebSn);
                    mebList.remove(mebSn);
                    break;
                }
            }
            $("#SUCCESS_DELMBR_LIST").text(delMebList.toString());
            $("#SUCCESS_MEMBER_LIST").text(mebList.toString());
        }
    });

}

//删除和商务TV成员
function deleteTVMemberSn(el) {
     
    var sn = $(el).closest("li").attr("id").substring(9);
    var delMebList = new Wade.DatasetList($("#SUCCESS_MEMBERDEL_LIST").text());
    MessageBox.confirm("提示信息", "是否确定删除该【 " + sn + "】成员号码？", function(btn) {
        if ("ok" == btn) {
            $(el).closest("li").remove();
            var mebList = new Wade.DatasetList($("#SUCCESS_MEMBER_LIST").text());
            for (var i = 0, size = mebList.length; i < size; i++) {
                var mebSn = mebList.get(i);
                if (mebSn.get("DEV_MAC_NUMBER") == sn) {
                	delMebList.add(mebSn);
                    mebList.remove(mebSn);
                    break;
                }
            }
            $("#SUCCESS_DELMBR_LIST").text(delMebList.toString());
            $("#SUCCESS_MEMBER_LIST").text(mebList.toString());
        }
    });
}

function drawMemberListHtml(memberInfo) {
    var sn = memberInfo.get("SERIAL_NUMBER");
    var htmlStr = "<li id='MEB_LIST_" + sn + "'>";
    htmlStr += "<div class='e_ico-pic e_ico-user'></div>";
    htmlStr += "<div class='main'>";
    htmlStr += "<div class='title'>" + sn + "</div>";
    if (memberInfo.get("SHORT_CODE")) {
        htmlStr += "<div class='content'>" + memberInfo.get("SHORT_CODE") + "</div>";
    }
    htmlStr += "</div>";
    htmlStr += "<div class='fn' ontap='deleteMemberSn(this);'>";
    htmlStr += "<span class='e_ico-delete'></span>";
    htmlStr += "</div>";
    htmlStr += "</li>";
    $("#MEB_LIST_UL").append(htmlStr);
    var mebList = new Wade.DatasetList($("#SUCCESS_MEMBER_LIST").text());
    mebList.add(memberInfo);
    $("#SUCCESS_MEMBER_LIST").text(mebList.toString());
}

//新增云WiFi成员列表
function drawWifiMemberListHtml(memberInfo) {
     
    var sn = memberInfo.get("MAC_NUMBER");
    var htmlStr = "<li id='MEB_LIST_" + sn + "'>";
    htmlStr += "<div class='e_ico-pic e_ico-user'></div>";
    htmlStr += "<div class='main'>";
    htmlStr += "<div class='title'>" + sn + "</div>";
    if (memberInfo.get("SERIAL_NUMBER")) {
        htmlStr += "<div class='content'>" + memberInfo.get("SERIAL_NUMBER") + "</div>";
    }
    htmlStr += "</div>";
    htmlStr += "<div class='fn' ontap='deleteWifiMemberSn(this);'>";
    htmlStr += "<span class='e_ico-delete'></span>";
    htmlStr += "</div>";
    htmlStr += "</li>";
    $("#MEB_LIST_UL").append(htmlStr);
    var mebList = new Wade.DatasetList($("#SUCCESS_MEMBER_LIST").text());
    mebList.add(memberInfo);
    $("#SUCCESS_MEMBER_LIST").text(mebList.toString());
}

//新增和商务TV成员列表
function drawTVMemberListHtml(memberInfo) {
     
    var sn = memberInfo.get("DEV_MAC_NUMBER");
    var htmlStr = "<li id='MEB_LIST_" + sn + "' title='" + sn + "'>";
    htmlStr += "<div class='e_ico-pic e_ico-user'></div>";
    htmlStr += "<div class='main'>";
    htmlStr += "<div class='title'>" + sn + "</div>";
    if (memberInfo.get("DEV_SN_NUMBER")) {
        htmlStr += "<div class='content'>" + memberInfo.get("DEV_SN_NUMBER") + "</div>";
    }
    htmlStr += "</div>";
    htmlStr += "<div class='fn' ontap='deleteTVMemberSn(this);'>";
    htmlStr += "<span class='e_ico-delete'></span>";
    htmlStr += "</div>";
    htmlStr += "</li>";
    $("#MEB_LIST_UL").append(htmlStr);
    var mebList = new Wade.DatasetList($("#SUCCESS_MEMBER_LIST").text());
    mebList.add(memberInfo);
    $("#SUCCESS_MEMBER_LIST").text(mebList.toString());
}



function checkMemberSingle(offerCode) {
    var mebList = new Wade.DatasetList($("#SUCCESS_MEMBER_LIST").text());
    if (mebList && mebList.length > 0) {
        var snFlag = false;
        var snMessage = "";
        for (var i = 0; i < mebList.length; i++) {
            if (offerCode == "380300") {
                var macNum = $("#MAC_NUMBER").val();
                var oldMacNum = mebList.get(i).get("MAC_NUMBER");
                if (oldMacNum == macNum) {
                    snFlag = true;
                    snMessage = "成员号码列表中已经存在相同的网关MAC号[" + macNum + "]，请勿重复添加！";
                    break;
                }
            } else {
                var sn = $("#SERIAL_NUMBER").val();
                var shortCode = $("#SHORT_CODE").val();
                var oldSn = mebList.get(i).get("SERIAL_NUMBER");
                var oldCode = mebList.get(i).get("SHORT_CODE");
                if (oldSn == sn) {
                    snFlag = true;
                    snMessage = "成员号码列表中已经存在相同的号码[" + sn + "]，请勿重复添加！";
                    break;
                }
                if (oldCode == shortCode) {
                    snFlag = true;
                    snMessage = "成员号码列表中已经存在相同的短号[" + oldCode + "]，请勿重复添加！";
                    break;
                }
            }

        }

        if (snFlag) {
            MessageBox.alert("提示信息", snMessage);
            return false;
        }
    }

    return true;
}