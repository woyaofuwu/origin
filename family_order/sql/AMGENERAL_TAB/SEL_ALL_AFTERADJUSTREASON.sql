--IS_CACHE=Y
select adjust_reason_id RSRV_NUM1,
       0 RSRV_NUM2,
       0 RSRV_NUM3,
       adjust_reason PARA_CODE1,
       '' PARA_CODE2,
       '' PARA_CODE3,
       '' PARA_CODE4,
       '' PARA_CODE5,
       '' PARA_CODE6,
       '' PARA_CODE7,
       '' PARA_CODE8,
       '' PARA_CODE9
  from TD_A_ADJUSTREASON
 where eparchy_code =
       (select area_code
          from td_m_area
         where this_tag = '1' and AREA_LEVEL = 20)