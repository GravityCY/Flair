package me.gravityio.flair.util;

public class ArrayPointer<T> {
    private int index;
    private final T[] args;

    public ArrayPointer(int index, T[] args) {
        this.index = index;
        this.args = args;
    }

    public boolean isEnd() {
        return this.index >= this.args.length;
    }

    public boolean hasNext() {
        return this.index < this.args.length;
    }

    public T peek() {
        if (this.index >= this.args.length) {
            return null;
        }
        return this.args[index];
    }

    public T peek(int offset) {
        if (this.index + offset >= this.args.length) {
            return null;
        }
        return this.args[index + offset];
    }

    public T eat() {
        if (this.index >= this.args.length) {
            return null;
        }
        return this.args[index++];
    }

    public T prev() {
        return this.args[index - 1];
    }

    public void skip() {
        this.skip(1);
    }

    public void skip(int count) {
        this.index += count;
    }
}