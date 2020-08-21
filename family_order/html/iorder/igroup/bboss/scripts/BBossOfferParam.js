/**
 * 绑定的初始化方法
 */

var ATT_INFOS = new Wade.DatasetList();// 合同附件集合
var AUDITOR_INFOS = new Wade.DatasetList();// 审批人集合
var CONTACTOR_INFOS = new Wade.DatasetList();// 联系人集合
var indexNum = 0;// 标记LIST中的合同位置

// 初始化方法
function initBBossOfferParam() {
    // 事件绑定
    eventBinding();

    // 其他设置
    $("#addApprove li[name='time'] .value input").val(getNowDateyyyyMMddhhmmss());

    // 初始化数据
    initParamData();
}

function initParamData() {	
	// 1 合同附件初始化
    var data = $("#OLD_CONTRACT_INFO").val();
    if (!data || data.length<=0) {
        return;
    }

    var oldContractInfo = new Wade.DataMap($("#OLD_CONTRACT_INFO").val());
    var name = oldContractInfo.get("CONT_NAME");
    var type = oldContractInfo.get("ATT_TYPE_CODE");
    var tagClass = "";
    indexNum++;
    oldContractInfo.put("INDEX_NUM", "OLD"+indexNum);
    $("#OLD_CONTRACT_INFO").val(oldContractInfo.toString());

    if (type == 1) {
        type = "合同";
        tagClass = "e_tag-green";
    } else if (type == 2) {
        type = "普通";
    }

    var html = "";
    html += '<li>';
    html += '	<div class="ico">';
    html += '		<span class="e_ico-file"></span>';
    html += '	</div>';
    html += '	<div class="main" ontap="showContractInfo(this);" indexNum="'+'OLD'+indexNum+'">';
    html += '		<div class="title">';
    html += '			<a href="#nogo">' + name + '</a>';
    html += '		</div>';
    html += '	</div>';
    html += '	<div class="side" ontap="showContractInfo(this);" indexNum="'+indexNum+'">';
    html += '		<span class="e_tag ' + tagClass + '">' + type + '</span>';
    html += '	</div>';
    html += '	<div class="fn" ontap="enclosureDelete(this);"' + ' indexNum="'
        + indexNum + '"' + '>';
    html += '		<span class="e_ico-delete"></span>';
    html += '	</div>';
    html +=  '</li>';

    $("#enclosureList ul").append(html);
    $("#enclosureList").css("display", "");
}

