INSERT INTO tf_b_trade_discnt(TRADE_ID,ACCEPT_MONTH,USER_ID,USER_ID_A,PACKAGE_ID,PRODUCT_ID,DISCNT_CODE,
           SPEC_TAG,RELATION_TYPE_CODE,INST_ID,CAMPN_ID,START_DATE,END_DATE,MODIFY_TAG,UPDATE_TIME,
           UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,
           RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,
           RSRV_TAG2,RSRV_TAG3)
  SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(substrb(:TRADE_ID, 5, 2)),TO_NUMBER(:USER_ID),-1,
         :PACKAGE_ID,:PRODUCT_ID,TO_NUMBER(a.para_code3),
         0,null,to_number(:INST_ID),:CAMPN_ID,TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),
         TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),:MODIFY_TAG,sysdate,
         :UPDATE_STAFF_ID,:UPDATE_DEPART_ID,null,null,null,null,null,null,
         null,null,null,null,null,null,null,null,null,null,null
    FROM td_s_commpara a
  where a.subsys_code=:SUBSYS_CODE
       AND a.param_attr=:PARAM_ATTR
       AND a.param_code=:PARAM_CODE
       AND a.para_code2=:PARA_CODE2
       AND (a.para_code1=:PARA_CODE1 OR para_code1='*')
       AND a.para_code4=:PARA_CODE4
       AND (sysdate BETWEEN start_date AND end_date )
       AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')
       AND NOT EXISTS (SELECT 1 FROM tf_b_trade_discnt
                        WHERE trade_id = TO_NUMBER(:TRADE_ID)
                          AND accept_month = TO_NUMBER(substrb(:TRADE_ID, 5, 2))
                          AND discnt_code = a.para_code3
                          AND modify_tag = :MODIFY_TAG)
      AND NOT EXISTS (SELECT 1 FROM tf_f_user_discnt
                       WHERE PARTITION_ID=mod(to_number(:USER_ID),10000)
                         AND user_id = TO_NUMBER(:USER_ID)
                         AND discnt_code = a.para_code3
                         AND sysdate<end_date)