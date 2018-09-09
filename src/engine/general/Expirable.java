package engine.general;

public interface Expirable {
	boolean isExpired();
	void renew();
}
