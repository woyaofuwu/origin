function resetData(){
	$("#SERIAL_NUMBER").val("");
	$("#NUMBER_TYPE").val("");
	$("#SHORT_NUMBER").val("");
	$("#PACKAGE_MESSAGE").val("");
	$("#REMARK").val("");
}
function onSubmit(obj){
    if(!$.validate.verifyAll("onsubmitPart")){
        return false;
    }
    var lineInfos = LINE_INFOS.getData(true,'SERIAL_NUMBER,NUMBER_TYPE,SHORT_NUMBER,PACKAGE_MESSAGE,REMARK');

    for (var i = 0; i < lineInfos.length; i++) {
        var lineInfo = lineInfos.get(i);
        var j=i+1;
        if(lineInfo.get("SERIAL_NUMBER")==""){
            MessageBox.alert("温馨提示","表格第"+j+"行的成员号码不能为空");
            return false;
        }
        if(lineInfo.get("NUMBER_TYPE")==""){
            MessageBox.alert("温馨提示","表格第"+j+"行的号码类型不能为空");
            return false;
        }
        if(lineInfo.get("SHORT_NUMBER")==""){
            MessageBox.alert("温馨提示","表格第"+j+"行的短号不能为空");
            return false;
        }
        if(lineInfo.get("PACKAGE_MESSAGE")==""){
            MessageBox.alert("温馨提示","表格第"+j+"行的套餐信息不能为空");
            return false;
        }

    }
    $.beginPageLoading("数据加载中......");
    $.ajax.submit("onsubmitPart", "onSubmit", "&LINE_INFOS="+lineInfos, "", function(data){
            $.endPageLoading();
            var ret={};
            var tempArchiveData=new Wade.DataMap();
            tempArchiveData.put("CONTRACT_NUMBER",data.get('CONTRACT_NUMBER'));
            tempArchiveData.put("CONTRACT_WRITE_DATE",data.get('CONTRACT_WRITE_DATE'));
            tempArchiveData.put("CONTRACT_END_DATE",data.get('CONTRACT_END_DATE'));
            tempArchiveData.put("ARCHIVE_ID",data.get('ARCHIVE_ID'));
            tempArchiveData.put("ARCHIVES_NAME",data.get('ARCHIVES_NAME'));
            tempArchiveData.put("AGREEMENT_DEF_ID",data.get('AGREEMENT_DEF_ID'));
            tempArchiveData.put("URL",data.get('URL'));
            ret.TEMP_ARCHIVE_DATA=tempArchiveData;
            setPopupReturnValue(this,ret ,false);
            MessageBox.success("温馨提示", "操作成功", function(btn){
                hidePopup(obj);
            });
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        });
}

function onPrint(obj){
    var lineInfos = LINE_INFOS.getData(true,'SERIAL_NUMBER,NUMBER_TYPE,SHORT_NUMBER,PACKAGE_MESSAGE,REMARK');

    $.beginPageLoading("数据加载中......");
    $.ajax.submit("onsubmitPart", "onPrint", "&LINE_INFOS="+lineInfos, "", function(data){
            $.endPageLoading();
            var fileId = data.get("FILE_ID");
            popupPage('合同预览', 'igroup.minorec.elecagreement.ElecContractPreview', 'init', '&FILE_ID='+fileId, null, 'full', '', '');
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        });
}

function addData(obj){
    if(!$.validate.verifyAll("editPart")){
        return false;
    }

    if($("#BUTTONSTATE").val() == "add"){
        LINE_INFOS.addRow({
            "DELETE":"<span ontap=\"deleteOneRowData();\" class=\"e_ico-delete\"></span>",
            "SERIAL_NUMBER":$("#SERIAL_NUMBER").val(),
            "NUMBER_TYPE":$("#NUMBER_TYPE").val(),
            "SHORT_NUMBER":$("#SHORT_NUMBER").val(),
            "PACKAGE_MESSAGE":$("#PACKAGE_MESSAGE").val(),
            "REMARK":$("#REMARK").val()

        });
    }else{
    	if($("#SYNCHUPDATE").val()=="false"){
    		LINE_INFOS.updateRow({
    			"DELETE":"<span ontap=\"deleteOneRowData();\" class=\"e_ico-delete\"></span>",
    			"SERIAL_NUMBER":$("#SERIAL_NUMBER").val(),
    			"NUMBER_TYPE":$("#NUMBER_TYPE").val(),
    			"SHORT_NUMBER":$("#SHORT_NUMBER").val(),
    			"PACKAGE_MESSAGE":$("#PACKAGE_MESSAGE").val(),
    			"REMARK":$("#REMARK").val()
    		},LINE_INFOS.selected);
    	}else{
    		var maxLine = LINE_INFOS.getData(true).length;
    		var rowData = LINE_INFOS.getSelectedRowData();
    		updateCol($("#SERIAL_NUMBER").val(),rowData.get("SERIAL_NUMBER"),maxLine,"SERIAL_NUMBER");
    		updateCol($("#NUMBER_TYPE").val(),rowData.get("NUMBER_TYPE"),maxLine,"NUMBER_TYPE");
    		updateCol($("#SHORT_NUMBER").val(),rowData.get("SHORT_NUMBER"),maxLine,"SHORT_NUMBER");
    		updateCol($("#PACKAGE_MESSAGE").val(),rowData.get("PACKAGE_MESSAGE"),maxLine,"PACKAGE_MESSAGE");
    		updateCol($("#REMARK").val(),rowData.get("REMARK"),maxLine,"REMARK");
    	}
    }
    hidePopup(obj);
}

function showEditPopup(synchupdate){
    var obj = LINE_INFOS.getSelectedRowData();

    if(obj == null || obj == undefined){
        MessageBox.alert("温馨提示","请先选择您要修改的数据？");
        return false;
    }

    $("#SYNCHUPDATE").val(synchupdate);
    $("#BUTTONSTATE").val("edit");

    $("#SERIAL_NUMBER").val(obj.get("SERIAL_NUMBER"));
    $("#NUMBER_TYPE").val(obj.get("NUMBER_TYPE"));
    $("#SHORT_NUMBER").val(obj.get("SHORT_NUMBER"));
    $("#PACKAGE_MESSAGE").val(obj.get("PACKAGE_MESSAGE"));
    $("#REMARK").val(obj.get("REMARK"));
    showPopup('tradeTablepopup','tradeTablepopup_item',true);
}









