import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.x;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;

public class ValkiriaQuery {

	private List<N1qlQueryRow> ValkN1qlRow;
	private String querySelect;
	private String queryWhere;
	private String queryLimits;
	private String queryLongest;
	private String queryAny;
	private int cnttmp = 0;
	private List<N1qlQueryRow> ValkN1qlLookupBucket;
    public int ctrValores;
    private CouchbaseManager valk;
	private String filehost;
	private String fileuser;
	private String filepass;
 
	
	public ValkiriaQuery () 
	{	
		try {
			viewFile("C:\\Users\\CTI8820\\Desktop\\ProyectoDW\\Valkiria.ini");
			this.valk = new CouchbaseManager(filehost,fileuser,filepass);
		} catch (IOException e) {
			this.valk = new CouchbaseManager("localhost","Administrator","admin123");	
		}	
	
		this.valk.openCluster();
		this.valk.openBucket("LOOKUPBUCKET");	
		Statement statementBucket = select("NAME,MATCH,`RETURN`,LIMITS,`ANY`,LONGEST,LONGESTANY, ARRAY_LENGTH(MATCH) CNT_MATCH, ARRAY_LENGTH(LIMITS) CNT_LIMITS, ARRAY_LENGTH(`ANY`) CNT_ANY, ARRAY_LENGTH(LONGEST) CNT_LONGEST, ARRAY_LENGTH(LONGESTANY) CNT_LONGESTANY")
	            .from(i("LOOKUPBUCKET"));
		ValkN1qlLookupBucket = valk.ValkQueryResult(statementBucket).allRows();	
	/*	for (N1qlQueryRow rowbkt : ValkN1qlLookupBucket) { 	 
			System.out.println (rowbkt.value());
	    }*/
	}
	
	public List<N1qlQueryRow> find (String findBucket,String findValue ) {
		querySelect= "";queryWhere="";queryLimits="";queryLongest="";queryAny="";
		ValkN1qlRow = null;
		cnttmp = 0;

		String[] parts = findValue.split(",");
		int cntValores = parts.length;
		ctrValores = 0;
		
		for (N1qlQueryRow row : ValkN1qlLookupBucket) 
	    { 	if (row.value().getString("NAME").equals(findBucket)) {
			
			JsonObject jsonObject = row.value();
			JsonDocument docs = JsonDocument.create("ID1", jsonObject);	
			
			
			if (docs.content().getInt("CNT_MATCH")!=null ) {ctrValores = ctrValores + docs.content().getInt("CNT_MATCH");}
			if (docs.content().getInt("CNT_LIMITS")!=null ) {ctrValores = ctrValores + docs.content().getInt("CNT_LIMITS");}
			if (docs.content().getInt("CNT_ANY")!=null ) {ctrValores = ctrValores + docs.content().getInt("CNT_ANY");}
			if (docs.content().getInt("CNT_LONGEST")!=null ) {ctrValores = ctrValores + docs.content().getInt("CNT_LONGEST");}
			if (docs.content().getInt("CNT_LONGESTANY")!=null ) {ctrValores = ctrValores + docs.content().getInt("CNT_LONGESTANY");}
			
		
			if (ctrValores== cntValores) {
				
			
			armaQuerySelect ( docs.content().getArray("RETURN"));
			armaQueryMatch ( docs.content().getArray("MATCH"), parts);				
			armaQueryLimits ( docs.content().getArray("LIMITS"), parts);
			armaQueryAny ( docs.content().getArray("ANY"), parts);
			armaQueryLongest  ( docs.content().getArray("LONGEST"), parts);
			armaQueryLongestAny  ( docs.content().getArray("LONGESTANY"), parts);
			
			Statement statementFind = select(querySelect).from(i(findBucket)).where(x(queryWhere));
//			System.out.println (statementFind);
			
			ValkN1qlRow =  valk.ValkQueryResult(statementFind).allRows();;
			
			/*for (N1qlQueryRow rowFind : valk.ValkQueryResult(statementFind).allRows()) 
		    { 	
				ValkN1qlRow = rowFind;
				StringCnt = rowFind.value().size();	
				StringName = rowFind.value().getNames();	
				ToMap = rowFind.value().toMap();
		    }*/
			
			}
        }
	    }
        return ValkN1qlRow; 
     }

