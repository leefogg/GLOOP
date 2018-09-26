package GLOOP.general.collections;

public abstract class GenericStackable implements Stackable {
	@Override
	public void pushed() {
		enable();
	}

	@Override
	public void poped() {
		disable();
	}

	@Override
	public abstract void enable();

	@Override
	public abstract void disable();
}