function showContractInfo(el) {
    var DataIndex = el.getAttribute('indexnum');
    var contractInfo = new Wade.DataMap();
    if (DataIndex.indexOf("OLD") != "-1") {
        contractInfo = new Wade.DataMap($("#OLD_CONTRACT_INFO").val());
    }
    else {
        for (var i = 0; i < ATT_INFOS.length; i++) {
            if (DataIndex == ATT_INFOS.get(i, "INDEX_NUM")) {
                contractInfo = ATT_INFOS.get(i);
                break;
            }
        }
    }
    $("#ATT_TYPE_CODE").val(contractInfo.get('ATT_TYPE_CODE'));
    $("#ATT_NAME").val(contractInfo.get('ATT_TYPE_CODE'));
    $("#ATT_CODE").val(contractInfo.get('ATT_CODE'));
    $("#CONT_NAME").val(contractInfo.get('CONT_NAME'));
    $("#ContEffdate").val(contractInfo.get('START_TIME'));
    $("#ContExpdate").val(contractInfo.get('END_TIME'));
    $("#RecontExpdate").val(contractInfo.get('RECONT_TIME'));
    $("#ContFee").val(contractInfo.get('CONT_FEE'));
    $("#PerferPlan").val(contractInfo.get('PERFER_PLAN'));
    $("#IsAutoRecont").val(contractInfo.get('AUTO_RECONT'));
    $("#AutoRecontCyc").val(contractInfo.get('AUTO_RECONT_CYC'));
    $("#IsRecont").val(contractInfo.get('IS_RECONT'));

    $("#addEnclosure_float").attr("class", "c_float c_float-show");

}
function eventBinding() {
    $("#ATT_TYPE_CODE").change(function () {
        // 合同附件
        if (this.value == 1) {
            $("#e_contractInputfild").css("display", "");
        }
        // 普通附件
        else if (this.value == 2) {
            $("#e_contractInputfild").css("display", "none");
        }
    });

    $("#OPERTYPE").change(function () {
        // 每次集团业务的商品操作类型变化的时候
        var operCode = $("#cond_OPER_TYPE").val();
        if ("CrtUs" == operCode || "ChgUs" == operCode || "DstUs" == operCode) {
            // 取得所有展示出来的子商品信息
            var showSubOffers = $("#productOfferUL").find("li");
            for (var b = 0; b < showSubOffers.length; b++) {
                // 子商品展示全置为可选状态
                var tempDivEle = $($(showSubOffers[b]).children()[1]).children();
                tempDivEle.attr("disabled", "");
            }
        }
    });

    $("#CONTACTOR_TYPE_CODE").change(function () {
        //当联系人类型为订单提交人的时候，必须要同步总部
        var contactorType = $("#CONTACTOR_TYPE_CODE").val();
        if ("5" == contactorType) {
            $("#CONTACTOR_PHONE").val("");
//            $("#CONTACTOR_PHONE").attr("readOnly", true);
        }
        else {
//            $("#CONTACTOR_PHONE").attr("readOnly", false);
        }
    });
    
}

/**
 * 联系人删除
 *
 * @param el
 */
function contractorDelete(el) {
    var indexAttr = el.getAttribute("indexNum");
    for (var i = 0; i < CONTACTOR_INFOS.length; i++) {
        if (indexAttr == CONTACTOR_INFOS.get(i, "INDEX_NUM")) {
            CONTACTOR_INFOS.removeAt(i);
            break;
        }
    }
    el.parentNode.parentNode.parentNode.outerHTML = "";
}

/**
 * 附件删除
 *
 * @param el
 */
function enclosureDelete(el) {
    var indexAttr = el.getAttribute("indexNum");
    for (var i = 0; i < ATT_INFOS.length; i++) {
        if (indexAttr == ATT_INFOS.get(i, "INDEX_NUM")) {
            ATT_INFOS.removeAt(i);
            break;
        }
    }
    $("#ATT_INFOS").val("");
    el.parentNode.outerHTML = "";
}

/**
 * 审批信息删除
 *
 * @param el
 */
function approveDelete(el) {
    MessageBox.confirm("确认删除", "删除审批信息", function (btn) {
        if ("ok" == btn) {
            var indexAttr = el.getAttribute("index");
            for (var i = 0; i < AUDITOR_INFOS.length; i++) {
                if (indexAttr == AUDITOR_INFOS.get(i, "INDEX_NUM")) {
                    AUDITOR_INFOS.removeAt(i);
                    break;
                }
            }
            el.parentNode.outerHTML = "";
        }
    });
}

/**
 * 获取当前日期
 *
 * @returns {String}
 */
function getNowDate() {
    var d = new Date();
    var year = d.getFullYear();
    var month = d.getMonth() + 1;
    month = month < 10 ? ("0" + month) : month;
    var dt = d.getDate();
    dt = dt < 10 ? ("0" + dt) : dt;
    return year + "-" + month + "-" + dt;
}

/**
 * 获取当前日期 格式：yyyyMMddhhmmss
 *
 * @returns {String}
 */
