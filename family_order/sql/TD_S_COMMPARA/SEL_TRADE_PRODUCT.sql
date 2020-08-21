SELECT f_sys_getcodename('product_id',PRODUCT_ID,NULL,NULL) para_code1,f_sys_getcodename('product_id',OLD_PRODUCT_ID,NULL,NULL) para_code2,
       decode(MODIFY_TAG,'0','增加','1','删除','2','修改','其他') para_code3,
       to_char(start_date,'yyyy-mm-dd hh24:mi:ss') para_code4,
       to_char(end_date,'yyyy-mm-dd hh24:mi:ss') para_code5,
       '' para_code6,
       '' para_code7,
       '' para_code8,'' para_code9,
       '' para_code10,
       '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
       '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
       '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
       '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM TF_B_TRADE_PRODUCT
 WHERE trade_id = TO_NUMBER(:TRADE_ID)