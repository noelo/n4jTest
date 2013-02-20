package com.acme.gps.test;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.server.WrappingNeoServerBootstrapper;

public class BuildAndTestDB {

	private static EmbeddedGraphDatabase db = null;
	private static WrappingNeoServerBootstrapper srv = null;

	private void startupServer() {
		db = new EmbeddedGraphDatabase("/tmp/neo4jdb/");
		srv = new WrappingNeoServerBootstrapper(db);
		srv.start();
	}

	private void stopServer() {
		srv.stop();
		db.shutdown();
	}

	private Node createVM(String vmID, String vmName, String osType,
			int cpuCount, boolean license, GraphDatabaseService db) {
		Node currNode = db.createNode();
		currNode.setProperty("name", "VCD-VM");
		currNode.setProperty("vmID", vmID);
		currNode.setProperty("vmName", vmName);
		currNode.setProperty("osType", osType);
		currNode.setProperty("cpuCount", cpuCount);
		currNode.setProperty("licensed", license);
		return currNode;
	}

	private Node createAppContainer(String acntrID, String acntrName,
			GraphDatabaseService db) {
		Node currNode = db.createNode();
		currNode.setProperty("name", "ApplicationContainer");
		currNode.setProperty("acntrID", acntrID);
		currNode.setProperty("acntrName", acntrName);
		return currNode;
	}

	private Node createNIC(String macID, String ipAddr, String subnetMask,
			GraphDatabaseService db) {
		Node currNode = db.createNode();
		currNode.setProperty("name", "NetworkCard");
		currNode.setProperty("macID", macID);
		currNode.setProperty("ipAddr", ipAddr);
		currNode.setProperty("subnetMask", subnetMask);
		return currNode;
	}

