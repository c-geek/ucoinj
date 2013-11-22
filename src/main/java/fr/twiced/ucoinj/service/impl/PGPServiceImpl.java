package fr.twiced.ucoinj.service.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.security.SignatureException;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;

import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.exceptions.NoSignaturePacketException;
import fr.twiced.ucoinj.pgp.PGPHelper;
import fr.twiced.ucoinj.service.PGPService;

@Service
public class PGPServiceImpl implements PGPService {
	
	public PGPServiceImpl() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider ());
	}

	@Override
	public PGPPublicKey extractPubkey(String base64stream) throws NoPublicKeyPacketException {
		List<PGPPublicKey> pubkeys = PGPHelper.extract(base64stream);
		if (pubkeys.isEmpty()) {
			throw new NoPublicKeyPacketException("error.packet.publickey.not.found");
		}
		return pubkeys.get(0);
	}

	@Override
	public boolean verify(String originalDocument, String base64signature, PGPPublicKey pubkey) throws Exception {
		try {
			return new Signature(base64signature).verify(pubkey, originalDocument);
		} catch (SignatureException | PGPException e) {
			throw new BadSignatureException(e);
		}
	}

	@Override
	public String extractIssuer(String base64signature) throws Exception {
		return new Signature(base64signature).getIssuerKeyId().toUpperCase();
	}

	@Override
	public String extractFingerprint(PGPPublicKey publicKey) {
		byte[] twentyBytesFingerprint = publicKey.getFingerprint();
		byte[] fourtyHexBytesFingerprint = Hex.encode(twentyBytesFingerprint);
		return new String(fourtyHexBytesFingerprint).toUpperCase();
	}

	@Override
	public String extractFingerprint(String base64PublicKey) throws NoPublicKeyPacketException {
		return extractFingerprint(extractPubkey(base64PublicKey));
	}

	@Override
	public boolean verify(String base64signature, PGPPublicKey pubkey) throws BadSignatureException, NoPublicKeyPacketException,
			NoSignaturePacketException, Exception {
		try {
			return new Signature(base64signature).verify(pubkey, "");
		} catch (SignatureException | PGPException e) {
			throw new BadSignatureException(e);
		}
	}

	@Override
	public String sign(String data, PGPPrivateKey pk) throws PGPException, SignatureException, IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		OutputStream out = new ArmoredOutputStream(bos);
		Security.addProvider(new BouncyCastleProvider());
		PGPSignatureGenerator sGen = new PGPSignatureGenerator(new BcPGPContentSignerBuilder(pk.getPublicKeyPacket().getAlgorithm(), PGPUtil.SHA1));
        sGen.init(PGPSignature.CANONICAL_TEXT_DOCUMENT, pk);
        
        BCPGOutputStream bOut = new BCPGOutputStream(out);
        InputStream bIn = new BufferedInputStream(new ByteArrayInputStream(data.getBytes()));

        int ch;
        while ((ch = bIn.read()) >= 0)
        {
            sGen.update((byte)ch);
        }
        bIn.close();
        sGen.generate().encode(bOut);
        bOut.close();
        out.close();
        bos.close();
        return bos.toString();
	}

	@Override
	public PGPPrivateKey extractPrivateKey(String stream, String password) throws PGPException, IOException {
		PGPSecretKey secretKey = readSecretKey(new ByteArrayInputStream(stream.getBytes()));
		PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(password.toCharArray());
		return secretKey.extractPrivateKey(decryptor);
	}
	
	private PGPSecretKey readSecretKey(InputStream input) throws IOException, PGPException {
        PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(input));
        Iterator<?> keyRingIter = pgpSec.getKeyRings();
        while (keyRingIter.hasNext()) {
            PGPSecretKeyRing keyRing = (PGPSecretKeyRing)keyRingIter.next();
            Iterator<?> keyIter = keyRing.getSecretKeys();
            while (keyIter.hasNext()) {
                PGPSecretKey key = (PGPSecretKey)keyIter.next();
                if (key.isSigningKey()) {
                    return key;
                }
            }
        }
        throw new IllegalArgumentException("Can't find signing key in key ring.");
    }
}