function getNowDateyyyyMMddhhmmss() {
    var d = new Date();
    var year = d.getFullYear();
    var month = d.getMonth() + 1;
    month = String(month < 10 ? ("0" + month) : month);
    var dt = d.getDate();
    dt = String(dt < 10 ? ("0" + dt) : dt);    
    var hours = d.getHours();
    hours = String(hours < 10 ? ("0" + hours) : hours);
    var minutes = d.getMinutes();
    minutes = String(minutes < 10 ? ("0" + minutes) : minutes);
    var seconds = d.getSeconds();
    seconds = String(seconds < 10 ? ("0" + seconds) : seconds);
   
    return String(year + month + dt + hours + minutes + seconds);    
}

/**
 * 新增附件
 *
 */
function addEnclosure() {
	var dataset = PageData.getData($("#ATT_INFOS"));
	for(var i=0;i<dataset.length;i++){
		var data = dataset.get(i);
		if(data){
			if(data.get("ATT_TYPE_CODE")=="1" && $("#ATT_TYPE_CODE").val()=="1"){
				//只能上传一个合同附件
				MessageBox.alert("提示","只能上传一个合同附件！");
				return false;
			}
			if(data.get("ATT_TYPE_CODE")=="2" && $("#ATT_TYPE_CODE").val()=="2"){
				//只能上传一个普通附件
				MessageBox.alert("提示","只能上传一个普通附件！");
				return false;
			}
		}				
	}
    var name = $("#ATT_NAME").val();
    var type = $("#ATT_TYPE_CODE").val();
    var tagClass = "";
    indexNum++;

    if (type == 1) {
        type = "合同";
        tagClass = "e_tag-green";
    } else if (type == 2) {
        type = "普通";
    }

    var html = "";
    html += '<li>';
    html += '	<div class="ico">';
    html += '		<span class="e_ico-file"></span>';
    html += '	</div>';
    html += '	<div class="main">';
    html += '		<div class="title">';
    html += '			<a href="#nogo">' + name + '</a>';
    html += '		</div>';
    html += '	</div>';
    html += '	<div class="side">';
    html += '		<span class="e_tag ' + tagClass + '">' + type + '</span>';
    html += '	</div>';
    html += '	<div class="fn" ontap="enclosureDelete(this);"' + ' indexNum="'
        + indexNum + '"' + '>';
    html += '		<span class="e_ico-delete"></span>';
    html += '	</div>';
    html += '</li>';

    //校验合同附件
    var flag=checkAttInfo();
    if(true==flag){
    	 var attMap = new Wade.DataMap();
    	    var attType=$("#ATT_TYPE_CODE").val()
    		
    	    attMap.put("ATT_TYPE_CODE",attType);
    	    attMap.put("ATT_CODE", $("#ATT_CODE").val());
    	    attMap.put("CONT_NAME", $("#CONT_NAME").val());
    	    if("1"==attType){
    	    attMap.put("ContEffdate", $("#ContEffdate").val());
    	    attMap.put("ContExpdate", $("#ContExpdate").val());
    	    attMap.put("IsAutoRecont", $("#IsAutoRecont").val());
    	    attMap.put("RecontExpdate", $("#RecontExpdate").val());
    	    attMap.put("ContFee", $("#ContFee").val());
    	    attMap.put("PerferPlan", $("#PerferPlan").val());
    	    attMap.put("AutoRecontCyc", $("#AutoRecontCyc").val());
    	    attMap.put("IsRecont", $("#IsRecont").val());
    	    }
    	    attMap.put("INDEX_NUM", indexNum);
    	    attMap.put("ATT_NAME_FILENAME", $("#ATT_NAME").val());

    	    ATT_INFOS.add(attMap);
    	    $("#ATT_INFOS").val(ATT_INFOS);
    	    $("#enclosureList ul").append(html);
    	    $("#enclosureList").css("display", "");
            hideFloatDetailInfo('addEnclosure');
    }else{
    	return false;
    }
   
}

/**
 * 校验合同信息
 */

