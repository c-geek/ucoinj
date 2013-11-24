package fr.twiced.ucoinj.pgp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPUtil;

import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;

public class PGPHelper {
	
	public static String enarmor(byte[] bytes) throws IOException{
		OutputStream armoredOut;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		armoredOut = new ArmoredOutputStream(out);
        armoredOut.write(bytes);
        armoredOut.close();
		return new String(out.toByteArray());
	}
	
	@SuppressWarnings("unchecked")
	public static List<PGPPublicKey> extract(String stream) throws NoPublicKeyPacketException{
		List<PGPPublicKey> certs = new ArrayList<PGPPublicKey>();
		try {
			PGPPublicKeyRingCollection ringCollection = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(new ByteArrayInputStream(stream.getBytes())));
			Iterator<PGPPublicKeyRing> rings = ringCollection.getKeyRings();
			while (rings.hasNext()) {
				PGPPublicKeyRing keyRing = (PGPPublicKeyRing) rings.next();
				Iterator<PGPPublicKey> pubkeys = keyRing.getPublicKeys();
				while(pubkeys.hasNext()){
					PGPPublicKey pubkey = pubkeys.next();
					boolean isSubKey = false;
					Iterator<PGPSignature> sigs = pubkey.getSignatures();
					while(sigs.hasNext()){
						PGPSignature sig = sigs.next();
						isSubKey = isSubKey || sig.getSignatureType() == PGPSignature.SUBKEY_BINDING;
					}
					certs.add(pubkey);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PGPException e) {
			e.printStackTrace();
		}
		if (certs.isEmpty()) {
			throw new NoPublicKeyPacketException("error.packet.publickey.not.found");
		}
		return certs;
	}
	
	public static PGPPublicKeyRing getKeyring(InputStream keyBlockStream) throws IOException {
		// PGPUtil.getDecoderStream() will detect ASCII-armor automatically and
		// decode it,
		// the PGPObject factory then knows how to read all the data in the
		// encoded stream
		PGPObjectFactory factory = new PGPObjectFactory(
				PGPUtil.getDecoderStream(keyBlockStream));

		// these files should really just have one object in them,
		// and that object should be a PGPPublicKeyRing.
		Object o = factory.nextObject();
		if (o instanceof PGPPublicKeyRing) {
			return (PGPPublicKeyRing) o;
		}
		throw new IllegalArgumentException("Input text does not contain a PGP Public Key");
	}
}
