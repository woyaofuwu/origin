--IS_CACHE=Y
SELECT to_char(alliance_id) alliance_id,alliance_name,alliance_type_code,area_code,cooperationinfo,linkphone,address,linkaman,alliancestate,deploystate,to_char(applytime,'yyyy-mm-dd hh24:mi:ss') applytime,assessor,to_char(assesstime,'yyyy-mm-dd hh24:mi:ss') assesstime,alliance_rank,favourabletype,remark,offinfo,allianceinfo,zklimit 
  FROM tf_m_alliance
 WHERE (alliance_name like '%'||:ALLIANCE_NAME||'%' or :ALLIANCE_NAME is null)
   AND (alliance_type_code=:ALLIANCE_TYPE_CODE or :ALLIANCE_TYPE_CODE is null)
   AND (area_code=:AREA_CODE or :AREA_CODE is null)
   AND (alliancestate=:ALLIANCESTATE or :ALLIANCESTATE is null) 
   AND (alliance_rank=:ALLIANCE_RANK or :ALLIANCE_RANK is null)
   ORDER by degree_score desc