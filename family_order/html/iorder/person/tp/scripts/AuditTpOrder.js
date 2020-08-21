function queryUncheckOrder()
{
    //查询条件校验
    if(!$.validate.verifyAll("QueryPart"))
    {
        return false;
    }
    $.beginPageLoading("正在查询数据...");
    //播记录查询
    $.ajax.submit('QueryPart','queryUncheckOrder', null, 'QueryListPart', function(data)
        {
            $.endPageLoading();
        },
        function(error_code,error_info)
        {
            $.endPageLoading();
            alert(error_info);
        });
}

function openAudit(){

    var tableData = resSelBoxTable.getCheckedRowsData("TAB_CHECK");
    if(null == tableData || ''==tableData || '[]' == tableData){
        MessageBox.alert("请选择一条数据才能审核！");
        return;
    }

    popupDialog('甩单审核','tp.AuditPage', 'initAduit',"&TABLE_INFOS="+tableData, null, '80%', '300px', true, null, null);

}

function openDetail(tableIndex) {
    var rowData = resSelBoxTable.getRowData(tableIndex);
    var param = '&ORDER_ID=' + rowData.get("ORDER_ID");
    popupDialog('甩单审核','tp.AuditPage', 'initAduit',param, null, '80%', '300px', true, null, null);
    // showPopup('myPopup','myPopup-item');
}


function auditBatchTpOrder(obj){
    if(!$.validate.verifyAll(obj))
    {
        return false;
    }
    //$.ajax.submit(obj,'auditTpOrder', '', 'submitPart');
    $.beginPageLoading("正在提交数据...");
    ajaxSubmit(obj,'auditBatchTpOrder','','',function(data){
        $.endPageLoading();
        MessageBox.success("成功提示",data.get('SUCCESS'),function(btn){
            if("ok" == btn){
                window.parent.location.reload();
            }
        });
    }, function(code, info, detail) {
        $.endPageLoading();
        MessageBox.error("错误提示", info, function(btn) {
        }, null, detail);
    });

}

