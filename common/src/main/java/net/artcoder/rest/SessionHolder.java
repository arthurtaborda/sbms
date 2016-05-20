package net.artcoder.rest;

public class SessionHolder {

	private static final ThreadLocal<String> sessionCookie = new ThreadLocal<>();

	public static void setSessionCookie(String value) {
		sessionCookie.set(value);
	}

	public static String getSessionCookie() {
		return sessionCookie.get();
	}

	public static void clear() {
		sessionCookie.remove();
	}

	public static boolean isClean() {
		return sessionCookie.get() == null;
	}
}
