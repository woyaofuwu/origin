--IS_CACHE=Y
SELECT /*+INDEX(a PK_TD_M_RES_COMMPARA)*/  
eparchy_code,para_attr,para_value2,para_value3,para_value4,para_value5,para_value6,
to_char(para_value10) para_value10,to_char(para_value11) para_value11,to_char(para_value12) para_value12,to_char(rdvalue1,'yyyy-mm-dd hh24:mi:ss') rdvalue1,to_char(rdvalue2,'yyyy-mm-dd hh24:mi:ss') rdvalue2,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id ,
           F_RES_GETCODENAME('area_code',:PARA_CODE1,  '','') para_name, --业务区名称
         F_RES_GETCODENAME('depart_id',a.para_code1,'','') para_value10, --操作点名称
         a.para_code1, --操作点编码
         F_RES_GETCODENAME('res_kind',:EPARCHY_CODE, '1', a.para_code2) para_code2, --卡类型
         a.para_value7,  --号段
         a.para_value8, --面值 
         a.para_value1, --库存数量 
         a.para_value9   --金额
   from td_m_res_commpara a
   where a.eparchy_code=:EPARCHY_CODE
     and a.para_attr=:PARA_ATTR
     and (a.para_code1=:PARA_CODE1
        OR a.para_code1 IN (SELECT depart_id FROM td_m_depart WHERE area_code=:PARA_CODE1) )
     and (:SIM_TYPE_CODE is null or a.para_code2=:SIM_TYPE_CODE)
     and (:ALUE_CODE is null or a.para_value8=:ALUE_CODE)