function checkAttInfo() {	
	 var flag = false;
	 if(!$("#ATT_NAME").val() || $("#ATT_NAME").val()==""){
         MessageBox.alert("告警提示","合同附件未上传！");
         return false;
     }
	 
	if($("#ATT_NAME").val()!=null && $("#ATT_NAME").val() != ""){
		//$.beginPageLoading("合同附件校验中。。。。");  
		
		var attCode = $("#ATT_NAME").val();
	    var attTypeType = $("#ATT_TYPE_CODE").val();
		var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
		var custId = custInfo.get("CUST_ID");
		var groupId = custInfo.get("GROUP_ID");
		var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
		var offerCode = offerData.get("OFFER_CODE");
	    var param = '&ATT_CODE='+attCode+'&ATT_TYPE_CODE='+attTypeType+
		'&CUST_ID='+custId+'&GROUP_ID='+groupId+'&PRODUCT_ID='+offerCode+'&MPRODUCT_ID='+offerCode; 
		 Wade.httphandler.submit('',
					'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'attFileCheck',
					param, function(d) {
		        		//var dataList = d.map.result; 
		        		var result = d.map.RESULT;
		        		
		        		if(result==true||result=='true'){
		    				//afterAttFile(obj);
		    				flag = true;
		    			}else{   
		    				var message=d.map.ERROR_MESSAGE;
		    				MessageBox.alert("错误提示",message); 
		    				//return false;
		    			}
		            }, function(e, i) {
		                MessageBox.alert("操作失败");
		                //return false;
		            },{async:false});
	}
	return flag;
}
/**
 * 校验合同信息
 */
/*function checkAttInfo() {
	var attType=$("#ATT_TYPE_CODE").val();
	if("1"==attType){
		if($("#ContEffdate").val()==null||$("#ContEffdate").val()=='undefined'||$("#ContEffdate").val()==""){
			MessageBox.alert("告警提示","合同开始日期未填写！");
			return false;
		}
		if($("#ContExpdate").val()==null||$("#ContExpdate").val()=='undefined'||$("#ContExpdate").val()==""){
			MessageBox.alert("告警提示","合同结束日期未填写！");
			return false;
		}
		if($("#IsAutoRecont").val()==null||$("#IsAutoRecont").val()=='undefined'||$("#IsAutoRecont").val()==""){
			MessageBox.alert("告警提示","是否自动续约未填写！");
			return false;
		}
		if("1"==$("#IsAutoRecont").val()&&($("#RecontExpdate").val()==null||$("#RecontExpdate").val()=='undefined'||$("#RecontExpdate").val()=="")){
			MessageBox.alert("告警提示","续约后到期时间未填写！");
			return false;
		}
		if($("#ContFee").val()==null||$("#ContFee").val()=='undefined'||$("#ContFee").val()==""){
			MessageBox.alert("告警提示","签约资费未填写！");
			return false;
		}
		if($("#PerferPlan").val()==null||$("#PerferPlan").val()=='undefined'||$("#PerferPlan").val()==""){
			MessageBox.alert("告警提示","优惠方案未填写！");
			return false;
		}
		if($("#AutoRecontCyc").val()==null||$("#AutoRecontCyc").val()=='undefined'||$("#AutoRecontCyc").val()==""){
			MessageBox.alert("告警提示","自动续约周期填写！");
			return false;
		}
		if($("#IsRecont").val()==null||$("#IsRecont").val()=='undefined'||$("#IsRecont").val()==""){
            MessageBox.alert("告警提示","是否为续签合同未填写！");
            return false;
        }
        if(!$("#ATT_NAME").val() || $("#ATT_NAME").val()==""){
            MessageBox.alert("告警提示","合同附件未上传！");
            return false;
        }
        if(!$("#ATT_CODE").val() || $("#ATT_CODE").val()==""){
            MessageBox.alert("告警提示","是否为合同编码未填写！");
            return false;
        }
        if(!$("#CONT_NAME").val() || $("#CONT_NAME").val()==""){
            MessageBox.alert("告警提示","是否为合同了名称未填写！");
            return false;
        }
        var startDate = new String($("#ContEffdate").val());
    	var endDate = new String($("#ContExpdate").val());
		var RecontExpdate = new String($("#RecontExpdate").val());
    	var a=new Date(startDate);
    	var b = new Date(endDate);
		var recontExpdate = new Date(RecontExpdate);
    	if(a>=b){
    		MessageBox.alert("合同结束时间不能早于合同开始时间");
    		return false;
    	}
		if((b>=recontExpdate)&&($("#IsAutoRecont").val()=="1")){
    		MessageBox.alert("续约后到期时间不能早于合同结束时间");
    		return false;
    	}

		return true;
	}
	if("2"==attType){
		if(!$("#ATT_NAME").val() || $("#ATT_NAME").val()==""){
            MessageBox.alert("告警提示","普通附件未上传！");
            return false;
        }
	}
	return true;
}*/

