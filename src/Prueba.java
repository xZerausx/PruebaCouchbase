public class Prueba {

	public static void main(String[] args) {
		
		System.out.println("Inicio PruebaQuery");

		ValkiriaQuery valkN1ql = new ValkiriaQuery();	
		
		System.out.println("#");
//  	{"ACC_ID":"675947329","CAC_CLU_CELLULAR_NUMBER":"3512335565","CLU_BILL_NUMBER":"3512335565","CLU_CBT_ID":"CO","CST_CYC_ID":"22"}		
		System.out.println(valkN1ql.find("ACC_TMP_FORMATTER","3512335565")); // OK
		System.out.println("#");
		System.out.println(valkN1ql.find("ACC_TMP_FORMATTER","351")); // [], valor no encontrado (isempty = true)	
		System.out.println("#");
//		{"MATCH_CENTRAL":"1","MATCH_LARGO_DESDE":"13","MATCH_LARGO_HASTA":"30","MATCH_NUMERO":"B","MATCH_PREFIJO":"222","MATCH_RUTA":"RUTA_CAN0","RET_DESDE":"3","RET_EXIT":"0","RET_LARGO":"30","RET_PREFIJO":"","RET_SUFIJO":"","RET_TIPO_NUMERO":"LOCAL"}	
		System.out.println(valkN1ql.find("TF_NORM_RULES","1,B,13,RUTA_CAN0,2223512335565")); // OK
		System.out.println("#");
//		{"MATCH_CENTRAL":"1","MATCH_LARGO_DESDE":"0","MATCH_LARGO_HASTA":"30","MATCH_NUMERO":"B","MATCH_PREFIJO":"0670","MATCH_RUTA":"RUTA_OTH","RET_DESDE":"0","RET_EXIT":"1","RET_LARGO":"30","RET_PREFIJO":"","RET_SUFIJO":"","RET_TIPO_NUMERO":"LDN"}		
//		{"MATCH_CENTRAL":"1","MATCH_LARGO_DESDE":"0","MATCH_LARGO_HASTA":"30","MATCH_NUMERO":"B","MATCH_PREFIJO":"06","MATCH_RUTA":"RUTA_OTH","RET_DESDE":"0","RET_EXIT":"1","RET_LARGO":"30","RET_PREFIJO":"","RET_SUFIJO":"","RET_TIPO_NUMERO":"LDN"}		
		System.out.println(valkN1ql.find("TF_NORM_RULES","1,B,15,RUTA_OTH,067035122335565")); // OK
		System.out.println("#");
		System.out.println(valkN1ql.find("TF_NORM_RULES","1,A")); // null, parametros incompletos
		System.out.println("#");
//		{"SAR_DESCRIPTION":"IDN89","SAR_END_DATE":"39991231235900","SAR_HOMMER_FLAG":"N","SAR_ID":"2059","SAR_IP_FROM":"103010066192","SAR_IP_TO":"103010066207","SAR_START_DATE":"20130101000000"}		
		System.out.println(valkN1ql.find("SGSN_ADDRESS_RANGE","103010066193,20190109000000")); // OK
		System.out.println("#");
//		{"SAR_DESCRIPTION":"PERMO","SAR_END_DATE":"39991231235900","SAR_HOMMER_FLAG":"N","SAR_ID":"2028","SAR_IP_FROM":"200004247100","SAR_IP_TO":"200004247191","SAR_START_DATE":"20130101000000"}
//		{"SAR_DESCRIPTION":"PERMO","SAR_END_DATE":"39991231235900","SAR_HOMMER_FLAG":"N","SAR_ID":"2028","SAR_IP_FROM":"200004247176","SAR_IP_TO":"200004247191","SAR_START_DATE":"20130101000000"}
		System.out.println("   "+valkN1ql.find("SGSN_ADDRESS_RANGE","200004247101,20190109000000"));// Rango solapado, consulta antes de solapar
		System.out.println("   "+valkN1ql.find("SGSN_ADDRESS_RANGE","200004247180,20190109000000"));// Rango Solapado, consulta solapamiento
		
		valkN1ql.close();
		
		System.out.println("Fin PruebaQuery");
	}
}
