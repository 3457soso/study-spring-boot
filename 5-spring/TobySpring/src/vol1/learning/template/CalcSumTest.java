package learning.template;

import org.junit.Before;
import org.junit.Test;
import _etc.calculator.Calculator;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CalcSumTest {
    Calculator calculator;
    String filepath;

    @Before
    public void init() {
        this.calculator = new Calculator();
        filepath = calculator.getClass().getResource("numbers.txt").getPath();
    }

    @Test
    public void sumOfNumbers() throws IOException {
        assertThat(calculator.calcSum(filepath), is(10));
    }

    @Test
    public void multiplyOfNumbers() throws IOException {
        assertThat(calculator.calcMultiply(filepath), is(24));
    }

    @Test
    public void concatenateStrings() throws IOException {
        assertThat(calculator.concatenate(filepath), is("1234"));
    }
}
