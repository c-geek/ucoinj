package fr.twiced.ucoinj.tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UCoinTest {
	
	public String readFile(String testResource) throws IOException, URISyntaxException {
		byte[] encoded = Files.readAllBytes(Paths.get(getClass().getResource(testResource).toURI()));
		return StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(encoded)).toString();
	}
}
