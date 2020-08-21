function queryTpOrderInfos()
{
    //查询条件校验
    if(!$.validate.verifyAll("QueryPart"))
    {
        return false;
    }
    $.beginPageLoading("正在查询数据...");
    //播记录查询
    $.ajax.submit('QueryPart','queryTpOrderInfos', null, 'QueryListPart', function(data)
        {
            $.endPageLoading();
        },
        function(error_code,error_info)
        {
            $.endPageLoading();
            alert(error_info);
        });
}

function openDetail(tableIndex) {
    var rowData = resSelBoxTable.getRowData(tableIndex);
    var param = '&ORDER_ID=' + rowData.get("ORDER_ID");
    popupDialog('甩单详情信息','tp.QueryTpOrderDetail', 'queryTpOrderDetail',param, null, '80%', '300px', true, null, null);
}

function submitAction(obj){
    if (!$.validate.verifyAll(obj)) {
        return;
    }

    var tableData = resSelBoxTable.getCheckedRowsData("TAB_CHECK");
    if(null == tableData || ''==tableData || '[]' == tableData){
        MessageBox.alert("请选择一条数据才能操作！");
        return;
    }

    ajaxSubmit(obj,'submitAction',"&TABLE_INFOS="+tableData,'',function(data){
        $.endPageLoading();
        MessageBox.success("成功提示",data.get('SUCCESS'),function(btn){
            if("ok" == btn){
                window.location.reload();
            }
        });
    }, function(code, info, detail) {
        $.endPageLoading();
        MessageBox.error("错误提示", info, function(btn) {
        }, null, detail);
    });

}