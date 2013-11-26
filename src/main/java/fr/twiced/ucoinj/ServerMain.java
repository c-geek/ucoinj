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
		System.out.println(new ServerMain().exec(args));
	}

    private static String OPT_PRIVATE_KEY = "privk";
    private static String OPT_IPV4 = "ipv4";
    private static String OPT_PORT = "port";
	
	public String exec(String[] args){
		Options options = new Options();
		options.addOption(OPT_PRIVATE_KEY, true, "private key to be used for signing messages");
		options.addOption(OPT_IPV4, true, "IPV4 interface to listen for requests");
		options.addOption("p", OPT_PORT, true, "port to listen for requests");
		
		CommandLineParser parser = new BasicParser();
		try {
			
			CommandLine cmd = parser.parse(options, args);
			int port = 8080;
			
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

	        // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
	        // the initialization phase of your application
	        SLF4JBridgeHandler.install();
	        
	        GlobalConfiguration config = GlobalConfiguration.getInstance();
	        config.setPrivateKey(cmd.getOptionValue(OPT_PRIVATE_KEY));
	        config.setIPv4(cmd.getOptionValue(OPT_IPV4));
	        config.setPort(port);

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
		return "{\"result\": \"error\", \"data\": null}";
	}
	
	private static class OptionRequiredException extends RuntimeException {
		private static final long serialVersionUID = -5866603787705576032L;
		public OptionRequiredException(CommandLine cmd) {
			super("Bad arguments given to CLI.");
		}
	}
}
