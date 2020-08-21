UPDATE TF_B_CHL_RESSALE_LOG
   SET 
       END_RES_NO = decode(:END_RES_NO,null,end_res_no,:END_RES_NO),
       RES_TYPE_CODE = decode(:RES_TYPE_CODE,null,res_type_code,:RES_TYPE_CODE),
       RES_KIND_CODE = decode(:RES_KIND_CODE,null,res_kind_code,:RES_KIND_CODE),
       STOCK_ID = decode(:STOCK_ID,null,stock_id,:STOCK_ID),
       SALE_NUM = decode(:SALE_NUM,null,sale_num,to_number(:SALE_NUM)),
       VALUE_CODE = decode(:VALUE_CODE,null,value_code,:VALUE_CODE),
       SALE_TIME = decode(:SALE_TIME,null,sale_time,to_date(:SALE_TIME,'yyyy-mm-dd hh24:mi:ss')),
       REMARK = :REMARK      
 WHERE LOG_ID = to_number(:LOG_ID)
   AND (:START_RES_NO IS NULL OR START_RES_NO = :START_RES_NO)