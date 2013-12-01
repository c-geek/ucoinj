package fr.twiced.ucoinj;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class ServerMain {

    private static final Logger log = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String args[]) throws Exception {
		new ServerMain().exec(args);
	}

    private static String OPT_CURRENCY = "currency";
    private static String OPT_PRIVATE_KEY = "privk";
    private static String OPT_IPV4 = "ipv4";
    private static String OPT_PORT = "port";
    private static String OPT_REMOTEH= "remoteh";
    private static String OPT_REMOTE4= "remote4";
    private static String OPT_REMOTE6= "remote6";
    private static String OPT_REMOTEP= "remotep";
	
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
		
		CommandLineParser parser = new BasicParser();
		try {
			
			CommandLine cmd = parser.parse(options, args);
			int port = 8080;
			int remotePort = 8080;
			
			// --currency
			if(!cmd.hasOption(OPT_CURRENCY)){
				throw new OptionRequiredException(cmd);
			}
			
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
			
			// --remotep
			if(cmd.hasOption(OPT_REMOTEP)){
				remotePort = Integer.valueOf(cmd.getOptionValue(OPT_REMOTEP));
			}

	        // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
	        // the initialization phase of your application
	        SLF4JBridgeHandler.install();
	        
	        GlobalConfiguration config = GlobalConfiguration.getInstance();
	        config.setCurrency(cmd.getOptionValue(OPT_CURRENCY));
	        config.setPrivateKey(cmd.getOptionValue(OPT_PRIVATE_KEY));
	        config.setIPv4(cmd.getOptionValue(OPT_IPV4));
	        config.setPort(port);
	        config.setRemoteHost(cmd.getOptionValue(OPT_REMOTEH));
	        config.setRemoteIPv4(cmd.getOptionValue(OPT_REMOTE4));
	        config.setRemoteIPv6(cmd.getOptionValue(OPT_REMOTE6));
	        config.setRemotePort(remotePort);

	        try {
	            JettyServer server = new JettyServer(config.getIPv4(), config.getPort());
	            server.start();
	            log.info(String.format("uCoin server listening on %s port %d", config.getIPv4(), config.getPort()));
	            server.join();
	        } catch (Exception e) {
	            log.error("Failed to start server.", e);
	        }
			
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp("java -jar jpgp.jar", options);
		}
	}
	
	private static class OptionRequiredException extends RuntimeException {
		private static final long serialVersionUID = -5866603787705576032L;
		public OptionRequiredException(CommandLine cmd) {
			super("Bad arguments given to CLI:" + cmd.getArgList().toString());
		}
	}
}