/**
 * 新增审批信息
 *
 * @param el
 */
function addApprove(el) {
    var name = $("#addApprove li[name='name'] .value input").val();
    var time = $("#addApprove li[name='time'] .value input").val();
    var remark = $("#addApprove li[name='remark'] .value input").val();
    indexNum++;

    if (!time.trim()) {
        time = getNowDate();
    }

    var html = "";
    html += '<li class="link">';
    html += '	<div class="box" ontap="approveDelete(this);" index="' + indexNum + '">';
    html += '		<div class="ico"></div>';
    html += '		<div class="title">' + name + '</div>';
    html += '		<div class="date">' + time + '</div>';
    html += '		<div class="content">' + remark + '</div>';
    html += '	</div>';
    html += '</li>';

    var auditorMap = new Wade.DataMap();
    auditorMap.put("AUDITOR", $("#AUDITOR").val());
    auditorMap.put("AUDITOR_TIME", $("#AUDITOR_TIME").val());
    auditorMap.put("AUDITOR_DESC", $("#AUDITOR_DESC").val());
    auditorMap.put("INDEX_NUM", indexNum);
    AUDITOR_INFOS.add(auditorMap);
    $("#AUDITOR_INFOS").val(AUDITOR_INFOS);

    $("#approveList ul").append(html);
    $("#approveList").css("display", "");
}
// 校验是否为手机号码
function IsPhoneNumber(s) {
    if (s == null || s == '') {
        return false;
    }
    if (s.length != 11
        || (s.substring(0, 2) != '13' && s.substring(0, 2) != '15'
        && s.substring(0, 3) != '147' && s.substring(0, 3) != '186'
        && s.substring(0, 3) != '189' && s.substring(0, 3) != '188'
        && s.substring(0, 3) != '187' && s.substring(0, 3) != '182'
        && s.substring(0, 3) != '183' && s.substring(0, 3) != '180'
        	&&s.substring(0, 3) != '177'&&s.substring(0, 3) != '176')) {
        return false;
    }
    if (!IsAllNumber(s)) {
        return false;
    }
    return true
}
// 校验是否为数字类型
function IsAllNumber(s) {
    if (s == null) {
        return false;
    }
    if (s == '') {
        return true;
    }
    s = '' + s;
    if (s.substring(0, 1) == '-' && s.length > 1) {
        s = s.substring(1, s.length);
    }
    var patrn = /^[0-9]*$/;
    if (!patrn.exec(s)) {
        return false;
    }
    return true
}
/**
 * 新增联系人
 *
 * @param el
 */
