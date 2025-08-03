package me.gravityio.flair.util;

import java.util.List;

public class ListPointer<T> {
    private int index;
    private final List<T> args;

    public ListPointer(int index, List<T> args) {
        this.index = index;
        this.args = args;
    }

    public boolean isEnd() {
        return this.index >= this.args.size();
    }

    public boolean hasNext() {
        return this.index < this.args.size();
    }

    public T peek() {
        if (this.index >= this.args.size()) {
            return null;
        }
        return this.args.get(index);
    }

    public T peek(int offset) {
        if (this.index + offset >= this.args.size()) {
            return null;
        }
        return this.args.get(index + offset);
    }

    public T eat() {
        if (this.index >= this.args.size()) {
            return null;
        }
        return this.args.get(index++);
    }

    public T prev() {
        return this.args.get(index - 1);
    }

    public void skip() {
        this.skip(1);
    }

    public void skip(int count) {
        this.index += count;
    }
}