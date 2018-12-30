package _etc.calculator;

public interface LineCallback<T> {
    T doSomethingWithLine(String line, T value);
}
