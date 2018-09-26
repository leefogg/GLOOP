package GLOOP.resources;

public interface Expirable {
	boolean isExpired();
	void renew();
}
