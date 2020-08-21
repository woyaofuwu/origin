--IS_CACHE=Y
SELECT eparchy_code,res_type_code,depart_id,depart_code,depart_kind_code,depart_name,depart_frame,valid_flag,area_code,parent_depart_id,manager_id,order_no,to_char(warnning_value_u) warnning_value_u,to_char(warnning_value_d) warnning_value_d,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,depart_level,stock_level,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_num1,rsrv_num2,rsrv_num3,remark
  FROM td_s_assignrule a
 WHERE eparchy_code=:EPARCHY_CODE
   AND res_type_code=:RES_TYPE_CODE
   AND depart_code>=:DEPART_CODE_S
   AND depart_code<=:DEPART_CODE_E
   AND valid_flag=:VALID_FLAG
   AND area_code=:AREA_CODE
   AND start_date<=SYSDATE
   AND (end_date>=SYSDATE OR end_date IS NULL)
   AND EXISTS (SELECT b.depart_kind_code
                 FROM TD_M_DEPARTKIND b
                WHERE b.code_type_code='0'
                  AND b.eparchy_code=:EPARCHY_CODE
                  AND b.depart_kind_code=a.depart_kind_code
               )
   ORDER BY depart_code