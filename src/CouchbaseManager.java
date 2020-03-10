import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.Statement;

public class CouchbaseManager {
	private Bucket bucket;
	private Cluster cluster;
	private N1qlQueryResult ValkQueryResult;
	private String host;
	private String user;
	private String pass;
	
	public CouchbaseManager() {
		this.host = "127.0.0.1";
		this.host = "Administrator";
		this.user = "admin123";
	}
	
	public CouchbaseManager(String host, String user, String pass ) {
		this.host = host;
		this.user = user;
		this.pass = pass;
	}
	
	
	public void openCluster() { //Setter cluster
		CouchbaseEnvironment env = DefaultCouchbaseEnvironment.builder().connectTimeout(10000).build();
		cluster = CouchbaseCluster.create(env,this.host).authenticate(this.user, this.pass);

	}
	
	public void openBucket(String bucketname) {
	bucket =  (bucket != null) ? bucket : cluster.openBucket(bucketname);
	}
	
	public N1qlQueryResult ValkQueryResult( Statement statement) { // SETTER result 
		ValkQueryResult = bucket.query(N1qlQuery.simple(statement));     
	    if (ValkQueryResult.finalSuccess()) {
	    	return ValkQueryResult;
	    }else {return null;}      
	}
	
	public void closeCluster() { 
		cluster.disconnect();
	}

	public void closeBucket() {
		bucket.close();
	}
			
}