	public void armaQuerySelect(JsonArray jsonArray) {
        querySelect = jsonArray.getString(0);
    	for (int i= 1; i < jsonArray.size(); i++) {
    		querySelect = querySelect +", "+ jsonArray.getString(i);
    	}
	}
	
	public void armaQueryMatch(JsonArray jsonArray, String[] parts) {
		if (jsonArray != null) {
	    	for (int i= 0; i < jsonArray.size(); i++) {
	    		if (i > 0) { queryWhere = queryWhere + " AND " ; }
	    		queryWhere = queryWhere + jsonArray.getString(i) + " = \"" + parts[cnttmp]+"\" ";
	    		cnttmp ++;
	    	}			
		}else {
			queryWhere = " 1 = 1";
		}
	}
	
	public void armaQueryLimits(JsonArray jsonArray, String[] parts) {
		if (jsonArray != null) {
	    	for (int i= 0; i < jsonArray.size(); i++) {
	    		queryLimits = queryLimits +" AND \"" + parts[cnttmp] + "\" BETWEEN " + jsonArray.getObject(i).getString("FROM") + " AND " + jsonArray.getObject(i).getString("TO") ;
	    		cnttmp ++;
	    	}			
		}
		if (queryLimits != "") {queryWhere = queryWhere + queryLimits;}	
	}

	public void armaQueryAny(JsonArray jsonArray, String[] parts) {
		if (jsonArray != null) {
	    	for (int i= 0; i < jsonArray.size(); i++) {
	    		queryAny = queryAny + " AND " + jsonArray.getString(i) + " IN [\"" + parts[cnttmp] + "\", \"*\"]";
	    		cnttmp ++;
	    	}		
		}
		if (queryAny != "") {queryWhere = queryWhere + queryAny;}	
	}
	
	public void armaQueryLongest(JsonArray jsonArray, String[] parts) {
		if (jsonArray != null) {
	    	for (int i= 0; i < jsonArray.size(); i++) {
	    		queryLongest = queryLongest + " AND REVERSE(" + jsonArray.getString(i) + ") IN SUFFIXES(REVERSE(\"" + parts[cnttmp] + "\"))";
	    		cnttmp ++;
	    	}		
		}
		if (queryLongest != "") {queryWhere = queryWhere + queryLongest;}		
	}
	
	public void armaQueryLongestAny(JsonArray jsonArray, String[] parts) {
		if (jsonArray != null) {
	    	for (int i= 0; i < jsonArray.size(); i++) {
	    		queryLongest = queryLongest + " AND ( REVERSE(" + jsonArray.getString(i) + ") IN SUFFIXES(REVERSE(\"" + parts[cnttmp] + "\")) OR "+ jsonArray.getString(i) + " = \"*\")";
	    		cnttmp ++;
	    	}		
		}
		if (queryLongest != "") {queryWhere = queryWhere + queryLongest;}		
	}

	public void close() {
		 valk.closeBucket();
		 valk.closeCluster();   
	}
	
    public void viewFile(String archivo) throws FileNotFoundException, IOException {
        String cadena;
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        while((cadena = b.readLine())!=null) {        	
            String[] fileini = cadena.split(":");
            if (fileini[0].equals("Clusters")) {
            	filehost = fileini[1];
            }
            if (fileini[0].equals("User")) {
            	fileuser = fileini[1];
            }
            if (fileini[0].equals("Pass")) {
            	filepass = fileini[1];
            }
        }
        b.close();
    }


	
}