function addContact(el) {
    var type = $("#CONTACTOR_TYPE_CODE").text();
    var name = $("#POP_CONTACTOR_ID").val();
    var phone = $("#CONTACTOR_ID_SN").val();
   
    var regx = /(【.*】)(.*)/ig;
    if("undefined"==type||"--请选择--"==type|| typeof(type) == "undefined"){
    	 MessageBox.alert("请选择正确联系人类型！");
         return;
    };
    var resultName = "";
    if(name==""){
    	 MessageBox.alert("联系人名字不能为空！");
         return;
    }else{
    	if(phone==""){
    		
    	}    	
    }
    while (resultName = regx.exec(name)){
        name = resultName[2];
    }
    if(!IsPhoneNumber(phone)){
        MessageBox.alert("请输入正确的手机号码！");
        return;
    };
    
    var queryBBossStaff = queryBBossStaffInfo();
    if("false" == queryBBossStaff){
    	return;
    }
    
    indexNum++;

    var html = "";
    html += '<li>';
    html += '	<div class="pic">';
    html += '		<span class="e_ico-pic e_ico-user"></span>';
    html += '	</div>';
    html += '	<div class="main">';
    html += '		<div class="title">' + name + '</div>';
    html += '		<div class="content">';
    html += '			' + phone + ' <br />';
    html += '			<span class="e_blue">' + type + '</span>';
    html += '	<div class="c_fn" ontap="contractorDelete(this);"' + ' indexNum="'
        + indexNum + '"' + '>';
    html += '		<div class="right">';
    html += '			<span class="e_ico-delete"></span>';
    html += '		</div>';
    html += '	</div>';
    html += '		</div>';
    html += '	</div>';
    html += '</li>';

    var contactorMap = new Wade.DataMap();
    contactorMap.put("CONTACTOR_TYPE_CODE", $("#CONTACTOR_TYPE_CODE").val());
    contactorMap.put("CONTACTOR_NAME", name);
    contactorMap.put("CONTACTOR_PHONE", $("#CONTACTOR_ID_SN").val());
    contactorMap.put("INDEX_NUM", indexNum);
    contactorMap.put("STAFF_NUMBER",$("#STAFF_NUMBER").val());
    CONTACTOR_INFOS.add(contactorMap);
    $("#CONTACTOR_INFOS").val(CONTACTOR_INFOS);

    $("#contactList ul").append(html);
    $("#contactList").css("display", "");

    hideFloatDetailInfo('addContact');
}

function showFloatDetailInfo(compId) {	
    $("#" + compId + "_float").attr("class", "c_float c_float-show");
}

function hideFloatDetailInfo(compId) {
    $("#" + compId + "_float").attr("class", "c_float");
}

function dealSubOfferDisplay(showSubOffers, offerIdArray, isNeedDisable) {
    for (var b = 0; b < showSubOffers.length; b++) {
        var tempDivEle = $($(showSubOffers[b]).children()[1]).children();
        var tempId = tempDivEle.attr("id");
        var flag = false;
        for (var j = 0; j < offerIdArray.length; j++) {
            if (tempId == offerIdArray[j]) {
                flag = true;
            }
        }
        // 如果不是可填写的数据则置灰并取消选择
        if (!flag) {
            tempDivEle.attr("disabled", "true");
            tempDivEle.get(0).checked = false;
            checkedProductOffer(document.getElementById(tempId));
        }
        // 再根据是否需要全部置灰选项将可选数据置灰并置为选择
        else {
            if (isNeedDisable) {
                tempDivEle.attr("disabled", "true");
                tempDivEle.get(0).checked = true;
            }
        }
    }
}

