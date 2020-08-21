UPDATE td_s_res_tradetype
   SET res_trade_type=:RES_TRADE_TYPE,remark=:REMARK,update_time=SYSDATE,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  
 WHERE eparchy_code=:EPARCHY_CODE
   AND res_type_code=:RES_TYPE_CODE
   AND res_trade_type_code=:RES_TRADE_TYPE_CODE