	public void PopulateDatabase() throws Throwable {

		Transaction tx = db.beginTx();

		try {
			Node owner = db.createNode();
			owner.setProperty("name", "Owner");
			owner.setProperty("id", "CloudTeam");

			Node businessGroupNode = db.createNode();
			businessGroupNode.setProperty("name", "BusinessGroup");
			businessGroupNode.setProperty("id", "Client Business Group1");

			owner.createRelationshipTo(businessGroupNode,
					DynamicRelationshipType.withName("MEMBER_OF"));

			Node businessServiceNode = db.createNode();
			businessServiceNode.setProperty("name", "BusinessService");
			businessServiceNode.setProperty("id", "Business Service 1");
			businessGroupNode.createRelationshipTo(businessServiceNode,
					DynamicRelationshipType.withName("MANAGES"));

			owner.createRelationshipTo(businessServiceNode,
					DynamicRelationshipType.withName("MEMBER_OF"));

			Node environment1 = db.createNode();
			environment1.setProperty("name", "Production");
			Node environment2 = db.createNode();
			environment2.setProperty("name", "Test");

			Node appcontainer1 = createAppContainer("123", "ApacheCntr1", db);
			Node appcontainer2 = createAppContainer("234", "JBoss EAP6", db);
			Node appcontainer3 = createAppContainer("753", "SQL Server", db);
			Node appcontainer4 = createAppContainer("800", "Java Data Grid", db);

			environment1.createRelationshipTo(appcontainer1,
					DynamicRelationshipType.withName("CONSISTS_OF"));
			environment1.createRelationshipTo(appcontainer2,
					DynamicRelationshipType.withName("CONSISTS_OF"));

			environment2.createRelationshipTo(appcontainer3,
					DynamicRelationshipType.withName("CONSISTS_OF"));
			environment2.createRelationshipTo(appcontainer4,
					DynamicRelationshipType.withName("CONSISTS_OF"));

			businessServiceNode.createRelationshipTo(environment1,
					DynamicRelationshipType.withName("CONTAINS")).setProperty(
					"type", "production");
			businessServiceNode.createRelationshipTo(environment2,
					DynamicRelationshipType.withName("CONTAINS")).setProperty(
					"type", "development");

			Node vm1 = createVM("12345", "TestVM1", "WINDOWS", 2, Boolean.TRUE,
					db);
			Node vm2 = createVM("54321", "TestVM2", "RHEL", 2, Boolean.TRUE, db);
			Node vm3 = createVM("ABCDE", "DRVM1", "SUSE", 8, Boolean.TRUE, db);
			Node vm4 = createVM("ZYXQL", "PRODVM2", "RHEL", 8, Boolean.TRUE, db);

			appcontainer1.createRelationshipTo(vm1,
					DynamicRelationshipType.withName("EXECUTES"));
			appcontainer1.createRelationshipTo(vm2,
					DynamicRelationshipType.withName("EXECUTES"));
			appcontainer2.createRelationshipTo(vm3,
					DynamicRelationshipType.withName("EXECUTES"));
			appcontainer3.createRelationshipTo(vm4,
					DynamicRelationshipType.withName("EXECUTES"));

			Node nic1 = createNIC("0903940345", "192.168.1.2", "255.255.255.0",
					db);
			Node nic2 = createNIC("9037409803", "192.168.1.20", "255.255.255.0",
					db);
			Node nic3 = createNIC("2873682348", "192.168.1.30", "255.255.255.0",
					db);
			Node nic4 = createNIC("1234122344", "192.168.1.40", "255.255.255.0",
					db);
			Node nic5 = createNIC("3457546756", "192.168.1.50", "255.255.255.0",
					db);
			Node nic6 = createNIC("7897789789", "192.168.1.60", "255.255.255.0",
					db);
			Node nic7 = createNIC("08937503345", "192.168.1.70", "255.255.255.0",
					db);
			Node nic8 = createNIC("98235963004", "192.168.1.80", "255.255.255.0",
					db);
			
			vm1.createRelationshipTo(nic1, DynamicRelationshipType.withName("CONNECTS_VIA")).setProperty("usage", "PUBLIC");
			vm1.createRelationshipTo(nic2, DynamicRelationshipType.withName("CONNECTS_VIA")).setProperty("usage", "MANAGEMENT");
			vm1.createRelationshipTo(nic3, DynamicRelationshipType.withName("CONNECTS_VIA")).setProperty("usage", "CLUSTER");
			vm2.createRelationshipTo(nic4, DynamicRelationshipType.withName("CONNECTS_VIA")).setProperty("usage", "PUBLIC");
			vm2.createRelationshipTo(nic5, DynamicRelationshipType.withName("CONNECTS_VIA")).setProperty("usage", "MANAGEMENT");
			vm2.createRelationshipTo(nic7, DynamicRelationshipType.withName("CONNECTS_VIA")).setProperty("usage", "CLUSTER");
			vm3.createRelationshipTo(nic6, DynamicRelationshipType.withName("CONNECTS_VIA")).setProperty("usage", "MANAGEMENT");
			vm4.createRelationshipTo(nic8, DynamicRelationshipType.withName("CONNECTS_VIA")).setProperty("usage", "MANAGEMENT");

			

			Node clusterNode = db.createNode();
			clusterNode.setProperty("name", "cluster1");	
			clusterNode.createRelationshipTo(nic3, DynamicRelationshipType.withName("ClusterNIC"));
			clusterNode.createRelationshipTo(nic7, DynamicRelationshipType.withName("ClusterNIC"));
			appcontainer1.createRelationshipTo(clusterNode, DynamicRelationshipType.withName("CLUSTER_CONFIG"));

			tx.success();
		} finally {
			tx.finish();
		}

	}

	public static void main(String[] args) throws Throwable {
		BuildAndTestDB myDB = new BuildAndTestDB();
		myDB.startupServer();
		myDB.PopulateDatabase();
		Thread.currentThread().sleep(1000000);
		myDB.stopServer();

	}

}