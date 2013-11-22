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
	
	public String exec(String[] args){
		Options options = new Options();
		options.addOption(OPT_PRIVATE_KEY, true, "Private key to be used for signing messages.");
		
		CommandLineParser parser = new BasicParser();
		try {
			
			CommandLine cmd = parser.parse(options, args);
			
			// --privk
			if(!cmd.hasOption(OPT_PRIVATE_KEY)){
				throw new OptionRequiredException(cmd);
			}

	        // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
	        // the initialization phase of your application
	        SLF4JBridgeHandler.install();
	        
	        GlobalConfiguration config = GlobalConfiguration.getInstance();
	        config.setPrivateKey(cmd.getOptionValue(OPT_PRIVATE_KEY));

	        try {
	            JettyServer server = new JettyServer(8080);
	            server.start();
	            log.info("Server started");
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
