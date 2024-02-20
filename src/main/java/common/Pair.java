package common;

public record Pair<T, R>(
	T first,
	R second
) {
	public T key() {
		return first;
	}

	public R value() {
		return second;
	}
}