function dealOfferListBySvcState(offerStaList,merchOperCode) {
    var showOfferList = new Array();
    var offerNum = 0;
    for (var i = 0; i < offerStaList.length; i++) {
        var offerSta = offerStaList.get(i);
        if (merchOperCode == "4" && offerSta.get("OFFER_STA") == "N"){
            showOfferList[offerNum]=offerSta.get("OFFER_ID")+"_"+offerSta.get("OFFER_INDEX");
            offerNum++;
        }
    }
    return showOfferList;
}
function dealSubOfferShowPart(merchOperCode) {
    // 取得已经订购的子商品信息
    var subOffers = PageData.getData($("#class_" + $("#cond_OFFER_ID").val())).get("SUBOFFERS");
    
    // 暂时只处理恢复的场景
    if ("4" != merchOperCode){
        var offerIdArray = new Array();
        var n=0;//新增元素数量
        for (var i = 0; i < subOffers.length; i++) {
            var subOffer = subOffers.get(i);
            var operCode = subOffer.get("OPER_CODE");
            if(operCode && "0"==operCode){
            	n++;
            	continue;
            }
            offerIdArray[i-n] = subOffer.get("OFFER_ID")+"_"+subOffer.get("OFFER_INDEX");
        }

        return offerIdArray;
    }
    $.beginPageLoading("数据加载中......");
    var showOfferArray = new Array();
    Wade.httphandler.submit('',
        'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify',
        'qryOfferSta', '&SUBSCRIBER_LIST=' + subOffers, function(d){
            var offerStaList = d.map.result;
            showOfferArray = dealOfferListBySvcState(offerStaList,merchOperCode);
            $.endPageLoading();
        }, function(e, i) {
            $.endPageLoading();
            MessageBox.alert("操作失败");
            result = false;
        },{
            async : false
        });
    return showOfferArray;
}

function dealProdListByMerchOperCode() {
    var operCode = $("#cond_OPER_TYPE").val();
    
/*    // 成员业务暂时不做处理
    if (operCode == "memCrt" || operCode == "memChg" || operCode == "memDel") {
        return;
    }*/
    var merchOperCode = $("#OPERTYPE").val();
    var ispre = $("#IS_PRE").val();
    //预受理
    if("true" == ispre){
    	return;
    }
    // 变更商品资费、本地商品资费、的时候，只能操作已订购的产品
    if ("3"==merchOperCode || "4" == merchOperCode || "5" == merchOperCode || "55" == merchOperCode || "6" == merchOperCode || "10" == merchOperCode || "11" == merchOperCode ||
        "13" == merchOperCode || "9" == merchOperCode || "2" == merchOperCode) {
        var offerIdArray = dealSubOfferShowPart(merchOperCode);
        // 取得所有展示出来的子商品信息
        var showSubOffers = $("#productOfferUL").find("li");
        if ("3" == merchOperCode) {
            dealSubOfferDisplay(showSubOffers, offerIdArray, false);
        }
        else {
            dealSubOfferDisplay(showSubOffers, offerIdArray, true);
        }
    }
}
function merchInfoSubmit(handler) {
    //判断是否必填项是否填完
    if (!$.validate.verifyAll("BbossOfferParam")) {
        return false;
    }
    if (!validateBbossParam()) {
        return false;
    }
    
    submitBBOSSChaSpec();
    dealProdListByMerchOperCode();
    $("ul[id=offerChaSpecUL] li:nth-child(1)").children().eq("1").css("display", "none");
    backPopup(handler);
}

