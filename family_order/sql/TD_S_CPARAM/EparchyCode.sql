--IS_CACHE=Y
SELECT 'EparchyCode' KEY,AREA_NAME VALUE1,'-1' VALUE2, AREA_CODE VRESULT FROM TD_M_AREA
 WHERE 'EparchyCode'=:KEY