package gloop.general.collections;

public abstract class GenericStackable implements Stackable {
	@Override
	public void pushed() {
		enable();
	}

	@Override
	public void poped() {
		disable();
	}
}
