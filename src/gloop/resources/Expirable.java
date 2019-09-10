package gloop.resources;

public interface Expirable {
	boolean isExpired();
	void renew();
}
