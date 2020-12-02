package Storage;

import com.couchbase.lite.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {

    private static final String DB_NAME = "wayship";
    /*      Credentials declared this way purely for expediency in this demo - use OAUTH in production code */
    private static final String DB_USER = "wayship";
    private static final String DB_PASS = "wayship";
    //  private static final String SYNC_GATEWAY_URL = "ws://127.0.0.1:4984/db" + DB_NAME;
    private static final String SYNC_GATEWAY_URL = "wss://sgway.wayship.io/wayship";
    static Database database;

    public static void main(String[] args) throws CouchbaseLiteException, InterruptedException, URISyntaxException {
        // Initialize Couchbase Lite
        CouchbaseLite.init();

        // Get the database (and create it if it doesn’t exist).
        DatabaseConfiguration config = new DatabaseConfiguration();

        config.setDirectory("/home/wayship/");
        // config.setDirectory(context.getFilesDir().getAbsolutePath());

        config.setEncryptionKey(new EncryptionKey(DB_PASS));
        database = new Database(DB_NAME, config);

        Endpoint targetEndpoint = new URLEndpoint(new URI(SYNC_GATEWAY_URL));
        ReplicatorConfiguration replConfig = new ReplicatorConfiguration(database, targetEndpoint);
        replConfig.setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL);

        // Add authentication.
        replConfig.setAuthenticator(new BasicAuthenticator(DB_USER, DB_PASS));

        // Create replicator (be sure to hold a reference somewhere that will prevent the Replicator from being GCed)
        Replicator replicator = new Replicator(replConfig);

        // Listen to replicator change events.
        replicator.addChangeListener(change -> {
            if (change.getStatus().getError() != null) {
                System.err.println("Error code ::  " + change.getStatus().getError().getCode());
            }
        });

        // Start replication.
//        System.out.println("Remote sync started.");
//        replicator.start(true);
//        replicator.addChangeListener(change -> {
//            CouchbaseLiteException error = change.getStatus().getError();
//            if (error != null) {
//                System.out.println("Error Code " + error);
//            }
//
//            System.out.println(change.getStatus().getActivityLevel());
//            if (change.getStatus().getActivityLevel() == Replicator.ActivityLevel.STOPPED) {
//                System.out.println("Replication stopped");
//                replicator.start(true);
//            }
//        });

        // Start Passive Peer
        Main.startReplicationLocal();
    }

    static void startReplicationLocal() throws URISyntaxException, CouchbaseLiteException, InterruptedException {
        final URLEndpointListenerConfiguration thisConfig
                = new URLEndpointListenerConfiguration(database);

        thisConfig.setPort(55990);

        thisConfig.setNetworkInterface("127.0.0.1");

        thisConfig.setEnableDeltaSync(false);
        thisConfig.setDisableTls(false);
        // Configure server security

        // Use an Anonymous Self-Signed Cert
        thisConfig.setTlsIdentity(null);

        // Configure Client Security using an Authenticator
        // For example, Basic Authentication
        char[] charArray = {'w', 'a', 'y', 's', 'h', 'i', 'p'};
        thisConfig.setAuthenticator(new ListenerPasswordAuthenticator(
                (validUser, validPassword)
                -> "wayship".equals(validUser)
                && Arrays.equals(charArray, validPassword)));

        // Initialize the listener
        final URLEndpointListener thisListener
                = new URLEndpointListener(thisConfig);

        // Start the listener
        thisListener.start();
        int connectionCount;
        connectionCount = thisListener.getStatus().getConnectionCount();
        while (connectionCount > -1) {
            connectionCount = thisListener.getStatus().getConnectionCount();
            System.out.println("The total connections " + connectionCount);
            Thread.sleep(10000);
        }
        System.out.println("Finish");
    }
}
