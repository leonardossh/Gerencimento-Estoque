package util;

import java.security.MessageDigest;

/*
 * Essa estratégia não é ideal para ambiente de produção, sendo utilizada aqui 
 * no momento apenas para ensinar.
 */
public class HashUtil {
	public static String sha256(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = md.digest(input.getBytes("UTF-8"));

			StringBuilder sb = new StringBuilder();
			for (byte b : hashBytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException("Erro ao gerar hash SHA-256", e);
		}
	}
}