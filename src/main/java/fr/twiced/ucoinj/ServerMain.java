package fr.twiced.ucoinj;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import fr.twiced.ucoinj.bean.Key;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.KeyDao;

public class ServerMain {

    private static final Logger log = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String args[]) throws Exception {
		new ServerMain().exec(args);
	}

    private static String COMMAND = "currency";
    
    private static String OPT_CURRENCY = "currency";
    private static String OPT_PRIVATE_KEY = "privk";
    private static String OPT_IPV4 = "ipv4";
    private static String OPT_PORT = "port";
    private static String OPT_REMOTEH= "remoteh";
    private static String OPT_REMOTE4= "remote4";
    private static String OPT_REMOTE6= "remote6";
    private static String OPT_REMOTEP= "remotep";
    private static String OPT_MDB = "mdb";
    private static String OPT_MHOST = "mhost";
    private static String OPT_MPORT = "mport";
    private static String OPT_MUSER = "muser";
    private static String OPT_MPASSWD = "mpasswd";
	
	public void exec(String[] args){
		Options options = new Options();
		options.addOption(OPT_CURRENCY, true, "Currency to handle");
		options.addOption(OPT_PRIVATE_KEY, true, "private key to be used for signing messages");
		options.addOption(OPT_IPV4, true, "IPV4 interface to listen for requests");
		options.addOption("p", OPT_PORT, true, "port to listen for requests");
		options.addOption(OPT_REMOTEH, true, "Remote interface using DNS access");
		options.addOption(OPT_REMOTE4, true, "Remote interface for IPv4 access");
		options.addOption(OPT_REMOTE6, true, "Remote interface for IPv6 access");
		options.addOption(OPT_REMOTEP, true, "Remote port used for others to contact this node");
		options.addOption(OPT_MDB, true, "MySQL DB name");
		options.addOption(OPT_MHOST, true, "MySQL DB host");
		options.addOption(OPT_MPORT, true, "MySQL DB port");
		options.addOption(OPT_MUSER, true, "MySQL DB user");
		options.addOption(OPT_MPASSWD, true, "MySQL DB password");
		
		CommandLineParser parser = new BasicParser();
		try {

	        GlobalConfiguration config = GlobalConfiguration.getInstance();
			CommandLine cmd = parser.parse(options, args);
	        String command = (String) (cmd.getArgList().isEmpty() ? "" : cmd.getArgList().get(0));
			int port = 8080;
			int dbPort = 3306;
			int remotePort = 8080;
			String dbHost = "localhost";
			String dbName = "ucoinj";
			
			// --currency
			if(!cmd.hasOption(OPT_CURRENCY)){
				throw new OptionRequiredException(cmd);
			}
			
	        config.setCurrency(cmd.getOptionValue(OPT_CURRENCY));
	        dbName = config.getCurrency();
			
			// --mhost
			if(cmd.hasOption(OPT_MHOST)){
				dbHost = cmd.getOptionValue(OPT_MHOST);
			}
			
			// --mport
			if(cmd.hasOption(OPT_MPORT)){
				dbPort = Integer.valueOf(cmd.getOptionValue(OPT_MPORT));
			}
			
			// --mdb
			if(cmd.hasOption(OPT_MDB)){
				dbName = cmd.getOptionValue(OPT_MDB);
			}
			
			// --muser
			if(cmd.hasOption(OPT_MUSER)){
				config.setDBUsername(cmd.getOptionValue(OPT_MUSER));
			}
			
			// --mpasswd
			if(cmd.hasOption(OPT_MPASSWD)){
				config.setDBPassword(cmd.getOptionValue(OPT_MPASSWD));
			}

			config.setDBURL(String.format("jdbc:mysql://%s:%d/%s?createDatabaseIfNotExist=true", dbHost, dbPort, dbName));
			
			if (command.equals("start")) {
				// --privk
				if(!cmd.hasOption(OPT_PRIVATE_KEY)){
					throw new OptionRequiredException(cmd);
				}
				
				// --ipv4
				if(!cmd.hasOption(OPT_IPV4)){
					throw new OptionRequiredException(cmd);
				}
				
				// --port
				if(cmd.hasOption(OPT_PORT)){
					port = Integer.valueOf(cmd.getOptionValue(OPT_PORT));
				}
				
				// --remoteh, --remote4, --remote6
				if(!(cmd.hasOption(OPT_REMOTEH) || cmd.hasOption(OPT_REMOTE4) || cmd.hasOption(OPT_REMOTE6))){
					throw new OptionRequiredException(cmd);
				}
				
				// --remoteh
				if(cmd.hasOption(OPT_REMOTEH)){
			        config.setRemoteHost(cmd.getOptionValue(OPT_REMOTEH));
				}
				
				// --remote4
				if(cmd.hasOption(OPT_REMOTE4)){
			        config.setRemoteIPv4(cmd.getOptionValue(OPT_REMOTE4));
				}
				
				// --remote6
				if(cmd.hasOption(OPT_REMOTE6)){
			        config.setRemoteIPv6(cmd.getOptionValue(OPT_REMOTE6));
				}
				
				// --remotep
				if(cmd.hasOption(OPT_REMOTEP)){
					remotePort = Integer.valueOf(cmd.getOptionValue(OPT_REMOTEP));
			        config.setRemotePort(remotePort);
				}

		        config.setPort(port);
		        config.setPrivateKey(cmd.getOptionValue(OPT_PRIVATE_KEY));
		        config.setIPv4(cmd.getOptionValue(OPT_IPV4));
			}

	        // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
	        // the initialization phase of your application
	        SLF4JBridgeHandler.install();
	        
	        if (command == null || command.equals("help")) {
	        	
	        	// Help on usage
				printUsage();
				
	        } else if (command.equals("start")) {
	        	
	        	// Start server
		        try {
		            JettyServer server = new JettyServer(config.getIPv4(), config.getPort());
		            server.start();
		            log.info(String.format("uCoin server listening on %s port %d", config.getIPv4(), config.getPort()));
		            server.join();
		        } catch (Exception e) {
		            log.error("Failed to start server.", e);
		        }
	        } else {
	        	ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfiguration.class);
	        	if (command.equals("manage-key") || command.equals("forget-key")) {
	        		
	        		if (cmd.getArgList().size() < 2) {
	        			throw new OptionRequiredException("missing key fingerprint argument");
	        		}
	        		String fingerprint = cmd.getArgList().get(1).toString();
	        		KeyDao keyDao = ctx.getBean(KeyDao.class);
	        		Key k = keyDao.getByKeyId(new KeyId(fingerprint));
	        		if (k == null) {
	        			k = new Key(fingerprint);
	        			k.setManaged(command.equals("manage-key"));
	        			keyDao.save(k);
	        		} else {
	        			k.setManaged(command.equals("manage-key"));
	        			keyDao.update(k);
	        		}
		        }
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error: " + e.getMessage());
			printUsage();
		}
	}
	
	private void printUsage() {
    	System.out.println("\r\n"
			+ "  Usage: ucoind --currency <name> [options] <command>\r\n"
			+ "\r\n"
			+ "  Commands:\r\n"
			+ "\r\n"
			+ "    sync [host] [port]     Tries to synchronise data with remote uCoin node\r\n"
			+ "    manage-keys            Update managed keys configuration and send corresponding forwards to other peers\r\n"
			+ "    manage-key [key]       Add given key to stack of managed keys of this node\r\n"
			+ "    forget-key [key]       Remove given key of the managed keys' stack of this node\r\n"
			+ "    config                 Register configuration in database\r\n"
			+ "    reset [config|data]    Reset configuration or data in database\r\n"
			+ "    update-merkles         Reset Merkle trees and computes them again according to stored data.\r\n"
			+ "    start                  Start uCoin server using given --currency\r\n"
			+ "\r\n"
			+ "  Options:\r\n"
			+ "\r\n"
			+ "    -h, --help                output usage information\r\n"
			+ "    -V, --version             output the version number\r\n"
			+ "    -p, --port <port>         Port to listen for requests\r\n"
			+ "    -c, --currency <name>     Name of the currency managed by this node.\r\n"
			+ "    --mhost <host>            MySQL host.\r\n"
			+ "    --mport <port>            MySQL port.\r\n"
			+ "    --mdb <name>              MySQL database name (defaults to currency name).\r\n"
			+ "    --mdpasswd <password>     MySQL password.\r\n"
			+ "    --pgpkey <keyPath>        Path to the private key used for signing HTTP responses.\r\n"
			+ "    --pgppasswd <password>    Password for the key provided with --httpgp-key option.\r\n"
			+ "    --ipv4 <address>          IPV4 interface to listen for requests\r\n"
			+ "    --ipv6 <address>          IPV6 interface to listen for requests\r\n"
			+ "    --remoteh <host>          Remote interface others may use to contact this node\r\n"
			+ "    --remote4 <host>          Remote interface for IPv4 access\r\n"
			+ "    --remote6 <host>          Remote interface for IPv6 access\r\n"
			+ "    --remotep <port>          Remote port others may use to contact this node\r\n"
			+ "    --kmanagement <ALL|KEYS>  Define key management policy"
		);
	}
	
	private static class OptionRequiredException extends RuntimeException {
		
		private static final long serialVersionUID = -5866603787705576032L;
		
		public OptionRequiredException(CommandLine cmd) {
			super("Bad arguments given to CLI: " + cmd.getArgList().toString());
		}
		public OptionRequiredException(String message) {
			super("Bad arguments given to CLI: " + message);
		}
	}
}