function validateBbossParam() {
    // 1.1、客户经理和订单提交人是否都已经填写
    var manageInfo = false;
    var bizOperatorInfo = false;
    for (var i = 0; i < CONTACTOR_INFOS.length; i++) {
        var contactorMap = CONTACTOR_INFOS.get(i);
        // 是否为客户经理信息
        if ("2" == contactorMap.get("CONTACTOR_TYPE_CODE")) {
            manageInfo = true;
        }
        // 是否为订单提交人信息
        if ("5" == contactorMap.get("CONTACTOR_TYPE_CODE")) {
            bizOperatorInfo = true;
        }
    }
    // 1.2、合同信息是否已经填写
    var operCode = $("#cond_OPER_TYPE").val();
    var contractInfo = true;
    // 1.3 预受理将合同信息清空
    if ("true" == $("#IS_PRE").val()){
        ATT_INFOS = new Wade.DatasetList();
    }
    if ("true" != $("#IS_PRE").val() && (operCode == "CrtUs" || operCode =="preCrt")) {
        contractInfo = false;
        for (var i = 0; i < ATT_INFOS.length; i++) {
            // 判断是否为合同附件
            contractInfo = ATT_INFOS.get(i).get("ATT_TYPE_CODE") == "1" ? true : false;
            if (contractInfo)
            {
                break;
            }
        }
    }

    // 2、是否校验通过，不通过提示校验信息
    if (!manageInfo && operCode!="CrtMb" && operCode!="ChgMb" && operCode!="DstMb") {
        MessageBox.alert("提示信息","请填写联系人中的客户经理信息！");
        return false;
    }
    if (!bizOperatorInfo && operCode!="CrtMb" && operCode!="ChgMb" && operCode!="DstMb") {
        MessageBox.alert("提示信息","请填写联系人中的订单提交人信息！");
        return false;
    }
    if (!contractInfo && operCode!="CrtMb" && operCode!="ChgMb" && operCode!="DstMb" && operCode!="DstUs") {
        MessageBox.alert("提示信息","请填写合同附件信息！");
        return false;
    }
    //3合同变更是否重新上传了合同
    var opertype=$("#OPERTYPE").val();
    if(opertype=="21"&&ATT_INFOS.length==0){
    	MessageBox.alert("提示信息","合同变更操作请填写合同附件信息！");
        return false;
    }
    return true;
}

function queryStaffInfo() {
	var sfaffId=$("#CONTACTOR_ID").val();
    $.beginPageLoading("数据加载中......");
	$.ajax.submit("", "queryStaffInfo", "STAFF_ID="+sfaffId, "", function(data){
        $.endPageLoading();
		var access_number = data.get("ACCESS_NUMBER");
		var bbossOpId = data.get("OP_BBOSS_ID");
        if (!access_number){
            MessageBox.alert("该员工未同步bboss总部，请核实！");
        }
		 $("#CONTACTOR_PHONE").val(access_number);
		 $("#STAFF_NUMBER").val(bbossOpId);

	},
	function(error_code,error_info){
		showDetailErrorInfo(error_code,error_info);
        $.endPageLoading();
        MessageBox.error("错误提示",error_info,null, null);
    });
}

/**
 * 当不自动续约时 续约周期和续约时间不显示
 * @param e
 */
function checkIsAutoRecont(e){
	var isIsAutoRecont=$(e).attr("value");
	if("0"==isIsAutoRecont){
		 $("#RecontTime").css("display", "none");
	}else{
		 $("#RecontTime").css("display", "");
	}
}

function openStaffPopup(fieldName)
{
	$("#staffSelFrame").contents().find("#field_name").val(fieldName);
	showPopup('staffPicker','staffPickerHome');
}

/**员工组件选择后查询员工联系电话和总部用户名*/
function queryBBossStaffInfo(){
		var staffId = $("#CONTACTOR_ID").val();
		if (staffId == "") {
			MessageBox.alert("提示信息","请指定具体人员！");
			return "false";
		}
		var flag = "true";
		Wade.httphandler.submit('',
		        'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify',
		        'queryStaffNumber', "STAFF_ID=" + staffId, function(data){
			if ($("#CONTACTOR_TYPE_CODE").val() == 5 && $("#STAFF_NUMBER")) {
				var staffNumber = data.map.STAFF_NUMBER;
				if (staffNumber == "") {
					MessageBox.alert("提示信息","该员工未同步总部用户名，请重新选择！");
					flag = "false";
					return;
				}
				$("#STAFF_NUMBER").val(staffNumber);
			}
			var serialNumber = data.map.STAFF_PHONE;
			$("#CONTACTOR_ID_SN").val(serialNumber);
		        }, function(e, i) {
		            $.endPageLoading();
		            MessageBox.alert("操作失败");
		            flag = "false";
		        },{ async : false});
		return flag;
}
